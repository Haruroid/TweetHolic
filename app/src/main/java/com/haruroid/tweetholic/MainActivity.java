package com.haruroid.tweetholic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.haruroid.tweetholic.recycler.images.ImagesAdapter;
import com.haruroid.tweetholic.recycler.images.ImagesDataClass;
import com.haruroid.tweetholic.twitter.Profile;

import java.util.ArrayList;
import java.util.List;

import twitter4j.auth.AccessToken;

public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;

    private RecyclerView rc_images;
    private List<ImagesDataClass> imagelist;
    private ImagesAdapter imgadapter;
    private ImageButton btn_addimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rc_images = findViewById(R.id.rec_images);
        imagelist = new ArrayList<>();
        imgadapter = new ImagesAdapter(imagelist);
        LinearLayoutManager llm = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);

        rc_images.setHasFixedSize(true);
        rc_images.setLayoutManager(llm);
        ((SimpleItemAnimator) rc_images.getItemAnimator()).setSupportsChangeAnimations(false);
        rc_images.setAdapter(imgadapter);

        btn_addimage = findViewById(R.id.btn_addimage);
        btn_addimage.setOnClickListener(addImages);

        SharedPreferences profile = getSharedPreferences("Profile", Context.MODE_PRIVATE);
        String profile_json = profile.getString("Tokens","");
        if(profile_json.equals("")){
            //ログイン
        }
        Gson gson = new Gson();
        Profile user_profile = gson.fromJson(profile_json,Profile.class);
        if(user_profile.accessToken == null) {
            //ログイン
        }
    }

    View.OnClickListener addImages = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            intent.setType("image/*");
            startActivityForResult(intent, READ_REQUEST_CODE);

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode,resultCode,resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    ImagesDataClass imagesDataClass = new ImagesDataClass();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imagesDataClass.setThumb(Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth() * 0.25),(int)(bitmap.getHeight() * 0.25),true));
                    imagesDataClass.setImageuri(uri);
                    imagelist.add(imagesDataClass);
                    imgadapter.notifyDataSetChanged();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
