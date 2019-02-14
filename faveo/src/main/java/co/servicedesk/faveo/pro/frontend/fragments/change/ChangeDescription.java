package co.servicedesk.faveo.pro.frontend.fragments.change;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChangeDescription.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChangeDescription#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeDescription extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    int changeId;
    ScrollView scrollView;
    String tableNameRoot="sd_changes";
    String identifierRoot="reason";
    String identifierImopact="impact";
    String identifierSymptoms="rollout-plan";
    String identifierSolutions="backout-plan";
    String table="sd_changes";
    String parameterName="";
    String identifier="";
    TextView textViewReason,textViewImpact,textViewRollout,textViewBackout;
    TextView textViewEditReason,textViewDeleteReason,textViewEditImpact,textViewDeleteImpact,
            textViewEditRollout,textViewDeleteRollout,textViewEditBackout,textViewDeleteBackout;
    ProgressDialog progressDialog;
    CardView cardViewReason,cardViewImpact,cardViewRolloutPlan,cardViewBackout;
    private OnFragmentInteractionListener mListener;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ChangeDescription() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeDescription.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeDescription newInstance(String param1, String param2) {
        ChangeDescription fragment = new ChangeDescription();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_change_description, container, false);
        scrollView=rootView.findViewById(R.id.scrollChangeDescription);
        textViewDeleteReason=rootView.findViewById(R.id.deleteReasonForChange);
        textViewEditReason=rootView.findViewById(R.id.editReasonForChange);

        textViewDeleteImpact=rootView.findViewById(R.id.deleteImpact);
        textViewEditImpact=rootView.findViewById(R.id.editImpact);

        textViewDeleteRollout=rootView.findViewById(R.id.deleteRolloutPlan);
        textViewEditRollout=rootView.findViewById(R.id.editRolloutPlan);

        textViewDeleteBackout=rootView.findViewById(R.id.deleteBackoutPlan);
        textViewEditBackout=rootView.findViewById(R.id.editBackoutPlan);

        cardViewReason=rootView.findViewById(R.id.reasonForChangeCard);
        cardViewImpact=rootView.findViewById(R.id.impactCard);
        cardViewRolloutPlan=rootView.findViewById(R.id.rolloutPlan);
        cardViewBackout=rootView.findViewById(R.id.backoutplan);

        textViewBackout=rootView.findViewById(R.id.backoutplantext);
        textViewImpact=rootView.findViewById(R.id.impact);
        textViewRollout=rootView.findViewById(R.id.rolloutplandetails);
        textViewReason=rootView.findViewById(R.id.reasonForChangeDescrip);

        textViewReason.setMovementMethod(new ScrollingMovementMethod());
        textViewRollout.setMovementMethod(new ScrollingMovementMethod());
        textViewImpact.setMovementMethod(new ScrollingMovementMethod());
        final Intent intent = getActivity().getIntent();
        changeId= intent.getIntExtra("changeId",0);
        if (InternetReceiver.isConnected()) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.pleasewait));
            progressDialog.show();
            new FetchProblemDescription(getActivity(), changeId, tableNameRoot, identifierRoot).execute();
        }

        textViewEditReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassReasonForChange cdd = new CustomDialogClassReasonForChange(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        textViewEditImpact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassImpact cdd = new CustomDialogClassImpact(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        textViewEditRollout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassRolloutPlan cdd = new CustomDialogClassRolloutPlan(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        textViewDeleteReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),changeId,tableNameRoot,identifierRoot).execute();

            }
        });
        textViewDeleteImpact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),changeId,tableNameRoot,identifierImopact).execute();
            }
        });
        textViewDeleteRollout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),changeId,tableNameRoot,identifierSymptoms).execute();
            }
        });
        textViewEditBackout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassBackoutPlan cdd = new CustomDialogClassBackoutPlan(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        textViewDeleteBackout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),changeId,tableNameRoot,identifierSolutions).execute();
            }
        });






        return rootView;
    }
    public class workAround extends AsyncTask<String,Void,String> {
        int changeId;
        String tableModule;
        String identifier;
        String solution;
        String body;
        workAround(int changeId,String tableModule,String identifier,String solution,String body){
            this.changeId=changeId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.solution=solution;
            this.body=body;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().workaroundModule(changeId,tableModule,identifier,solution,body);

        }
        protected void onPostExecute(String result){
            progressDialog.dismiss();
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                if (data.equals("Updated.")){
                    Toasty.success(getActivity(),"successfully updated",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
    public class DeleteWorkAround extends AsyncTask<String,Void,String>{
        int problemId;
        String tableName;
        String identifier;
        Context context;

        DeleteWorkAround(Context context,int problemId,String tableName,String identifier){
            this.context=context;
            this.problemId=problemId;
            this.tableName=tableName;
            this.identifier=identifier;
        }
        @Override
        protected String doInBackground(String... strings) {
            return new Helpdesk().deleteworkAroundModule(problemId,tableName,identifier);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            try{
                JSONObject jsonObject=new JSONObject(s);
                String data=jsonObject.getString("data");
                if (data.equals("Deleted")){
                    Toast.makeText(context, "successfully deleted the root cause", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

    public class FetchProblemDescription extends AsyncTask<String,Void,String> {
        int problemId;
        String tableModule;
        String identifier;
        Context context;
        FetchProblemDescription(Context context,int problemId,String tableModule,String identifier){
            this.problemId=problemId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.context=context;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().getworkaroundModule(problemId,tableModule,identifier);

        }
        protected void onPostExecute(String result){
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                String data1=data.replaceAll("&nbsp;"," ");
                Prefs.putString("description",data1);
                textViewReason.setText(data1);
                new FetchProblemDescriptionImpact(getActivity(),problemId,tableNameRoot,identifierImopact).execute();
            } catch (JSONException | NullPointerException e) {
                Prefs.putString("description","");
                cardViewReason.setVisibility(View.GONE);
//                imageViewDeleteRootCause.setVisibility(View.GONE);
//                imageViewEditRootCause.setVisibility(View.GONE);
                new FetchProblemDescriptionImpact(getActivity(),problemId,tableNameRoot,identifierImopact).execute();
                e.printStackTrace();
            }

        }
    }
    public class FetchProblemSolution extends AsyncTask<String,Void,String> {
        int problemId;
        String tableModule;
        String identifier;
        Context context;
        FetchProblemSolution(Context context,int problemId,String tableModule,String identifier){
            this.problemId=problemId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.context=context;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().getworkaroundModule(problemId,tableModule,identifier);

        }
        protected void onPostExecute(String result){
            progressDialog.dismiss();
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                String data1=data.replaceAll("&nbsp;"," ");
                Prefs.putString("solution",data1);
                textViewBackout.setText(data1);

            } catch (JSONException | NullPointerException e) {
                Prefs.putString("solution","");
                cardViewBackout.setVisibility(View.GONE);
                e.printStackTrace();
            }

        }
    }
    public class FetchProblemDescriptionImpact extends AsyncTask<String,Void,String> {
        int problemId;
        String tableModule;
        String identifier;
        Context context;
        FetchProblemDescriptionImpact(Context context,int problemId,String tableModule,String identifier){
            this.problemId=problemId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.context=context;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().getworkaroundModule(problemId,tableModule,identifier);

        }
        protected void onPostExecute(String result){
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                String data1=data.replaceAll("&nbsp;"," ");
                Prefs.putString("impact",data);
                textViewImpact.setText(data1);
                new FetchProblemDescriptionSymptoms(getActivity(),changeId,tableNameRoot,identifierSymptoms).execute();
            } catch (JSONException | NullPointerException e) {
                Prefs.putString("impact","");
                cardViewImpact.setVisibility(View.GONE);
//                imageViewDeleteImpact.setVisibility(View.GONE);
//                imageViewEditImpact.setVisibility(View.GONE);
                new FetchProblemDescriptionSymptoms(getActivity(),changeId,tableNameRoot,identifierSymptoms).execute();
                e.printStackTrace();
            }

        }
    }
    public class FetchProblemDescriptionSymptoms extends AsyncTask<String,Void,String> {
        int problemId;
        String tableModule;
        String identifier;
        Context context;
        FetchProblemDescriptionSymptoms(Context context,int problemId,String tableModule,String identifier){
            this.problemId=problemId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.context=context;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().getworkaroundModule(problemId,tableModule,identifier);

        }
        protected void onPostExecute(String result){
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                String data1=data.replaceAll("&nbsp;"," ");
                textViewRollout.setText(data1);
                Prefs.putString("symptoms",data1);
                new FetchProblemSolution(getActivity(),changeId,tableNameRoot,identifierSolutions).execute();
            } catch (JSONException | NullPointerException e) {
                Prefs.putString("symptoms","");
                cardViewRolloutPlan.setVisibility(View.GONE);
//                imageViewDeleteSymptom.setVisibility(View.GONE);
//                imageViewEditSymptom.setVisibility(View.GONE);
                new FetchProblemSolution(getActivity(),changeId,tableNameRoot,identifierSolutions).execute();
                e.printStackTrace();
            }

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    class CustomDialogClassReasonForChange extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassReasonForChange(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_reason);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.rootCause);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="reason";
                        parameterName="reason";
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    class CustomDialogClassImpact extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassImpact(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialogimpact);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.rootCause);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="impact";
                        parameterName="impact";
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
    class CustomDialogClassRolloutPlan extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassRolloutPlan(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_rolloutplan);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.rootCause);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="rollout-plan";
                        parameterName="rollout-plan";
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    class CustomDialogClassBackoutPlan extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassBackoutPlan(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_backoutplan);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.rootCause);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="backout-plan ";
                        parameterName="backout-plan ";
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(changeId,table,identifier,parameterName,body).execute();
                    }

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}
