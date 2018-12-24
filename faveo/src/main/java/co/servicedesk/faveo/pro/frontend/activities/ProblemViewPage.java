package co.servicedesk.faveo.pro.frontend.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.fragments.ProblemSpecific;
import co.servicedesk.faveo.pro.frontend.fragments.ProblemDescription;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.ProblemAssociatedAssets;
import co.servicedesk.faveo.pro.model.ProblemAssociatedTicket;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ProblemViewPage extends AppCompatActivity implements ProblemDescription.OnFragmentInteractionListener,ProblemSpecific.OnFragmentInteractionListener {
    Context context;
    ViewPager vpPager;
    TabLayout tabLayout;
    private ItemAdapter mAdapter;
    private ItemAdapterAsset itemAdapterAsset;
    ImageView imageView,imageViewBack;
    int problemId;
    String problemTitle;
    List<ProblemAssociatedAssets> items;
    List<ProblemAssociatedTicket> items1;
    int assetCount=0;
    int ticketCount=0;
    String table="sd_problem";
    String parameterName="";
    String identifier="";
    AHBottomNavigation bottomNavigation;
    ProgressDialog progressDialog;
    SpotsDialog dialog1;
    TextView textViewTicketTitle;
    String ticketId;
    LoaderTextView loaderTextViewFrom,loaderTextViewstatus,loaderTextViewdepartment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_view_page);
        Window window = ProblemViewPage.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ProblemViewPage.this, R.color.faveo));
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
        imageView=findViewById(R.id.editProblem);
        textViewTicketTitle=findViewById(R.id.ticketNumberDemo);
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbarProblem);
        loaderTextViewdepartment=mAppBarLayout.findViewById(R.id.problemdepartment);
        loaderTextViewFrom=mAppBarLayout.findViewById(R.id.fromProblem);
        loaderTextViewstatus=mAppBarLayout.findViewById(R.id.problemstatus);
        final Intent intent = getIntent();
        problemId= intent.getIntExtra("problemId",0);
        ticketId=intent.getStringExtra("ticket_id");
        problemTitle=intent.getStringExtra("problemTitle");
        textViewTicketTitle.setText(problemTitle);
        imageViewBack=findViewById(R.id.imageViewBackProblemDetail);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ProblemViewPage.this,EditAndViewProblem.class);
                intent1.putExtra("problemId",problemId);
                intent1.putExtra("ticket_id",ticketId);
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
            new FetchProblemDetail(problemId).execute();
        }

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Tickets", R.drawable.ticket, R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Assets", R.drawable.ic_local_grocery_store_black_24dp, R.color.white);
        //AHBottomNavigationItem item3 = new AHBottomNavigationItem("Change", R.drawable.changes, R.color.white);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Update", R.drawable.ic_update_black_24dp, R.color.white);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("More", R.drawable.ic_expand_more_black_24dp, R.color.white);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        //bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.white));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.white));

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.faveo));
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

// Add or remove notification for each item
        //bottomNavigation.setNotification("6", 0);


        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (position == 0) {
                    MyBottomSheetDialogTickets myBottomSheetDialog = new MyBottomSheetDialogTickets(ProblemViewPage.this);
                    myBottomSheetDialog.show();
                }
                if (position == 1) {
                    MyBottomSheetDialogAssets myBottomSheetDialog = new MyBottomSheetDialogAssets(ProblemViewPage.this);
                    myBottomSheetDialog.show();
                }
                if (position == 2) {
                    MyBottomSheetDialogUpdate myBottomSheetDialog = new MyBottomSheetDialogUpdate(ProblemViewPage.this);
                    myBottomSheetDialog.show();
                }
//                if (position == 3) {
//                    MyBottomSheetDialogUpdate myBottomSheetDialog = new MyBottomSheetDialogUpdate(ProblemViewPage.this);
//                    myBottomSheetDialog.show();
//                }
                if (position == 3) {
                    MyBottomSheetDialogMore myBottomSheetDialog = new MyBottomSheetDialogMore(ProblemViewPage.this);
                    myBottomSheetDialog.show();
                }
                return true;
            }
        });


// Disable the translation inside the CoordinatorLayout

    }

    @SuppressLint("StaticFieldLeak")
    public class FetchProblemDetail extends AsyncTask<String,Void,String>{
        int problemId;

        FetchProblemDetail(int problemId){
            this.problemId=problemId;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().fetchProblemDetail(problemId);
        }
        protected void onPostExecute(String result){
            items= new ArrayList<>();
            items1=new ArrayList<>();
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                JSONObject jsonObject3=jsonObject1.getJSONObject("department");
                String name=jsonObject3.getString("name");
                String from=jsonObject1.getString("from");
                JSONObject jsonObject4=jsonObject1.getJSONObject("status_type_id");
                String status=jsonObject4.getString("name");
                if (!from.equals("")){
                    loaderTextViewFrom.setText(from);
                }
                if (!name.equals("")){
                    loaderTextViewdepartment.setText(name);
                }
                if (!status.equals("")){
                    loaderTextViewstatus.setText(status);
                }
                JSONArray jsonArray=jsonObject1.getJSONArray("asset");
                for (int i=0;i<jsonArray.length();i++){
                    assetCount++;
                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                    String name1=jsonObject2.getString("name");
                    int id=jsonObject2.getInt("id");
                    ProblemAssociatedAssets problemAssociatedTicket=new ProblemAssociatedAssets(1,name1);
                    items.add(problemAssociatedTicket);


                }
                JSONArray jsonArray1=jsonObject1.getJSONArray("tickets");
                for (int i=0;i<jsonArray1.length();i++){
                    ticketCount++;
                    JSONObject jsonObject2=jsonArray1.getJSONObject(i);
                    int id=jsonObject2.getInt("id");
                    String number=jsonObject2.getString("ticket_number");
                    String title=jsonObject2.getString("title");

                    ProblemAssociatedTicket problemAssociatedTicket=new ProblemAssociatedTicket(id,title,number);
                    items1.add(problemAssociatedTicket);

                }
                bottomNavigation.setNotification(""+assetCount, 1);
                bottomNavigation.setNotification(""+ticketCount,0);

                } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public class workAround extends AsyncTask<String,Void,String> {
        int problemId;
        String tableModule;
        String identifier;
        String solution;
        String body;
        workAround(int problemId,String tableModule,String identifier,String solution,String body){
            this.problemId=problemId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.solution=solution;
            this.body=body;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().workaroundModule(problemId,tableModule,identifier,solution,body);

        }
        protected void onPostExecute(String result){
            progressDialog.dismiss();
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                if (data.equals("Updated.")){
                    Toasty.success(ProblemViewPage.this,"successfully updated",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProblemDescription(), "Analysis");
        adapter.addFrag(new ProblemSpecific(), "Detail");
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


    public class MyBottomSheetDialogMore extends BottomSheetDialog {

        Context context;
        TextView  viewproblem;

        MyBottomSheetDialogMore(@NonNull Context context) {
            super(context);
            this.context = context;
            createMore();
        }

        public void createMore() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_more, null);
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
            viewproblem = (TextView) bottomSheetView.findViewById(R.id.delete);
            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BottomDialog.Builder(ProblemViewPage.this)
                            .setTitle(R.string.deleting_prob)
                            .setContent(R.string.suredeletingproblem)
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
                                            dialog1= new SpotsDialog(ProblemViewPage.this,"Deleting Problem...");
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
    }

    public class MyBottomSheetDialogAssets extends BottomSheetDialog {

        Context context;
        RecyclerView recyclerView;
        TextView textViewEmpty;
        MyBottomSheetDialogAssets(@NonNull Context context) {
            super(context);
            this.context = context;
            createTickets();
        }

        public void createTickets() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_assets, null);
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
            recyclerView = (RecyclerView) bottomSheetView.findViewById(R.id.recyclerViewAsset);
            textViewEmpty=bottomSheetView.findViewById(R.id.empty_view);
            if (items.isEmpty()){
                textViewEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProblemViewPage.this));
                itemAdapterAsset = new ItemAdapterAsset(ProblemViewPage.this,items);
                recyclerView.setAdapter(itemAdapterAsset);
                itemAdapterAsset.notifyDataSetChanged();
            }


        }

    }


    public class MyBottomSheetDialogTickets extends BottomSheetDialog {

        Context context;
        RecyclerView recyclerView;
        TextView textViewEmptyTextView;
        MyBottomSheetDialogTickets(@NonNull Context context) {
            super(context);
            this.context = context;
            createTickets();
        }

        public void createTickets() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_tickets, null);
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
            recyclerView = (RecyclerView) bottomSheetView.findViewById(R.id.recyclerView);
            textViewEmptyTextView=bottomSheetView.findViewById(R.id.empty_view);
            if (items1.isEmpty()){
                textViewEmptyTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else{
                recyclerView.setVisibility(View.VISIBLE);
                textViewEmptyTextView.setVisibility(View.GONE);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProblemViewPage.this));
                mAdapter = new ItemAdapter(items1);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }


        }

    }

    public class MyBottomSheetDialogChange extends BottomSheetDialog {

        Context context;
        TextView associate, viewproblem;

        MyBottomSheetDialogChange(@NonNull Context context) {
            super(context);
            this.context = context;
            createChange();
        }

        public void createChange() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_change, null);
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

            associate = (TextView) bottomSheetView.findViewById(R.id.associate);
            viewproblem = (TextView) bottomSheetView.findViewById(R.id.viewproblem);

            associate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "clicked on new change", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public class MyBottomSheetDialogUpdate extends BottomSheetDialog {

        Context context;
        TextView associate, viewproblem, deleteproblem, solutions;

        MyBottomSheetDialogUpdate(@NonNull Context context) {
            super(context);
            this.context = context;
            createUpdate();
        }

        public void createUpdate() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_update, null);
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

            associate = (TextView) bottomSheetView.findViewById(R.id.associate);
            viewproblem = (TextView) bottomSheetView.findViewById(R.id.viewproblem);
            deleteproblem = (TextView) bottomSheetView.findViewById(R.id.deleteproblem);
            solutions = bottomSheetView.findViewById(R.id.solution);
            associate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassRootCause cdd = new CustomDialogClassRootCause(ProblemViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });
            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassImpact cdd = new CustomDialogClassImpact(ProblemViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });
            deleteproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassSymptoms cdd = new CustomDialogClassSymptoms(ProblemViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });
            solutions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassSolution cdd = new CustomDialogClassSolution(ProblemViewPage.this);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });

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
                    Toasty.success(ProblemViewPage.this, getString(R.string.problem_deleted), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ProblemViewPage.this,ExistingProblems.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class CustomDialogClassRootCause extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassRootCause(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialogrootcause);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.rootCause);
            String data=Prefs.getString("description",null);
            if (!data.equals("")){
                editTextRootCause.setText(data);
            }
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
                        identifier="root-cause";
                        parameterName="root-cause";
                        progressDialog = new ProgressDialog(ProblemViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(problemId,table,identifier,parameterName,body).execute();
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
        public EditText editTextImpact;
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
            editTextImpact=findViewById(R.id.impactEdit);
            String data=Prefs.getString("impact",null);
            if (!data.equals("")){
                editTextImpact.setText(data);
            }
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextImpact.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextImpact.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="impact";
                        parameterName="impact";
                        progressDialog = new ProgressDialog(ProblemViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(problemId,table,identifier,parameterName,body).execute();
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

    class CustomDialogClassSymptoms extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        EditText editTextSymptoms;
        public CustomDialogClassSymptoms(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_symptons);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextSymptoms=findViewById(R.id.symptomsEdit);
            String data=Prefs.getString("symptoms",null);
            if (!data.equals("")){
                editTextSymptoms.setText(data);
            }
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextSymptoms.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextSymptoms.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="symptoms";
                        parameterName="symptoms";
                        progressDialog = new ProgressDialog(ProblemViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(problemId,table,identifier,parameterName,body).execute();
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

    class CustomDialogClassSolution extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        EditText editTextSolution;
        public CustomDialogClassSolution(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_solution);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextSolution=findViewById(R.id.editSolution);
            String data=Prefs.getString("solution",null);
            if (!data.equals("")){
                editTextSolution.setText(data);
            }
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextSolution.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextSolution.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="solution";
                        parameterName="solution";
                        progressDialog = new ProgressDialog(ProblemViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(problemId,table,identifier,parameterName,body).execute();
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

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private List<ProblemAssociatedTicket> mItems;


        ItemAdapter(List<ProblemAssociatedTicket> items) {
            mItems = items;

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_associated_ticket, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final ProblemAssociatedTicket problemAssociatedTicket = mItems.get(position);
            holder.textViewTicketNumber.setText(problemAssociatedTicket.getTicketnumber());
            holder.textViewSubject.setText(problemAssociatedTicket.getTitle());
            holder.cardViewAssociated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), TicketDetailActivity.class);
                    intent.putExtra("ticket_id", problemAssociatedTicket.getId() + "");
                    view.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewSubject,textViewTicketNumber;
            String item;
            CardView cardViewAssociated;

            ViewHolder(View itemView) {
                super(itemView);
                textViewSubject = (TextView) itemView.findViewById(R.id.textView_ticket_subject);
                textViewTicketNumber=itemView.findViewById(R.id.textView_ticket_number);
                cardViewAssociated=itemView.findViewById(R.id.ticketAssociated);
            }

        }
        }

    public class ItemAdapterAsset extends RecyclerView.Adapter<ItemAdapterAsset.ViewHolder> {

        private List<ProblemAssociatedAssets> mItems;
        Context context;


        ItemAdapterAsset(Context context,List<ProblemAssociatedAssets> items) {
            mItems = items;
            this.context=context;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_associated_assets, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ProblemAssociatedAssets problemAssociatedTicket = mItems.get(position);
            holder.assetTitle.setText(problemAssociatedTicket.getTitle());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView assetTitle;
            CardView cardViewAssociated;
            ViewHolder(View itemView) {
                super(itemView);
                assetTitle=itemView.findViewById(R.id.textView_asset_title);
                cardViewAssociated=itemView.findViewById(R.id.ticketAssociated);
            }

        }



    }
}



