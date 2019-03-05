package co.servicedesk.faveo.pro.frontend.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.model.DataModel;
import co.servicedesk.faveo.pro.model.ServicedeskModule;

public class ProblemListAdapter extends ArrayAdapter<ServicedeskModule> {
    Context mContext;
    int layoutResourceId;
    ServicedeskModule data[] = null;

    public ProblemListAdapter(Context mContext, int layoutResourceId, ServicedeskModule[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageView2);
        TextView textViewName = (TextView) listItem.findViewById(R.id.inboxtv);

        ServicedeskModule folder = data[position];


        imageViewIcon.setImageResource(folder.getIcon());
        textViewName.setText(folder.getName());

        return listItem;
    }

}
