package co.servicedesk.faveo.pro.frontend.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class FeedBackActivity extends AppCompatActivity {
ImageView imageView;
String supportEmail="test00478@gmail.com";
Button buttonSend;
EditText editTextsubjectFeedback,editTextmessageFeedback,editTextemail;
String message,subject="";
ProgressDialog progressDialog;
String billingUrl,clientName,emailForFeedback,issueOrSuggestion;
CheckBox smoothCheckBoxIssue,smoothCheckBoxSuggestion;
LinearLayout textViewSuggestion,textViewIssue;
SpotsDialog dialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_feed_back);
        Window window = FeedBackActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(FeedBackActivity.this,R.color.mainActivityTopBar));
        imageView= (ImageView) findViewById(R.id.imageViewBack);
        editTextmessageFeedback= (EditText) findViewById(R.id.messageFeedback);
        editTextsubjectFeedback= (EditText) findViewById(R.id.subjectFeedback);
        buttonSend= (Button) findViewById(R.id.sendFeedback);
        editTextemail= (EditText) findViewById(R.id.emailfeedback);
        smoothCheckBoxIssue= (CheckBox) findViewById(R.id.issueCheckBox);
        smoothCheckBoxSuggestion= (CheckBox) findViewById(R.id.suggestionCheckBox);
        textViewSuggestion= (LinearLayout) findViewById(R.id.suggestion);
        textViewIssue= (LinearLayout) findViewById(R.id.issue);

        smoothCheckBoxSuggestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    //Toast.makeText(FeedBackActivity.this, "checked"+smoothCheckBoxSuggestion.getTag(), Toast.LENGTH_SHORT).show();
                    //smoothCheckBoxIssue.setVisibility(View.GONE);
                    textViewIssue.setVisibility(View.GONE);
                    issueOrSuggestion="Suggestion";
                }
                else{
                    textViewIssue.setVisibility(View.VISIBLE);
                    //smoothCheckBoxIssue.setVisibility(View.VISIBLE);
                }
            }
        });
        smoothCheckBoxIssue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                        //smoothCheckBoxSuggestion.setVisibility(View.GONE);
                    issueOrSuggestion="Issue";
                        textViewSuggestion.setVisibility(View.GONE);
                }else{
                    textViewSuggestion.setVisibility(View.VISIBLE);
                    //smoothCheckBoxSuggestion.setVisibility(View.VISIBLE);

                }
            }
        });
        editTextemail.setEnabled(false);
        progressDialog=new ProgressDialog(this);
        try {
            billingUrl = Prefs.getString("BASE_URL", null);
            clientName = Prefs.getString("clientNameForFeedback", null);
            emailForFeedback = Prefs.getString("emailForFeedback", null);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        Log.d("billingFromFeedback",billingUrl);
        Log.d("supportEmail",supportEmail);
        Log.d("emailForFeedback",emailForFeedback);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FeedBackActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message=editTextmessageFeedback.getText().toString();
                subject=editTextsubjectFeedback.getText().toString();
                StringBuilder stringBuffer=new StringBuilder(message);
                stringBuffer.append("URL: "+billingUrl);
                String newMessage="<B>Topic: </B>"+issueOrSuggestion+"<br/><br/><B>Message: </B>"+message+"<br/><br/><br/><B>URL: </B>"+billingUrl+"<br/><B>Name: </B>"+clientName+"<br/><B>Email: </B>"+emailForFeedback;

                if (message.equals("")&&subject.equals("")&&!smoothCheckBoxIssue.isChecked()&&!smoothCheckBoxSuggestion.isChecked()){
                    Toasty.info(FeedBackActivity.this,getString(R.string.pleaseSelectRequiredField), Toast.LENGTH_LONG).show();
                    return;
                }
                else if (subject.equals("")){
                    Toasty.info(FeedBackActivity.this,getString(R.string.selectSubject), Toast.LENGTH_LONG).show();
                    return;
                }
                else if (message.equals("")){
                    Toasty.info(FeedBackActivity.this,getString(R.string.selectMessage), Toast.LENGTH_LONG).show();
                    return;
                }
                else if (!smoothCheckBoxIssue.isChecked()&&!smoothCheckBoxSuggestion.isChecked()){
                    Toasty.info(FeedBackActivity.this,getString(R.string.pleaseChooseATopic),Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    if (InternetReceiver.isConnected()){
                        try {
                            message = URLEncoder.encode(newMessage.trim(), "utf-8");
                            subject=URLEncoder.encode(subject.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        new BottomDialog.Builder(FeedBackActivity.this)
                                .setTitle(getString(R.string.submitFeedback))
                                .setContent(getString(R.string.submittingConfirm))
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
                                            dialog1= new SpotsDialog(FeedBackActivity.this, getString(R.string.submitFeedback));
                                            dialog1.show();
                                            new CustomerFeedback(subject,message).execute();
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
    private class CustomerFeedback extends AsyncTask<String, Void, String> {
        String subject;
        String message;


        public CustomerFeedback(String subject, String message) {
            this.subject = subject;
            this.message = message;
        }

        protected String doInBackground(String... urls) {

            return new Helpdesk().customerFeedback(subject, message);
        }

        protected void onPostExecute(String result) {
           dialog1.dismiss();
//            Log.d("result",result);
            if (result == null) {
                Toasty.error(FeedBackActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }
//
//            if (result.startsWith("Message Sent!")){
//                Toasty.success(FeedBackActivity.this,getString(R.string.feedbackPosted),Toast.LENGTH_LONG).show();
//                Intent intent=new Intent(FeedBackActivity.this,SettingsActivity.class);
//                startActivity(intent);
//            }
//            String state= Prefs.getString("403",null);
//            try {
//                if (state.equals("403") && !state.equals("null")) {
//                    Toasty.warning(FeedBackActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
//                    Prefs.putString("403", "null");
//                    return;
//                }
//            }catch (NullPointerException e){
//                e.printStackTrace();
//            }

            try{
                JSONObject jsonObject=new JSONObject(result);
                //Log.d("feedbackresult",jsonObject.toString());
                String resultFromResponse=jsonObject.getString("message");
                if (resultFromResponse.equals("Message Sent! Thanks for reaching out! Someone from our team will get back to you soon.")){
                    Toasty.success(FeedBackActivity.this,getString(R.string.feedbackPosted),Toast.LENGTH_LONG).show();
                Intent intent=new Intent(FeedBackActivity.this,SettingsActivity.class);
                startActivity(intent);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }


//            try {
//
//                JSONObject jsonObject=new JSONObject(result);
//                JSONObject jsonObject1=jsonObject.getJSONObject("response");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


        }


    }
}
