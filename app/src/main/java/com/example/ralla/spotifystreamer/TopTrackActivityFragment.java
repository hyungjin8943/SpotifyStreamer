package com.example.ralla.spotifystreamer;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

public class TopTrackActivityFragment extends Fragment {
    private MusicArtist myArtist;

    private ArtistTrackAdapter artistTrackAdapter;

    public TopTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_track, container, false);

        artistTrackAdapter = new ArtistTrackAdapter(getActivity(), new ArrayList<ArtistTrack>());
        ListView listView = (ListView)rootView.findViewById(R.id.listview_track);
        listView.setAdapter(artistTrackAdapter);

        ActionBar actionBar = getActivity().getActionBar();
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            myArtist = intent.getExtras().getParcelable("MyArtist");
            ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(myArtist.getName());

            FetchTopTrackTask topTrackTask = new FetchTopTrackTask();
            topTrackTask.execute(myArtist);
        }
        return rootView;
    }

    public class FetchTopTrackTask extends AsyncTask<MusicArtist,Void, ArtistTrack[]>{
        private final String LOG_TAG = FetchTopTrackTask.class.getSimpleName();

        @Override
        protected ArtistTrack[] doInBackground(MusicArtist... params) {
           Tracks results = null;
            if(params.length==0){
                return null;
            }
            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                //setting the option of the county

                Map<String, Object> options = new HashMap<>();
                options.put("country", "US");

                results = spotify.getArtistTopTrack(params[0].getSpotifyID(),options);
            }catch (RetrofitError error){
                SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
            }
            if(results == null) {
                Toast toast = new Toast(getActivity());
                Toast.makeText(getActivity(),"No Tracks found please try another artist", Toast.LENGTH_LONG).show();

                return null;

            }

            ArtistTrack[] artistTracks = new ArtistTrack[results.tracks.size()];
            Log.v(LOG_TAG,"track size: "+ results.tracks.size());
            List<Track> trackList = results.tracks;
            Iterator<Track> trackIterator = trackList.iterator();
            int i=0;
            while (trackIterator.hasNext()){
                Track track = trackIterator.next();
                ArtistTrack artistTrack = new ArtistTrack();
                //setting the title of the track
                Log.v(LOG_TAG, track.name);
                artistTrack.setTrack(track.name);

                Log.v(LOG_TAG, track.id);
                artistTrack.setSoptifyID(track.id);

                Log.v(LOG_TAG, track.album.name);
                artistTrack.setAlbum(track.album.name);

                Log.v(LOG_TAG,track.preview_url);
                artistTrack.setPreviewURL(track.preview_url);

                if(track.album.images.size() <=0){
                    artistTrack.setAlbumThumbnail(null);
                }else {
                    artistTrack.setAlbumThumbnail(track.album.images.get(track.album.images.size()-1).url);
                    Log.v(LOG_TAG, track.album.images.get(track.album.images.size() - 1).url);
                    Log.v(LOG_TAG, "this is the track images list size :" + track.album.images.size());
                }

                artistTracks[i] = artistTrack;
                i++;
            }
            Log.v(LOG_TAG,"total: "+i + "track.total :"+results.tracks.size());


            return artistTracks;
        }

        @Override
        protected void onPostExecute(ArtistTrack[] artistTracks) {
            if(artistTracks != null){
                artistTrackAdapter.clear();

                for (ArtistTrack track : artistTracks){
                    artistTrackAdapter.add(track);
                }
            }
        }
    }
}