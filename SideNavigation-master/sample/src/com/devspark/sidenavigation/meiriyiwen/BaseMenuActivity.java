package com.devspark.sidenavigation.meiriyiwen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.meiriyiwen.LoadMoreListView.LoadMoreListView;
import com.devspark.sidenavigation.meiriyiwen.imageCache.ImageLoader;
import com.theindex.CuzyAdSDK.*;
import com.umeng.analytics.MobclickAgent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-12
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class BaseMenuActivity extends SherlockActivity implements ISideNavigationCallback{

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.meiriyiwen.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.meiriyiwen.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.meiriyiwen.extra.MODE";
    public static final String EXTRA_WEBURL = "com.devspark.sidenavigation.meiriyiwen.extra.weburl";


    public ImageView icon;
    public SideNavigationView sideNavigationView;

    public LoadMoreListView listView;
    public ArrayList<CuzyTBKItem> rawData = new ArrayList<CuzyTBKItem>();
    public ArrayList<CuzyTBKItem> LoadingMoreArray = new ArrayList<CuzyTBKItem>();

    public String htmlContentString = "";

    protected boolean displayImages = true;
    protected int imageCacheSize = 200;
    protected int imagesInParallel = 2;
    protected String imageCacheDir = null;

    public  cuzyAdapter adapter = null;
    public ImageLoader imageLoader=  null;

    public ProgressBar progressBar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        CuzyAdSDK.getInstance().setContext(this);
        CuzyAdSDK.getInstance().registerApp("200056","051a9e4652fc5b881dfc6ba74d3cd633");


        setContentView(R.layout.menuactivity1);
        listView = (LoadMoreListView)findViewById(R.id.listView);
        listView.setDividerHeight(0);
        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            public void onLoadMore() {
                // Do the work to load more items at the end of list
                // here
                new LoadDataTask().execute();
            }
        });

        // Click on ListView Row:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Object o = listView.getItemAtPosition(position);
                // Here i want have values of the clicked row,- like the name and/or id...,- but i get the following:
                // Output o.toString(): com.example.customlistview.MainActivity$Person@41252310
                Log.i("TEST", o.toString());

                // Here i intend to Start a new Activity passing the "name" of the user (and/or id ...) to the new activity
            }
        });



        testSimpleListView();

        icon = (ImageView) findViewById(android.R.id.icon);
        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);


        progressBar =  (ProgressBar)findViewById(R.id.myprogressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra(EXTRA_TITLE)) {
            String title = getIntent().getStringExtra(EXTRA_TITLE);
            int resId = getIntent().getIntExtra(EXTRA_RESOURCE_ID, 0);
            setTitle(title);
             //icon.setImageResource(resId);
            sideNavigationView.setMode(getIntent().getIntExtra(EXTRA_MODE, 0) == 0 ? SideNavigationView.Mode.LEFT : SideNavigationView.Mode.RIGHT);
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        testCuzySDKfunction();


    }

    public void testSimpleListView()
    {



        imageLoader=new ImageLoader(this);
        adapter = new cuzyAdapter(rawData, this,this, imageLoader,1);
        listView.setAdapter(adapter);


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
            progressBar.setVisibility(View.INVISIBLE);

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
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values){
        }
    }

    public void appendListView()
    {



    }
    public void reloadListView(){

        adapter = new cuzyAdapter(rawData, this,this,imageLoader,1);

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

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

        Intent intent = new Intent(this, baseMenuActivity_setting.class);
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

        Intent intent = new Intent(this, baseMenuActivity_setting.class);
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
            listView.onLoadMoreComplete();

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            //((LoadMoreListView) getListView()).onLoadMoreComplete();
            listView.onLoadMoreComplete();;
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
}
