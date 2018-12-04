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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import co.servicedesk.faveo.pro.model.ChangeModel;
import co.servicedesk.faveo.pro.model.ProblemModel;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

/**
 * This class is written by Sayar, it is responsible for getting all the list of existing changes in Faveo.
 * We can go to either change view page or can edit or can delete the change from Faveo.We will get a option to create new change in Faveo.
 */
public class ExistingChanges extends AppCompatActivity {
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    List<ChangeModel> problemList = new ArrayList<>();
    ProblemAdpter mAdapter;
    ImageView imageView;
    SpotsDialog dialog1;
    static String nextPageURL = "";
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    Button button;
    public int ticketId;
    int changeId;
    String changeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_changes);
        Window window = ExistingChanges.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ExistingChanges.this, R.color.faveo));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        button = findViewById(R.id.createNewChange);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ExistingChanges.this, NewProblem.class);
                startActivity(intent1);
            }
        });
        recyclerView = findViewById(R.id.listExistingProblem);
        imageView = findViewById(R.id.imageViewBackChange);
        if (InternetReceiver.isConnected()) {
            dialog1 = new SpotsDialog(ExistingChanges.this, getString(R.string.pleasewait));
            dialog1.show();
            new FetchExistingChanges().execute();
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
                        new FetchExistingChanges().execute();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                    swipeRefresh.setRefreshing(false);
                }
            }
        });


    }

    class FetchExistingChanges extends AsyncTask<String, Void, String> {


        FetchExistingChanges() {
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getExistingChanges();
        }

        protected void onPostExecute(String result) {
            if (dialog1 != null && dialog1.isShowing()) {
                dialog1.dismiss();
            }
            problemList.clear();
            swipeRefresh.setRefreshing(false);
            if (isCancelled()) return;

            if (result == null) {
                Toasty.error(ExistingChanges.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                Data data=new Data(0,"No recipients");
//                stringArrayList.add(data);
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                nextPageURL = jsonObject.getString("next_page_url");
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    int id = jsonObject2.getInt("id");
                    String subject = jsonObject2.getString("subject");
                    String createdDate = jsonObject2.getString("created_at");
                    ChangeModel problemModel = new ChangeModel(subject, createdDate, id);
                    problemList.add(problemModel);
                }
                recyclerView.setHasFixedSize(false);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExistingChanges.this);
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
                                    new FetchNextPage(ExistingChanges.this).execute();
                                    StyleableToast st = new StyleableToast(ExistingChanges.this, getString(R.string.loading), Toast.LENGTH_SHORT);
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
                mAdapter = new ProblemAdpter(ExistingChanges.this, problemList);
                recyclerView.setAdapter(mAdapter);
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
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    int id = jsonObject2.getInt("id");
                    String subject = jsonObject2.getString("subject");
                    String email = jsonObject2.getString("from");
                    String createdDate = jsonObject2.getString("created_at");
                    ChangeModel changeModel = new ChangeModel(subject, createdDate, id);
                    problemList.add(changeModel);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // databaseHandler.close();
            return "success";
        }

        protected void onPostExecute(String result) {

            if (result == null) {
                Toast.makeText(ExistingChanges.this, "Something went wrong", Toast.LENGTH_LONG).show();
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


    public class ProblemAdpter extends RecyclerView.Adapter<ProblemAdpter.MyViewHolder> {
        private List<ChangeModel> moviesList;
        Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject;
            ImageView options;
            RelativeTimeTextView relativeTimeTextView;
            RelativeLayout relativeLayout;

            public MyViewHolder(View view) {
                super(view);
                email = view.findViewById(R.id.textView_client_email);
                subject = view.findViewById(R.id.collaboratorname);
                options = view.findViewById(R.id.textViewOptions);
                relativeTimeTextView = view.findViewById(R.id.textView_ticket_time);
                relativeLayout = view.findViewById(R.id.problemList);


            }
        }

        ProblemAdpter(Context context, List<ChangeModel> moviesList) {
            this.moviesList = moviesList;
            this.context = context;

        }

        @Override
        public ProblemAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_of_changes, parent, false);
            return new ProblemAdpter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProblemAdpter.MyViewHolder holder, int position) {
            final ChangeModel changeModel = moviesList.get(position);
//             holder.options.setColorFilter(getColor(R.color.faveo));
            holder.options.setImageDrawable(getDrawable(R.drawable.menudot));

            if (changeModel.getSubject().equals("")) {
                holder.subject.setVisibility(View.GONE);
            } else {
                holder.subject.setVisibility(View.VISIBLE);
                holder.subject.setText(changeModel.getSubject());
            }

            holder.relativeTimeTextView.setReferenceTime(Helper.relativeTime(changeModel.getCreatedDate()));

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeId = changeModel.getId();
                    changeTitle = changeModel.getSubject();
                    MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(ExistingChanges.this);
                    myBottomSheetDialog.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

    }

    public class MyBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener {

        Context context;
        TextView viewproblem, deleteproblem, editProblem;

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
            editProblem = bottomSheetView.findViewById(R.id.textview_more);
            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ExistingChanges.this, ChangeViewPage.class);
                    intent.putExtra("changeId", changeId);
                    intent.putExtra("changeTitle", changeTitle);
                    startActivity(intent);
                }
            });
            deleteproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BottomDialog.Builder(ExistingChanges.this)
                            .setContent("Deleting Change?")
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
                                    if (InternetReceiver.isConnected()) {
                                        if (InternetReceiver.isConnected()) {
                                            dialog1 = new SpotsDialog(ExistingChanges.this, "Deleting changes...");
                                            dialog1.show();
                                            new DeleteChange(changeId).execute();

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
                    Intent intent1 = new Intent(ExistingChanges.this, EditAndViewChange.class);
                    intent1.putExtra("changeId", changeId);
                    startActivity(intent1);
                }
            });


        }

        @Override
        public void onClick(View view) {


        }
    }

    private class DeleteChange extends AsyncTask<String, Void, String> {

        int changeId;

        public DeleteChange(int changeId) {
            this.changeId = changeId;
        }


        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().deleteChange(changeId);
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog1 != null && dialog1.isShowing()) {
                dialog1.dismiss();
            }

            try {
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Changes Deleted.")){
                    Toasty.success(ExistingChanges.this, getString(R.string.change_delete), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ExistingChanges.this,ExistingChanges.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
