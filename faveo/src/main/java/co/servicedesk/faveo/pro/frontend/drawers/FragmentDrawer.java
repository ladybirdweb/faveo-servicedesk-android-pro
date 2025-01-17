package co.servicedesk.faveo.pro.frontend.drawers;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.servicedesk.faveo.pro.CircleTransform;
import co.servicedesk.faveo.pro.Constants;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.UIUtils;
import co.servicedesk.faveo.pro.UiUtilsServicedesk;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.activities.CreateChange;
import co.servicedesk.faveo.pro.frontend.activities.CreateTicketActivity;
import co.servicedesk.faveo.pro.frontend.activities.ExistingChanges;
import co.servicedesk.faveo.pro.frontend.activities.ExistingProblems;
import co.servicedesk.faveo.pro.frontend.activities.LoginActivity;
import co.servicedesk.faveo.pro.frontend.activities.MainActivity;
import co.servicedesk.faveo.pro.frontend.activities.NewProblem;
import co.servicedesk.faveo.pro.frontend.activities.SettingsActivity;
import co.servicedesk.faveo.pro.frontend.adapters.DrawerItemCustomAdapter;
import co.servicedesk.faveo.pro.frontend.adapters.ProblemListAdapter;
import co.servicedesk.faveo.pro.frontend.fragments.About;
import co.servicedesk.faveo.pro.frontend.fragments.ClientList;
import co.servicedesk.faveo.pro.frontend.fragments.ConfirmationDialog;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.ClosedTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.InboxTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.MyTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.TrashTickets;
import co.servicedesk.faveo.pro.frontend.fragments.tickets.UnassignedTickets;
import co.servicedesk.faveo.pro.frontend.services.MyFirebaseInstanceIDService;
import co.servicedesk.faveo.pro.model.DataModel;
import co.servicedesk.faveo.pro.model.ServicedeskModule;
import es.dmoral.toasty.Toasty;

/**
 * This is the fragment where we are going to handle the
 * drawer item events,for create ticket ,inbox,client list...
 */
public class FragmentDrawer extends Fragment implements View.OnClickListener {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    View layout;
    Context context;
    DataModel[] drawerItem;
    ServicedeskModule[] servicedeskModulesProblem,servicedeskModulesChanges;
    DrawerItemCustomAdapter drawerItemCustomAdapter;
    ConfirmationDialog confirmationDialog;
    //int count=0;
    ProgressDialog progressDialog;
    String title;
    static String token;
    int responseCodeForShow;
    //    @BindView(R.id.inbox_count)
//    TextView inbox_count;
//    @BindView(R.id.my_tickets_count)
//    TextView my_tickets_count;
//    @BindView(R.id.trash_tickets_count)
//    TextView trash_tickets_count;
//    @BindView(R.id.unassigned_tickets_count)
//    TextView unassigned_tickets_count;
//    @BindView(R.id.closed_tickets_count)
//    TextView closed_tickets_count;
    @BindView(R.id.usernametv)
    TextView userName;
    @BindView(R.id.domaintv)
    TextView domainAddress;
    @BindView(R.id.roleTv)
    TextView userRole;
    @BindView(R.id.imageView_default_profile)
    ImageView profilePic;
    @BindView(R.id.listviewNavigation)
    ListView listView;
    @BindView(R.id.ticket_list)
    LinearLayout ticketList;
    ListView listViewProblem;
    ListView listViewChanges;
    ListView listViewChange;
    LinearLayout linearLayoutChange;
    LinearLayout linearLayoutProblem;
    View viewProblem,viewChange;
    ProblemListAdapter problemListAdapter,changeListAdapter;


    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

//    public static List<NavDrawerItem> getData() {
//        List<NavDrawerItem> data = new ArrayList<>();
//
//        for (String title : titles) {
//            NavDrawerItem navItem = new NavDrawerItem();
//            navItem.setTitle(title);
//            data.add(navItem);
//        }
//        return data;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = (new String[]{"Item1", "Item2", "Item3", "Item4"});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        listView= (ListView) layout.findViewById(R.id.listviewNavigation);
        listViewProblem=layout.findViewById(R.id.listviewProblems);
        listViewChange=layout.findViewById(R.id.listviewchange);
        linearLayoutChange=layout.findViewById(R.id.change);
        linearLayoutProblem=layout.findViewById(R.id.problems);
        viewProblem=layout.findViewById(R.id.problemView);
        viewChange=layout.findViewById(R.id.changeView);
        //listViewChange=layout.findViewById(R.id.listviewChanges);
        layout.findViewById(R.id.create_ticket).setOnClickListener(this);
        layout.findViewById(R.id.client_list).setOnClickListener(this);
        layout.findViewById(R.id.settings).setOnClickListener(this);
        layout.findViewById(R.id.about).setOnClickListener(this);
        layout.findViewById(R.id.logout).setOnClickListener(this);
        layout.findViewById(R.id.problems).setOnClickListener(this);
        //layout.findViewById(R.id.changesModule).setOnClickListener(this);
        drawerItem = new DataModel[5];
        servicedeskModulesProblem=new ServicedeskModule[2];
        servicedeskModulesChanges=new ServicedeskModule[2];
        servicedeskModulesProblem[0]=new ServicedeskModule(R.drawable.circle_name,getString(R.string.all_prob));
        servicedeskModulesProblem[1]=new ServicedeskModule(R.drawable.circle_name,getString(R.string.new_prob));
        servicedeskModulesChanges[0]=new ServicedeskModule(R.drawable.circle_name,getString(R.string.all_change));
        servicedeskModulesChanges[1]=new ServicedeskModule(R.drawable.circle_name,getString(R.string.new_change));
        problemListAdapter =new ProblemListAdapter(getActivity(),R.layout.list_servicedesk_row,servicedeskModulesProblem);
        changeListAdapter=new ProblemListAdapter(getActivity(),R.layout.list_servicedesk_row,servicedeskModulesChanges);
        listViewChange.setAdapter(changeListAdapter);
        changeListAdapter.notifyDataSetChanged();
        listViewProblem.setAdapter(problemListAdapter);
        progressDialog=new ProgressDialog(getActivity());
        problemListAdapter.notifyDataSetChanged();
        ButterKnife.bind(this, layout);
        confirmationDialog=new ConfirmationDialog();
        drawerItem[0] = new DataModel(R.drawable.inbox_tickets,getString(R.string.inbox),Prefs.getString("inboxTickets",null));
        drawerItem[1] = new DataModel(R.drawable.my_ticket,getString(R.string.my_tickets),Prefs.getString("myTickets", null));
        drawerItem[2] = new DataModel(R.drawable.unassigned_ticket,getString(R.string.unassigned_tickets),Prefs.getString("unassignedTickets",null));
        drawerItem[3] = new DataModel(R.drawable.closed_ticket,getString(R.string.closed_tickets),Prefs.getString("closedTickets", null));
        drawerItem[4] = new DataModel(R.drawable.trash_ticket ,getString(R.string.trash),Prefs.getString("trashTickets", null));
        drawerItemCustomAdapter=new DrawerItemCustomAdapter(getActivity(),R.layout.list_view_item_row,drawerItem);
        listView.setAdapter(drawerItemCustomAdapter);
        progressDialog=new ProgressDialog(getActivity());
        drawerItemCustomAdapter.notifyDataSetChanged();
        UIUtils.setListViewHeightBasedOnItems(listView);

        checkingServicePlugin();



/**
 * This is dynamic height adjusting on the basis of item in list view
 */
        //UIUtils.setListViewHeightBasedOnItems(listView);
        UiUtilsServicedesk.setListViewHeightBasedOnItems(listViewProblem);
        UiUtilsServicedesk.setListViewHeightBasedOnItems(listViewChange);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment=null;
                //Toast.makeText(context, "Clicked at :"+position, Toast.LENGTH_SHORT).show();
                if (position==0){
                    title = getString(R.string.inbox);
                    fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                    if (fragment == null)
                        fragment = new InboxTickets();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        // fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        ((MainActivity) getActivity()).setActionBarTitle(title);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }else if (position==1){
                    title = getString(R.string.my_tickets);
                    fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                    if (fragment == null)
                        fragment = new MyTickets();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        // fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        ((MainActivity) getActivity()).setActionBarTitle(title);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
                else if (position==2){
                    title = getString(R.string.unassigned_tickets);
                    fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                    if (fragment == null)
                        fragment = new UnassignedTickets();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        // fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        ((MainActivity) getActivity()).setActionBarTitle(title);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }else if (position==3){
                    title = getString(R.string.closed_tickets);
                    fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                    if (fragment == null)
                        fragment = new ClosedTickets();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        // fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        ((MainActivity) getActivity()).setActionBarTitle(title);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }else if (position==4){
                    title = getString(R.string.trash);
                    fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                    if (fragment == null)
                        fragment = new TrashTickets();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.commit();
                        ((MainActivity) getActivity()).setActionBarTitle(title);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }

            }
        });

        listViewProblem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    Intent intent1=new Intent(getContext(), ExistingProblems.class);
                    Prefs.putString("cameFromMain","True");
                    startActivity(intent1);
                }
                else if (i==1){
                    Intent intent1=new Intent(getContext(), NewProblem.class);
                    Prefs.putString("cameFromMain","True");
                    startActivity(intent1);
                }
            }
        });

        linearLayoutChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listViewChange.getVisibility()==View.VISIBLE){
                    listViewChange.setVisibility(View.GONE);
                }
                else{
                    listViewChange.setVisibility(View.VISIBLE);
                }
            }
        });

        listViewChange.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    Intent intent1=new Intent(getContext(), ExistingChanges.class);
                    startActivity(intent1);
                }
                else{
                    Intent intent1=new Intent(getContext(), CreateChange.class);
                    Prefs.putString("needToAttachChange","false");
                    startActivity(intent1);
                }
            }
        });
        try {
            String letter = Prefs.getString("profilePicture", null);
            Log.d("profilePicture", letter);
            if (letter.contains("jpg") || letter.contains("png") || letter.contains("jpeg")) {
                //profilePic.setColorFilter(getContext().getResources().getColor(R.color.white));
                //profilePic.setColorFilter(context.getResources().getColor(R.color.faveo), PorterDuff.Mode.SRC_IN);
                Picasso.with(context).load(letter).transform(new CircleTransform()).into(profilePic);
            }
            else {
                int color= Color.parseColor("#ffffff");
                String letter1 = String.valueOf(Prefs.getString("PROFILE_NAME", "").charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(letter1,color);
                profilePic.setColorFilter(context.getResources().getColor(R.color.faveo), PorterDuff.Mode.SRC_IN);
                profilePic.setImageDrawable(drawable);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        userRole.setText(Prefs.getString("ROLE", ""));
        domainAddress.setText(Prefs.getString("BASE_URL", ""));
        userName.setText(Prefs.getString("PROFILE_NAME", ""));

        ticketList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //count++;

                if (listView.getVisibility()==View.VISIBLE){
                    listView.setVisibility(View.GONE);
                }
                else{
                    listView.setVisibility(View.VISIBLE);
                }

            }
        });




        return layout;
    }

    public void checkingServicePlugin(){
        if (Prefs.getString("activated",null).equals("True")){
            viewProblem.setVisibility(View.VISIBLE);
            viewChange.setVisibility(View.VISIBLE);
            linearLayoutProblem.setVisibility(View.VISIBLE);
            linearLayoutChange.setVisibility(View.VISIBLE);

        }else{
            viewProblem.setVisibility(View.GONE);
            viewChange.setVisibility(View.GONE);
            linearLayoutProblem.setVisibility(View.GONE);
            linearLayoutChange.setVisibility(View.GONE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Whenever the this method is going to be called then the
     * async task will be cancelled .
     */
    @Override
    public void onStop() {
        // notice here that I keep a reference to the task being executed as a class member:
        if (this.new FetchDependency() != null && this.new FetchDependency().getStatus() == AsyncTask.Status.RUNNING)
            this.new FetchDependency().cancel(true);
        super.onStop();
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                new ServiceActive().execute();
                new SendPostRequest().execute();
                new FetchDependency().execute();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                new SendPostRequest().execute();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    private class ServiceActive extends AsyncTask<String,Void,String>{



        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().isSeriveDeskActivate();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Log.d("response", s);
                JSONObject jsonObject=new JSONObject(s);
                JSONObject data=jsonObject.getJSONObject("data");
                String plugin_status=data.getString("plugin_status");
                if (plugin_status.equals("true")){
                    Prefs.putString("activated","True");
                    viewProblem.setVisibility(View.VISIBLE);
                    viewChange.setVisibility(View.VISIBLE);
                    linearLayoutProblem.setVisibility(View.VISIBLE);
                    linearLayoutChange.setVisibility(View.VISIBLE);
                }
                else{
                    Prefs.putString("activated","False");
                    viewProblem.setVisibility(View.GONE);
                    viewChange.setVisibility(View.GONE);
                    linearLayoutProblem.setVisibility(View.GONE);
                    linearLayoutChange.setVisibility(View.GONE);
                }

                //checkingServicePlugin();
            }catch (NullPointerException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL(Constants.URL + "authenticate"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", Prefs.getString("USERNAME", null));
                postDataParams.put("password", Prefs.getString("PASSWORD", null));
                Log.e("params",postDataParams.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                //MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.d("ifresponseCode",""+responseCode);
                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else {
                    if (responseCode==400){
                        Log.d("cameInThisBlock","true");
                        responseCodeForShow=400;
                    }
                    else if (responseCode==401){
                        responseCodeForShow=401;
                    }
                    else if (responseCode==403){
                        responseCodeForShow=403;
                    }
                    else if (responseCode==405){
                        responseCodeForShow=405;
                    }
                    else if (responseCode==302){
                        responseCodeForShow=302;
                    }

                    Log.d("elseresponseCode",""+responseCode);
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("resultFromNewCall",result);

//            if (responseCodeForShow==400){
//                final Toast toast = Toasty.info(getActivity(), getString(R.string.urlchange),Toast.LENGTH_SHORT);
//                toast.show();
//                new CountDownTimer(10000, 1000)
//                {
//                    public void onTick(long millisUntilFinished) {toast.show();}
//                    public void onFinish() {toast.cancel();}
//                }.start();
//                Prefs.clear();
//                Intent intent=new Intent(getActivity(),LoginActivity.class);
//                startActivity(intent);
//                return;
//            }
//            if (responseCodeForShow==401){
//                final Toast toast = Toasty.info(getActivity(), getString(R.string.apiDisabled),Toast.LENGTH_LONG);
//                toast.show();
//                new CountDownTimer(10000, 1000)
//                {
//                    public void onTick(long millisUntilFinished) {toast.show();}
//                    public void onFinish() {toast.cancel();}
//                }.start();
//                Prefs.clear();
//                Intent intent=new Intent(getActivity(),LoginActivity.class);
//                startActivity(intent);
//                return;
//            }
//            if (responseCodeForShow==403){
//                final Toast toast = Toasty.info(getActivity(), getString(R.string.bannedOrdeactivated),Toast.LENGTH_LONG);
//                toast.show();
//                new CountDownTimer(10000, 1000)
//                {
//                    public void onTick(long millisUntilFinished) {toast.show();}
//                    public void onFinish() {toast.cancel();}
//                }.start();
//                Prefs.clear();
//                Intent intent=new Intent(getActivity(),LoginActivity.class);
//                startActivity(intent);
//                return;
//            }
//
//            if (responseCodeForShow==405){
//                final Toast toast = Toasty.info(getActivity(), getString(R.string.urlchange),Toast.LENGTH_LONG);
//                toast.show();
//                new CountDownTimer(10000, 1000)
//                {
//                    public void onTick(long millisUntilFinished) {toast.show();}
//                    public void onFinish() {toast.cancel();}
//                }.start();
//                Prefs.clear();
//                Intent intent=new Intent(getActivity(),LoginActivity.class);
//                startActivity(intent);
//                return;
//            }
//
//
//            if (responseCodeForShow==302){
//                final Toast toast = Toasty.info(getActivity(), getString(R.string.urlchange),Toast.LENGTH_SHORT);
//                toast.show();
//                new CountDownTimer(10000, 1000)
//                {
//                    public void onTick(long millisUntilFinished) {toast.show();}
//                    public void onFinish() {toast.cancel();}
//                }.start();
//                Prefs.clear();
//                Intent intent=new Intent(getActivity(),LoginActivity.class);
//                startActivity(intent);
//                return;
//            }

            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                token = jsonObject1.getString("token");
                JSONObject jsonObject2=jsonObject1.getJSONObject("User");
                String role=jsonObject2.getString("role");
                if (role.equals("User")){
                    final Toast toast = Toasty.info(getActivity(), getString(R.string.roleChanged),Toast.LENGTH_SHORT);
                    toast.show();
                    new CountDownTimer(10000, 1000)
                    {
                        public void onTick(long millisUntilFinished) {toast.show();}
                        public void onFinish() {toast.cancel();}
                    }.start();
                    Prefs.clear();
                    //Prefs.putString("role",role);
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    //Toasty.info(getActivity(), getString(R.string.roleChanged), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
                Prefs.putString("TOKEN", token);
                Log.d("TOKEN",token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    private class FetchDependency extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog.setMessage("Loading your information");
        }

        protected String doInBackground(String... urls) {

            return new Helpdesk().getDependency();

        }

        protected void onPostExecute(String result) {
            Log.d("Depen Response : ", result + "");
            progressDialog.dismiss();

            if (result == null) {

                return;
            }
            String state=Prefs.getString("403",null);
            try {
                if (state.equals("403") && !state.equals(null)) {
                    Toasty.warning(getActivity(), getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.clear();
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    Prefs.putString("403", "null");
                    startActivity(intent);
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");

                int open = 0, closed = 0, trash = 0, unasigned = 0, my_tickets = 0;
                JSONArray jsonArrayTicketsCount = jsonObject1.getJSONArray("tickets_count");
                for (int i = 0; i < jsonArrayTicketsCount.length(); i++) {
                    String name = jsonArrayTicketsCount.getJSONObject(i).getString("name");
                    String count = jsonArrayTicketsCount.getJSONObject(i).getString("count");

                    switch (name) {
                        case "Open":
                            open = Integer.parseInt(count);
                            break;
                        case "Closed":
                            closed = Integer.parseInt(count);
                            break;
                        case "Deleted":
                            trash = Integer.parseInt(count);
                            break;
                        case "unassigned":
                            unasigned = Integer.parseInt(count);
                            break;
                        case "mytickets":
                            my_tickets = Integer.parseInt(count);
                            break;
                        default:
                            break;

                    }
                }


                if (open > 999)
                    Prefs.putString("inboxTickets", "999+");
                else
                    Prefs.putString("inboxTickets", open + "");

                if (closed > 999)
                    Prefs.putString("closedTickets", "999+");
                else
                    Prefs.putString("closedTickets", closed + "");

                if (my_tickets > 999)
                    Prefs.putString("myTickets", "999+");
                else
                    Prefs.putString("myTickets", my_tickets + "");

                if (trash > 999)
                    Prefs.putString("trashTickets", "999+");
                else
                    Prefs.putString("trashTickets", trash + "");

                if (unasigned > 999)
                    Prefs.putString("unassignedTickets", "999+");
                else
                    Prefs.putString("unassignedTickets", unasigned + "");
                if (isAdded()) {
                    drawerItem[0] = new DataModel(R.drawable.inbox_tickets, getString(R.string.inbox), Prefs.getString("inboxTickets", null));
                    drawerItem[1] = new DataModel(R.drawable.my_ticket, getString(R.string.my_tickets), Prefs.getString("myTickets", null));
                    drawerItem[2] = new DataModel(R.drawable.unassigned_ticket, getString(R.string.unassigned_tickets), Prefs.getString("unassignedTickets", null));
                    drawerItem[3] = new DataModel(R.drawable.closed_ticket, getString(R.string.closed_tickets), Prefs.getString("closedTickets", null));
                    drawerItem[4] = new DataModel(R.drawable.trash_ticket, getString(R.string.trash), Prefs.getString("trashTickets", null));
                    drawerItemCustomAdapter = new DrawerItemCustomAdapter(getActivity(), R.layout.list_view_item_row, drawerItem);
                    listView.setAdapter(drawerItemCustomAdapter);
                    drawerItemCustomAdapter.notifyDataSetChanged();
                    UIUtils.setListViewHeightBasedOnItems(listView);
//                drawerItemCustomAdapter.notifyDataSetChanged();
//

                }
//                else{
//                    Toasty.warning(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
//                }
            }

            catch (JSONException e) {
                String state1=Prefs.getString("400",null);

                try {
                    if (state1.equals("badRequest")) {
                        Toasty.info(getActivity(), getString(R.string.apiDisabled), Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toasty.error(getActivity(), "Parsing Error!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }catch (NullPointerException e1){
                    e1.printStackTrace();
                }


            }
        }
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        new FetchDependency().execute();
//        drawerItemCustomAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        title = getString(R.string.app_name);
        switch (v.getId()) {

            case R.id.create_ticket:
                Prefs.putString("firstusername","null");
                Prefs.putString("lastusername","null");
                Prefs.putString("firstuseremail","null");
                Prefs.putString("firstusermobile","null");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent inte = new Intent(getContext(), CreateTicketActivity.class);
                startActivity(inte);
                break;
            case R.id.settings:
                Intent intent=new Intent(getContext(),SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.client_list:
                Prefs.putString("normalclientlist","true");
                Prefs.putString("filtercustomer","true");
                title = getString(R.string.client_list);
                fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                if (fragment == null)
                    fragment = new ClientList();
                break;
            case R.id.problems:
                if (listViewProblem.getVisibility()==View.VISIBLE){
                listViewProblem.setVisibility(View.GONE);
            }
            else{
                listViewProblem.setVisibility(View.VISIBLE);
            }

                break;
            case R.id.change:
                Log.d("clicked","true");
                if (listViewChange.getVisibility()==View.VISIBLE){
                    listViewChange.setVisibility(View.GONE);
                }
                else{
                    listViewChange.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.about:
                title = getString(R.string.about);
                fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                if (fragment == null)
                    fragment = new About();
                break;
            case R.id.logout:
                try {
                    MyFirebaseInstanceIDService.sendRegistrationToServer("0");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                confirmationDialog.show(getFragmentManager(),null);

                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            ((MainActivity) getActivity()).setActionBarTitle(title);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }



    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }
}

