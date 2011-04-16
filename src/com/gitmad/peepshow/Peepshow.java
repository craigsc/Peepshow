package com.gitmad.peepshow;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;

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
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TwoLineListItem;
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
        


        final ListView list_view = (ListView) findViewById(R.id.peep_log);
        try
        {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


            final ArrayList<Peep> peeps = ApiHandler.GetInstance().doAction(API_ACTION.GET_PEEPS,
                    new Pair<String, String>("latitude", String.format("%f", m_lat)),
                    new Pair<String, String>("longitude", String.format("%f", m_lon)));
            final PeepListAdapter adapter = new PeepListAdapter(peeps);
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

    @Override
    public void onLocationChanged(Location location) {
        this.m_lon = location.getLongitude();
        this.m_lat = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
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
            TwoLineListItem view = (convertView != null) ? (TwoLineListItem) convertView : createView(parent);
            bindView(view, peeps.get(position));
            return view;
        }

        private TwoLineListItem createView(ViewGroup parent)
        {
            TwoLineListItem item = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            item.getText2().setSingleLine();
            item.getText2().setEllipsize(TextUtils.TruncateAt.END);
            return item;
        }

        private void bindView(TwoLineListItem view, Peep peep)
        {
            renderDescription(peep, view);
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            startShow(peeps.get(position));
        }
    }

    private void renderDescription(final Peep peep, final TwoLineListItem view)
    {
        if (peep.getType().equalsIgnoreCase("audio")) {
            view.getText1().setText(String.format("%s - %s", peep.getArtist(), peep.getTitle()));
            view.getText2().setText("votes: " + peep.getVotes());
        } else if (peep.getType().equalsIgnoreCase("web")) {
            view.getText1().setText(peep.getUrl());
            view.getText2().setText("votes: " + peep.getVotes());

        } else if (peep.getType().equalsIgnoreCase("video")) {

        }

    }

    private void startShow(Peep peep)
    {
        Intent next = new Intent();
        if (peep.getType().equalsIgnoreCase("audio")) {

        } else if (peep.getType().equalsIgnoreCase("web")) {
            next = new Intent(Intent.ACTION_VIEW, Uri.parse(peep.getUrl()));
        } else if (peep.getType().equalsIgnoreCase("video")) {

        }
        startActivity(next);
    }
}