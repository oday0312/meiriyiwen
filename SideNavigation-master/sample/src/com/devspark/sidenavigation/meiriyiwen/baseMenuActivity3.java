package com.devspark.sidenavigation.meiriyiwen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.meiriyiwen.LoadMoreListView.LoadMoreListView;
import com.devspark.sidenavigation.meiriyiwen.imageCache.ImageLoader;


/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-12
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class baseMenuActivity3 extends BaseMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        setContentView(R.layout.menuactivity3);
        testSimpleListView();

        icon = (ImageView) findViewById(android.R.id.icon);
        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);



        if (getIntent().hasExtra(EXTRA_TITLE)) {
            String title = getIntent().getStringExtra(EXTRA_TITLE);
            int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
            setTitle(title);
            //icon.setImageResource(resId);
            sideNavigationView.setMode(getIntent().getIntExtra(EXTRA_MODE, 0) == 0 ? SideNavigationView.Mode.LEFT : SideNavigationView.Mode.RIGHT);
        }
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
            String title = "http://book.meiriyiwen.com";
            //setTitle(title);
            WebView uiwebview = (WebView)findViewById(R.id.webView3);
            uiwebview.setWebViewClient(new Callback());
            uiwebview.getSettings().setBuiltInZoomControls(true);
            uiwebview.getSettings().setJavaScriptEnabled(true);
            uiwebview.loadUrl(title);

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        testCuzySDKfunction();
    }

    public void testSimpleListView()
    {



    }
    public void testCuzySDKfunction()
    {

        new LongOperation().execute("");

    }

    private class LongOperation extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String...params){

            return "hello";
        }

        @Override
        protected void onPostExecute(String result){
            reloadListView();
        }

        @Override
        protected void onPreExecute(){
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values){
        }
    }





    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            return false;
        }
        @Override
        public void onPageStarted(android.webkit.WebView view, java.lang.String url, android.graphics.Bitmap favicon){



        }
        @Override
        public void onPageFinished(android.webkit.WebView view, java.lang.String url) {


        }
        @Override
        public void onReceivedError(android.webkit.WebView view, int errorCode, java.lang.String description, java.lang.String failingUrl){




        }

    }



}
