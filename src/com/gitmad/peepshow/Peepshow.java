package com.gitmad.peepshow;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;

public class Peepshow extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startService(new Intent(this, MediaService.class));
        Cursor mCur = managedQuery(Browser.BOOKMARKS_URI,
        		Browser.HISTORY_PROJECTION, null, null, null
        		);
        mCur.moveToFirst();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        long currentTime = System.currentTimeMillis();
        long fiveMinAgo = 300000;
        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {
            	String title    = mCur.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX);
            	String url      = mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
            	long accessTime = mCur.getLong(Browser.HISTORY_PROJECTION_DATE_INDEX);
            	String encoded = java.net.URLEncoder.encode(url);
            	if(accessTime>(currentTime-fiveMinAgo)){
            		Log.v("titleIdx", title);
                	Log.v("urlIdx", encoded);
                	Log.v("accessTime", df.format(new Date(accessTime)));
            	}
                mCur.moveToNext();
                
            }
        }

        

        
    }
}