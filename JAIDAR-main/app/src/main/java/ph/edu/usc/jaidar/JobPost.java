package ph.edu.usc.jaidar;

import java.util.ArrayList;
import java.util.List;

public class JobPost {
    private String id;
    private String title;
    private String description;
    private int headcount;
    private String tag;
    private double rate;
    private String userPost;
    private String status;
    private List<User> applicantList;

    public JobPost() {}

    public JobPost(String id, String title, String description, int headcount, String tag, double rate, String userPost, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.headcount = headcount;
        this.tag = tag;
        this.rate = rate;
        this.userPost = userPost;
        this.status = status;   //active, completed,
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getHeadcount() { return headcount; }
    public double getRate() { return rate; }
    public String getUserPost() { return userPost; }
    public List<User> getAllApplicant(){
        return this.applicantList;
    }
    public void addApplicant(User applicant){
        this.applicantList.add(applicant);
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public void setApplicants(List<User> applicantList){
        this.applicantList = applicantList;
    }
}
