package com.example.nostack.views.organizer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;
import com.example.nostack.services.QrCodeImageGenerator;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.QrCodeViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Creates the fragment to display the QR code for the event and allow for exporting of the QR code
 */
public class OrganizerQRCode extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Event event;
    private QrCode qrCode;
    private String qrCodeText;
    private Bitmap bmp;
    private String mParam2;
    private final Integer QR_CODE_DIMENSION = 500;
    private QrCodeViewModel qrCodeViewModel;
    private EventViewModel eventViewModel;
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

    /**
     * This method is called when the fragment is being created and checks
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qrCodeViewModel = new ViewModelProvider(requireActivity()).get(QrCodeViewModel.class);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
    }

    /**
     * This method is called when the fragment needs to create its view and it will
     * generate a QR code for the event
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Returns the view created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_organizer_q_r_code, container, false);

        // Watch for errors
        qrCodeViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                qrCodeViewModel.clearErrorLiveData();
            }
        });

//        eventViewModel.getEvent().observe(getViewLifecycleOwner(), e -> {
//            if (e != null) {
//                event = e;
//                qrCodeViewModel.fetchQrCode(e.getCheckInQrId());
//            }
//        });

        qrCodeViewModel.getQrCode().observe(getViewLifecycleOwner(), qr -> {
            if (qr != null) {
                qrCode = qr;
                qrCodeText = "0" + "." + qrCode.getId();
                bmp = QrCodeImageGenerator.generateQrCodeImage(qrCodeText);
                ImageView qrCodeImageView = view.findViewById(R.id.OrganizerQRImage);
                qrCodeImageView.setImageBitmap(bmp);
            }
        });

        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerQRCode.this).popBackStack();
            }
        });

        view.findViewById(R.id.OrganizerEventPageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(OrganizerQRCode.this).navigate(R.id.action_organizerQRCode_to_organizerEventPageQRCode);
            }
        });

        view.findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = getImageUri(bmp);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/png");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        });

        return view;
    }

    public Uri getImageUri(Bitmap bitmap) {
        File cachePath = new File(getContext().getCacheDir(), "images");
        cachePath.mkdirs();
        try (FileOutputStream stream = new FileOutputStream(cachePath + "/image.png")) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(getContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        return FileProvider.getUriForFile(getContext(), "com.example.nostack.provider", newFile);
    }

    public void shareImageUri(Uri imageUri, Context context) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
}