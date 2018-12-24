package co.servicedesk.faveo.pro.frontend.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.servicedesk.faveo.pro.CircleTransform;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.frontend.activities.ClientDetailActivity;
import co.servicedesk.faveo.pro.model.ClientOverview;

/**
 * This adapter is for the recycler view which we have used
 * in client details page.
 */
public class ClientOverviewAdapter extends RecyclerView.Adapter<ClientOverviewAdapter.ClientViewHolder> {
    private List<ClientOverview> clientOverviewList;
    Context context;
    private static ClientOverview clientOverview;
    public ClientOverviewAdapter(Context context,List<ClientOverview> clientOverviewList) {
        this.clientOverviewList = clientOverviewList;
        this.context=context;
    }

    @Override
    public int getItemCount() {
        return clientOverviewList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(final ClientViewHolder clientViewHolder, int i) {
        try {
             clientOverview= clientOverviewList.get(i);

            String letter = String.valueOf(clientOverview.clientName.charAt(0)).toUpperCase();
            Log.d("idClient",String.valueOf(clientOverview.clientID));
            //clientViewHolder.textViewClientID.setText(clientOverview.clientID + "");
            clientViewHolder.textViewClientName.setText(clientOverview.clientName);
            clientViewHolder.textViewClientEmail.setText(clientOverview.clientEmail);
            if (clientOverview.clientPhone.equals("") || clientOverview.clientPhone.equals("null")||clientOverview.clientPhone.equals("Not available")||clientOverview.clientPhone.equals(" "))
                clientViewHolder.textViewClientPhone.setVisibility(View.GONE);
            else {
                clientViewHolder.textViewClientPhone.setVisibility(View.VISIBLE);
                clientViewHolder.textViewClientPhone.setText(clientOverview.clientPhone);
            }
            if (clientOverview.clientPicture.equals("")){
                clientViewHolder.roundedImageViewProfilePic.setVisibility(View.GONE);

            }
            else if (clientOverview.clientPicture.contains(".jpg")||clientOverview.clientPicture.contains(".png")||clientOverview.clientPicture.contains(".jpeg")){
                Picasso.with(context).load(clientOverview.getClientPicture()).transform(new CircleTransform()).into(clientViewHolder.roundedImageViewProfilePic);

            }
            else{
                ColorGenerator generator = ColorGenerator.MATERIAL;
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(letter,generator.getRandomColor());
                clientViewHolder.roundedImageViewProfilePic.setAlpha(0.6f);
                clientViewHolder.roundedImageViewProfilePic.setImageDrawable(drawable);
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        clientViewHolder.client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=clientViewHolder.getAdapterPosition();
                clientOverview= clientOverviewList.get(pos);
                Intent intent = new Intent(view.getContext(), ClientDetailActivity.class);
                intent.putExtra("CLIENT_ID", clientOverview.clientID + "");
                Log.d("clientid",clientOverview.clientID + "");
                Prefs.putString("clientId",clientOverview.clientID+"");
                Log.d("cameHere","TRUE");
                intent.putExtra("CLIENT_NAME", clientOverview.clientName);
                intent.putExtra("CLIENT_EMAIL", clientOverview.clientEmail);
                intent.putExtra("CLIENT_PHONE", clientOverview.clientPhone);
                intent.putExtra("CLIENT_PICTURE", clientOverview.clientPicture);
                intent.putExtra("CLIENT_COMPANY", clientOverview.clientCompany);
                intent.putExtra("CLIENT_ACTIVE", clientOverview.clientActive);
                view.getContext().startActivity(intent);
            }
        });

        }

    @Override
    public ClientViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_client, viewGroup, false);
        return new ClientViewHolder(itemView);
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        protected View client;
        ImageView roundedImageViewProfilePic;
        TextView textViewClientName;
        TextView textViewClientEmail;
        TextView textViewClientPhone;

        ClientViewHolder(View v) {
            super(v);
            client = v.findViewById(R.id.client);
            roundedImageViewProfilePic = (ImageView) v.findViewById(R.id.imageView_default_profile);
            textViewClientName = (TextView) v.findViewById(R.id.textView_client_name);
            textViewClientEmail = (TextView) v.findViewById(R.id.textView_client_email);
            textViewClientPhone = (TextView) v.findViewById(R.id.textView_client_phone);
        }

    }

}
