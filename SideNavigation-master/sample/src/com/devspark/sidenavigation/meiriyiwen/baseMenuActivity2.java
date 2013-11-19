package com.devspark.sidenavigation.meiriyiwen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.meiriyiwen.LoadMoreListView.LoadMoreListView;
import com.devspark.sidenavigation.meiriyiwen.imageCache.ImageLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-12
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class baseMenuActivity2 extends BaseMenuActivity {

    public LoadMoreListView listView;
    public ArrayList<PaperItem> rawData = new ArrayList<PaperItem>();
    public  cuzyAdapter adapter = null;
    public ImageLoader imageLoader=  null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuactivity2);


        listView = (LoadMoreListView)findViewById(R.id.listView);
        listView.setDividerHeight(0);
        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            public void onLoadMore() {
                // Do the work to load more items at the end of list
                // here
                //new LoadDataTask().execute();
            }
        });



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
        StringBuffer bufferString = new StringBuffer();
        StringBuffer bufferWriter = new StringBuffer();
        try
        {
            Document doc = Jsoup.connect("http://voice.meiriyiwen.com/voice/list_all").get();
            Elements links = doc.select("a[href]");
            Elements writerNotes = doc.select("td");

            int j = 0;
            int writerNotesCount = writerNotes.size();
            for (int i = 0;i<links.size() ;i++)
            {
                Element link = links.get(i);

                String tempString =  link.attr("abs:href");

                if (tempString.contains("vid="))
                {

                    PaperItem temp = new PaperItem();
                    temp.urlString = link.attr("abs:href");
                    String idString = temp.urlString.replace("http://voice.meiriyiwen.com/voice/show/?vid=","");
                    temp.contentString = (idString)+ ".  " + writerNotes.get(3*j).text();
                    temp.titleString = writerNotes.get(3*j+1).text() +"(文)/"+ writerNotes.get(3*j+2).text()+"(音)";

                    temp.imageString = "http://voice.meiriyiwen.com/upload_imgs/"+(idString)+"_img_250.jpg";
                    Log.d("alex huang url string ", temp.urlString);
                    rawData.add(temp);
                    j++;

                }



            }


        }
        catch (Exception e)
        {

            //myString = e.getMessage();
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        testSimpleListView();
        // Click on ListView Row:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Object o = listView.getItemAtPosition(position);
                PaperItem tempItem = rawData.get(position);
                Log.i("alex huang ", tempItem.urlString);
                startWebViewActivity(tempItem.urlString);
            }
        });


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




            return"Executed";
        }

        @Override
        protected void onPostExecute(String result){
            reloadListView();
        }

        @Override
        protected void onPreExecute(){
           // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values){
        }
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







}
