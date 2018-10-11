package co.servicedesk.faveo.pro.frontend.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;
import com.pixplicity.easyprefs.library.Prefs;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import co.servicedesk.faveo.pro.Constants;
import co.servicedesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.adapters.CollaboratorAdapter;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.CollaboratorSuggestion;
import co.servicedesk.faveo.pro.model.Data;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class NewProblem extends AppCompatActivity implements PermissionCallback, ErrorCallback {
    TextView attachmentFileName;
    RelativeLayout attachment_layout;
    BottomSheetLayout bottomSheet;
    Animation rotation;
    AutoCompleteTextView editTextEmail;
    EditText editTextsubject,editTextDescription;
    ImageView refresh,imageBack;
    ArrayList<CollaboratorSuggestion> emailHint;
    ArrayAdapter<CollaboratorSuggestion> arrayAdapterCC;
    int id=0;
    int id1=0;
    ArrayList<Data> staffitemsauto,departmentItems,impactItems,statusItems,priorityItems,assetItems;
    Spinner staffautocompletetextview,departmentSpinner,impactSpinner,statusSpinner,prioritySpinner;
    ArrayAdapter<Data> autocompletetextview,departmentAdapter,impactAdapter,statusAdapetr,assetAdapter,priorityAdapter;
    int gallery,document,camera,audio=0;
    private static final int PICKFILE_REQUEST_CODE = 1234;
    Button button,buttonSubmit;
    MultiAutoCompleteTextView assetItem;
    String path="";
    boolean allCorrect;
    String email1;
    SpotsDialog dialog1;
    StringBuilder sb1 = new StringBuilder();
    String assetListFinal;
    ArrayList<String> getStaffItems;
    String condition;
    int ticketId;
    String token;
    String assets=null;
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
        setContentView(R.layout.activity_new_problem);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Window window = NewProblem.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(NewProblem.this,R.color.faveo));
        bottomSheet= (BottomSheetLayout) findViewById(R.id.bottomsheet);
        ImageButton imageButton= (ImageButton) findViewById( R.id.attachment_close);
        button= (Button) findViewById(R.id.attachment);
        attachment_layout=findViewById(R.id.attachment_layout);
        attachmentFileName=findViewById(R.id.attachment_name);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        editTextEmail=findViewById(R.id.email_edittext);
        refresh=findViewById(R.id.imageRefresh);
        buttonSubmit=findViewById(R.id.buttonSubmit);
        emailHint=new ArrayList<>();
        getStaffItems = new ArrayList<>();
        arrayAdapterCC=new CollaboratorAdapter(this,emailHint);
        //arrayAdapterCC=new ArrayAdapter<Data>(CreateTicketActivity.this,android.R.layout.simple_dropdown_item_1line,emailHint);
        editTextEmail.addTextChangedListener(passwordWatcheredittextSubject);
        imageBack=findViewById(R.id.imageViewBack);
        staffautocompletetextview=findViewById(R.id.autocompletetext);
        departmentSpinner=findViewById(R.id.department);
        statusSpinner=findViewById(R.id.spinner_status);
        prioritySpinner=findViewById(R.id.spinner_pri);
        assetItem=findViewById(R.id.addAsset);
        impactSpinner=findViewById(R.id.impact);
        editTextsubject=findViewById(R.id.sub_edittext);
        editTextDescription=findViewById(R.id.msg_edittext);
        try {
            ticketId = Integer.parseInt(Prefs.getString("TICKETid", null));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        try {
            condition = Prefs.getString("cameFromMain", null);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
       departmentSpinner.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
               return false;
           }
       });
       staffautocompletetextview.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
               return false;
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
                        Log.d("email1",email1);
                        editTextEmail.setText(email1);
                    }
                }

            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View view = NewProblem.this.getCurrentFocus();
//                if (view != null) {
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                MenuSheetView menuSheetView =
//                        new MenuSheetView(NewProblem.this, MenuSheetView.MenuType.LIST, "Choose...", new MenuSheetView.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                if (bottomSheet.isSheetShowing()) {
//                                    bottomSheet.dismissSheet();
//                                }
//                                if (item.getItemId()==R.id.imageGalley){
//                                    gallery=2;
//                                    reqPermissionCamera();
//                                    return true;
//                                }
//                                else if (item.getItemId()==R.id.videoGallery){
//                                    camera=3;
//                                    reqPermissionCamera();
//                                    return true;
//                                }
//                                else if (item.getItemId()==R.id.musicGallery){
//                                    audio=4;
//                                    reqPermissionCamera();
//                                    return true;
//                                }
//                                else if (item.getItemId()==R.id.documentGallery){
//                                    document=1;
//                                    reqPermissionCamera();
//                                    return true;
//                                }
//
//                                return true;
//                            }
//                        });
//                menuSheetView.inflateMenu(R.menu.navigation);
//                bottomSheet.showWithSheetView(menuSheetView);
//
////                if (bottomNavigationView.getVisibility()==View.GONE){
////                    bottomNavigationView.setVisibility(View.VISIBLE);
////                }
////                else if (bottomNavigationView.getVisibility()==View.VISIBLE){
////                    bottomNavigationView.setVisibility(View.GONE);
////                }
//
//
//            }
//        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachment_layout.setVisibility(View.GONE);
                attachmentFileName.setText("");
                //attachmentFileSize.setText("");
                path="";
                //toolbarAttachment.setVisibility(View.GONE);
            }
        });
        if (InternetReceiver.isConnected()){
            refresh.startAnimation(rotation);
            new FetchDependencyForProblem("problem").execute();
        }

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh.startAnimation(rotation);
                new FetchDependencyForProblem("problem").execute();
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Data priority = (Data) prioritySpinner.getSelectedItem();
                final Data impact = (Data) impactSpinner.getSelectedItem();
                final Data status = (Data) statusSpinner.getSelectedItem();
                final Data department = (Data) departmentSpinner.getSelectedItem();
                final Data staff= (Data) staffautocompletetextview.getSelectedItem();
                allCorrect = true;

                if (editTextEmail.getText().toString().equals("")){
                    Toasty.warning(NewProblem.this, getString(R.string.selectUser), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
               else if (!Helper.isValidEmail(editTextEmail.getText().toString())){
                    Toasty.warning(NewProblem.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
               else if (editTextsubject.getText().toString().equals("")){
                    Toasty.warning(NewProblem.this, getString(R.string.sub_must_not_be_empty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
             else if (editTextDescription.getText().toString().equals("")){
                    Toasty.warning(NewProblem.this, getString(R.string.descriptionEmpty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                else if (priority.ID == 0) {
                    Toasty.warning(NewProblem.this, getString(R.string.please_select_some_priority), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                } else if (impact.ID == 0) {
                    Toasty.warning(NewProblem.this, getString(R.string.impactrequired), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                else if (status.ID==0){
                    Toasty.warning(NewProblem.this, getString(R.string.statusRequired), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                else if (department.ID==0){
                    Toasty.warning(NewProblem.this, getString(R.string.departmentRequired), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }

                String assetList=assetItem.getText().toString();
                String[] values = new String[0];
                assetListFinal = assetList.replaceAll("\\s+,$", "");
                assetListFinal = assetList.replaceAll(" ", "");
                sb1 = new StringBuilder();


                if (allCorrect){
                    if (InternetReceiver.isConnected()){
                        if (assetList.equals("")){

                            if (condition.equals("True")){
                                String email=email1;
                                String subjec=editTextsubject.getText().toString();
                                String descrition=editTextDescription.getText().toString();

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
                                new BottomDialog.Builder(NewProblem.this)
                                        .setTitle(R.string.creating_problem)
                                        .setContent(R.string.problem_confirm)
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
                                                        dialog1= new SpotsDialog(NewProblem.this, getString(R.string.creating_problem));
                                                        dialog1.show();
                                                        new CreateProblem(finalEmail, finalSubjec,status.ID,priority.ID,impact.ID,department.ID,staff.ID,finalDescrition).execute();
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
                            else{
                                String email=email1;
                                String subjec=editTextsubject.getText().toString();
                                String descrition=editTextDescription.getText().toString();

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
                                new BottomDialog.Builder(NewProblem.this)
                                        .setTitle(R.string.creating_problem)
                                        .setContent(R.string.problem_confirm)
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
                                                        dialog1= new SpotsDialog(NewProblem.this, getString(R.string.creating_problem));
                                                        dialog1.show();
                                                        new CreateAndAttach(ticketId,finalEmail, finalSubjec,status.ID,priority.ID,impact.ID,department.ID,staff.ID,finalDescrition).execute();
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

                            if (condition.equals("True")){
                                Log.d("assetFinal",assetListFinal);
                                values = assetListFinal.split(",");
                                for (int i=0;i<values.length;i++){
                                    Log.d("cameHere","True");
                                    Data data=assetItems.get(i);
                                    sb1.append("&asset[]=").append(data.getID());

                                }
                                Log.d("assetList",sb1.toString());
                                String email=email1;
                                String subjec=editTextsubject.getText().toString();
                                String descrition=editTextDescription.getText().toString();

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
                                new BottomDialog.Builder(NewProblem.this)
                                        .setTitle(R.string.creating_problem)
                                        .setContent(R.string.problem_confirm)
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
                                                        dialog1= new SpotsDialog(NewProblem.this, getString(R.string.creating_problem));
                                                        dialog1.show();
                                                        new CreateProblem(finalEmail, finalSubjec,status.ID,priority.ID,impact.ID,department.ID,staff.ID,finalDescrition+sb1.toString()).execute();
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
                            else {
                                //                                Log.d("assetFinal",assetListFinal);
                                values = assetListFinal.split(",");
                                for (int i=0;i<values.length;i++){
                                    Log.d("cameHere","True");
                                    Data data=assetItems.get(i);
                                    sb1.append("&asset[]=").append(data.getID());

                                }
                                Log.d("assetList",sb1.toString());
                                String email=email1;
                                String subjec=editTextsubject.getText().toString();
                                String descrition=editTextDescription.getText().toString();

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
                                new BottomDialog.Builder(NewProblem.this)
                                        .setTitle(R.string.creating_problem)
                                        .setContent(R.string.problem_confirm)
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
                                                        dialog1= new SpotsDialog(NewProblem.this, getString(R.string.creating_problem));
                                                        dialog1.show();
                                                        new CreateAndAttach(ticketId,finalEmail, finalSubjec,status.ID,priority.ID,impact.ID,department.ID,staff.ID,finalDescrition+sb1.toString()).execute();
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
//                            else if (condition.equals("False")){
//                                Log.d("assetFinal",assetListFinal);
//                                values = assetListFinal.split(",");
//                                for (int i=0;i<values.length;i++){
//                                    Log.d("cameHere","True");
//                                    Data data=assetItems.get(i);
//                                    sb1.append("&asset[]=").append(data.getID());
//
//                                }
//                                Log.d("assetList",sb1.toString());
//                                String email=email1;
//                                String subjec=editTextsubject.getText().toString();
//                                String descrition=editTextDescription.getText().toString();
//
//                                try {
//                                    email = URLEncoder.encode(email.trim(), "utf-8");
//                                    subjec = URLEncoder.encode(subjec.trim(), "utf-8");
//                                    descrition = URLEncoder.encode(descrition.trim(), "utf-8");
//
//                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                }
//                                final String finalEmail = email;
//                                final String finalSubjec = subjec;
//                                final String finalDescrition = descrition;
//                                new BottomDialog.Builder(NewProblem.this)
//                                        .setTitle(R.string.creating_problem)
//                                        .setContent(R.string.problem_confirm)
//                                        .setPositiveText("YES")
//                                        .setNegativeText("NO")
//                                        .setPositiveBackgroundColorResource(R.color.white)
//                                        //.setPositiveBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)
//                                        .setPositiveTextColorResource(R.color.faveo)
//                                        .setNegativeTextColor(R.color.black)
//                                        //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
//                                        .onPositive(new BottomDialog.ButtonCallback() {
//                                            @Override
//                                            public void onClick(BottomDialog dialog) {
//                                                if (InternetReceiver.isConnected()){
//                                                    if (InternetReceiver.isConnected()){
//                                                        dialog1= new SpotsDialog(NewProblem.this, getString(R.string.creating_problem));
//                                                        dialog1.show();
//                                                        new CreateAndAttach(ticketId,finalEmail, finalSubjec,status.ID,priority.ID,impact.ID,department.ID,staff.ID,finalDescrition+sb1.toString()).execute();
//                                                    }
//                                                }
//                                            }
//                                        }).onNegative(new BottomDialog.ButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull BottomDialog bottomDialog) {
//                                        bottomDialog.dismiss();
//                                    }
//                                })
//                                        .show();
//                            }



                        }


                    }

                }
            }
        });

    }






    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                case 3:
                    break;
                //Read External Storage
                case 4:
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent, 11);
                    break;
                //Camera
                case 5:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 12);
                    }
                    break;

            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        String fileName;
        String filePath = null;
        try {
            if (data.toString().equals("")) {
                Log.d("data",data.toString());
                attachment_layout.setVisibility(View.GONE);
            } else {
                attachment_layout.setVisibility(View.VISIBLE);
                Log.d("data",data.toString());
                switch (requestCode){
                    case Constant.REQUEST_CODE_PICK_IMAGE:
                        if (resultCode==RESULT_OK){
                            ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                            StringBuilder builder = new StringBuilder();
                            for (ImageFile file : list) {
                                filePath = file.getPath();
                                Log.d("filePath",path);
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length()/1024;
                            if (size > 6000) {
                                Toasty.info(NewProblem.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos=path.lastIndexOf("/");
                                fileName=path.substring(pos+1,path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName",fileName);
                            }
                        }
                        break;
                    case Constant.REQUEST_CODE_PICK_VIDEO:
                        if (resultCode == RESULT_OK) {
                            ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                            StringBuilder builder = new StringBuilder();
                            for (VideoFile file : list) {
                                filePath = file.getPath();
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length()/1024;
                            if (size > 6000) {
                                Toasty.info(NewProblem.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos=path.lastIndexOf("/");
                                fileName=path.substring(pos+1,path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName",fileName);
                            }
                        }
                        break;
                    case Constant.REQUEST_CODE_PICK_AUDIO:
                        if (resultCode == RESULT_OK) {
                            ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                            StringBuilder builder = new StringBuilder();
                            for (AudioFile file : list) {
                                filePath = file.getPath();
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length()/1024;
                            if (size > 6000) {
                                Toasty.info(NewProblem.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos=path.lastIndexOf("/");
                                fileName=path.substring(pos+1,path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName",fileName);
                            }
                        }
                        break;
                    case Constant.REQUEST_CODE_PICK_FILE:
                        if (resultCode == RESULT_OK) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            StringBuilder builder = new StringBuilder();
                            for (NormalFile file : list) {
                                filePath = file.getPath();
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length()/1024;
                            if (size > 6000) {
                                Toasty.info(NewProblem.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos=path.lastIndexOf("/");
                                fileName=path.substring(pos+1,path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName",fileName);
                            }
                        }
                        break;

                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    private void reqPermissionCamera() {
        new AskPermission.Builder(this).setPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setCallback(this)
                .setErrorCallback(this)
                .request(PICKFILE_REQUEST_CODE);
    }

    @Override
    public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app.");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onDialogShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    @Override
    public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app. Open setting screen?");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onSettingsShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Log.d("requestCode",""+requestCode);

        if (document==1){
            Intent intent4 = new Intent(this, NormalFilePickActivity.class);
            intent4.putExtra(Constant.MAX_NUMBER, 1);
            //intent4.putExtra(IS_NEED_FOLDER_LIST, true);
            intent4.putExtra(NormalFilePickActivity.SUFFIX,
                    new String[] {"xlsx", "xls", "doc", "dOcX", "ppt", ".pptx", "pdf"});
            startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
//            new MaterialFilePicker()
//                    .withActivity(TicketReplyActivity.this)
//                    .withRequestCode(FILE_PICKER_REQUEST_CODE)
//                    .withHiddenFiles(true)
//                    .start();
            document=0;
        }
        if (gallery==2){
            Intent intent1 = new Intent(this, ImagePickActivity.class);
            intent1.putExtra(IS_NEED_CAMERA, true);
            intent1.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
//            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.setType("image/*");
//            startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            gallery=0;
        }

        if (camera==3){
            Intent intent2 = new Intent(this, VideoPickActivity.class);
            intent2.putExtra(IS_NEED_CAMERA, true);
            intent2.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
//            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//            startActivityForResult(intent, CAMERA_REQUEST);
            camera=0;
        }
        if (audio==4){
            Intent intent3 = new Intent(this, AudioPickActivity.class);
            intent3.putExtra(IS_NEED_RECORDER, true);
            intent3.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
            audio=0;

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        Toasty.warning(NewProblem.this,getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
    }

    private class CreateProblem extends AsyncTask<File, Void, String> {
        String from;
        String subject;
        String description;
        int status;
        int priority;
        int dept;
        int staff;
        int impact;
        CreateProblem(String form, String subject,int status,
                      int priority,int impact,int dept,int staff,String description) {
            this.from=form;
            this.subject = subject;
            this.status = status;
            this.priority = priority;
            this.impact = impact;
            this.dept = dept;
            this.staff=staff;
            this.description = description;

        }


        @Override
        protected String doInBackground(File... params) {


            return new Helpdesk().createProblem(from, subject,status, priority,impact,dept,staff,description +sb1.toString());
        }

        protected void onPostExecute(String result) {
            //Toast.makeText(CreateTicketActivity.this, "api called", Toast.LENGTH_SHORT).show();

            dialog1.dismiss();

            try{
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String success=jsonObject1.getString("success");
                if (success.equals("Problem Created Successfully.")){
                    Intent intent=new Intent(NewProblem.this,ExistingProblems.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }



    private class CreateAndAttach extends AsyncTask<File, Void, String> {
        int ticketId;
        String from;
        String subject;
        String description;
        int status;
        int priority;
        int dept;
        int staff;
        int impact;
        CreateAndAttach(int ticketId,String form, String subject,int status,
                      int priority,int impact,int dept,int staff,String description) {
            this.ticketId=ticketId;
            this.from=form;
            this.subject = subject;
            this.status = status;
            this.priority = priority;
            this.impact = impact;
            this.dept = dept;
            this.staff=staff;
            this.description = description;

        }

//        protected String doInBackground(String... urls) {
//
        //return new Helpdesk().postCreateTicket(userID, subject, body, helpTopic, priority, fname, lname, phone, email, code, staff, mobile+ collaborators, new File[]{new File(result)});
//        }

        @Override
        protected String doInBackground(File... files) {


            return new Helpdesk().createProblemAndAttach(ticketId,from, subject,status, priority,impact,dept,staff,description);
        }

        protected void onPostExecute(String result) {
            //Toast.makeText(CreateTicketActivity.this, "api called", Toast.LENGTH_SHORT).show();

            dialog1.dismiss();

            try{
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                if (data.equals("Created new problem and attached to this ticket")){
                    Toasty.success(NewProblem.this,"successfully created the problem and attached to this ticket",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(NewProblem.this,TicketDetailActivity.class);
                    Prefs.putString("cameFromNewProblem","true");
                    startActivity(intent);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    private class FetchDependencyForProblem extends AsyncTask<String,Void,String>{
        String type;

        public FetchDependencyForProblem(String type) {
            this.type = type;
        }

        protected String doInBackground(String... strings) {
            return new Helpdesk().getDependencyForServiceDesk(type);
        }

        protected void onPostExecute(String result) {
            refresh.clearAnimation();
            Data data = null;
            try {
                JSONObject jsonObject = new JSONObject(result);
                priorityItems = new ArrayList<>();
                priorityItems.add(new Data(0, "--"));
                JSONArray jsonArrayPriority = jsonObject.getJSONArray("priority_ids");
                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArrayPriority.getJSONObject(i).getString("priority").equals("")) {
                        data = new Data(Integer.parseInt(jsonArrayPriority.getJSONObject(i).getString("priority_id")), jsonArrayPriority.getJSONObject(i).getString("priority"));
                        priorityItems.add(data);
                    }
                }

                priorityAdapter = new ArrayAdapter<>(NewProblem.this, android.R.layout.simple_dropdown_item_1line, priorityItems);
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
                assetAdapter = new ArrayAdapter<>(NewProblem.this, android.R.layout.simple_dropdown_item_1line, assetItems);
                assetItem.setAdapter(assetAdapter);

// set threshold value 1 that help us to start the searching from first character
                assetItem.setThreshold(1);
// set tokenizer that distinguish the various substrings by comma
                assetItem.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

                statusItems=new ArrayList<>();
                statusItems.add(new Data(0,"--"));
                JSONArray jsonArraystatus=jsonObject.getJSONArray("status_type_ids");

                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArraystatus.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArraystatus.getJSONObject(i).getString("id")), jsonArraystatus.getJSONObject(i).getString("name"));
                        statusItems.add(data);
                    }
                }

                statusAdapetr = new ArrayAdapter<>(NewProblem.this, android.R.layout.simple_dropdown_item_1line, statusItems);
                statusSpinner.setAdapter(statusAdapetr);






                departmentItems=new ArrayList<>();
                departmentItems.add(new Data(0,"--"));
                JSONArray jsonArrayDepartment=jsonObject.getJSONArray("departments");
                for (int i=0;i<jsonArrayDepartment.length();i++){
                   if (!jsonArrayDepartment.getJSONObject(i).getString("name").equals("")){
                       data = new Data(Integer.parseInt(jsonArrayDepartment.getJSONObject(i).getString("id")), jsonArrayDepartment.getJSONObject(i).getString("name"));
                       departmentItems.add(data);
                   }
                }

                departmentAdapter = new ArrayAdapter<>(NewProblem.this, android.R.layout.simple_dropdown_item_1line,departmentItems);
                departmentSpinner.setAdapter(departmentAdapter);


                impactItems=new ArrayList<>();
                impactItems.add(new Data(0,"--"));
                JSONArray jsonArrayImpact=jsonObject.getJSONArray("impact_ids");
                for (int i=0;i<jsonArrayImpact.length();i++){
                    if (!jsonArrayImpact.getJSONObject(i).getString("name").equals("")){
                        data = new Data(Integer.parseInt(jsonArrayImpact.getJSONObject(i).getString("id")), jsonArrayImpact.getJSONObject(i).getString("name"));
                        impactItems.add(data);
                    }
                }

                impactAdapter = new ArrayAdapter<>(NewProblem.this, android.R.layout.simple_dropdown_item_1line,impactItems);
                impactSpinner.setAdapter(impactAdapter);



                staffitemsauto=new ArrayList<>();
                staffitemsauto.add(new Data(0,"--"));
                JSONArray jsonArrayStaffs = jsonObject.getJSONArray("assigned_ids");
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


                autocompletetextview = new ArrayAdapter<>(NewProblem.this, android.R.layout.simple_dropdown_item_1line,staffitemsauto);
                staffautocompletetextview.setAdapter(autocompletetextview);
                Collections.sort(staffitemsauto, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });

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
//                Toasty.error(collaboratorAdd.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
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
                    arrayAdapterCC=new CollaboratorAdapter(NewProblem.this,emailHint);
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
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
            //Toast.makeText(getApplicationContext(), result,
            //Toast.LENGTH_LONG).show();
            Log.d("resultFromNewCall",result);
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                token = jsonObject1.getString("token");
                Prefs.putString("TOKEN", token);
                Log.d("TOKEN",token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            try {
//                uploadMultipartData();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }

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

    @Override
    public void onBackPressed() {
        finish();
    }
}
