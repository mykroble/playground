package ph.edu.usc.jaidar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ph.edu.usc.jaidar.messaging.ChatActivity;
import ph.edu.usc.jaidar.R;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ViewHolder> {

    private JobPost job;
    private List<User> applicants;
    private Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ApplicantAdapter(Context context, JobPost job) {
        this.job = job;
        this.applicants = job.getAllApplicant();
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, location, currentStatus, acceptedView, rejectedView;
//        ImageView chatButton;
        LinearLayout applicantClick;
        Button acceptButton, rejectButton, cancelButton;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.applicant_name);
            email = view.findViewById(R.id.applicant_email);
            location = view.findViewById(R.id.applicant_location);
            applicantClick = view.findViewById(R.id.applicant_click);
            acceptButton = view.findViewById(R.id.accept_button);
            rejectButton = view.findViewById(R.id.reject_button);
            cancelButton = view.findViewById(R.id.cancel_button);
            currentStatus = view.findViewById(R.id.current_status);
            acceptedView = view.findViewById(R.id.accepted);
            rejectedView = view.findViewById(R.id.rejected);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_applicant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(applicants.get(position).getName());
        holder.email.setText(applicants.get(position).getEmail());
        holder.location.setText(applicants.get(position).getLocation());

        holder.applicantClick.setOnClickListener(v -> {
            Intent intent = new Intent(this.context, ChatActivity.class);
            intent.putExtra("receiverEmail", applicants.get(position).getEmail());
            intent.putExtra("receiverName", applicants.get(position).getName());
            context.startActivity(intent);
        });

        Log.d("MYDEBUG", "Title: " + job.getTitle() + " | Status: " + job.getStatus());
        if(!"completed".equals(job.getStatus())) {
            String status = applicants.get(position).getApplicationStatus();
            holder.currentStatus.setText(status);
            Log.d("MYDEBUG", "Name: " + applicants.get(position).getName() + " | Status: " + applicants.get(position).getApplicationStatus());
            if (status.equals("pending")) {
                // set pending status
                holder.currentStatus.setText("Pending");
                holder.currentStatus.setTextColor(Color.BLACK);

                holder.acceptButton.setVisibility(View.VISIBLE);
                holder.rejectButton.setVisibility(View.VISIBLE);
                holder.cancelButton.setVisibility(View.GONE);

                holder.acceptedView.setVisibility(View.GONE);
                holder.rejectedView.setVisibility(View.GONE);
            } else if (status.equals("accepted")) {
                //set accepted status
                holder.currentStatus.setText("Accepted!");
                holder.currentStatus.setTextColor(Color.GREEN);

                holder.acceptButton.setVisibility(View.GONE);
                holder.rejectButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.VISIBLE);

                holder.acceptedView.setVisibility(View.VISIBLE);
                holder.rejectedView.setVisibility(View.GONE);
            } else if (status.equals("rejected")) {
                //set rejected status
                holder.currentStatus.setText("Rejected");
                holder.currentStatus.setTextColor(Color.RED);

                holder.acceptButton.setVisibility(View.GONE);
                holder.rejectButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.GONE);

                holder.acceptedView.setVisibility(View.GONE);
            }

            holder.acceptButton.setOnClickListener(v -> {
                db.collection("job_recruitment_apply")
                        .document(applicants.get(position).getJob_recruitment_apply_id())
                        .update("status", "accepted")
                        .addOnSuccessListener(aVoid -> {
                            //set accepted status
                            holder.currentStatus.setVisibility(View.GONE);

                            holder.acceptButton.setVisibility(View.GONE);
                            holder.rejectButton.setVisibility(View.GONE);
                            holder.cancelButton.setVisibility(View.VISIBLE);

                            holder.acceptedView.setVisibility(View.VISIBLE);
                            holder.rejectedView.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        });
            });
            holder.rejectButton.setOnClickListener(v -> {
                db.collection("job_recruitment_apply")
                        .document(applicants.get(position).getJob_recruitment_apply_id())
                        .update("status", "rejected")
                        .addOnSuccessListener(aVoid -> {
                            //set rejected status
                            holder.currentStatus.setVisibility(View.GONE);

                            holder.acceptButton.setVisibility(View.GONE);
                            holder.rejectButton.setVisibility(View.GONE);
                            holder.cancelButton.setVisibility(View.GONE);

                            holder.acceptedView.setVisibility(View.GONE);
                            holder.rejectedView.setVisibility(View.VISIBLE);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        });
            });
            holder.cancelButton.setOnClickListener(v -> {
                db.collection("job_recruitment_apply")
                        .document(applicants.get(position).getJob_recruitment_apply_id())
                        .update("status", "pending")
                        .addOnSuccessListener(aVoid -> {
                            // set pending status
                            holder.currentStatus.setText("Pending");
                            holder.currentStatus.setVisibility(View.VISIBLE);

                            holder.acceptButton.setVisibility(View.VISIBLE);
                            holder.rejectButton.setVisibility(View.VISIBLE);
                            holder.cancelButton.setVisibility(View.GONE);

                            holder.acceptedView.setVisibility(View.GONE);
                            holder.rejectedView.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }

    @Override
    public int getItemCount() {
        if(this.applicants != null)
            return applicants.size();
        return 0;
    }
}
