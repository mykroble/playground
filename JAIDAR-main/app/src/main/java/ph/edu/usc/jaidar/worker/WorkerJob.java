package ph.edu.usc.jaidar.worker;


public class WorkerJob {
    private String id;
    private String title;
    private String description;
    private int rate;
    private String tag;
    private String user_post;
    private String status;

    public WorkerJob() {} // Required for Firestore

    public WorkerJob(String id, String title, String description, int rate, String tag, String user_post, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rate = rate;
        this.tag = tag;
        this.user_post = user_post;
        this.status = status;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getRate() { return rate; }
    public String getTag() { return tag; }
    public String getUserPost() { return user_post; }
    public String getStatus() { return status; }
}
