package co.servicedesk.faveo.pro.frontend.activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.gordonwong.materialsheetfab.DimOverlayFrameLayout;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.haha.perflib.Main;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import butterknife.ButterKnife;
import co.servicedesk.faveo.pro.SharedPreference;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.frontend.drawers.FragmentDrawer;
import co.servicedesk.faveo.pro.frontend.fragments.About;
import co.servicedesk.faveo.pro.frontend.fragments.ClientList;
import co.servicedesk.faveo.pro.frontend.fragments.Settings;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.ClosedTickets;

import co.servicedesk.faveo.pro.frontend.fragments.tickets.CreatedAtAsc;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.CreatedAtDesc;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.DueByAsc;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.DueByDesc;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.InboxTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.MyTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.SortByTicketNumberAscending;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.SortByTicketNumberDescending;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.SortByTicketPriorityAsc;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.SortByTicketPriorityDesc;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.TrashTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.UnassignedTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.UpdatedAtAsc;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.UpdatedAtDesc;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.MessageEvent;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * This is the main activity where we are loading the inbox fragment
 * once log in success we will start this activity. Here we are loading the
 * navigation drawer item.
 */
public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener,
        ClosedTickets.OnFragmentInteractionListener,
        InboxTickets.OnFragmentInteractionListener,
        MyTickets.OnFragmentInteractionListener,
        TrashTickets.OnFragmentInteractionListener,
        UnassignedTickets.OnFragmentInteractionListener,
        About.OnFragmentInteractionListener,
        ClientList.OnFragmentInteractionListener,
        Settings.OnFragmentInteractionListener,UpdatedAtDesc.OnFragmentInteractionListener,
        UpdatedAtAsc.OnFragmentInteractionListener,DueByAsc.OnFragmentInteractionListener,DueByDesc.OnFragmentInteractionListener,
        SortByTicketNumberAscending.OnFragmentInteractionListener,SortByTicketNumberDescending.OnFragmentInteractionListener,
        SortByTicketPriorityAsc.OnFragmentInteractionListener,SortByTicketPriorityDesc.OnFragmentInteractionListener,CreatedAtAsc.OnFragmentInteractionListener,CreatedAtDesc.OnFragmentInteractionListener{

    // The BroadcastReceiver that tracks network connectivity changes.
//    public InternetReceiver receiver = new InternetReceiver();

    protected boolean doubleBackToExitPressedOnce = false;
    public static boolean isShowing = false;
    ArrayList<String> strings;
    ArrayList<String> strings1;
    Toolbar toolbar;
    Context context;
    FrameLayout rootLayout;
    FabSpeedDial fab;
    DimOverlayFrameLayout dimOverlayFrameLayout;
    private SharedPreference sharedPreferenceObj;
    //    private ArrayList<String> mList = new ArrayList<>();
//    @BindView(R.id.sort_view)
//    RelativeLayout sortView;
//    @BindView(R.id.sorting_type_textview)
//    TextView sortTextview;
//    @BindView(R.id.arrow_imgView)
//    ImageView arrowDown;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceObj=new SharedPreference(MainActivity.this);
        isShowing = true;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        fab = (FabSpeedDial) findViewById(R.id.fab_main);
        dimOverlayFrameLayout = (DimOverlayFrameLayout) findViewById(R.id.dimOverlay);
        Window window = MainActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.faveo));
        ButterKnife.bind(this);


//        Log.d("firebaseToken",FirebaseInstanceId.getInstance().getToken());
        rootLayout = (FrameLayout) findViewById(R.id.root_layout);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Prefs.putString("querry1", "null");
        strings = new ArrayList<>();
        strings.add(0, "Sort by");
        strings.add(1, "Due by time");
        strings.add(2, "Priority");
        strings.add(3, "Created at");
        strings.add(4, "Updated at");
        strings.add(5, "Ticket title");
        strings.add(6, "Status");
//        Prefs.putString("came from filter","false");

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        fab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id=menuItem.getItemId();

                if (id==R.id.fab_createTicket){
                    Intent intent=new Intent(MainActivity.this,CreateTicketActivity.class);
                    startActivity(intent);
                }
                else if (id==R.id.fab_createProblem){
                    Intent intent=new Intent(MainActivity.this,NewProblem.class);
                    startActivity(intent);
                }
                else if (id==R.id.fab_addAsset){

                }
                //TODO: Start some activity
                return false;
            }
        });
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_inbox);
        mToolbar.setNavigationIcon(R.drawable.ic_action_attach_file);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


//        if (savedInstanceState == null) {
//            rootLayout.setVisibility(View.INVISIBLE);
//
//            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
//            if (viewTreeObserver.isAlive()) {
//                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        circularRevealActivity();
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                        } else {
//                            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        }
//                    }
//
//                    private void circularRevealActivity() {
//                        int cx = rootLayout.getWidth() / 2;
//                        int cy = rootLayout.getHeight() / 2;
//
//                        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());
//
//                        // create the animator for this view (the start radius is zero)
//                        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
//                        circularReveal.setDuration(3000);
//
//                        // make the view visible and start the animation
//                        rootLayout.setVisibility(View.VISIBLE);
//                        circularReveal.start();
//                    }
//                });
//
//            }
//        }
        Prefs.putString("querry1", "null");


//Initializing the bottomNavigationView
//        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setBackgroundColor(Color.parseColor("#cee0ef"));
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.action_call:
//                                Toast.makeText(MainActivity.this, "call clicked", Toast.LENGTH_SHORT).show();
//                                break;
//                            case R.id.action_chat:
//                                Toast.makeText(MainActivity.this, "chat clicked", Toast.LENGTH_SHORT).show();
//                                break;
//                            case R.id.action_contact:
//                                Toast.makeText(MainActivity.this, "contact clicked", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                        return false;
//                    }
//                });


//        getSupportActionBar().setTitle("Inbox");

        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        //mToolbar.setVisibility(View.GONE);

        /*
          Loading the inbox fragment here.
         */
        InboxTickets inboxTickets = new InboxTickets();
        //inboxTickets.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, inboxTickets);
        fragmentTransaction.commit();
        setActionBarTitle(getResources().getString(R.string.inbox));
//        TapTargetView.showFor(this,                 // `this` is an Activity
//                TapTarget.forView(findViewById(R.id.fab_main), "This is a FAB", "From here you can create ticket,make some changes and request for an item")
//                        // All options below are optional
//                        .outerCircleColor(R.color.faveo)      // Specify a color for the outer circle
//                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
//                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
//                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
//                        .titleTextColor(R.color.white)      // Specify the color of the title text
//                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
//                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
//                        .textColor(R.color.blue)            // Specify a color for both the title and description text
//                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
//                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
//                        .drawShadow(true)                   // Whether to draw a drop shadow or not
//                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
//                        .tintTarget(true)                   // Whether to tint the target view's color
//                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
//                        //.icon(R.drawable.ic_action_attach_file)                     // Specify a custom drawable to draw as the target
//                        .targetRadius(60),                  // Specify the target radius (in dp)
//                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
//                    @Override
//                    public void onTargetClick(TapTargetView view) {
//                        super.onTargetClick(view);      // This call is optional
//                        //doSomething();
//                    }
//                });

        // We load a drawable and create a location to show a tap target here
        // We need the display to get the width and height at this point in time
//        final Display display = getWindowManager().getDefaultDisplay();
//        // Load our little droid guy
//        final Drawable droid = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
//        // Tell our droid buddy where we want him to appear
//        final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
//        // Using deprecated methods makes you look way cool
//        droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);
//
//        final SpannableString sassyDesc = new SpannableString("It allows you to go back, sometimes");
//        sassyDesc.setSpan(new StyleSpan(Typeface.ITALIC), sassyDesc.length() - "sometimes".length(), sassyDesc.length(), 0);
//
//        // We have a sequence of targets, so lets build it!
//        final TapTargetSequence sequence = new TapTargetSequence(this)
//                .targets(
//
//                        // This tap target will target the back button, we just need to pass its containing toolbar
//                        TapTarget.forToolbarNavigationIcon(mToolbar, "This is the hamburger icon,from here you can control the app.You will get option to create ticket,access the settings and support page and also you will get option to log out from the app.", sassyDesc).id(1)
//                                .dimColor(android.R.color.black)
//                                .outerCircleColor(R.color.faveo)
//                                .targetCircleColor(android.R.color.white)
//                                .transparentTarget(true)
//                                .textColor(android.R.color.white).cancelable(false),
//                        // Likewise, this tap target will target the search button
//                        TapTarget.forToolbarMenuItem(mToolbar, R.id.actionsearch, "This is a search icon", "From here you will be able to search ticket and user in your helpdesk.")
//                                .dimColor(android.R.color.black)
//                                .outerCircleColor(R.color.faveo)
//                                .targetCircleColor(android.R.color.white)
//                                .transparentTarget(true)
//                                .textColor(android.R.color.white)
//                                .id(2).cancelable(false),
//                        TapTarget.forToolbarMenuItem(mToolbar, R.id.action_noti, "This is a notification icon", "You will get all the notification in your helpdesk from here.")
//                                .dimColor(android.R.color.black)
//                                .outerCircleColor(R.color.faveo)
//                                .targetCircleColor(android.R.color.white)
//                                .transparentTarget(true)
//                                .textColor(android.R.color.white)
//                                .id(3).cancelable(false)
//                )
//                .listener(new TapTargetSequence.Listener() {
//                    // This listener will tell us when interesting(tm) events happen in regards
//                    // to the sequence
//                    @Override
//                    public void onSequenceFinish() {
//                        //((TextView) findViewById(R.id.educated)).setText("Congratulations! You're educated now!");
//                    }
//
//                    @Override
//                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
//                    }
//
//                    @Override
//                    public void onSequenceCanceled(TapTarget lastTarget) {
////                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
////                                .setTitle("Uh oh")
////                                .setMessage("You canceled the sequence")
////                                .setPositiveButton("Oops", null).show();
////                        TapTargetView.showFor(dialog,
////                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
////                                        .cancelable(false)
////                                        .tintTarget(false), new TapTargetView.Listener() {
////                                    @Override
////                                    public void onTargetClick(TapTargetView view) {
////                                        super.onTargetClick(view);
////                                        dialog.dismiss();
////                                    }
////                                });
//                    }
//                });
//        if(sharedPreferenceObj.getApp_runFirst().equals("FIRST"))
//        {
//            // That's mean First Time Launch
//            // After your Work , SET Status NO
//            TapTargetView.showFor(this,                 // `this` is an Activity
//                    TapTarget.forView(findViewById(R.id.fab_main), "This is a FAB", "From here you can create ticket,or you can edit your profile")
//                            // All options below are optional
//                            .dimColor(android.R.color.black)
//                            .outerCircleColor(R.color.faveo)
//                            .targetCircleColor(android.R.color.white)
//                            .textColor(android.R.color.white).cancelable(false),                  // Specify the target radius (in dp)
//                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
//                        @Override
//                        public void onTargetClick(TapTargetView view) {
//                            super.onTargetClick(view);      // This call is optional
//                            sequence.start();
//                        }
//                    });
//
//            sharedPreferenceObj.setApp_runFirst("NO");
//        }
//        else
//        {
//
//            // App is not First Time Launch
//        }
    }

    @Override
    protected void onDestroy() {
        isShowing = false;
        super.onDestroy();
        }

//    @OnClick(R.id.sort_view)
//    public void onClickSort() {
//        arrowDown.animate().rotation(180).start();
//
//        new BottomSheet.Builder(this).title("Sort by").sheet(R.menu.sort_menu).listener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case R.id.action_date:
//                        sortTextview.setText("Due by date");
//                        break;
//                    case R.id.action_time:
//                        sortTextview.setText("Due by time");
//                        break;
//                    case R.id.action_status:
//                        sortTextview.setText("Status");
//                        break;
//                    case R.id.action_priority:
//                        sortTextview.setText("Priority");
//                        break;
//                }
//            }
//        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                arrowDown.animate().rotation(0).start();
//            }
//        }).show();
//
//    }

    /**
     * This will handle the drawer item.
     * @param view
     * @param position
     */
    @Override
    public void onDrawerItemSelected(View view, int position) {
    }

    public void setActionBarTitle(final String title) {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.title);
        mTitle.setText(title.toUpperCase());

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_inbox, menu);
//        return true;
//    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item items refer to the menu items.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_search) {
//            startActivity(new Intent(MainActivity.this, SearchActivity.class));
//            return true;
//        }

        if (id == R.id.action_noti) {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * While resuming it will check if the internet
     * is available or not.
     */
    @Override
    protected void onResume() {
        Log.d("OnResumeMainActivity","TRUE");
        Prefs.putString("cameFromNotification","false");
        Prefs.putString("ticketThread","");
        Prefs.putString("TicketRelated","");
        Prefs.putString("searchResult", "");
        Prefs.putString("searchUser","");
        checkConnection();
        super.onResume();
        // register connection status listener
        //FaveoApplication.getInstance().setInternetListener(this);

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

//    /**
//     * Callback will be triggered when there is change in
//     * network connection
//     */
//    @Override
//    public void onNetworkConnectionChanged(boolean isConnected) {
//        showSnack(isConnected);
//    }

    /**
     * Handling the back button here.
     * As if we clicking twice then it will
     * ask press one more time to exit,we are handling
     * the double back button pressing here.
     */
    @Override
    public void onBackPressed() {
        new BottomDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.confirmLogOut))
                .setContent(getString(R.string.confirmMessage))
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
                        finish();
                    }
                }).onNegative(new BottomDialog.ButtonCallback() {
            @Override
            public void onClick(@NonNull BottomDialog bottomDialog) {
                bottomDialog.dismiss();
            }
        })
                .show();
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Snackbar.make(findViewById(android.R.id.content), R.string.press_again_exit, Snackbar.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2500);
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
//        Snackbar.make(findViewById(android.R.id.content), event.message, Snackbar.LENGTH_LONG).show();
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

}