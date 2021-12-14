package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Add extends AppCompatActivity {
    protected EditText editName, editTel, editEmail;
    protected Spinner category;
    protected Button btnInsert;

    protected void CloseThisActivity(){
        finishActivity(200);
        Intent i = new Intent(Add.this,MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editName=findViewById(R.id.editName);
        editTel=findViewById(R.id.editTel);
        editEmail=findViewById(R.id.editEmail);
        category=findViewById(R.id.category);
        btnInsert=findViewById(R.id.btnInsert);


        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db=null;
                try{
                    db=SQLiteDatabase.openOrCreateDatabase(
                            getFilesDir().getPath()+"/"+"kontakti.db",
                            null
                    );
                    String name=editName.getText().toString();
                    String tel=editTel.getText().toString();
                    String email=editEmail.getText().toString();
                    String categoryText=category.getSelectedItem().toString();
                    String q="INSERT INTO KONTAKTI (name, tel, email, category)" +
                            " VALUES(?,?,?,?); ";
                    db.execSQL(q, new Object[]{name, tel, email, categoryText});
                    Toast.makeText(getApplicationContext(), "Insert Successful", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }finally {
                    if(db!=null){
                        db.close();
                        db=null;
                    }
                    CloseThisActivity();
                }
            }
        });
    }
}
