package co.servicedesk.faveo.pro.frontend.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
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
import co.servicedesk.faveo.pro.frontend.adapters.TicketOverviewAdapter;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.TicketOverview;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

/**
 * Created by Amal on 27/03/2017.
 */

public class TicketFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view1;
    Toolbar toolbar,toolbar1;
    TextView toolbarTextview;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    TextView textView;
    TextView empty_view;
    String lastQuerry;
    List<TicketOverview> ticketOverviewList = new ArrayList<TicketOverview>();
    int total;
    static String nextPageURL = "";
    ProgressDialog progressDialog;
    SpotsDialog dialog1;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    TextView noInternet_view;
    TicketOverviewAdapter ticketOverviewAdapter;
    private String mParam1;
    private String mParam2;
    LinearLayout toolbarView;
    String querry;
    TicketOverview ticketOverview;
    int pageno=1;
    private boolean isViewShown = false;
    private OnFragmentInteractionListener mListener;

    public static TicketFragment newInstance(int page, String title) {
        TicketFragment fragmentFirst = new TicketFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    public TicketFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
            toolbarView= (LinearLayout) rootView.findViewById(R.id.toolbarview);
            toolbarView.setVisibility(View.GONE);
            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar2);
            toolbar.setVisibility(View.GONE);
            toolbarTextview = (TextView) toolbar.findViewById(R.id.toolbartextview);
            view1 = rootView.findViewById(R.id.separationView);
            view1.setVisibility(View.GONE);
            toolbar1 = (Toolbar) rootView.findViewById(R.id.toolbarfilteration);
            toolbar1.setVisibility(View.GONE);
            textView = (TextView) rootView.findViewById(R.id.totalcount);
            textView.setVisibility(View.GONE);
            //textView.setVisibility(View.VISIBLE);
            recyclerView = (ShimmerRecyclerView) rootView.findViewById(R.id.cardList);
            recyclerView.setVisibility(View.VISIBLE);
            empty_view= (TextView) rootView.findViewById(R.id.empty_view);
            noInternet_view= (TextView) rootView.findViewById(R.id.noiternet_view);
            swipeRefresh= (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
            swipeRefresh.setColorSchemeResources(R.color.faveo_blue);
            dialog1= new SpotsDialog(getActivity(), getString(R.string.pleasewait));
            querry=Prefs.getString("querry1",null);

            swipeRefresh.setColorSchemeResources(R.color.faveo_blue);
        if (!isViewShown) {
            if (InternetReceiver.isConnected()) {
                if (querry.equals("") || querry.equals("null")) {
                    Log.d("QUERRY", "No Querry");
                    recyclerView.setVisibility(View.GONE);
                    empty_view.setVisibility(View.GONE);
                    //empty_view.setText(getString(R.string.noTicket));
                } else {
                    noInternet_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_view.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(true);
                    getActivity().getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                    new FetchFirst(getActivity(), querry).execute();
                }
//                noInternet_view.setVisibility(View.GONE);
//                // swipeRefresh.setRefreshing(true);
//                progressDialog.show();
//                new FetchFirst(getActivity(),querry).execute();
            } else {
                noInternet_view.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                empty_view.setVisibility(View.GONE);
            }
        }
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (InternetReceiver.isConnected()) {
                        if (querry.equals("")||querry.equals("null")){
                            Log.d("QUERRY","No Querry");
                            recyclerView.setVisibility(View.GONE);
                            empty_view.setVisibility(View.VISIBLE);
                            swipeRefresh.setRefreshing(false);
                        }
                        else{
                            noInternet_view.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            empty_view.setVisibility(View.GONE);
                            dialog1.show();
                            getActivity().getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                            );
                            new FetchFirst(getActivity(),querry).execute();
                        }
//                        loading = true;
//                        recyclerView.setVisibility(View.VISIBLE);
//                        noInternet_view.setVisibility(View.GONE);
//                        if (ticketOverviewList.size() != 0) {
////                            ticketThreadList.clear();
////                            ticketThreadAdapter.notifyDataSetChanged();
////                            task = new Conversation.FetchTicketThreads(getActivity());
////                            task.execute();
//                        }
                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        swipeRefresh.setRefreshing(false);
                        empty_view.setVisibility(View.GONE);
                        noInternet_view.setVisibility(View.VISIBLE);
                    }

                }
            });
            //Toast.makeText(getActivity(), "ticket fragment", Toast.LENGTH_SHORT).show();

        return rootView;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            //fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            if (InternetReceiver.isConnected()) {
                if (querry.equals("")||querry.equals("null")){
                    Log.d("QUERRY","No Querry");
                    recyclerView.setVisibility(View.GONE);
                    empty_view.setVisibility(View.GONE);
                    //empty_view.setText(getString(R.string.noUser));
                }
                else{
                    noInternet_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_view.setVisibility(View.GONE);
                    //progressDialog.show();
                    getActivity().getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                    new FetchFirst(getActivity(), querry).execute();
                }
//                noInternet_view.setVisibility(View.GONE);
//                // swipeRefresh.setRefreshing(true);
//                progressDialog.show();
//                new FetchFirst(getActivity(),querry).execute();
            } else {
                noInternet_view.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                empty_view.setVisibility(View.GONE);
            }

        } else {
            isViewShown = false;
        }
    }

    private class FetchFirst extends AsyncTask<String, Void, String> {
        Context context;
        String querry;
        String agentName;
        FetchFirst(Context context,String querry) {
            this.context = context;
            this.querry=querry;
        }

        protected String doInBackground(String... urls) {
//            if (nextPageURL.equals("null")) {
//                return "all done";
//            }
            String result = new Helpdesk().searchQuerry(querry);
            if (result == null)
                return null;
            String data;
            ticketOverviewList.clear();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Prefs.putString("searchResult",jsonObject.toString());
                JSONObject jsonObject1=jsonObject.getJSONObject("result");
                total= jsonObject1.getInt("total");
                try {
                    data = jsonObject1.getString("data");
                    nextPageURL = jsonObject1.getString("next_page_url");
                } catch (JSONException e) {
                    data = jsonObject1.getString("result");
                }
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ticketOverview = Helper.parseTicketSearchOverview(jsonArray, i);
                    if (ticketOverview != null)
                        ticketOverviewList.add(ticketOverview);
//                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
//                    String ticketNumber = jsonObject2.getString("ticket_number");
//                    String assignedto=jsonObject2.getString("assigned_to");
//                    String ID = jsonObject2.getString("id");
//                    JSONObject User=jsonObject2.getJSONObject("User");
//                    String firstName = User.getString("first_name");
//                    String lastName = User.getString("last_name");
//                    String username = User.getString("user_name");
//                    String profilePic = User.getString("profile_pic");
//                    if (assignedto.equals("null")){
//                        agentName="Unassigned";
//                    }
//                    else{
//                        JSONObject assigned=jsonObject2.getJSONObject("assigned");
//                        agentName=assigned.getString("first_name")+" "+assigned.getString("last_name");
//                    }
//
//                    JSONArray thread=jsonObject2.getJSONArray("thread");
//                    JSONObject jsonObject5=thread.getJSONObject(0);
//                    String title=jsonObject5.getString("title");
//                    JSONObject statuses=jsonObject2.getJSONObject("statuses");
//                    String ticketStatusName = statuses.getString("name");
//                    JSONObject priority=jsonObject2.getJSONObject("priority");
//                    String priorityColor = priority.getString("priority_color");
//                    Log.d("PriorityColor",priorityColor);

                    //ID,first name ,last name,User name,profile pic,agent name,title,ticket status name,priority color,ticket number.
                    //Log.d("ticket status name",ticketStatusName);
//                    for (int i1=0;i<jsonArray1.length();i1++){
//
//                    }



                    //String countcollaborator=jsonArray.getJSONObject(i).getString("countcollaborator");
                    //String countthread=jsonArray.getJSONObject(i).getString("countthread");
                    //String source=jsonArray.getJSONObject(i).getString("source");
                    //JSONObject jsonObject2=
//                    String title = jsonArray.getJSONObject(i).getString("ticket_title");
//                    String ticketStatusName = jsonArray.getJSONObject(i).getString("ticket_status_name");
//                    String updatedAt = jsonArray.getJSONObject(i).getString("updated_at");
//                    String dueDate = jsonArray.getJSONObject(i).getString("duedate");
//                    String priorityColor = jsonArray.getJSONObject(i).getString("color");
//                    String attachment = jsonArray.getJSONObject(i).getString("countattachment");
//                    String last_replier=jsonArray.getJSONObject(i).getString("last_replier");
//                    String agentName=jsonArray.getJSONObject(i).getString("a_fname")+" "+jsonArray.getJSONObject(i).getString("a_lname");
                    //Log.d("agentname",agentName);

                    //ticketOverview = Helper.parseTicketOverview(jsonArray, i);
//                    if (ticketOverview != null)
//                        ticketOverviewList.add(ticketOverview);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return "success";
        }

        protected void onPostExecute(String result) {
            dialog1.dismiss();
            //Toast.makeText(context, "Total is:"+total, Toast.LENGTH_SHORT).show();
            //Log.d("total",""+total);
            Prefs.putString("cameFromSearch","true");
            textView.setVisibility(View.VISIBLE);
            textView.setText(""+total+" tickets");
            if (swipeRefresh.isRefreshing())
                swipeRefresh.setRefreshing(false);
            if (result == null) {
                Toasty.error(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            if (result.equals("all done")) {

                Toasty.info(context, getString(R.string.all_caught_up), Toast.LENGTH_SHORT).show();
                return;
            }
            //recyclerView = (ShimmerRecyclerView) rootView.findViewById(R.id.cardList);
            recyclerView.setHasFixedSize(false);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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
                                pageno++;
                                new FetchNextPage(getActivity(),nextPageURL,querry).execute();
                                StyleableToast st = new StyleableToast(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT);
                                st.setBackgroundColor(Color.parseColor("#3da6d7"));
                                st.setTextColor(Color.WHITE);
                                st.setIcon(R.drawable.ic_autorenew_black_24dp);
                                st.spinIcon();
                                st.setMaxAlpha();
                                st.show();
                                //Toast.makeText(getActivity(), "Loading!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
//
            ticketOverviewAdapter = new TicketOverviewAdapter(getContext(),ticketOverviewList);
            recyclerView.setAdapter(ticketOverviewAdapter);

            if (ticketOverviewAdapter.getItemCount() == 0) {
                empty_view.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            } else empty_view.setVisibility(View.GONE);
        }
    }

    /**
     * Async task for next page details.
     */
    private class FetchNextPage extends AsyncTask<String, Void, String> {
        Context context;
        int page;
        String querry;
        String url;

        FetchNextPage(Context context,String url,String querry) {
            this.context = context;
            this.url=url;
            this.querry=querry;
        }

        protected String doInBackground(String... urls) {
            if (nextPageURL.equals("null")) {
                pageno=1;
                //Toasty.info(context, getString(R.string.all_caught_up), Toast.LENGTH_SHORT).show();
                return "all done";
            }
            String result = new Helpdesk().nextPageURLForSearching(nextPageURL,querry);
            if (result == null)
                return null;
            // DatabaseHandler databaseHandler = new DatabaseHandler(context);
            // databaseHandler.recreateTable();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("result");
                nextPageURL = jsonObject1.getString("next_page_url");
                String data = jsonObject1.getString("data");
                int my_tickets = jsonObject1.getInt("total");
                if (my_tickets > 999)
                    Prefs.putString("myTickets", "999+");
                else
                    Prefs.putString("myTickets", my_tickets + "");
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    TicketOverview ticketOverview = Helper.parseTicketSearchOverview(jsonArray, i);
                    if (ticketOverview != null) {
                        ticketOverviewList.add(ticketOverview);
                        // databaseHandler.addTicketOverview(ticketOverview);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //databaseHandler.close();
            return "success";
        }

        protected void onPostExecute(String result) {
            if (result == null)
                return;
            if (result.equals("all done")) {
                pageno=1;
                Toasty.info(context, getString(R.string.all_caught_up), Toast.LENGTH_SHORT).show();
                return;
            }
            ticketOverviewAdapter.notifyDataSetChanged();
            loading = true;
        }
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onResume() {
        super.onResume();

    }




    @Override
    public void onStart() {
        //Toast.makeText(getActivity(), "onStart", Toast.LENGTH_SHORT).show();
        super.onStart();
    }

}
