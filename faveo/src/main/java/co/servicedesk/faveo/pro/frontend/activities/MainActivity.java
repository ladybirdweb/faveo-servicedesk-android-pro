package co.servicedesk.faveo.pro.frontend.activities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
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
    FloatingActionButton fab;
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
        fab = (FloatingActionButton) findViewById(R.id.fab_main);
        dimOverlayFrameLayout = (DimOverlayFrameLayout) findViewById(R.id.dimOverlay);
        Window window = MainActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.mainActivityTopBar));
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,TicketFilter.class);
                startActivity(intent);
            }
        });
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_inbox);
        mToolbar.setNavigationIcon(R.drawable.ic_action_attach_file);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Prefs.putString("querry1", "null");

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


        final Display display = getWindowManager().getDefaultDisplay();
        // Load our little droid guy
        final Drawable droid = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        // Tell our droid buddy where we want him to appear
        final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
        // Using deprecated methods makes you look way cool
        droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);
        final Typeface typeface = ResourcesCompat.getFont(this, R.font.hintedlainesansregular);
        final SpannableString sassyDesc = new SpannableString("It allows you to go back, sometimes");
        sassyDesc.setSpan(new StyleSpan(Typeface.NORMAL), sassyDesc.length() - "sometimes".length(), sassyDesc.length(), 0);

        // We have a sequence of targets, so lets build it!
        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        // This tap target will target the back button, we just need to pass its containing toolbar
                        // Likewise, this tap target will target the search button
                        // `this` is an Activity
                        TapTarget.forToolbarNavigationIcon(mToolbar, "This is the hamburger icon,from here you can control the app.You will get option to create ticket,view all the tickets,access the settings and support page and also you will get option to log out from the app.").id(1)
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.faveo)
                                .textTypeface(typeface)
                                .targetCircleColor(android.R.color.white)
                                .transparentTarget(true)
                                .textColor(android.R.color.white).cancelable(false).id(2),                 // Specify the target radius (in dp)

                        TapTarget.forToolbarMenuItem(mToolbar, R.id.actionsearch, "This is a search icon from here you will be able to search tickets and users in FAVEO.")
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.faveo)
                                .targetCircleColor(android.R.color.white)
                                .transparentTarget(true)
                                .textTypeface(typeface)
                                .textColor(android.R.color.white)
                                .id(3).cancelable(false),
                        TapTarget.forToolbarMenuItem(mToolbar, R.id.action_noti, "This is a notification icon you will get all the latest updates of your tickets from here.")
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.faveo)
                                .textTypeface(typeface)
                                .targetCircleColor(android.R.color.white)
                                .transparentTarget(true)
                                .textColor(android.R.color.white)
                                .id(4).cancelable(false)
                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme)
                                //.setMessage(getString(R.string.intro);
                                .setPositiveButton("Ok", null).show();
                        TapTargetView.showFor(dialog,
                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), getString(R.string.intro))
                                        .cancelable(false)
                                        .outerCircleColor(R.color.faveo)
                                        .textColor(android.R.color.white)
                                        .textTypeface(typeface)
                                        .dimColor(android.R.color.black)
                                        .tintTarget(false), new TapTargetView.Listener() {
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);
                                        dialog.dismiss();
                                    }
                                });



//                        final BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
//                        dialog.setContentView(R.layout.bottom_custom_view);
//                        Button button=dialog.findViewById(R.id.continueExploring);
//                        button.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                dialog.cancel();
//                            }
//                        });
//                        dialog.show();

//                        new BottomDialog.Builder(MainActivity.this)
//                                .setContent(R.string.intro)
//                                .setPositiveText("ok")
//                                .setPositiveBackgroundColor(R.color.faveo)
//                                .setPositiveTextColor(R.color.colorAccent)
//                                .setCancelable(false)
//                                .setPositiveBackgroundColorResource(R.color.white)
//                                //.setPositiveBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)
//                                .setPositiveTextColorResource(R.color.faveo)
//                                //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
//                                .onPositive(new BottomDialog.ButtonCallback() {
//                                    @Override
//                                    public void onClick(BottomDialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
//                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
//                                .setTitle("Uh oh")
//                                .setMessage("You canceled the sequence")
//                                .setPositiveButton("Oops", null).show();
//                        TapTargetView.showFor(dialog,
//                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
//                                        .cancelable(false)
//                                        .tintTarget(false), new TapTargetView.Listener() {
//                                    @Override
//                                    public void onTargetClick(TapTargetView view) {
//                                        super.onTargetClick(view);
//                                        dialog.dismiss();
//                                    }
//                                });
                    }
                });

        if (sharedPreferenceObj.getApp_runFirst().equals("FIRST")) {
            // That's mean First Time Launch
            // After your Work , SET Status NO
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.fab_main), "This is the FAB from here you will get the option to filter the tickets in FAVEO based upon agent name,department,source,priority and many more.").id(1)
                            .dimColor(android.R.color.black)
                            .outerCircleColor(R.color.faveo)
                            .textTypeface(typeface)
                            .targetCircleColor(android.R.color.white)
                            .transparentTarget(true)
                            .textColor(android.R.color.white).cancelable(false),                 // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            sequence.start();
                        }
                    });

            sharedPreferenceObj.setApp_runFirst("NO");
        } else {

            // App is not First Time Launch
        }

    }

    @Override
    protected void onDestroy() {
        isShowing = false;
        super.onDestroy();
        }


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