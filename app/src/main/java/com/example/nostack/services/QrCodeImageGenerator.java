package com.example.nostack.services;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrCodeImageGenerator {
    private final static int QR_CODE_DIMENSION = 500;
    public static Bitmap generateQrCodeImage(String qrCodeText) {
        MultiFormatWriter writer = new MultiFormatWriter();
        Bitmap bmp = null;
        try {
            BitMatrix bitMatrix = writer.encode(qrCodeText, BarcodeFormat.QR_CODE, QR_CODE_DIMENSION, QR_CODE_DIMENSION);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF); // Black and white colors
                }
            }
        } catch (WriterException e) {
            Log.e("QRCodeWriter", "Unable to generate QR code... " + e);
        }
        return bmp;
    }
}
