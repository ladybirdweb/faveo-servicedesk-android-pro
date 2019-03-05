package co.servicedesk.faveo.pro.frontend.fragments.problem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.adapters.CollaboratorAdapter;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.CollaboratorSuggestion;
import co.servicedesk.faveo.pro.model.Data;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProblemSpecific.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProblemSpecific#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProblemSpecific extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Animation rotation;
    AutoCompleteTextView editTextEmail;
    EditText editTextsubject,editTextDescription;
    ArrayList<CollaboratorSuggestion> emailHint;
    ArrayAdapter<CollaboratorSuggestion> arrayAdapterCC;
    int id=0;
    int id1=0;
    ArrayList<Data> staffitemsauto,departmentItems,impactItems,statusItems,priorityItems,assetItems;
    Spinner staffautocompletetextview,departmentSpinner,impactSpinner,statusSpinner,prioritySpinner;
    ArrayAdapter<Data> autocompletetextview,departmentAdapter,impactAdapter,statusAdapetr,assetAdapter,priorityAdapter;
    EditText assetItem;
    String email1;
    ArrayList<String> getStaffItems;
    int problemId;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProblemSpecific() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProblemSpecific.
     */
    // TODO: Rename and change types and number of parameters
    public static ProblemSpecific newInstance(String param1, String param2) {
        ProblemSpecific fragment = new ProblemSpecific();
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
        View rootView=inflater.inflate(R.layout.fragment_problem_specific,container,false);
        rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        editTextEmail=rootView.findViewById(R.id.email_edittext);
        emailHint=new ArrayList<>();
        getStaffItems = new ArrayList<>();
        arrayAdapterCC=new CollaboratorAdapter(getActivity(),emailHint);
        //arrayAdapterCC=new ArrayAdapter<Data>(CreateTicketActivity.this,android.R.layout.simple_dropdown_item_1line,emailHint);
        staffitemsauto=new ArrayList<>();
        staffautocompletetextview=rootView.findViewById(R.id.autocompletetext);
        departmentSpinner=rootView.findViewById(R.id.department);
        statusSpinner=rootView.findViewById(R.id.spinner_status);
        prioritySpinner=rootView.findViewById(R.id.spinner_pri);
        assetItem=rootView.findViewById(R.id.addAsset);
        impactSpinner=rootView.findViewById(R.id.impact);
        editTextsubject=rootView.findViewById(R.id.sub_edittext);
        editTextDescription=rootView.findViewById(R.id.msg_edittext);
        final Intent intent = getActivity().getIntent();
        problemId= intent.getIntExtra("problemId",0);

        if (InternetReceiver.isConnected()){
            new FetchDependencyForProblem("problem").execute();
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
    private class FetchDependencyForProblem extends AsyncTask<String,Void,String> {
        String type;

        public FetchDependencyForProblem(String type) {
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
                JSONArray jsonArrayPriority = jsonObject.getJSONArray("priority_ids");
                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArrayPriority.getJSONObject(i).getString("priority").equals("")) {
                        data = new Data(Integer.parseInt(jsonArrayPriority.getJSONObject(i).getString("priority_id")), jsonArrayPriority.getJSONObject(i).getString("priority"));
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

                statusItems=new ArrayList<>();
                statusItems.add(new Data(0,"--"));
                JSONArray jsonArraystatus=jsonObject.getJSONArray("status_type_ids");

                for (int i = 0; i < jsonArrayPriority.length(); i++) {
                    if (!jsonArraystatus.getJSONObject(i).getString("name").equals("")) {
                        data = new Data(Integer.parseInt(jsonArraystatus.getJSONObject(i).getString("id")), jsonArraystatus.getJSONObject(i).getString("name"));
                        statusItems.add(data);
                    }
                }

                statusAdapetr = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, statusItems);
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

                departmentAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,departmentItems);
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

                impactAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,impactItems);
                impactSpinner.setAdapter(impactAdapter);




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


                autocompletetextview = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,staffitemsauto);
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

            new FetchProblemDetail(problemId).execute();
        }


    }
    private class FetchProblemDetail extends AsyncTask<String,Void,String>{
        int problemId;

        public FetchProblemDetail(int problemId){
            this.problemId=problemId;
        }
        protected String doInBackground(String... strings) {
            return new Helpdesk().fetchProblemDetail(problemId);
        }
        protected void onPostExecute(String result){


            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String subject=jsonObject1.getString("subject");
                String from=jsonObject1.getString("from");
                String description=jsonObject1.getString("description");
                editTextsubject.setText(subject);
                editTextDescription.setText(description);
                editTextEmail.setText(from);

                editTextEmail.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                editTextDescription.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
                editTextsubject.setOnTouchListener(new View.OnTouchListener() {
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



                email1=from;
                JSONObject jsonObject2=jsonObject1.getJSONObject("priority_id");
                try {
                    if (jsonObject2.getString("priority") != null) {
                        prioritySpinner.setSelection(getIndex(prioritySpinner, jsonObject2.getString("priority")));
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


                JSONObject jsonObject3=jsonObject1.getJSONObject("status_type_id");
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

                JSONObject jsonObject4=jsonObject1.getJSONObject("impact_id");
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

                JSONObject jsonObject5=jsonObject1.getJSONObject("department");
                try {
                    if (jsonObject5.getString("name") != null) {
                        departmentSpinner.setSelection(getIndex(departmentSpinner, jsonObject5.getString("name")));
                        departmentSpinner.setOnTouchListener(new View.OnTouchListener() {
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
                try {
                    JSONObject jsonObject6=jsonObject1.getJSONObject("assigned_id");
                    if (jsonObject6.getString("first_name") != null&&jsonObject6.getString("last_name") != null) {
                        //spinnerHelpTopics.setSelection(getIndex(spinnerHelpTopics, jsonObject1.getString("helptopic_name")));
                        id1= Integer.parseInt(jsonObject6.getString("id"));
                        Log.d("id of the assignee",""+id1);
                        for (int j = 0; j < staffitemsauto.size(); j++) {
                            Data data=staffitemsauto.get(j);
                            if (data.getID()==id1) {
                                Log.d("cameHere","True");
                                Log.d("position",""+j);
                                staffautocompletetextview.setSelection(j);
                            }

                        }

                        staffautocompletetextview.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                        //spinnerStaffs.setSelection(staffItems.indexOf("assignee_email"));
                    }
                    //spinnerHelpTopics.setSelection(Integer.parseInt(jsonObject1.getString("helptopic_id")));
                } catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                } catch (Exception e) {
//                    spinnerHelpTopics.setVisibility(View.GONE);
//                    tv_helpTopic.setVisibility(View.GONE);
                    e.printStackTrace();
                }
//                staffautocompletetextview.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                        //Find the currently focused view, so we can grab the correct window token from it.
//                        View view1 = getActivity().getCurrentFocus();
//                        //If no view currently has focus, create a new one, just so we can grab a window token from it
//                        if (view1 == null) {
//                            view1 = new View(getActivity());
//                        }
//                        imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
//                        return false;
//                    }
//                });


                try{
                    JSONArray jsonArray=jsonObject1.getJSONArray("asset");
                    StringBuilder stringBuilder=new StringBuilder();
                    if (!jsonArray.equals("")){
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject6=jsonArray.getJSONObject(i);
                            stringBuilder.append(jsonObject6.getString("name")).append(",");
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
}
