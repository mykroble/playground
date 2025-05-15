package ph.edu.usc.jaidar.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import ph.edu.usc.jaidar.ActivitySectionActivity;
import ph.edu.usc.jaidar.HomePageActivity;
import ph.edu.usc.jaidar.LandingPageActivity;
import ph.edu.usc.jaidar.R;
import ph.edu.usc.jaidar.messaging.ChatActivity;
import ph.edu.usc.jaidar.messaging.UserListActivity;

public class UserProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView nameDisplay, emailDisplay;
    String profileUid;
    String name = "", email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        profileUid = getIntent().getStringExtra("profileUid");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        ViewPageAdapter adapter = new ViewPageAdapter(this, profileUid);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "About" : "Reviews")
        ).attach();

        nameDisplay = findViewById(R.id.nameDisplay);
        emailDisplay = findViewById(R.id.emailDisplay);

        if (profileUid != null) {
            db.collection("users").document(profileUid).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            name = snapshot.getString("name");
                            email = snapshot.getString("email");
                            nameDisplay.setText(name);
                            emailDisplay.setText(email);
                        } else {
                            nameDisplay.setText("Unknown User");
                            emailDisplay.setText("No email");
                        }
                    })
                    .addOnFailureListener(e -> {
                        nameDisplay.setText("Error loading user");
                        emailDisplay.setText("Error");
                    });
        }

        ImageView chatBtn = findViewById(R.id.chatButton);
        chatBtn.setOnClickListener(v -> {
            if (!email.isEmpty() && !name.isEmpty()) {
                Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                intent.putExtra("receiverName", name);
                intent.putExtra("receiverEmail", email);
                startActivity(intent);
            }
        });

        Button logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences sharedPreferences = getSharedPreferences("userpref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
            startActivity(intent);
            finish();
        });

        navigation(mAuth);
    }

    private void navigation(FirebaseAuth mAuth){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        String currentUid = mAuth.getUid();
        if (currentUid != null && currentUid.equals(profileUid)) {
            bottomNavigationView.setSelectedItemId(R.id.profile);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                startActivity(new Intent(this, HomePageActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.activity) {
                startActivity(new Intent(this, ActivitySectionActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.message) {
                startActivity(new Intent(this, UserListActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.profile) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("profileUid", currentUid);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }
}
