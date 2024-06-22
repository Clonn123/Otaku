package org.hse.otaku;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;


import static org.hse.otaku.AnimeViewActivity.EXTRA_ANIMEID;
import static org.hse.otaku.AnimeViewActivity.EXTRA_DESCRIPTION;
import static org.hse.otaku.AnimeViewActivity.EXTRA_SCORE;
import static org.hse.otaku.AnimeViewActivity.EXTRA_TITLE;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        final int ani_id = intent.getIntExtra(EXTRA_ANIMEID, 0);
        System.out.println(ani_id);


        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setContentView(R.layout.activity_detail);
        //inal Intent intent = getIntent();
        InputStream ims = null;
        AdView mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        try {
            ims = getAssets().open("covers/" + intent.getIntExtra(EXTRA_ANIMEID, 0)+".jpg");

        } catch (IOException e) {
            e.printStackTrace();
        }
        final Drawable d = Drawable.createFromStream(ims, null);

        final String title = intent.getStringExtra(EXTRA_TITLE);
        String score = intent.getStringExtra(EXTRA_SCORE);
        final String description = intent.getStringExtra(EXTRA_DESCRIPTION);


        ImageView imageView = findViewById(R.id.image_view_detail);
        final TextView textTitle = findViewById(R.id.text_view_title_detail);
        TextView textScore = findViewById(R.id.text_view_score);
        TextView textDescription = findViewById(R.id.text_view_description);
        imageView.setImageDrawable(d);
        textTitle.setText(title);
        final String mScore = score;
        textScore.setText("Оценка: " + score);
        textDescription.setText(description);
        final Button button = (Button) findViewById(R.id.button_set_watching);
        final DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
        boolean CheckIfInDB = mDatabaseHelper.checkAnimeForUser(Integer.parseInt(String.valueOf(ani_id)));
        //System.out.println(AnimeViewNameDesc.ani_id);





        if (!CheckIfInDB){
            button.setText("Установить не смотрю");
            button.setBackgroundColor(Color.WHITE);
            button.setTextColor(Color.BLACK);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    button.setText("Смотрю");
                    button.setBackgroundColor(Color.parseColor("#FFFFBB33"));
                    button.setTextColor(Color.WHITE);
                    boolean deleteAnime = mDatabaseHelper.deleteFromWatching(Integer.parseInt(String.valueOf(ani_id)));
                    if (deleteAnime){
                        Toast.makeText(getApplicationContext(), "Аниме удалено из просмотра", Toast.LENGTH_SHORT).show();
                        finish();
                        recreate();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Что-то пошло не так...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    button.setText("Set not watching");
                    button.setBackgroundColor(Color.WHITE);
                    button.setTextColor(Color.BLACK);

                    boolean Checkifindb = mDatabaseHelper.checkAnimeForUser(Integer.parseInt(String.valueOf(ani_id)));
                    if (Checkifindb) {
                        assert mScore != null;
                        boolean insertAnime = mDatabaseHelper.addDataAnime(Integer.parseInt(String.valueOf(ani_id)), userID, title, Integer.parseInt(mScore), description);
                        if (insertAnime) {
                            Toast.makeText(getApplicationContext(), "Аниме добавлено в лист просмотра", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), "Что-то пошло не так...", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Аниме уже добавлено лист просмотра!", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}