package cindyliu96.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import org.w3c.dom.Text;

public class MainActivity extends Activity {
    public static String username;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private ArrayList<String> groupNames;
    private HashMap<String, String> user;

    private SQLiteHandler db;
    private SessionManager session;
    private EditText groupName;
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
        groupNames = LoginActivity.groupNames();

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        user = db.getUserDetails();
        username = user.get("name");
        System.out.println("User details: " + user);

        String ug = user.get("user_group");
        //if the user does not already have a group, prompt the user to add to a group or create a new group
        if (ug == null || ug.equals("null")) {
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
        if (ug != null && !ug.equals("null")) {
            System.out.println("The user already belongs to a group");
            System.out.println("The user belongs to group: " + user.get("user_group"));
            System.out.println("The user's group is null: " + (user.get("user_group") == "null"));
            System.out.println("hi");
            //set to home screen layout
            setContentView(R.layout.activity_main);
        }

        txtName = (TextView) findViewById(R.id.name);
        //txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);



        String name = user.get("name");
        String email = user.get("email");



        // Displaying the user details on the screen
        txtName.setText(name);
        //txtEmail.setText(email);

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
                if (groupNames.contains(name)) {
                    System.out.println("The group already exists please enter a new group name");
                    return;
                }
                if (name != null && !name.isEmpty()) {
                    groupNames.add(name);
                }

                updateUser(name);
                System.out.println("Trying to update user group");

                addGroupToDatabases(name);
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
        submitButton = (Button) dialog.findViewById(R.id.submitGroup);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to check if the groupname is in sqlite database.
                EditText groupName = (EditText) dialog.findViewById(R.id.group);
                final String name = groupName.getText().toString();
                System.out.println("available groups: " + groupNames);
                if (name != null && groupNames.contains(name) && !name.isEmpty()) {
                    System.out.println("User will now be joining the group: " + name);
                    updateUser(name);
                    addGroupToDatabases(name);
                } else {
                    System.out.println("No group with group name: " + name + " exists");
                }
            }
        });
    }

    public void addGroupToDatabases(final String name) {
        if (!name.isEmpty()) {
            //add to sqlite database
            //updateUser(name);
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
                System.out.println("User's email is: " + email);
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

    public void goToPayPal(View view) {
        Intent intent = new Intent(this, PayPal_selection.class);
        startActivity(intent);
    }
}

