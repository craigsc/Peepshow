package com.gitmad.peepshow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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

import static com.gitmad.peepshow.utils.Messages.ShowErrorDialog;

public class Peepshow extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ListView list_view = (ListView) findViewById(R.id.peep_log);
        try
        {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            final ArrayList<Peep> peeps = ApiHandler.GetInstance().doAction(API_ACTION.GET_PEEPS,
                    new Pair<String, String>("latitude", String.format("%f", lastKnownLocation.getLatitude())),
                    new Pair<String, String>("longitude",String.format("%f", lastKnownLocation.getLongitude())));
            final PeepListAdapter adapter = new PeepListAdapter(peeps);
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(adapter);
        }
        catch (final Exception ex)
        {
            ShowErrorDialog(this, "SHIT DUN BEEN CRAZY");
            /*startActivity(new Intent(this, AccountLoginActivity.class));
            finish();*/
        }
        
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
            view.getText1().setText(peep.toString());
            view.getText2().setText(peep.toString());
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            startShow(peeps.get(position));
        }
    }

    private void startShow(Peep peep)
    {
        Intent next = new Intent();
       /* next.setClass(this, Quest.class);
        next.putExtra("quest", quest);
        next.putExtra("current_sequence", 0);*/
        startActivity(next);
    }
}