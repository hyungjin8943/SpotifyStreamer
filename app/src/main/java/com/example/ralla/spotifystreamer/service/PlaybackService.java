package com.example.ralla.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.ralla.spotifystreamer.ArtistTrack;
import com.example.ralla.spotifystreamer.MusicPlaybackActivityFragment;

import java.io.IOException;
import java.util.ArrayList;


// reference https://www.youtube.com/watch?v=tC3FUF47tlo

public class PlaybackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{
    public static final String LOG_TAG = PlaybackService.class.getSimpleName();
    public static final String ACTION_PLAY = "com.example.ralla.spotifystreamer.action_play";
    public static final String ACTION_RESUME= "com.example.ralla.spotifystreamer.action_pause";
    public static final String ACTION_PAUSE= "com.example.ralla.spotifystreamer.action_resume";
    public static final String ACTION_NOW_PLAYING="com.example.ralla.spotifystreamer.action_now_paying";
    public static final String ACTION_SET_DURATION = "com.example.ralla.spotifystreamer.set_duration";
    public static final String ACTION_PASUE_SUCESS = "com.example.ralla.spotifystreamer.pause_sucess";
    public static final String ACTION_SEEKBAR = "com.example.ralla.spotifystreamer.seekbar";
    public static final String ACTION_MUSIC_COMPLETE="com.example.ralla.spotifystreamer.music_complete";
    public static final String ACTION_SEEKBAR_CHANGED_BYUSER="com.example.ralla.spotifystreamer.seekbar_changed_byuser";
    public static final String ACTION_STOP_AND_PLAY_NEW_SONG = "com.example.ralla.spotifystreamer.stop_and_play_new_song";
    public static final String ACTION_STOP="com.example.ralla.spotifystreamer.stop";
    public static final String ACTION_NOTIFY_FRAGMANET="com.exmaple.ralla.spotifystreamer.notify_fragment";

    private ArtistTrack mArtistTrack;
    private String mArtistName;
    private ArrayList<ArtistTrack> mTopTracksList;
    private int position;
    private  MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private int trackPosition;
    private static int songEnded;
    private static final int NOTIFICATION_ID = 1;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    public PlaybackService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG,"onStartCommand called");
        ArtistTrack track;
        String action = intent.getAction();

        switch (action){
            case PlaybackService.ACTION_PLAY:
                Log.v(LOG_TAG,"action play is called");
                mArtistTrack = intent.getExtras().getParcelable("Music");
                mArtistName = intent.getExtras().getString("Artist");
                mTopTracksList=intent.getExtras().getParcelableArrayList("TopList");
                position=intent.getExtras().getInt("Position");
                Log.v(LOG_TAG,"url is "+mArtistTrack.getPreviewURL());
                songEnded=0;
                playTrack(mArtistTrack);
                break;

            case PlaybackService.ACTION_PAUSE:
                Log.v(LOG_TAG, "action pause is called");
                track = intent.getExtras().getParcelable("Music");
                Log.v(LOG_TAG, "track id = "+ track.getSoptifyID() +" mArtistTrack id ="+mArtistTrack.getSoptifyID());
                if (track.getSoptifyID().equals(mArtistTrack.getSoptifyID())){
                   pauseTrack();
                }
                break;

            case PlaybackService.ACTION_RESUME:
                Log.v(LOG_TAG,"action resume is called");
                 track = intent.getExtras().getParcelable("Music");
                if(track.getSoptifyID().equals(mArtistTrack.getSoptifyID())){
                    resumeTrack();
                }
                else {
                    Log.v(LOG_TAG,"In side here");
                }
                break;
            case PlaybackService.ACTION_SEEKBAR_CHANGED_BYUSER:
                Log.v(LOG_TAG,"Seekbar has been changed by user");
                track = intent.getExtras().getParcelable("Music");
                if(track.getSoptifyID().equals(mArtistTrack.getSoptifyID())){
                    updateSeekbarFromUser(intent);
                }
                break;
            case PlaybackService.ACTION_STOP_AND_PLAY_NEW_SONG:
                Log.v(LOG_TAG,"new song was requested by user");
                stopTrack();
                mArtistTrack = intent.getExtras().getParcelable("Music");
                songEnded=0;
                playTrack(mArtistTrack);
                break;
            case PlaybackService.ACTION_STOP:
                stopTrack();
                songEnded=1;
                break;
        }
        setupHandler();
        initNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    private void setupHandler() {
        handler.removeCallbacks(seekbarupdate); // stop thread
        handler.postDelayed(seekbarupdate, 1000); // 1 second
    }
    private Runnable seekbarupdate = new Runnable(){
        public void run(){
            Log.v(LOG_TAG,"inside run");
            sendtrackPosition();

            handler.postDelayed(this,1000); // 1 second
        }
    };

    private void sendtrackPosition(){
        if(mediaPlayer.isPlaying()){
            trackPosition=mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            Intent seekIntent = new Intent(ACTION_SEEKBAR);
            seekIntent.putExtra("currentTrack",mArtistTrack);
            seekIntent.putExtra("counter",trackPosition);
            seekIntent.putExtra("duration",duration);
            seekIntent.putExtra("song_ended",songEnded);
            sendBroadcast(seekIntent);

        }
    }
    private void updateSeekbarFromUser(Intent seekbarIntent){
        int seekprogress = seekbarIntent.getExtras().getInt("Progress");
        if(mediaPlayer.isPlaying()){
            handler.removeCallbacks(seekbarupdate);
            mediaPlayer.seekTo(seekprogress);
            setupHandler();
        }
    }

    private void resumeTrack(){
        if(mediaPlayer != null){
            startMusic();
            Intent intent = new Intent(ACTION_NOW_PLAYING);
            sendBroadcast(intent);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        handler.removeCallbacks(seekbarupdate);
        cancelNotification();
    }

    private void pauseTrack(){

        if(mediaPlayer!=null){
            Log.v(LOG_TAG, "pausing the track");
            mediaPlayer.pause();
            Intent intent = new Intent(ACTION_PASUE_SUCESS);
            sendBroadcast(intent);


        }
    }
    private void stopTrack(){
        if(mediaPlayer == null){
            return;
        }
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        mediaPlayer.setOnPreparedListener(null);
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;

    }

    private void playTrack(ArtistTrack track){
        stopTrack();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        try{
            mediaPlayer.setDataSource(track.getPreviewURL());
            mediaPlayer.prepareAsync();
        }catch (IOException e){
           e.printStackTrace();
        }
    }

    private void startMusic(){
        if(mediaPlayer != null){
            mediaPlayer.start();
        }
    }
    public void setSongEnded(int i){
        songEnded=i;
    }

    private void initNotification(){

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = android.R.drawable.ic_media_play;
        CharSequence musicInfo = mArtistName + " - "+ mArtistTrack.getTrack();
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, musicInfo, when);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        Context context = getApplicationContext();
        CharSequence contentTitle = "Spotify Streamer";
        CharSequence contentText = "Listen To Music While Performing Other Tasks";

        Intent notificationIntent = new Intent(this, MusicPlaybackActivityFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
    // Cancel Notification
    private void cancelNotification() {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




    /** call when the MediaPlayer is ready*/
    @Override
    public void onPrepared(MediaPlayer mp) {

        int length = mp.getDuration();
        Intent intent = new Intent(ACTION_SET_DURATION);
        intent.putExtra("Duration",length);
        sendBroadcast(intent);
        startMusic();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v(LOG_TAG,"Error loading music !!!!");
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // next track ?
        int duration = mediaPlayer.getDuration();
        Intent seekIntent = new Intent(ACTION_MUSIC_COMPLETE);
        seekIntent.putExtra("counter",trackPosition);
        seekIntent.putExtra("duration",duration);
        songEnded=1;
        seekIntent.putExtra("song_ended", songEnded);
        sendBroadcast(seekIntent);
    }
}
