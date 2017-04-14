package com.example.ralla.spotifystreamer;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ralla on 8/17/15.
 */
public class Utility {
    public static String millisecondsToMMSS(long milliseconds) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        );
    }

}
