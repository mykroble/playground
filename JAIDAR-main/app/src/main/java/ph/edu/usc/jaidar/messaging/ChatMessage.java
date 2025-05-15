package ph.edu.usc.jaidar.messaging;

public class ChatMessage {
    private String message, sender;
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(String message, String sender, long timestamp) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getMessage() { return message; }
    public String getSender() { return sender; }
    public long getTimestamp() { return timestamp; }
}
