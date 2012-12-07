package com.shining.bookmanager;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class MenuActivity extends TabActivity {

	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		  Resources res=getResources();
	      TabHost host=getTabHost();
	      TabSpec spec;
	        
	      
	        
	        spec=host
	        		.newTabSpec(res.getString(R.string.tab_bookshelf))
	        		.setIndicator(res.getString(R.string.tab_bookshelf), res.getDrawable(R.drawable.tab_bookshelf))
	        		.setContent(new Intent(this,BookShelf.class));
	        
	        host.addTab(spec);
	        
	        spec=host
	        		.newTabSpec(res.getString(R.string.tab_scan))
	        		.setIndicator(res.getString(R.string.tab_scan),res.getDrawable(R.drawable.tab_scan))
	        		.setContent(new Intent(this,ScanBook.class));
	        
	        host.addTab(spec);
	        
	        host.setCurrentTab(0);
	        
	        host.setOnTabChangedListener(new OnTabChangeListener(){
	        	
	        	public void onTabChanged(String tabId){
	        		
	        		
	        		
	        	}
	        	
	        });
	
	        BookInfoDao.initBookInfoDao(this);
	}

	 public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.activity_search_book, menu);
	        
	        return true;
	    }
	 
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
	        
		 super.onOptionsItemSelected(item);
		 /*
	        switch(item.getItemId())
	        {
	        	case R.id.menu_exit:
	        		finish();
	        		System.exit(0);
	        		break;
	        }
	        */
	        return true;
	        
	    }

}