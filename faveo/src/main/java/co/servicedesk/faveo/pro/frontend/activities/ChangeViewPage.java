package co.servicedesk.faveo.pro.frontend.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import co.servicedesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.fragments.change.ChangeDescription;
import co.servicedesk.faveo.pro.frontend.fragments.change.ChangeSpecific;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.AttachedRelease;
import co.servicedesk.faveo.pro.model.Attachedchange;
import co.servicedesk.faveo.pro.model.ProblemModel;
import co.servicedesk.faveo.pro.model.ReleaseModel;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ChangeViewPage extends AppCompatActivity implements ChangeDescription.OnFragmentInteractionListener,ChangeSpecific.OnFragmentInteractionListener {
    Context context;
    ViewPager vpPager;
    TabLayout tabLayout;
    TextView textViewTicketTitle,textViewChangeID;
    TextView loaderTextViewRequester,loaderTextViewstatus;
    LoaderTextView loaderTextViewPriority;
    int changeId;
    String changeTitle;
    SpotsDialog dialog1;
    ImageView imageViewEdit,imageViewBack;
    AHBottomNavigation bottomNavigation;
    String table="sd_changes";
    String parameterName="";
    String identifier="";
    ProgressDialog progressDialog;
    List<ReleaseModel> problemList = new ArrayList<>();
    static String nextPageURL = "";
    ProblemAdpter problemAdpter;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    int releaseAvailableOrNot=2;
    ProblemAdpterAttached problemAdpterAttached;
    List<AttachedRelease> problemListAttached = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_view_page);
        Window window = ChangeViewPage.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ChangeViewPage.this, R.color.mainActivityTopBar));
        context = this;
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.white)
        );


        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setupWithViewPager(vpPager);
        setupViewPager(vpPager);
        textViewTicketTitle=findViewById(R.id.title);
        imageViewBack=findViewById(R.id.imageViewBackTicketDetail);
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbarProblem);
        loaderTextViewRequester=findViewById(R.id.agentassigned);
        loaderTextViewstatus=mAppBarLayout.findViewById(R.id.status);
        textViewChangeID=findViewById(R.id.subject);
        imageViewEdit=findViewById(R.id.editChange);
        loaderTextViewPriority=findViewById(R.id.department);
        final Intent intent = getIntent();
        changeId= intent.getIntExtra("changeId",0);
        Log.d("changeId",""+changeId);
        changeTitle=intent.getStringExtra("changeTitle");
        textViewChangeID.setText("#CHN-"+changeId);

        if (InternetReceiver.isConnected()){
            new AttachedReleaseFetch(changeId).execute();
        }

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Update", R.drawable.ic_update_black_24dp, R.color.white);
        AHBottomNavigationItem item2=new AHBottomNavigationItem("Release",R.drawable.ic_new_releases_black_24dp,R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Delete", R.drawable.ic_delete_black_24dp, R.color.white);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.white));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.white));

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.toolbarColor));
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ChangeViewPage.this,EditAndViewChange.class);
                intent1.putExtra("changeId",changeId);
                startActivity(intent1);
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if (InternetReceiver.isConnected()){
            dialog1 = new SpotsDialog(ChangeViewPage.this, getString(R.string.pleasewait));
            dialog1.show();
            new FetchChangeDetail(changeId).execute();
        }






        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (position == 0) {
                    MyBottomSheetDialogUpdate myBottomSheetDialog = new MyBottomSheetDialogUpdate(ChangeViewPage.this);
                    myBottomSheetDialog.show();
                }
                if (position == 1) {
                    if (releaseAvailableOrNot==0){
                        MyBottomSheetDialogRelease myBottomSheetDialog = new MyBottomSheetDialogRelease(ChangeViewPage.this);
                        myBottomSheetDialog.show();
                    }
                    else{
                        MyBottomSheetDialogShowingAttachedRelease myBottomSheetDialogShowingAttachedRelease=new MyBottomSheetDialogShowingAttachedRelease(ChangeViewPage.this);
                        myBottomSheetDialogShowingAttachedRelease.show();
                    }

                }
                if (position==2){
                    new BottomDialog.Builder(ChangeViewPage.this)
                            .setTitle("Deleting change")
                            .setContent("Are you sure you want to delete this change?")
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
                                            dialog1= new SpotsDialog(ChangeViewPage.this,"Deleting change");
                                            dialog1.show();
                                            new DeleteProblem(changeId).execute();

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

                return true;
            }
        });



    }
    //API to check if there is any existing release is attached or not
    public class AttachedReleaseFetch extends AsyncTask<String,Void,String>{
        int changeId;

        public AttachedReleaseFetch(int changeId){
            this.changeId=changeId;
        }

        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().attachedRelease(changeId);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject=new JSONObject(s);
                String message=jsonObject.getString("message");
                if (message.equals("error")){
                    //changeAvailableOrNot=0;
                }
            } catch (JSONException e) {
                releaseAvailableOrNot=0;
                bottomNavigation.setNotification(0+"",1);
                e.printStackTrace();
            }


            try{
                JSONObject jsonObject=new JSONObject(s);
                Log.d("jsonObject",jsonObject.toString());
                releaseAvailableOrNot=1;
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                JSONObject jsonObject1=jsonArray.getJSONObject(0);
                int id=jsonObject1.getInt("id");
                String subject=jsonObject1.getString("subject");
                Log.d("id&subject",id+" "+subject);
                JSONArray releaseType=jsonObject1.getJSONArray("releaseType");
                JSONObject jsonObject2=releaseType.getJSONObject(0);
                String typeName=jsonObject2.getString("name");

                JSONArray priority=jsonObject1.getJSONArray("priority");
                JSONObject jsonObject3=priority.getJSONObject(0);
                String priorityName=jsonObject3.getString("name");
                AttachedRelease attachedRelease=new AttachedRelease(id,subject,typeName,priorityName);
                problemListAttached.add(attachedRelease);
                bottomNavigation.setNotification(1+"",1);

            }catch (JSONException e){
                releaseAvailableOrNot=0;
                bottomNavigation.setNotification(0+"",1);
                e.printStackTrace();
            }
        }
    }


    //detaching release from the change

    private class DetachChange extends AsyncTask<String,Void,String>{
        int changeId;

        public DetachChange(int changeId){
            this.changeId=changeId;
        }
        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().detachRelease(changeId);
        }

        @Override
        protected void onPostExecute(String s) {

            if (dialog1 != null && dialog1.isShowing()) {
                dialog1.dismiss();
            }

            if (s.equals("")||s.equals(null)){
                Toasty.error(ChangeViewPage.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            try{
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Release Detached Successfully")){
                    Toasty.success(ChangeViewPage.this,getString(R.string.release_detached),Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
//                    Intent intent=new Intent(TicketDetailActivity.this,MainActivity.class);
//                    startActivity(intent);

                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }





    private class DeleteProblem extends AsyncTask<String,Void,String>{

        int changeId;

        public DeleteProblem(int changeId){
            this.changeId=changeId;
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
                    Toasty.success(ChangeViewPage.this, "Changes Deleted.", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ChangeViewPage.this,ExistingChanges.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public class MyBottomSheetDialogShowingAttachedRelease extends BottomSheetDialog {

        Context context;
        TextView textViewHeading;
        MyBottomSheetDialogShowingAttachedRelease(@NonNull Context context) {
            super(context);
            this.context = context;
            createChange();
        }

        public void createChange() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_attached_problem, null);
            setContentView(bottomSheetView);
            textViewHeading=bottomSheetView.findViewById(R.id.heading);
            textViewHeading.setText("Associated release :");
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
            RecyclerView recyclerViewAttachedProblem;
            recyclerViewAttachedProblem = (RecyclerView) bottomSheetView.findViewById(R.id.listAttached);
            final LinearLayoutManager linearLayoutManager1= new LinearLayoutManager(ChangeViewPage.this);
            linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewAttachedProblem.setLayoutManager(linearLayoutManager1);
            problemAdpterAttached = new ProblemAdpterAttached(ChangeViewPage.this,problemListAttached);
            recyclerViewAttachedProblem.setAdapter(problemAdpterAttached);
            //recyclerView.getAdapter().notifyDataSetChanged();
            problemAdpterAttached.notifyDataSetChanged();


        }

    }
    public class ProblemAdpterAttached extends RecyclerView.Adapter<ProblemAdpterAttached.MyViewHolder> {
        private List<AttachedRelease> moviesList;
        Context context;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject,impact;
            RelativeLayout relativeLayout;
            ImageView imageButton;
            TextView problemIdView;
            public MyViewHolder(View view) {
                super(view);
                email = (TextView) view.findViewById(R.id.textViewChangeuser);
                subject= (TextView) view.findViewById(R.id.messageChange);
                relativeLayout=view.findViewById(R.id.problemList);
                imageButton=view.findViewById(R.id.textViewOptions);
                problemIdView=view.findViewById(R.id.changeId);
                impact=view.findViewById(R.id.priorityChange);

            }
        }

        public ProblemAdpterAttached(Context context,List<AttachedRelease> moviesList) {
            this.moviesList = moviesList;
            this.context=context;
        }

        @Override
        public ProblemAdpterAttached.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.attached_release, parent, false);
            return new ProblemAdpterAttached.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProblemAdpterAttached.MyViewHolder holder, int position) {
            final AttachedRelease movie = moviesList.get(position);
            holder.imageButton.setImageResource(R.drawable.ic_close_black_24dp);
            holder.problemIdView.setText("#REL-"+movie.getId());

            holder.impact.setText(movie.getReleaseType());

            holder.subject.setText(movie.getSubject());

            holder.email.setText(movie.getPriority());
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BottomDialog.Builder(ChangeViewPage.this)
                            .setTitle("Detaching release")
                            .setContent("Are you sure you want to detach the release?")
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
                                            dialog1= new SpotsDialog(ChangeViewPage.this, "Detaching Change");
                                            dialog1.show();
                                            new DetachChange(changeId).execute();

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
                    Intent intent=new Intent(ChangeViewPage.this,ReleaseViewPage.class);
                    intent.putExtra("releaseId", movie.getId());
                    Prefs.putString("cameFromMain","False");
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
        }

    }
    public class MyBottomSheetDialogUpdate extends BottomSheetDialog {

        Context context;
        TextView reasonChnage, impact, rolloutPlan, backoutPlan;

        MyBottomSheetDialogUpdate(@NonNull Context context) {
            super(context);
            this.context = context;
            createUpdate();
        }

        public void createUpdate() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_update_change, null);
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

            reasonChnage = (TextView) bottomSheetView.findViewById(R.id.reasonForChange);
            impact = (TextView) bottomSheetView.findViewById(R.id.impactChange);
            rolloutPlan = (TextView) bottomSheetView.findViewById(R.id.rolloutPlan);
            backoutPlan = bottomSheetView.findViewById(R.id.backoutPlan);
            reasonChnage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassReasonForChange cdd = new CustomDialogClassReasonForChange(ChangeViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });
            impact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassImpact cdd = new CustomDialogClassImpact(ChangeViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });
            rolloutPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassRolloutPlan cdd = new CustomDialogClassRolloutPlan(ChangeViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });
            backoutPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassBackoutPlan cdd = new CustomDialogClassBackoutPlan(ChangeViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });

        }

    }
    public class MyBottomSheetDialogRelease extends BottomSheetDialog {

        Context context;
        TextView newRelease,existingRelease;

        MyBottomSheetDialogRelease(@NonNull Context context) {
            super(context);
            this.context = context;
            createUpdate();
        }

        public void createUpdate() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_release, null);
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
            newRelease=bottomSheetView.findViewById(R.id.newRelease);
            existingRelease=bottomSheetView.findViewById(R.id.getexistingRelease);

            newRelease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ChangeViewPage.this,NewRelease.class);
                    Prefs.putString("needToAttachRelease","true");
                    Prefs.putInt("changeId",changeId);
                    startActivity(intent);
                }
            });

            existingRelease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyBottomSheetDialogExistingReleases myBottomSheetDialogExistingReleases=new MyBottomSheetDialogExistingReleases(ChangeViewPage.this);
                    myBottomSheetDialogExistingReleases.show();
                }
            });


        }

    }

    public class MyBottomSheetDialogExistingReleases extends BottomSheetDialog {

        Context context;
        TextView textViewTitle;
        MyBottomSheetDialogExistingReleases(@NonNull Context context) {
            super(context);
            this.context = context;
            createTickets();
        }

        public void createTickets() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheetexisting_problem, null);
            setContentView(bottomSheetView);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
            final RecyclerView recyclerView = (RecyclerView) bottomSheetView.findViewById(R.id.recyclerViewExistingProblem);
            textViewTitle=bottomSheetView.findViewById(R.id.title_bottom_sheet);
            textViewTitle.setText(R.string.existing_releases);
            final ProgressBar progressBar=bottomSheetView.findViewById(R.id.progressbarExisting);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            class FetchExistingProblem extends AsyncTask<String, Void, String> {


                FetchExistingProblem() {
                }

                protected String doInBackground(String... urls) {
                    return new Helpdesk().getExistingRelease();
                }

                protected void onPostExecute(String result) {
                    String email;
                    //dialog1.dismiss();
                    problemList.clear();
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    if (isCancelled()) return;

                    if (result == null) {
                        Toasty.error(ChangeViewPage.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
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
                            String createdDate=jsonObject2.getString("updated_at");
                            String priority=jsonObject2.getString("priority");
                            ReleaseModel problemModel=new ReleaseModel(subject,priority,createdDate,id);
                            problemList.add(problemModel);
                        }
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        final LinearLayoutManager linearLayoutManager= new LinearLayoutManager(ChangeViewPage.this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        problemAdpter = new ProblemAdpter(ChangeViewPage.this,problemList);
                        recyclerView.setAdapter(problemAdpter);
                        //recyclerView.getAdapter().notifyDataSetChanged();

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
                                            new FetchNextPage(ChangeViewPage.this).execute();
                                            StyleableToast st = new StyleableToast(ChangeViewPage.this, getString(R.string.loading), Toast.LENGTH_SHORT);
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
                        problemAdpter.notifyDataSetChanged();

                        //recyclerView.setHasFixedSize(false);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }


                }
            }


            if (InternetReceiver.isConnected()){
                new FetchExistingProblem().execute();
            }



        }

    }
    class FetchNextPage extends AsyncTask<String, Void, String> {
        Context context;

        FetchNextPage(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... urls) {
            String email;
            if (nextPageURL.equals("null")) {
                return "all done";
            }
            String result = new Helpdesk().nextPageURL(nextPageURL);
            if (result == null)
                return null;
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
            return "success";
        }

        protected void onPostExecute(String result) {

            if (result == null) {
                Toast.makeText(ChangeViewPage.this, "Something went wrong", Toast.LENGTH_LONG).show();
                return;
            }
            if (result.equals("all done")) {
                Toasty.info(context, getString(R.string.all_problem_loaded), Toast.LENGTH_SHORT).show();
                return;
            }
            problemAdpter.notifyDataSetChanged();
            loading = true;
        }
    }

    //Adapter for existing changes
    public class ProblemAdpter extends RecyclerView.Adapter<ProblemAdpter.MyViewHolder> {
        private List<ReleaseModel> moviesList;
        Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject,changeID;
            ImageView options;
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
                changeID=view.findViewById(R.id.problemId);
            }
        }

        public ProblemAdpter(Context context,List<ReleaseModel> moviesList) {
            this.moviesList = moviesList;
            this.context=context;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listofreleases, parent, false);
            return new ProblemAdpter.MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(final ProblemAdpter.MyViewHolder holder, int position) {
            final ReleaseModel movie = moviesList.get(position);
            holder.options.setColorFilter(getApplicationContext().getResources().getColor(R.color.faveo));
            holder.options.setImageDrawable(getDrawable(R.drawable.addnew));
            holder.changeID.setText("#CHN-"+movie.getId());

            if (movie.getSubject().equals("")){
                holder.subject.setVisibility(View.GONE);
            }
            else{
                holder.subject.setVisibility(View.VISIBLE);
                holder.subject.setText(movie.getSubject());
            }

            holder.relativeTimeTextView.setReferenceTime(Helper.relativeTime(movie.getCreatedDate()));


            holder.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //problemId=movie.getId();
                    new BottomDialog.Builder(context)
                            .setTitle("Associating with change")
                            .setContent("Are you sure you want to attach this release with the problem?")
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
                                            dialog1= new SpotsDialog(context, "Associating release");
                                            dialog1.show();
                                            new AttachRelease(changeId,movie.getId()).execute();


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
        public int getItemCount() {
            return moviesList.size();
        }

    }


    // API for attaching release with change
    public class AttachRelease extends AsyncTask<String,Void,String>{

        int changeId,releaseId;

        public AttachRelease(int changeId,int releaseId){
            this.changeId=changeId;
            this.releaseId=releaseId;
        }


        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().attachReleaseWithChange(changeId,releaseId);
        }

        @Override
        protected void onPostExecute(String s) {
            dialog1.dismiss();
            if (s.equals("")||s.equals(null)){
                Toasty.error(ChangeViewPage.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            try{
                JSONObject jsonObject=new JSONObject(s);
                JSONObject data=jsonObject.getJSONObject("data");
                String success=data.getString("success");
                if (success.equals("Release Updated Successfully.")){
                    Toasty.success(ChangeViewPage.this,"Successfully attached to the problem",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());

                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
    class CustomDialogClassReasonForChange extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassReasonForChange(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_reason);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.reason);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="reason";
                        parameterName="reason";
                        progressDialog = new ProgressDialog(ChangeViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    class CustomDialogClassImpact extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassImpact(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialogimpact);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.impactEdit);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="impact";
                        parameterName="impact";
                        progressDialog = new ProgressDialog(ChangeViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
    class CustomDialogClassRolloutPlan extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassRolloutPlan(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_rolloutplan);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.rollout_plan);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="rollout-plan";
                        parameterName="rollout-plan";
                        progressDialog = new ProgressDialog(ChangeViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    class CustomDialogClassBackoutPlan extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassBackoutPlan(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_backoutplan);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.backout_plan);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="backout-plan";
                        parameterName="backout-plan";
                        progressDialog = new ProgressDialog(ChangeViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ChangeDescription(), "Description");
        adapter.addFrag(new ChangeSpecific(), "Detail");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class FetchChangeDetail extends AsyncTask<String,Void,String> {
        int changeId;

        public FetchChangeDetail(int changeId){
            this.changeId=changeId;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().fetchChangeDetail(changeId);
        }
        protected void onPostExecute(String result){
            dialog1.dismiss();

            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String subject=jsonObject1.getString("subject");
                textViewTicketTitle.setText(subject);
                JSONObject jsonObject2=jsonObject1.getJSONObject("status");
                String statusName=jsonObject2.getString("name");
                JSONObject jsonObject3=jsonObject1.getJSONObject("priority");
                String priorityName=jsonObject3.getString("name");
                loaderTextViewPriority.setText(priorityName);
                loaderTextViewstatus.setText(statusName);
                JSONArray jsonArray =jsonObject1.getJSONArray("requester");
                String requester="";
                if (jsonArray.length()==0){
                    requester=getString(R.string.not_available);
                }
                else{
                    JSONObject jsonObject4=jsonArray.getJSONObject(0);
                    String first_name=jsonObject4.getString("first_name");
                    String last_name=jsonObject4.getString("last_name");
                    String user_name=jsonObject4.getString("user_name");
                    requester=first_name+" "+last_name;
                }

                Log.d("Name",requester);

                loaderTextViewRequester.setText(requester);


                } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
    public class workAround extends AsyncTask<String,Void,String> {
        int changeId;
        String tableModule;
        String identifier;
        String solution;
        String body;
        workAround(int changeId,String tableModule,String identifier,String solution,String body){
            this.changeId=changeId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.solution=solution;
            this.body=body;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().workaroundModule(changeId,tableModule,identifier,solution,body);

        }
        protected void onPostExecute(String result){
            progressDialog.dismiss();
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                if (data.equals("Updated.")){
                    Toasty.success(ChangeViewPage.this,"successfully updated",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}
