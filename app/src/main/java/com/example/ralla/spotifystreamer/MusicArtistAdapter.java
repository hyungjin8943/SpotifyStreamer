package com.example.ralla.spotifystreamer;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ralla on 6/29/15.
 */
public class MusicArtistAdapter extends ArrayAdapter<MusicArtist> {
    private static final String LOG_TAG = MusicArtistAdapter.class.getSimpleName();


    public MusicArtistAdapter(Activity context,List<MusicArtist> musicArtistList){
        super(context, 0, musicArtistList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MusicArtist musicArtist = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        ImageView thumbnailView = (ImageView) convertView.findViewById(R.id.album_image);


        thumbnailView.setImageBitmap(null); // in order to clear the bitmap

        if(musicArtist.existThumbnail()) {
            Log.v(LOG_TAG, musicArtist.getThumbnail());
            Picasso.with(thumbnailView.getContext()).load(musicArtist.getThumbnail()).into(thumbnailView);
        }
        else {
            Picasso.with(thumbnailView.getContext()).load("https://upload.wikimedia.org/wikipedia/en/4/48/Blank.JPG").into(thumbnailView);
        }

        TextView artistView = (TextView) convertView.findViewById(R.id.artist_name);
        artistView.setText(musicArtist.getName());


        return convertView;


    }
}
