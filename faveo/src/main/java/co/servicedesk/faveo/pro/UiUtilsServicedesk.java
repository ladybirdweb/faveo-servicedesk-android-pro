package co.servicedesk.faveo.pro;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import co.servicedesk.faveo.pro.frontend.adapters.DrawerItemCustomAdapter;
import co.servicedesk.faveo.pro.frontend.adapters.ProblemListAdapter;

public class UiUtilsServicedesk {
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ProblemListAdapter listAdapter = (ProblemListAdapter) listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}
