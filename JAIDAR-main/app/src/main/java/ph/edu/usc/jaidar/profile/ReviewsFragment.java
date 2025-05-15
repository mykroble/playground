package ph.edu.usc.jaidar.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.edu.usc.jaidar.R;

public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private String currentUid; // the person logged in
    private Button writeReviewBtn;
    private FirebaseFirestore db;
    private String profileUid;
    private List<Review> reviewList;
    private ReviewAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(ph.edu.usc.jaidar.R.layout.fragment_reviews, container, false);

        recyclerView = view.findViewById(ph.edu.usc.jaidar.R.id.reviewList);
        writeReviewBtn = view.findViewById(ph.edu.usc.jaidar.R.id.writeReviewBtn);

        db = FirebaseFirestore.getInstance();
        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList);
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (getArguments() != null) {
            profileUid = getArguments().getString("profileUid", currentUid);
        }

        // Hide review button if viewing own profile
        if (profileUid.equals(currentUid)) {
            writeReviewBtn.setVisibility(View.GONE);
        } else {
            writeReviewBtn.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadReviews();

        writeReviewBtn.setOnClickListener(v -> showReviewDialog());

        return view;
    }

    private void loadReviews() {
        db.collection("users").document(profileUid).collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    reviewList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Review review = doc.toObject(Review.class);
                        reviewList.add(review);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showReviewDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(ph.edu.usc.jaidar.R.layout.dialog_write_review, null);
        EditText commentInput = dialogView.findViewById(ph.edu.usc.jaidar.R.id.commentInput);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarInput);

        new AlertDialog.Builder(getContext())
                .setTitle("Write a Review")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {

                    String comment = commentInput.getText().toString().trim();
                    float rating = ratingBar.getRating();

                    if (comment.isEmpty() || rating == 0f) {
                        Toast.makeText(getContext(), "All fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.collection("users").document(currentUid).get()
                            .addOnSuccessListener(snapshot -> {
                                String name = snapshot.getString("name");
                                if (name == null) name = "Anonymous";

                                Map<String, Object> review = new HashMap<>();
                                review.put("reviewerName", name);
                                review.put("comment", comment);
                                review.put("rating", rating);
                                review.put("timestamp", FieldValue.serverTimestamp());

                                db.collection("users").document(profileUid)
                                        .collection("reviews").add(review)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Review submitted!", Toast.LENGTH_SHORT).show();
                                            loadReviews();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed to load your profile", Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
