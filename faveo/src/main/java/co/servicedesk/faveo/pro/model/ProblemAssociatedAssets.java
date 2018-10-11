package co.servicedesk.faveo.pro.model;

public class ProblemAssociatedAssets {
    int id;
    String title,assetnumber;

    public ProblemAssociatedAssets(int id, String title, String assetnumber) {
        this.id = id;
        this.title = title;
        this.assetnumber = assetnumber;
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
        return assetnumber;
    }

    public void setTicketnumber(String ticketnumber) {
        this.assetnumber = assetnumber;
    }
}
