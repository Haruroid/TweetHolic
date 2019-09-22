package com.haruroid.tweetholic.recycler.images;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.haruroid.tweetholic.R;

public class ImagesViewHolder extends RecyclerView.ViewHolder {

    public Button del;
    public ImageView thumb;

    public ImagesViewHolder(View itemView){
        super(itemView);
        del = itemView.findViewById(R.id.btn_del);
        thumb = itemView.findViewById(R.id.img);
    }
}
