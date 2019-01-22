package com.example.umara.morsecode;
import android.view.animation.AlphaAnimation;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.HashMap;
public class MorseCode {


    static String[] ALPHA = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
            "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    static String[] MORSE = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..",
            "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
            "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----"};

    public static HashMap<String, String> ALPHA_TO_MORSE = new HashMap<>(); // used to create a "key" for Alphabet char to Morse Char
    public static HashMap<String, String> MORSE_TO_ALPHA = new HashMap<>(); // used to create a "key" for Morse char to Alpha Char

    static {
        for(int i = 0; i < ALPHA.length && i < MORSE.length; i++){
            ALPHA_TO_MORSE.put(ALPHA[i], MORSE[i]); // iterate through String[] and assign appropriate keys to values
            MORSE_TO_ALPHA.put(MORSE[i], ALPHA[i]);
        }
    }

    public static String morsetoAlpha(String morseCode){
        StringBuilder builder = new StringBuilder();
        String[] words = morseCode.trim().split("   "); // splits inputted morseCode into words

        for(String word: words){
            for(String letter: word.split(" ")){
                String alpha = MORSE_TO_ALPHA.get(letter); // retrieve alphabet letter - using key
                builder.append(alpha); // increment to builder
            }
            builder.append(" ");
        }
        return builder.toString().toUpperCase(); // return converted morseCode
    }


    public static String[] alphaToMorse(String englishCode) { // split inputted text into words
        StringBuilder builder = new StringBuilder();
        String[] words = englishCode.trim().split(" ");

        return words; // return words
    }


}
