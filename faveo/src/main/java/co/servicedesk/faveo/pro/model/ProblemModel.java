package co.servicedesk.faveo.pro.model;

public class ProblemModel {
    private String email,subject,createdDate;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ProblemModel(String email, String subject, String createdDate, int id) {
        this.email = email;
        this.subject = subject;
        this.createdDate = createdDate;
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
