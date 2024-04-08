package com.example.nostack.views.admin.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nostack.R;
import com.example.nostack.models.Image;
import java.util.ArrayList;

public class ImageRecycleViewAdapter extends RecyclerView.Adapter<ImageViewHolder>{
    private ArrayList<Image> images;
    private Fragment currFragment;
    private Context context;
    private ImageArrayRecycleViewInterface imageArrayRecycleViewInterface;

    public ImageRecycleViewAdapter(Context context, ArrayList<Image> images, Fragment currfragment, ImageArrayRecycleViewInterface imageArrayRecycleViewInterface) {
        this.images = images;
        this.context = context;
        this.currFragment = currfragment;
        this.imageArrayRecycleViewInterface = imageArrayRecycleViewInterface;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.imagelistcontent, parent, false);
        return new ImageViewHolder(itemView, imageArrayRecycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Double size = (double) images.get(position).getSize() / (1024);
        String formattedSize = String.format("%.2f", (size > 1024 ? size/1024 : size)) + (size > 1024 ? " MB" : " KB");

        holder.imageName.setText(images.get(position).getId());
        holder.imageDate.setText(images.get(position).getCreated());
        holder.imageSize.setText(formattedSize);
        holder.imageType.setText(images.get(position).getType());

        images.get(position).getImage(context).addOnSuccessListener(drawable -> {
            holder.imageIcon.setImageDrawable(drawable);
        }).addOnFailureListener(e -> {
            Log.d("ImageRecycleViewAdapter", "Failed to get image: " + e.getMessage());
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void clear() {
        images.clear();
        notifyDataSetChanged();
    }

    public void addImage(Image image) {
        Log.d("ImageRecycleViewAdapter", "Adding image: " + image.getId());
        images.add(image);
        notifyDataSetChanged();
    }

    public void remove(Image image) {
        images.remove(image);
        notifyDataSetChanged();
    }
}
