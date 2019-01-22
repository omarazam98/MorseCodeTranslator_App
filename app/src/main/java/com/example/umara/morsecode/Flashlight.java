package com.example.umara.morsecode;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.Log;
//import android.widget.Toast;

public class Flashlight {
        private static Camera cam;
        private static Camera.Parameters p;
        private static MediaPlayer morseNoise;

    public void flashSetup(Context ctx){
        cam = android.hardware.Camera.open(); // initialize camera and its parameter
        p = cam.getParameters();
        morseNoise = MediaPlayer.create(ctx , R.raw.morse_beep); // initialize mediaPlayer - retrieve sound clip

    }

    static public void flashToggle(boolean state) { // logic for flashlight and sound

        if (!state) { // nothing is being outputted - pause sound / flashlight
            morseNoise.pause();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(p);

        } else if (state) { // Enable sound and flashlight

            try {
                morseNoise.start();
                p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            } catch (RuntimeException e) {
                Log.e("Err", "Failed to toggle flash" + e.getMessage());

            }
        }
    }

    }

