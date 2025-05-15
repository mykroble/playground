package ph.edu.usc.jaidar;

public class Offer {
    private String id;
    private String job_listing_id;
    private String offer_status;
    private String poster_id;
    private String hirer_id;
    private String title;
    private String description;
    private Double rate;
    private String tag;
    private String listing_user_post;
    private String listing_status;
    private User hirer = new User();
    private User poster = new User();

    public Offer() {}

    public Offer(String id, String job_listing_id, String offer_status, String poster_id, String hirer_id,
                 String title, String description, Double rate, String tag, String listing_user_post, String listing_status) {
        this.id = id;
        this.job_listing_id = job_listing_id;
        this.offer_status = offer_status;
        this.poster_id = poster_id;
        this.hirer_id = hirer_id;
        this.title = title;
        this.description = description;
        this.rate = rate;
        this.tag = tag;
        this.listing_user_post = listing_user_post;
        this.listing_status = listing_status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobListingId() { return this.job_listing_id; }
    public void setJobListingId(String job_listing_id) { this.job_listing_id = job_listing_id; }

    public String getJobListingStatus() { return offer_status; }
    public void setJobListingStatus(String offer_status) { this.offer_status = offer_status; }

    public String getPosterId() { return poster_id; }
    public void setPosterId(String poster_id) { this.poster_id = poster_id; }
    public String getHirerId() { return hirer_id; }
    public void setHirerId(String hirer_id) { this.hirer_id = hirer_id; }
    public void setHirer(User hirer){
        this.hirer = hirer;
    }
    public User getHirer(){
        return this.hirer;
    }
    public void setPoster(User poster){
        this.poster = poster;
    }
    public User getPoster(){
        return this.poster;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getUserPost() { return listing_user_post; }
    public void setUserPost(String listing_user_post) { this.listing_user_post = listing_user_post; }

    public String getStatus() { return listing_status; }
    public void setStatus(String listing_status) { this.listing_status = listing_status; }

    public void setJobListingData(String title, String descpription, Double rate, String tag, String listing_user_post, String listing_status){
        this.title = title;
        this.description = descpription;
        this.rate = rate;
        this.tag = tag;
        this.listing_user_post = listing_user_post;
        this.listing_status = listing_status;
    }

    public void setJobListingOffer(String id, String job_listing_id, String status, String hirer, String poster){
        this.id = id;
        this.job_listing_id = job_listing_id;
        this.offer_status = status;
        this.hirer_id = hirer;
        this.poster_id = poster;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "jobListingId='" + job_listing_id + '\'' +
                ", title='" + title + '\'' +
                ", rate=" + rate +
                ", tag='" + tag + '\'' +
                ", poster_id=" + poster_id +
                ", hirer_id=" + hirer_id +
                ", Hirer{" +
                    "id=" + hirer +
                    ", name=" + hirer.getName() +
                    ", email=" + hirer.getEmail() +
                "}" +
                '}';
    }
}
