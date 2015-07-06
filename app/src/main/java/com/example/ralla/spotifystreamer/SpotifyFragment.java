package com.example.ralla.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


public class SpotifyFragment extends Fragment {
    private String userInput;

    private MusicArtistAdapter artistAdapter;

    public SpotifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =   inflater.inflate(R.layout.fragment_main, container, false);
        // setting the adapter
        artistAdapter = new MusicArtistAdapter(getActivity(),new ArrayList<MusicArtist>());
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        listView.setAdapter(artistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 MusicArtist artist = artistAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),TopTrackActivity.class)
                        .putExtra("MyArtist",artist);
                startActivity(intent);
            }
        });


        final EditText  editText = (EditText)rootView.findViewById(R.id.music_search);
        editText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                            userInput = editText.getText().toString();
                            FetchArtistTask artistTask = new FetchArtistTask();
                            artistTask.execute(userInput);
                            return true;
                        }
                        return false;
                    }
                }
        );



        return rootView;
    }



    @Override
    public void onStart() {
        super.onStart();



    }


    /**
     * does the Async task for fetching live data from the spotify surver
     */
    public class FetchArtistTask extends AsyncTask<String,Void, MusicArtist[]>{
        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();


        @Override
        protected MusicArtist[] doInBackground(String... params) {
            ArtistsPager results = null;
            if(params.length==0){
                return null;
            }
                try {
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();
                    results = spotify.searchArtists(params[0]);
                }catch (RetrofitError error){
                    SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);
                }

            if(results == null) {
                Toast toast = new Toast(getActivity());
                Toast.makeText(getActivity(),"No match artist found try another artist", Toast.LENGTH_LONG).show();

                return null;

            }
                // creating MusicArtist object based on the spotifyApi

                 MusicArtist[] musicArtistList = new MusicArtist[results.artists.total];

                if (results.artists.limit < results.artists.total) { // the result is bigger than total
                     musicArtistList = new MusicArtist[results.artists.limit];
                }



                List<Artist> artistsList = results.artists.items;
                Iterator<Artist> aIterator = artistsList.iterator();
                int i =0;
                while (aIterator.hasNext()) {
                    Artist artist = aIterator.next();

                    MusicArtist musicArtist = new MusicArtist();
                    Log.v(LOG_TAG, artist.name);
                    musicArtist.setName(artist.name);
                    Log.v(LOG_TAG, artist.id);
                    musicArtist.setSpotifyID(artist.id);

                    if (artist.images.size() <= 0) {

                      musicArtist.setThumbnail(null);

                    } else  {

                        musicArtist.setThumbnail(artist.images.get(artist.images.size()-1).url);
                        Log.v(LOG_TAG, artist.images.get(artist.images.size()-1).url);
                        Log.v(LOG_TAG, "this is the size :"+artist.images.size());
                    }
                    musicArtistList[i] = musicArtist;
                    i++;
                }
                    Log.v(LOG_TAG,"total: "+i + "artists.total :"+results.artists.limit);

            return musicArtistList;
        }

        /**
         * executes after the doInBackground method
         * @param musicArtists the array of artist that was found by doInBackground method
         */

        @Override
        protected void onPostExecute(MusicArtist[] musicArtists) {

            Log.v(LOG_TAG,"result artists list length" + musicArtists.length);

            if(musicArtists ==null || musicArtists.length==0){
                    Toast.makeText(getActivity(), "No match artist found", Toast.LENGTH_LONG).show();

            }else{
                artistAdapter.clear();
                for (MusicArtist artists : musicArtists){
                    artistAdapter.add(artists); // update data
                }
              //  Log.v(LOG_TAG,"adpater size:" = artistAdapter.)
            }

            // new data is added to the adaptor
        }
    }



}
