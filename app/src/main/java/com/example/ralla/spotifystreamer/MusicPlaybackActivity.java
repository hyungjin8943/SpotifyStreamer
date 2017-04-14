package com.example.ralla.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MusicPlaybackActivity extends ActionBarActivity {
    //https://discussions.udacity.com/t/dialog-fragment-embedded-phone-ui/25022/3 referred
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playback);
        if(savedInstanceState == null){


            Bundle arg = getIntent().getBundleExtra("TrackList");
            String artistName = arg.getString("Artist");
            int postition = arg.getInt("Position");
            ArrayList<ArtistTrack> mTopTracksList= arg.getParcelableArrayList("ListOfTopTracks");

            Bundle argumentsFromTopTrack = new Bundle();
            argumentsFromTopTrack.putString("Artist",artistName);
            argumentsFromTopTrack.putInt("Position",postition);
            argumentsFromTopTrack.putParcelableArrayList("ListOfTopTracks", mTopTracksList);

            MusicPlaybackActivityFragment fragment = new MusicPlaybackActivityFragment();
                fragment.setArguments(argumentsFromTopTrack);

                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_stream, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
