package co.servicedesk.faveo.pro.frontend.activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import co.servicedesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.fragments.ClientList;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.ProblemModel;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class ExistingProblems extends AppCompatActivity {

    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    List<ProblemModel> problemList = new ArrayList<>();
    ProblemAdpter mAdapter;
    ImageView imageView;
    SpotsDialog dialog1;
    static String nextPageURL = "";
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    Button button;
    public int ticketId;
    int problemId;
    private BottomSheetBehavior mBottomSheetBehavior;
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
        try {
            ticketId = Integer.parseInt(Prefs.getString("TICKETid", null));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        swipeRefresh=findViewById(R.id.swipeRefresh);
        button=findViewById(R.id.createNewProblem);
        MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(ExistingProblems.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ExistingProblems.this, NewProblem.class);
                startActivity(intent1);
            }
        });
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

        swipeRefresh.setColorSchemeResources(R.color.faveo_blue);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetReceiver.isConnected()) {
                    loading = true;

                    try {
                            swipeRefresh.setRefreshing(true);
                            //progressDialog.show();
                            new FetchExistingProblem().execute();

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
//        empty_view.setText(R.string.no_problems_found);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    public class AttachProblem extends AsyncTask<String,Void,String>{

        int ticketId,problemId;

        public AttachProblem(int ticketId,int problemId){
            this.ticketId=ticketId;
            this.problemId=problemId;
        }


        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().associateProblem(ticketId,problemId);
        }

        @Override
        protected void onPostExecute(String s) {
            dialog1.dismiss();
            if (s.equals("")||s.equals(null)){
                Toasty.error(ExistingProblems.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            try{
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Problem attached to this ticket")){
                    Toasty.success(ExistingProblems.this,"Successfully attached to the ticket",Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }


    private class DeleteProblem extends AsyncTask<String,Void,String>{

        int problemId;

        public DeleteProblem(int problemId){
            this.problemId=problemId;
        }



        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().deleteProblem(problemId);
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog1 != null && dialog1.isShowing()) {
                dialog1.dismiss();
            }

            try {
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Problem Deleted Successfully.")){
                    Toasty.success(ExistingProblems.this, getString(R.string.problem_deleted), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ExistingProblems.this,ExistingProblems.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
            swipeRefresh.setRefreshing(false);
            if (isCancelled()) return;

            if (result == null) {
                Toasty.error(ExistingProblems.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                Data data=new Data(0,"No recipients");
//                stringArrayList.add(data);
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                nextPageURL = jsonObject.getString("next_page_url");
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject2=jsonArray.getJSONObject(i);
                            int id=jsonObject2.getInt("id");
                            String subject=jsonObject2.getString("subject");
                            String email=jsonObject2.getString("from");
                            String createdDate=jsonObject2.getString("created_at");
                            ProblemModel problemModel=new ProblemModel(email,subject,createdDate,id);
                            problemList.add(problemModel);
                        }

                        recyclerView.setHasFixedSize(false);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExistingProblems.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                    mAdapter = new ProblemAdpter(ExistingProblems.this,problemList);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (dy > 0) {
                            visibleItemCount = linearLayoutManager.getChildCount();
                            totalItemCount = linearLayoutManager.getItemCount();
                            pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                            if (loading) {
                                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                    loading = false;
                                    new FetchNextPage(ExistingProblems.this).execute();
                                    StyleableToast st = new StyleableToast(ExistingProblems.this, getString(R.string.loading), Toast.LENGTH_SHORT);
                                    st.setBackgroundColor(Color.parseColor("#3da6d7"));
                                    st.setTextColor(Color.WHITE);
                                    st.setIcon(R.drawable.ic_autorenew_black_24dp);
                                    st.spinIcon();
                                    st.setMaxAlpha();
                                    st.show();
                                }
                            }
                        }
                    }
                });
                    recyclerView.setAdapter(mAdapter);
                    //recyclerView.getAdapter().notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e1) {
                e1.printStackTrace();
            }


        }
    }
    public class FetchNextPage extends AsyncTask<String, Void, String> {
        Context context;

        FetchNextPage(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... urls) {
            if (nextPageURL.equals("null")) {
                return "all done";
            }
            String result = new Helpdesk().nextPageURL(nextPageURL);
            if (result == null)
                return null;
            //DatabaseHandler databaseHandler = new DatabaseHandler(context);
            //databaseHandler.recreateTable();
            try {
                JSONObject jsonObject = new JSONObject(result);

                nextPageURL = jsonObject.getString("next_page_url");
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                    int id=jsonObject2.getInt("id");
                    String subject=jsonObject2.getString("subject");
                    String email=jsonObject2.getString("from");
                    String createdDate=jsonObject2.getString("created_at");
                    ProblemModel problemModel=new ProblemModel(email,subject,createdDate,id);
                    problemList.add(problemModel);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // databaseHandler.close();
            return "success";
        }

        protected void onPostExecute(String result) {

            if (result == null) {
                Toast.makeText(ExistingProblems.this, "Something went wrong", Toast.LENGTH_LONG).show();
                return;
            }
            if (result.equals("all done")) {
                Toasty.info(context, getString(R.string.all_problem_loaded), Toast.LENGTH_SHORT).show();
                return;
            }
            mAdapter.notifyDataSetChanged();
            loading = true;
        }
    }




    public class ProblemAdpter extends RecyclerView.Adapter<ProblemAdpter.MyViewHolder> implements Filterable{
        private List<ProblemModel> moviesList;
        Context context;
        private List<ProblemModel> mFilteredList;
        private List<Integer> integers=new ArrayList<>();

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject,options;
            RelativeTimeTextView relativeTimeTextView;
            RelativeLayout relativeLayout;
            public MyViewHolder(View view) {
                super(view);
                email = (TextView) view.findViewById(R.id.textView_client_email);
                //relativeLayout= (RelativeLayout) view.findViewById(R.id.attachedCollaborator);
                subject= (TextView) view.findViewById(R.id.collaboratorname);
                options=view.findViewById(R.id.textViewOptions);
                relativeTimeTextView=view.findViewById(R.id.textView_ticket_time);
                relativeLayout=view.findViewById(R.id.problemList);


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
                holder.email.setText("From :  " +movie.getEmail());
            }

            if (movie.getSubject().equals("")){
                holder.subject.setVisibility(View.GONE);
            }
            else{
                holder.subject.setVisibility(View.VISIBLE);
                holder.subject.setText(movie.getSubject());
            }

            holder.relativeTimeTextView.setReferenceTime(Helper.relativeTime(movie.getCreatedDate()));

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    problemId=movie.getId();
                    MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(ExistingProblems.this);
                    myBottomSheetDialog.show();
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
    public class MyBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener {

        Context context;
        TextView viewproblem,deleteproblem;

        MyBottomSheetDialog(@NonNull Context context) {
            super(context);
            this.context = context;
            create();
        }

        public void create() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
            setContentView(bottomSheetView);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
            BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    // do something
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // do something
                }
            };
            viewproblem = (TextView) bottomSheetView.findViewById(R.id.viewproblem);
            deleteproblem = (TextView) bottomSheetView.findViewById(R.id.deleteproblem);
            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ExistingProblems.this,ProblemViewPage.class);
                                    intent.putExtra("problemId",problemId);
                                    startActivity(intent);
                }
            });
            deleteproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                                    new BottomDialog.Builder(ExistingProblems.this)
                                            .setContent("Deleting Problem?")
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
                                                            dialog1= new SpotsDialog(ExistingProblems.this,"Deleting Problem...");
                                                            dialog1.show();
                                                            new DeleteProblem(problemId).execute();

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

        }

        @Override
        public void onClick(View view) {


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

