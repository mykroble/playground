package ph.edu.usc.jaidar.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ph.edu.usc.jaidar.MessageModel;
import ph.edu.usc.jaidar.R;

public class ChatActivity extends AppCompatActivity {

    RecyclerView chatRecyclerView;
    EditText messageEditText;
    ImageButton sendButton;
    TextView chatHeaderTextView;
    ImageView backBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    String senderEmail;
    String receiverEmail;
    String receiverName;

    ArrayList<MessageModel> messageList;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.chatMessageEditText);
        sendButton = findViewById(R.id.chatSendButton);
        chatHeaderTextView = findViewById(R.id.chatHeaderText);
        backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(v -> goBack());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        senderEmail = mAuth.getCurrentUser().getEmail().toLowerCase();
        receiverEmail = getIntent().getStringExtra("receiverEmail").toLowerCase();
        receiverName = getIntent().getStringExtra("receiverName");

        if (receiverEmail == null || senderEmail == null) {
            Toast.makeText(ChatActivity.this, "Error: Missing email", Toast.LENGTH_SHORT).show();
            goBack();
            return;
        }

        chatHeaderTextView.setText(receiverName);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, senderEmail);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void goBack() {
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) return;

        CollectionReference chatRef = db.collection("messages");

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("senderEmail", senderEmail); // updated field name
        msgData.put("receiverEmail", receiverEmail); // updated field name
        msgData.put("message", message);
        msgData.put("timestamp", System.currentTimeMillis());

        chatRef.add(msgData)
                .addOnSuccessListener(documentReference -> messageEditText.setText(""))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show());
    }

    private void loadMessages() {
        db.collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(ChatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        messageList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            String sender = doc.getString("senderEmail");
                            String receiver = doc.getString("receiverEmail");
                            String text = doc.getString("message");

                            if (
                                    (sender != null && receiver != null && text != null) &&
                                            ((sender.equals(senderEmail) && receiver.equals(receiverEmail)) ||
                                                    (sender.equals(receiverEmail) && receiver.equals(senderEmail)))
                            ) {
                                messageList.add(new MessageModel(sender, receiver, text));
                            }
                        }

                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }
}
