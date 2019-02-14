package co.servicedesk.faveo.pro.frontend.fragments.problem;

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
import android.view.MotionEvent;
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
 * {@link ProblemDescription.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProblemDescription#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProblemDescription extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    TextView textViewRootCauseDescrip,textViewImpact,textViewSymptoms,textViewSolutions;
    ScrollView scrollView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int problemId;
    String tableNameRoot="sd_problem";
    String identifierRoot="root-cause";
    String identifierImopact="impact";
    String identifierSymptoms="symptoms";
    String identifierSolutions="solution";
    String table="sd_problem";
    String parameterName="";
    String identifier="";
    TextView imageViewEditRootCause,imageViewDeleteRootCause,imageViewEditImpact,imageViewDeleteImpact,
            imageViewEditSymptom,imageViewDeleteSymptom,imageViewEditSolution,imageViewDeleteSolution;
    ProgressDialog progressDialog;
    CardView cardViewRootCause,cardViewImpact,cardViewSymptoms,cardViewSolution;
    private OnFragmentInteractionListener mListener;

    public ProblemDescription() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProblemDescription.
     */
    // TODO: Rename and change types and number of parameters
    public static ProblemDescription newInstance(String param1, String param2) {
        ProblemDescription fragment = new ProblemDescription();
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
        View rootView=inflater.inflate(R.layout.fragment_problem_description, container, false);
        scrollView=rootView.findViewById(R.id.scrollProblemDescription);
        imageViewDeleteRootCause=rootView.findViewById(R.id.deleteRootCause);
        imageViewEditRootCause=rootView.findViewById(R.id.editRootCause);
        imageViewDeleteImpact=rootView.findViewById(R.id.deleteImpact);
        imageViewEditImpact=rootView.findViewById(R.id.editImpact);
        imageViewDeleteSymptom=rootView.findViewById(R.id.deleteSymptom);
        imageViewEditSymptom=rootView.findViewById(R.id.editSymptom);
        imageViewEditSolution=rootView.findViewById(R.id.editSolution);
        imageViewDeleteSolution=rootView.findViewById(R.id.deleteSolution);
        cardViewRootCause=rootView.findViewById(R.id.rootCard);
        cardViewImpact=rootView.findViewById(R.id.impactCard);
        cardViewSymptoms=rootView.findViewById(R.id.symptomsCard);
        cardViewSolution=rootView.findViewById(R.id.solutionCard);
        textViewSolutions=rootView.findViewById(R.id.solutions);
        textViewImpact=rootView.findViewById(R.id.impact);
        textViewSymptoms=rootView.findViewById(R.id.symptoms);
        textViewRootCauseDescrip=rootView.findViewById(R.id.rootCauseDescrip);
        textViewRootCauseDescrip.setMovementMethod(new ScrollingMovementMethod());
        textViewSymptoms.setMovementMethod(new ScrollingMovementMethod());
        textViewImpact.setMovementMethod(new ScrollingMovementMethod());
        final Intent intent = getActivity().getIntent();
        problemId= intent.getIntExtra("problemId",0);
        if (InternetReceiver.isConnected()) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.pleasewait));
            progressDialog.show();
            new FetchProblemDescription(getActivity(), problemId, tableNameRoot, identifierRoot).execute();
        }

        imageViewEditRootCause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassRootCause cdd = new CustomDialogClassRootCause(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        imageViewEditImpact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassImpact cdd = new CustomDialogClassImpact(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        imageViewEditSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassSymptoms cdd = new CustomDialogClassSymptoms(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        imageViewDeleteRootCause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),problemId,tableNameRoot,identifierRoot).execute();

            }
        });
        imageViewDeleteImpact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),problemId,tableNameRoot,identifierImopact).execute();
            }
        });
        imageViewDeleteSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),problemId,tableNameRoot,identifierSymptoms).execute();
            }
        });
        imageViewEditSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialogClassSolution cdd = new CustomDialogClassSolution(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });
        imageViewDeleteSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),problemId,tableNameRoot,identifierSolutions).execute();
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                textViewImpact.getParent().requestDisallowInterceptTouchEvent(false);
                textViewRootCauseDescrip.getParent().requestDisallowInterceptTouchEvent(false);
                textViewSymptoms.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        textViewImpact.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                textViewImpact.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        textViewSymptoms.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                textViewSymptoms.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
        textViewRootCauseDescrip.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                textViewRootCauseDescrip.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });



        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
                textViewRootCauseDescrip.setText(data1);
                new FetchProblemDescriptionImpact(getActivity(),problemId,tableNameRoot,identifierImopact).execute();
                } catch (JSONException | NullPointerException e) {
                Prefs.putString("description","");
                cardViewRootCause.setVisibility(View.GONE);
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
                textViewSolutions.setText(data1);

            } catch (JSONException | NullPointerException e) {
                Prefs.putString("solution","");
                cardViewSolution.setVisibility(View.GONE);
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
                new FetchProblemDescriptionSymptoms(getActivity(),problemId,tableNameRoot,identifierSymptoms).execute();
            } catch (JSONException | NullPointerException e) {
                Prefs.putString("impact","");
                cardViewImpact.setVisibility(View.GONE);
//                imageViewDeleteImpact.setVisibility(View.GONE);
//                imageViewEditImpact.setVisibility(View.GONE);
                new FetchProblemDescriptionSymptoms(getActivity(),problemId,tableNameRoot,identifierSymptoms).execute();
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
                textViewSymptoms.setText(data1);
                Prefs.putString("symptoms",data1);
                new FetchProblemSolution(getActivity(),problemId,tableNameRoot,identifierSolutions).execute();
            } catch (JSONException | NullPointerException e) {
                Prefs.putString("symptoms","");
                cardViewSymptoms.setVisibility(View.GONE);
//                imageViewDeleteSymptom.setVisibility(View.GONE);
//                imageViewEditSymptom.setVisibility(View.GONE);
                new FetchProblemSolution(getActivity(),problemId,tableNameRoot,identifierSolutions).execute();
                e.printStackTrace();
            }

        }
    }

    class CustomDialogClassRootCause extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;

        public CustomDialogClassRootCause(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialogrootcause);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.rootCause);
            String data=Prefs.getString("description",null);
            if (!data.equals("")){
                editTextRootCause.setText(data);
            }
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextRootCause.getText().toString().equals("")){
                        Toast.makeText(c, "message can not be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextRootCause.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="root-cause";
                        parameterName="root-cause";
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(getActivity(),problemId,table,identifier,parameterName,body).execute();
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
        public EditText editTextImpact;
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
            editTextImpact=findViewById(R.id.impactEdit);
            String data=Prefs.getString("impact",null);
            if (!data.equals("")){
                editTextImpact.setText(data);
            }
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextImpact.getText().toString().equals("")){
                        Toast.makeText(c, "message can not be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextImpact.getText().toString();
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
                        new workAround(getActivity(),problemId,table,identifier,parameterName,body).execute();
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

    class CustomDialogClassSymptoms extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        EditText editTextSymptoms;
        public CustomDialogClassSymptoms(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_symptons);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextSymptoms=findViewById(R.id.symptomsEdit);
            String data=Prefs.getString("symptoms",null);
            if (!data.equals("")){
                editTextSymptoms.setText(data);
            }
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextSymptoms.getText().toString().equals("")){
                        Toast.makeText(c, "message can not be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextSymptoms.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="symptoms";
                        parameterName="symptoms";
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(getActivity(),problemId,table,identifier,parameterName,body).execute();
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
    public class workAround extends AsyncTask<String,Void,String> {
        int problemId;
        String tableModule;
        String identifier;
        String solution;
        String body;
        Context context;
        workAround(Context context,int problemId,String tableModule,String identifier,String solution,String body){
            this.problemId=problemId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.solution=solution;
            this.body=body;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().workaroundModule(problemId,tableModule,identifier,solution,body);

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
    class CustomDialogClassSolution extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        EditText editTextSolution;
        public CustomDialogClassSolution(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_solution);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextSolution=findViewById(R.id.editSolution);
            String data=Prefs.getString("solution",null);
            if (!data.equals("")){
                editTextSolution.setText(data);
            }
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    if (editTextSolution.getText().toString().equals("")){
                        Toasty.info(c,"message cannot be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{
                        String body=editTextSolution.getText().toString();
                        try {
                            body = URLEncoder.encode(body.trim(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        identifier="solution";
                        parameterName="solution";
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.pleasewait));
                        progressDialog.show();
                        new workAround(getActivity(),problemId,table,identifier,parameterName,body).execute();
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
}
