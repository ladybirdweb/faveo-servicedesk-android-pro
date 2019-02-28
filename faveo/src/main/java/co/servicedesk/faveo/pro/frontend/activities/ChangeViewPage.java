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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.fragments.change.ChangeDescription;
import co.servicedesk.faveo.pro.frontend.fragments.change.ChangeSpecific;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
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

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Update", R.drawable.ic_update_black_24dp, R.color.white);
        AHBottomNavigationItem item2=new AHBottomNavigationItem("Release",R.drawable.ic_new_releases_black_24dp,R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Delete", R.drawable.ic_delete_black_24dp, R.color.white);

// Add items
        bottomNavigation.addItem(item1);
        //bottomNavigation.addItem(item2);
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
//                if (position == 1) {
//                    MyBottomSheetDialogRelease myBottomSheetDialog = new MyBottomSheetDialogRelease(ChangeViewPage.this);
//                    myBottomSheetDialog.show();
//                }
                if (position==1){
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
