package ph.edu.usc.jaidar.profile;

public class Review {
    private String reviewerName;
    private String comment;
    private float rating;

    public Review() {} // required for Firestore

    public Review(String reviewerName, String comment, float rating) {
        this.reviewerName = reviewerName;
        this.comment = comment;
        this.rating = rating;
    }

    public String getReviewerName() { return reviewerName; }
    public String getComment() { return comment; }
    public float getRating() { return rating; }
}
