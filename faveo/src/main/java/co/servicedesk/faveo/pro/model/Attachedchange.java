package co.servicedesk.faveo.pro.model;

public class Attachedchange {
    int id;
    String imapact,requester,subject;

    public Attachedchange(int id, String imapact, String requester, String subject) {
        this.id = id;
        this.imapact = imapact;
        this.requester = requester;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImapact() {
        return imapact;
    }

    public void setImapact(String imapact) {
        this.imapact = imapact;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
