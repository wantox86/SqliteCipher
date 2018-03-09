package id.netzme.testsqllite.sqlitecipher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import id.netzme.testsqllite.sqlitecipher.helper.DBHelper;
import id.netzme.testsqllite.sqlitecipher.helper.DBHelperCipher;
import id.netzme.testsqllite.sqlitecipher.helper.MigrateDB;
import static id.netzme.testsqllite.sqlitecipher.tools.Constanst.*;

import net.sqlcipher.database.SQLiteDatabase;

import static id.netzme.testsqllite.sqlitecipher.helper.DBHelper.*;

public class MainActivity extends AppCompatActivity {
    private boolean AUTO_MIGRATE = false;
    DBHelper mydb;
    DBHelperCipher mydbCipher;
    TextView name ;
    TextView phone;
    TextView email;
    TextView street;
    TextView place;

    Button btnRetrieveOriginal;
    Button btnRetrieveEncrypted;
    Button btnMigrateDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (TextView) findViewById(R.id.editTextName);
        phone = (TextView) findViewById(R.id.editTextPhone);
        email = (TextView) findViewById(R.id.editTextStreet);
        street = (TextView) findViewById(R.id.editTextEmail);
        place = (TextView) findViewById(R.id.editTextCity);

        btnRetrieveOriginal = (Button) findViewById(R.id.buttonOriginal);
        btnRetrieveEncrypted = (Button) findViewById(R.id.buttonEncrypted);
        btnMigrateDB = (Button) findViewById(R.id.buttonMigrate);

        try {
            // INIT SQLLITE
            mydb = new DBHelper(this);
            insertDBOriginalIfEmpty(DUMMY_DATA_COUNT);

            // INIT SQLCIPHER
            SQLiteDatabase.loadLibs(this);
            mydbCipher = new DBHelperCipher(this);
            btnRetrieveEncrypted.setEnabled(false);

            Toast.makeText(getApplicationContext(), "AUTO MIGRATE : " + AUTO_MIGRATE, Toast.LENGTH_LONG).show();
            if(AUTO_MIGRATE) {
                migrateDB(true);
                btnRetrieveOriginal.setEnabled(false);
                btnMigrateDB.setEnabled(false);
                btnRetrieveEncrypted.setEnabled(true);
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR INIT DB", Toast.LENGTH_LONG).show();
        }
    }

    private void migrateDB(boolean deleteOriginal) throws IOException {
        if(MigrateDB.isDatabaseFileExist(this, DBHelper.DATABASE_NAME)) {
            MigrateDB.encrypt(this,
                    DBHelper.DATABASE_NAME,
                    DBHelperCipher.DATABASE_NAME,
                    DBHelperCipher.KEY_CIPHER,
                    deleteOriginal);
            Toast.makeText(getApplicationContext(),
                    "Success Migrate from " + DBHelper.DATABASE_NAME + " to " + DBHelperCipher.DATABASE_NAME,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void insertDBOriginalIfEmpty(int count) {
        if(mydb.numberOfRows() == 0) {
            String name = "Wawan";
            String phone = "+6285600000000";
            String email = "wawan@netzme.id";
            String street = "Bekasi";
            String place = "Bekasi";
            for (int i = 0; i < count; i++) {
                if(mydb.insertContact(
                        name + " - " + i,
                        phone,
                        email,
                        street,
                        place)){

                } else{
                    Toast.makeText(getApplicationContext(), "Failed Save Database",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }

    }

    public void migrateDB(View view) {
        try {
            migrateDB(false);
            btnRetrieveEncrypted.setEnabled(true);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR INIT DB", Toast.LENGTH_LONG).show();
        }
    }

    public void retrieveFromOriginalDB(View view) {
        populateContent(mydb.getAllContacts(), DBHelper.DATABASE_NAME);
    }

    public void retrieveFromEncryptedDB(View view) {
        populateContent(mydbCipher.getAllContacts(), DBHelperCipher.DATABASE_NAME);
    }

    private void populateContent(ArrayList<HashMap<String, String>> allContacts, String dbName) {
        if(allContacts.size() > 0) {
            name.setText(allContacts.get(0).get(CONTACTS_COLUMN_NAME));
            phone.setText(allContacts.get(0).get(CONTACTS_COLUMN_PHONE));
            email.setText(allContacts.get(0).get(CONTACTS_COLUMN_EMAIL));
            street.setText(allContacts.get(0).get(CONTACTS_COLUMN_STREET));
            place.setText(allContacts.get(0).get(CONTACTS_COLUMN_CITY));

            Toast.makeText(getApplicationContext(), "Contact : " + allContacts.size() + " members",
                    Toast.LENGTH_SHORT).show();
            File databasePath = getDatabasePath(dbName);
            String pathDB = databasePath.getAbsolutePath();
            Toast.makeText(getApplicationContext(), "DB Loc: " + pathDB, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Database is Empty", Toast.LENGTH_SHORT).show();
        }
    }

}
