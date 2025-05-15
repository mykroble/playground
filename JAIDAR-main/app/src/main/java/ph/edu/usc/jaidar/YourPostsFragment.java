package ph.edu.usc.jaidar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class YourPostsFragment extends Fragment {

    private final Context context;
    private RecyclerView onGoingListView, completedListView;
    private TextView emptyOnGoingView, emptyCompletedView;
    private FirebaseFirestore db;
    private List<JobPost> postList;
    private final List<JobPost> onGoingPostList = new ArrayList<>();
    private final List<JobPost> completedPostList = new ArrayList<>();
    private YourPostsOnGoingAdapter onGoingAdapter;
    private YourPostsCompletedAdapter completedAdapter;
    public YourPostsFragment(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_posts, container, false);
        emptyOnGoingView = view.findViewById(R.id.ongoingEmptyMessage);
        emptyCompletedView = view.findViewById(R.id.completedEmptyMessage);

        completedListView = view.findViewById(R.id.completed_post_list);
        completedListView.setLayoutManager(new LinearLayoutManager(getContext()));
        completedAdapter = new YourPostsCompletedAdapter(context, completedPostList);
        completedListView.setAdapter(completedAdapter);

        onGoingListView = view.findViewById(R.id.on_going_post_list);
        onGoingListView.setLayoutManager(new LinearLayoutManager(getContext()));
        onGoingAdapter = new YourPostsOnGoingAdapter(this, context, onGoingPostList);
        onGoingListView.setAdapter(onGoingAdapter);

        postList = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("job_recruitments")
                .whereEqualTo("user_post", uid)
                .orderBy("posted_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(postSnap -> {
                    if (postSnap.isEmpty()) {
                        return;
                    }

                    Map<String, JobPost> postMap = new HashMap<>();
                    for (QueryDocumentSnapshot doc : postSnap) {
                        JobPost jp = new JobPost(
                                doc.getId(),
                                doc.getString("title"),
                                doc.getString("description"),
                                doc.getLong("headcount").intValue(),
                                doc.getString("tag"),
                                doc.getDouble("rate"),
                                doc.getString("user_post"),
                                doc.getString("status")
                        );
                        postMap.put(doc.getId(), jp);
                    }

                    // Step 2: Load all applicants for *all* these posts in one query
                    db.collection("job_recruitment_apply")
                            .whereIn("job_recruitment", new ArrayList<>(postMap.keySet()))
                            .get()
                            .addOnSuccessListener(applySnap -> {
                                Map<String, List<String>> postToApplicants = new HashMap<>();
                                Map<String, String> userStatusMap = new HashMap<>();
                                Map<String, String> userToApplyMap = new HashMap<>();
                                for (QueryDocumentSnapshot applyDoc : applySnap) {
                                    String applyId = applyDoc.getId();
                                    String postId = applyDoc.getString("job_recruitment");
                                    String userId = applyDoc.getString("apply_user");
                                    String status = applyDoc.getString("status");

                                    if (postId != null && userId != null) {
                                        postToApplicants
                                                .computeIfAbsent(postId, k -> new ArrayList<>())
                                                .add(userId);
                                        String key = postId + "_" + userId;
                                        userStatusMap.put(key, status);
                                        userToApplyMap.put(key, applyId);
                                    }
                                }


                                // Step 3: Batch-fetch user details for all applicants
                                Set<String> allUserIds = new HashSet<>();
                                for (List<String> applicants : postToApplicants.values()) {
                                    allUserIds.addAll(applicants);
                                }

                                if (!allUserIds.isEmpty()) {
                                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
                                    for (String userId : allUserIds) {
                                        userTasks.add(db.collection("users").document(userId).get());
                                    }

                                    // When all user data is fetched, process the data
                                    Tasks.whenAllSuccess(userTasks)
                                            .addOnSuccessListener(results -> {
                                                Map<String, User> users = new HashMap<>();
                                                for (Object result : results) {
                                                    DocumentSnapshot userDoc = (DocumentSnapshot) result;
                                                    User user = userDoc.toObject(User.class);
                                                    if (user != null) {
                                                        String userid = userDoc.getId();
                                                        user.setUid(userid);

                                                        users.put(user.getUid(), user);
                                                    }
                                                }

                                                // Step 4: Assign applicants to job posts
                                                for (Map.Entry<String, List<String>> entry : postToApplicants.entrySet()) {
                                                    String postId = entry.getKey();
                                                    JobPost jp  = postMap.get(postId);
                                                    jp.setApplicants(new ArrayList<>());

                                                    for (String userId : entry.getValue()) {
                                                        User originalUser = users.get(userId);
                                                        if (originalUser == null) continue;

                                                        String key = postId + "_" + userId;

                                                        // Create a fresh copy for this job post
                                                        User applicant = new User();
                                                        applicant.setUid(originalUser.getUid());
                                                        applicant.setName(originalUser.getName());
                                                        applicant.setEmail(originalUser.getEmail());
                                                        applicant.setLocation(originalUser.getLocation());
                                                        applicant.setApplicationStatus(userStatusMap.get(key));
                                                        applicant.setJob_recruitment_apply_id(userToApplyMap.get(key));

                                                        jp.addApplicant(applicant);
                                                    }

                                                }
                                                splitAndNotify(postMap);
                                            });
                                } else {
                                    splitAndNotify(postMap);
                                }
                            });
                });
        return view;
    }

    public void splitAndNotify(Map<String, JobPost> postMap){
        postList.addAll(postMap.values());
        onGoingPostList.clear();
        completedPostList.clear();
        for (JobPost job : postList) {
            if (!job.getStatus().isEmpty() && job.getStatus().equals("active")) {
                onGoingPostList.add(job);
            } else if (!job.getStatus().isEmpty() && job.getStatus().equals("completed")) {
                List<User> temp = job.getAllApplicant();
                if(temp != null){
                    job.setApplicants(new ArrayList<>());
                    for(User user : temp){
                        if("accepted".equals(user.getApplicationStatus())) {
                            job.addApplicant(user);
                        }
                    }
                }
                completedPostList.add(job);
            }
        }
        if (onGoingPostList.isEmpty()) {
            onGoingListView.setVisibility(View.GONE);
            emptyOnGoingView.setVisibility(View.VISIBLE);
        } else {
            emptyOnGoingView.setVisibility(View.GONE);
            onGoingListView.setVisibility(View.VISIBLE);
        }
        if (completedPostList.isEmpty()) {
            completedListView.setVisibility(View.GONE);
            emptyCompletedView.setVisibility(View.VISIBLE);
        } else {
            emptyCompletedView.setVisibility(View.GONE);
            completedListView.setVisibility(View.VISIBLE);
        }
        onGoingAdapter.notifyDataSetChanged();
        completedAdapter.notifyDataSetChanged();
        Log.d("MYDEBUG", "full list: " + postList.size());
        Log.d("MYDEBUG", "ongoing list: " + onGoingPostList.size());
        Log.d("MYDEBUG", "complete list: " + completedPostList.size());
    }
    public void markPostAsComplete(JobPost job) {
        job.setStatus("completed");

        onGoingPostList.remove(job);

        completedPostList.add(job);

        if (onGoingPostList.isEmpty()) {
            onGoingListView.setVisibility(View.GONE);
            emptyOnGoingView.setVisibility(View.VISIBLE);
        } else {
            emptyOnGoingView.setVisibility(View.GONE);
            onGoingListView.setVisibility(View.VISIBLE);
        }
        if (completedPostList.isEmpty()) {
            completedListView.setVisibility(View.GONE);
            emptyCompletedView.setVisibility(View.VISIBLE);
        } else {
            emptyCompletedView.setVisibility(View.GONE);
            completedListView.setVisibility(View.VISIBLE);
        }
        onGoingAdapter.notifyDataSetChanged();
        completedAdapter.notifyDataSetChanged();
    }

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_your_posts, container, false);
//
//        recyclerView = view.findViewById(R.id.postList);
//        emptyView    = view.findViewById(R.id.empty_post);
//
//        db       = FirebaseFirestore.getInstance();
//        postList = new ArrayList<>();
//        adapter  = new YourPostsAdapter(postList);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
//
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // 1) Load all posts by this user
//        db.collection("job_recruitments")
//                .whereEqualTo("user_post", uid)
//                .orderBy("posted_at", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(postSnap -> {
//                    if (postSnap.isEmpty()) {
//                        emptyView.setVisibility(View.VISIBLE);
//                        return;
//                    }
//
//                    emptyView.setVisibility(View.GONE);
//
//                    // Map of postId → JobPost
//                    Map<String, JobPost> postMap = new HashMap<>();
//                    for (DocumentSnapshot doc : postSnap) {
//                        JobPost jp = new JobPost(
//                                doc.getId(),
//                                doc.getString("title"),
//                                doc.getString("description"),
//                                doc.getLong("headcount").intValue(),
//                                doc.getDouble("rate"),
//                                doc.getString("user_post")
//                        );
//                        jp.setApplicants(new ArrayList<>());        // ensure list is initialized
//                        postMap.put(doc.getId(), jp);
//                    }
//                    Log.d("MYDEBUG", "Post Map: " + postMap);
//
//                    // 2) Load all applies for *all* these posts in one query
//                    db.collection("job_recruitment_apply")
//                            .whereIn("job_recruitment", new ArrayList<>(postMap.keySet()))
//                            .orderBy("applied_at", Query.Direction.ASCENDING)
//                            .get()
//                            .addOnSuccessListener(applySnap -> {
//                                // group uids by postId
//                                Map<String, List<String>> postToUids = new HashMap<>();
//                                Set<String> allUids = new HashSet<>();
//
//                                for (DocumentSnapshot aDoc : applySnap) {
//                                    String postId = aDoc.getString("job_recruitment");
//                                    String applyUid = aDoc.getString("apply_user");
//                                    if (postId == null || applyUid == null) continue;
//
//                                    allUids.add(applyUid);
//                                    postToUids.computeIfAbsent(postId, k -> new ArrayList<>())
//                                            .add(applyUid);
//                                }
//
//                                if (allUids.isEmpty()) {
//                                    // no applicants at all → just display posts
//                                    postList.addAll(postMap.values());
//                                    adapter.notifyDataSetChanged();
//                                    return;
//                                }
//
//                                // 3) Batch-fetch all user profiles in parallel
//                                List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
//                                for (String applyUid : allUids) {
//                                    userTasks.add(db.collection("users").document(applyUid).get());
//                                }
//
//                                Tasks.whenAllSuccess(userTasks)
//                                        .addOnSuccessListener(results -> {
//                                            // map UID → User
//                                            Map<String, User> userMap = new HashMap<>();
//                                            for (Object o : results) {
//                                                DocumentSnapshot uDoc = (DocumentSnapshot) o;
//                                                User u = uDoc.toObject(User.class);
//                                                u.setUid(uDoc.getId());
//                                                userMap.put(uDoc.getId(), u);
//                                            }
//
//                                            // 4) assign each applicant to its JobPost
//                                            for (Map.Entry<String, List<String>> entry : postToUids.entrySet()) {
//                                                JobPost jp = postMap.get(entry.getKey());
//                                                if (jp == null) continue;
//                                                for (String appUid : entry.getValue()) {
//                                                    User applicant = userMap.get(appUid);
//                                                    if (applicant != null) {
//                                                        jp.addApplicant(applicant);
//                                                    }
//                                                }
//                                            }
//
//                                            // finally, update your list & adapter
//                                            postList.addAll(postMap.values());
//                                            adapter.notifyDataSetChanged();
//                                        });
//                            });
//                });
//                                            Log.d("MYDEBUG", "Post List Size: " + postList.size());
//
//        return view;
//    }

}
