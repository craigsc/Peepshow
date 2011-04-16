package com.gitmad.peepshow;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MusicBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_MUSIC_STATUS = "com.gitmad.peepshow.service.MUSIC_STATUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SHIT BALLS", "FUCK MY TITTIES");
        Intent i = new Intent(context, MusicInfoFetcher.class);
        i.putExtras(intent);
        context.startService(i);
    }

}
