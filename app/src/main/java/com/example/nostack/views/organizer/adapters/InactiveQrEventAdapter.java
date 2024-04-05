package com.example.nostack.views.organizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nostack.R;
import com.example.nostack.models.Event;
import com.example.nostack.models.QrCode;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

public class InactiveQrEventAdapter extends ArrayAdapter<Pair<Event, QrCode>> {
    private Context context;
    private ArrayList<Pair<Event, QrCode>> qrEventList;

    public InactiveQrEventAdapter(Context context, ArrayList<Pair<Event, QrCode>> list) {
        super(context, 0, list);
        this.context = context;
        this.qrEventList = list;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.reuse_qr_list_content, parent, false);

        Pair<Event, QrCode> currentPair = qrEventList.get(position);

        TextView eventName = listItem.findViewById(R.id.reuse_qr_event_name);
        eventName.setText(currentPair.first.getName());

        TextView qrCodeId = listItem.findViewById(R.id.reuse_qr_code_id);
        qrCodeId.setText(currentPair.second.getId());

        return listItem;
    }

    public void addQrCode(Pair<Event, QrCode> pair) {add(pair);}
}