package com.example.ralla.spotifystreamer;

/**
 * Created by Ralla on 7/6/15.
 */
public class ArtistTrack {
    String soptifyTrackID;
    String track;
    String album;
    String albumThumbnail;
    String previewURL;

    public ArtistTrack(){

    }
    public ArtistTrack(String soptifyID, String track, String album, String albumThumbnail, String previewURL){
        this.soptifyTrackID = soptifyID;
        this.track = track;
        this.album = album;
        this.albumThumbnail = albumThumbnail;
        this.previewURL = previewURL;


    }

    public String getAlbum() {
        return album;
    }

    public String getSoptifyID() {
        return soptifyTrackID;
    }

    public String getAlbumThumbnail() {
        return albumThumbnail;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public String getTrack() {
        return track;
    }

    public void setAlbum(String album) {
        this.album = album;

    }

    public void setAlbumThumbnail(String albumThumbnail) {
        this.albumThumbnail = albumThumbnail;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public void setSoptifyID(String soptifyID) {
        this.soptifyTrackID = soptifyID;
    }

    public void setTrack(String track) {
        this.track = track;
    }


}
