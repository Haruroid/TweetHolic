package com.haruroid.tweetholic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.haruroid.tweetholic.recycler.images.ImagesAdapter;
import com.haruroid.tweetholic.recycler.images.ImagesDataClass;
import com.haruroid.tweetholic.twitter.Profile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private static final int REQUEST_LOGIN = 10;

    private Handler handler;

    private RecyclerView rc_images;
    private List<ImagesDataClass> imagelist;
    private ImagesAdapter imgadapter;
    private ImageButton btn_addimage;
    private Button btn_post;
    private EditText tx_tweet;

    private SharedPreferences profile;
    private Gson gson;

    private TwitterFactory factory;
    private Twitter twitter;
    private Profile user_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(getMainLooper());

        rc_images = findViewById(R.id.rec_images);
        imagelist = new ArrayList<>();
        imgadapter = new ImagesAdapter(imagelist);
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        rc_images.setHasFixedSize(true);
        rc_images.setLayoutManager(llm);
        ((SimpleItemAnimator) rc_images.getItemAnimator()).setSupportsChangeAnimations(false);
        rc_images.setAdapter(imgadapter);

        btn_addimage = findViewById(R.id.btn_addimage);
        btn_addimage.setOnClickListener(addImages);

        btn_post = findViewById(R.id.btn_post);
        btn_post.setOnClickListener(post);

        tx_tweet = findViewById(R.id.tx_tweet);

        profile = getSharedPreferences("Profile", Context.MODE_PRIVATE);
        String profile_json = profile.getString("profile", "{}");
        gson = new Gson();
        user_profile = gson.fromJson(profile_json, Profile.class);
        factory = new TwitterFactory();
        if (user_profile.accessToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivityForResult(intent, REQUEST_LOGIN);
        } else {
            twitter = factory.getInstance();
            twitter.setOAuthConsumer(user_profile.oauth_consumer_key, user_profile.oauth_consumer_secret);
            twitter.setOAuthAccessToken(user_profile.accessToken);
        }

    }

    View.OnClickListener addImages = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(imgadapter.getItemCount() <= 3) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        }
    };

    View.OnClickListener post = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (twitter != null && !tx_tweet.getText().toString().equals("")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StatusUpdate update = new StatusUpdate(tx_tweet.getText().toString());
                            if(imgadapter.getItemCount() > 0){
                                ArrayList<Long> ids = new ArrayList<>();
                                for(ImagesDataClass data : imagelist) {
                                    try {
                                        InputStream inputStream = getContentResolver().openInputStream(data.getImageuri());
                                        ids.add(twitter.uploadMedia(Integer.toString(ids.size()),inputStream).getMediaId());
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                                long idsl[] = new long[ids.size()];
                                int i = 0;
                                for(Long id: ids){
                                    idsl[i++] = id;
                                }
                                update.setMediaIds(idsl);
                            }

                            twitter.updateStatus(update);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tx_tweet.setText("");
                                    imagelist.clear();
                                    imgadapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this,R.string.tweeted,Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (final TwitterException e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,R.string.failed_to_tweet + "\n" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    ImagesDataClass imagesDataClass = new ImagesDataClass();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imagesDataClass.setThumb(Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.25), (int) (bitmap.getHeight() * 0.25), true));
                    imagesDataClass.setImageuri(uri);
                    imagelist.add(imagesDataClass);
                    imgadapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                profile.edit().putString("profile", resultData.getStringExtra("prof_json")).apply();
                user_profile = gson.fromJson(resultData.getExtras().getString("prof_json"), Profile.class);
                twitter = factory.getInstance();
                twitter.setOAuthConsumer(user_profile.oauth_consumer_key, user_profile.oauth_consumer_secret);
                twitter.setOAuthAccessToken(user_profile.accessToken);
            }

        }
    }
}
