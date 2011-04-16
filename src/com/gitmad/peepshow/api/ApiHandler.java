package com.gitmad.peepshow.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import com.gitmad.peepshow.view.Peep;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static com.gitmad.peepshow.utils.Messages.Debug;
import static com.gitmad.peepshow.utils.Messages.Error;

/**
 * TODO: Enter class description.
 */
public class ApiHandler
{
    private static final ApiHandler s_instance = new ApiHandler();
    
    private static final Gson GSON = new Gson();

    private static Activity caller;

    public static ApiHandler GetInstance() { return s_instance; }

    public static final String WEB_HOST = "peep.craigsc.com";

    public static final Integer WEB_PORT = 80;

    public static enum API_ACTION
    {
        GET_PEEPS("get")
        {
            @Override
            @SuppressWarnings({"unchecked"})
            protected <T> T handleResponse(InputStream response) {
                Reader response_reader = new InputStreamReader(response);
                Type response_type = new TypeToken<Collection<Peep>>(){}.getType();
                Collection<Peep> peep_col = GSON.fromJson(response_reader, response_type);
                return (T) new ArrayList<Peep>(peep_col);
            }
        },
        SEND_AUDIO("get")
        {
        	 @Override
             @SuppressWarnings({"unchecked"})
             protected <T> T handleResponse(InputStream response) {
        		 if(response.toString() == "error")
        			 Log.v("ERROR","SEND AUDIO ERROR");
        		 return null;
        	 }
        	
        },
        SEND_WEB("get")
        {
        	 @Override
             @SuppressWarnings({"unchecked"})
             protected <T> T handleResponse(InputStream response) {
        		 if(response.toString() == "error")
        			 Log.v("ERROR","SEND WEB ERROR");
        		 return null;
        	 }
        	
        },
        
        ;
        
        private final String request_type;
        private API_ACTION(final String request_type)
        {
            this.request_type = request_type;
        }

        public String getRequestType() { return this.request_type; }

        protected boolean isEditAction() { return false; }

        protected boolean isUpdateAction() { return false; }

        protected abstract <T> T handleResponse(final InputStream response);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T doAction(final API_ACTION action, final Pair<String, String> ... args)
    {
        String url_string = String.format("http://%s:%d", WEB_HOST, WEB_PORT);

        try
        {
            url_string += String.format("/%s", action.getRequestType());
            String params = "";
            for (int i = 0; i < args.length; i++)
            {
                params += String.format("/%s", args[i].second);
            }

            Debug("############## URL STRING: " + url_string);
            Debug("############## URL PARAMS: " + params);
            if (!action.isEditAction()) url_string += params;
            final URL url = new URL(url_string);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Connection", "close");
            connection.setRequestMethod("GET");


            if (action.isUpdateAction() || action.isEditAction())
            {
                connection.setDoOutput(true);
                final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(params); out.flush(); out.close();
            }

            int response_code = connection.getResponseCode();
            if (response_code != HttpURLConnection.HTTP_NOT_FOUND)
            {
                final InputStream response = connection.getInputStream();
                try
                {
                    return (T) action.handleResponse(response);
                }
                catch (final Exception ex)
                {
                    Error("ACTION RESULTED IN AN EXCEPTION. THROWING: " + ex.getMessage());
                }
                finally { connection.disconnect(); }
            }
            else
            {
                connection.disconnect();
                // TODO: REMOVE THIS WHEN WE HAVE NETWORK
                return (T) new ArrayList<Peep>();
            }
        }
        catch (final MalformedURLException ex) { Error("Bad URL", ex); }
        catch (final IOException ex) { Error("IO Exception: ", ex); }
        return null;
    }
}
