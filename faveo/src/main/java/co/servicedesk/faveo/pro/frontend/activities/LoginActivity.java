package co.servicedesk.faveo.pro.frontend.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.servicedesk.faveo.pro.Constants;
import co.servicedesk.faveo.pro.R;
import co.servicedesk.faveo.pro.backend.api.v1.Authenticate;
import co.servicedesk.faveo.pro.backend.api.v1.Helpdesk;
import co.servicedesk.faveo.pro.frontend.receivers.InternetReceiver;
import co.servicedesk.faveo.pro.model.MessageEvent;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;


/**
 * This log in activity is for verifying the url and and
 * checking the credentials provided by the User.Here we are using view flipper
 * we will check first if the url provided by the User is registered or not
 * if it is correct then we are going to load the view pager where User will give
 * the User name and the password.If it is right then we are going to load the
 * inbox page to the User.We have used verify url,verify billing url and sign in async task.
 */
public class LoginActivity extends AppCompatActivity {

    TextView textViewFieldError, textViewForgotPassword;
    EditText editTextUsername, editTextPassword, editTextAPIkey;
    int paddingTop, paddingBottom;
    ProgressDialog progressDialogVerifyURL;
    public ProgressDialog progressDialogSignIn;
    ProgressDialog progressDialogBilling;
    List<String> urlSuggestions;
    Animation animation;
    StringBuilder companyURLUser;
    StringBuilder companyURLUser1;
    String companyURL;
    String companyURL1;
    int count=0;
    @BindView(R.id.button_signin)
    Button buttonSignIn;
    @BindView(R.id.fab_verify_url)
    FloatingActionButton buttonVerifyURL;
    @BindView(R.id.input_password)
    AppCompatEditText passwordEdittext;
    @BindView(R.id.input_username)
    AppCompatEditText usernameEdittext;
    AutoCompleteTextView editTextCompanyURL;
    @BindView(R.id.viewFlipper)
    ViewFlipper viewflipper;
    @BindView(R.id.input_layout_username)
    TextInputLayout textInputLayoutUsername;
    @BindView(R.id.input_layout_password)
    TextInputLayout textInputLayoutPass;
    @BindView(R.id.url)
    TextView url;
    @BindView(R.id.flipcolor)
    View flipColor;
    @BindView(R.id.usernameError)
    TextView userNameError;
    @BindView(R.id.passwordError)
    TextView passwordError;
    @BindView(R.id.urlError)
    TextView urlError;
    @BindView(R.id.backButton)
    ImageButton imageBackButton;
    @BindView(R.id.animationLayout)
    LinearLayout linearLayout;
    String urlGivenByUser;
    String error;
    TextView findHelp;
    SpotsDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        Window window = LoginActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.mainActivityTopBar));

        ButterKnife.bind(this);
        try {
            error = Prefs.getString("NoToken", null);
//            Log.d("NoToken",error);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Boolean loginComplete = Prefs.getBoolean("LOGIN_COMPLETE", false);
        if (loginComplete) {
            Constants.URL = Prefs.getString("COMPANY_URL", "");
            Constants.URL1=Prefs.getString("companyurl","");
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        dialog= new SpotsDialog(this, getString(R.string.verifying_url));
        findHelp= (TextView) findViewById(R.id.helpUrl);
        url.setVisibility(View.GONE);
        flipColor.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_200));
        buttonSignIn.setEnabled(true);
        editTextCompanyURL= (AutoCompleteTextView) findViewById(R.id.editText_company_url);
        urlSuggestions=new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line, urlSuggestions);
        editTextCompanyURL.setThreshold(1);
        editTextCompanyURL.setAdapter(adapter);
        usernameEdittext.addTextChangedListener(mTextWatcher);
        passwordEdittext.addTextChangedListener(mTextWatcher);
        //Prefs.putString("URLneedtoshow",null);
        editTextCompanyURL.setText(Prefs.getString("URLneedtoshow",null));

        setUpViews();
        findHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,HelpingActivity.class);
                startActivity(intent);
            }
        });
        animation= AnimationUtils.loadAnimation(LoginActivity.this,R.anim.shake_error);

        /**
         * This is only for xiaomi devices.For getting the notification
         * they have to enable the permission.
         */


        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Enable Permission")
                    .setMessage("To get notifications on your phone in Faveo, you can simply enable the permission in auto start.")
                    .setIcon(R.drawable.ic_warning_black_36dp)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(intent);
                        }
                    });
            builder.create();
            builder.show();
            //this will open auto start screen where User can enable permission for your app

        }

        /**
         * This button is handling the situation
         * where User can go from sign in page to URL
         * page.
         */
        imageBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewflipper.setDisplayedChild(0);
                imageBackButton.setVisibility(View.GONE);
//                editTextCompanyURL.setText("http://");
                editTextCompanyURL.setSelection(editTextCompanyURL.getText().toString().length());
                editTextCompanyURL.requestFocus();
            }
        });

        /**
         * This button is for getting the url from the User
         * and it will check if it is a registered url or not.
         * Here we are using input method manager for hiding the
         * soft keyboard from the User.Implemented thread here
         * for showing the error message for specific period of time.
         */
        buttonVerifyURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String companyURLUser = editTextCompanyURL.getText().toString();
                //Prefs.putString("URL",companyURL);
                companyURL=editTextCompanyURL.getText().toString().trim();
                Prefs.putString("URLneedtoshow",companyURL);
                companyURLUser=new StringBuilder(editTextCompanyURL.getText().toString().trim());
                companyURLUser.insert(0,"https://").toString();
                companyURLUser1=new StringBuilder(editTextCompanyURL.getText().toString().trim());
                companyURLUser1.insert(0,"http://");
                companyURL1=companyURLUser1.toString();
                //companyURL=companyURLUser.toString();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                assert inputManager != null;
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (companyURL.trim().length() == 0 || !Patterns.WEB_URL.matcher(companyURL).matches()) {
                    linearLayout.startAnimation(animation);
                    findHelp.setVisibility(View.GONE);
                    urlError.setVisibility(View.VISIBLE);
                    urlError.setText(getString(R.string.please_enter_valid_url));
                    urlError.setTextColor(Color.parseColor("#ff0000"));
                    urlError.postDelayed(new Runnable() {
                        public void run() {
                            urlError.setVisibility(View.GONE);
                            findHelp.setVisibility(View.VISIBLE);
                        }
                    }, 5000);
//                    linearLayout.startAnimation(animation);
//                    urlError.setVisibility(View.VISIBLE);
//
//                    urlError.postDelayed(new Runnable() {
//                        public void run() {
//                            urlError.setVisibility(View.INVISIBLE);
//                        }
//                    }, 5000);
//                    urlError.setText(getString(R.string.please_enter_valid_url));
//                    urlError.setTextColor(Color.parseColor("#ff0000"));
                    //Toasty.warning(v.getContext(), getString(R.string.please_enter_valid_url), Toast.LENGTH_LONG).show();
                    return;
                    }
                if (InternetReceiver.isConnected()) {
                    urlSuggestions.add(companyURL);
                    //progressDialogVerifyURL.show();
//                    SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                    pDialog.setTitleText("Loading");
//                    pDialog.setCancelable(false);
//                    pDialog.show();

                    //dialog.setMessage(getString(R.string.verifying_url));
                    dialog= new SpotsDialog(LoginActivity.this, getString(R.string.verifying_url));
                    dialog.show();
                    new VerifyURL(LoginActivity.this, companyURL).execute();
                    } else
                    Toasty.warning(v.getContext(), getString(R.string.oops_no_internet), Toast.LENGTH_LONG).show();
            }
        });


        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username=usernameEdittext.getText().toString();
                String password=passwordEdittext.getText().toString();


//                if (username.equals("")&&password.equals("")){
//                    userNameError.setVisibility(View.VISIBLE);
//                    userNameError.setTextColor(Color.parseColor("#ff0000"));
//                    userNameError.setText("Username is required");
//                    passwordError.setVisibility(View.VISIBLE);
//                    passwordError.setTextColor(Color.parseColor("#ff0000"));
//                    passwordError.setText("Password is required");
//                    return;
//                }
//
//             else
                if (username.equals("")&&password!=null){
                    passwordError.setVisibility(View.GONE);
                    usernameEdittext.requestFocus();
                    textInputLayoutUsername.startAnimation(animation);
                    userNameError.setVisibility(View.VISIBLE);
                    userNameError.postDelayed(new Runnable() {
                        public void run() {
                            userNameError.setVisibility(View.INVISIBLE);
                        }
                    }, 5000);
//

                    return;

                    //usernameEdittext.setError();
                    //Toasty.warning(LoginActivity.this, "Please provide User name", Toast.LENGTH_LONG).show();
                }
                else if (username!=null&&password.equals("")){
                    userNameError.setVisibility(View.GONE);
                    textInputLayoutPass.startAnimation(animation);
                    passwordEdittext.requestFocus();
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.postDelayed(new Runnable() {
                        public void run() {
                            passwordError.setVisibility(View.INVISIBLE);
                        }
                    }, 5000);
                    return;

                    //Toasty.warning(LoginActivity.this, "Please provide password", Toast.LENGTH_LONG).show();
                }

//                else if (passwordEdittext.getText().toString().equals("")){
//                    //passwordEdittext.setError("Password is required");
//                    passwordEdittext.startAnimation(animation);
//                    passwordError.setVisibility(View.VISIBLE);
//                    passwordError.setTextColor(Color.parseColor("#ff0000"));
//                    passwordError.setText("This field is required");
//                    Toasty.warning(LoginActivity.this, "Please provide password", Toast.LENGTH_LONG).show();
//
//                }
                else{
                    if (InternetReceiver.isConnected()){
                        textInputLayoutUsername.setEnabled(false);
                        textInputLayoutPass.setEnabled(false);
                        buttonSignIn.setText(R.string.signing_in);
                        dialog= new SpotsDialog(LoginActivity.this, getString(R.string.signing_in));
                        dialog.show();
                        new SignIn(LoginActivity.this, username, password).execute();
                    }
                    else{
                        Toasty.warning(LoginActivity.this, getString(R.string.oops_no_internet), Toast.LENGTH_LONG).show();
                    }
                }
//                 if (usernameEdittext.getText().toString()!=null&&passwordEdittext.getText().toString()!=null){
//
//                }

//                if (InternetReceiver.isConnected()) {
//                    if (username != null && password != null) {
//                        //progressDialogSignIn.show();
//                        textInputLayoutUsername.setEnabled(false);
//                        textInputLayoutPass.setEnabled(false);
//                        buttonSignIn.setText(R.string.signing_in);
//                        new SignIn(LoginActivity.this, username, password).execute();
//                    }
//                }else
//                    Toasty.warning(LoginActivity.this, getString(R.string.oops_no_internet), Toast.LENGTH_LONG).show();
            }

        });

//        buttonSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setNormalStates();
//
//                String username = editTextUsername.getText().toString();
//                String password = editTextPassword.getText().toString();
//                if (username.trim().length() == 0 || password.trim().length() == 0) {
//                    if (username.trim().length() == 0)
//                        setUsernameErrorStates();
//                    else
//                        setPasswordErrorStates();
//                    return;
//                }
//                if (InternetReceiver.isConnected()) {
//                    progressDialogSignIn.show();
//                    new SignIn(LoginActivity.this, username, password).execute();
//                } else
//                    Toast.makeText(v.getContext(), "Oops! No internet", Toast.LENGTH_LONG).show();
//            }
//        });

        //Forgot password
//        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//                startActivity(intent);
//            }
//        });

    }
//    private class NewTask extends AsyncTask<String,Void,String>{
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//        }
//    }
    /**
     *Async task is for verifying the url.
     */
    private class VerifyURL extends AsyncTask<String, Void, String> {
        Context context;
        String companyURL;
        String baseURL;
        String apiDisabled;

        VerifyURL(Context context, String companyURL) {
            this.context = context;
            this.companyURL = companyURL;
            baseURL = companyURL;
        }

        protected String doInBackground(String... urls) {
            if (!companyURL.endsWith("/"))
                companyURL = companyURL.concat("/");
            return new Helpdesk().getBaseURL(companyURL);
        }

        protected void onPostExecute(String result) {
            //progressDialogVerifyURL.dismiss();
            dialog.dismiss();

            //Log.d("result",result);
            try {
                apiDisabled = Prefs.getString("400", null);
                 if (apiDisabled.equals("badRequest")) {
                    Prefs.putString("400", "null");
                    Toasty.info(context, getString(R.string.apiDisabled), Toast.LENGTH_LONG).show();
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            if (result == null) {
                count++;
                Toasty.warning(context, getString(R.string.invalid_url), Toast.LENGTH_LONG).show();
                return;
//                if (count==0){
//                    Toasty.warning(context, getString(R.string.invalid_url), Toast.LENGTH_LONG).show();
//                    count--;
//                    return;
//                }

                //new VerifyURLSecure(LoginActivity.this,companyURL1).execute();




            }

            else if (result.contains("success")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    dynamicShortcut();
                }
//                urlSuggestions.add(baseURL);
//                Set<String> set = new HashSet<>(urlSuggestions);
//                Prefs.putStringSet("URL_SUG", set);

                Prefs.putString("BASE_URL", baseURL);
                dialog= new SpotsDialog(LoginActivity.this, getString(R.string.access_checking));
                dialog.show();
                //progressDialogBilling.show();
                new VerifyBilling(LoginActivity.this, baseURL).execute();
                Prefs.putString("companyurl",urlGivenByUser);
                Prefs.putString("COMPANY_URL", companyURL + "api/v1/");
                Constants.URL = Prefs.getString("COMPANY_URL", "");
                Constants.URL1=Prefs.getString("companyurl",null);
                Prefs.putString("domain","https://");
                Prefs.putString("companyUrl",companyURL);
                Log.d("companyurl",companyURL);

//                if (BuildConfig.DEBUG) {
//                    viewflipper.showNext();
//                    imageBackButton.setVisibility(View.VISIBLE);
//                    url.setText(baseURL);
//                    url.setVisibility(View.VISIBLE);
//                    flipColor.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.faveo));
//                } else {
//                    progressDialogBilling.show();
//                    new VerifyBilling(LoginActivity.this, baseURL).execute();
//                }

            } else {
                linearLayout.startAnimation(animation);
                urlError.setVisibility(View.VISIBLE);
                urlError.setText(getString(R.string.error_verifying_url));
                urlError.setTextColor(Color.parseColor("#ff0000"));
                urlError.postDelayed(new Runnable() {
                    public void run() {
                        urlError.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
//                urlError.setVisibility(View.VISIBLE);
//                urlError.setText(getString(R.string.error_verifying_url));
//                urlError.setTextColor(Color.parseColor("#ff0000"));
//                urlError.postDelayed(new Runnable() {
//                    public void run() {
//                        urlError.setVisibility(View.INVISIBLE);
//                    }
//                }, 5000);

                //Toasty.error(context, getString(R.string.error_verifying_url), Toast.LENGTH_LONG).show();
            }
        }
    }
//    private class VerifyURLSecure extends AsyncTask<String, Void, String> {
//        Context context;
//        String companyURL;
//        String baseURL;
//
//        VerifyURLSecure(Context context, String companyURL) {
//            this.context = context;
//            this.companyURL = companyURL;
//            baseURL = companyURL;
//        }
//
//        protected String doInBackground(String... urls) {
//            if (!companyURL.endsWith("/"))
//                companyURL = companyURL.concat("/");
//            return new Helpdesk().getBaseURL(companyURL);
//        }
//
//        protected void onPostExecute(String result) {
//            progressDialogVerifyURL.dismiss();
//            if (result == null) {
//                linearLayout.startAnimation(animation);
//                urlError.setVisibility(View.VISIBLE);
//                urlError.setText(getString(R.string.error_verifying_url));
//                urlError.setTextColor(Color.parseColor("#ff0000"));
//                urlError.postDelayed(new Runnable() {
//                    public void run() {
//                        urlError.setVisibility(View.INVISIBLE);
//                    }
//                }, 9000);
//                //Toasty.warning(context, getString(R.string.invalid_url), Toast.LENGTH_LONG).show();
//                return;
//
//            }
//
//            else if (result.contains("success")) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
//                    dynamicShortcut();
//                }
////                urlSuggestions.add(baseURL);
////                Set<String> set = new HashSet<>(urlSuggestions);
////                Prefs.putStringSet("URL_SUG", set);
//
//                Prefs.putString("BASE_URL", baseURL);
//                Prefs.putString("companyurl",urlGivenByUser);
//                Prefs.putString("COMPANY_URL", companyURL + "api/v1/");
//                Constants.URL = Prefs.getString("COMPANY_URL", "");
//                Constants.URL1=Prefs.getString("companyurl",null);
//                Prefs.putString("domain","http://");
//                Log.d("companyurl",Constants.URL1);
//                Prefs.putString("companyUrl",Constants.URL1);
//                if (BuildConfig.DEBUG) {
//                    viewflipper.showNext();
//                    imageBackButton.setVisibility(View.VISIBLE);
//                    url.setText(baseURL);
//                    url.setVisibility(View.VISIBLE);
//                    flipColor.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.faveo));
//                } else {
//                    progressDialogBilling.show();
//                    new VerifyBilling(LoginActivity.this, baseURL).execute();
//                }
//
//            } else {
//                linearLayout.startAnimation(animation);
//                urlError.setVisibility(View.VISIBLE);
//                urlError.setText(getString(R.string.error_verifying_url));
//                urlError.setTextColor(Color.parseColor("#ff0000"));
//                urlError.postDelayed(new Runnable() {
//                    public void run() {
//                        urlError.setVisibility(View.INVISIBLE);
//                    }
//                }, 5000);
////                urlError.setVisibility(View.VISIBLE);
////                urlError.setText(getString(R.string.error_verifying_url));
////                urlError.setTextColor(Color.parseColor("#ff0000"));
////                urlError.postDelayed(new Runnable() {
////                    public void run() {
////                        urlError.setVisibility(View.INVISIBLE);
////                    }
////                }, 5000);
//
//                //Toasty.error(context, getString(R.string.error_verifying_url), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    //N 7.0-> shortcuts

    /**
     * This method is for getting the short cut if we are
     * holding the icon for long time.
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    void dynamicShortcut() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        ShortcutInfo webShortcut = new ShortcutInfo.Builder(this, "Web app")
                .setShortLabel("Web App")
                .setLongLabel("Open the web app")
                .setIcon(Icon.createWithResource(this, R.drawable.add))
                .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(editTextCompanyURL.getText().toString())))
                .build();

        shortcutManager.setDynamicShortcuts(Collections.singletonList(webShortcut));
    }

    /**
     * This async task is for verifying the url,for paid version only.
     */
    @SuppressLint("StaticFieldLeak")
    private class VerifyBilling extends AsyncTask<String, Void, String> {
        Context context;
        String baseURL;

        VerifyBilling(Context context, String baseURL) {
            this.context = context;
            this.baseURL = baseURL;
        }

        @Override
        protected String doInBackground(String... params) {

            return new Helpdesk().getCheckBillingURL(baseURL);
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialogBilling.dismiss();
            dialog.dismiss();
            Log.d("Response BillingVerfy", result + "");
            if (result == null) {
                Toasty.error(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                return;
            }
            if (result.contains("success")) {
                Prefs.putString("BillingUrl",baseURL);
                urlSuggestions.add(baseURL);
                viewflipper.showNext();
                imageBackButton.setVisibility(View.VISIBLE);
                url.setText(baseURL);
                url.setVisibility(View.VISIBLE);
                flipColor.setBackgroundColor(ContextCompat.getColor(context, R.color.faveo));
            } else if (result.contains("fails")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.access_denied)
                        .setMessage(R.string.please_purchase_faveo)
                        .setIcon(R.drawable.ic_warning_black_36dp)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();

                            }
                        });
                builder.create();
                builder.show();

            } else
                Toasty.error(context, getString(R.string.error_checking_pro), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This button will work after the url is been verified
     * it will take the User name and password and it will check
     * it is valid or not.
     */


//    @OnClick(R.id.button_signin)
//    public void signIn() {
//        String username = usernameEdittext.getText().toString();
//        String password = passwordEdittext.getText().toString();
//        if (InternetReceiver.isConnected()) {
//            //progressDialogSignIn.show();
//            textInputLayoutUsername.setEnabled(false);
//            textInputLayoutPass.setEnabled(false);
//            buttonSignIn.setText(R.string.signing_in);
//            new SignIn(LoginActivity.this, username, password).execute();
//        } else
//            Toasty.warning(this, getString(R.string.oops_no_internet), Toast.LENGTH_LONG).show();
//    }

    /**
     * Post the User credentials to server. This will execute after the
     * verify url is being verified.
     */
    private class SignIn extends AsyncTask<String, Void, String> {
        Context context;
        String username;
        String password;
        String apiDisabled;
        SignIn(Context context, String username, String password) {
            this.context = context;
            this.username = username;
            this.password = password;
        }

        protected String doInBackground(String... urls) {
            return new Authenticate().postAuthenticateUser(username, password);
        }

        protected void onPostExecute(String result) {
            dialog.dismiss();
            Log.d("Response login", result + "");
            //progressDialogSignIn.dismiss();
            if (result == null) {
//                Intent intent=new Intent(LoginActivity.this,LoginActivity.class);
//                startActivity(intent);

                Toasty.error(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                apiDisabled = Prefs.getString("unauthorized", null);
                if (apiDisabled.equals("true")){
                    Prefs.putString("unauthorized","false");
                    textInputLayoutUsername.setEnabled(true);
                    textInputLayoutPass.setEnabled(true);
                    buttonSignIn.setText(getString(R.string.sign_in));
                    Toasty.info(context, getString(R.string.apiDisabled), Toast.LENGTH_LONG).show();
                    textInputLayoutUsername.setEnabled(true);
                    textInputLayoutPass.setEnabled(true);
                    return;
                }

            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try{
                apiDisabled = Prefs.getString("403", null);
                if (apiDisabled.equals("403")){
                    Prefs.putString("403","false");
                    Toasty.info(context, getString(R.string.bannedOrdeactivated), Toast.LENGTH_LONG).show();
                    textInputLayoutUsername.setEnabled(true);
                    textInputLayoutPass.setEnabled(true);
                    //progressBar.setVisibility(View.GONE);
                    buttonSignIn.setText(getString(R.string.sign_in));
                    return;
                }
            }catch (NullPointerException e){

            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("camehere","true");
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String token = jsonObject1.getString("token");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("user");
                String userID = jsonObject2.getString("id");
                String profile_pic = jsonObject2.getString("profile_pic");
                Prefs.putString("profilePicture",profile_pic);
                String role = jsonObject2.getString("role");
                Log.d("ROLE",role);
                if (role.equals("User")){
                    textInputLayoutUsername.setEnabled(true);
                    textInputLayoutPass.setEnabled(true);
                    buttonSignIn.setText(getString(R.string.sign_in));
                    Toasty.warning(getApplicationContext(), getString(R.string.userLogIn), Toast.LENGTH_LONG).show();
                    return;

                }
                else{
                    String firstName = jsonObject2.getString("first_name");
                    String lastName = jsonObject2.getString("last_name");
                    String userName = jsonObject2.getString("user_name");
                    String email=jsonObject2.getString("email");
                    String clientname;
                    if (firstName == null || firstName.equals(""))
                        clientname = userName;
                    else
                        clientname = firstName + " " + lastName;
                    Prefs.putString("clientNameForFeedback",clientname);
                    Prefs.putString("emailForFeedback",email);
                    Prefs.putString("PROFILE_NAME",clientname);
                    SharedPreferences.Editor authenticationEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    authenticationEditor.putString("ID", userID);
                    authenticationEditor.putString("TOKEN", token);
                    authenticationEditor.putString("USERNAME", username);
                    authenticationEditor.putString("PASSWORD", password);
                    authenticationEditor.putBoolean("LOGIN_COMPLETE", true);
                    authenticationEditor.putString("PROFILE_PIC", profile_pic);
                    authenticationEditor.putString("ROLE", role);
                    authenticationEditor.putString("PROFILE_NAME", clientname);
                    //authenticationEditor.putString("AGENT_SIGN", jsonObject1.getString("agent_sign"));
                    authenticationEditor.apply();



                    Prefs.putString("FCMtoken", FirebaseInstanceId.getInstance().getToken());

                    new SendingFCM(LoginActivity.this, FirebaseInstanceId.getInstance().getToken()).execute();

                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }




            } catch (JSONException e) {
                textInputLayoutUsername.setEnabled(true);
                textInputLayoutPass.setEnabled(true);
                buttonSignIn.setText(getString(R.string.sign_in));
                //Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                userNameError.setVisibility(View.VISIBLE);
                userNameError.setText(R.string.credentialsMismatch);
                userNameError.setTextColor(Color.parseColor("#ff0000"));
                userNameError.postDelayed(new Runnable() {
                    public void run() {
                        userNameError.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
                //StyleableToast st = new StyleableToast(LoginActivity.this, getString(R.string.wrong_credentials), Toast.LENGTH_LONG);
                passwordEdittext.startAnimation(animation);
                usernameEdittext.startAnimation(animation);
                //Toasty.error(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

//    private void setNormalStates() {
//        textViewFieldError.setVisibility(View.INVISIBLE);
//        editTextUsername.setBackgroundResource(R.drawable.edittext_modified_states);
//        editTextPassword.setBackgroundResource(R.drawable.edittext_modified_states);
//        editTextAPIkey.setBackgroundResource(R.drawable.edittext_modified_states);
//        editTextAPIkey.setPadding(0, paddingTop, 0, paddingBottom);
//        editTextUsername.setPadding(0, paddingTop, 0, paddingBottom);
//        editTextPassword.setPadding(0, paddingTop, 0, paddingBottom);
//    }
//
//    private void setUsernameErrorStates() {
//        textViewFieldError.setText("Please insert username");
//        textViewFieldError.setVisibility(View.VISIBLE);
//        editTextUsername.setBackgroundResource(R.drawable.edittext_error_state);
//        editTextUsername.setPadding(0, paddingTop, 0, paddingBottom);
//    }
//
//    private void setPasswordErrorStates() {
//        textViewFieldError.setText("Please insert password");
//        textViewFieldError.setVisibility(View.VISIBLE);
//        editTextPassword.setBackgroundResource(R.drawable.edittext_error_state);
//        editTextPassword.setPadding(0, paddingTop, 0, paddingBottom);
//    }

    /**
     * To initialize the views.Here we are initializing all
     * the edit text,progress dialog and other views.
     */
    private void setUpViews() {

        progressDialogVerifyURL = new ProgressDialog(this);
        progressDialogVerifyURL.setMessage(getString(R.string.verifying_url));
        progressDialogVerifyURL.setCancelable(false);

        progressDialogSignIn = new ProgressDialog(this);
        progressDialogSignIn.setMessage(getString(R.string.signing_in));
        progressDialogSignIn.setCancelable(false);

        progressDialogBilling = new ProgressDialog(this);
        progressDialogBilling.setMessage(getString(R.string.access_checking));
        progressDialogBilling.setCancelable(false);


//        InputFilter filter = new InputFilter() {
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                String filtered = "";
//                for (int i = start; i < end; i++) {
//                    char character = source.charAt(i);
//                    if (!Character.isWhitespace(character)) {
//                        filtered += character;
//                    }
//                }
//
//                return filtered;
//            }
//
//        };

        // editTextCompanyURL = (EditText) findViewById(R.id.editText_company_url);
        // editTextCompanyURL.setFilters(new InputFilter[]{filter});

//
//            if (editTextCompanyURL != null) {
//                editTextCompanyURL.setText("");
//                editTextCompanyURL.append("http://");
//            }

//        String url=Prefs.getString("URL","");
//        editTextCompanyURL.setText(url);
//        editTextCompanyURL.requestFocus(url.length());

//        Set<String> set = Prefs.getStringSet("URL_SUG", new HashSet<String>());
//        urlSuggestions = new ArrayList<>(set);
//



        //viewflipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        // buttonVerifyURL = (FloatingActionButton) findViewById(R.id.fab_verify_url);
        textViewFieldError = (TextView) findViewById(R.id.textView_field_error);
        textViewForgotPassword = (TextView) findViewById(R.id.forgot_password);
        editTextUsername = (EditText) findViewById(R.id.editText_username);
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        editTextAPIkey = (EditText) findViewById(R.id.editText_company_api_key);
        //buttonSignIn = (Button) findViewById(R.id.button_1);
        paddingTop = editTextUsername.getPaddingTop();
        paddingBottom = editTextUsername.getPaddingBottom();

    }

    /**
     * While resuming it will check if the internet
     * is available or not.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        //FaveoApplication.getInstance().setInternetListener(this);
        checkConnection();
    }

    /**
     * For checking if the internet is available or not.
     */
    private void checkConnection() {
        boolean isConnected = InternetReceiver.isConnected();
        showSnackIfNoInternet(isConnected);
    }

    /**
     * Display the snackbar if network connection is not there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnackIfNoInternet(boolean isConnected) {
        if (!isConnected) {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.sry_not_connected_to_internet, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

    }

    /**
     * Display the snackbar if network connection is there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnack(boolean isConnected) {

        if (isConnected) {

            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.connected_to_internet, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else {
            showSnackIfNoInternet(false);
        }

    }

//    /**
//     * Callback will be triggered when there is change in
//     * network connection
//     */
//    @Override
//    public void onNetworkConnectionChanged(boolean isConnected) {
//        showSnack(isConnected);
//    }

    /**
     * To send the FCM token to server.This is required
     * for getting push notification from the server.
     */
    private class SendingFCM extends AsyncTask<String, Void, String> {
        Context context;
        String token;

        SendingFCM(Context context, String token) {
            this.context = context;
            this.token = token;
        }


        @Override
        protected String doInBackground(String... params) {

            return new Helpdesk().postFCMToken(token, Prefs.getString("ID", null));
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Response FCM", result + "");
            if (result == null) {
                return;
            }

            if (result.contains("success")) {
                Log.d("RESPONSE_FCM_Success", "" + result);
            } else if (result.contains("fails")) {
                Log.d("RESPONSE_FCM_FAILS", "" + result);
            }
        }
    }

    /**
     * For checking if the User related fields
     * are empty or not.
     */
    void checkFieldsForEmptyValues() {

        String username = usernameEdittext.getText().toString();
        String password = passwordEdittext.getText().toString();
//        if (username.trim().length()==0){
//            textInputLayoutUsername.setError("Enter your full name");
//        }
//        else if (password.trim().length()==0){
//            tex
//        }
//        if (username.trim().length() == 0 || password.trim().length() == 0) {
//            buttonSignIn.setEnabled(false);
//        } else {
//            buttonSignIn.setEnabled(true);
//        }

        if (username.trim().length() == 0 && password.trim().length() == 0) {
            buttonSignIn.setEnabled(true);
        }

        else{
            buttonSignIn.setEnabled(true);
        }
// else if (username.trim().length() <= 2) {
//            textInputLayoutUsername.setError("Username must be at least 3 characters");
//        } else {
//            textInputLayoutUsername.setError(null);
//            buttonSignIn.setEnabled(true);
//        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    /**
     * This method will be called when a MessageEvent is posted (in the UI thread for Toast).
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        showSnack(event.message);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}