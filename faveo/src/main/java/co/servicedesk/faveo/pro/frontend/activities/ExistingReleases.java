package co.servicedesk.faveo.pro.frontend.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

import co.servicedesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.ProblemModel;
import co.servicedesk.faveo.pro.model.ReleaseModel;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ExistingReleases extends AppCompatActivity {

    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    List<ReleaseModel> problemList = new ArrayList<>();
    ProblemAdpter mAdapter;
    ImageView imageView;
    SpotsDialog dialog1;
    static String nextPageURL = "";
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    Button button;
    public int ticketId;
    int releaseId;
    String problemTitle;
    int total;
    TextView noInternetView,emptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_releases);
        Window window = ExistingReleases.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ExistingReleases.this,R.color.mainActivityTopBar));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            ticketId = Integer.parseInt(Prefs.getString("TICKETid", null));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        swipeRefresh=findViewById(R.id.swipeRefresh);
        button=findViewById(R.id.createNewRelease);

        noInternetView=findViewById(R.id.noiternet_view);
        emptyView=findViewById(R.id.empty_view);
        MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(ExistingReleases.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ExistingReleases.this,NewRelease.class);
                startActivity(intent);
            }
        });
        recyclerView=findViewById(R.id.listExistingRelease);
        imageView=findViewById(R.id.imageViewBack);
        if (InternetReceiver.isConnected()){
            dialog1 = new SpotsDialog(ExistingReleases.this, getString(R.string.pleasewait));
            dialog1.show();
            new FetchExistingReleases().execute();
        }
        else{
            recyclerView.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            noInternetView.setVisibility(View.VISIBLE);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ExistingReleases.this,MainActivity.class);
                startActivity(intent);
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
                        new FetchExistingReleases().execute();

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    swipeRefresh.setRefreshing(false);
                    noInternetView.setVisibility(View.VISIBLE);
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


    private class DeleteRelease extends AsyncTask<String,Void,String> {

        int releaseId;

        public DeleteRelease(int releaseId){
            this.releaseId=releaseId;
        }



        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().deleteRelease(releaseId);
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog1 != null && dialog1.isShowing()) {
                dialog1.dismiss();
            }

            try {
                JSONObject jsonObject=new JSONObject(s);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String data=jsonObject1.getString("success");
                if (data.equals("Release Deleted Successfully.")){
                    Toasty.success(ExistingReleases.this, getString(R.string.release_deleted), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ExistingReleases.this,ExistingReleases.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class FetchExistingReleases extends AsyncTask<String, Void, String> {


        FetchExistingReleases() {
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getExistingRelease();
        }

        protected void onPostExecute(String result) {
            if (dialog1 != null && dialog1.isShowing()) {
                dialog1.dismiss();
            }
            problemList.clear();
            swipeRefresh.setRefreshing(false);
            if (isCancelled()) return;

            if (result == null) {
                Toasty.error(ExistingReleases.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                Data data=new Data(0,"No recipients");
//                stringArrayList.add(data);
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                nextPageURL = jsonObject.getString("next_page_url");
                total=jsonObject.getInt("total");
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                    int id=jsonObject2.getInt("id");
                    String subject=jsonObject2.getString("subject");
                    String createdDate=jsonObject2.getString("updated_at");
                    String priority=jsonObject2.getString("priority");
                    ReleaseModel releaseModel=new ReleaseModel(subject,priority,createdDate,id);
                    problemList.add(releaseModel);
                }

                recyclerView.setHasFixedSize(false);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExistingReleases.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
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
                                    new FetchNextPage(ExistingReleases.this).execute();
                                    StyleableToast st = new StyleableToast(ExistingReleases.this, getString(R.string.loading), Toast.LENGTH_SHORT);
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
                mAdapter = new ProblemAdpter(ExistingReleases.this,problemList);
                runLayoutAnimation(recyclerView);
                recyclerView.setAdapter(mAdapter);
                if (mAdapter.getItemCount()==0){
                    emptyView.setVisibility(View.VISIBLE);
                } else emptyView.setVisibility(View.GONE);
                //recyclerView.getAdapter().notifyDataSetChanged();
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
                    String createdDate=jsonObject2.getString("updated_at");
                    String priority=jsonObject2.getString("priority");
                    ReleaseModel problemModel=new ReleaseModel(subject,priority,createdDate,id);
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
                Toast.makeText(ExistingReleases.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }



    public class ProblemAdpter extends RecyclerView.Adapter<ProblemAdpter.MyViewHolder>{
        private List<ReleaseModel> moviesList;
        Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView subject;
            ImageView options;
            RelativeTimeTextView relativeTimeTextView;
            RelativeLayout relativeLayout;
            TextView textViewPriority;
            TextView textViewId;
            public MyViewHolder(View view) {
                super(view);
                subject=view.findViewById(R.id.collaboratorname);
                options=view.findViewById(R.id.textViewOptions);
                relativeTimeTextView=view.findViewById(R.id.textView_ticket_time);
                relativeLayout=view.findViewById(R.id.problemList);
                textViewId=view.findViewById(R.id.problemId);
                textViewPriority=view.findViewById(R.id.priority);

            }
        }

        ProblemAdpter(Context context,List<ReleaseModel> moviesList) {
            this.moviesList = moviesList;
            this.context=context;

        }
        @Override
        public ProblemAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listofreleases, parent, false);
            return new ProblemAdpter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProblemAdpter.MyViewHolder holder, int position) {
            final ReleaseModel movie = moviesList.get(position);
            holder.options.setColorFilter(getColor(R.color.faveo));
            holder.options.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_24dp));
            holder.textViewId.setText("#REL-"+movie.getId());
            if (movie.getSubject().equals("")){
                holder.subject.setVisibility(View.GONE);
            }
            else{
                holder.subject.setVisibility(View.VISIBLE);
                holder.subject.setText(movie.getSubject());
            }

            holder.relativeTimeTextView.setReferenceTime(Helper.relativeTime(movie.getCreatedDate()));

            if (!movie.getPriority().equals("")){
                holder.textViewPriority.setText(movie.getPriority());
            }

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    releaseId=movie.getId();
                    problemTitle=movie.getSubject();
                    MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(ExistingReleases.this);
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
        Intent intent=new Intent(ExistingReleases.this,MainActivity.class);
        startActivity(intent);
    }
    public class MyBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener {

        Context context;
        TextView viewproblem,deleteproblem,editProblem;

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
            editProblem=bottomSheetView.findViewById(R.id.textview_more);
            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            deleteproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BottomDialog.Builder(ExistingReleases.this)
                            .setTitle(R.string.delete_release)
                            .setContent(R.string.release_confirmation)
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
                                            dialog1= new SpotsDialog(ExistingReleases.this,getString(R.string.delete_release));
                                            dialog1.show();
                                            new DeleteRelease(releaseId).execute();

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
            editProblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1=new Intent(ExistingReleases.this,EditAndViewRelease.class);
                    intent1.putExtra("releaseId",releaseId);
                    startActivity(intent1);
                }
            });

            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ExistingReleases.this,ReleaseViewPage.class);
                    intent.putExtra("releaseId",releaseId);
                    Prefs.putInt("releaseId",releaseId);
                    startActivity(intent);

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

