package co.servicedesk.faveo.pro.model;

public class ProblemModel {
    private String email,subject;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProblemModel(String email, String subject, int id) {
        this.email = email;
        this.subject = subject;
        this.id = id;
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
