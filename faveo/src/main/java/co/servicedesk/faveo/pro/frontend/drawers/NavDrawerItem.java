package co.servicedesk.faveo.pro.frontend.drawers;

/**
 * Created by Sumit
 * This class is for creating the object for the NavDrawer item.
 */
class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private String count="";

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

