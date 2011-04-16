package com.gitmad.peepshow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.gitmad.peepshow.api.ApiHandler;
import com.gitmad.peepshow.api.ApiHandler.API_ACTION;
import com.gitmad.peepshow.view.Peep;

import static com.gitmad.peepshow.utils.Messages.Error;
import static com.gitmad.peepshow.utils.Messages.ShowErrorDialog;


public class Peepshow extends Activity implements LocationListener {
    /** Called when the activity is first created. */
    private double m_lon, m_lat;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startService(new Intent(this, MediaService.class));

        try
        {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            final ArrayList<Peep> peeps = ApiHandler.GetInstance().doAction(API_ACTION.GET_PEEPS,
                new Pair<String, String>("latitude", String.format("%f", m_lat)),
                new Pair<String, String>("longitude", String.format("%f", m_lon)));
            final PeepListAdapter adapter = new PeepListAdapter(peeps);
            final ListView list_view = (ListView) findViewById(R.id.peep_log);
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(adapter);
        }
        catch (final Exception ex)
        {
            ShowErrorDialog(this, "SHIT DUN BEEN CRAZY");
            Error(ex.getMessage());
            /*startActivity(new Intent(this, AccountLoginActivity.class));
            finish();*/
        }

        
    }

    public void onLocationChanged(Location location) {
    	((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);
        this.m_lon = location.getLongitude();
        this.m_lat = location.getLatitude();
        
        final ArrayList<Peep> peeps = ApiHandler.GetInstance().doAction(API_ACTION.GET_PEEPS,
                new Pair<String, String>("latitude", String.format("%f", m_lat)),
                new Pair<String, String>("longitude", String.format("%f", m_lon)));
        final PeepListAdapter adapter = new PeepListAdapter(peeps);
        final ListView list_view = (ListView) findViewById(R.id.peep_log);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(adapter);
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }

    class PeepListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
    {
        private final List<Peep> peeps;
        private final LayoutInflater inflater;

        public PeepListAdapter(final List<Peep> peeps)
        {
            this.peeps = peeps;
            this.inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() { return peeps.size(); }

        public Object getItem(int position) { return position; }

        public long getItemId(int position) { return position; }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.peep_row, null);
            }
            Peep peep = peeps.get(position);
            if (peep != null) {
                renderDescription(peep, view);

            }
            /*return v;
            View view = (convertView != null) ? convertView : createView(parent);
            bindView(view, peeps.get(position));*/
            return view;
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            startShow(peeps.get(position));
        }
    }

    private void renderDescription(final Peep peep, final View view)
    {
        if (peep.getType().equalsIgnoreCase("audio")) {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.music_note);
            TextView tt = (TextView) view.findViewById(R.id.toptext);
            TextView bt = (TextView) view.findViewById(R.id.bottomtext);
            if (icon != null) {

            }
            if (tt != null) {
                  tt.setText(String.format("%s - %s", peep.getArtist(), peep.getTitle()));
            }
            if(bt != null){
                  bt.setText("votes: " + peep.getVotes());
            }
        } else if (peep.getType().equalsIgnoreCase("web")) {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.world);
            TextView tt = (TextView) view.findViewById(R.id.toptext);
            TextView bt = (TextView) view.findViewById(R.id.bottomtext);
            if (tt != null) {
                  tt.setText(peep.getUrl());
            }
            if(bt != null){
                  bt.setText("votes: " + peep.getVotes());
            }

        } else if (peep.getType().equalsIgnoreCase("video")) {

        }

    }

    private void startShow(Peep peep)
    {
        Intent next = new Intent();
        if (peep.getType().equalsIgnoreCase("audio")) {
            final String url = ApiHandler.GetInstance().doAction(API_ACTION.SEARCH_YOUTUBE,
                    new Pair<String, String>("", peep.getArtist()),
                    new Pair<String, String>("", peep.getTitle()));
            next = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(next);
        } else if (peep.getType().equalsIgnoreCase("web")) {
            next = new Intent(Intent.ACTION_VIEW, Uri.parse(peep.getUrl()));
            startActivity(next);
        } else if (peep.getType().equalsIgnoreCase("video")) {

        }
    }
}