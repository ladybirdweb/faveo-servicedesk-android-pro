package co.servicedesk.faveo.pro.frontend.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.servicedesk.faveo.pro.Helper;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.activities.TicketDetailActivity;
import co.servicedesk.faveo.pro.frontend.activities.TicketReplyActivity;
import co.servicedesk.faveo.pro.frontend.adapters.CollaboratorAdapter;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.CollaboratorSuggestion;
import co.servicedesk.faveo.pro.model.Data;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class collaboratorAdd extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextViewUser;
    Button searchUer, deleteUser;
    ArrayList<CollaboratorSuggestion> stringArrayList;
    CollaboratorAdapter arrayAdapterCC;
    RelativeLayout relativeLayout;
    ArrayAdapter<String> spinnerPriArrayAdapter;
    int id = 0;
    int id1 = 0;
    String email;
    ImageView imageView;
    Toolbar toolbar;
    String email1;
    String term;
    ArrayList<String> strings;
    Spinner recipients;
    String email2;
    SpotsDialog dialog1;
    ProgressDialog progressDialog;
    public static boolean isShowing = false;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_collaborator_add);
        Window window = collaboratorAdd.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(collaboratorAdd.this,R.color.faveo));
        recipients= (Spinner) findViewById(R.id.spinnerRecipients);
        relativeLayout= (RelativeLayout) findViewById(R.id.recipients);
        strings = new ArrayList<>();
        strings.add("Show Recipients");
        isShowing=true;
        progressBar= (ProgressBar) findViewById(R.id.collaboratorProgressBarReply);
        progressDialog=new ProgressDialog(collaboratorAdd.this);
        new FetchCollaboratorAssociatedWithTicket(Prefs.getString("TICKETid", null)).execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCollaborator);
        ImageView imageView = (ImageView) toolbar.findViewById(R.id.imageViewBack);
        autoCompleteTextViewUser = (AutoCompleteTextView) findViewById(R.id.appCompatAutoCompleteTextView);
        autoCompleteTextViewUser.setHorizontallyScrolling(true);
        autoCompleteTextViewUser.setMovementMethod(new ScrollingMovementMethod());
        searchUer = (Button) findViewById(R.id.buttonSearchUser);

        deleteUser = (Button) findViewById(R.id.buttonDeleteUser);
        stringArrayList = new ArrayList<CollaboratorSuggestion>();
        arrayAdapterCC=new CollaboratorAdapter(collaboratorAdd.this,stringArrayList);
        //arrayAdapterCC = new ArrayAdapter<Data>(collaboratorAdd.this, android.R.layout.simple_dropdown_item_1line, stringArrayList);
        autoCompleteTextViewUser.setThreshold(2);
        autoCompleteTextViewUser.setDropDownWidth(1500);
        autoCompleteTextViewUser.addTextChangedListener(passwordWatcheredittextSubject);
        email1 = autoCompleteTextViewUser.getText().toString();
        autoCompleteTextViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name1=autoCompleteTextViewUser.getText().toString();
                for (int i1=0;i1<stringArrayList.size();i1++){
                    CollaboratorSuggestion data1=stringArrayList.get(i1);
                    if (data1.getEmail().equals(name1)){
                        CollaboratorSuggestion data2=stringArrayList.get(i1);
                        id1=data2.getId();
                        Log.d("id",id+"");
                        email1=data2.getEmail();
                        Log.d("email1",email1);
                        autoCompleteTextViewUser.setText(email1);
//                        editTextEmail.setText(email1);
//                        firstname=data2.getFirst_name();
//                        lastname=data2.getLast_name();
//                        editTextEmail.setText(email1);
//                        editTextFirstName.setText(firstname);
//                        editTextLastName.setText(lastname);
                    }
                }
//                try{
//                    Data data = stringArrayList.get(i);
//                    id1 = data.getID();
//                    email = data.getName();
//                    Log.d("idoftheuser",id1+"");
//                    Log.d("ccobject",stringArrayList.get(i).toString());
//                }catch (IndexOutOfBoundsException e){
//                    e.printStackTrace();
//                }
            }


        });
        spinnerPriArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strings); //selected item will look like a spinner set from XML
        spinnerPriArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipients.setAdapter(spinnerPriArrayAdapter);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(collaboratorAdd.this, TicketReplyActivity.class);
                startActivity(intent);
            }
        });

        recipients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    email2=null;
                }
                else{
                    email2=recipients.getSelectedItem().toString();
                    //Toast.makeText(collaboratorAdd.this, "email is:"+email2, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                email2=null;
            }
        });
        searchUer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=autoCompleteTextViewUser.getText().toString();

                if (autoCompleteTextViewUser.getText().toString().equals("")) {
                    Toasty.info(collaboratorAdd.this, getString(R.string.collaboratorEmpty), Toast.LENGTH_SHORT).show();
                }
                else if (id1==0){
                    Toasty.info(collaboratorAdd.this, getString(R.string.collaboratorNotFound), Toast.LENGTH_SHORT).show();
                }
                else{
                    new BottomDialog.Builder(collaboratorAdd.this)
                            .setTitle(getString(R.string.addingCollaborator))
                            .setContent(getString(R.string.collabConfirmation))
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
                                    Log.d("id of the user", String.valueOf(id));
                                    if (id1==0){
                                        Toasty.error(collaboratorAdd.this, getString(R.string.collaboratorNotFound), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    dialog1= new SpotsDialog(collaboratorAdd.this, getString(R.string.addingCollaborator));
                                    dialog1.show();
                                    new collaboratorAdduser(Prefs.getString("TICKETid", null), String.valueOf(id1)).execute();
                                }
                            }).onNegative(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog bottomDialog) {
                            bottomDialog.dismiss();
                        }
                    })
                            .show();
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(collaboratorAdd.this);
//                    // Setting Dialog Title
//                    alertDialog.setTitle("Adding collaborator...");
//                    // Setting Dialog Message
//                    alertDialog.setMessage(getString(R.string.collabConfirmation));
//                    // Setting Icon to Dialog
//                    alertDialog.setIcon(R.mipmap.ic_launcher);
//                    // Setting Positive "Yes" Button
//                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to invoke YES event
//                            Log.d("id of the user", String.valueOf(id));
//                            if (id1==0){
//                                Toasty.error(collaboratorAdd.this, getString(R.string.collaboratorNotFound), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            dialog1= new SpotsDialog(collaboratorAdd.this, getString(R.string.addingCollaborator));
//                            dialog1.show();
//                            new collaboratorAdduser(Prefs.getString("TICKETid", null), String.valueOf(id1)).execute();
//
//                        }
//                    });
//                    // Setting Negative "NO" Button
//                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to invoke NO event
//                            //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                            dialog.cancel();
//                        }
//                    });
//
//                    // Showing Alert Message
//                    alertDialog.show();
                }
//                if (email.contains("<")) {
////                    int pos = email.indexOf("<");
////                    int pos1 = email.lastIndexOf(">");
////                    finalEmail2=email.substring(pos+1,pos1);
//
//
//                }else{
//                    Toasty.info(collaboratorAdd.this,getString(R.string.userEmpty),Toast.LENGTH_SHORT).show();
//                    //Toast.makeText(collaboratorAdd.this,getString(R.string.userEmpty), Toast.LENGTH_SHORT).show();
//                    return;
//                }


//                else if (!finalEmail2.equalsIgnoreCase(finalEmail)){
//
//                }


//                else if (id==0){
//                    Toast.makeText(collaboratorAdd.this, "user not found", Toast.LENGTH_SHORT).show();
//                    return;
//                }


                //Toast.makeText(collaboratorAdd.this, "added", Toast.LENGTH_SHORT).show();

            }
        });
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String email = autoCompleteTextViewUser.getText().toString();

//                if (Prefs.getString("noUser",null).equals("null")){
//                    Toasty.info(collaboratorAdd.this,getString(R.string.userEmpty),Toast.LENGTH_SHORT).show();
//                }
                //Toast.makeText(collaboratorAdd.this, "clicked on delete", Toast.LENGTH_SHORT).show();
                int pos=recipients.getSelectedItemPosition();
                try {

                    if (pos==0){
                        Toasty.info(collaboratorAdd.this,getString(R.string.userEmpty),Toast.LENGTH_SHORT).show();

                    }
                    else {

                        new BottomDialog.Builder(collaboratorAdd.this)
                                .setTitle(getString(R.string.removingColl))
                                .setContent(getString(R.string.removingCollConf))
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
                                        email2 = recipients.getSelectedItem().toString();
                                        dialog1= new SpotsDialog(collaboratorAdd.this, getString(R.string.removingC));
                                        dialog1.show();
                                        new collaboratorRemoveUser(Prefs.getString("TICKETid", null), email2).execute();
                                    }
                                }).onNegative(new BottomDialog.ButtonCallback() {
                            @Override
                            public void onClick(@NonNull BottomDialog bottomDialog) {
                                bottomDialog.dismiss();
                            }
                        })
                                .show();


//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(collaboratorAdd.this);
//                        // Setting Dialog Title
//                        alertDialog.setTitle(getString(R.string.removingColl));
//                        // Setting Dialog Message
//                        alertDialog.setMessage(getString(R.string.removingCollConf));
//                        // Setting Icon to Dialog
//                        alertDialog.setIcon(R.mipmap.ic_launcher);
//                        // Setting Positive "Yes" Button
//                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Write your code here to invoke YES event
//                                email2 = recipients.getSelectedItem().toString();
//                                dialog1= new SpotsDialog(collaboratorAdd.this, getString(R.string.removingC));
//                                dialog1.show();
//                                new collaboratorRemoveUser(Prefs.getString("TICKETid", null), email2).execute();
//                            }
//                        });
//                        // Setting Negative "NO" Button
//                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Write your code here to invoke NO event
//                                //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                                dialog.cancel();
//                            }
//                        });
//
//                        // Showing Alert Message
//                        alertDialog.show();


                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }


                //Toast.makeText(collaboratorAdd.this,getString(R.string.userEmpty), Toast.LENGTH_SHORT).show();
            }
        });


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
            progressBar.setVisibility(View.GONE);
            searchUer.setVisibility(View.VISIBLE);
            if (isCancelled()) return;
            stringArrayList.clear();
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

//            if (result == null) {
//                Toasty.error(collaboratorAdd.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                return;
//            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("users");
                if (jsonArray.length() == 0) {
                    Prefs.putString("noUser", "null");
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String email = jsonObject1.getString("email");
                        id = Integer.parseInt(jsonObject1.getString("id"));
                        String first_name = jsonObject1.getString("first_name");
                        String last_name = jsonObject1.getString("last_name");
                        String profilePic=jsonObject1.getString("profile_pic");
                        //Toast.makeText(TicketSaveActivity.this, "email:"+email, Toast.LENGTH_SHORT).show();
                        CollaboratorSuggestion collaboratorSuggestion=new CollaboratorSuggestion(id,first_name,last_name,email,profilePic);
                        //Data data = new Data(id, first_name + " " + last_name + " <" + email + ">");
                        stringArrayList.add(collaboratorSuggestion);
                        Prefs.putString("noUser", "1");
                    }
                    autoCompleteTextViewUser.setAdapter(arrayAdapterCC);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        }
    }

    private class collaboratorAdduser extends AsyncTask<String, Void, String> {
        String ticketId, userId;

        public collaboratorAdduser(String ticketId, String userId) {
            this.ticketId = ticketId;
            this.userId = userId;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().createCollaborator(Prefs.getString("TICKETid", null), String.valueOf(userId));
        }

        protected void onPostExecute(String result) {
            if (isCancelled()) return;
            dialog1.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("collaborator");
//                JSONArray jsonArray=jsonObject.getJSONArray("users");
//                if (jsonArray.length()==0){
//                    Toast.makeText(collaboratorAdd.this, "user not found", Toast.LENGTH_SHORT).show();
//                }
                String role = jsonObject1.getString("role");
                if (role.contains("ccc")) {
                    autoCompleteTextViewUser.setText("");
                    id = 0;
                    Toasty.success(collaboratorAdd.this, getString(R.string.collaboratoraddedsuccesfully), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(collaboratorAdd.this,collaboratorAdd.class);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

//            if (result == null) {
//                Toasty.error(collaboratorAdd.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                return;
//            }


        }
    }

    private class collaboratorRemoveUser extends AsyncTask<String, Void, String> {
        String ticketId, userId;

        public collaboratorRemoveUser(String ticketId, String userId) {
            this.ticketId = ticketId;
            this.userId = userId;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().removeCollaborator(Prefs.getString("TICKETid", null), email2);
        }

        protected void onPostExecute(String result) {
            dialog1.dismiss();
            if (isCancelled()) return;
            try {
                JSONObject jsonObject = new JSONObject(result);
                String collaborator=jsonObject.getString("collaborator");
                if (collaborator.equals("deleted successfully")){
                    Toasty.success(collaboratorAdd.this, getString(R.string.collaboratorRemove), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(collaboratorAdd.this,collaboratorAdd.class);
                    startActivity(intent);
                }
//                JSONArray jsonArray=jsonObject.getJSONArray("users");
//                if (jsonArray.length()==0){
//                    Toast.makeText(collaboratorAdd.this, "user not found", Toast.LENGTH_SHORT).show();
//                }
//                String role=jsonObject1.getString("role");
//                if (role.contains("ccc")){
//                    autoCompleteTextViewUser.setText("");
//                    id=0;
//                    Toasty.success(collaboratorAdd.this,getString(R.string.collaboratoraddedsuccesfully),Toast.LENGTH_SHORT).show();
//                }

            } catch (JSONException |NullPointerException e) {
                e.printStackTrace();
            }


//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

//            if (result == null) {
//                Toasty.error(collaboratorAdd.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                return;
//            }


        }
    }

    private class FetchCollaboratorAssociatedWithTicket extends AsyncTask<String, Void, String> {
        String ticketid;

        FetchCollaboratorAssociatedWithTicket(String ticketid) {

            this.ticketid = ticketid;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().postCollaboratorAssociatedWithTicket(ticketid);
        }

        protected void onPostExecute(String result) {
            int noOfCollaborator=0;
            if (isCancelled()) return;
            //strings.clear();

//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

            if (result == null) {
                Toasty.error(collaboratorAdd.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                Data data=new Data(0,"No recipients");
//                stringArrayList.add(data);
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("collaborator");
                if (jsonArray.length()==0){
                    recipients.setVisibility(View.GONE);
                    return;
                }else {
                    relativeLayout.setVisibility(View.VISIBLE);
                    recipients.setVisibility(View.VISIBLE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        noOfCollaborator++;
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String email = jsonObject1.getString("email");
                        //String first_name = jsonObject1.getString("first_name");
                        //String last_name = jsonObject1.getString("last_name");
                        //int id= Integer.parseInt(jsonObject1.getString("id"));
                        //Toast.makeText(TicketSaveActivity.this, "email:"+email, Toast.LENGTH_SHORT).show();

                        //stringArrayList.add(data);

                        strings.add(email);

                    }
                    //Toast.makeText(collaboratorAdd.this, "noofcollaborators:"+noOfCollaborator, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


//                if (jsonObject1.getString("last_message").equals("null")) {
//                    editTextLastMessage.setText("Not available");
//                } else
//                    editTextLastMessage.setText(jsonObject1.getString("last_message"));


        }


//     class collaboratorRemoveuser extends AsyncTask<String, Void, String> {
//        String ticketId,userId;
//
//        public collaboratorRemoveuser(String ticketId, String userId) {
//            this.ticketId = ticketId;
//            this.userId = userId;
//        }
//
//        protected String doInBackground(String... urls) {
//            return new Helpdesk().removeCollaborator(Prefs.getString("ticketID",null),finalEmail);
//        }
//
//        protected void onPostExecute(String result) {
//            if (isCancelled()) return;
//
//            try {
//                JSONObject jsonObject=new JSONObject(result);
//                String collaboratorNotFound=jsonObject.getString("collaborator");
//                if (collaboratorNotFound.equals("not found")) {
//                    Toasty.warning(collaboratorAdd.this, getString(R.string.collaboratorNotFound), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                else if (collaboratorNotFound.equals("deleted successfully")){
//                    .setText("");
//                    Toasty.success(collaboratorAdd.this,getString(R.string.collaboratorRemove),Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
////            if (progressDialog.isShowing())
////                progressDialog.dismiss();
//
////            if (result == null) {
////                Toasty.error(collaboratorAdd.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
////                return;
////            }
//
//
//
//        }




    }
    final TextWatcher passwordWatcheredittextSubject = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            //Toast.makeText(TicketSaveActivity.this, "API called", Toast.LENGTH_SHORT).show();
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            term = autoCompleteTextViewUser.getText().toString();
            searchUer.setVisibility(View.VISIBLE);
            if (InternetReceiver.isConnected()) {
                if (!term.equals("")) {
                    searchUer.setVisibility(View.GONE);
                    //int pos = term.lastIndexOf(",");
                    //term = term.substring(pos + 1, term.length());
                    String newTerm=term;
                    Log.d("newTerm", newTerm);
                    arrayAdapterCC=new CollaboratorAdapter(collaboratorAdd.this,stringArrayList);
                    progressBar.setVisibility(View.VISIBLE);
                    //arrayAdapterCC = new ArrayAdapter<>(collaboratorAdd.this, android.R.layout.simple_dropdown_item_1line, stringArrayList);
                    new FetchCollaborator(newTerm.trim()).execute();
                    autoCompleteTextViewUser.setAdapter(arrayAdapterCC);
                }
//            Toast.makeText(collaboratorAdd.this, "term:"+term, Toast.LENGTH_SHORT).show();
                else {
                    arrayAdapterCC=new CollaboratorAdapter(collaboratorAdd.this,stringArrayList);
                    //arrayAdapterCC = new ArrayAdapter<>(collaboratorAdd.this, android.R.layout.simple_dropdown_item_1line, stringArrayList);
                    //new FetchCollaborator("s").execute();
                    //Data data = new Data(0, "No result found");
                    //stringArrayList.add(data);
//                autoCompleteTextViewCC.setAdapter(stringArrayAdapterCC);
//                stringArrayAdapterCC.notifyDataSetChanged();
//                autoCompleteTextViewCC.setThreshold(0);
//                autoCompleteTextViewCC.setDropDownWidth(1000);

                }


                //buttonsave.setEnabled(true);
            }
        }

        public void afterTextChanged(Editable s) {
//            if (term.equals("")){
//                searchUer.setVisibility(View.GONE);
//            }
//            else{
//                searchUer.setVisibility(View.VISIBLE);
//            }
        }
    };
    @Override
    public void onBackPressed() {
        if (!TicketDetailActivity.isShowing) {
            Log.d("isShowing", "false");
            Intent intent = new Intent(this, TicketDetailActivity.class);
            startActivity(intent);
        } else Log.d("isShowing", "true");
        super.onBackPressed();

//        if (fabExpanded)
//            exitReveal();
//        else super.onBackPressed();
    }

}