package com.example.ralla.spotifystreamer;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ralla.spotifystreamer.service.PlaybackService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MusicPlaybackActivityFragment extends DialogFragment {
    private final String LOG_TAG = MusicPlaybackActivityFragment.class.getSimpleName();
    public ArrayList<ArtistTrack> mTopTracksList;
    public int currentPosition;
    public String artistName;

    private ImageButton mPreviousButton;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;
    private TextView mArtist;
    private TextView mAlbumName;
    private ImageView mAlbumArt;
    private TextView mTrackName;
    private SeekBar mPlayerSeekBar;
    private TextView mPlayerStartTime;
    private TextView mPlayerEndTime;

    private ArtistTrack currentTrack;
    private boolean musicPlaying;
    private int progressTime;
    private int tracklength;
    private  int progressByUser;
    private ArtistTrack currentServiceTrack;
    private int trackListSize;

    public MusicPlaybackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // references
        View rootView = inflater.inflate(R.layout.fragment_music_playback, container, false);
        mPreviousButton = (ImageButton)rootView.findViewById(R.id.btn_previous);
        mPlayButton = (ImageButton)rootView.findViewById(R.id.btn_play);
        mNextButton = (ImageButton)rootView.findViewById(R.id.btn_next);
        mArtist = (TextView)rootView.findViewById(R.id.player_artist_name);
        mAlbumName = (TextView)rootView.findViewById(R.id.player_album_name);
        mAlbumArt = (ImageView)rootView.findViewById(R.id.player_album_art);
        mTrackName = (TextView)rootView.findViewById(R.id.player_track_name);
        mPlayerSeekBar = (SeekBar)rootView.findViewById(R.id.seek_bar);
        mPlayerStartTime = (TextView)rootView.findViewById(R.id.start_time);
        mPlayerEndTime = (TextView)rootView.findViewById(R.id.end_time);


        tracklength = -1; // a indicator that is used to identify if the song was new or paused song


        // need to add filer actions
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackService.ACTION_SET_DURATION);
        filter.addAction(PlaybackService.ACTION_PASUE_SUCESS);
        filter.addAction(PlaybackService.ACTION_NOW_PLAYING);
        filter.addAction(PlaybackService.ACTION_SEEKBAR);
        filter.addAction(PlaybackService.ACTION_MUSIC_COMPLETE);
        filter.addAction(PlaybackService.ACTION_NOTIFY_FRAGMANET);

        getActivity().registerReceiver(receiver, filter);


        //intent for getting the information of the song
        Bundle arguments = getArguments();
        // setting the display of the fragment
        if (arguments != null ) {

            progressTime = 0;

            artistName = arguments.getString("Artist");
            // Log.v(LOG_TAG,artistName);
            mArtist.setText(artistName);

            //music position index
            currentPosition= arguments.getInt("Position");
            //  Log.v(LOG_TAG,"position: " + currentPosition );
            mTopTracksList = arguments.getParcelableArrayList("ListOfTopTracks");
            //   Log.v(LOG_TAG, "List size: "+mTopTracksList.size());
            currentTrack = mTopTracksList.get(currentPosition);
            initalizeTrackInfo();



        }

        // listeners for the buttons
        mPlayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!musicPlaying && tracklength ==-1){
                    mPlayerSeekBar.setProgress(progressTime);
                    mPlayerStartTime.setText(Utility.millisecondsToMMSS(progressTime));
                    mPlayerEndTime.setText("-");
                    Log.v(LOG_TAG, "play button clicked");
                    Intent serviceIntent = new Intent(getActivity(), PlaybackService.class);
                    serviceIntent.putExtra("Music",currentTrack);
                    serviceIntent.putExtra("Artist",artistName);
                    serviceIntent.putExtra("TopList",mTopTracksList);
                    serviceIntent.putExtra("Position", currentPosition);
                    serviceIntent.setAction(PlaybackService.ACTION_PLAY);
                    getActivity().startService(serviceIntent);

                }
                if(!musicPlaying && tracklength != -1){

                    Log.v(LOG_TAG, "resume button clicked");
                    Intent serviceIntent = new Intent(getActivity(), PlaybackService.class);
                    serviceIntent.putExtra("Music",currentTrack);
                    serviceIntent.setAction(PlaybackService.ACTION_RESUME);
                    getActivity().startService(serviceIntent);

                }
                if(musicPlaying){
                    if(currentServiceTrack.getSoptifyID().equals(currentTrack.getSoptifyID())){
                        Log.v(LOG_TAG,"pause button clicked");
                        Intent serviceIntent = new Intent(getActivity(), PlaybackService.class);
                        serviceIntent.putExtra("Music",currentTrack);
                        serviceIntent.setAction(PlaybackService.ACTION_PAUSE);
                        getActivity().startService(serviceIntent);
                    }
                    else {

                        Log.v(LOG_TAG, "onther muisc play button clicked while music was played");
                        tracklength=-1; // since new music is being requested to play
                        Intent serviceIntent = new Intent(getActivity(), PlaybackService.class);
                        serviceIntent.putExtra("Music", currentTrack);
                        serviceIntent.setAction(PlaybackService.ACTION_STOP_AND_PLAY_NEW_SONG);

                        getActivity().startService(serviceIntent);
                    }
                }

            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "onther muisc play button clicked while music was played");

                // get next artistTrack info
                trackListSize= mTopTracksList.size();

                currentPosition = currentPosition+1;
                if(currentPosition == trackListSize){ // setting the track to 1st track if it is at the end of the track list
                    currentPosition = 0;
                }
                currentTrack = mTopTracksList.get(currentPosition); // next track
                tracklength=-1; // since new music is being requested to play
                Intent serviceIntent = new Intent(getActivity(), PlaybackService.class);
                serviceIntent.putExtra("Music", currentTrack);
                serviceIntent.setAction(PlaybackService.ACTION_STOP_AND_PLAY_NEW_SONG);
                getActivity().startService(serviceIntent);
                initalizeTrackInfo();
            }
        });
        mPreviousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                trackListSize = mTopTracksList.size();

                currentPosition= currentPosition-1;
                if(currentPosition<0){
                    currentPosition = trackListSize-1;
                }
                currentTrack = mTopTracksList.get(currentPosition); // previous track
                tracklength=-1; // new song so the duration
                Intent serviceIntent = new Intent(getActivity(), PlaybackService.class);
                serviceIntent.putExtra("Music", currentTrack);
                serviceIntent.setAction(PlaybackService.ACTION_STOP_AND_PLAY_NEW_SONG);
                getActivity().startService(serviceIntent);
                initalizeTrackInfo();
            }
        });

        //seekbar listener
        mPlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    progressByUser=mPlayerSeekBar.getProgress();
                  Intent  intent = new Intent(getActivity(), PlaybackService.class);
                    intent.putExtra("Music",currentTrack);
                    intent.putExtra("Progress", progressByUser);
                    intent.setAction(PlaybackService.ACTION_SEEKBAR_CHANGED_BYUSER);
                    getActivity().startService(intent);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });



        return rootView;
    }

    //https://discussions.udacity.com/t/started-service-stopped-immediately-after-mediaplayers-onprepared-was-fired/27255/5

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(LOG_TAG, "inside broadcast reciver action :" + intent.getAction());
            switch (action){
                case PlaybackService.ACTION_SET_DURATION:
                  int time = intent.getExtras().getInt("Duration");
                    setEndTime(time);
                    Log.v(LOG_TAG,"In Here!!!");
                    musicPlaying= true;
                    changePlaybutton(); // update the play button since it is now playing

                    break;

                case PlaybackService.ACTION_PASUE_SUCESS:
                    Log.v(LOG_TAG,"In Here!!");
                    musicPlaying=false;
                    changePlaybutton();
                    // stop updating the seekbar
                    break;

                case PlaybackService.ACTION_NOW_PLAYING:
                    musicPlaying=true;
                    changePlaybutton();
                    break;

                case PlaybackService.ACTION_SEEKBAR:
                    updateSeekbar(intent);
                    break;
                case PlaybackService.ACTION_MUSIC_COMPLETE:
                    musicComplete(intent);
                    break;
                case PlaybackService.ACTION_NOTIFY_FRAGMANET:

                    break;

            }
        }
    };

    private void initalizeTrackInfo(){

        mAlbumName.setText(currentTrack.getAlbum());
        mTrackName.setText(currentTrack.getTrack());

        if(currentTrack.getBigAlbumThumbnail() != null) {
            Picasso.with(getActivity()).load(currentTrack.getBigAlbumThumbnail()).into(mAlbumArt);
        }else{
            Picasso.with(getActivity()).load(R.drawable.blank).into(mAlbumArt);
        }
        mPlayerSeekBar.setProgress(progressTime);
        mPlayerStartTime.setText(Utility.millisecondsToMMSS(progressTime));
        mPlayerEndTime.setText("-");

    }
    private void musicComplete(Intent musicCompleteIntent){
        int count = musicCompleteIntent.getExtras().getInt("progress");
        int maxLength = musicCompleteIntent.getExtras().getInt("duration");

        mPlayerSeekBar.setProgress(count);
        progressTime=count;
        mPlayerStartTime.setText(Utility.millisecondsToMMSS(count));
        musicPlaying=false;
        changePlaybutton();
        tracklength=-1;
    }
    private void updateSeekbar(Intent seekbarIntent){
        int duration = seekbarIntent.getExtras().getInt("duration");
        setEndTime(duration);
        int counter = seekbarIntent.getExtras().getInt("counter");
        int songEnded = seekbarIntent.getExtras().getInt("song_ended");
        currentServiceTrack = seekbarIntent.getExtras().getParcelable("currentTrack");
        musicPlaying=true;
        if(currentTrack.getSoptifyID().equals(currentServiceTrack.getSoptifyID())) {
            mPlayerSeekBar.setProgress(counter);
            mPlayerStartTime.setText(Utility.millisecondsToMMSS(counter)); //update the progress
            setEndTime(duration);
            changePlaybutton();

        }else {
            mPlayerEndTime.setText("-");

        }

    }

    private void changePlaybutton(){

        if(musicPlaying){
          //  Log.v(LOG_TAG,"changing play button");
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        }
        else{
          //  Log.v(LOG_TAG,"changing pause button");
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }
    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void setEndTime(int time){
        Log.v(LOG_TAG, "in set end Time");
        if(tracklength== time) {
            return;
        }
        tracklength= time;
        mPlayerSeekBar.setMax(tracklength);
        mPlayerEndTime.setText(Utility.millisecondsToMMSS(tracklength));
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


}
