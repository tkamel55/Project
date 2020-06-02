package com.example.kamel.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamel.project.Model.Global;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoDescriptionActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST = 1;
    String urlVideoIntent;
    String urlPhotoIntent;
    String titleIntent;
    String descIntent;
    String dateIntent;
    String durationIntent;
    String idIntent;
    String idCategoryIntent;

    TextView tvTitle;
    TextView tvDate;
    TextView tvDesc;
    TextView tvDuration;
    ImageView ivThumb;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_description);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Video Description");
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleIntent = getIntent().getExtras().getString("title", "");
        dateIntent = getIntent().getExtras().getString("date", "");
        descIntent = getIntent().getExtras().getString("desc", "");
        durationIntent = getIntent().getExtras().getString("duration", "");
        urlPhotoIntent = getIntent().getExtras().getString("url_photo", "");
        urlVideoIntent = getIntent().getExtras().getString("url_video", "");
        idIntent = getIntent().getExtras().getString("id", "");
        idCategoryIntent = getIntent().getExtras().getString("id_category", "");

        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvDesc = findViewById(R.id.tvDescription);
        ivThumb = findViewById(R.id.ivThumbnail);
        tvDuration = findViewById(R.id.tvDuration);

        tvDuration.setText("Duration : " + formatDuration(Long.parseLong(durationIntent)));

        tvTitle.setText(titleIntent);

        final String image = urlPhotoIntent;
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black_24dp).networkPolicy(NetworkPolicy.OFFLINE).into(ivThumb, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(image).into(ivThumb);
                }
            });
        } catch (Exception e) {

        }

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(dateIntent));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        tvDate.setText(Global.UploadDate + dateTime);

        tvDesc.setText(descIntent);

        Button btnPlayVideo = findViewById(R.id.btnPlayVideo);
        btnPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                intent.putExtra("url_video", urlVideoIntent);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(VideoDescriptionActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }

    private String formatDuration(long timeInMillisec) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec))
        );
    }
}
