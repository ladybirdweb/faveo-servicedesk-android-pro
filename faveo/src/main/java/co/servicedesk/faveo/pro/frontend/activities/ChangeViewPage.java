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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.gson.JsonObject;
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
import co.servicedesk.faveo.pro.frontend.fragments.ChangeDescription;
import co.servicedesk.faveo.pro.frontend.fragments.ChangeSpecific;
import co.servicedesk.faveo.pro.frontend.fragments.ProblemDescription;
import co.servicedesk.faveo.pro.frontend.fragments.ProblemSpecific;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ChangeViewPage extends AppCompatActivity implements ChangeDescription.OnFragmentInteractionListener,ChangeSpecific.OnFragmentInteractionListener {
    Context context;
    ViewPager vpPager;
    TabLayout tabLayout;
    TextView textViewTicketTitle;
    LoaderTextView loaderTextViewImpact,loaderTextViewRequester,loaderTextViewstatus;
    int changeId;
    String changeTitle;
    SpotsDialog dialog1;
    ImageView imageViewEdit;
    AHBottomNavigation bottomNavigation;
    String table="sd_changes";
    String parameterName="";
    String identifier="";
    ProgressDialog progressDialog;
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
        window.setStatusBarColor(ContextCompat.getColor(ChangeViewPage.this, R.color.faveo));
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
        textViewTicketTitle=findViewById(R.id.changeTitleView);
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbarProblem);
        loaderTextViewImpact=mAppBarLayout.findViewById(R.id.changeImpact);
        loaderTextViewRequester=mAppBarLayout.findViewById(R.id.changeRequester);
        loaderTextViewstatus=mAppBarLayout.findViewById(R.id.statusChange);
        imageViewEdit=findViewById(R.id.editChange);
        final Intent intent = getIntent();
        changeId= intent.getIntExtra("changeId",0);
        changeTitle=intent.getStringExtra("changeTitle");
        textViewTicketTitle.setText(changeTitle);
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

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.faveo));
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ChangeViewPage.this,EditAndViewChange.class);
                intent1.putExtra("changeId",changeId);
                startActivity(intent1);
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
                    MyBottomSheetDialogRelease myBottomSheetDialog = new MyBottomSheetDialogRelease(ChangeViewPage.this);
                    myBottomSheetDialog.show();
                }
                if (position==2){
                    new BottomDialog.Builder(ChangeViewPage.this)
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
                                            dialog1= new SpotsDialog(ChangeViewPage.this,"Deleting Problem...");
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
                    Toasty.success(ChangeViewPage.this, getString(R.string.problem_deleted), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ChangeViewPage.this,ExistingProblems.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
            editTextRootCause=findViewById(R.id.rootCause);
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
            editTextRootCause=findViewById(R.id.rootCause);
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
            editTextRootCause=findViewById(R.id.rootCause);
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
            editTextRootCause=findViewById(R.id.rootCause);
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
                        identifier="backout-plan ";
                        parameterName="backout-plan ";
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
                JSONObject jsonObject2=jsonObject1.getJSONObject("status");
                String statusName=jsonObject2.getString("name");
                JSONObject jsonObject3=jsonObject1.getJSONObject("impactType");
                String impactName=jsonObject3.getString("name");
                loaderTextViewImpact.setText(impactName);
                loaderTextViewstatus.setText(statusName);
                JSONObject jsonObject4 =jsonObject2.getJSONObject("requester");

                //loaderTextViewstatus.setText(name);

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
