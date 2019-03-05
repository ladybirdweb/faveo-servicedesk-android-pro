package co.servicedesk.faveo.pro.model;

public class Attachedproblem {
    int id;
    String subject,from,impact,status,department;

    public Attachedproblem(int id, String subject, String from, String impact,String status,String department) {
        this.id = id;
        this.subject = subject;
        this.from = from;
        this.impact = impact;
        this.status=status;
        this.department=department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
