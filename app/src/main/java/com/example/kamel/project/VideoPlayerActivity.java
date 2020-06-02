package com.example.kamel.project;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView videoView ;
    String urlVideoIntent ;
    ProgressBar progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView) ;
        progressBar = findViewById(R.id.progressBar) ;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) ;
        getSupportActionBar().hide();

        playerVideo() ;
    }

    public void AlertShow(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cannot play this video")
                .setCancelable(false)
                .setPositiveButton("Ya"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        }) ;


        AlertDialog alertDialog = builder.create() ;
        alertDialog.show();
    }

    void playerVideo() {

        try {
            MediaController mediaController = new MediaController(this) ;
            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);


            urlVideoIntent = getIntent().getExtras().getString("url_video","");
            Uri videoUri = Uri.parse(urlVideoIntent) ;


            videoView.setVideoURI(videoUri);
            videoView.requestFocus() ;

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                    progressBar.setVisibility(View.GONE);
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();

                }


            });
        }catch (Exception e){
            AlertShow() ;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoView.stopPlayback();
    }
}
