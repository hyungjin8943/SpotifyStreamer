package com.example.ralla.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ralla on 6/29/15.
 */
public class MusicArtist implements Parcelable {
    String name;
    String spotifyID;
    String thumbnail;
    public MusicArtist(){

    }
    public MusicArtist(String name, String spotifyID, String thumbnail){
        this.name = name;
        this.spotifyID = spotifyID;
        this.thumbnail = thumbnail;
    }

    public void setName(String name){
        this.name= name;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    public void setSpotifyID(String spotifyID){
        this.spotifyID = spotifyID;

    }public void setThumbnail(String thumbnail){
        this.thumbnail=thumbnail
        ;
    }
    public boolean existThumbnail(){
        if (this.thumbnail==null){
            return false;
        }
        return true;
    }
    public String getName(){
        return this.name;
    }
    public String getSpotifyID(){
        return this.spotifyID;
    }
    public String getThumbnail(){
        return this.thumbnail;
    }

    public MusicArtist(Parcel in){
        readFromParcel(in);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MusicArtist createFromParcel(Parcel in ) {
            return new MusicArtist( in );
        }

        public MusicArtist[] newArray(int size) {
            return new MusicArtist[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(spotifyID);
        dest.writeString(thumbnail);
    }
    private void readFromParcel(Parcel in){
        name = in.readString();
        spotifyID = in.readString();
        thumbnail= in.readString();
    }
}
