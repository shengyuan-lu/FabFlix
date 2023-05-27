package edu.uci.ics.fabflixmobile.ui.login;

import static edu.uci.ics.fabflixmobile.data.Constants.baseURL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "Login";

    private EditText username;
    private EditText password;
    private TextView message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d(TAG, response);
                    try {
                        JSONObject responseJsonObj = new JSONObject(response); // Convert response string to a JSON object

                        if (responseJsonObj.get("status").equals("success")) {
                            finish(); // Complete and destroy login activity once successful

                            // initialize the activity(page)/destination
                            Intent MovieListPage = new Intent(LoginActivity.this, MovieListActivity.class);
                            // activate the list page.
                            startActivity(MovieListPage);
                        } else {
                            // If login fails, show an error message
                            message.setText((String) responseJsonObj.get("message"));
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "Json parse error");
                    }
                },
                error -> {
                    // error
                    Log.d(TAG, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                // POST request form data
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                params.put("frontendType", "android");
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}