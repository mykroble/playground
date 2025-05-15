package ph.edu.usc.jaidar.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ph.edu.usc.jaidar.R;

public class AboutFragment extends Fragment {

    private EditText locationInput, backgroundInput, experienceInput;
    private Button editBtn, saveBtn;
    private FirebaseFirestore db;
    private String uid;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        locationInput = view.findViewById(R.id.locationInput);
        backgroundInput = view.findViewById(R.id.backgroundInput);
        experienceInput = view.findViewById(R.id.experienceInput);
        editBtn = view.findViewById(R.id.editBtn);
        saveBtn = view.findViewById(R.id.saveBtn);

        setEditingEnabled(false);
        db = FirebaseFirestore.getInstance();

        uid = getArguments().getString("profileUid");
        String currentUid = FirebaseAuth.getInstance().getUid();
        boolean isOwner = uid.equals(currentUid);

        loadUserData();

        if (isOwner) {
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(v -> {
                setEditingEnabled(true);
                saveBtn.setVisibility(View.VISIBLE);
            });

            saveBtn.setOnClickListener(v -> {
                saveUserData();
                setEditingEnabled(false);
                saveBtn.setVisibility(View.GONE);
            });
        } else {
            editBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
        }

        return view;
    }


    private void loadUserData() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        locationInput.setText(snapshot.getString("location"));
                        backgroundInput.setText(snapshot.getString("background"));
                        experienceInput.setText(snapshot.getString("experience"));
                    }
                });
    }

    private void saveUserData() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("location", locationInput.getText().toString().trim());
        updates.put("background", backgroundInput.getText().toString().trim());
        updates.put("experience", experienceInput.getText().toString().trim());

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setEditingEnabled(boolean enabled) {
        locationInput.setEnabled(enabled);
        backgroundInput.setEnabled(enabled);
        experienceInput.setEnabled(enabled);
    }
}
