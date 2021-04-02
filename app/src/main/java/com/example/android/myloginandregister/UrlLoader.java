package com.example.android.myloginandregister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**This Activity Load all urls into web view **/
public class UrlLoader extends AppCompatActivity {
    WebView webView; // webView object
    ImageView imageViewBackArrow; // back arrow on top of screen
    String url,actionName;  // url and action Name
    TextView textViewActionName;  // this textView will display action name like find course, Admission office etc
    LinearLayout linearLayoutProgress;
    Button buttonPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_loader);  // define UI for URL loader Activity
        /**Check initView(), getIntentData(), initWebLoader() function
         * definitions in bellow code **/
        initView();// after setContentView call initView Function see init View function
        getIntentData();// call get Intent Data function
        initWebLoader();// call WebLoader

    }

    private void getIntentData() {
        /** when we starting this activity(UrlLoader) we are passing url and action type
         * so in bellow lines we are getting url and action Name from intent
         * **/
        Intent intent=getIntent();// get Activity intent
        url=intent.getStringExtra("url");// get url from intent
        actionName=intent.getStringExtra("actionName");// get action name from intent
        textViewActionName.setText(actionName);// set action name on text View

    }

    /** initView() function will connect all element from UI
     */
    private void initView() {
        webView=findViewById(R.id.webView);// get web view from UI
        textViewActionName=findViewById(R.id.textViewFileName);// get textView action Name from Ui
        linearLayoutProgress=findViewById(R.id.layoutProgress);// get progress layout from UI
        buttonPay=findViewById(R.id.buttonPay);// get button pay button from UI


        // make listener for button pay,pay button will be only visible for fee services
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // when some one click on Button pay this method will be trigger
            url=getString(R.string.pay_fee_link);  // get URL of fee ling from string.xml
            initWebLoader();// start web View with new URL
            }
        });
        imageViewBackArrow=findViewById(R.id.backArrow); // get back arrow from UI

       // click listener for back arrow
        imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();/** when some one click on back arrow on left top of the screen
                 remove UrlLoader Activity from activity stack so the application will automatically resume previous activity
                 */
            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebLoader() {

        // setting up web View
        webView.getSettings().setJavaScriptEnabled(true);// enable java script for web view
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);


        // before starting loading

        // make progress bar layout visible
        linearLayoutProgress.setVisibility(View.VISIBLE);
        // and make webView invisible
        webView.setVisibility(View.GONE);

        // listener for webView
        webView.setWebViewClient(new WebViewClient() {

           // bellow function will be called when page start loading
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            // bellow function will be called when page finish loading
            @Override
            public void onPageFinished(WebView view, final String url) {

                if (view.getTitle().equals(""))// when nothing load like empty page
                    view.reload();// start reloading
                else{// when page loaded successfully

                    // invisible progress layout
                    linearLayoutProgress.setVisibility(View.GONE);
                    //visible webView
                    webView.setVisibility(View.VISIBLE);

                    // action name =Fee Services visible button pay
                    if(actionName.equals("Fee Service")){
                        buttonPay.setVisibility(View.VISIBLE);
                    }else {
                        // if action name other then Fee Services
                        // hide pay button
                        buttonPay.setVisibility(View.GONE);
                    }
                }
            }
            // when web View received SSL Error
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
            // when web view get error when there is no internet
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showToast("Your Internet Connection May not be active Or " + error.getDescription());
                }
            }
        });
        webView.loadUrl(url); // load URL to web View
    }

    //bellow function will show toast
    private void showToast(String s) {
        Toast.makeText(getApplicationContext(),
                s, Toast.LENGTH_SHORT).show();
    }
}