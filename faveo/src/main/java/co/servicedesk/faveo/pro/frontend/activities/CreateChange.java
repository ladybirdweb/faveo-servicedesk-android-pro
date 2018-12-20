package co.servicedesk.faveo.pro.frontend.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.adapters.CollaboratorAdapter;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.CollaboratorSuggestion;
import co.servicedesk.faveo.pro.model.Data;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class CreateChange extends AppCompatActivity {

    ImageView imageViewback,refresh;
    AutoCompleteTextView editTextEmail;
    Button buttonsubmit;
    MultiAutoCompleteTextView assetItem;
    SpotsDialog dialog1;
    Animation rotation;
    ArrayList<Data> staffitemsauto,impactItems,statusItems,priorityItems,assetItems,changeTypeItems;
    Spinner impactSpinner,statusSpinner,prioritySpinner,changeTypeSpinner;
    ArrayAdapter<Data> impactAdapter,statusAdapetr,assetAdapter,priorityAdapter,changeTypeAdapter;
    ArrayList<CollaboratorSuggestion> emailHint;
    ArrayAdapter<CollaboratorSuggestion> arrayAdapterCC;
    int id1=0;
    boolean allCorrect;
    EditText editTextSubject,editTextDescrip;
    String assetListFinal;
    String email1="";
    StringBuilder sb1 = new StringBuilder();
    int idForequester=0;
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_change);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Window window = CreateChange.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(CreateChange.this,R.color.faveo));
        impactSpinner=findViewById(R.id.impact);
        prioritySpinner=findViewById(R.id.spinner_pri);
        statusSpinner=findViewById(R.id.spinner_status);
        assetItem=findViewById(R.id.addAsset);
        imageViewback=findViewById(R.id.imageViewBack);
        editTextEmail=findViewById(R.id.email_edittext);
        editTextEmail.addTextChangedListener(passwordWatcheredittextSubject);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        refresh=findViewById(R.id.imageRefresh);
        buttonsubmit=findViewById(R.id.buttonSubmit);
        editTextSubject=findViewById(R.id.sub_edittext);
        editTextDescrip=findViewById(R.id.msg_edittext);
        changeTypeSpinner=findViewById(R.id.spinner_changeType);
        emailHint=new ArrayList<>();
        arrayAdapterCC=new CollaboratorAdapter(this,emailHint);
        imageViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1= new SpotsDialog(CreateChange.this,getString(R.string.refreshConfirmation));
                dialog1.show();
                refresh.startAnimation(rotation);
                new FetchDependency("change").execute();
            }
        });

        editTextEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name1=editTextEmail.getText().toString();

                for (int i1=0;i1<emailHint.size();i1++){
                    CollaboratorSuggestion data1=emailHint.get(i1);
                    if (data1.getEmail().equals(name1)){
                        CollaboratorSuggestion data2=emailHint.get(i1);
                        email1=data2.getEmail();
                        idForequester=data2.getId();
                        Log.d("email1",email1);
                        editTextEmail.setText(email1);
                    }
                    else{
                        email1="";
                        idForequester=0;
                    }
                }

            }
        });

        prioritySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                return false;
            }
        });
        impactSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                return false;
            }
        });
        statusSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                return false;
            }
        });
        changeTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                return false;
            }
        });

        if (InternetReceiver.isConnected()){
            refresh.startAnimation(rotation);
            new FetchDependency("change").execute();

        }

        buttonsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Data priority= (Data) prioritySpinner.getSelectedItem();
                final Data status= (Data) statusSpinner.getSelectedItem();
                final Data impact= (Data) impactSpinner.getSelectedItem();
                final Data changeTypes= (Data) changeTypeSpinner.getSelectedItem();
                allCorrect = true;

                if (editTextSubject.getText().toString().equals("")){
                    Toasty.warning(CreateChange.this, getString(R.string.sub_must_not_be_empty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (priority.ID==0){
                    Toasty.warning(CreateChange.this, getString(R.string.please_select_some_priority), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (changeTypes.ID==0){
                    Toasty.warning(CreateChange.this, getString(R.string.select_changeType), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (status.ID==0){
                    Toasty.warning(CreateChange.this, getString(R.string.statusRequired), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (impact.ID==0){
                    Toasty.warning(CreateChange.this, getString(R.string.impactrequired), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (editTextDescrip.getText().toString().equals("")){
                    Toasty.warning(CreateChange.this, getString(R.string.descriptionEmpty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }

                String assetList=assetItem.getText().toString();
                String[] values = new String[0];
                assetListFinal = assetList.replaceAll("\\s+,$", "");
                assetListFinal = assetList.replaceAll(" ", "");
                sb1 = new StringBuilder();

                if (allCorrect) {
                    if (assetList.equals("")) {
                        String email = email1;
                        String subjec = editTextSubject.getText().toString();
                        String descrition = editTextDescrip.getText().toString();

                        try {
                            email = URLEncoder.encode(email.trim(), "utf-8");
                            subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                            descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        final String finalEmail = email;
                        final String finalSubjec = subjec;
                        final String finalDescrition = descrition;

                        new BottomDialog.Builder(CreateChange.this)
                                .setTitle("Creating change")
                                .setContent("Are you sure you want to create the change?")
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
                                        if (InternetReceiver.isConnected()) {
                                            if (InternetReceiver.isConnected()) {
                                                dialog1 = new SpotsDialog(CreateChange.this, getString(R.string.creating_problem));
                                                dialog1.show();
                                                new CreateChangeApi(idForequester, finalSubjec, status.ID, priority.ID, changeTypes.ID, impact.ID, finalDescrition).execute();
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
                    } else {
                        values = assetListFinal.split(",");
                        for (int i = 0; i < values.length; i++) {
                            String name = values[i];
                            for (int j = 0; j < assetItems.size(); j++) {
                                Data data = assetItems.get(j);
                                Log.d("beforeCondition", "true");
                                String finalname = data.getName().replace(" ", "");
                                if (finalname.equals(name)) {
                                    Log.d("beforeCondition", "false");
                                    sb1.append("&asset[]=").append(data.getID());
                                } else {

                                }
                            }
                            Log.d("name", sb1.toString());
                            String email = email1;
                            String subjec = editTextSubject.getText().toString();
                            String descrition = editTextDescrip.getText().toString();

                            try {
                                email = URLEncoder.encode(email.trim(), "utf-8");
                                subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                                descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            final String finalEmail = email;
                            final String finalSubjec = subjec;
                            final String finalDescrition = descrition;

                            new BottomDialog.Builder(CreateChange.this)
                                    .setTitle("Creating change")
                                    .setContent("Are you sure you want to create the change?")
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
                                            if (InternetReceiver.isConnected()) {
                                                if (InternetReceiver.isConnected()) {
                                                    dialog1 = new SpotsDialog(CreateChange.this, getString(R.string.creating_problem));
                                                    dialog1.show();
                                                    new CreateChangeApi(idForequester, finalSubjec, status.ID, priority.ID, changeTypes.ID, impact.ID, finalDescrition+sb1.toString()).execute();
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

            }
        });

    }
    private class CreateChangeApi extends AsyncTask<File, Void, String> {
        int from;
        String subject;
        String description;
        int status;
        int priority;
        int changeTypes;
        int impact;
        CreateChangeApi(int form, String subject,int status,
                      int priority,int changeTypes,int impact,String description) {
            this.from=form;
            this.subject = subject;
            this.status = status;
            this.priority = priority;
            this.changeTypes=changeTypes;
            this.impact = impact;
            this.description = description;

        }


        @Override
        protected String doInBackground(File... params) {


            return new Helpdesk().createChange(idForequester, subject,status, priority,changeTypes,impact,description);
        }

        protected void onPostExecute(String result) {
            //Toast.makeText(CreateTicketActivity.this, "api called", Toast.LENGTH_SHORT).show();
                dialog1.dismiss();
                Log.d("result",result);
            try{
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("error");
                JSONArray jsonArray=jsonObject1.getJSONArray("subject");
                for (int i=0;i<jsonArray.length();i++){
                    String message=jsonArray.getString(i);
                    if (message.equals("The subject should be less than 50 characters.")){
                        Toasty.warning(CreateChange.this,"The subject should be less than 50 characters.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            }catch (JSONException e){
                e.printStackTrace();
            }


            try{
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                if (data.equals("Changes Created.")){
                    Toasty.success(CreateChange.this,"change created successfully",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(CreateChange.this,ExistingChanges.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
    private class FetchDependency extends AsyncTask<String,Void,String> {
        String type;

        public FetchDependency(String type) {
            this.type = type;
        }

        protected String doInBackground(String... strings) {
            return new Helpdesk().getDependencyForServiceDesk(type);
        }

        protected void onPostExecute(String result) {
            refresh.clearAnimation();
            try {
                dialog1.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            Data data = null;
            try {
                JSONObject jsonObject = new JSONObject(result);
                priorityItems = new ArrayList<>();
                priorityItems.add(new Data(0, "--"));
                JSONArray jsonArrayPriority = jsonObject.getJSONArray("sd_changes_priorities");
                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArrayPriority.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArrayPriority.getJSONObject(i).getString("id")), jsonArrayPriority.getJSONObject(i).getString("name"));
                        priorityItems.add(data);
                    }
                }

                priorityAdapter = new ArrayAdapter<>(CreateChange.this, android.R.layout.simple_dropdown_item_1line, priorityItems);
                prioritySpinner.setAdapter(priorityAdapter);





                assetItems=new ArrayList<>();
                JSONArray jsonArrayAsset=jsonObject.getJSONArray("assets");
                Prefs.putString("assets",jsonArrayAsset.toString());
                for (int i=0;i<jsonArrayAsset.length();i++){
                    if (!jsonArrayAsset.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArrayAsset.getJSONObject(i).getString("id")), jsonArrayAsset.getJSONObject(i).getString("name"));
                        Prefs.putString(jsonArrayAsset.getJSONObject(i).getString("name"),jsonArrayAsset.getJSONObject(i).getString("id"));
                        assetItems.add(data);
                    }
                }
                assetAdapter = new ArrayAdapter<>(CreateChange.this, android.R.layout.simple_dropdown_item_1line, assetItems);
                assetItem.setAdapter(assetAdapter);

// set threshold value 1 that help us to start the searching from first character
                assetItem.setThreshold(1);
// set tokenizer that distinguish the various substrings by comma
                assetItem.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

                statusItems=new ArrayList<>();
                statusItems.add(new Data(0,"--"));
                JSONArray jsonArraystatus=jsonObject.getJSONArray("statuses");

                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArraystatus.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArraystatus.getJSONObject(i).getString("id")), jsonArraystatus.getJSONObject(i).getString("name"));
                        statusItems.add(data);
                    }
                }

                statusAdapetr = new ArrayAdapter<>(CreateChange.this, android.R.layout.simple_dropdown_item_1line, statusItems);
                statusSpinner.setAdapter(statusAdapetr);


                changeTypeItems=new ArrayList<>();
                changeTypeItems.add(new Data(0, "--"));
                JSONArray jsonArrayChangeTypes = jsonObject.getJSONArray("sd_changes_types");
                for (int i = 0; i < jsonArrayChangeTypes.length(); i++) {
                    if (!jsonArrayChangeTypes.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArrayChangeTypes.getJSONObject(i).getString("id")), jsonArrayChangeTypes.getJSONObject(i).getString("name"));
                        changeTypeItems.add(data);
                    }
                }

                changeTypeAdapter = new ArrayAdapter<>(CreateChange.this, android.R.layout.simple_dropdown_item_1line, changeTypeItems);
                changeTypeSpinner.setAdapter(changeTypeAdapter);


                impactItems=new ArrayList<>();
                impactItems.add(new Data(0,"--"));
                JSONArray jsonArrayImpact=jsonObject.getJSONArray("sd_impact_types");
                for (int i=0;i<jsonArrayImpact.length();i++){
                    if (!jsonArrayImpact.getJSONObject(i).getString("name").equals("")){
                        data = new Data(Integer.parseInt(jsonArrayImpact.getJSONObject(i).getString("id")), jsonArrayImpact.getJSONObject(i).getString("name"));
                        impactItems.add(data);
                    }
                }

                impactAdapter = new ArrayAdapter<>(CreateChange.this, android.R.layout.simple_dropdown_item_1line,impactItems);
                impactSpinner.setAdapter(impactAdapter);



                staffitemsauto=new ArrayList<>();
                staffitemsauto.add(new Data(0,"--"));
                JSONArray jsonArrayStaffs = jsonObject.getJSONArray("requester");
                for (int i = 0; i < jsonArrayStaffs.length(); i++) {
                    if (jsonArrayStaffs.getJSONObject(i).getString("first_name").equals("")&&jsonArrayStaffs.getJSONObject(i).getString("last_name").equals("")){
                        Log.d("cameHere","TRUE");
                        data = new Data(Integer.parseInt(jsonArrayStaffs.getJSONObject(i).getString("id")), jsonArrayStaffs.getJSONObject(i).getString("email"));
                        staffitemsauto.add(data);
                    }
                    else {
                        data = new Data(Integer.parseInt(jsonArrayStaffs.getJSONObject(i).getString("id")), jsonArrayStaffs.getJSONObject(i).getString("first_name")+" "+jsonArrayStaffs.getJSONObject(i).getString("last_name"));
                        staffitemsauto.add(data);
                    }
                }


            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }


    }
    private class FetchCollaborator extends AsyncTask<String, Void, String> {
        String term;

        FetchCollaborator(String term) {

            this.term = term;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getUser(term);
        }

        protected void onPostExecute(String result) {
            if (isCancelled()) return;
            refresh.clearAnimation();
            emailHint.clear();
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

//            if (result == null) {
//                Toasty.error(CollaboratorAdd.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                return;
//            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("users");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String email = jsonObject1.getString("email");
                    id1 = Integer.parseInt(jsonObject1.getString("id"));
                    String first_name=jsonObject1.getString("first_name");
                    String last_name=jsonObject1.getString("last_name");
                    String profilePic=jsonObject1.getString("profile_pic");
                    CollaboratorSuggestion collaboratorSuggestion=new CollaboratorSuggestion(id1,first_name,last_name,email,profilePic);
                    emailHint.add(collaboratorSuggestion);

                }
                editTextEmail.setThreshold(3);
                editTextEmail.setDropDownWidth(1500);
                editTextEmail.setAdapter(arrayAdapterCC);
                editTextEmail.showDropDown();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }


        }
    }
    TextWatcher passwordWatcheredittextSubject = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            //Toast.makeText(TicketSaveActivity.this, "API called", Toast.LENGTH_SHORT).show();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String term = editTextEmail.getText().toString();
            if (InternetReceiver.isConnected()) {
                if (!term.equals("")&&term.length()==3){

                    //progressBar.setVisibility(View.VISIBLE);
//                    refresh.startAnimation();
                    refresh.startAnimation(rotation);
                    String newTerm=term;
                    arrayAdapterCC=new CollaboratorAdapter(CreateChange.this,emailHint);
                    //arrayAdapterCC = new ArrayAdapter<Data>(CreateTicketActivity.this, android.R.layout.simple_dropdown_item_1line, emailHint);
                    new FetchCollaborator(newTerm.trim()).execute();

                    //stringArrayAdapterCC.notifyDataSetChanged();
//                autoCompleteTextViewCC.setThreshold(0);
//                autoCompleteTextViewCC.setDropDownWidth(1000);

                }



                //buttonsave.setEnabled(true);
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

}
