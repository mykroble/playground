package ph.edu.usc.jaidar.worker;



import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ph.edu.usc.jaidar.R;
import ph.edu.usc.jaidar.worker.WorkerDetailsActivity;

public class WorkerJobAdapter extends RecyclerView.Adapter<WorkerJobAdapter.ViewHolder> {

    private Context context;
    private List<WorkerJob> jobList;

    public WorkerJobAdapter(Context context, List<WorkerJob> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.jobTitle);
            subtitle = view.findViewById(R.id.name);
            icon = view.findViewById(R.id.jobIcon);
        }
    }

    @NonNull
    @Override
    public WorkerJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerJobAdapter.ViewHolder holder, int position) {
        WorkerJob job = jobList.get(position);
        holder.title.setText(job.getTitle());
        holder.subtitle.setText("₱" + job.getRate() + " • " + job.getTag());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkerDetailsActivity.class);
            intent.putExtra("job_listing_id", job.getId());
            intent.putExtra("title", job.getTitle());
            intent.putExtra("description", job.getDescription());
            intent.putExtra("rate", job.getRate());
            intent.putExtra("tag", job.getTag());
            intent.putExtra("posterUid", job.getUserPost());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }
}
