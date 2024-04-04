package com.example.nostack.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.models.Image;
import com.example.nostack.viewmodels.EventViewModel;
import com.example.nostack.viewmodels.ImageViewModel;
import com.example.nostack.views.attendee.AttendeeBrowse;
import com.example.nostack.views.event.adapters.EventArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Creates the AttendeeBrowse fragment which is used to display the events that the user can attend
 */
public class AdminBrowseImages extends Fragment {
    private ImageArrayAdapter imageArrayAdapter;
    private ListView imageList;
    private ArrayList<Image> dataList;
    private ImageViewModel imageViewModel;

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
        imageList = rootView.findViewById(R.id.admin_viewPager2);
        imageArrayAdapter = new ImageArrayAdapter(getContext(), dataList, this);
        imageList.setAdapter(imageArrayAdapter);
        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
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
            imageArrayAdapter.clear();
            for (Image image : images) {
                imageArrayAdapter.addImage(image);
            }
            imageArrayAdapter.notifyDataSetChanged();
        });

    }
}
