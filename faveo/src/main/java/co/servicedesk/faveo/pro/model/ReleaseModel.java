package co.servicedesk.faveo.pro.model;

public class ReleaseModel {
    private String subject,priority,createdDate;
    private int id;

    public ReleaseModel(String subject, String priority, String createdDate, int id) {
        this.subject = subject;
        this.priority = priority;
        this.createdDate = createdDate;
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
