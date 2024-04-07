package com.example.nostack.views.admin;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.models.Image;
import com.example.nostack.services.SkeletonProvider;
import com.example.nostack.viewmodels.ImageViewModel;
import com.example.nostack.views.admin.adapters.ImageArrayAdapter;
import com.example.nostack.views.admin.adapters.ImageArrayRecycleViewInterface;
import com.example.nostack.views.admin.adapters.ImageRecycleViewAdapter;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.recyclerview.SkeletonRecyclerViewAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the AttendeeBrowse fragment which is used to display the events that the user can attend
 */
public class AdminBrowseImages extends Fragment {
    private ImageArrayAdapter imageArrayAdapter;
    private ImageRecycleViewAdapter imageRecycleViewAdapter;
    private RecyclerView imageList;
    private ArrayList<Image> dataList;
    private ImageViewModel imageViewModel;
    private Skeleton skeleton;

    public AdminBrowseImages() {
    }

    /**
     * This method is called when the fragment is being created and then sets up the variables for the view
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageViewModel = new ViewModelProvider(requireActivity()).get(ImageViewModel.class);
        dataList = new ArrayList<>();
    }

    /**
     * This method is called when the fragment is being created and then sets up the view for the fragment
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Returns the modified view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_home_browseimages, container, false);
        imageList = rootView.findViewById(R.id.admin_imageView);
        imageRecycleViewAdapter = new ImageRecycleViewAdapter(getContext(), dataList, this, new ImageArrayRecycleViewInterface() {
            @Override
            public void onImageClick(int position) {
                showDialog(dataList.get(position));
            }
        });
        imageList.setAdapter(imageRecycleViewAdapter);
        imageList.setLayoutManager(new LinearLayoutManager(getContext()));
        skeleton = SkeletonProvider.getSingleton().adminImageSkeleton(imageList);
        skeleton.showSkeleton();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Watch for errors
        imageViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                imageViewModel.clearErrorLiveData();
            }
        });

        // Fetch and get Images
        imageViewModel.fetchAllImages();
        imageViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            imageRecycleViewAdapter.clear();
            for (Image image : images) {
                imageRecycleViewAdapter.addImage(image);
            }
            imageRecycleViewAdapter.notifyDataSetChanged();
            skeleton.showOriginal();
        });
    }

    /**
     * Show the dialog for the image
     * @param image
     * @return void
     */
    private void showDialog(Image image){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_admin_images_dialog);
        dialog.show();

        ImageView imageBanner = dialog.findViewById(R.id.admin_imageDialog);
        TextView imageTitle = dialog.findViewById(R.id.admin_imageDialogTitle);
        TextView imageSize = dialog.findViewById(R.id.admin_imageDialogSize);
        TextView imageCreated = dialog.findViewById(R.id.admin_imageDialogCreated);
        TextView imageType = dialog.findViewById(R.id.admin_imageDialogType);

        imageTitle.setText(image.getId());
        imageSize.setText(image.getSize() + "bytes");
        imageType.setText(image.getType());
        imageCreated.setText((image.getCreated()));

        String uri = image.getUrl();

        if (uri != null) {
            // Get image from firebase storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 250, false);
                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getActivity().getResources(), scaledBmp);
                d.setCornerRadius(50f);
                imageBanner.setImageDrawable(d);
            }).addOnFailureListener(exception -> {
                Log.w("Image Profile", "Error getting Image banner", exception);
            });
        }

        dialog.findViewById(R.id.admin_deleteImageButton).setOnClickListener(v -> {
            deleteImage(image);
            dialog.dismiss();
        });
    }

    /**
     * Delete an image
     * @param image
     * @return void
     */
    private void deleteImage(Image image) {
        imageViewModel.deleteImage(image, new ImageViewModel.DeleteImageCallback() {
            @Override
            public void onImageDeleted() {
                Toast.makeText(getContext(), "Image deleted.", Toast.LENGTH_SHORT).show();
                imageRecycleViewAdapter.remove(image);
                imageRecycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onImageDeleteFailed() {
                Toast.makeText(getContext(), "Image failed to delete.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
