package com.example.nostack.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GenerateProfileImage {
    public GenerateProfileImage() {
    }

    /**
     * Generate a profile image from the user's first and last name
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @return Returns the generated profile image
     */
    public static Bitmap generateProfileImage(String firstName, String lastName) {
        // Generate a profile image from the user's first and last name
        String initials = firstName.substring(0, 1) + lastName.substring(0, 1);
        int width = 100;
        int height = 100;
        int[] rgb = generateRGB();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30f);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawRGB(rgb[0], rgb[1], rgb[2]);
        canvas.drawText(initials, 32, 60, paint);

        return bitmap;
    }

    /**
     * Generate a random RGB color
     * @return Returns the generated RGB color
     */
    private static int[] generateRGB(){
        int[] rgb = new int[3];
        rgb[0] = (int) (Math.random() * 256);
        rgb[1] = (int) (Math.random() * 256);
        rgb[2] = (int) (Math.random() * 256);
        return rgb;
    }
}
