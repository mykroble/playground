package ph.edu.usc.jaidar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    TextView loginLink;
    Button signupBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        loginLink = findViewById(R.id.alreadyAccount);
        signupBtn = findViewById(R.id.signupBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        signupBtn.setOnClickListener(v -> signupAttempt());
    }

    public void signupAttempt() {
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmation = confirmPasswordInput.getText().toString();

        if (validateInput(name, email, password, confirmation)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                // ✅ Add additional fields to Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("uid", uid); // ✅ This line was added
                                userMap.put("name", name);
                                userMap.put("email", email);

                                db.collection("users").document(uid).set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error saving user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public boolean validateInput(String name, String email, String password, String confirmation) {
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing Name input field.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing email input field.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isEmail(email)) {
            Toast.makeText(getApplicationContext(), "Invalid Email Address.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing password input field.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (confirmation.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing confirmation input field.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmation)) {
            Toast.makeText(getApplicationContext(), "Password confirmation does not match.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
