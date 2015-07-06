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
 * Created by Ralla on 7/6/15.
 */
public class ArtistTrackAdapter extends ArrayAdapter<ArtistTrack> {
    private static final String LOG_TAG = ArtistTrackAdapter.class.getSimpleName();
    public ArtistTrackAdapter(Activity context, List<ArtistTrack> artistTrackList){
        super(context,0,artistTrackList);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArtistTrack artistTrack = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_music,parent, false);
        }
        ImageView thumbnailView = (ImageView) convertView.findViewById(R.id.album_image);

        thumbnailView.setImageBitmap(null);
        if(artistTrack.getAlbumThumbnail() != null){
            Log.v(LOG_TAG, artistTrack.getAlbumThumbnail());
            Picasso.with(thumbnailView.getContext()).load(artistTrack.getAlbumThumbnail()).into(thumbnailView);
        }
        else {
            Picasso.with(thumbnailView.getContext()).load("https://upload.wikimedia.org/wikipedia/en/4/48/Blank.JPG").into(thumbnailView);
        }

        TextView trackView = (TextView) convertView.findViewById(R.id.track);
        trackView.setText(artistTrack.getTrack());

        TextView albumView = (TextView) convertView.findViewById(R.id.album);
        albumView.setText(artistTrack.getAlbum());


        return convertView;
    }
}

