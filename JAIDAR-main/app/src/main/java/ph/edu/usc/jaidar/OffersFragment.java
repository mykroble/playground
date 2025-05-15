package ph.edu.usc.jaidar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class OffersFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Context context;
    List<Offer> allOffers = new ArrayList<>();
    List<Offer> outboundOffers = new ArrayList<>();
    List<Offer> inBoundOffers = new ArrayList<>();
    private RecyclerView outboundList, inboundList;
    private TextView outboundEmpty, inboundEmpty, inboundTitle, outboundTitle;
    InboundAdapter inboundAdapter;
//    OutboundAdapter outboundAdapter;
    public OffersFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        outboundList = view.findViewById(R.id.outbound_list);
        inboundList = view.findViewById(R.id.inbound_list);
        outboundEmpty = view.findViewById(R.id.outbound_empty);
        inboundEmpty = view.findViewById(R.id.inbound_empty);
        inboundTitle = view.findViewById(R.id.inbound_title);
        outboundTitle = view.findViewById(R.id.outbound_title);

        inboundTitle.setVisibility(View.VISIBLE);
        inboundList.setLayoutManager(new LinearLayoutManager(getContext()));
        inboundAdapter = new InboundAdapter(this, context, inBoundOffers);
        inboundList.setAdapter(inboundAdapter);

        loadOffers();

    }
    private void loadOffers() {
        String currentUid = mAuth.getCurrentUser().getUid();

        //inbound
        db.collection("job_listing_offer")
                .whereEqualTo("poster", currentUid)
                .get()
                .addOnSuccessListener(offerSnapshot -> {
                    if(!offerSnapshot.isEmpty()) {
                        Log.d("OFFERS", "offerSnapshot found : " + offerSnapshot.size());
                        List<Offer> offers = new ArrayList<>();
                        List<String> jobListingIds = new ArrayList<>();
                        List<String> hirerIds = new ArrayList();

                        for (var doc : offerSnapshot) {
                            Offer offer = new Offer();
                            offer.setJobListingOffer(
                                    doc.getId(),
                                    doc.getString("job_listing"),
                                    doc.getString("status"),
                                    doc.getString("hirer"),
                                    doc.getString("poster")
                            );
                            offers.add(offer);
                            jobListingIds.add(doc.getString("job_listing"));
                            hirerIds.add(offer.getHirerId());
                        }

                        Log.d("OFFERS", "jobListingIds: " + jobListingIds);
                        db.collection("job_listing")
                                .whereIn(FieldPath.documentId(), jobListingIds)
                                .get()
                                .addOnSuccessListener(jobSnapshot -> {
                                    Map<String, Offer> jobMap = new HashMap<>();
                                    for (QueryDocumentSnapshot doc : jobSnapshot) {
                                        Offer content = new Offer();
                                        content.setJobListingData(
                                                doc.getString("title"),
                                                doc.getString("description"),
                                                doc.getLong("rate").doubleValue(),
                                                doc.getString("tag"),
                                                doc.getString("user_post"),
                                                doc.getString("status")
                                        );
                                        jobMap.put(doc.getId(), content);
                                        Log.w("OFFERS", "\njobMap: " + jobMap.get(doc.getId().toString()));
                                    }

                                    for (Offer offer : offers) {
                                        Offer job = jobMap.get(offer.getJobListingId());
                                        if (job != null) {
                                            offer.setJobListingData(
                                                    job.getTitle(),
                                                    job.getDescription(),
                                                    job.getRate(),
                                                    job.getTag(),
                                                    job.getUserPost(),
                                                    job.getStatus()
                                            );
                                        } else {
                                            Log.d("OFFERS", "No job found for ID: " + offer.getJobListingId());
                                        }
                                    }

//                                    Log.w("OFFERS", "\nhirerIds: " + hirerIds.toString());
//                                    for(Offer offer : offers) {
//                                        if(offer != null && offer.getHirerId() != null && !offer.getHirerId().isEmpty()) {
//                                            db.collection("users")
//                                                    .document(offer.getHirerId())
//                                                    .get()
//                                                    .addOnCompleteListener(task -> {
//                                                        Log.d("OFFERS", "User query completed. Successful: " + task.isSuccessful());
//                                                    })
//                                                    .addOnSuccessListener(snapshot -> {
//                                                        User hirer = new User();
//                                                        hirer.setUid(snapshot.getId());
//                                                        hirer.setName(snapshot.getString("name"));
//                                                        hirer.setEmail(snapshot.getString("email"));
//                                                        Log.d("OFFERS", "hirer name: " + snapshot.getString("name"));
//                                                        Log.d("OFFERS", "hirer User instance: " + hirer.toString());
//                                                        offer.setHirer(hirer);
//                                                    })
//                                                    .addOnFailureListener(e -> {
//                                                        Log.e("OFFERS", "Error fetching users", e);
//                                                        Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show();
//                                                    });
//                                        }
//                                    }

//                                    db.collection("users").whereIn("uid", hirerIds).get()
//                                            .addOnSuccessListener(usersSnapshot -> {
//                                                Map<String, User> userDataMap = new HashMap<>();
//                                                for(DocumentSnapshot doc : usersSnapshot){
//                                                    User hirer = new User();
//                                                    hirer.setUid(doc.getId());
//                                                    hirer.setName(doc.getString("name"));
//                                                    hirer.setEmail(doc.getString("email"));
//                                                    userDataMap.put(doc.getId(), hirer);
//                                                }
//
//                                                for(Offer offer : offers){
//                                                    User hirer = userDataMap.get(offer.getHirerId());
//                                                    offer.setHirer(hirer);
//                                                }
//                                                inBoundOffers.clear();
//                                                inBoundOffers.addAll(offers);
//                                                inboundAdapter.notifyDataSetChanged();
//                                            });

                                    db.collection("users")
                                            .whereIn(FieldPath.documentId(), new ArrayList<>(new HashSet<>(hirerIds)))
                                            .get()
                                            .addOnSuccessListener(snapshot -> {
                                                Map<String, User> userDataMap = new HashMap<>();
                                                for(DocumentSnapshot doc : snapshot){
                                                    User hirer = new User();
                                                    hirer.setUid(doc.getId());
                                                    hirer.setName(doc.getString("name"));
                                                    hirer.setEmail(doc.getString("email"));
                                                    userDataMap.put(doc.getId(), hirer);
                                                }

                                                Log.d("OFFERS", "\n\ncorrect key:  " + hirerIds);
                                                for(Offer offer : offers){
                                                    Log.d("OFFERS", "\n\noffer.getHiredId: " + offer.getHirerId());
                                                    Log.d("OFFERS", "\n\nuserDataMap.get(offer.getHirerId(): " + userDataMap.get(offer.getHirerId()));
                                                    User hirer = userDataMap.get(offer.getHirerId());
                                                    offer.setHirer(hirer);
                                                }
                                                inBoundOffers.clear();
                                                inBoundOffers.addAll(offers);
                                                inboundAdapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("OFFERS", "Error fetching users", e);
                                                Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show();
                                            });

                                            Log.d("OFFERS", "\noffers count : " +offers.size() + " | offers: " + offers);
                                            if(offers.isEmpty()){
                                                inboundList.setVisibility(View.GONE);
                                                inboundEmpty.setVisibility(View.VISIBLE);
                                            } else {
                                                inboundEmpty.setVisibility(View.GONE);
                                                inboundList.setVisibility(View.VISIBLE);
                                            }
                                });
                    } else {
                        //no inbound
                        inBoundOffers.clear();
                        inboundList.setVisibility(View.GONE);
                        inboundEmpty.setVisibility(View.VISIBLE);
                        inboundAdapter.notifyDataSetChanged();
                    }
                });
    }
    private void fetchUsers(List<String> hirerIds, Consumer<Map<String, User>> onComplete) {
        Map<String, User> userDataMap = new HashMap<>();
        List<List<String>> chunks = new ArrayList<>();

        for (int i = 0; i < hirerIds.size(); i += 10) {
            int end = Math.min(i + 10, hirerIds.size());
            chunks.add(hirerIds.subList(i, end));
        }

        AtomicInteger completed = new AtomicInteger(0);
        int totalChunks = chunks.size();

        for (List<String> chunk : chunks) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        for (DocumentSnapshot doc : snapshot) {
                            User user = new User();
                            user.setUid(doc.getId());
                            user.setName(doc.getString("name"));
                            user.setEmail(doc.getString("email"));
                            userDataMap.put(doc.getId(), user);
                        }

                        if (completed.incrementAndGet() == totalChunks) {
                            onComplete.accept(userDataMap); // all chunks done
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OFFERS", "Failed to fetch chunk", e);
                    });
        }
    }

    public void myNotify(){
        inboundAdapter.notifyDataSetChanged();
    }

}
