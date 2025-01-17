package co.servicedesk.faveo.pro.frontend.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class InternalNoteActivity extends AppCompatActivity {
    ImageView imageView;
    EditText editTextInternalNote;
    Button buttonCreate;
    public static String ticketID;
    ProgressDialog progressDialog;
    SpotsDialog dialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_note);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Window window = InternalNoteActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(InternalNoteActivity.this,R.color.mainActivityTopBar));
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            public void run() {
//                //
//                // Do the stuff
//                //
//                String result= new Authenticate().postAuthenticateUser(Prefs.getString("USERNAME", null), Prefs.getString("PASSWORD", null));
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
//                    JSONObject jsonObject2=jsonObject1.getJSONObject("User");
//                    String role1=jsonObject2.getString("role");
//                    if (role1.equals("User")){
//                        Prefs.clear();
//                        //Prefs.putString("role",role);
//                        Intent intent=new Intent(InternalNoteActivity.this,LoginActivity.class);
//                        Toasty.info(InternalNoteActivity.this,getString(R.string.roleChanged), Toast.LENGTH_LONG).show();
//                        startActivity(intent);
//
//
//                    }
//
//
//                } catch (JSONException | NullPointerException e) {
//                    e.printStackTrace();
//                }
//
//                handler.postDelayed(this, 30000);
//            }
//        };
//        runnable.run();
        imageView= (ImageView) findViewById(R.id.imageViewBackTicketInternalNote);
        editTextInternalNote = (EditText) findViewById(R.id.editText_internal_note);
        buttonCreate = (Button) findViewById(R.id.button_create);
        ticketID= Prefs.getString("TICKETid",null);
        progressDialog=new ProgressDialog(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = editTextInternalNote.getText().toString();
                if (note.trim().length() == 0) {
                    Toasty.warning(InternalNoteActivity.this, getString(R.string.msg_must_not_be_empty), Toast.LENGTH_LONG).show();
                    return;
                }
                String userID = Prefs.getString("ID", null);
                if (userID != null && userID.length() != 0) {
                    try {
                        note = URLEncoder.encode(note, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    new CreateInternalNoteForTicket(Integer.parseInt(ticketID), Integer.parseInt(userID), note).execute();
                    dialog1= new SpotsDialog(InternalNoteActivity.this, getString(R.string.creating_note));
                    dialog1.show();


                } else
                    Toasty.warning(InternalNoteActivity.this, getString(R.string.wrong_user_id), Toast.LENGTH_LONG).show();
            }
        });

    }
    class CreateInternalNoteForTicket extends AsyncTask<String, Void, String> {
        int ticketID;
        int userID;
        String note;

        CreateInternalNoteForTicket(int ticketID, int userID, String note) {
            this.ticketID = ticketID;
            this.userID = userID;
            this.note = note;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().postCreateInternalNote(ticketID, userID, note);
        }

        protected void onPostExecute(String result) {

            if (result == null) {
                Toasty.error(InternalNoteActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            new FetchTicketThreads(InternalNoteActivity.this, Prefs.getString("TICKETid", null)).execute();

        }
    }
    class FetchTicketThreads extends AsyncTask<String, Void, String> {
        Context context;
        String ticketID;


        FetchTicketThreads(Context context,String ticketID) {
            this.context = context;
            this.ticketID = ticketID;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getTicketThread(ticketID);
        }

        protected void onPostExecute(String result) {
            dialog1.dismiss();
            Log.d("calledFromReply","true");
            try {
                progressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            if (result == null) {
                return;
            }
            try {
                JSONObject jsonObject=new JSONObject(result);
                Log.d("ticketThreadReply",jsonObject.toString());
                Prefs.putString("ticketThread",jsonObject.toString());
                Toasty.success(InternalNoteActivity.this, getString(R.string.internal_notes_posted), Toast.LENGTH_LONG).show();
                editTextInternalNote.setText("");
                Intent intent=new Intent(InternalNoteActivity.this,MainActivity.class);
                Prefs.putString("cameFromNewProblem","true");
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
