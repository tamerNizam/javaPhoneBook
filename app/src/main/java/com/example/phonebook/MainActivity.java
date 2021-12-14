package com.example.phonebook;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected ListView simpleList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnResetDatabase:
                resetDb();
                return true;
            case R.id.btnPageAdd:
                Intent i = new Intent(MainActivity.this,Add.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void resetDb() {
        SQLiteDatabase db=null;
        try{
            db=SQLiteDatabase.openOrCreateDatabase(
                    getFilesDir().getPath()+"/"+"kontakti.db",
                    null
            );
            String q="DROP TABLE KONTAKTI";
            db.execSQL(q);
            initDb();
            selectDb();
            Toast.makeText(getApplicationContext(),"You clear your database!",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }finally {
            if(db!=null){
                db.close();
                db=null;
            }
        }
    }

    protected void initDb() throws SQLException{
        SQLiteDatabase db=null;
        db=SQLiteDatabase.openOrCreateDatabase(
                getFilesDir().getPath()+"/"+"kontakti.db",
                null
        );
        String q="CREATE TABLE if not exists KONTAKTI(" +
                "ID integer primary key AUTOINCREMENT," +
                "name text not null," +
                "tel text not null," +
                "email text not null," +
                "category text not null," +
                "unique(name, tel) );";

        db.execSQL(q);
        db.close();
    }

    public void selectDb() throws SQLException{
        SQLiteDatabase db=null;
        db=SQLiteDatabase.openOrCreateDatabase(
                getFilesDir().getPath()+"/"+"kontakti.db",
                null
        );
        simpleList.clearChoices();
        ArrayList<String> listResults= new ArrayList<String>();
        String q="SELECT * FROM KONTAKTI ORDER BY name;";
        Cursor c=db.rawQuery(q, null);
        while(c.moveToNext()){
            String category=c.getString(c.getColumnIndex("category"));
            String name=c.getString(c.getColumnIndex("name"));
            String tel=c.getString(c.getColumnIndex("tel"));
            String email=c.getString(c.getColumnIndex("email"));
            String ID=c.getString(c.getColumnIndex("ID"));
            listResults.add(ID+"\t"+name+"\t"+email+"\t"+tel+"\t"+category);
        }

        ArrayAdapter<String> arrayAdapter=
                new ArrayAdapter<String>(
                        getApplicationContext(),
                        R.layout.activity_listview,
                        R.id.textView,
                        listResults
                );
        simpleList.setAdapter(arrayAdapter);
        db.rawQuery(q, null); //Can't use execSql for SELECT(where return data)
        db.close();
    }

    @Override
    @CallSuper
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        try{
            selectDb();
        }catch (Exception e){

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleList=findViewById(R.id.simpleList);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected="";
                TextView clickedText=view.findViewById(R.id.textView);
                selected=clickedText.getText().toString();
                String[] elements=selected.split("\t");
                String ID=elements[0];
                String Name=elements[1];
                String Email=elements[2];
                String Tel=elements[3];
                String Category=elements[4];
                Intent intent=new Intent(MainActivity.this,Update.class);
                Bundle b=new Bundle();
                b.putString("ID",ID);
                b.putString("name",Name);
                b.putString("email",Email);
                b.putString("tel", Tel);
                b.putString("category", Category);

                intent.putExtras(b);
                startActivityForResult(intent,200,b);
            }
        });

        try{
            initDb();
            selectDb();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
