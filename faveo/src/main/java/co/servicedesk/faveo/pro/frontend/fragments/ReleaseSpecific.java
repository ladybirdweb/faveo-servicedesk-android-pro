package co.servicedesk.faveo.pro.frontend.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.activities.EditAndViewRelease;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.Data;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReleaseSpecific.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReleaseSpecific#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReleaseSpecific extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    TextView editTextSubject,editTextDescription,editTextStart,editTextEnd,multiAutoCompleteTextViewAssets,textViewStatus,textViewPriority,textViewType;
    int releaseId;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ReleaseSpecific() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReleaseSpecific.
     */
    // TODO: Rename and change types and number of parameters
    public static ReleaseSpecific newInstance(String param1, String param2) {
        ReleaseSpecific fragment = new ReleaseSpecific();
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
        View rootView=inflater.inflate(R.layout.fragment_release_specific, container, false);
        releaseId= Prefs.getInt("releaseId",0);
        editTextDescription=rootView.findViewById(R.id.msg_edittext);
        editTextSubject=rootView.findViewById(R.id.sub_edittext);
        editTextEnd=rootView.findViewById(R.id.endDate_edittext);
        editTextStart=rootView.findViewById(R.id.startDate_edittext);
        multiAutoCompleteTextViewAssets=rootView.findViewById(R.id.addAsset);
        textViewPriority=rootView.findViewById(R.id.spinner_pri);
        textViewStatus=rootView.findViewById(R.id.spinner_status);
        textViewType=rootView.findViewById(R.id.releaseType);

        if (InternetReceiver.isConnected()){
            new FetchReleaseDetail(releaseId).execute();
        }



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

    private class FetchReleaseDetail extends AsyncTask<String,Void,String> {
        int releaseId;

        public FetchReleaseDetail(int releaseId){
            this.releaseId=releaseId;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().fetchReleaseDetail(releaseId);
        }
        protected void onPostExecute(String result){

            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String subject=jsonObject1.getString("subject");
                String description=jsonObject1.getString("description");
                String startDate=jsonObject1.getString("startDate");
                String endDate=jsonObject1.getString("endDate");

                if (!description.equals("")){
                    editTextDescription.setText(description);
                }

                if (!subject.equals("")){
                    editTextSubject.setText(subject);
                }

                if (startDate.equals("null")){
                    editTextStart.setText("");

                }else{
                    editTextStart.setText(startDate);
                }

                if (endDate.equals("null")){
                    editTextEnd.setText("");
                }else{
                    editTextEnd.setText(endDate);
                }
                editTextSubject.setText(subject);
                editTextDescription.setText(description);
                JSONObject jsonObject2=jsonObject1.getJSONObject("priority");
                try {
                    if (jsonObject2.getString("name") != null) {
                        textViewPriority.setText(jsonObject2.getString("name"));

                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject6=jsonObject1.getJSONObject("releaseType");
                try {
                    if (jsonObject6.getString("name") != null) {
                        textViewType.setText(jsonObject6.getString("name"));

                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }



                JSONObject jsonObject3=jsonObject1.getJSONObject("status");
                try {
                    if (jsonObject3.getString("name") != null) {

                        textViewStatus.setText(jsonObject3.getString("name"));

                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }


                //JSONArray jsonArray=jsonObject1.getJSONArray("assigned_id");


                try{
                    JSONArray jsonArray=jsonObject1.getJSONArray("asset");
                    StringBuilder stringBuilder=new StringBuilder();
                    if (!jsonArray.equals("")){
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject7=jsonArray.getJSONObject(i);
                            stringBuilder.append(jsonObject7.getString("name")).append(",");
                        }

                        multiAutoCompleteTextViewAssets.setText(stringBuilder.toString());
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }


            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}
