package ph.edu.usc.jaidar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ph.edu.usc.jaidar.messaging.UserListActivity;
import ph.edu.usc.jaidar.profile.UserProfileActivity;
import ph.edu.usc.jaidar.worker.WorkerJob;
import ph.edu.usc.jaidar.worker.WorkerJobAdapter;

public class HomePageActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button logoutBtn;
    Button hirer, worker;
    Spinner categorySpinner, workerSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page); // Shows your XML layout

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        categorySpinner = findViewById(R.id.categorySpinner);
        workerSpinner = findViewById(R.id.workerCategorySpinner);

        String[] categories = { //later on get from db firestore
                "All","Electrician", "Plumber", "Carpenter", "Welding", "Roofer", "Mechanic", "Caretaker", "Ironworker"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        categorySpinner.setAdapter(adapter);
        workerSpinner.setAdapter(adapter);
        workerSort(categories);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = categories[position];
                if (!selected.equals("All")) {
                    Jobpostings(FirebaseFirestore.getInstance(), selected);
                } else {
                    Jobpostings(FirebaseFirestore.getInstance());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        hirer = findViewById(R.id.btnHiring);
        hirer.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RecruitmentEditorActivity.class);
            intent.putExtra(RecruitmentEditorActivity.USER_ROLE, RecruitmentEditorActivity.HIRER); //WORKER or HIRER
            startActivity(intent);
        });
        worker = findViewById(R.id.btnWorker);
        worker.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RecruitmentEditorActivity.class);
            intent.putExtra(RecruitmentEditorActivity.USER_ROLE, RecruitmentEditorActivity.WORKER); //WORKER or HIRER
            startActivity(intent);
        });


        Bottomnavigation(mAuth);
        Jobpostings(db);
        loadWorkerJobs(db);

    }

    public void loadWorkerJobs(FirebaseFirestore db){
        RecyclerView recycler = findViewById(R.id.workerRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<WorkerJob> jobList = new ArrayList<>();
        WorkerJobAdapter adapter = new WorkerJobAdapter(this, jobList);
        recycler.setAdapter(adapter);

        db.collection("job_listing")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        WorkerJob job = new WorkerJob(
                                doc.getId(),
                                doc.getString("title"),
                                doc.getString("description"),
                                doc.getLong("rate").intValue(),
                                doc.getString("tag"),
                                doc.getString("user_post"),
                                doc.getString("status")
                        );
                        jobList.add(job);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void Bottomnavigation(FirebaseAuth mAuth) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home); // ✅ Always highlight Home in homepage

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                return true; // already here
            } else if (id == R.id.activity) {
                startActivity(new Intent(HomePageActivity.this, ActivitySectionActivity.class));
                return true;
            } else if (id == R.id.message) {
                startActivity(new Intent(HomePageActivity.this, UserListActivity.class));
                return true;       
            } else if (id == R.id.profile) {
                String currentUid = mAuth.getUid();
                Intent intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("profileUid", currentUid);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    private void workerSort(String[] categories){
        workerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = categories[position];
                if (!selected.equals("All")) {
                    loadWorkerJobsFiltered(FirebaseFirestore.getInstance(), selected);
                } else {
                    loadWorkerJobs(FirebaseFirestore.getInstance());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    public void loadWorkerJobsFiltered(FirebaseFirestore db, String tagFilter) {
        RecyclerView recycler = findViewById(R.id.workerRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<WorkerJob> jobList = new ArrayList<>();
        WorkerJobAdapter adapter = new WorkerJobAdapter(this, jobList);
        recycler.setAdapter(adapter);

        db.collection("job_listing")
                .whereEqualTo("status", "active")
                .whereEqualTo("tag", tagFilter)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        WorkerJob job = new WorkerJob(
                                doc.getId(),
                                doc.getString("title"),
                                doc.getString("description"),
                                doc.getLong("rate").intValue(),
                                doc.getString("tag"),
                                doc.getString("user_post"),
                                doc.getString("status")
                        );
                        jobList.add(job);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to filter jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void Jobpostings(FirebaseFirestore db) {
        Jobpostings(db, null); // default: no filter
    }

    public void Jobpostings(FirebaseFirestore db, String tagFilter) {
        RecyclerView jobRecycler = findViewById(R.id.jobPreviewRecycler);
        jobRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<JobPost> jobList = new ArrayList<>();
        JobPreviewAdapter adapter = new JobPreviewAdapter(this, jobList);
        jobRecycler.setAdapter(adapter);

        // Build base query
        com.google.firebase.firestore.Query query = db.collection("job_recruitments").whereEqualTo("status", "active");
        if (tagFilter != null && !tagFilter.equals("All")) {
            query = query.whereEqualTo("tag", tagFilter);
        }

        query.get()
                .addOnSuccessListener(querySnap -> {
                    for (DocumentSnapshot doc : querySnap) {
                        String id = doc.getId();
                        String title = doc.getString("title");
                        String description = doc.getString("description");
                        Long headcount = doc.getLong("headcount");
                        String tag = doc.getString("tag");
                        Double rate = doc.getDouble("rate");
                        String userPost = doc.getString("user_post");
                        String status = doc.getString("status");

                        JobPost job = new JobPost(
                                id,
                                title,
                                description,
                                headcount != null ? headcount.intValue() : 0,
                                tag,
                                rate != null ? rate : 0,
                                userPost,
                                status
                        );
                        jobList.add(job);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load job offers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}

