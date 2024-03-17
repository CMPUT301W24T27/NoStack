package com.example.nostack.services;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Generate a random name for the user
 * Uses the format of Adjective + Noun + Tag
 * Run on a separate thread to prevent blocking the main thread
 */
public class GenerateName {
    private static final String ADJECTIVES_URL = "https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-adjectives.txt";
    private static final String NOUNS_URL = "https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-nouns.txt";
    public GenerateName() {
    }

    /**
     * Listener to callback for the main thread
     */
    public interface GenerateNameListener {
        void onNameGenerated(String[] name, Exception e);
    }

    /**
     * Get a random adjective from the list
     * @return Returns a random adjective
     * @throws IOException
     */
    public static String getAdjective() throws IOException {
        ArrayList adjectives = new ArrayList();
        URL req = new URL(ADJECTIVES_URL);
        Scanner scanner = new Scanner(req.openStream());

        while (scanner.hasNext()) {
            adjectives.add(scanner.next());
        }

        scanner.close();

        // Randomly pick an adjective
        return (String) adjectives.get((int)(Math.random() * (double)adjectives.size()));
    }

    /**
     * Get a random noun from the list
     * @return Returns a random noun
     * @throws IOException
     */
    public static String getNoun() throws IOException {
        ArrayList nouns = new ArrayList();
        URL req = new URL(NOUNS_URL);
        Scanner scanner = new Scanner(req.openStream());

        while (scanner.hasNext()) {
            nouns.add(scanner.next());
        }

        scanner.close();

        // Randomly pick a noun
        return (String) nouns.get((int)(Math.random() * (double)nouns.size()));
    }

    /**
     * Generate a random name for the user
     * @return name[] A string array containing the generated name
     * @throws IOException
     */
    public static String[] generateName() throws IOException {
        String[] name = new String[3];
        String tag = Integer.toString((int)(Math.random() * 10000.0D));

        name[0] = getAdjective();
        name[1] = getNoun();
        name[2] = tag;

        // Capitalize the first letter of first and last name
        name[0] = name[0].substring(0, 1).toUpperCase() + name[0].substring(1);
        name[1] = name[1].substring(0, 1).toUpperCase() + name[1].substring(1);

        return name;
    }

    /**
     * Generate a random name for the user
     * @param listener The listener to callback for the main thread'
     * @return void
     */
    public static void generateNameAsync(final GenerateNameListener listener) {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] name = generateName();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onNameGenerated(name, null);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onNameGenerated(null, e);
                        }
                    });
                }

            }
        }).start();
    }
}
