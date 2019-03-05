package co.servicedesk.faveo.pro.model;

public class ChangeModel {

    private String subject,createdDate,priority,email;
    private int id;

    public ChangeModel(String subject, String createdDate, String priority, String email, int id) {
        this.subject = subject;
        this.createdDate = createdDate;
        this.priority = priority;
        this.email = email;
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
