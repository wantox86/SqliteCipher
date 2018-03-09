package id.netzme.testsqllite.sqlitecipher.helper;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

/**
 * Created by wawansetiawan on 09/03/18.
 */

public class MigrateDB {
    public static boolean isDatabaseFileExist(Context ctxt, String orgDBName) {
        File originalFile=ctxt.getDatabasePath(orgDBName);
        return originalFile.exists();
    }

    public static void encrypt(Context ctxt,
                               String orgDBName,
                               String encDBName,
                               String passphrase,
                               boolean deleteOriginal) throws IOException {
        File originalFile=ctxt.getDatabasePath(orgDBName);
        File encryptedFile=ctxt.getDatabasePath(encDBName);

        originalFile.getAbsolutePath();
        if (originalFile.exists()) {
            File newFile=
                    File.createTempFile("sqlcipherutils", "tmp",
                            ctxt.getCacheDir());
            SQLiteDatabase db=
                    SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(),
                            "", null,
                            SQLiteDatabase.OPEN_READWRITE);

            db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';",
                    newFile.getAbsolutePath(), passphrase));
            db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
            db.rawExecSQL("DETACH DATABASE encrypted;");

            int version=db.getVersion();

            db.close();

            db=
                    SQLiteDatabase.openDatabase(newFile.getAbsolutePath(),
                            passphrase, null,
                            SQLiteDatabase.OPEN_READWRITE);
            db.setVersion(version);
            db.close();

            if(deleteOriginal) {
                originalFile.delete();
            }

            newFile.renameTo(encryptedFile);
        }
    }
}

