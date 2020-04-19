package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase contactsDB = null;
    Button createDatabaseBtn, addContactBtn, deleteContactBtn, getContactsBtn, deleteDatabaseBtn;
    EditText nameEditTxt, emailEditTxt, deleteIdEditTxt;
    TextView databaseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDatabaseBtn = (Button) findViewById(R.id.createDatabaseBtn);
        addContactBtn = (Button) findViewById(R.id.addContactBtn);
        deleteContactBtn = (Button) findViewById(R.id.deleteContactBtn);
        getContactsBtn = (Button) findViewById(R.id.getContactsBtn);
        deleteDatabaseBtn = (Button) findViewById(R.id.deleteDatabaseBtn);
        nameEditTxt = (EditText) findViewById(R.id.nameEditTxt);
        emailEditTxt = (EditText) findViewById(R.id.emailEditTxt);
        deleteIdEditTxt = (EditText) findViewById(R.id.deleteIDEditTxt);
        databaseTextView = (TextView) findViewById(R.id.databaseTextView);


    }


    public void createDatabase(View view) {
        try {
            contactsDB = this.openOrCreateDatabase("MyContacts", MODE_PRIVATE, null);
            contactsDB.execSQL("CREATE TABLE IF NOT EXISTS contacts " +
                    "(id integer primary key, name VARCHAR, email VARCHAR);");

            File database = getApplicationContext().getDatabasePath("MyContacts.db");

            if(!database.exists()) {
                Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Database Missing", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Log.e("CONTACTS ERROR", "Error creating database");
        }

        addContactBtn.setClickable(true);
        deleteContactBtn.setClickable(true);
        getContactsBtn.setClickable(true);
        deleteContactBtn.setClickable(true);

    }

    public void addContact(View view) {

        String contactName = nameEditTxt.getText().toString();
        String contactEmail = emailEditTxt.getText().toString();

        contactsDB.execSQL("INSERT INTO contacts (name, email) VALUES ('"+ contactName +"', '"+ contactEmail +"');");
    }

    public void getContacts(View view) {
        Cursor cursor = contactsDB.rawQuery("SELECT * FROM contacts", null);
        Log.d("Cursor--------------->", cursor.toString());

        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int emailColumn = cursor.getColumnIndex("email");
        cursor.moveToFirst();
        String contactList = "";

        if(cursor!= null && (cursor.getCount() > 0)) {
            do {
                String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);
                String email = cursor.getString(emailColumn);
                contactList = contactList + id + " : " + name + " : " + email + '\n';
            } while(cursor.moveToNext());
            databaseTextView.setText(contactList);
        } else {
            Toast.makeText(this, "No results to show.", Toast.LENGTH_SHORT).show();
            databaseTextView.setText("");
        }

    }

    public void deleteContact(View view) {
        String id = deleteIdEditTxt.getText().toString();
        contactsDB.execSQL("DELETE FROM contacts WHERE id = " + id + ";");
        Toast.makeText(this, "Contact with id " + id + " deleted.", Toast.LENGTH_SHORT).show();
        getContacts(view);


    }

    public void deleteDatabase(View view) {

        this.deleteDatabase("MyContacts.db");
        Toast.makeText(this, "Database MyContacts deleted.", Toast.LENGTH_SHORT).show();
        getContacts(view);


    }

    @Override
    protected void onDestroy() {
        contactsDB.close();
        super.onDestroy();
    }
}
