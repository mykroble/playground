package ph.edu.usc.jaidar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    TextView signupLink;
    CheckBox remember;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);

        loginBtn = (Button) findViewById(R.id.loginBtn);

        signupLink = (TextView) findViewById(R.id.signUpLink);
        remember = (CheckBox) findViewById(R.id.remember_me);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAttempt();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });


    }

    public void loginAttempt(){
        String emailStr = email.getText().toString();
        String password = this.password.getText().toString();

        if(validateInput(emailStr, password)){
            mAuth.signInWithEmailAndPassword(emailStr, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
//                    if loggedin successfully
                    SharedPreferences sharedPreferences = getSharedPreferences("userpref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(remember.isChecked()) {
//                        if remember me is checked
                        editor.putString("email",emailStr);
                        editor.putString("password",password);
                        editor.apply();
                    } else {
//                        if not, clears the shared pref
                        editor.clear();
                        editor.apply();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    this.password.setText("");
                    Exception e = task.getException();
                    if (e != null) {
//                        this part is cool, I might wanna make this error thingy later
                        String message = e.getMessage();
                        if (message != null && message.contains("email address is already in use")) {
                            Toast.makeText(this, "Email already registered.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Invalid data input. Please try again with different data.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateInput(String emailStr, String password){
        if(!emailStr.isEmpty() && isEmail(emailStr) && !password.isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmail(String emailStr){
        if(emailStr.contains("@")){
            return true;
        } else {
            return false;
        }
    }
}