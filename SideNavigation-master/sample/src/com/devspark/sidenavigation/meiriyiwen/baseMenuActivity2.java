package com.devspark.sidenavigation.meiriyiwen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.meiriyiwen.imageCache.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.R.*;


import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-12
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class baseMenuActivity2 extends SherlockActivity implements ISideNavigationCallback{

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.meiriyiwen.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.meiriyiwen.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.meiriyiwen.extra.MODE";
    public static final String EXTRA_WEBURL = "com.devspark.sidenavigation.meiriyiwen.extra.weburl";


    public ImageView icon;
    public SideNavigationView sideNavigationView;


    public String htmlContentString = "";

    protected boolean displayImages = true;
    protected int imageCacheSize = 200;
    protected int imagesInParallel = 2;
    protected String imageCacheDir = null;

    public ImageLoader imageLoader=  null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        setContentView(R.layout.menuactivity2);




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
            String title = "http://pianke.me/ting/";
            //setTitle(title);
            WebView uiwebview = (WebView)findViewById(R.id.menu2webview);
            uiwebview.setWebViewClient(new Callback());
            uiwebview.getSettings().setBuiltInZoomControls(true);
            uiwebview.getSettings().setJavaScriptEnabled(true);

            uiwebview.getSettings().setUseWideViewPort(true);
            uiwebview.getSettings().setLoadWithOverviewMode(true);

            WebSettings webSettings= uiwebview.getSettings();

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int mDensity = metrics.densityDpi;
            if (mDensity == 240) {
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else if (mDensity == 160) {
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
            } else if(mDensity == 120) {
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            }else if (mDensity == DisplayMetrics.DENSITY_TV){
                uiwebview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            }



            uiwebview.loadUrl(title);

        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }


    public void testCuzySDKfunction()
    {

        new LongOperation().execute("");

    }

    public int currentPageIndex = 1;
    public int LoadingMoreFlag = 0;
    public class LongOperation extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String...params){

            if (LoadingMoreFlag ==0)
            {

                currentPageIndex = 1;
                try{
                    String urlstring = "http://select.yeeyan.org/lists/social/horizontal/" + currentPageIndex;
                    htmlContentString = getResultForHttpGet(urlstring);
                }
                catch (Exception e)
                {

                }


                Log.d("cuzy.com: ", "return of raw data: htmlcontent string:  " + htmlContentString);
            }
            else
            {

                currentPageIndex++;
                try{
                    String urlstring = "http://select.yeeyan.org/lists/social/horizontal/" + currentPageIndex;
                    htmlContentString = getResultForHttpGet(urlstring);
                }
                catch (Exception e)
                {

                }
                Log.d("cuzy.com: ", "return of raw data: htmlcontent string:  " + htmlContentString);

            }

            return"Executed";
        }

        @Override
        protected void onPostExecute(String result){
            //might want to change "executed" for the returned string passed into onPostExecute() but that is upto you

            if (LoadingMoreFlag==0)
            {
                reloadListView();

            }
            else
            {
                appendListView();
            }
        }

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected void onProgressUpdate(Void... values){
        }
    }

    public void appendListView()
    {



    }
    public void reloadListView(){


    }



    public void startWebViewActivity(String urlString)
    {
        Intent intent = new Intent(this, webViewActivity.class);
        intent.putExtra(EXTRA_WEBURL, urlString);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        if (sideNavigationView.getMode() == SideNavigationView.Mode.RIGHT) {
            menu.findItem(R.id.mode_right).setChecked(true);
        } else {
            menu.findItem(R.id.mode_left).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sideNavigationView.toggleMenu();
                break;
            case R.id.mode_left:
                item.setChecked(true);
                sideNavigationView.setMode(SideNavigationView.Mode.LEFT);
                break;
            case R.id.mode_right:
                item.setChecked(true);
                sideNavigationView.setMode(SideNavigationView.Mode.RIGHT);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSideNavigationItemClick(int itemId) {
        switch (itemId) {
            case R.id.side_navigation_menu_item1:
                invokeActivity1(getString(R.string.title1), R.drawable.ic_android1);
                break;

            case R.id.side_navigation_menu_item2:
                invokeActivity2(getString(R.string.title2), R.drawable.ic_android2);
                break;

            case R.id.side_navigation_menu_item3:
                invokeActivity3(getString(R.string.title3), R.drawable.ic_android3);
                break;

            case R.id.side_navigation_menu_item4:
                invokeActivity4(getString(R.string.title4), R.drawable.ic_android4);
                break;

            case R.id.side_navigation_menu_item5:
                invokeActivity5(getString(R.string.title5), R.drawable.ic_android5);
                break;

            default:
                return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // hide menu if it shown
        if (sideNavigationView.isShown()) {
            sideNavigationView.hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Start activity from SideNavigation.
     *
     * @param title title of Activity
     * @param resId resource if of background image
     */
    protected void invokeActivity(String title, int resId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }


    protected void invokeActivity1(String title, int resId ) {

        Intent intent = new Intent(this, BaseMenuActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }

    protected void invokeActivity2(String title, int resId ) {

        Intent intent = new Intent(this, baseMenuActivity2.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }
    protected void invokeActivity3(String title, int resId ) {

        Intent intent = new Intent(this, baseMenuActivity3.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }
    public void invokeActivity4(String title, int resId ) {

        Intent intent = new Intent(this, baseMenuActivity4.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }

    public void invokeActivity5(String title, int resId ) {

        Intent intent = new Intent(this, baseMenuActivity5.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_RESOURCE_ID, resId);
        intent.putExtra(EXTRA_MODE, sideNavigationView.getMode() == SideNavigationView.Mode.LEFT ? 0 : 1);

        // all of the other activities on top of it will be closed and this
        // Intent will be delivered to the (now on top) old activity as a
        // new Intent.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // no animation of transition
        overridePendingTransition(0, 0);
    }


    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }

            // Simulates a background task
            try
            {
                LoadingMoreFlag = 1;
                new LongOperation().execute("");
            }
            catch (Exception e)
            {
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            //adapter.notifyDataSetChanged();

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            //((LoadMoreListView) getListView()).onLoadMoreComplete();
            //listView.onLoadMoreComplete();;
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    public String getResultForHttpGet(String inputUrlstring) throws ClientProtocolException, IOException {
        //服务器  ：服务器项目  ：servlet名称
        String path="";
        String uri=path+inputUrlstring;
        //name:服务器端的用户名，pwd:服务器端的密码
        //注意字符串连接时不能带空格

        String result="";

        HttpGet httpGet=new HttpGet(uri);//编者按：与HttpPost区别所在，这里是将参数在地址中传递
        HttpResponse response=new DefaultHttpClient().execute(httpGet);
        if(response.getStatusLine().getStatusCode()==200){
            HttpEntity entity=response.getEntity();
            result= EntityUtils.toString(entity, HTTP.UTF_8);
        }
        return result;
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
