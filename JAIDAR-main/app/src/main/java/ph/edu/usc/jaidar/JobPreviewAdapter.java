package ph.edu.usc.jaidar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JobPreviewAdapter extends RecyclerView.Adapter<JobPreviewAdapter.ViewHolder> {

    private final List<JobPost> jobList;
    private final Context context;

    public JobPreviewAdapter(Context context, List<JobPost> jobs) {
        this.context = context;
        this.jobList = jobs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.jobTitle);
            subtitle = itemView.findViewById(R.id.name);
            icon = itemView.findViewById(R.id.jobIcon);
        }
    }

    @NonNull
    @Override
    public JobPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobPost job = jobList.get(position);
        holder.title.setText(job.getTitle());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(job.getUserPost())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String name = snapshot.getString("name");
                    holder.subtitle.setText(name != null ? name : "Unknown");
                })
                .addOnFailureListener(e -> {
                    holder.subtitle.setText("Error");
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobListingDetailsActivity.class);
            intent.putExtra("jobRecruitmentId", job.getId());
            intent.putExtra("jobTitle", job.getTitle());
            intent.putExtra("jobSubtitle", "₱" + job.getRate() + " • Headcount: " + job.getHeadcount());
            intent.putExtra("about", job.getDescription());
            intent.putExtra("posterUid", job.getUserPost());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }
}

