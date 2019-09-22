package com.haruroid.tweetholic.recycler.images;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.haruroid.tweetholic.R;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesViewHolder> {
    List<ImagesDataClass> imagesDataClassList;

    public ImagesAdapter(List<ImagesDataClass> list) {
        imagesDataClassList = list;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_images, parent, false);
        ImagesViewHolder vh = new ImagesViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder vh, final int pos) {
        vh.thumb.setImageBitmap(imagesDataClassList.get(pos).getThumb());
        vh.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    imagesDataClassList.remove(pos);
                    ImagesAdapter.this.notifyItemRemoved(pos);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesDataClassList.size();
    }

}
