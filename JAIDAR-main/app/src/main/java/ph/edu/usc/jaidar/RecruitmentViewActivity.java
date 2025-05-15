package ph.edu.usc.jaidar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RecruitmentViewActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //get from intent or sharedpref?
    private String jobRecruitmentId = "cxACH6JkcFrlYC9GMcAt";   //get from intent
    View overlay;
    ProgressBar spinner;
    private ImageView backBtn;
    private TextView rateValue, titleText;
    private TextView descriptionInput;
    private TextView fullNameView;
    private Button applyBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment_view);

        overlay = findViewById(R.id.loading_overlay);
        spinner = findViewById(R.id.loading_spinner);
        showLoading();

        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> goBack());

        rateValue = findViewById(R.id.rate_value);
        titleText = findViewById(R.id.title);
        descriptionInput = findViewById(R.id.description_input);

        fullNameView = findViewById(R.id.user_whole_name);
        applyBtn = findViewById(R.id.apply_button);
        applyBtn.setOnClickListener(v -> apply());

        loadJobDetails();
    }

    private void showLoading(){
        overlay.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        overlay.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
    }
    private void goBack(){
        //imma replace this previousPage with variable passed from intent from last page.
        Class previousPage = HomePageActivity.class;
        Intent intent = new Intent(this, previousPage);
        this.startActivity(intent);
    }

    private void loadJobDetails() {

        db.collection("job_recruitments").document(jobRecruitmentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        double rate = documentSnapshot.getDouble("rate");
                        String description = documentSnapshot.getString("description");
                        String title = documentSnapshot.getString("title");
                        String authorUid = documentSnapshot.getString("user_post");

                        titleText.setText(title);
                        rateValue.setText(rate + "");
                        descriptionInput.setText(description);

                        db.collection("users").document(authorUid).get()
                                .addOnSuccessListener(snapshot -> {
                                    fullNameView.setText(snapshot.getString("name"));
                                })
                                .addOnFailureListener(e -> {
                                    fullNameView.setText("");
                                }).addOnCompleteListener(task -> {
                                    hideLoading();
                                });

                        db.collection("job_recruitment_apply").whereEqualTo("apply_user", userId).whereEqualTo("status", "active").get()
                                .addOnSuccessListener(snapshot -> {
                                    if(!snapshot.isEmpty()){
                                        applyBtn.setText("APPLIED!");
                                        applyBtn.setTextColor(Color.parseColor("#FFFFFF"));
                                        applyBtn.setEnabled(false);
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to load job details.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Job not found.", Toast.LENGTH_SHORT).show();
                        goBack();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load job details.", Toast.LENGTH_SHORT).show();
                    goBack();
                });
    }

    private void apply(){
        showLoading();
        Map<String, Object> application = new HashMap<>();
        application.put("apply_user", userId);
        application.put("job_recruitment", jobRecruitmentId);
        application.put("status", "active");
        application.put("applied_at", FieldValue.serverTimestamp());

        db.collection("job_recruitment_apply")
                .add(application)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Application sent!", Toast.LENGTH_SHORT).show();
                    applyBtn.setText("APPLIED!");
                    applyBtn.setTextColor(Color.parseColor("#FFFFFF"));
                    applyBtn.setEnabled(false);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error. Application was not sent.", Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
                    hideLoading();
                });
    }}