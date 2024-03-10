package com.example.nostack;

import static com.example.nostack.utils.GenerateProfileImage.generateProfileImage;
import static com.google.common.base.CharMatcher.any;
import static com.google.common.base.Verify.verify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.nostack.utils.GenerateProfileImage;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void testGenerateProfileImage() {
        // Input data
        String firstName = "John";
        String lastName = "Doe";

        GenerateProfileImage generateProfileImage = new GenerateProfileImage();

        // Call the method to test
        Bitmap resultBitmap = generateProfileImage.generateProfileImage(firstName, lastName);

        // Assert that the method returns a non-null Bitmap
        assertNotNull(resultBitmap);

    }

}
