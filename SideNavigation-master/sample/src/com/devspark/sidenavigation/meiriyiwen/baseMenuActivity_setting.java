package com.devspark.sidenavigation.meiriyiwen;

import android.os.Bundle;
import android.widget.*;
import com.devspark.sidenavigation.SideNavigationView;


/**
 * Created with IntelliJ IDEA.
 * User: apple
 * Date: 13-5-12
 * Time: 下午6:24
 * To change this template use File | Settings | File Templates.
 */
public class baseMenuActivity_setting extends BaseMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        setContentView(R.layout.menuactivity_setting);


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

    }





}
