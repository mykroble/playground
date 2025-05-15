package ph.edu.usc.jaidar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecruitmentEditorActivity extends AppCompatActivity {

    View overlay;
    ProgressBar spinner;
    int dbCounter;
    public static final String USER_ROLE = "user_role";
    public static final String WORKER = "worker";
    public static final String HIRER = "hirer";
    String[] arrTags = {"Electrician", "Plumber", "Carpenter", "Welding",
            "Roofer", "Mechanic", "Caretaker", "Ironworker"};
    List<String> tags = new ArrayList<>(Arrays.asList(arrTags));
    ImageView backBtn;
    TextView userWholeNameText, userNameText, currentTagView, cancelTagView, tagPrompt, topContext;
    LinearLayout headcountSection, tagSection;
    EditText titleInput, rateInput, descriptionInput, headcountCustomInput;
    RadioButton headcountOption1, headcountOption2, headcountOption3;
    FlexboxLayout tagListLayout;
    Button actionButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment_editor);

        overlay = findViewById(R.id.loading_overlay);
        spinner = findViewById(R.id.loading_spinner);
        showLoading();

        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(v -> goBack());

        topContext = findViewById(R.id.top_context);

        userNameText = findViewById(R.id.user_username);
        userWholeNameText = findViewById(R.id.user_whole_name);
        titleInput = findViewById(R.id.title_input);
        rateInput = findViewById(R.id.rate_input);
        descriptionInput = findViewById(R.id.description_input);

        headcountSection = findViewById(R.id.headcount_section);
        headcountCustomInput = findViewById(R.id.headcount_custom_input);
        headcountOption1 = findViewById(R.id.headcount_option1);
        headcountOption2 = findViewById(R.id.headcount_option2);
        headcountOption3 = findViewById(R.id.headcount_option3);

        tagSection = findViewById(R.id.tag_section);
        tagPrompt = findViewById(R.id.tag_prompt);
        tagListLayout = findViewById(R.id.tag_list);
        currentTagView = findViewById(R.id.current_tag);
        cancelTagView = findViewById(R.id.cancel_tag);

        actionButton = findViewById(R.id.action_button);

        // Format editor accordinglt

        String role = getIntent().getStringExtra(USER_ROLE);
        if(role != null) {
            if (role.equals(HIRER)) {
                //Creates recruitment post
                dbCounter = 1;
                loadUserData();
                setTopContext(HIRER);
                setTitleHint(HIRER);
                enableHeadcountSection();
                enableTagSection();
                enablePostButton();
            } else if (role.equals(WORKER)) {
                //Creates List your services
                dbCounter = 1;
                loadUserData();
                setTopContext(WORKER);
                setTitleHint(WORKER);
                disableHeadcountSection();
                enableTagSection();
                enableAddButton();
            } else {
                Toast.makeText(this, "Failed to load template", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Failed to load template", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
        }

    }

    private void goBack(){
        //imma replace this previousPage with variable passed from intent from last page.
        Class previousPage = HomePageActivity.class;
        Intent intent = new Intent(this, previousPage);
        this.startActivity(intent);
    }
    private void setTopContext(String user){
        if(user.equals(WORKER)){
            topContext.setText("List Your Services");
        } else if (user.equals(HIRER)){
            topContext.setText("Create Recruitment Post");
        }
    }
    private void showLoading(){
        overlay.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
    }
    private void hideLoading(){
        overlay.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
    }
    private void tryHideLoading(){
        --dbCounter;
        if(dbCounter == 0) {
            overlay.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        }
    }
    private void loadUserData(){
        db.collection("users").document(userId).get().addOnSuccessListener(snapshot ->{
            if(snapshot.exists()) {
                String wholeName = snapshot.getString("name");
                userWholeNameText.setText(wholeName);
            }
        }).addOnFailureListener(e -> {
            userWholeNameText.setText("");
        }).addOnCompleteListener(task -> {
            tryHideLoading();
        });
    }
    private void setTitleHint(String user){
        if(user.equals(WORKER)){
            titleInput.setHint("Your job listing headline");
        } else if (user.equals(HIRER)){
            titleInput.setHint("Post headline");
        }
    }

    private void enableHeadcountSection(){
        headcountSection.setVisibility(View.VISIBLE);
        headcountOption1.setOnClickListener(v -> disableCustomHeadcount());
        headcountOption2.setOnClickListener(v -> disableCustomHeadcount());
        headcountOption3.setOnClickListener(v -> enableCustomHeadcount());
    }
    private void disableHeadcountSection(){
        headcountSection.setVisibility(View.GONE);
    }
    private void enableCustomHeadcount(){
        headcountCustomInput.setEnabled(true);
        headcountCustomInput.setFocusable(true);
        headcountCustomInput.setFocusableInTouchMode(true);
    }
    private void disableCustomHeadcount() {
        headcountCustomInput.setEnabled(false);
        headcountCustomInput.setFocusable(false);
        headcountCustomInput.setText("");
    }
    private void enableTagSection(){
        for (String tag : tags) {
            TextView tagView = new TextView(this);
            tagView.setText(tag);
            tagView.setPadding(20, 10, 20, 10);
            tagView.setTextColor(Color.WHITE);
            tagView.setBackgroundResource(R.drawable.bg_tag_unselected);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            tagView.setLayoutParams(params);

            tagView.setOnClickListener(v -> {
                tagPrompt.setText("Current tag: ");
                currentTagView.setText(tag);
                currentTagView.setVisibility(View.VISIBLE);
                cancelTagView.setVisibility(View.VISIBLE);
                tagListLayout.setVisibility(View.GONE);
            });

            tagListLayout.addView(tagView);
        }

        cancelTagView.setOnClickListener(v -> {
            tagPrompt.setText("Assign a tag");
            currentTagView.setVisibility(View.GONE);
            cancelTagView.setVisibility(View.GONE);
            tagListLayout.setVisibility(View.VISIBLE);
            currentTagView.setText("");
        });

        tagSection.setVisibility(View.VISIBLE);
    }

    private void enablePostButton(){
        actionButton.setText("POST");
        actionButton.setOnClickListener(v -> saveJobOffer());
    }
    private void enableAddButton(){
        actionButton.setText("ADD");
        actionButton.setOnClickListener(v -> saveJobListing());
    }
    private void saveJobOffer() {
        showLoading();

        String title = titleInput.getText().toString().trim();
        String rateStr = rateInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String tag = currentTagView.getText().toString().trim();
        String headcountStr = "0";

        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Title is required");
            hideLoading();
            return;
        }

        if (TextUtils.isEmpty(rateStr)) {
            rateInput.setError("Rate is required");
            hideLoading();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            descriptionInput.setError("Description is required");
            hideLoading();
            return;
        }

        if(TextUtils.isEmpty(tag)){
            Toast.makeText(this, "1 Tag must be chosen", Toast.LENGTH_SHORT).show();
            hideLoading();
            return;
        }

        if (headcountOption3.isChecked()) {
            headcountStr = headcountCustomInput.getText().toString().trim();
            if (TextUtils.isEmpty(headcountStr)) {
                headcountCustomInput.setError("Enter headcount");
                hideLoading();
                return;
            }
        } else if (headcountOption1.isChecked()) {
            headcountStr = "1";
        } else if (headcountOption2.isChecked()) {
            headcountStr = "2";
        } else {
            Toast.makeText(this, "Select a headcount", Toast.LENGTH_SHORT).show();
            hideLoading();
            return;
        }

        int rate = Integer.parseInt(rateStr);
        int headcount = Integer.parseInt(headcountStr);

        Map<String, Object> jobData = new HashMap<>();
        jobData.put("user_post", userId);
        jobData.put("title", title);
        jobData.put("rate", rate);
        jobData.put("description", description);
        jobData.put("headcount", headcount);
        jobData.put("tag", tag);
        jobData.put("posted_at", FieldValue.serverTimestamp());
        jobData.put("status", "active");    //completed, cancelled? full?

        db.collection("job_recruitments")
                .add(jobData)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Job offer posted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to post", Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
                        hideLoading();
                });
    }
    private void saveJobListing(){
        showLoading();

        String title = titleInput.getText().toString().trim();
        String rateStr = rateInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String tag = currentTagView.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Title is required");
            hideLoading();
            return;
        }

        if (TextUtils.isEmpty(rateStr)) {
            rateInput.setError("Rate is required");
            hideLoading();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            descriptionInput.setError("Description is required");
            hideLoading();
            return;
        }
        if(TextUtils.isEmpty(tag)){
            Toast.makeText(this, "1 Tag must be chosen", Toast.LENGTH_SHORT).show();
            hideLoading();
            return;
        }

        int rate = Integer.parseInt(rateStr);

        Map<String, Object> jobData = new HashMap<>();
        jobData.put("user_post", userId);
        jobData.put("title", title);
        jobData.put("rate", rate);
        jobData.put("description", description);
        jobData.put("tag", tag);
        jobData.put("posted_at", FieldValue.serverTimestamp());
        jobData.put("status", "active");    //completed, cancelled? full?

        db.collection("job_listing")
                .add(jobData)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Job listing successfully added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post", Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
                    hideLoading();
                });
    }
}
