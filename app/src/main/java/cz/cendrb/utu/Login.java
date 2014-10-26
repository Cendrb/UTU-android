package cz.cendrb.utu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Activity activity = this;

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

    private void login() {
        new LoginWithProgressDialog(this, getResources().getString(R.string.wait), getResources().getString(R.string.logging_in), null).execute();
    }

    private void showData() {
        Intent intent = new Intent(this, utu.class);
        startActivity(intent);
    }

    public class LoginWithProgressDialog extends TaskWithProgressDialog<LoginResult> {
        public LoginWithProgressDialog(Activity activity, String titleMessage, String message, Runnable postAction) {
            super(activity, titleMessage, message, postAction);
        }

        @Override
        protected LoginResult doInBackground(Void... voids) {
            if (utu.isOnline(activity)) {
                if (utu.dataLoader.login(email.getText().toString(), password.getText().toString())) {
                    showData();
                    return LoginResult.WebLoginSuccess;
                } else
                    return LoginResult.InvalidUsernameOrPassword;
            } else {
                showData();
                return LoginResult.BackupSuccess;
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
            }
            super.onPostExecute(loginResult);
        }
    }
}
