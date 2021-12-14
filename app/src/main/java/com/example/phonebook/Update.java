package com.example.phonebook;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Update extends AppCompatActivity {
    protected String ID;
    protected EditText euName,euEmail,euTel,inputSms;
    protected Spinner category;
    protected Button btnUpdate,btnDelete, btnMakeCall, btnSendSms;
    protected final short SMS_PORT=6734;
    protected SmsManager smsManager;

    boolean isValid(String text, String expression)
            throws PatternSyntaxException
    {
        Pattern pattern=Pattern.compile(expression);
        Matcher m=pattern.matcher(text);
        return m.matches();

    }

    protected void CloseThisActivity(){
        finishActivity(200);
        Intent i = new Intent(Update.this,MainActivity.class);
        startActivity(i);
    }

    private int getIndexOfSpinner(Spinner spinner, String myString){
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int spinnerPosition = adapter.getPosition(myString);
        return adapter.getPosition(myString);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        euName=findViewById(R.id.euName);
        euEmail=findViewById(R.id.euEmail);
        euTel=findViewById(R.id.euTel);
        category=findViewById(R.id.category);
        inputSms=findViewById(R.id.inputSms);
        btnUpdate=findViewById(R.id.btnUpdate);
        btnDelete=findViewById(R.id.btnDelete);
        btnMakeCall=findViewById(R.id.btnMakeCall);
        btnSendSms=findViewById(R.id.btnSendSMS);
        smsManager=SmsManager.getDefault();

        Bundle b=getIntent().getExtras();
        if(b!=null){
            ID=b.getString("ID");
            euName.setText(b.getString("name"));
            euEmail.setText(b.getString("email"));
            euTel.setText(b.getString("tel"));

            int possitionCategory=getIndexOfSpinner(category, b.getString("category"));
            category.setSelection(possitionCategory);
        }
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db=null;
                try{
                    db=SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath()+"/"+"kontakti.db",
                            null
                    );
                    String name=euName.getText().toString();
                    String tel=euTel.getText().toString();
                    String email=euEmail.getText().toString();
                    String categoryText=category.getSelectedItem().toString();
                    String q="UPDATE KONTAKTI SET name=?, tel=?, email=?, category=?" +
                            " WHERE ID=?; ";
                    db.execSQL(q, new Object[]{name, tel, email, categoryText, ID});
                    Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    if(db!=null){
                        db.close();
                        db=null;
                    }
                }
                CloseThisActivity();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db=null;
                try{
                    db=SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath()+"/"+"kontakti.db",
                            null
                    );
                    String q="DELETE FROM KONTAKTI" +
                            " WHERE ID=?; ";
                    db.execSQL(q, new Object[]{ ID});
                    Toast.makeText(getApplicationContext(), "Delete Successful", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    if(db!=null){
                        db.close();
                        db=null;
                    }
                }
                CloseThisActivity();
            }
        });

        btnMakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                // Check for permission
                if (ActivityCompat.checkSelfPermission(Update.this, Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    String tel = euTel.getText().toString();
                    callIntent.setData(Uri.parse("tel:" + tel));
                    startActivity(callIntent);
                    Toast.makeText(getApplicationContext(),"Please wait...",Toast.LENGTH_LONG).show();
                }else {
                    // Ask for permission if haven't
                    ActivityCompat.requestPermissions(Update.this,
                            new String[]{Manifest.permission.CALL_PHONE},200);
                    Toast.makeText(getApplicationContext(),"You must give permission for call!",Toast.LENGTH_LONG).show();
                }
            }
        });

        if(smsManager==null){
            Toast.makeText(getApplicationContext(), "No SMS Service",
                    Toast.LENGTH_LONG
            ).show();
        }else{
            btnSendSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check for permission
                    if (ActivityCompat.checkSelfPermission(Update.this, Manifest.permission.SEND_SMS) ==
                            PackageManager.PERMISSION_GRANTED) {
                        try {
                            String smsText = inputSms.getText().toString();
                            if (smsText.equals("")) {
                                throw new Exception("Message is required!");
                            }

                            String tel = euTel.getText().toString();
                            if (!isValid(tel, "([\\d]){2,}[\\.\\-]?")) {
                                throw new Exception("Invalid phone number!");
                            }
                            smsManager.sendDataMessage(tel,
                                    null,
                                    SMS_PORT,
                                    smsText.getBytes("UTF-8"),
                                    null,
                                    null
                            );

                            Toast.makeText(getApplicationContext(), "SMS sending...",
                                    Toast.LENGTH_LONG
                            ).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    } else {
                        // Ask for permission if haven't
                        ActivityCompat.requestPermissions(Update.this,
                                new String[]{Manifest.permission.SEND_SMS},200);
                        Toast.makeText(getApplicationContext(),"You must give permission for send email!",Toast.LENGTH_LONG).show();
                    }


                }
            });




        }

    }
}