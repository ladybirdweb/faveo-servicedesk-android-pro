package co.servicedesk.faveo.pro.frontend.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.servicedesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.Attachedproblem;
import co.servicedesk.faveo.pro.model.ProblemModel;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class AssociatedProblem extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView imageView;
    int ticketId;
    SpotsDialog dialog1;
    TextView textViewEmptyView;
    ProblemAdpter mAdapter;
    List<Attachedproblem> problemList = new ArrayList<>();
    int problemId;
    ImageView imageViewRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associated_problem);
        Window window = AssociatedProblem.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AssociatedProblem.this,R.color.faveo));
        textViewEmptyView=findViewById(R.id.empty_view);
        recyclerView=findViewById(R.id.list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ticketId= Integer.parseInt(Prefs.getString("TICKETid",null));
        imageView=findViewById(R.id.imageViewBack);
        imageViewRefresh=findViewById(R.id.imageRefresh);
        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetReceiver.isConnected()){
                    dialog1 = new SpotsDialog(AssociatedProblem.this, getString(R.string.pleasewait));
                    dialog1.show();
                    new FetchAttachedProblem(ticketId).execute();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (InternetReceiver.isConnected()){
            dialog1 = new SpotsDialog(AssociatedProblem.this, getString(R.string.pleasewait));
            dialog1.show();
            new FetchAttachedProblem(ticketId).execute();
        }
        }



    private class FetchAttachedProblem extends AsyncTask<String,Void,String>{

        int ticketId;

        public FetchAttachedProblem(int ticketId){
            this.ticketId=ticketId;
        }


        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().attachedProblem(ticketId);
        }

        @Override
        protected void onPostExecute(String s) {
            dialog1.dismiss();
            problemList.clear();
            if (s.equals("")||s.equals(null)){
                Toasty.error(AssociatedProblem.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            try{
                textViewEmptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                JSONObject jsonObject=new JSONObject(s);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                //String data=jsonObject.getString("data");
//                if (data.equals(null)||data.equals("null")){
//                textViewEmptyView.setVisibility(View.VISIBLE);
//                }
                    problemId=jsonObject1.getInt("id");
                    String subject=jsonObject1.getString("subject");
                    String from=jsonObject1.getString("from");
                    JSONObject jsonObject2=jsonObject1.getJSONObject("impact_id");
                    String impact=jsonObject2.getString("name");
                    Attachedproblem attachedproblem=new Attachedproblem(problemId,subject,from,impact);
                    problemList.add(attachedproblem);
                    Log.d("subject",subject);


                recyclerView.setHasFixedSize(false);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AssociatedProblem.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                mAdapter = new ProblemAdpter(AssociatedProblem.this,problemList);
                recyclerView.setAdapter(mAdapter);
                //recyclerView.getAdapter().notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();

            }catch (JSONException e){
                textViewEmptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                e.printStackTrace();
            }


        }
    }

    private class DetachProblem extends AsyncTask<String,Void,String>{
        int ticketId,problemId;

        public DetachProblem(int ticketId,int problemId){
            this.ticketId=ticketId;
            this.problemId=problemId;
        }
        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().detachProblem(ticketId,problemId);
        }

        @Override
        protected void onPostExecute(String s) {

            if (dialog1 != null && dialog1.isShowing()) {
                dialog1.dismiss();
            }

            if (s.equals("")||s.equals(null)){
                Toasty.error(AssociatedProblem.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            try{
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Detached Successfully")){
                    Toasty.success(AssociatedProblem.this,"successfully detached the problem from the ticket",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AssociatedProblem.this,AssociatedProblem.class);
                    startActivity(intent);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

    public class ProblemAdpter extends RecyclerView.Adapter<AssociatedProblem.ProblemAdpter.MyViewHolder> {
        private List<Attachedproblem> moviesList;
        Context context;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject,impact;
            RelativeLayout relativeLayout;
            ImageButton imageButton;
            public MyViewHolder(View view) {
                super(view);
                email = (TextView) view.findViewById(R.id.textView_client_email);
                subject= (TextView) view.findViewById(R.id.collaboratorname);
                impact=view.findViewById(R.id.impact);
                relativeLayout=view.findViewById(R.id.problemList);
                imageButton=view.findViewById(R.id.detach);
            }
        }

        public ProblemAdpter(Context context,List<Attachedproblem> moviesList) {
            this.moviesList = moviesList;
            this.context=context;
        }

        @Override
        public AssociatedProblem.ProblemAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listattachedproblem, parent, false);
            return new ProblemAdpter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AssociatedProblem.ProblemAdpter.MyViewHolder holder, int position) {
            final Attachedproblem movie = moviesList.get(position);

            if (!movie.getFrom().equals("")||!movie.getFrom().equals(null)){
                holder.email.setText("From :"+movie.getFrom());
            }

            if (!movie.getImpact().equals("")||!movie.getImpact().equals(null)){
                holder.impact.setText("Impact :"+movie.getImpact());
            }

            if (!movie.getSubject().equals(null)||!movie.getSubject().equals("")){
                holder.subject.setText(movie.getSubject());
            }


            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BottomDialog.Builder(AssociatedProblem.this)
                            .setContent("Are you sure you want to detach the problem?")
                            .setPositiveText("YES")
                            .setNegativeText("NO")
                            .setPositiveBackgroundColorResource(R.color.white)
                            //.setPositiveBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)
                            .setPositiveTextColorResource(R.color.faveo)
                            .setNegativeTextColor(R.color.black)
                            //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                            .onPositive(new BottomDialog.ButtonCallback() {
                                @Override
                                public void onClick(BottomDialog dialog) {
                                    if (InternetReceiver.isConnected()){
                                        if (InternetReceiver.isConnected()){
                                            dialog1= new SpotsDialog(AssociatedProblem.this, "Detaching Problem..");
                                            dialog1.show();
                                            new DetachProblem(ticketId,problemId).execute();

                                        }
                                    }
                                }
                            }).onNegative(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog bottomDialog) {
                            bottomDialog.dismiss();
                        }
                    })
                            .show();
                }
            });

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(AssociatedProblem.this,ProblemViewPage.class);
                    intent.putExtra("problemId",movie.getId());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
