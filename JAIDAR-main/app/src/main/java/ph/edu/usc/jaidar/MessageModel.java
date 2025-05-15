package ph.edu.usc.jaidar;

public class MessageModel {
    private String sender;
    private String receiver;
    private String message;

    public MessageModel() {}

    public MessageModel(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}
