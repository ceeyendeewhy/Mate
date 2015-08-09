package cindyliu96.test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yunjie on 7/24/2015.
 */
public class DataBaseWrapper extends SQLiteOpenHelper {
    public static final String USERS = "Users";
    public static final String USER_ID = "_id";
    public static final String USER_NAME = "_name";
    public static final String USER_NICKNAME = "_nickname";
    public static final String USER_DOB = "_dob";

    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 1;

    //creation SQLite statement
    private static final String DATABASE_CREATE = "create table " + USERS
            + "(" + USER_ID + " integer primary key autoincrement, "
            + USER_NAME + " text not null, "
            + USER_NICKNAME + " text, "
            + USER_DOB + " text not null);";

    public DataBaseWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getUsers() {
        Cursor cursor = getReadableDatabase().query(USERS, new String[] { USER_ID, USER_NAME, USER_NICKNAME,
                USER_DOB }, null, null, null, null, null);
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS);
        onCreate(db);
    }
}
