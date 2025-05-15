package ph.edu.usc.jaidar;

public class User {
    private String uid;
    private String name;
    private String email;
    private String gender;
    private int age;
    private String location;
    private String experience;
    private String background;
    private String applicationStatus; //optional to use
    private String job_recruitment_apply_id;
    public User(){};
    public User(String uid, String name, String email, String gender, int age, String location, String experience, String background) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.location = location;
        this.experience = experience;
        this.background = background;
    }
//    getter
    public String getUid(){
        return this.uid;
    }
    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    public String getGender() {
        return this.gender;
    }
    public int getAge() {
        return this.age;
    }
    public String getLocation() {
        return this.location;
    }
    public String getExperience() {
        return this.experience;
    }

    public String getBackground() {
        return this.background;
    }
    public String getApplicationStatus(){return this.applicationStatus;}
    public String getJob_recruitment_apply_id(){ return this.job_recruitment_apply_id; }

//    setter
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setBackground(String background) {
        this.background = background;
    }
    public void setApplicationStatus(String applicationStatus){this.applicationStatus = applicationStatus; }
    public void setJob_recruitment_apply_id(String job_recruitment_apply_id){ this.job_recruitment_apply_id = job_recruitment_apply_id; }
}
