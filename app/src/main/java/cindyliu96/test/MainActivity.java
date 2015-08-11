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
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private HashMap<String, String> user;

    private SQLiteHandler db;
    private SessionManager session;

    private Dialog dialog;
    private Button addNewHome;
    private Button joinHome;
    private Button submitButton;
    private Button cancelButton;
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //delete database
        //db.deleteUsers();

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        user = db.getUserDetails();

        //if the user does not already have a group, prompt the user to add to a group or create a new group
        if (user.get("user_group") == null || user.get("user_group").trim().length() == 0) {
            System.out.println("THE USER DOES NOT BELONG TO A GROUP YET");
            //ask the user to either join an existing group or make a new one
            //I will display the two options
            setContentView(R.layout.edit_home);
            joinHome = (Button) findViewById(R.id.joinExistingHome);
            addNewHome = (Button) findViewById(R.id.createNewHome);

            joinHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinExistingHome();
                }
            });

            addNewHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addHome();
                }
            });
        }
        if (user.get("user_group") != null) {
            System.out.println("The user already belongs to a group");
            System.out.println("The user belongs to group: " + user.get("user_group"));
            System.out.println("The user's group is null: " + (user.get("user_group") == "null"));
            System.out.println("hi");
            //set to home screen layout
            setContentView(R.layout.activity_main);
        }


        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);



        String name = user.get("name");
        String email = user.get("email");



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

    public void addHome() {
        //make a new dialog popup and user can enter a new group name.
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.set_home_dialog);
        dialog.setTitle("Add a new home");
        dialog.show();

        cancelButton = (Button) dialog.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        submitButton = (Button) dialog.findViewById(R.id.submitGroup);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText groupName = (EditText) dialog.findViewById(R.id.group);
                final String name = groupName.getText().toString();
                System.out.println("Name of group is: " + name);
                updateUser(name);
                       System.out.println("Trying to update user group");
                        if (!name.isEmpty()) {

                            //add to sqlite database
                            updateUser(name);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please enter your details!", Toast.LENGTH_LONG)
                                    .show();
                        }
                        String tag_string_req = "req_update";

                        //showDialog();

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                AppConfig.URL_REGISTER, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Update Response: " + response.toString());
                                //hideDialog();

                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean error = jObj.getBoolean("error");
                                    if (!error) {
                                        System.out.println("User group successfully updated in mysql");
                                        // User successfully stored in MySQL
                                    } else {
                                        System.out.println("The use group failed to update");
                                        // Error occurred in registration. Get the error
                                        // message
                                        String errorMsg = jObj.getString("error_msg");
                                        Toast.makeText(getApplicationContext(),
                                                errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Update Error: " + error.getMessage());
                                Toast.makeText(getApplicationContext(),
                                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();ooo
                            }
                        }) {
                            String email = db.getUserDetails().get("email");
                            @Override
                            protected Map<String, String> getParams() {
                                System.out.println("trying to return params");
                                // Posting params to register url
                                Map<String, String> params = new HashMap<>();
                                params.put("tag", "update");
                                params.put("user_group", name);
                                params.put("email", email);
                                return params;
                            }
                        };
                        System.out.println("updating group to mysql database");
                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//                System.out.println("here1");
//                System.out.println("user group: " + db.getUserDetails().get("user_group"));
                dialog.dismiss();
                    }
        });
        //System.out.println("here2");
    }

    public void joinExistingHome() {
        //new dialog appears and prompt user to enter existing group name
        //make a new dialog popup and user can enter a new group name.
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.set_home_dialog);
        dialog.setTitle("Enter existing home");
        dialog.show();

        cancelButton = (Button) dialog.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        submitButton = (Button) dialog.findViewById(R.id.submitEvent);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
    }

    public void updateUser(String groupName) {
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> userDetails = db.getUserDetails();
        String uid = userDetails.get("uid");
        System.out.println("group name: " + groupName);

        db.updateUserGroup(groupName);
        db.close();
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

