package co.servicedesk.faveo.pro.frontend.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.activities.EditAndViewChange;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.Data;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChangeSpecific.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChangeSpecific#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeSpecific extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    AutoCompleteTextView editTextEmail;
    MultiAutoCompleteTextView assetItem;
    ArrayList<Data> staffitemsauto,impactItems,statusItems,priorityItems,assetItems,changeTypeItems;
    Spinner impactSpinner,statusSpinner,prioritySpinner,changeTypeSpinner;
    ArrayAdapter<Data> impactAdapter,statusAdapetr,assetAdapter,priorityAdapter,changeTypeAdapter;
    EditText editTextSubject,editTextDescrip;
    int changeID;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ChangeSpecific() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeSpecific.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeSpecific newInstance(String param1, String param2) {
        ChangeSpecific fragment = new ChangeSpecific();
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
        View rootview=inflater.inflate(R.layout.fragment_change_specific, container, false);
        changeID=Prefs.getInt("changeId",0);
        impactSpinner=rootview.findViewById(R.id.impact);
        prioritySpinner=rootview.findViewById(R.id.spinner_pri);
        statusSpinner=rootview.findViewById(R.id.spinner_status);
        assetItem=rootview.findViewById(R.id.addAsset);
        editTextEmail=rootview.findViewById(R.id.email_edittext);
        editTextSubject=rootview.findViewById(R.id.sub_edittext);
        editTextDescrip=rootview.findViewById(R.id.msg_edittext);
        changeTypeSpinner=rootview.findViewById(R.id.spinner_changeType);
        if (InternetReceiver.isConnected()){
            new FetchDependency("change").execute();
        }
        return rootview;
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

                priorityAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, priorityItems);
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
                assetAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, assetItems);
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

                statusAdapetr = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, statusItems);
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

                changeTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, changeTypeItems);
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

                impactAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,impactItems);
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

                new FetchChangeDetail(changeID).execute();

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }


    }
    private class FetchChangeDetail extends AsyncTask<String,Void,String>{
        int changeId;

        public FetchChangeDetail(int changeId){
            this.changeId=changeId;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().fetchChangeDetail(changeId);
        }
        protected void onPostExecute(String result){

            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String subject=jsonObject1.getString("subject");
                JSONArray requester=jsonObject1.getJSONArray("requester");
                editTextEmail.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                editTextDescrip.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                editTextSubject.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                assetItem.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                if (requester.isNull(0)){
                    editTextEmail.setText("");
                }
                else{


                }
                String description=jsonObject1.getString("description");
                editTextSubject.setText(subject);
                editTextDescrip.setText(description);
                JSONObject jsonObject2=jsonObject1.getJSONObject("priority");
                try {
                    if (jsonObject2.getString("name") != null) {
                        prioritySpinner.setSelection(getIndex(prioritySpinner, jsonObject2.getString("name")));
                        prioritySpinner.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });

                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject6=jsonObject1.getJSONObject("changeType");
                try {
                    if (jsonObject6.getString("name") != null) {
                        changeTypeSpinner.setSelection(getIndex(changeTypeSpinner, jsonObject6.getString("name")));
                        changeTypeSpinner.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });

                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }



                JSONObject jsonObject3=jsonObject1.getJSONObject("status");
                try {
                    if (jsonObject3.getString("name") != null) {

                        statusSpinner.setSelection(getIndex(statusSpinner, jsonObject3.getString("name")));
                        statusSpinner.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });

                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject4=jsonObject1.getJSONObject("impactType");
                try {
                    if (jsonObject4.getString("name") != null) {

                        impactSpinner.setSelection(getIndex(impactSpinner, jsonObject4.getString("name")));
                        impactSpinner.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });

                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }


                //JSONArray jsonArray=jsonObject1.getJSONArray("assigned_id");


                try{
                    JSONArray jsonArray=jsonObject1.getJSONArray("assetList");
                    StringBuilder stringBuilder=new StringBuilder();
                    if (!jsonArray.equals("")){
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject7=jsonArray.getJSONObject(i);
                            stringBuilder.append(jsonObject7.getString("name")).append(",");
                        }

                        assetItem.setText(stringBuilder.toString());
                        assetItem.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }


            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            Log.d("item ", spinner.getItemAtPosition(i).toString());
            if (spinner.getItemAtPosition(i).toString().equals(myString.trim())) {
                index = i;
                break;
            }
        }
        return index;
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
}
