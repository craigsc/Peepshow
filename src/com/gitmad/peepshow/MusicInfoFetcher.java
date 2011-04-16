package com.gitmad.peepshow;

import java.io.Serializable;
import java.util.NoSuchElementException;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import com.gitmad.peepshow.exceptions.IncompleteMetadataException;
import com.gitmad.peepshow.exceptions.InvalidMetadataException;

public class MusicInfoFetcher extends Service {
    static final String FROM_MUSIC_STATUS_FETCHER =
        "com.gitmad.peepshow.service.from_music_status_fetcher";
    static final String BROADCAST_ACTION =
        "com.gitmad.peepshow.service.broadcast_action";
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("SHIT BALLS", "FUCK MY TITTIES");
        Track t;
        try {
            t = new Track(intent, this);
            System.out.println(t.getArtist());
        } catch (InvalidMetadataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


}

class Track implements Serializable {
    private static final String sources = "PREU";

    public static long getID(Intent i) {
        // It might be in a long extra or an int extra, so we have to check
        // both:
        final int def = -1;
        long id = i.getLongExtra("id", def);
        if (id != def) {
            return id;
        }
        return i.getIntExtra("id", def);
    }

    public Track(Intent i, Context c) throws InvalidMetadataException {
        String iSource = i.getStringExtra("source");
        if (iSource == null || iSource.length() < 1) {
            source = 'P';
        } else {
            source = iSource.charAt(0);
            if (sources.indexOf(source) == -1) {
                throw new InvalidMetadataException();
            }
        }

        id = getID(i);

        if (id != -1) {
            final String[] columns = new String[] {
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.TRACK,
            };
            Cursor cur = c.getContentResolver().query(
                ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),
                columns, null, null, null);
            if (cur == null) {
                throw new NoSuchElementException();
            }

            try {
                final String UNKNOWN = "<unknown>";

                if (!cur.moveToFirst()) {
                    throw new IncompleteMetadataException();
                }
                artist = cur.getString(cur.getColumnIndex(
                    MediaStore.Audio.AudioColumns.ARTIST));
                if (artist.length() == 0 || artist.equals(UNKNOWN)) {
                    throw new IncompleteMetadataException();
                }
                track = cur.getString(cur.getColumnIndex(
                    MediaStore.Audio.AudioColumns.TITLE));
                if (track.length() == 0 || track.equals(UNKNOWN)) {
                    throw new IncompleteMetadataException();
                }
                length = cur.getLong(cur.getColumnIndex(
                    MediaStore.Audio.AudioColumns.DURATION));
                if (length == 0) {
                    length = null;
                }
                album = cur.getString(cur.getColumnIndex(
                    MediaStore.Audio.AudioColumns.ALBUM));
                if (album.length() == 0 || album.equals(UNKNOWN)) {
                    album = null;
                }
                tracknumber = cur.getInt(cur.getColumnIndex(
                    MediaStore.Audio.AudioColumns.TRACK));
                // The track number is returned with an encoding of the disc
                // number too. We don't need the disc number:
                tracknumber %= 1000;
                if (tracknumber == 0) {
                    tracknumber = null;
                }
                mbtrackid = null;
            } finally {
                cur.close();
            }
        } else {
            // These are required:
            artist = i.getStringExtra("artist");
            if (artist == null || artist.length() == 0) {
                throw new IncompleteMetadataException();
            }
            track = i.getStringExtra("track");
            if (track == null || track.length() == 0) {
                throw new IncompleteMetadataException();
            }

            // This is required if source is P:
            length = new Long(i.getIntExtra("secs", -1));
            if (length == -1) {
                if (source == 'P') {
                    throw new IncompleteMetadataException();
                } else {
                    length = null;
                }
            } else {
                length *= 1000; // We store in milliseconds.
            }

            // These are optional:
            album = i.getStringExtra("album");
            if (album != null && album.length() == 0) {
                album = null;
            }
            tracknumber = i.getIntExtra("tracknumber", -1);
            if (tracknumber == -1) {
                tracknumber = null;
            }
            mbtrackid = i.getStringExtra("mb-trackid");
            if (mbtrackid != null && mbtrackid.length() == 0) {
                mbtrackid = null;
            }
        }
    }

    public String getArtist() {
        return artist;
    }

    public String getTrack() {
        return track;
    }

    public char getSource() {
        return source;
    }

    public Long getMillis() {
        return length;
    }

    public Long getSecs() {
        return length == null ? null : length / 1000;
    }

    public String getAlbum() {
        return album;
    }

    public Integer getTracknumber() {
        return tracknumber;
    }

    public String getMbtrackid() {
        return mbtrackid;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Track)) {
            return false;
        }
        Track other = (Track) o;
        if (id != -1) {
            return id == other.id;
        }
        return
            artist == other.artist &&
            track == other.track &&
            source == other.source &&
            length == other.length &&
            album == other.album &&
            tracknumber == other.tracknumber &&
            mbtrackid == other.mbtrackid;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        String s = (id == -1) ? "" : "(" + id + ") ";
        return s + artist + " - " + track;
    }

    private long id;

    private String artist;
    private String track;
    private char source;
    private Long length;
    private String album;
    private Integer tracknumber;
    private String mbtrackid;

    private static final long serialVersionUID = 1L;
}

