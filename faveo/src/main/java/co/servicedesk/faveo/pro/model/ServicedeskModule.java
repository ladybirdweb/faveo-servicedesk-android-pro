package co.servicedesk.faveo.pro.model;

public class ServicedeskModule {
    public String name;
    public int icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public ServicedeskModule(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }
}
