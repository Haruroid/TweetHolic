package com.haruroid.tweetholic;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.haruroid.tweetholic.twitter.Profile;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class LoginActivity extends AppCompatActivity {
    EditText cs, ck, pin;
    Twitter twitter;
    RequestToken requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView cs_help = new TextView(this), ck_help = new TextView(this);
        cs_help.setText(getText(R.string.consumer_secret));
        ck_help.setText(getText(R.string.consumer_key));
        cs = new EditText(this);
        ck = new EditText(this);
        linearLayout.addView(cs_help);
        linearLayout.addView(cs);
        linearLayout.addView(ck_help);
        linearLayout.addView(ck);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.consumer_setting))
                .setView(linearLayout)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                twitter = TwitterFactory.getSingleton();
                                twitter.setOAuthConsumer(ck.getText().toString(), cs.getText().toString());
                                try {
                                    requestToken = twitter.getOAuthRequestToken();
                                    AccessToken accessToken = null;
                                    Uri uri = Uri.parse(requestToken.getAuthorizationURL());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                })
                .show();

        LinearLayout layout_pin = new LinearLayout(this);
        layout_pin.setOrientation(LinearLayout.HORIZONTAL);
        pin = new EditText(this);
        pin.setHint(R.string.input_pin);
        Button enter = new Button(this);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pin.getText().toString().length() < 0)
                    return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, pin.getText().toString());
                            Profile profile = new Profile();
                            profile.accessToken = accessToken;
                            profile.oauth_consumer_key = ck.getText().toString();
                            profile.oauth_consumer_secret = cs.getText().toString();
                            Gson gson = new Gson();
                            String prof_json = gson.toJson(profile);
                            Intent result = new Intent();
                            Bundle data = new Bundle();
                            data.putString("prof_json", prof_json);
                            result.putExtras(data);
                            setResult(RESULT_OK, result);
                            finish();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        layout_pin.addView(pin);
        layout_pin.addView(enter);
        setContentView(layout_pin);
    }
}
