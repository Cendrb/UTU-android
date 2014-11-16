package cz.cendrb.utu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import cz.cendrb.utu.enums.LoginResult;

public class Login extends Activity {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String AUTO_LOGIN = "auto_login";
    public static final String CREDENTIALS_SAVED = "credentials_saved";

    EditText email;
    EditText password;
    CheckBox permanentLogin;
    CheckBox autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button login = (Button) findViewById(R.id.loginButton);
        Button viewAll = (Button) findViewById(R.id.loginViewAll);
        email = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.loginPassword);
        permanentLogin = (CheckBox) findViewById(R.id.loginPermanentLogin);
        autoLogin = (CheckBox) findViewById(R.id.loginAutoLogin);

        permanentLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                autoLogin.setEnabled(b);
                if (!b)
                    autoLogin.setChecked(false);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showData();
            }
        });

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        if (preferences.getBoolean(Login.CREDENTIALS_SAVED, false)) {
            email.setText(preferences.getString(Login.EMAIL, ""));
            password.setText(preferences.getString(Login.PASSWORD, ""));
            permanentLogin.setChecked(true);
            autoLogin.setChecked(preferences.getBoolean(Login.AUTO_LOGIN, false));

            if (preferences.getBoolean(Login.AUTO_LOGIN, false))
                login();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.utu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_web_version) {
            UtuClient.openUrl(this, "http://utu.herokuapp.com");
        }

        return super.onOptionsItemSelected(item);
    }

    private void login() {
        new LoginWithProgressDialog(this).execute();
    }

    private void showData() {
        Intent intent = new Intent(this, utu.class);
        startActivity(intent);
    }

    public class LoginWithProgressDialog extends TaskWithProgressDialog<LoginResult> {
        public LoginWithProgressDialog(Activity activity) {
            super(activity, getResources().getString(R.string.wait), getResources().getString(R.string.logging_in));
        }

        @Override
        protected LoginResult doInBackground(Void... voids) {
            if (utu.isOnline(activity)) {
                switch (utu.utuClient.login(email.getText().toString(), password.getText().toString())) {
                    case LoggedIn:
                        showData();
                        return LoginResult.WebLoginSuccess;
                    case WrongCredentials:
                        return LoginResult.InvalidUsernameOrPassword;
                    case FailedToConnect:
                        return LoginResult.FailedToConnect;
                    default:
                        return LoginResult.FailedToConnect;
                }
            } else {
                return LoginResult.FailedToConnect;
            }
        }

        @Override
        protected void onPostExecute(LoginResult loginResult) {
            SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            switch (loginResult) {
                case InvalidUsernameOrPassword:
                    Toast.makeText(activity, R.string.wrong_username_or_password, Toast.LENGTH_LONG).show();
                    editor.putBoolean(Login.CREDENTIALS_SAVED, false);
                    editor.putBoolean(Login.AUTO_LOGIN, false);
                    editor.apply();
                    break;
                case WebLoginSuccess:
                    if (permanentLogin.isChecked()) {
                        editor.putBoolean(Login.CREDENTIALS_SAVED, true);
                        editor.putBoolean(Login.AUTO_LOGIN, autoLogin.isChecked());
                        editor.putString(Login.EMAIL, email.getText().toString());
                        editor.putString(Login.PASSWORD, password.getText().toString());
                        editor.apply();
                    }
                    break;
                case FailedToConnect:
                    showData();
                    break;
            }
            super.onPostExecute(loginResult);
        }
    }
}
