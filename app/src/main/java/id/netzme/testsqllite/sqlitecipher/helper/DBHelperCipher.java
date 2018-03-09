package id.netzme.testsqllite.sqlitecipher.helper;

import android.content.ContentValues;
import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wawansetiawan on 08/03/18.
 */

public class DBHelperCipher extends SQLiteOpenHelper {

    public static final String KEY_CIPHER = "THIS_IS_KEY";
    public static final String DATABASE_NAME = "MyDBName_enc.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_STREET = "street";
    public static final String CONTACTS_COLUMN_CITY = "place";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    private HashMap hp;

    public DBHelperCipher(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, street text,place text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact (String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase(KEY_CIPHER);
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public android.database.Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase(KEY_CIPHER);
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase(KEY_CIPHER);
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase(KEY_CIPHER);
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase(KEY_CIPHER);
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<HashMap<String, String>> getAllContacts() {
        ArrayList<HashMap<String, String>> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase(KEY_CIPHER);
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            HashMap<String, String> contact = new HashMap<>();
            contact.put(CONTACTS_COLUMN_ID, res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
            contact.put(CONTACTS_COLUMN_NAME, res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            contact.put(CONTACTS_COLUMN_CITY, res.getString(res.getColumnIndex(CONTACTS_COLUMN_CITY)));
            contact.put(CONTACTS_COLUMN_EMAIL, res.getString(res.getColumnIndex(CONTACTS_COLUMN_EMAIL)));
            contact.put(CONTACTS_COLUMN_PHONE, res.getString(res.getColumnIndex(CONTACTS_COLUMN_PHONE)));
            contact.put(CONTACTS_COLUMN_STREET, res.getString(res.getColumnIndex(CONTACTS_COLUMN_STREET)));
            array_list.add(contact);
            res.moveToNext();
        }
        return array_list;
    }
}
