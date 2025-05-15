package ph.edu.usc.jaidar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ph.edu.usc.jaidar.profile.UserProfileActivity;

public class JobListingDetailsActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getUid();
    String jobRecruitmentId, posterUid;
    View overlay;
    ProgressBar spinner;
    private ImageButton backBtn, companyLogo;
    private TextView poster, jobTitle, subtitle, aboutDescription;
    private Button applyBtn, saveBtn;
    private ImageView waveBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_listing_details);
        overlay = findViewById(R.id.loading_overlay);
        spinner = findViewById(R.id.loading_spinner);

        initialization();
        this.jobRecruitmentId = getIntent().getStringExtra("jobRecruitmentId");
        jobTitle.setText(getIntent().getStringExtra("jobTitle"));
        subtitle.setText(getIntent().getStringExtra("jobSubtitle"));
        aboutDescription.setText(getIntent().getStringExtra("about"));
        this.posterUid = getIntent().getStringExtra("posterUid");

        backBtn.setOnClickListener(view -> finish());
        getPoster();
        poster.setOnClickListener(v -> {
            Intent intent = new Intent(JobListingDetailsActivity.this, UserProfileActivity.class);
            intent.putExtra("profileUid", posterUid);
            startActivity(intent);
        });
        Log.d("POSTER_UID", "Received UID: [" + this.posterUid + "]" + "Current uid: [" + this.userId + "]");
        if(isOwner()){
            applyBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
        } else {
            setApplyButton();
            setSaveButton();
        }
    }

    public void initialization(){
        backBtn = findViewById(R.id.backBtn);
        companyLogo = findViewById(R.id.companyLogo);
        poster = findViewById(R.id.poster);
        jobTitle = findViewById(R.id.jobTitle);
        subtitle = findViewById(R.id.subtitle);
        applyBtn = findViewById(R.id.applyBtn);
        saveBtn = findViewById(R.id.saveBtn);
        waveBg = findViewById(R.id.waveBg);
        aboutDescription = findViewById(R.id.aboutDescription);
    }
    public void getPoster(){
        if (posterUid != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(posterUid)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        String name = snapshot.getString("name");
                        poster.setText(name != null ? name : "Job Poster");
                    })
                    .addOnFailureListener(e -> {
                        poster.setText("Error loading poster");
                    });
        }
    }
    private boolean isOwner(){
        return (this.userId.equals(this.posterUid)) ? true : false;
    }
    private void setApplyButton(){
        db.collection("job_recruitment_apply")
            .whereEqualTo("job_recruitment", jobRecruitmentId)
            .whereEqualTo("apply_user", userId)
            .get()
            .addOnSuccessListener(snapshot -> {
                if(!snapshot.isEmpty()){
                    DocumentSnapshot doc = snapshot.getDocuments().get(0); // get first matching document
                    applyBtn.setText(doc.getString("status"));
                    applyBtn.setEnabled(false);
                } else {
                    applyBtn.setText("Apply");
                    applyBtn.setEnabled(true);
                    applyBtn.setOnClickListener(view -> {
                        apply(jobRecruitmentId);
                    });
                }
            }).addOnFailureListener(e -> {
                applyBtn.setText("Error");
                applyBtn.setEnabled(false);
                Toast.makeText(this, "Failed to fetch job application status.", Toast.LENGTH_SHORT).show();
            });
    }
    private void setSaveButton(){
        db.collection("users")
            .document(userId)
            .collection("job_recruitment_saves")
            .whereEqualTo("job_recruitment", jobRecruitmentId)
            .whereEqualTo("save_user", userId)
            .get()
            .addOnSuccessListener(snapshot -> {
                if(!snapshot.isEmpty()){
                    saveBtn.setText("Saved!");
                    saveBtn.setEnabled(false);
                } else {
                    saveBtn.setText("Save");
                    saveBtn.setEnabled(true);
                    saveBtn.setOnClickListener(view -> {
                        save(jobRecruitmentId);
                    });
                }
            }).addOnFailureListener(e -> {
                saveBtn.setText("Error");
                saveBtn.setEnabled(false);
                Toast.makeText(this, "Failed to fetch job save status.", Toast.LENGTH_SHORT).show();
            });
    }
    private void showLoading(){
        overlay.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        overlay.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
    }
    private void apply(String jobRecruitmentId){
        showLoading();
        Map<String, Object> application = new HashMap<>();
        application.put("apply_user", userId);
        application.put("job_recruitment", jobRecruitmentId);
        application.put("status", "pending");
        application.put("applied_at", FieldValue.serverTimestamp());

        db.collection("job_recruitment_apply")
                .add(application)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Application sent!", Toast.LENGTH_SHORT).show();
                    applyBtn.setText("Applied!");
                    applyBtn.setEnabled(false);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error. Application was not sent.", Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
                    hideLoading();
                });
    }

    private void save(String jobRecruitmentId){
        showLoading();
        Map<String, Object> save = new HashMap<>();
        save.put("save_user", userId);
        save.put("job_recruitment", jobRecruitmentId);
        save.put("saved_at", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userId)
                .collection("job_recruitment_saves")
                .add(save)
                .addOnSuccessListener(docRef -> {
                    saveBtn.setText("Saved!");
                    saveBtn.setEnabled(false);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error. Failed to save.", Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
                    hideLoading();
                });
    }
}
