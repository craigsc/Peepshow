package com.gitmad.peepshow.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
import org.json.JSONException;
import org.json.JSONObject;

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
        GET_PEEPS("get", null)
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

        SEND_AUDIO("send", "audio") {
            @Override
            @SuppressWarnings({"unchecked"})
            protected <T> T handleResponse(InputStream response) {
                 if(response.toString() == "error")
                     Log.v("ERROR","SEND AUDIO ERROR");
                 return null;
             }
        },
        
        SEND_WEB("send","web")
        {
            @Override
            @SuppressWarnings({"unchecked"})
            protected <T> T handleResponse(InputStream response) {
                if(response.toString() == "error")
                    Log.v("ERROR","SEND WEB ERROR");
                return null;
            }
        },
        SEARCH_YOUTUBE("get", "video")
        {
            @Override
            @SuppressWarnings({"unchecked"})
            protected <T> T handleResponse(InputStream response) {
                try {
                    String response_str = "";
                    if (response != null) {
                        Writer writer = new StringWriter();

                        char[] buffer = new char[2048];
                        try {
                            Reader reader = new BufferedReader(
                                    new InputStreamReader(response, "UTF-8"));
                            int n;
                            while ((n = reader.read(buffer)) != -1) {
                                writer.write(buffer, 0, n);
                            }
                        } finally {
                            response.close();
                        }

                        //Debug(writer.toString());
                        response_str = writer.toString();
                    } else {
                        response_str=  "";
                    }
                    JSONObject obj = new JSONObject(response_str);
                    return (T) obj.getJSONObject("feed").getJSONArray("entry").getJSONObject(0).getJSONObject("content").getString("src");
                } catch (final IOException ex) {



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        private final String request_type, media_type;
        private API_ACTION(final String request_type, final String media_type)
        {
            this.request_type = request_type;
            this.media_type = media_type;
        }

        public String getRequestType() { return this.request_type; }

        public String getMediaType() { return this.media_type; }

        protected boolean isEditAction() { return false; }

        protected boolean isUpdateAction() { return false; }

        protected abstract <T> T handleResponse(final InputStream response);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T doAction(final API_ACTION action, final Pair<String, String> ... args)
    {

        if (action.equals(API_ACTION.SEARCH_YOUTUBE))
        {
            /* http://gdata.youtube.com/feeds/api/videos?alt=json&q=football+-soccer&orderby=published&start-index=1&max-results=2&v=2&prettyprint=true*/

            try {
                String search_str = "";
                for (int i =0; i< args.length; i++)
                {
                    if (i != 0) {
                        search_str += "+";
                    }
                    search_str += args[i].second;
                }
                search_str = search_str.replace(" ", "%20");
                final String url_string = String.format("http://gdata.youtube.com/feeds/api/videos?alt=json&q=%s&orderby=published&start-index=1&max-reults=1&v=2&prettyprint=true", search_str);
                final URL url = new URL(url_string);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Connection", "close");
                connection.setRequestMethod("GET");

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
        String url_string = String.format("http://%s:%d", WEB_HOST, WEB_PORT);

        try
        {
            url_string += String.format("/%s", action.getRequestType());
            if (action.getMediaType() != null)
                url_string += String.format("/%s", action.getMediaType());

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
