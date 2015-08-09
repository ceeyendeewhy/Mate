package cindyliu96.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yunjie on 7/25/2015.
 */
public class UserOperations {
    private DataBaseWrapper dbHelper;
    private String[] USER_TABLE_COLUMNS = {
            DataBaseWrapper.USER_ID,
            DataBaseWrapper.USER_NAME,
            DataBaseWrapper.USER_NICKNAME,
            DataBaseWrapper.USER_DOB
    };

    private SQLiteDatabase database;

    public UserOperations(Context context) {
        dbHelper = new DataBaseWrapper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public User addUser(String name, String nickname, String dob) {
        ContentValues values = new ContentValues();
        values.put(DataBaseWrapper.USER_NAME, name);
        values.put(DataBaseWrapper.USER_NICKNAME, nickname);
        values.put(DataBaseWrapper.USER_DOB, dob);

        long UserId = database.insert(DataBaseWrapper.USERS, null, values);

        //return the user
        Cursor cursor = database.query(DataBaseWrapper.USERS,
                USER_TABLE_COLUMNS, DataBaseWrapper.USER_ID + " = "
                        + UserId, null, null, null, null);
        cursor.moveToFirst();
        User newUser = parseUser(cursor);
        cursor.close();
        return newUser;
    }

    public void deleteUser(User u) {
        long id = u.getId();
        System.out.println("User deleted with id: " + id);
        database.delete(DataBaseWrapper.USERS, DataBaseWrapper.USER_ID
                + " = " + id, null);
    }

    public List getAllUsers() {
        List users = new ArrayList();
        Cursor cursor = database.query(DataBaseWrapper.USERS,
                USER_TABLE_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = parseUser(cursor);
            System.out.println("Username: " + user.getName());
            System.out.println("User id: " + user.getId());
            System.out.println("User nickname: " + user.getNickname());
            System.out.println("User birthday: " + user.getDob());
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    private User parseUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(0));
        user.setName(cursor.getString(1));
        user.setNickname(cursor.getString(2));
        user.setDob(cursor.getString(3));
        return user;
    }
}

