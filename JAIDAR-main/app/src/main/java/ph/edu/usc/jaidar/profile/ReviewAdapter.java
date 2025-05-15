package ph.edu.usc.jaidar.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ph.edu.usc.jaidar.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final List<Review> reviewList;

    public ReviewAdapter(List<Review> list) {
        this.reviewList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName, comment;
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            reviewerName = view.findViewById(R.id.reviewerName);
            comment = view.findViewById(R.id.comment);
            ratingBar = view.findViewById(R.id.ratingBar);
        }
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.reviewerName.setText(review.getReviewerName());
        holder.comment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
