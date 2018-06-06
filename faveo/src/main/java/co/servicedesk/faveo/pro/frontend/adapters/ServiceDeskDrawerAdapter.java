package co.servicedesk.faveo.pro.frontend.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.model.ServiceDeskDrawer;

public class ServiceDeskDrawerAdapter extends ArrayAdapter<ServiceDeskDrawer>{

    Context mContext;
    int layoutResourceId;
    ServiceDeskDrawer data[] = null;

    public ServiceDeskDrawerAdapter(Context mContext, int layoutResourceId, ServiceDeskDrawer[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageView2);
        TextView textViewName = (TextView) listItem.findViewById(R.id.inboxtv);

        ServiceDeskDrawer folder = data[position];


        imageViewIcon.setImageResource(folder.getIcon());
        textViewName.setText(folder.getName());


        return listItem;
    }

}
