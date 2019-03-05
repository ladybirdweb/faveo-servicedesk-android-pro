package co.servicedesk.faveo.pro.model;

public class ProblemAssociatedTicket {
    int id;
    String title,ticketnumber;

    public ProblemAssociatedTicket(int id, String title, String ticketnumber) {
        this.id = id;
        this.title = title;
        this.ticketnumber = ticketnumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTicketnumber() {
        return ticketnumber;
    }

    public void setTicketnumber(String ticketnumber) {
        this.ticketnumber = ticketnumber;
    }
}
