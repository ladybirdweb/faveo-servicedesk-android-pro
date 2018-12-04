package co.servicedesk.faveo.pro.model;

public class ChangeModel {

    private String subject,createdDate;
    private int id;

    public ChangeModel(String subject, String createdDate, int id) {
        this.subject = subject;
        this.createdDate = createdDate;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
