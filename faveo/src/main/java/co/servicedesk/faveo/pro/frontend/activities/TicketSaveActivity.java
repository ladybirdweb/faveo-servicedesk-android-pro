package co.servicedesk.faveo.pro.frontend.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.Data;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class TicketSaveActivity extends AppCompatActivity {
    Spinner  spinnerType, spinnerStatus, spinnerSource,
            spinnerPriority, spinnerHelpTopics;
    ProgressDialog progressDialog;
    SpotsDialog dialog1;
    AsyncTask<String, Void, String> task;
    @BindView(R.id.spinner_staffs)
    Spinner spinnerStaffs;
    EditText edittextsubject;
    Button buttonsave;
    ImageView imageView;
    Spinner autoCompleteTextViewstaff;
    ImageView refresh;
    ArrayList<Data> helptopicItems, priorityItems, typeItems, sourceItems, staffItems;
    ArrayAdapter<Data> spinnerPriArrayAdapter, spinnerHelpArrayAdapter, spinnerTypeArrayAdapter, spinnerSourceArrayAdapter, staffArrayAdapter;
    int id,id1;
    String option;
    public static String
            keyDepartment = "", valueDepartment = "",
            keySLA = "", valueSLA = "",
            keyStatus = "", valueStatus = "",
            keyStaff = "", valueStaff = "",
            keyName="",
            keyPriority = "", valuePriority = "",
            keyTopic = "", valueTopic = "",
            keySource = "", valueSource = "",
            keyType = "", valueType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_save);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        Window window = TicketSaveActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(TicketSaveActivity.this,R.color.mainActivityTopBar));
        //final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(TicketSaveActivity.this);
        StrictMode.setThreadPolicy(policy);
        if (InternetReceiver.isConnected()){
            new FetchDependency().execute();
        }


        option=Prefs.getString("cameFromNotification", null);
        switch (option) {
            case "true":
                Prefs.putString("cameFromNotification","true");
                break;
            case "false":
                Prefs.putString("cameFromNotification","false");
                break;
            case "none":
                Prefs.putString("cameFromNotification","none");
                break;
            default:
                Prefs.putString("cameFromNotification","");
                break;
        }
        setUpViews();

        if (InternetReceiver.isConnected()) {
            dialog1= new SpotsDialog(TicketSaveActivity.this, getString(R.string.pleasewait));
            dialog1.show();
            Log.d("FromTicketSave","true");
            task = new FetchTicketDetail(Prefs.getString("TICKETid",null));
            task.execute();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsave);
        TextView textView = (TextView) toolbar.findViewById(R.id.titlesave);
        imageView= (ImageView) toolbar.findViewById(R.id.imageViewBackTicketSave);
        refresh= (ImageView) findViewById(R.id.imageViewRefresh);
        textView.setText(getString(R.string.ticketProperties));

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new BottomDialog.Builder(TicketSaveActivity.this)
                        .setTitle(getString(R.string.refreshingPage))
                        .setContent(getString(R.string.refreshPage))
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
                            dialog1= new SpotsDialog(TicketSaveActivity.this, getString(R.string.refreshing));
                            dialog1.show();
                            new FetchDependency().execute();
                            setUpViews();
                            task = new FetchTicketDetail(Prefs.getString("TICKETid",null));
                            task.execute();
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        spinnerPriority.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonsave.setVisibility(View.VISIBLE);
                return false;
            }
        });
        autoCompleteTextViewstaff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Data data=staffItems.get(position);
                id1=data.getID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                id1=0;
            }
        });
        spinnerHelpTopics.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonsave.setVisibility(View.VISIBLE);
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittextsubject.getWindowToken(), 0);
                return false;
            }
        });

        spinnerPriority.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonsave.setVisibility(View.VISIBLE);
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittextsubject.getWindowToken(), 0);
                return false;
            }
        });
        spinnerSource.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonsave.setVisibility(View.VISIBLE);
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittextsubject.getWindowToken(), 0);
                return false;
            }
        });

        autoCompleteTextViewstaff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonsave.setVisibility(View.VISIBLE);
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittextsubject.getWindowToken(), 0);
                return false;
            }
        });

        spinnerType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                buttonsave.setVisibility(View.VISIBLE);
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittextsubject.getWindowToken(), 0);
                return false;
            }
        });

        edittextsubject.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                edittextsubject.setCursorVisible(true);
                buttonsave.setVisibility(View.VISIBLE);
                return false;
            }
        });
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allCorrect = true;
                final String subject = edittextsubject.getText().toString();
                final Data helpTopic = (Data) spinnerHelpTopics.getSelectedItem();
                final Data source = (Data) spinnerSource.getSelectedItem();
                final Data priority = (Data) spinnerPriority.getSelectedItem();
                final Data type = (Data) spinnerType.getSelectedItem();


                if (subject.trim().length() == 0) {
                    Toasty.warning(TicketSaveActivity.this, getString(R.string.sub_must_not_be_empty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                } else if (subject.trim().length() < 5) {
                    Toasty.warning(TicketSaveActivity.this, getString(R.string.sub_minimum_char), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                else if (helpTopic.ID==0) {
                    allCorrect = false;
                    Toasty.warning(TicketSaveActivity.this, getString(R.string.select_some_helptopic), Toast.LENGTH_SHORT).show();
                } else if (priority.ID==0) {
                    allCorrect = false;
                    Toasty.warning(TicketSaveActivity.this, getString(R.string.please_select_some_priority), Toast.LENGTH_SHORT).show();
                } else if (source.ID==0) {
                    allCorrect = false;
                    Toasty.warning(TicketSaveActivity.this, getString(R.string.select_source), Toast.LENGTH_SHORT).show();
                }
                if (allCorrect) {
                    if (InternetReceiver.isConnected()) {
                        new BottomDialog.Builder(TicketSaveActivity.this)
                                .setTitle(getString(R.string.editingticket))
                                .setContent(getString(R.string.editingConfirmation))
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
                                            //dialogBuilder.cancel();
                                            dialog1= new SpotsDialog(TicketSaveActivity.this, getString(R.string.updating_ticket));
                                            dialog1.show();
                                            try {
                                                new SaveTicket(
                                                        Integer.parseInt(Prefs.getString("TICKETid",null)),
                                                        URLEncoder.encode(subject.trim(), "utf-8"),
                                                        helpTopic.ID,
                                                        source.ID,
                                                        priority.ID,id1)
                                                        .execute();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
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
                }
            }
        });
    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        if (InternetReceiver.isConnected()){
//            new FetchTicketDetail(Prefs.getString("TICKETid",null)).execute();
//        }
//    }
    private class SaveTicket extends AsyncTask<String, Void, String> {
        int ticketNumber;
        String subject;
        //int slaPlan;
        int helpTopic;
        int ticketSource;
        int ticketPriority;
        int staff;

        SaveTicket(int ticketNumber, String subject, int helpTopic, int ticketSource, int ticketPriority,int staff) {
            this.ticketNumber = ticketNumber;
            this.subject = subject;
            // this.slaPlan = slaPlan;
            this.helpTopic = helpTopic;
            this.ticketSource = ticketSource;
            this.ticketPriority = ticketPriority;
            // this.ticketStatus = ticketStatus;
            this.staff=staff;
        }

        protected String doInBackground(String... urls) {
            if (subject.equals("Not available"))
                subject = "";
            return new Helpdesk().postEditTicket(ticketNumber, subject,
                    helpTopic, ticketSource, ticketPriority,staff);
        }

        protected void onPostExecute(String result) {
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
            Prefs.putString("ticketThread","");
            Log.d("Depen Response : ", result + "");
            if (result == null) {
                Toasty.error(TicketSaveActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            String state=Prefs.getString("403",null);

            try {
                if (state.equals("403") && !state.equals(null)) {
                    Toasty.warning(TicketSaveActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.equals("Permission denied, you do not have permission to access the requested page.")) {
                    Toasty.warning(TicketSaveActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            if (result.contains("Edited successfully")) {
                Toasty.success(TicketSaveActivity.this, getString(R.string.update_success), Toast.LENGTH_LONG).show();
                Prefs.putString("cameFromNewProblem","true");
                Intent intent=new Intent(TicketSaveActivity.this, TicketDetailActivity.class);
                intent.putExtra("ticket_id", Prefs.getString("TICKETid",null));
                startActivity(intent);
                //new FetchTicketDetail1(Prefs.getString("TICKETid",null)).execute();

            } else
                Toasty.error(TicketSaveActivity.this, getString(R.string.failed_to_update_ticket), Toast.LENGTH_LONG).show();
        }
    }
    private class FetchTicketDetail extends AsyncTask<String, Void, String> {
        String ticketID;

        FetchTicketDetail(String ticketID) {
            this.ticketID = ticketID;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getTicketDetail(ticketID);
        }

        protected void onPostExecute(String result) {
            dialog1.dismiss();
            if (isCancelled()) return;


            if (result == null) {
                Toasty.error(TicketSaveActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONObject jsonObject2=jsonObject1.getJSONObject("ticket");
                String title=jsonObject2.getString("title");

                if (title.startsWith("=?UTF-8?Q?") && title.endsWith("?=")) {
                    String first = title.replace("=?UTF-8?Q?", "");
                    String second = first.replace("_", " ");
                    String second1=second.replace("=C3=BA","");
                    String third = second1.replace("=C2=A0", "");
                    String fourth = third.replace("?=", "");
                    String fifth = fourth.replace("=E2=80=99", "'");
                    String sixth=fifth.replace("=3F","?");
                    edittextsubject.setText(sixth);
                } else {
                    edittextsubject.setText(title);
                }
                //edittextsubject.setText(title);
                String assignee=jsonObject2.getString("assignee");
                if (assignee.equals(null)||assignee.equals("null")||assignee.equals("")){
                    autoCompleteTextViewstaff.setSelection(0);
                }
                else{
                    JSONObject jsonObject3=jsonObject2.getJSONObject("assignee");
                    try {
                        if (jsonObject3.getString("first_name") != null&&jsonObject3.getString("last_name") != null) {
                            //spinnerHelpTopics.setSelection(getIndex(spinnerHelpTopics, jsonObject1.getString("helptopic_name")));
                            id1= Integer.parseInt(jsonObject3.getString("id"));
                            Log.d("id of the assignee",""+id1);
                            for (int j = 0; j < staffItems.size(); j++) {
                                Data data=staffItems.get(j);
                                if (data.getID()==id1) {
                                    Log.d("cameHere","True");
                                    Log.d("position",""+j);
                                    autoCompleteTextViewstaff.setSelection(j);
                                }
                            }
                            //spinnerStaffs.setSelection(staffItems.indexOf("assignee_email"));
                        }
                        //spinnerHelpTopics.setSelection(Integer.parseInt(jsonObject1.getString("helptopic_id")));
                    } catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    } catch (Exception e) {
//                    spinnerHelpTopics.setVisibility(View.GONE);
//                    tv_helpTopic.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }

                try {
                    if (jsonObject2.getString("priority_name") != null) {

                        spinnerPriority.setSelection(getIndex(spinnerPriority, jsonObject2.getString("priority_name")));


                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                try {
                    if (jsonObject2.getString("type_name") != null) {

                        for (int j = 0; j < spinnerType.getCount(); j++) {
                            if (spinnerType.getItemAtPosition(j).toString().equalsIgnoreCase(jsonObject2.getString("type_name"))) {
                                spinnerType.setSelection(j);
                            }
                        }
                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                try {
                    if (jsonObject2.getString("helptopic_name") != null)
                        spinnerHelpTopics.setSelection(getIndex(spinnerHelpTopics, jsonObject2.getString("helptopic_name")));


                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception e) {

                    e.printStackTrace();
                }



                try {
                    if (jsonObject2.getString("source_name") != null)


                        spinnerSource.setSelection(getIndex(spinnerSource, jsonObject2.getString("source_name")));

                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }


            } catch (JSONException | IllegalStateException e) {
                e.printStackTrace();
            }
        }

    }
    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            Log.d("item ", spinner.getItemAtPosition(i).toString());
            if (spinner.getItemAtPosition(i).toString().equals(myString.trim())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void setUpViews() {
        Prefs.getString("keyStaff", null);
        Data data;
        JSONObject jsonObject;
        String json = Prefs.getString("DEPENDENCY", "");
        try {
            staffItems = new ArrayList<>();
            jsonObject = new JSONObject(json);
            staffItems.add(new Data(0, "--"));
            JSONArray jsonArrayStaffs = jsonObject.getJSONArray("staffs");
            for (int i = 0; i < jsonArrayStaffs.length(); i++) {
                if (jsonArrayStaffs.getJSONObject(i).getString("first_name").equals("")&&jsonArrayStaffs.getJSONObject(i).getString("last_name").equals("")){
                    Log.d("cameHere","TRUE");
                    data = new Data(Integer.parseInt(jsonArrayStaffs.getJSONObject(i).getString("id")), jsonArrayStaffs.getJSONObject(i).getString("email"));
                }
                else {
                    data = new Data(Integer.parseInt(jsonArrayStaffs.getJSONObject(i).getString("id")), jsonArrayStaffs.getJSONObject(i).getString("first_name")+" "+jsonArrayStaffs.getJSONObject(i).getString("last_name"));
                }
                staffItems.add(data);
                Collections.sort(staffItems, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }
            helptopicItems = new ArrayList<>();
            helptopicItems.add(new Data(0, "--"));
            jsonObject = new JSONObject(json);
            JSONArray jsonArrayHelpTopics = jsonObject.getJSONArray("helptopics");
            for (int i = 0; i < jsonArrayHelpTopics.length(); i++) {
                Data data1 = new Data(Integer.parseInt(jsonArrayHelpTopics.getJSONObject(i).getString("id")), jsonArrayHelpTopics.getJSONObject(i).getString("topic"));
                helptopicItems.add(data1);
                Collections.sort(helptopicItems, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }

            JSONArray jsonArrayPriorities = jsonObject.getJSONArray("priorities");
            priorityItems = new ArrayList<>();
            priorityItems.add(new Data(0, "--"));
            for (int i = 0; i < jsonArrayPriorities.length(); i++) {
                Data data2 = new Data(Integer.parseInt(jsonArrayPriorities.getJSONObject(i).getString("priority_id")), jsonArrayPriorities.getJSONObject(i).getString("priority"));
                priorityItems.add(data2);
                Collections.sort(priorityItems, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }

            JSONArray jsonArrayType = jsonObject.getJSONArray("type");
            typeItems = new ArrayList<>();
            typeItems.add(new Data(0, "--"));
            for (int i = 0; i < jsonArrayType.length(); i++) {
                Data data3 = new Data(Integer.parseInt(jsonArrayType.getJSONObject(i).getString("id")), jsonArrayType.getJSONObject(i).getString("name"));
                typeItems.add(data3);
                Collections.sort(typeItems, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });

            }

            JSONArray jsonArraySources = jsonObject.getJSONArray("sources");
            sourceItems = new ArrayList<>();
            sourceItems.add(new Data(0, "--"));
            for (int i = 0; i < jsonArraySources.length(); i++) {
                Data data4 = new Data(Integer.parseInt(jsonArraySources.getJSONObject(i).getString("id")), jsonArraySources.getJSONObject(i).getString("name"));
                sourceItems.add(data4);
                Collections.sort(sourceItems, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        edittextsubject= (EditText) findViewById(R.id.editTextsubject);
        buttonsave= (Button) findViewById(R.id.buttonsave);
        spinnerPriority= (Spinner) findViewById(R.id.spinner_priority);
        spinnerPriArrayAdapter = new ArrayAdapter<>(TicketSaveActivity.this, android.R.layout.simple_spinner_dropdown_item, priorityItems);
        //selected item will look like a spinner set from XML
        spinnerPriArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(spinnerPriArrayAdapter);

        spinnerType= (Spinner) findViewById(R.id.spinner_type);
        spinnerTypeArrayAdapter = new ArrayAdapter<>(TicketSaveActivity.this, android.R.layout.simple_spinner_dropdown_item, typeItems);
        //selected item will look like a spinner set from XML
        spinnerTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerTypeArrayAdapter);


        spinnerHelpTopics= (Spinner) findViewById(R.id.spinner_help_topics);
        spinnerHelpArrayAdapter = new ArrayAdapter<>(TicketSaveActivity.this, android.R.layout.simple_spinner_dropdown_item, helptopicItems);
        //selected item will look like a spinner set from XML
        spinnerHelpArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHelpTopics.setAdapter(spinnerHelpArrayAdapter);


        autoCompleteTextViewstaff= (Spinner) findViewById(R.id.spinner_staffs);
        staffArrayAdapter=new ArrayAdapter<>(TicketSaveActivity.this,android.R.layout.simple_dropdown_item_1line,staffItems);
        //staffArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewstaff.setAdapter(staffArrayAdapter);
//        autoCompleteTextViewstaff.setThreshold(1);
//        autoCompleteTextViewstaff.setDropDownWidth(1000);

        spinnerSource= (Spinner) findViewById(R.id.spinner_source);
        spinnerSourceArrayAdapter = new ArrayAdapter<>(TicketSaveActivity.this, android.R.layout.simple_spinner_dropdown_item, sourceItems);
        //selected item will look like a spinner set from XML
        spinnerSourceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(spinnerSourceArrayAdapter);


    }
    @Override
    public void onBackPressed() {
        finish();

    }
    private class FetchDependency extends AsyncTask<String, Void, String> {
        String unauthorized;

        protected String doInBackground(String... urls) {

            return new Helpdesk().getDependency();

        }

        protected void onPostExecute(String result) {
            Log.d("Depen Response : ", result + "");
            Log.d("cameHere","True");
dialog1.dismiss();
            if (result==null) {
//                try {
//                    unauthorized = Prefs.getString("unauthorized", null);
//                    if (unauthorized.equals("true")) {
//                        loading.setText("Oops! Something went wrong.");
//                        progressDialog.setVisibility(View.INVISIBLE);
//                        textViewtryAgain.setVisibility(View.VISIBLE);
//                        textViewrefresh.setVisibility(View.VISIBLE);
//                        Prefs.putString("unauthorized", "false");
//                        textViewrefresh.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//
//                    }
//
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
            }
//            String state=Prefs.getString("403",null);
//
//            try {
//                if (state.equals("403") && !state.equals(null)) {
//                    Toasty.info(SplashActivity.this, getString(R.string.roleChanged), Toast.LENGTH_LONG).show();
//                    Prefs.clear();
//                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
//                    Prefs.putString("403", "null");
//                    startActivity(intent);
//                    return;
//                }
//            }catch (NullPointerException e){
//                e.printStackTrace();
//            }


            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                Prefs.putString("DEPENDENCY", jsonObject1.toString());
                // Preference.setDependencyObject(jsonObject1, "dependency");
                JSONArray jsonArrayDepartments = jsonObject1.getJSONArray("departments");
                for (int i = 0; i < jsonArrayDepartments.length(); i++) {
                    keyDepartment += jsonArrayDepartments.getJSONObject(i).getString("id") + ",";
                    valueDepartment += jsonArrayDepartments.getJSONObject(i).getString("name") + ",";
                }
                Prefs.putString("keyDept", keyDepartment);
                Prefs.putString("valueDept", valueDepartment);


                JSONArray jsonArraySla = jsonObject1.getJSONArray("sla");
                for (int i = 0; i < jsonArraySla.length(); i++) {
                    keySLA += jsonArraySla.getJSONObject(i).getString("id") + ",";
                    valueSLA += jsonArraySla.getJSONObject(i).getString("name") + ",";
                }
                Prefs.putString("keySLA", keySLA);
                Prefs.putString("valueSLA", valueSLA);

                JSONArray jsonArrayStaffs = jsonObject1.getJSONArray("staffs");
                for (int i = 0; i < jsonArrayStaffs.length(); i++) {
                    keyName +=jsonArrayStaffs.getJSONObject(i).getString("first_name") + jsonArrayStaffs.getJSONObject(i).getString("last_name") +",";
                    keyStaff += jsonArrayStaffs.getJSONObject(i).getString("id") + ",";
                    valueStaff += jsonArrayStaffs.getJSONObject(i).getString("email") + ",";
                }
                Prefs.putString("keyName",keyName);
                Prefs.putString("keyStaff", keyStaff);
                Prefs.putString("valueStaff", valueStaff);

                JSONArray jsonArrayType = jsonObject1.getJSONArray("type");
                for (int i = 0; i < jsonArrayType.length(); i++) {
                    keyType += jsonArrayType.getJSONObject(i).getString("id") + ",";
                    valueType += jsonArrayType.getJSONObject(i).getString("name") + ",";
                }
                Prefs.putString("keyType", keyType);
                Prefs.putString("valueType", valueType);

//                JSONArray jsonArrayStaffs = jsonObject1.getJSONArray("staffs");
//                for (int i = 0; i < jsonArrayStaffs.length(); i++) {
//                    keyStaff += jsonArrayStaffs.getJSONObject(i).getString("id") + ",";
//                    valueStaff += jsonArrayStaffs.getJSONObject(i).getString("email") + ",";
//                }


//                JSONArray jsonArrayTeams = jsonObject1.getJSONArray("teams");
//                for (int i = 0; i < jsonArrayTeams.length(); i++) {
//                    keyTeam += jsonArrayTeams.getJSONObject(i).getString("id") + ",";
//                    valueTeam += jsonArrayTeams.getJSONObject(i).getString("name") + ",";
//                }

                //Set<String> keyPri = new LinkedHashSet<>();
                // Set<String> valuePri = new LinkedHashSet<>();
                JSONArray jsonArrayPriorities = jsonObject1.getJSONArray("priorities");
                for (int i = 0; i < jsonArrayPriorities.length(); i++) {
                    // keyPri.add(jsonArrayPriorities.getJSONObject(i).getString("priority_id"));
                    //valuePri.add(jsonArrayPriorities.getJSONObject(i).getString("priority"));
                    keyPriority += jsonArrayPriorities.getJSONObject(i).getString("priority_id") + ",";
                    valuePriority += jsonArrayPriorities.getJSONObject(i).getString("priority") + ",";
                }
                Prefs.putString("keyPri", keyPriority);
                Prefs.putString("valuePri", valuePriority);
                //Prefs.putOrderedStringSet("keyPri", keyPri);
                // Prefs.putOrderedStringSet("valuePri", valuePri);
                //Log.d("Testtttttt", Prefs.getOrderedStringSet("keyPri", keyPri) + "   " + Prefs.getOrderedStringSet("valuePri", valuePri));


                JSONArray jsonArrayHelpTopics = jsonObject1.getJSONArray("helptopics");
                for (int i = 0; i < jsonArrayHelpTopics.length(); i++) {

                    keyTopic += jsonArrayHelpTopics.getJSONObject(i).getString("id") + ",";
                    valueTopic += jsonArrayHelpTopics.getJSONObject(i).getString("topic") + ",";
                }

                Prefs.putString("keyHelpTopic", keyTopic);
                Prefs.putString("valueHelptopic", valueTopic);

                JSONArray jsonArrayStatus = jsonObject1.getJSONArray("status");
                for (int i = 0; i < jsonArrayStatus.length(); i++) {

                    keyStatus += jsonArrayStatus.getJSONObject(i).getString("id") + ",";
                    valueStatus += jsonArrayStatus.getJSONObject(i).getString("name") + ",";

                }
                Prefs.putString("keyStatus", keyStatus);
                Prefs.putString("valueStatus", valueStatus);

                JSONArray jsonArraySources = jsonObject1.getJSONArray("sources");
                for (int i = 0; i < jsonArraySources.length(); i++) {
                    keySource += jsonArraySources.getJSONObject(i).getString("id") + ",";
                    valueSource += jsonArraySources.getJSONObject(i).getString("name") + ",";
                }

                Prefs.putString("keySource", keySource);
                Prefs.putString("valueSource", valueSource);

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

            } catch (JSONException | NullPointerException e) {
                //Toasty.error(SplashActivity.this, "Parsing Error!", Toast.LENGTH_LONG).show();
                Prefs.putString("unauthorized", "false");
                Prefs.putString("401","false");
                e.printStackTrace();
            }

//            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//            builder.setTitle("Welcome to FAVEO");
//            //builder.setMessage("After 2 second, this dialog will be closed automatically!");
//            builder.setCancelable(true);
//
//            final AlertDialog dlg = builder.create();
//
//            dlg.show();
//
//            final Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                public void run() {
//                    dlg.dismiss(); // when the task active then close the dialog
//                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
//                }
//            }, 3000);
        }
    }
}