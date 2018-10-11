package co.servicedesk.faveo.pro.model;

public class Attachedproblem {
    int id;
    String subject,from,impact;

    public Attachedproblem(int id, String subject, String from, String impact) {
        this.id = id;
        this.subject = subject;
        this.from = from;
        this.impact = impact;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }
}
