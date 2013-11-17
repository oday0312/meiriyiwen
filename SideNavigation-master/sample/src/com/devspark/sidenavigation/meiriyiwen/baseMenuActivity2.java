package com.devspark.sidenavigation.meiriyiwen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.meiriyiwen.LoadMoreListView.LoadMoreListView;
import com.devspark.sidenavigation.meiriyiwen.imageCache.ImageLoader;
import com.theindex.CuzyAdSDK.*;


/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-12
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class baseMenuActivity2 extends BaseMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        CuzyAdSDK.getInstance().setContext(this);
        CuzyAdSDK.getInstance().registerApp("200056","051a9e4652fc5b881dfc6ba74d3cd633");


        setContentView(R.layout.menuactivity1);
        listView = (LoadMoreListView)findViewById(R.id.listView);
        listView.setDividerHeight(0);
        int layoutID = com.theindex.CuzyAdSDK.R.layout.cuzy_list_cell_2;

        progressBar = (ProgressBar)findViewById(R.id.myprogressBar);
        progressBar.setVisibility(View.INVISIBLE);
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

    private class LongOperation extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String...params){

            rawData = CuzyAdSDK.getInstance().fetchRawItems("", "鞋子", 0);
            Log.d("cuzy.com: ", "return of raw data: Thindex:  " + rawData.size());

            return"Executed";
        }

        @Override
        protected void onPostExecute(String result){
            progressBar.setVisibility(View.INVISIBLE);
            reloadListView();
        }

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values){
        }
    }









}
