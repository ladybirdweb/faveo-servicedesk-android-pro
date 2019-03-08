package co.servicedesk.faveo.pro.frontend.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.Data;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class NewRelease extends AppCompatActivity {

RelativeLayout relativeLayoutStartDate,relativeLayoutEndDate;
String startDate="",endDate="";
EditText editTextSubject,editTextDescription;
TextView editTextStart,editTextEnd;
MultiAutoCompleteTextView multiAutoCompleteTextViewAssets;
Spinner spinnerPriority,spinnerreleaseType,spinnerStatus;
ImageView imageViewRefresh;
Animation rotation;
ArrayList<Data> priorityItems,statusItems,releaseTypeItems,assetItems;
ArrayAdapter<Data> priorityAdapter,statusAdapetr,releaseTypeAdapter,assetAdapter;
Button submit;
boolean allCorrect;
String assetListFinal;
String condition;
ImageView imageViewBack;
StringBuilder sb1 = new StringBuilder();
int changeId;
SpotsDialog dialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_release);

        Window window = NewRelease.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(NewRelease.this,R.color.mainActivityTopBar));
        try {
            condition = Prefs.getString("needToAttachRelease", null);
            changeId=Prefs.getInt("changeId",0);
            Log.d("condition",condition);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        imageViewBack=findViewById(R.id.imageViewBack);
        imageViewRefresh=findViewById(R.id.imageRefresh);
        relativeLayoutStartDate=findViewById(R.id.startDateContainer);
        relativeLayoutEndDate=findViewById(R.id.endDateContainer);
        editTextStart=findViewById(R.id.startDate_edittext);
        editTextEnd=findViewById(R.id.endDate_edittext);
        spinnerPriority=findViewById(R.id.spinner_pri);
        spinnerStatus=findViewById(R.id.spinner_status);
        spinnerreleaseType=findViewById(R.id.releaseType);
        editTextSubject=findViewById(R.id.sub_edittext);
        editTextDescription=findViewById(R.id.msg_edittext);
        multiAutoCompleteTextViewAssets=findViewById(R.id.addAsset);
        submit=findViewById(R.id.buttonSubmit);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        //Getting the release dependency from the api
        if (InternetReceiver.isConnected()){
            imageViewRefresh.startAnimation(rotation);
            new FetchDependency("release").execute();

        }

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        editTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialogView = View.inflate(NewRelease.this, R.layout.date_time_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(NewRelease.this).create();

                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        SimpleDateFormat formatter = new SimpleDateFormat("d.MM.yyyy HH:mm");
                        String dateString = formatter.format(new Date(calendar.getTimeInMillis()));
                        Log.d("DATE",dateString);
                        startDate=dateString;
                        editTextStart.setText(startDate);
                        alertDialog.dismiss();
                    }});
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialogView = View.inflate(NewRelease.this, R.layout.date_time_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(NewRelease.this).create();

                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        SimpleDateFormat formatter = new SimpleDateFormat("d.MM.yyyy HH:mm");
                        String dateString = formatter.format(new Date(calendar.getTimeInMillis()));
                        Log.d("DATE",dateString);
                        endDate=dateString;
                        editTextEnd.setText(endDate);
                        alertDialog.dismiss();
                    }});
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Data priority= (Data) spinnerPriority.getSelectedItem();
                final Data status= (Data) spinnerStatus.getSelectedItem();
                final Data releseType= (Data) spinnerreleaseType.getSelectedItem();

                allCorrect=true;

                if (editTextSubject.getText().toString().equals("")){
                    Toasty.warning(NewRelease.this, getString(R.string.sub_must_not_be_empty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (editTextSubject.getText().toString().length()>50){
                    Toasty.warning(NewRelease.this,getString(R.string.changeSubject),Toast.LENGTH_LONG).show();
                    allCorrect=false;
                }
                if (priority.ID==0){
                    Toasty.warning(NewRelease.this, getString(R.string.please_select_some_priority), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (status.ID==0){
                    Toasty.warning(NewRelease.this, getString(R.string.statusRequired), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (releseType.ID==0){
                    Toasty.warning(NewRelease.this, getString(R.string.releaseType_required), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (editTextDescription.getText().toString().equals("")){
                    Toasty.warning(NewRelease.this, getString(R.string.descriptionEmpty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }

                String assetList=multiAutoCompleteTextViewAssets.getText().toString();
                String[] values = new String[0];
                assetListFinal = assetList.replaceAll("\\s+,$", "");
                assetListFinal = assetList.replaceAll(" ", "");
                sb1 = new StringBuilder();


                if (allCorrect) {
                    if (assetList.equals("")) {

                        if (condition.equals("true")) {
                            String subjec = editTextSubject.getText().toString();
                            String descrition = editTextDescription.getText().toString();
                            try {
                                subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                                descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            final String finalSubjec = subjec;
                            final String finalDescrition = descrition;

                            final String finalSubjec1 = subjec;
                            final String finalDescrition1 = descrition;
                            new BottomDialog.Builder(NewRelease.this)
                                    .setTitle("Creating release")
                                    .setContent("Are you sure you want to create the release?")
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
                                                    dialog1 = new SpotsDialog(NewRelease.this,"Creating release");
                                                    dialog1.show();
                                                    new CreateReleaseAndAttach(changeId,finalSubjec1, finalDescrition1,status.ID,priority.ID,releseType.ID,startDate,endDate).execute();

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
                            String subjec = editTextSubject.getText().toString();
                            String descrition = editTextDescription.getText().toString();

                            try {
                                subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                                descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            final String finalSubjec1 = subjec;
                            final String finalDescrition1 = descrition;
                            new BottomDialog.Builder(NewRelease.this)
                                    .setTitle("Creating release")
                                    .setContent("Are you sure you want to create the release?")
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
                                                    dialog1 = new SpotsDialog(NewRelease.this,"Creating release");
                                                    dialog1.show();
                                                    new CreateRelease(finalSubjec1, finalDescrition1,status.ID,priority.ID,releseType.ID,startDate,endDate).execute();
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

                    else if (assetListFinal.charAt(assetListFinal.length()-1)==','){
                        if (condition.equals("true")){
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
                                String subjec = editTextSubject.getText().toString();
                                String descrition = editTextDescription.getText().toString();

                                try {
                                    subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                                    descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                final String finalSubjec = subjec;
                                final String finalDescrition = descrition;

                                final String finalSubjec1 = subjec;
                                final String finalDescrition1 = descrition;
                                new BottomDialog.Builder(NewRelease.this)
                                        .setTitle("Creating release")
                                        .setContent("Are you sure you want to create the release?")
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
                                                        dialog1 = new SpotsDialog(NewRelease.this,"Creating release");
                                                    dialog1.show();
                                                    new CreateReleaseAndAttach(changeId,finalSubjec1, finalDescrition1,status.ID,priority.ID,releseType.ID,startDate,endDate+sb1.toString()).execute();
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
                        else{
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
                                String subjec = editTextSubject.getText().toString();
                                String descrition = editTextDescription.getText().toString();

                                try {
                                    subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                                    descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                final String finalSubjec = subjec;
                                final String finalDescrition = descrition;

                                final String finalSubjec1 = subjec;
                                final String finalDescrition1 = descrition;
                                new BottomDialog.Builder(NewRelease.this)
                                        .setTitle("Creating release")
                                        .setContent("Are you sure you want to create the release?")
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
                                                        dialog1 = new SpotsDialog(NewRelease.this,"Creating release");
                                                        dialog1.show();
                                                        new CreateRelease(finalSubjec1, finalDescrition1,status.ID,priority.ID,releseType.ID,startDate,endDate+sb1.toString()).execute();
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
                    else if (!assetListFinal.equals("")){
                        if (condition.equals("true")){
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
                                String subjec = editTextSubject.getText().toString();
                                String descrition = editTextDescription.getText().toString();

                                try {
                                    subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                                    descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                final String finalSubjec = subjec;
                                final String finalDescrition = descrition;

                                final String finalSubjec1 = subjec;
                                final String finalDescrition1 = descrition;
                                new BottomDialog.Builder(NewRelease.this)
                                        .setTitle("Creating release")
                                        .setContent("Are you sure you want to create the release?")
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
                                                        dialog1 = new SpotsDialog(NewRelease.this,"Creating release");
                                                        dialog1.show();
                                                        new CreateReleaseAndAttach(changeId,finalSubjec1, finalDescrition1,status.ID,priority.ID,releseType.ID,startDate,endDate+sb1.toString()).execute();
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
                        else{
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
                                String subjec = editTextSubject.getText().toString();
                                String descrition = editTextDescription.getText().toString();

                                try {
                                    subjec = URLEncoder.encode(subjec.trim(), "utf-8");
                                    descrition = URLEncoder.encode(descrition.trim(), "utf-8");

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                final String finalSubjec = subjec;
                                final String finalDescrition = descrition;

                                final String finalDescrition1 = descrition;
                                final String finalSubjec1 = subjec;
                                new BottomDialog.Builder(NewRelease.this)
                                        .setTitle("Creating release")
                                        .setContent("Are you sure you want to create the release?")
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
                                                        dialog1 = new SpotsDialog(NewRelease.this,"Creating release");
                                                        dialog1.show();
                                                        new CreateRelease(finalSubjec1, finalDescrition1,status.ID,priority.ID,releseType.ID,startDate,endDate+sb1.toString()).execute();
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

            }
        });



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
            imageViewRefresh.clearAnimation();
            Data data = null;
            try {
                JSONObject jsonObject = new JSONObject(result);
                priorityItems = new ArrayList<>();
                priorityItems.add(new Data(0, "--"));
                JSONArray jsonArrayPriority = jsonObject.getJSONArray("sd_release_priorities");
                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArrayPriority.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArrayPriority.getJSONObject(i).getString("id")), jsonArrayPriority.getJSONObject(i).getString("name"));
                        priorityItems.add(data);
                    }
                }

                priorityAdapter = new ArrayAdapter<>(NewRelease.this, android.R.layout.simple_dropdown_item_1line, priorityItems);
                spinnerPriority.setAdapter(priorityAdapter);


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
                assetAdapter = new ArrayAdapter<>(NewRelease.this, android.R.layout.simple_dropdown_item_1line, assetItems);
                multiAutoCompleteTextViewAssets.setAdapter(assetAdapter);

// set threshold value 1 that help us to start the searching from first character
                multiAutoCompleteTextViewAssets.setThreshold(1);
// set tokenizer that distinguish the various substrings by comma
                multiAutoCompleteTextViewAssets.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
//
                statusItems=new ArrayList<>();
                statusItems.add(new Data(0,"--"));
                JSONArray jsonArraystatus=jsonObject.getJSONArray("sd_release_status");
//
                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArraystatus.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArraystatus.getJSONObject(i).getString("id")), jsonArraystatus.getJSONObject(i).getString("name"));
                        statusItems.add(data);
                    }
                }

                statusAdapetr = new ArrayAdapter<>(NewRelease.this, android.R.layout.simple_dropdown_item_1line, statusItems);
                spinnerStatus.setAdapter(statusAdapetr);


                releaseTypeItems=new ArrayList<>();
                releaseTypeItems.add(new Data(0, "--"));
                JSONArray jsonArrayChangeTypes = jsonObject.getJSONArray("sd_release_types");
                for (int i = 0; i < jsonArrayChangeTypes.length(); i++) {
                    if (!jsonArrayChangeTypes.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArrayChangeTypes.getJSONObject(i).getString("id")), jsonArrayChangeTypes.getJSONObject(i).getString("name"));
                        releaseTypeItems.add(data);
                    }
                }

                releaseTypeAdapter = new ArrayAdapter<>(NewRelease.this, android.R.layout.simple_dropdown_item_1line, releaseTypeItems);
                spinnerreleaseType.setAdapter(releaseTypeAdapter);


            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }


    }

    //API for creating release

    private class CreateRelease extends AsyncTask<File, Void, String> {
        String subject;
        String description;
        int status;
        int priority;
        int releaseType;
        String startDate,endDate;
        CreateRelease(String subject,String description,int status,
                        int priority,int releaseType,String startDate,String endDate) {
            this.subject = subject;
            this.description = description;
            this.status = status;
            this.priority = priority;
            this.releaseType=releaseType;
            this.startDate=startDate;
            this.endDate=endDate;


        }


        @Override
        protected String doInBackground(File... params) {


            return new Helpdesk().createRelease(subject,description,status, priority,releaseType,startDate,endDate);
        }

        protected void onPostExecute(String result) {
            //Toast.makeText(CreateTicketActivity.this, "api called", Toast.LENGTH_SHORT).show();
            Log.d("result",result);
            try{
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("error");
                JSONArray jsonArray=jsonObject1.getJSONArray("subject");
                for (int i=0;i<jsonArray.length();i++){
                    String message=jsonArray.getString(i);
                    if (message.equals("The subject should be less than 50 characters.")){
                        Toasty.warning(NewRelease.this,"The subject should be less than 50 characters.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            }catch (JSONException e){
                e.printStackTrace();
            }


            try{
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                if (data.equals("Release Created Successfully.")){
                    Toasty.success(NewRelease.this,"Release Created Successfully.",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(NewRelease.this,MainActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    private class CreateReleaseAndAttach extends AsyncTask<File, Void, String>{
        int changeId;
        String subject;
        String description;
        int status;
        int priority;
        int releaseType;
        String startDate,endDate;

        public CreateReleaseAndAttach(int changeId, String subject, String description, int status, int priority, int releaseType, String startDate, String endDate) {
            this.changeId = changeId;
            this.subject = subject;
            this.description = description;
            this.status = status;
            this.priority = priority;
            this.releaseType = releaseType;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        protected String doInBackground(File... files) {
            return new Helpdesk().newReleaseWithChange(changeId,subject,description,status,priority,releaseType,startDate,endDate);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("result",s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                JSONObject jsonObject1=jsonObject.getJSONObject("error");
                JSONArray jsonArray=jsonObject1.getJSONArray("subject");
                for (int i=0;i<jsonArray.length();i++){
                    String message=jsonArray.getString(i);
                    if (message.equals("The subject should be less than 50 characters.")){
                        Toasty.warning(NewRelease.this,"The subject should be less than 50 characters.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            }catch (JSONException e){
                e.printStackTrace();
            }


            try{
                JSONObject jsonObject=new JSONObject(s);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String data=jsonObject1.getString("success");
                if (data.equals("Release Created Successfully.")){
                    Toasty.success(NewRelease.this,"Release Created Successfully.",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(NewRelease.this,MainActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
