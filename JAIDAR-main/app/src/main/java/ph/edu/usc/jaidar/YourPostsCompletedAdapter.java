package ph.edu.usc.jaidar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class YourPostsCompletedAdapter extends RecyclerView.Adapter<YourPostsCompletedAdapter.ViewHolder> {
    private final List<JobPost> postList;
    private final Context context;

    public YourPostsCompletedAdapter(Context context, List<JobPost> list) {
        this.context = context;
        this.postList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, applicantNumber, headcount, userType;
        RecyclerView applicantList;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            applicantNumber = view.findViewById(R.id.applicant_number);
            headcount = view.findViewById(R.id.headcount);
            applicantList = view.findViewById(R.id.applicant_list);
            userType = view.findViewById(R.id.user_type);
        }
    }

    @NonNull
    @Override
    public YourPostsCompletedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_your_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JobPost jobPost = postList.get(position);
        holder.title.setText(jobPost.getTitle());
        holder.title.setOnClickListener(v -> {
            Intent intent = new Intent(context, JobListingDetailsActivity.class);
            intent.putExtra("jobRecruitmentId", postList.get(position).getId());
            intent.putExtra("jobTitle", postList.get(position).getTitle());
            intent.putExtra("jobSubtitle", "₱" + postList.get(position).getRate() + " • Headcount: " + postList.get(position).getHeadcount());
            intent.putExtra("about", postList.get(position).getDescription());
            intent.putExtra("posterUid", postList.get(position).getUserPost());
            context.startActivity(intent);
        });

        List<User> applicants = jobPost.getAllApplicant();
        int applicantCount = applicants != null ? applicants.size() : 0;
        holder.userType.setText("worker(s)");

        holder.applicantNumber.setText(String.valueOf(applicantCount));
        holder.headcount.setText(String.valueOf(jobPost.getHeadcount()));

        ApplicantAdapter adapter = new ApplicantAdapter(this.context, postList.get(position));
        holder.applicantList.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.applicantList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
