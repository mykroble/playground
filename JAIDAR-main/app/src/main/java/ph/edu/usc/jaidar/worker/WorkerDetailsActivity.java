package ph.edu.usc.jaidar.worker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.view.View;
import androidx.core.view.WindowInsetsCompat;

import ph.edu.usc.jaidar.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WorkerDetailsActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getUid();
    private String posterUid, job_listing_id;
    private TextView titleView, descriptionView, rateView, tagView, posterNameView;
    private Button offerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        titleView = findViewById(R.id.workerTitle);
        descriptionView = findViewById(R.id.workerDescription);
        rateView = findViewById(R.id.workerRate);
        tagView = findViewById(R.id.workerTag);
        posterNameView = findViewById(R.id.posterName);
        offerBtn = findViewById(R.id.offer_button);

        titleView.setText(getIntent().getStringExtra("title"));
        descriptionView.setText(getIntent().getStringExtra("description"));
        rateView.setText("₱" + getIntent().getIntExtra("rate", 0));
        tagView.setText(getIntent().getStringExtra("tag"));

        job_listing_id = getIntent().getStringExtra("job_listing_id");
        posterUid = getIntent().getStringExtra("posterUid");
        FirebaseFirestore.getInstance().collection("users")
                .document(posterUid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    posterNameView.setText(snapshot.getString("name"));
                })
                .addOnFailureListener(e -> {
                    posterNameView.setText("Unknown Poster");
                });

        if(!isOwner()){
            showOfferBtn();
            setOfferBtn();
        } else {
            hideOfferBtn();
        }
    }

    private boolean isOwner(){
        return (this.userId.equals(this.posterUid)) ? true : false;
    }
    private void setOfferBtn(){
        db.collection("job_listing_offer")
                .whereEqualTo("job_listing", job_listing_id)
                .whereEqualTo("hirer", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if(!snapshot.isEmpty()){
                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                        offerBtn.setText(doc.getString("status"));
                        offerBtn.setEnabled(false);
                    } else {
                        offerBtn.setText("Send Offer!");
                        offerBtn.setEnabled(true);
                        offerBtn.setOnClickListener(view -> {
                            offer();
                        });
                    }
                }).addOnFailureListener(e -> {
                    offerBtn.setText("Error");
                    offerBtn.setEnabled(false);
                    Toast.makeText(this, "Failed to fetch offer status for this job listing.", Toast.LENGTH_SHORT).show();
                });
    }
    private void showOfferBtn(){
        offerBtn.setVisibility(View.VISIBLE);
    }
    private void hideOfferBtn(){
        offerBtn.setVisibility(View.GONE);
    }
    private void offer(){
        Map<String, Object> offer = new HashMap<>();
        offer.put("job_listing", job_listing_id);
        offer.put("poster", posterUid);
        offer.put("hirer", userId);
        offer.put("status", "pending");
        offer.put("offered_at", FieldValue.serverTimestamp());

        db.collection("job_listing_offer")
                .add(offer)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Offer successfully sent!", Toast.LENGTH_SHORT).show();
                    offerBtn.setText("pending");
                    offerBtn.setEnabled(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                });
    }
}
