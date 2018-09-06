package co.servicedesk.faveo.pro.frontend.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.servicedesk.faveo.pro.CircleTransform;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.AttachedCollaborator;
import co.servicedesk.faveo.pro.model.ProblemModel;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ExistingProblems extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ProblemModel> problemList = new ArrayList<>();
    ProblemAdpter mAdapter;
    ImageView imageView;
    SpotsDialog dialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_problems);
        Window window = ExistingProblems.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ExistingProblems.this,R.color.faveo));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.list);
    imageView=findViewById(R.id.imageViewBack);
        if (InternetReceiver.isConnected()){
            dialog1 = new SpotsDialog(ExistingProblems.this, getString(R.string.pleasewait));
            dialog1.show();
            new FetchExistingProblem().execute();
        }




        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusearch, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private class FetchExistingProblem extends AsyncTask<String, Void, String> {


        FetchExistingProblem() {

        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getExisitngProblem();
        }

        protected void onPostExecute(String result) {
            dialog1.dismiss();
            problemList.clear();
            int noOfCollaborator=0;
            if (isCancelled()) return;
            //strings.clear();

//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

            if (result == null) {
                Toasty.error(ExistingProblems.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                Data data=new Data(0,"No recipients");
//                stringArrayList.add(data);
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                        JSONObject jsonObject1=jsonObject.getJSONObject("data");
                        JSONArray jsonArray=jsonObject1.getJSONArray("problems");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject2=jsonArray.getJSONObject(i);
                            int id=jsonObject2.getInt("id");
                            String subject=jsonObject2.getString("subject");
                            String email=jsonObject2.getString("from");
                            ProblemModel problemModel=new ProblemModel(email,subject,id);
                            problemList.add(problemModel);
                        }





                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new ProblemAdpter(ExistingProblems.this,problemList);
                    runLayoutAnimation(recyclerView);
                    recyclerView.setAdapter(mAdapter);
                    //recyclerView.getAdapter().notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e1) {
                e1.printStackTrace();
            }


        }
    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_right);

        recyclerView.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    public class ProblemAdpter extends RecyclerView.Adapter<ProblemAdpter.MyViewHolder> implements Filterable{
        private List<ProblemModel> moviesList;
        Context context;
        private List<ProblemModel> mFilteredList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject,options;

            public MyViewHolder(View view) {
                super(view);
                email = (TextView) view.findViewById(R.id.textView_client_email);
                //relativeLayout= (RelativeLayout) view.findViewById(R.id.attachedCollaborator);
                subject= (TextView) view.findViewById(R.id.collaboratorname);
                options=view.findViewById(R.id.textViewOptions);



            }
        }

        public ProblemAdpter(Context context,List<ProblemModel> moviesList) {
            this.moviesList = moviesList;
            this.context=context;
            this.mFilteredList= moviesList;
        }
        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();

                    if (charString.isEmpty()) {

                        moviesList = mFilteredList;
                    } else {

                        List<ProblemModel> filteredList = new ArrayList<>();

                        for (ProblemModel androidVersion : mFilteredList) {

                            if (androidVersion.getSubject().toLowerCase().contains(charString) || androidVersion.getEmail().toLowerCase().contains(charString)) {

                                filteredList.add(androidVersion);
                            }
                        }

                        moviesList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = moviesList;
                    return filterResults;
                }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            moviesList = (ArrayList<ProblemModel>) filterResults.values;
            notifyDataSetChanged();
        }
            };
        }
        @Override
        public ProblemAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listofproblems, parent, false);
            return new ProblemAdpter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProblemAdpter.MyViewHolder holder, int position) {
            final ProblemModel movie = moviesList.get(position);


            if (!movie.getEmail().equals("")) {
                holder.email.setText("From :" +movie.getEmail());
            }

            if (movie.getSubject().equals("")){
                holder.subject.setVisibility(View.GONE);
            }
            else{
                holder.subject.setVisibility(View.VISIBLE);
                holder.subject.setText(movie.getSubject());
            }

            holder.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, holder.options);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.options_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    //handle menu1 click
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
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
