package co.servicedesk.faveo.pro.model;

public class AttachedRelease {
    private int id;
    private String subject,releaseType,priority;

    public AttachedRelease(int id, String subject, String releaseType, String priority) {
        this.id = id;
        this.subject = subject;
        this.releaseType = releaseType;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
