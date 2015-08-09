//package cindyliu96.test;
//import android.content.Intent;
//import android.view.View.OnClickListener;
//import android.app.DatePickerDialog;
//import android.app.ListActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//
//public class MainActivity extends ListActivity {
//    private UserOperations userDBoperation;
//    Calendar myCalendar = Calendar.getInstance();
//
//    EditText birthday;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        userDBoperation = new UserOperations(this);
//
//        try {
//            userDBoperation.open();
//        } catch (SQLException e) {
//            return;
//        }
//
//        List values = userDBoperation.getAllUsers();
//
//        //listing elements remove after testing
//
//        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, values);
//        setListAdapter(adapter);
//
//        birthday = (EditText) findViewById(R.id.enter_birthday);
//
//        birthday.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(MainActivity.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.DAY_OF_MONTH), myCalendar.get(Calendar.MONTH)).show();
//            }
//        });
//    }
//
//    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            myCalendar.set(Calendar.YEAR, year);
//            myCalendar.set(Calendar.MONTH, monthOfYear);
//            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            updateLabel();
//        }
//    };
//
//    private void updateLabel() {
//        String myFormat = "MM/dd/yy";
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        birthday.setText(sdf.format(myCalendar.getTime()));
//    }
//
//    public void addUser(View view) {
//        ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
//        EditText name = (EditText) findViewById(R.id.enter_name);
//        EditText nickname = (EditText) findViewById(R.id.enter_nickname);
//        EditText dob = (EditText) findViewById(R.id.enter_birthday);
//        System.out.println("Name entered is: " + name.getText().toString());
//        System.out.println("nickname entered is: " + nickname.getText().toString());
//        System.out.println("Birthday entered is: " + dob.getText().toString());
//        User user = userDBoperation.addUser(name.getText().toString(), nickname.getText().toString(), dob.getText().toString());
//        adapter.add(user);
//    }
//
//    public void goToCalendar(View view) {
//        Intent intent = new Intent(this, EventCalendar.class);
//        startActivity(intent);
//    }
//
//    @Override
//    protected void onResume() {
//        try {
//            userDBoperation.open();
//        } catch (SQLException e) {
//            return;
//        }
//
//        super.onResume();
//    }
//
//    @Override
//    protected  void onPause() {
//        userDBoperation.close();
//        super.onPause();
//    }
//}

package cindyliu96.test;
import cindyliu96.test.SQLiteHandler;
import cindyliu96.test.SessionManager;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private HashMap<String, String> user;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        //if the user does not already have a group, prompt the user to add to a group or create a new group
        if (user.get("group") == null) {
            System.out.println("THE USER DOES NOT BELONG TO A GROUP YET");
            //ask the user to either join an existing group or make a new one
        }
        if (user.get("group") != null) {

        }

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToCalendar(View view) {
        Intent intent = new Intent(this, EventCalendar.class);
        startActivity(intent);
    }
}

