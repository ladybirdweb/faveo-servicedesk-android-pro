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
import android.view.View;
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
import co.servicedesk.faveo.pro.frontend.fragments.ReleaseDetail;
import co.servicedesk.faveo.pro.frontend.fragments.ReleaseSpecific;
import co.servicedesk.faveo.pro.frontend.fragments.change.ChangeDescription;
import co.servicedesk.faveo.pro.frontend.fragments.change.ChangeSpecific;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class ReleaseViewPage extends AppCompatActivity implements ReleaseDetail.OnFragmentInteractionListener,ReleaseSpecific.OnFragmentInteractionListener  {


    LoaderTextView loaderTextViewTitle,loaderTextViewSubject,loaderTextViewPriority,loaderTextViewStatus;
    SpotsDialog dialog1;
    int releaseId;
    ViewPager vpPager;
    TabLayout tabLayout;
    AHBottomNavigation bottomNavigation;
    String parameterName="";
    String identifier="";
    ProgressDialog progressDialog;
    String table="sd_releases";
    ImageView imageViewBack,editRelease;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_view_page);
        Window window = ReleaseViewPage.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ReleaseViewPage.this, R.color.mainActivityTopBar));
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.white)
        );
        tabLayout.setSelectedTabIndicatorHeight(0);
        tabLayout.setupWithViewPager(vpPager);
        setupViewPager(vpPager);
        editRelease=findViewById(R.id.editChange);
        imageViewBack=findViewById(R.id.imageViewBackTicketDetail);
        loaderTextViewTitle=findViewById(R.id.title);
        loaderTextViewPriority=findViewById(R.id.priority);
        loaderTextViewSubject=findViewById(R.id.subject);
        loaderTextViewStatus=findViewById(R.id.status);
        progressDialog=new ProgressDialog(ReleaseViewPage.this);
        final Intent intent=getIntent();
        releaseId=intent.getIntExtra("releaseId",0);

        loaderTextViewTitle.setText("#REL"+releaseId);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Update", R.drawable.ic_update_black_24dp, R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Delete", R.drawable.ic_delete_black_24dp, R.color.white);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.white));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.white));

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.toolbarColor));
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        if (InternetReceiver.isConnected()){
            dialog1 = new SpotsDialog(ReleaseViewPage.this, getString(R.string.pleasewait));
            dialog1.show();
            new FetchReleaseDetail(releaseId).execute();
        }

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ReleaseViewPage.this,EditAndViewRelease.class);
                intent1.putExtra("releaseId",releaseId);
                startActivity(intent1);
            }
        });

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (position == 0) {
                    MyBottomSheetDialogUpdate myBottomSheetDialog = new MyBottomSheetDialogUpdate(ReleaseViewPage.this);
                    myBottomSheetDialog.show();
                }
                if (position == 1) {
                    new BottomDialog.Builder(ReleaseViewPage.this)
                            .setTitle("Deleting release")
                            .setContent("Are you sure you want to delete this release?")
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
                                            dialog1= new SpotsDialog(ReleaseViewPage.this,"Deleting release");
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

                return true;
            }
        });



    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ReleaseDetail(), "Description");
        adapter.addFrag(new ReleaseSpecific(), "Detail");
        viewPager.setAdapter(adapter);
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


    public class MyBottomSheetDialogUpdate extends BottomSheetDialog {

        Context context;
        TextView textViewBuildPlan, textViewTestPlan;

        MyBottomSheetDialogUpdate(@NonNull Context context) {
            super(context);
            this.context = context;
            createUpdate();
        }

        public void createUpdate() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_release_update, null);
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

            textViewBuildPlan = (TextView) bottomSheetView.findViewById(R.id.buildPlan);
            textViewTestPlan = (TextView) bottomSheetView.findViewById(R.id.testPlan);

            textViewBuildPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassReasonForChange cdd = new CustomDialogClassReasonForChange(ReleaseViewPage.this);
                    Prefs.putString("build_plan","true");
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();

                }
            });

            textViewTestPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDialogClassReasonForChange cdd = new CustomDialogClassReasonForChange(ReleaseViewPage.this);
                    Prefs.putString("build_plan","false");
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.show();
                }
            });


        }

    }

    class CustomDialogClassReasonForChange extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;
        public TextView textViewTitle;

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
            textViewTitle=findViewById(R.id.title);
            if (Prefs.getString("build_plan",null).equals("true")){
                textViewTitle.setText(R.string.build_plan);
            }else{
                textViewTitle.setText(R.string.test_paln);
            }
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
                        if (Prefs.getString("build_plan",null).equals("true")){
                            identifier = "build-plan";
                            parameterName = "build-plan";
                        }else{
                            identifier = "test-plan";
                            parameterName = "test-plan";
                        }
                        progressDialog = new ProgressDialog(ReleaseViewPage.this);
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(releaseId,table,identifier,parameterName,body).execute();
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


    private class FetchReleaseDetail extends AsyncTask<String,Void,String> {
        int releaseId;

        public FetchReleaseDetail(int releaseId){
            this.releaseId=releaseId;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().getReleaseDetails(releaseId);
        }
        protected void onPostExecute(String result){
            dialog1.dismiss();

            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String subject=jsonObject1.getString("subject");
                loaderTextViewSubject.setText(subject);
                JSONObject jsonObject2=jsonObject1.getJSONObject("status");
                String statusName=jsonObject2.getString("name");
                JSONObject jsonObject3=jsonObject1.getJSONObject("priority");
                String priorityName=jsonObject3.getString("name");
                loaderTextViewPriority.setText(priorityName);
                loaderTextViewStatus.setText(statusName);


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
                    Toasty.success(ReleaseViewPage.this,"successfully updated",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
    private class DeleteRelease extends AsyncTask<String,Void,String>{

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
                    Toasty.success(ReleaseViewPage.this, "Release Deleted Successfully.", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ReleaseViewPage.this,ExistingReleases.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}

