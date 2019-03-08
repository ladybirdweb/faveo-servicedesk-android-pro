package co.servicedesk.faveo.pro.frontend.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.fragments.change.ChangeDescription;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReleaseDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReleaseDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReleaseDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    CardView cardViewreasonForbuildPlan,cardViewtestPlanCard;
    TextView textViewBuildPlan,textViewEditBuild,textViewDeleteBuild,textViewtestPlan,textViewEditTest,textViewdeleteTest;
    String tableNameRoot="sd_releases";
    String identifierRoot="build-plan";
    String identifierImopact="test-plan ";
    String table="sd_releases";
    String parameterName="";
    String identifier="";
    int releaseId;
    ProgressDialog progressDialog;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ReleaseDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReleaseDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static ReleaseDetail newInstance(String param1, String param2) {
        ReleaseDetail fragment = new ReleaseDetail();
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
        View rootView=inflater.inflate(R.layout.fragment_release_detail, container, false);
        releaseId= Prefs.getInt("releaseId",0);
        cardViewreasonForbuildPlan=rootView.findViewById(R.id.reasonForbuildPlan);
        cardViewtestPlanCard=rootView.findViewById(R.id.testPlanCard);
        textViewBuildPlan=rootView.findViewById(R.id.buildPlanText);
        textViewEditBuild=rootView.findViewById(R.id.editBuildPlan);
        textViewDeleteBuild=rootView.findViewById(R.id.deleteBuildPlan);
        textViewtestPlan=rootView.findViewById(R.id.testPlanDetail);
        textViewEditTest=rootView.findViewById(R.id.editTestPlan);
        textViewdeleteTest=rootView.findViewById(R.id.deleteTestPlan);
        progressDialog=new ProgressDialog(getActivity());

        if (InternetReceiver.isConnected()){
            progressDialog.setMessage(getString(R.string.pleasewait));
            progressDialog.show();
            new FetchReleaseBuildPlan(getActivity(), releaseId, tableNameRoot, identifierRoot).execute();

        }

        textViewEditBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("build_plan","true");
                CustomDialogClassReasonForChange cdd = new CustomDialogClassReasonForChange(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });

        textViewEditTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("build_plan","false");
                CustomDialogClassReasonForChange cdd = new CustomDialogClassReasonForChange(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });

        textViewDeleteBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),releaseId,tableNameRoot,identifierRoot).execute();
            }
        });

        textViewdeleteTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new DeleteWorkAround(getActivity(),releaseId,tableNameRoot,identifierRoot).execute();
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
                    Toasty.success(context, "successfully deleted.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

    public class FetchReleaseBuildPlan extends AsyncTask<String,Void,String> {
        int releaseId;
        String tableModule;
        String identifier;
        Context context;
        FetchReleaseBuildPlan(Context context,int releaseId,String tableModule,String identifier){
            this.releaseId=releaseId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.context=context;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().getworkaroundModule(releaseId,tableModule,identifier);

        }
        protected void onPostExecute(String result){
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                String data1=data.replaceAll("&nbsp;"," ");
                String data2=data1.replaceAll("&quot;","");
                Prefs.putString("description",data2);
                textViewBuildPlan.setText(data2);
                new FetchProblemDescriptionImpact(getActivity(),releaseId,tableNameRoot,identifierImopact).execute();
            } catch (JSONException | NullPointerException e) {
                Prefs.putString("description","");
                cardViewreasonForbuildPlan.setVisibility(View.GONE);
//                imageViewDeleteRootCause.setVisibility(View.GONE);
//                imageViewEditRootCause.setVisibility(View.GONE);
                new FetchProblemDescriptionImpact(getActivity(),releaseId,tableNameRoot,identifierImopact).execute();
                e.printStackTrace();
            }

        }
    }
    public class FetchProblemDescriptionImpact extends AsyncTask<String,Void,String> {
        int releaseId;
        String tableModule;
        String identifier;
        Context context;
        FetchProblemDescriptionImpact(Context context,int releaseId,String tableModule,String identifier){
            this.releaseId=releaseId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.context=context;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().getworkaroundModule(releaseId,tableModule,identifier);

        }
        protected void onPostExecute(String result){

            progressDialog.dismiss();
            try {
                JSONObject jsonObject=new JSONObject(result);
                String data=jsonObject.getString("data");
                String data1=data.replaceAll("&nbsp;"," ");
                String data2=data1.replaceAll("&quot;","");
                Prefs.putString("impact",data2);
                textViewtestPlan.setText(data2);
            } catch (JSONException | NullPointerException e) {
                Prefs.putString("impact","");
                cardViewtestPlanCard.setVisibility(View.GONE);
                e.printStackTrace();
            }

        }
    }

    class CustomDialogClassReasonForChange extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;
        public EditText editTextRootCause;
        public TextView textViewTitle;

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
            textViewTitle=findViewById(R.id.title);
            if (Prefs.getString("build_plan",null).equals("true")){
                textViewTitle.setText(R.string.build_plan);
            }else{
                textViewTitle.setText(R.string.test_paln);
            }

            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            editTextRootCause=findViewById(R.id.reason);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    try {
                        if (editTextRootCause.getText().toString().equals("")) {
                            Toasty.info(c, "message cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            String body = editTextRootCause.getText().toString();
                            try {
                                body = URLEncoder.encode(body.trim(), "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            if (Prefs.getString("build_plan",null).equals("true")){
                                identifier = "build-plan";
                                parameterName = "build-plan";
                            }else{
                                identifier = "test-plan";
                                parameterName = "test-plan";
                            }

                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage(getString(R.string.pleasewait));
                            progressDialog.show();
                            new workAround(releaseId, table, identifier, parameterName, body).execute();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
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
        int releaseId;
        String tableModule;
        String identifier;
        String solution;
        String body;
        workAround(int releaseId,String tableModule,String identifier,String solution,String body){
            this.releaseId=releaseId;
            this.tableModule=tableModule;
            this.identifier=identifier;
            this.solution=solution;
            this.body=body;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().workaroundModule(releaseId,tableModule,identifier,solution,body);

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

}
