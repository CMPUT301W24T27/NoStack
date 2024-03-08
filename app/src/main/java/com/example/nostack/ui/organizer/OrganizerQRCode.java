package com.example.nostack.ui.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.model.Events.Event;
import com.example.nostack.utils.QrCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerQRCode#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerQRCode extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Event event;
    private String mParam2;
    private Integer QR_CODE_DIMENSION = 500;

    public OrganizerQRCode() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrganizerQRCode.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerQRCode newInstance(Event event) {
        OrganizerQRCode fragment = new OrganizerQRCode();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("eventData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_q_r_code, container, false);
//
        QrCode qrCode = event.getCheckInQr();
        String qrCodeText = qrCode.getType() + "." + qrCode.getCode();
        Bitmap bitmap;
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

        ImageView qrCodeImageView = view.findViewById(R.id.OrganizerQRImage);
        qrCodeImageView.setImageBitmap(bmp);

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerQRCode.this).popBackStack();
            }
        });


        return view;
    }
}