package co.servicedesk.faveo.pro.frontend.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import co.servicedesk.faveo.pro.R;

public class CustomerFiltration extends AppCompatActivity {
ImageView imageView;
TextView textView;
String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_filtration);
        Window window = CustomerFiltration.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(CustomerFiltration.this,R.color.mainActivityTopBar));
        imageView= (ImageView) findViewById(R.id.imageViewBack);
        textView= (TextView) findViewById(R.id.filterCustomers);
        text="To filter customers and agents on your helpdesk you have to click on the filter option in the client list page and you will get some option to filter the customers like" +
                "<b>active users</b>,<b>inactive users</b>,<b>banned users</b>,<b>deleted users</b> and also by the <b>role</b> such as <b>admin</b>,<b>agent</b> and <b>User.</b>";
        textView.setText(Html.fromHtml(text));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
