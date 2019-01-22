package com.example.umara.morsecode;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 23; // Code for camera use request call

    // Variables defined for delaying the output of morseCode depending on the morse letter
    private static int singleTimeUnit = 240;
    private static int dotDuration = singleTimeUnit;
    private static int dashDuration = singleTimeUnit * 3;
    private static int gapInCharacter = singleTimeUnit;
    private static int gapInLetters = singleTimeUnit * 3;
    private static int GapInWords = singleTimeUnit * 7;

    private TextView txt;       // field created for user input
    private TextView result;    // field created for user output
    private Button toMorsebtn;  // Alphabet to Morse
    private Button toAlphabtn;  // Morse to Alphabet

    public static StringBuilder builder; // Holds the incremented converted string used to print to display
    public static boolean state; // Current state of flash On/Off
    public static String mVal;   // Holds current morseValue so that program can delay/flash accordingly

    Flashlight Flash;
    Thread newThread;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Buttons and Fields
        txt = (TextView) findViewById(R.id.txt);
        result = (TextView) findViewById(R.id.result);
        toAlphabtn = (Button) findViewById(R.id.toAlphaBtn);
        toMorsebtn = (Button) findViewById(R.id.toMorseBtn);


        checkCameraPermission(); // Asks/Verifies user for permission to use camera

        // adding listeners to buttons
        toAlphabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MorseToConvert = txt.getText().toString();
                String convertedText = MorseCode.morsetoAlpha(MorseToConvert); // method from MorseCode Class - Converts morse char to alphabet char
                result.setText(convertedText); // set to result display
            }
        });

        toMorsebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMorsebtn.setEnabled(false); // disable buttons when converting
                toAlphabtn.setEnabled(false);
                newThread = new Thread(morse); // new thread created to handle logic
                newThread.start();
            }
            Runnable morse = new Runnable() {

                private void textConversion() {

                    builder = new StringBuilder();
                    String txtToConvert = txt.getText().toString(); // assign user inputted text to variable
                    String[] words = MorseCode.alphaToMorse(txtToConvert); // calls method in MorseCode class - splits txtToConvert into words

                    for (String word : words) { // iterate through each word to start conversion
                        for (int i = 0; i < word.length(); i++) { // iterate through each char in word
                            String morse = MorseCode.ALPHA_TO_MORSE.get(word.substring(i, i + 1).toLowerCase()); // convert letter into string of morse char

                            for (int g = 0; g < morse.length(); g++) { // iterate through the morse char string to delay/flash correctly
                                mVal = String.valueOf(morse.charAt(g)); // holds current morse char
                                if (mVal.equals(".")) {
                                    state = true; // if state is true camera flash is ON vice versa
                                    builder.append(mVal); // increment current morse char to builder - (will hold entire converted text after conversion)
                                    MorseLight(dotDuration); // passes delay variable to executeMorseLights method
                                } else if (mVal.equals("-")) {
                                    state = true;
                                    builder.append(mVal);
                                    MorseLight(dashDuration);
                                }
                                state = false;
                                MorseLight(gapInCharacter); // after every morse char in one letter
                            }
                            builder.append(" ");
                            MorseLight(gapInLetters); // once a letter is done converting
                        }
                        builder.append("  ");
                        MorseLight(GapInWords); // once a word is done converting
                    }
                }

                private void MorseLight(int duration) {
                    try {
                        runOnUiThread(new Runnable() {      // new thread so display can be updated as conversion is taking place
                            @Override
                            public void run() {
                                result = (TextView) findViewById(R.id.result);
                                result.setText(builder.toString().toUpperCase());
                                Flash.flashToggle(state); // calls and passes state to a method in flash class that handles the flashlight
                            }
                        });

                        Thread.sleep(duration); // Stops the thread so that converted morse code is displayed as it would be in real-time

                    } catch (InterruptedException e) {
                        Log.e("Err", "Failed to ExecuteLights" + e.getMessage());
                    }
                }
                @Override
                public void run() {
                    textConversion(); // starts conversion

                    //Run on UI thread rather than background thread because it is updating the display
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            result = (TextView) findViewById(R.id.result); // clears display before next conversion
                            result.setText("");
                            toMorsebtn.setEnabled(true); // enable buttons
                            toAlphabtn.setEnabled(true);
                        }
                    });
                }
            };
        });

    }


    public void checkCameraPermission() { // checks if permission is granted

        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) { // check if camera permission is denied

                new AlertDialog.Builder(this) // informs user why permission is required
                        .setTitle("Camera Permission")
                        .setMessage("Flashlight requires this permission")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE); // requests permission
                            }
                        })
                        .create()
                        .show();
            }else{
            cameraPermission(); // permission is already granted call method to initialize camera
        }
        }
            @Override
            public void onRequestPermissionsResult ( int requestCode, String permissions[], //
            int[] grantResults){
                if (requestCode == REQUEST_CODE) // checks if Request Code is for camera permission
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        cameraPermission();
                    }
            }

    private void cameraPermission() {
        Flash = new Flashlight(); // Creates a Flashlight object
        Flash.flashSetup(this); // initializes  camera
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}