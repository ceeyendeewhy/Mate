package cindyliu96.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yunjie on 8/10/2015.
 */
public class UpdateActivity extends Activity {

        private static final String TAG = RegisterActivity.class.getSimpleName();
        private Button update;
        private EditText groupName;
        private ProgressDialog pDialog;
        private SessionManager session;
        private SQLiteHandler db;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.set_home_dialog);

            System.out.println("Here");
            update = (Button) findViewById(R.id.submitEvent);
            groupName = (EditText) findViewById(R.id.group);


            // Progress dialog
            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);

            // Session manager
            session = new SessionManager(getApplicationContext());

            // SQLite database handler
            db = new SQLiteHandler(getApplicationContext());


            // Register Button Click event
            update.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    System.out.println("Trying to update user group");
                    String group = groupName.getText().toString();
                    String uid = db.getUserDetails().get("uid");
                    if (!group.isEmpty()) {
                        updateUser(group, uid);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter your details!", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }

        private void updateUser(final String group, final String uid) {
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
                            // Now store the user in sqlite
                            String uid = jObj.getString("uid");
                            JSONObject user = jObj.getJSONObject("user");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String created_at = user
                                    .getString("created_at");
                            //Refresh after updating

                            // Inserting row in users table
                            //db.addUser(name, email, uid, created_at);

                            // Launch login activity
//                        Intent intent = new Intent(
//                                RegisterActivity.this,
//                                LoginActivity.class);
//                        startActivity(intent);
//                        finish();
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
//                hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("tag", "update");
                    params.put("user_group", group);
                    params.put("uid", uid);

                    return params;
                }

            };
            System.out.println("updating group to mysql database");
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        private void showDialog() {
            if (!pDialog.isShowing())
                pDialog.show();
        }

        private void hideDialog() {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
