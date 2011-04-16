package com.gitmad.peepshow;

import java.text.DateFormat;
import java.util.Date;

import com.gitmad.peepshow.api.ApiHandler;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Browser;
import android.util.Log;
import android.util.Pair;



public class MediaService extends Service {
	private static final String HOST = "10.0.2.2";
	private static final int PORT = 80;
	private static final String TAG = "MEDIA_SERVICE";
	private static boolean stopped = false;

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

	private class Poll implements Runnable,LocationListener {
		private double m_lon, m_lat;
		public void run() {
			while (!stopped) {
				
				
				
				ContentResolver c = getContentResolver();
				Cursor mCur = c.query(Browser.BOOKMARKS_URI,
		        		Browser.HISTORY_PROJECTION, null, null, null);

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
//		            		ApiHandler.GetInstance().doAction(ApiHandler.API_ACTION.SEND_WEB,
//		    						new Pair<String, String>("url", String.format("%f", url)),
//		    						new Pair<String, String>("latitude", String.format("%f", m_lat)),
//		    	                    new Pair<String, String>("longitude", String.format("%f", m_lon));
//		    						
//		    				);
		            		Log.v("titleIdx", title);
		                	Log.v("urlIdx", encoded);
		                	Log.v("accessTime", df.format(new Date(accessTime)));
		            	}
		                mCur.moveToNext();
		                
		            }
		        }
		        mCur.close();
				Log.v("ServiceTest","Service doing stuff");
				
				
				
				/*
				HttpClient client = new DefaultHttpClient();  
		        String getURL = "http://www.google.com";
		        HttpGet get = new HttpGet(getURL);
		        try {
					HttpResponse responseGet = client.execute(get);
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				*/
				try {
					// shutdown and start gps
					//locationManager.removeUpdates(myLocationListener); locationManager = null;
					Thread.sleep(30000);
					
					
				} catch (InterruptedException e) {
					Log.d(TAG, "Service thread sleep interrupted!");
				}
				
			}
		}
		@Override
		public void onLocationChanged(Location location) {
	        this.m_lon = location.getLongitude();
	        this.m_lat = location.getLatitude();
	    }
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}


}
