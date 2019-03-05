package co.servicedesk.faveo.pro.model;

public class ProblemModel {
    private String email,subject,createdDate,priority;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ProblemModel(String email, String subject, String createdDate, int id,String priority) {
        this.email = email;
        this.subject = subject;
        this.createdDate = createdDate;
        this.id = id;
        this.priority=priority;
    }


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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
