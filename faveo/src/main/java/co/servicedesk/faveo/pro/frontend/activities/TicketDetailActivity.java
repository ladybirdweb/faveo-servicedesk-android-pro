package co.servicedesk.faveo.pro.frontend.activities;

import android.app.ProgressDialog;
//import android.content.DialogInterface;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
//import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
//import android.text.SpannableString;
//import android.text.style.ForegroundColorSpan;
//import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import co.servicedesk.faveo.pro.Constants;
//import co.helpdesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.drawers.FragmentDrawer;
import co.servicedesk.faveo.pro.frontend.fragments.ticketDetail.Conversation;
import co.servicedesk.faveo.pro.frontend.fragments.ticketDetail.Detail;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.Attachedproblem;
import co.servicedesk.faveo.pro.model.Data;
import co.servicedesk.faveo.pro.model.MessageEvent;
//import co.helpdesk.faveo.pro.model.TicketDetail;
import co.servicedesk.faveo.pro.model.ProblemModel;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * This splash activity is responsible for
 * getting the metadata of our faveo application from the dependency API.
 */
public class TicketDetailActivity extends AppCompatActivity implements
        Conversation.OnFragmentInteractionListener,
        Detail.OnFragmentInteractionListener,View.OnClickListener,FragmentDrawer.FragmentDrawerListener, NavigationView.OnNavigationItemSelectedListener{
    //private DrawerLayout mDrawerLayout;
    ViewPager viewPager;
    public ViewPagerAdapter adapter;
    Conversation fragmentConversation;
    Detail fragmentDetail;
    EditText editTextInternalNote, editTextReplyMessage;
    Button buttonCreate, buttonSend;
    ProgressDialog progressDialog;
    SpotsDialog dialog1;
    public static String ticketID, ticketNumber;
    TextView textView;
    String status;
    String title;
    Context context;
    TextView addCc,headerTitle;
    View viewCollapsePriority;
    ImageView imgaeviewBack,imageViewSource;
    private ActionBarDrawerToggle mDrawerToggle;
    public static boolean isShowing = false;
    LoaderTextView textViewStatus, textViewTitle,textViewSubject,textViewDepartment;
    TextView textviewAgentName;
    ArrayList<Data> statusItems;
    int id = 0;
    FabSpeedDial fabSpeedDial;
    View view;
    NavigationView navigationView;
    private Menu menu;
    TextView textViewDemo;
    TextView textViewAssetCount,problemCount;
    View getView;
    RelativeLayout linearLayoutCreateAsset,linearLayoutCreateProblem;
    AHBottomNavigation bottomNavigation;
    int problemId;
    List<ProblemModel> problemList = new ArrayList<>();
    List<Attachedproblem> problemListAttached = new ArrayList<>();
    static String nextPageURL = "";
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    ProblemAdpter mAdapter;
    ProblemAdpterAttached problemAdpterAttached;
    int problemcount=2;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ticket_detail);
        Window window = TicketDetailActivity.this.getWindow();
        context=this;
// clear FLAG_TRANSLUCENT_STATUS flag:

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(TicketDetailActivity.this,R.color.mainActivityTopBar));
       // imageView=findViewById(R.id.collaboratorview);
        //view=findViewById(R.id.overlay);
        Prefs.putString("cameFromNewProblem","false");
        imageViewSource=findViewById(R.id.imageView_default_profile);
        //mDrawerLayout=findViewById(R.id.my_drawer_layout);

//        if (navigationView != null) {
//            setupDrawerContent(navigationView);
//            if (navigationView != null) {
//                navigationView.setNavigationItemSelectedListener(this);
//            }
//        }

        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_main);
        imageView=findViewById(R.id.collaboratorview);
//            navigationView=findViewById(R.id.nav_view);
//        textViewAssetCount=navigationView.findViewById(R.id.assetcount);
//        problemCount=navigationView.findViewById(R.id.problemcount);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Problems", R.drawable.problemimage, R.color.white);
        //AHBottomNavigationItem item2 = new AHBottomNavigationItem("Assets", R.drawable.ic_local_grocery_store_black_24dp, R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Reply", R.drawable.ic_reply_black_24dp, R.color.white);
        //AHBottomNavigationItem item4 = new AHBottomNavigationItem("Reply", R.drawable.ic_reply_black_24dp, R.color.white);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Internal note", R.drawable.ic_note_black_24dp, R.color.white);


        bottomNavigation.addItem(item1);
       //bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        //bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.white));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.white));

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.toolbarColor));
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (position == 0) {
                    if (problemcount==0){
                        MyBottomSheetDialogChange myBottomSheetDialog = new MyBottomSheetDialogChange(TicketDetailActivity.this);
                        myBottomSheetDialog.show();
                    }
                    else if (problemcount==1){
                        MyBottomSheetDialogShowingAttachedProblem myBottomSheetDialogShowingAttachedProblem=new MyBottomSheetDialogShowingAttachedProblem(TicketDetailActivity.this);
                        myBottomSheetDialogShowingAttachedProblem.show();
                    }

                }

                if (position == 1) {
                    Intent intent=new Intent(TicketDetailActivity.this,TicketReplyActivity.class);
                    intent.putExtra("ticket_id", ticketID);
                    startActivity(intent);
                }
//                if (position == 2) {
//                    MyBottomSheetDialogReply myBottomSheetDialog = new MyBottomSheetDialogReply(TicketDetailActivity.this);
//                    myBottomSheetDialog.show();
//                }
                if (position ==2) {
                    Intent intent=new Intent(TicketDetailActivity.this,InternalNoteActivity.class);
                    intent.putExtra("ticket_id", ticketID);
                    startActivity(intent);
                }
                if (position==4){

                }

                // Do something cool here...
                return true;
            }
        });

        try {

            if (Prefs.getString("activated", null).equals("True")) {
                bottomNavigation.setVisibility(View.VISIBLE);
                fabSpeedDial.setVisibility(View.GONE);

            } else {
                bottomNavigation.setVisibility(View.GONE);
                fabSpeedDial.setVisibility(View.VISIBLE);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("cameFromTicket","true");
                Intent intent=new Intent(TicketDetailActivity.this,CollaboratorAdd.class);
                intent.putExtra("ticket_id", ticketID);
                startActivity(intent);
            }
        });



       fabSpeedDial.setOnClickListener(this);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
               int id=menuItem.getItemId();

               if (id==R.id.fab_reply){
                   Intent intent=new Intent(TicketDetailActivity.this,TicketReplyActivity.class);
                   intent.putExtra("ticket_id", ticketID);
                   startActivity(intent);
               }
               else if (id==R.id.fab_internalnote){
                   Intent intent=new Intent(TicketDetailActivity.this,InternalNoteActivity.class);
                   intent.putExtra("ticket_id", ticketID);
                   startActivity(intent);
               }
                //TODO: Start some activity
                return false;
            }
        });
//        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_ticket);
//        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
//            @Override
//            public void onNavigationItemReselected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_favorites:
//                        Intent intent2=new Intent(TicketDetailActivity.this,TicketSaveActivity.class);
//                        startActivity(intent2);
//                        // TODO
//
//                    case R.id.action_schedules:
//                        // TODO
//                        Intent intent=new Intent(TicketDetailActivity.this,TicketReplyActivity.class);
//                        startActivity(intent);
//
//                    case R.id.action_music:
//                        // TODO
//                        Intent intent1=new Intent(TicketDetailActivity.this,InternalNoteActivity.class);
//                        startActivity(intent1);
//
//                }
//
//            }
//        });
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.action_favorites:
//                                Intent intent2=new Intent(TicketDetailActivity.this,TicketSaveActivity.class);
//                                startActivity(intent2);
//                                // TODO
//                                return true;
//                            case R.id.action_schedules:
//                                // TODO
//                                Intent intent=new Intent(TicketDetailActivity.this,TicketReplyActivity.class);
//                                startActivity(intent);
//                                return true;
//                            case R.id.action_music:
//                                // TODO
//                                Intent intent1=new Intent(TicketDetailActivity.this,InternalNoteActivity.class);
//                                startActivity(intent1);
//                                return true;
//                        }
//                        return false;
//                    }
//                });


        //setupFab();
        Prefs.putString("querry","null");
        statusItems=new ArrayList<>();
        JSONObject jsonObject1;
        //progressBar= (ProgressBar) findViewById(R.id.TicketDetailProgressbar);
        Data data;
        String json1 = Prefs.getString("DEPENDENCY", "");
        //statusItems.add(new Data(0, "Please select help topic"));
        try {
            jsonObject1 = new JSONObject(json1);
            JSONArray jsonArrayHelpTopics = jsonObject1.getJSONArray("status");
            for (int i = 0; i < jsonArrayHelpTopics.length(); i++) {
                Data data1 = new Data(Integer.parseInt(jsonArrayHelpTopics.getJSONObject(i).getString("id")), jsonArrayHelpTopics.getJSONObject(i).getString("name"));
                statusItems.add(data1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("ticketDetailOnCreate","True");
        final Intent intent = getIntent();
        ticketID=intent.getStringExtra("ticket_id");
        Prefs.putString("TICKETid",ticketID);
        try {
            if (InternetReceiver.isConnected()) {
                new FetchAttachedProblem(Integer.parseInt(ticketID)).execute();
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarTicketDetail);
        setSupportActionBar(mToolbar);
        //mToolbar.setOverflowIcon(R.drawable.ic_action_attach_file);
//        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_attach_file));

        //Log.d("cameFromNotification",cameFromNotification);
        ticketNumber = getIntent().getStringExtra("ticket_number");


        //linearLayout= (LinearLayout) findViewById(R.id.section_internal_note);
        //mToolbar = (Toolbar) findViewById(R.id.toolbarTicketDetail);
        textViewStatus = (LoaderTextView) mAppBarLayout.findViewById(R.id.status);
        textviewAgentName = (TextView) mAppBarLayout.findViewById(R.id.agentassigned);
        //textViewTitle = (LoaderTextView) mAppBarLayout.findViewById(R.id.title);
        textViewDepartment= (LoaderTextView) mAppBarLayout.findViewById(R.id.department);
        textViewSubject = (LoaderTextView) mAppBarLayout.findViewById(R.id.subject);
        imgaeviewBack= (ImageView) mToolbar.findViewById(R.id.imageViewBackTicketDetail);
        //viewpriority=mToolbar.findViewById(R.id.viewPriority);
        viewCollapsePriority=mAppBarLayout.findViewById(R.id.viewPriority1);
        //viewCollapsePriority.setBackgroundColor(Color.parseColor("#FF0000"));
        textViewDemo=findViewById(R.id.subject);
        dialog1= new SpotsDialog(TicketDetailActivity.this);
        mToolbar.inflateMenu(R.menu.menu_main_new);
        isShowing=true;
        //Log.d("came into ticket detail","true");
        //mToolbar.getMenu().getItem(0).setEnabled(false);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Prefs.putString("cameFromTicket","true");
//                Intent intent=new Intent(TicketDetailActivity.this,CollaboratorAdd.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        Prefs.putString("cameFromTicket","false");
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    //showOption(R.id.action_info);
                } else if (isShow) {
                    isShow = false;
                    //hideOption(R.id.action_info);
                }
            }
        });
        addCc = (TextView) findViewById(R.id.addcc);
        if (InternetReceiver.isConnected()){
//            progressDialog=new ProgressDialog(this);
//            progressDialog.setMessage(getString(R.string.pleasewait));
//            progressDialog.show();
            new FetchTicketDetail(Prefs.getString("TICKETid",null)).execute();
            //new FetchCollaboratorAssociatedWithTicket(Prefs.getString("ticketId", null)).execute();
            //new FetchCollaboratorAssociatedWithTicket(Prefs.getString("TICKETid",null)).execute();
            }
        imgaeviewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cameFromnotification",Prefs.getString("cameFromNotification",null));
                String option=Prefs.getString("cameFromNotification",null);

                switch (option) {
                    case "true": {
//                        Intent intent = new Intent(TicketDetailActivity.this, NotificationActivity.class);
//                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "none": {
                        //finish();
                        finish();
                        break;
                    }
                    case "false": {
                        finish();
                        break;
                    }
                    default: {
                        finish();
                        break;
                    }
                }
//                if (Prefs.getString("cameFromNotification",null).equals("true")){
//                    Intent intent = new Intent(TicketDetailActivity.this, NotificationActivity.class);
//                    startActivity(intent);
//                }
//               if (Prefs.getString("cameFromNotification",null).equals("false")){
//                    Intent intent1=new Intent(TicketDetailActivity.this,MainActivity.class);
//                    startActivity(intent1);
//                }if (Prefs.getString("cameFromnotification", null).equals("none")){
//                    Intent intent = new Intent(TicketDetailActivity.this, SearchActivity.class);
//                    startActivity(intent);
//                }
//
//                else{
//                    Intent intent1=new Intent(TicketDetailActivity.this,MainActivity.class);
//                    startActivity(intent1);
//                }
////                switch (Prefs.getString("cameFromNotification", null)) {
////                    case "true":
////                        Intent intent = new Intent(TicketDetailActivity.this, NotificationActivity.class);
////                        startActivity(intent);
////                        break;
////                    case "false":
////                        Intent intent1=new Intent(TicketDetailActivity.this,MainActivity.class);
////                        startActivity(intent1);
////                        break;
////                    default:
////                        finish();
////                        break;
////                }
              }


        });

        setSupportActionBar(mToolbar);
        Constants.URL = Prefs.getString("COMPANY_URL", "");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        JSONObject jsonObject;
        String json = Prefs.getString("DEPENDENCY", "");

        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArrayStaffs = jsonObject.getJSONArray("status");

            for (int i = 0; i < jsonArrayStaffs.length(); i++) {
                switch (jsonArrayStaffs.getJSONObject(i).getString("name")) {
                    case "Open":
                        Prefs.putString("openid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                        break;
                    case "Resolved":
                        Prefs.putString("resolvedid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                        break;
                    case "Closed":
                        Prefs.putString("closedid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                        break;
                    case "Deleted":
                        Prefs.putString("deletedid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                        break;
                    case "Archived":
                        Prefs.putString("archivedid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                        break;
                    case "Verified":
                        Prefs.putString("verifiedid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                        break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        progressDialog = new ProgressDialog(this);
        editTextInternalNote = (EditText) findViewById(R.id.editText_internal_note);
        editTextReplyMessage = (EditText) findViewById(R.id.editText_reply_message);
        buttonCreate = (Button) findViewById(R.id.button_create);
        buttonSend = (Button) findViewById(R.id.button_send);
//        linearLayoutCreateAsset=navigationView.findViewById(R.id.create_asset);
//        linearLayoutCreateProblem=navigationView.findViewById(R.id.create_problem);

//        linearLayoutCreateProblem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               Intent intent=new Intent(TicketDetailActivity.this,AssociatedProblem.class);
//               startActivity(intent);
//            }
//        });
//
//        linearLayoutCreateAsset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//            }
//        });


//        mDrawerToggle = new ActionBarDrawerToggle(TicketDetailActivity.this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                new FetchAttachedProblem(Integer.parseInt(Prefs.getString("TICKETid",null))).execute();
//                //textViewAssetCount.setText("1");
//
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//                //textViewAssetCount.setText("0");
//
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
////        mDrawerLayout.post(new Runnable() {
////            @Override
////            public void run() {
////                mDrawerToggle.syncState();
////            }
////        });
//
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
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_problem, null);
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
                    Intent intent=new Intent(TicketDetailActivity.this,NewProblem.class);
                    Prefs.putString("cameFromMain", "false");
                    startActivity(intent);
                }
            });
            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent=new Intent(TicketDetailActivity.this,ExistingProblems.class);
//                    startActivity(intent);
                    MyBottomSheetDialogExistingProblem myBottomSheetDialog = new MyBottomSheetDialogExistingProblem(TicketDetailActivity.this);
                    myBottomSheetDialog.show();

                }
            });

        }

    }

    public class MyBottomSheetDialogShowingAttachedProblem extends BottomSheetDialog {

        Context context;

        MyBottomSheetDialogShowingAttachedProblem(@NonNull Context context) {
            super(context);
            this.context = context;
            createChange();
        }

        public void createChange() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_attached_problem, null);
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
            RecyclerView recyclerViewAttachedProblem;
            recyclerViewAttachedProblem = (RecyclerView) bottomSheetView.findViewById(R.id.listAttached);
            final LinearLayoutManager linearLayoutManager1= new LinearLayoutManager(TicketDetailActivity.this);
            linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewAttachedProblem.setLayoutManager(linearLayoutManager1);
            problemAdpterAttached = new ProblemAdpterAttached(TicketDetailActivity.this,problemListAttached);
            recyclerViewAttachedProblem.setAdapter(problemAdpterAttached);
            //recyclerView.getAdapter().notifyDataSetChanged();
            problemAdpterAttached.notifyDataSetChanged();


        }

    }
    public class ProblemAdpterAttached extends RecyclerView.Adapter<ProblemAdpterAttached.MyViewHolder> {
        private List<Attachedproblem> moviesList;
        Context context;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject,impact;
            RelativeLayout relativeLayout;
            ImageButton imageButton;
            TextView department,textViewStatus,problemIdView;
            public MyViewHolder(View view) {
                super(view);
                email = (TextView) view.findViewById(R.id.textView_client_email);
                subject= (TextView) view.findViewById(R.id.collaboratorname);
                impact=view.findViewById(R.id.impact);
                relativeLayout=view.findViewById(R.id.problemList);
                imageButton=view.findViewById(R.id.detach);
                department=view.findViewById(R.id.textViewDepartmentName);
                textViewStatus=view.findViewById(R.id.statusView);
                problemIdView=view.findViewById(R.id.problemId);
            }
        }

        public ProblemAdpterAttached(Context context,List<Attachedproblem> moviesList) {
            this.moviesList = moviesList;
            this.context=context;
        }

        @Override
        public ProblemAdpterAttached.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listattachedproblem, parent, false);
            return new ProblemAdpterAttached.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ProblemAdpterAttached.MyViewHolder holder, int position) {
            final Attachedproblem movie = moviesList.get(position);

            holder.problemIdView.setText("#PRB-"+movie.getId());

            holder.email.setText(movie.getFrom());

            holder.impact.setText(movie.getImpact());

            holder.subject.setText(movie.getSubject());


            if (!movie.getStatus().equals("")){
                holder.textViewStatus.setText(movie.getStatus());
            }



            if (!movie.getDepartment().equals("")){
                holder.department.setText(movie.getDepartment());
            }

            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BottomDialog.Builder(TicketDetailActivity.this)
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
                                            dialog1= new SpotsDialog(TicketDetailActivity.this, "Detaching Problem..");
                                            dialog1.show();
                                            new DetachProblem(Integer.parseInt(ticketID),problemId).execute();

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
                    Intent intent=new Intent(TicketDetailActivity.this,ProblemViewPage.class);
                    intent.putExtra("problemId",movie.getId());
                    intent.putExtra("ticket_id",ticketID);
                    Log.d("subject",movie.getSubject());
                    Prefs.putString("cameFromMain","False");
                    intent.putExtra("problemTitle",movie.getSubject());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return moviesList.size();
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
                Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            try{
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Detached Successfully")){
                    Toasty.success(TicketDetailActivity.this,"successfully detached the problem from the ticket",Toast.LENGTH_SHORT).show();
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

    public class MyBottomSheetDialogChangeStatus extends BottomSheetDialog {

        Context context;

        ListView listView;
        MyBottomSheetDialogChangeStatus(@NonNull Context context) {
            super(context);
            this.context = context;
            createChange();
        }

        public void createChange() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_change_status, null);
            setContentView(bottomSheetView);
            listView=bottomSheetView.findViewById(R.id.listViewStatus);
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
            ArrayAdapter adapter = new ArrayAdapter<Data>(TicketDetailActivity.this,
                    R.layout.listview_item_row,statusItems);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    status = Prefs.getString("ticketstatus", null);
                    Data data=statusItems.get(i);
                    if (data.getName().equalsIgnoreCase(status)){
                        Toasty.warning(TicketDetailActivity.this, "Ticket is already in "+listView.getAdapter().getItem(i).toString()+" state", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        id=data.getID();
                        new BottomDialog.Builder(TicketDetailActivity.this)
                                .setTitle(getString(R.string.changingStatus))
                                .setContent(getString(R.string.statusConfirmation))
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
                                            new StatusChange(Integer.parseInt(ticketID), id).execute();
                                            dialog1= new SpotsDialog(TicketDetailActivity.this, getString(R.string.changingStatus));
                                            dialog1.show();
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




//                    if (status.equalsIgnoreCase(listView.getAdapter().getItem(i).toString())){
//                        Toasty.warning(TicketDetailActivity.this, "Ticket is already in "+listView.getAdapter().getItem(i).toString()+" state", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        new BottomDialog.Builder(TicketDetailActivity.this)
//                                .setTitle(getString(R.string.changingStatus))
//                                .setContent(getString(R.string.statusConfirmation))
//                                .setPositiveText("YES")
//                                .setNegativeText("NO")
//                                .setPositiveBackgroundColorResource(R.color.white)
//                                //.setPositiveBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)
//                                .setPositiveTextColorResource(R.color.faveo)
//                                .setNegativeTextColor(R.color.black)
//                                //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
//                                .onPositive(new BottomDialog.ButtonCallback() {
//                                    @Override
//                                    public void onClick(BottomDialog dialog) {
//                                        if (InternetReceiver.isConnected()){
//                                            new StatusChange(Integer.parseInt(ticketID), id).execute();
//                                            dialog1= new SpotsDialog(TicketDetailActivity.this, getString(R.string.changingStatus));
//                                            dialog1.show();
//                                        }
//                                    }
//                                }).onNegative(new BottomDialog.ButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull BottomDialog bottomDialog) {
//                                bottomDialog.dismiss();
//                            }
//                        })
//                                .show();
//                    }
                }
            });

        }

    }
    public class MyBottomSheetDialogExistingProblem extends BottomSheetDialog {

        Context context;

        MyBottomSheetDialogExistingProblem(@NonNull Context context) {
            super(context);
            this.context = context;
            createTickets();
        }

        public void createTickets() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheetexisting_problem, null);
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
            final RecyclerView recyclerView = (RecyclerView) bottomSheetView.findViewById(R.id.recyclerViewExistingProblem);
            final ProgressBar progressBar=bottomSheetView.findViewById(R.id.progressbarExisting);
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.faveo)));
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            class FetchExistingProblem extends AsyncTask<String, Void, String> {


                FetchExistingProblem() {
                }

                protected String doInBackground(String... urls) {
                    return new Helpdesk().getExisitngProblem();
                }

                protected void onPostExecute(String result) {
                    //dialog1.dismiss();
                    problemList.clear();
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    if (isCancelled()) return;

                    if (result == null) {
                        Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
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
                            String priority=jsonObject2.getString("priority");
                            ProblemModel problemModel=new ProblemModel(email,subject,createdDate,id,priority);
                            problemList.add(problemModel);
                        }
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        final LinearLayoutManager linearLayoutManager= new LinearLayoutManager(TicketDetailActivity.this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        mAdapter = new ProblemAdpter(TicketDetailActivity.this,problemList);
                        recyclerView.setAdapter(mAdapter);
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
                                            new FetchNextPage(TicketDetailActivity.this).execute();
                                            StyleableToast st = new StyleableToast(TicketDetailActivity.this, getString(R.string.loading), Toast.LENGTH_SHORT);
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
                        mAdapter.notifyDataSetChanged();

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
                    String priority=jsonObject2.getString("priority");
                    ProblemModel problemModel=new ProblemModel(email,subject,createdDate,id,priority);
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
                Toast.makeText(TicketDetailActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            try{
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Problem attached to this ticket")){
                    Toasty.success(TicketDetailActivity.this,"Successfully attached to the ticket",Toast.LENGTH_SHORT).show();
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


    public class ProblemAdpter extends RecyclerView.Adapter<ProblemAdpter.MyViewHolder> {
        private List<ProblemModel> moviesList;
        Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView email;
            public TextView subject;
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
                }
        }

        public ProblemAdpter(Context context,List<ProblemModel> moviesList) {
            this.moviesList = moviesList;
            this.context=context;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listofproblems, parent, false);
            return new ProblemAdpter.MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final ProblemModel movie = moviesList.get(position);
            holder.options.setColorFilter(getApplicationContext().getResources().getColor(R.color.faveo));
            holder.options.setImageDrawable(getDrawable(R.drawable.addnew));
            if (!movie.getEmail().equals("")) {
                holder.email.setText(movie.getEmail());
            }

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
                    problemId=movie.getId();
                    new BottomDialog.Builder(context)
                            .setTitle(R.string.associating)
                            .setContent(R.string.problem_with_ticket)
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
                                            dialog1= new SpotsDialog(context, "Associating Problem..");
                                            dialog1.show();
                                            new AttachProblem(Integer.parseInt(Prefs.getString("TICKETid",null)),problemId).execute();

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



public class MyBottomSheetDialogReply extends BottomSheetDialog {

        Context context;
        TextView associate, viewproblem;

        MyBottomSheetDialogReply(@NonNull Context context) {
            super(context);
            this.context = context;
            createChange();
        }

        public void createChange() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_reply, null);
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
                    Intent intent=new Intent(TicketDetailActivity.this,TicketReplyActivity.class);
                    startActivity(intent);
                }
            });
            viewproblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(TicketDetailActivity.this,InternalNoteActivity.class);
                    startActivity(intent);
                }
            });

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
//            dialog1.dismiss();
            problemList.clear();
            try {
                if (s.equals("") || s.equals(null)) {
                    Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
//                problemCount.setText("1");
                problemcount=1;
                problemId=jsonObject1.getInt("id");
                String subject=jsonObject1.getString("subject");
                String from=jsonObject1.getString("from");
                JSONObject jsonObject2=jsonObject1.getJSONObject("impact_id");
                String impact=jsonObject2.getString("name");
                JSONObject jsonObjectDepartment=jsonObject1.getJSONObject("department");
                String departmentname=jsonObjectDepartment.getString("name");
                JSONObject jsonObjectStatus=jsonObject1.getJSONObject("status_type_id");
                String statusName=jsonObjectStatus.getString("name");
                Attachedproblem attachedproblem=new Attachedproblem(problemId,subject,from,impact,statusName,departmentname);
                problemListAttached.add(attachedproblem);
                bottomNavigation.setNotification(problemcount+"",0);
                //String data=jsonObject.getString("data");
//                if (data.equals(null)||data.equals("null")){
//                textViewEmptyView.setVisibility(View.VISIBLE);
//                }

            }catch (JSONException e){
//                problemCount.setText("0");
                problemcount=0;
                bottomNavigation.setNotification(problemcount+"",0);
                e.printStackTrace();
            }


        }
    }


//        View header=navigationView.getHeaderView(0);
//        headerTitle=header.findViewById(R.id.nav_header_textView);
        //headerTitle.setText("#"+ticketNumber);
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        int id=menuItem.getItemId();
//                        //mDrawerLayout.closeDrawers();
//                        if (id == R.id.asset) {//DO your stuff }
//                            mDrawerLayout.closeDrawer(GravityCompat.END);
//
//                            }
//                        else if (id==R.id.problem){
//                            mDrawerLayout.closeDrawer(GravityCompat.END);
//                            Intent intent=new Intent(TicketDetailActivity.this,AssociatedProblem.class);
//                            Prefs.putString("cameFromMain","False");
//                            startActivity(intent);
//                        }
//
//                        return true;
//                    }
//                });



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main_new, menu);
        for (int i=0;i<statusItems.size();i++){
            Data data=statusItems.get(i);
            menu.add(data.getName());
        }

//        mToolbar.getMenu();


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id1 = item.getItemId();
        if (id1 == R.id.buttonsave) {
            Intent intent = new Intent(TicketDetailActivity.this, TicketSaveActivity.class);
            intent.putExtra("ticket_id", ticketID);
            startActivity(intent);
        }
        else{
            for (int i=0;i<statusItems.size();i++){
                Data data=statusItems.get(i);
                if (data.getName().equals(item.toString())){
                    id=data.getID();
                    Log.d("ID",""+id);
                }
            }
            try {
                status = Prefs.getString("ticketstatus", null);
                if (status.equalsIgnoreCase(item.toString())) {
                    Toasty.warning(TicketDetailActivity.this, "Ticket is already in " + item.toString() + " state", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(TicketDetailActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle(getString(R.string.changingStatus));

                    // Setting Dialog Message
                    alertDialog.setMessage(getString(R.string.statusConfirmation));

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.mipmap.ic_launcher);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke YES event
                            //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                            new StatusChange(Integer.parseInt(ticketID), id).execute();
                            progressDialog.show();
                            progressDialog.setMessage(getString(R.string.pleasewait));

                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        Log.d("item", String.valueOf(item));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fab_reply:
                Intent intent=new Intent(TicketDetailActivity.this,TicketReplyActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_internalnote:
                Intent intent1=new Intent(TicketDetailActivity.this,InternalNoteActivity.class);
                startActivity(intent1);
                Log.d("Raj", "Fab 1");
                break;

        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

//    public void animateFAB(){
//
//        if(fabSpeedDial.isMenuOpen()){
//
//            view.setVisibility(View.VISIBLE);
//            isFabOpen = false;
//            Log.d("Raj", "close");
//
//        } else {
//            view.setVisibility(View.GONE);
//            isFabOpen = true;
//            Log.d("Raj","open");
//
//        }
//    }

//    /**
//     * Handling the back button.
//     *
//     * @param item refers to the menu item .
//     * @return
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // handle arrow click here
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed(); // close this activity and return to preview activity (if there is any)
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    private void getCreateRequest() {
//        final CharSequence[] items = {"Reply", "Internal notes"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(TicketDetailActivity.this);
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                dialog.dismiss();
//                if (items[item].equals("Reply")) {
//                    cx = (int) fabAdd.getX() + dpToPx(40);
//                    cy = (int) fabAdd.getY();
//                    fabExpanded = true;
//                    fabAdd.hide();
//                    enterReveal("Reply");
//                } else {
//                    cx = (int) fabAdd.getX() + dpToPx(40);
//                    cy = (int) fabAdd.getY();
//                    fabExpanded = true;
//                    fabAdd.hide();
//                    enterReveal("Internal notes");
//                }
//            }
//        });
//        builder.show();
//    }

    /**
     * Async task for changing the status of the ticket.
     */
    private class StatusChange extends AsyncTask<String, Void, String> {
        int ticketId, statusId;

        StatusChange(int ticketId, int statusId) {

            this.ticketId = ticketId;
            this.statusId = statusId;

        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().postStatusChanged(ticketId, statusId);
            //return new Helpdesk().postStatusChanged(ticketId,statusId);
        }

        protected void onPostExecute(String result) {
            dialog1.dismiss();
            //progressDialog.dismiss();
//            if (result == null) {
//                Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                return;
            try {
                String state = Prefs.getString("403", null);
                if (state.equals("403") && !state.equals("null")) {
                    Toasty.warning(TicketDetailActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try{
                JSONObject jsonObject=new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("message");
                for (int i=0;i<jsonArray.length();i++){
                    String message=jsonArray.getString(i);
                    if (message.contains("Permission denied")){
                        Toasty.warning(TicketDetailActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                        Prefs.putString("403", "null");
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//                if (message1.contains("The ticket id field is required.")){
//                    Toasty.warning(TicketDetailActivity.this, getString(R.string.please_select_ticket), Toast.LENGTH_LONG).show();
//                }
//                else if (message1.contains("The status id field is required.")){
//                    Toasty.warning(TicketDetailActivity.this, getString(R.string.please_select_status), Toast.LENGTH_LONG).show();
//                }
//               else
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                JSONObject jsonObject1 = jsonObject.getJSONObject("response");
//                JSONArray jsonArray = jsonObject1.getJSONArray("message");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    String message = jsonArray.getString(i);
//                    if (message.equals("Permission denied, you do not have permission to access the requested page.")) {
//                        Toasty.warning(TicketDetailActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
//                        Prefs.putString("403", "null");
//                        return;
//                    }
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

//            }
            try {

                JSONObject jsonObject = new JSONObject(result);
                //JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                //JSONObject jsonObject2=jsonObject.getJSONObject("error");
                //String message1=jsonObject2.getString("ticket_id");
                String message2 = jsonObject.getString("message");


//                if (message2.contains("permission denied")&&Prefs.getString("403",null).equals("403")){
//
//                }
                if (!message2.equals("null")){
                    Toasty.success(TicketDetailActivity.this, getString(R.string.successfullyChanged), Toast.LENGTH_LONG).show();
                    Prefs.putString("ticketstatus", "Deleted");
                    finish();
                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
                }

//                if (message2.contains("Status changed to Deleted")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_deleted), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Deleted");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                } else if (message2.contains("Status changed to Open")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_opened), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Open");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                } else if (message2.contains("Status changed to Closed")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_closed), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Closed");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                } else if (message2.contains("Status changed to Resolved")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_resolved), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Resolved");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();

            }


        }


    }

//public void fabOpen(){
//        if (fabSpeedDial.isMenuOpen()){
//            viewPager.setVisibility(View.GONE);
//        }
//        else{
//            viewPager.setVisibility(View.VISIBLE);
//        }
//}
    /**
     * Here we are initializing the view pager
     * for the conversation and detail fragment.
     */
    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentConversation = new Conversation();
        fragmentDetail = new Detail();
        adapter.addFragment(fragmentConversation, getString(R.string.conversation));
        adapter.addFragment(fragmentDetail, getString(R.string.detail));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition(), true);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //fabSpeedDial.setRotation(positionOffset * 180.0f);

        }
        /**
         * This method is for controlling the FAB button.
         * @param position of the FAB button.
         */
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    //fabSpeedDial.show();
                    break;

                case 1:
                    //fabSpeedDial.hide();
                    break;
                default:
                    //fabSpeedDial.show();
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Here we are controlling the FAB reply and internal note option.
     *
     * @param type
     */
//    void enterReveal(String type) {
//        fab.setVisibility(View.GONE);
//        final View myView = findViewById(R.id.reveal);
//        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());
//        SupportAnimator anim =
//                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
//        if (type.equals("Reply")) {
//            myView.setVisibility(View.VISIBLE);
//            myView.findViewById(R.id.section_reply).setVisibility(View.VISIBLE);
//            myView.findViewById(R.id.section_internal_note).setVisibility(View.GONE);
//            overlay.setVisibility(View.VISIBLE);
//        } else {
//            myView.setVisibility(View.VISIBLE);
//            myView.findViewById(R.id.section_reply).setVisibility(View.GONE);
//            myView.findViewById(R.id.section_internal_note).setVisibility(View.VISIBLE);
//            overlay.setVisibility(View.VISIBLE);
//        }
//
//        anim.start();
//    }

//    void exitReveal() {
//
//        View myView = findViewById(R.id.reveal);
//        fab.show();
//        fabExpanded = false;
//        myView.setVisibility(View.GONE);
//        overlay.setVisibility(View.GONE);
//        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());
//        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.setDuration(300);
//        animator = animator.reverse();
//        animator.addListener(new SupportAnimator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart() {
//
//            }
//
//            @Override
//            public void onAnimationEnd() {
//                fab.show();
//                fabExpanded = false;
//                myView.setVisibility(View.GONE);
//               // overlay.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel() {
//
//            }
//
//            @Override
//            public void onAnimationRepeat() {
//
//            }
//
//        });
//        animator.start();

    //}

//    public int dpToPx(int dp) {
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//    }

    /**
     * Handling the back button here.
     */
    @Override
    public void onBackPressed() {
        finish();
//        if(mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
//            //drawer is open
//            mDrawerLayout.closeDrawers();
//        }
//        else{
//            finish();
//        }

//        Log.d("cameFromnotification",Prefs.getString("cameFromNotification",null));
//        String option=Prefs.getString("cameFromNotification",null);
//
//
//        switch (option) {
//            case "true": {
//                Intent intent = new Intent(TicketDetailActivity.this, NotificationActivity.class);
//                startActivity(intent);
//                break;
//            }
//            case "none": {
//                //finish();
//                Intent intent1=new Intent(TicketDetailActivity.this,SearchActivity.class);
//                startActivity(intent1);
//                break;
//            }
//            case "false": {
//                Intent intent1=new Intent(TicketDetailActivity.this,MainActivity.class);
//                startActivity(intent1);
//                finish();
//                break;
//            }
//            default: {
//                Intent intent1=new Intent(TicketDetailActivity.this,MainActivity.class);
//                startActivity(intent1);
//                finish();
//                break;
//            }
//        }
    }

    /**
     * While resuming it will check if the internet
     * is available or not.
     */
    @Override
    protected void onResume() {
        Prefs.putString("filePath","");
        checkConnection();
        Log.d("inOnResume","called");

        if (Prefs.getString("cameFromNewProblem",null).equals("true")){
            finish();
            startActivity(getIntent());
        }
        else{

        }

        super.onResume();


        //setupFab();
//        fab.bringToFront();

    }

    @Override
    protected void onDestroy() {
        isShowing = false;
        super.onDestroy();
        }
    private void checkConnection() {
        boolean isConnected = InternetReceiver.isConnected();
        showSnackIfNoInternet(isConnected);
    }

    /**
     * Display the snackbar if network connection is not there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnackIfNoInternet(boolean isConnected) {
        if (!isConnected) {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.sry_not_connected_to_internet, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

    }

    /**
     * Display the snackbar if network connection is there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnack(boolean isConnected) {

        if (isConnected) {

            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.connected_to_internet, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else {
            showSnackIfNoInternet(false);
        }

    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        showSnack(event.message);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private class FetchTicketDetail extends AsyncTask<String, Void, String> {
        String ticketID;
        String agentName;
        String title;
        FetchTicketDetail(String ticketID) {

            this.ticketID = ticketID;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getTicketDetail(ticketID);
        }

        protected void onPostExecute(String result) {
            //progressBar.setVisibility(View.GONE);
            if (isCancelled()) return;
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

            if (result == null) {
                //Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONObject jsonObject2=jsonObject1.getJSONObject("ticket");
                String ticketNumber=jsonObject2.getString("ticket_number");
                String statusName=jsonObject2.getString("status_name");
                String subject=jsonObject2.getString("title");
                String department=jsonObject2.getString("dept_name");
                String priorityColor=jsonObject2.getString("priority_color");
                String source=jsonObject2.getString("source_name");
                switch (source) {
                    case "Chat": {
                        int color = Color.parseColor(priorityColor);
                        imageViewSource.setImageResource(R.drawable.ic_chat_bubble_outline_black_24dp);
                        imageViewSource.setColorFilter(color);
                        break;
                    }
                    case "Web": {
                        int color = Color.parseColor(priorityColor);
                        imageViewSource.setImageResource(R.drawable.web_design);
                        imageViewSource.setColorFilter(color);
                        break;
                    }
                    case "Agent": {
                        int color = Color.parseColor(priorityColor);
                        imageViewSource.setImageResource(R.drawable.mail);
                        imageViewSource.setColorFilter(color);
                        break;
                    }
                    case "Email": {
                        int color = Color.parseColor(priorityColor);
                        imageViewSource.setImageResource(R.drawable.mail);
                        imageViewSource.setColorFilter(color);
                        break;
                    }
                    case "Facebook": {
                        int color = Color.parseColor(priorityColor);
                        imageViewSource.setImageResource(R.drawable.facebook);
                        imageViewSource.setColorFilter(color);
                        break;
                    }
                    case "Twitter": {
                        int color = Color.parseColor(priorityColor);
                        imageViewSource.setImageResource(R.drawable.twitter);
                        imageViewSource.setColorFilter(color);
                        break;
                    }
                    case "Call": {
                        int color = Color.parseColor(priorityColor);
                        imageViewSource.setImageResource(R.drawable.phone);
                        imageViewSource.setColorFilter(color);
                        break;
                    }
                    default:
                        imageViewSource.setVisibility(View.GONE);
                        break;
                }
                if (!priorityColor.equals("")||!priorityColor.equals("null")){
                    //viewpriority.setBackgroundColor(Color.parseColor(priorityColor));
                    viewCollapsePriority.setBackgroundColor(Color.parseColor(priorityColor));
                }
                else{
                    //viewpriority.setVisibility(View.GONE);
                    viewCollapsePriority.setVisibility(View.GONE);
                }
                JSONObject jsonObject3=jsonObject2.getJSONObject("from");
                String userName = jsonObject3.getString("first_name")+" "+jsonObject3.getString("last_name");
                if (userName.equals("")||userName.equals("null null")||userName.equals(" ")){
                    userName=jsonObject3.getString("user_name");
                    textviewAgentName.setText(userName);
                }
                else{
                    userName=jsonObject3.getString("first_name")+" "+jsonObject3.getString("last_name");
                    textviewAgentName.setText(userName);
                }
                if (!statusName.equals("null")||!statusName.equals("")){
                    textViewStatus.setText(statusName);
                }
                else{
                    textViewStatus.setVisibility(View.GONE);
                }
                //textViewTitle.setText("#"+ticketNumber);
                if (subject.startsWith("=?")){
                    title=subject.replaceAll("=?UTF-8?Q?","");
                    String newTitle=title.replaceAll("=E2=80=99","");
                    String second1=newTitle.replace("=C3=BA","");
                    String third = second1.replace("=C2=A0", "");
                    String finalTitle=third.replace("=??Q?","");
                    String newTitle1=finalTitle.replace("?=","");
                    String newTitle2=newTitle1.replace("_"," ");
                    Log.d("new name",newTitle2);
                    textViewSubject.setText(newTitle2);
                    textViewDemo.setText(ticketNumber);
                }
                else if (!subject.equals("null")){
                    textViewSubject.setText(subject);
                    textViewDemo.setText(ticketNumber);
                }
                else if (subject.equals("null")){
                    textViewDemo.setText(ticketNumber);
                }
                if (!department.equals("")||!department.equals("null")){
                    textViewDepartment.setText(department);
                }
                else{
                    textViewDepartment.setVisibility(View.GONE);
                }

                Log.d("TITLE",subject);
                Log.d("TICKETNUMBER",ticketNumber);
                //String priority=jsonObject1.getString("priority_id");





            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class MyBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener {

        Context context;
        TextView associate;

        MyBottomSheetDialog(@NonNull Context context) {
            super(context);
            this.context = context;
            create();
        }

        public void create() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
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

            associate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

        @Override
        public void onClick(View view) {


        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}