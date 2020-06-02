package com.example.kamel.project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AddVideoActivity extends AppCompatActivity {

    ImageView ivThumb ;
    TextView tvDuration ;

    StorageReference storageReference ;
    String myPhotoUrl = "" ;
    String myVideoUrl = "" ;
    StorageTask uploadTask , uploadVideoTask ;

    Button btnSetPicture , btnAddVideo , btnAdd ;

    private static final int MY_PERMISSIONS_REQUEST = 1 ;
    private static final int OTHER_REQ = 2 ;

    Uri imageUri , videoUri ;
    String idPos , duration ;
    MaterialEditText edtTitle , edtDesc ;
    String idIntent ;

    Bitmap imageBitmap ;
    int TAG ;

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
        setContentView(R.layout.activity_add_video);

        ActionBar actionBar = getSupportActionBar() ;
        actionBar.setTitle("Add Video") ;
      //  this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idIntent = getIntent().getExtras().getString("id","");

        ivThumb = findViewById(R.id.ivThumbnail) ;
        edtTitle = findViewById(R.id.tvTitle) ;
        edtDesc = findViewById(R.id.tvDescription) ;
        tvDuration = findViewById(R.id.tvDuration) ;

        btnSetPicture = findViewById(R.id.btnEditThumbnail) ;
        btnSetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(AddVideoActivity.this);
            }
        });

        btnAddVideo = findViewById(R.id.btnAddVideo) ;
        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddVideoActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddVideoActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(AddVideoActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST);

                    }else {
                        ActivityCompat.requestPermissions(AddVideoActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST);

                    }
                }else {
                    addVideo() ;
                }

            }
        });

        btnAdd = findViewById(R.id.btnAdd) ;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edtTitle.getText().toString() ;
                String desc = edtDesc.getText().toString() ;

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) ){
                    Toast.makeText(getApplicationContext(),"All fields must be filled",Toast.LENGTH_SHORT).show();
                }else if (videoUri == null){
                    Toast.makeText(getApplicationContext(),"Video Must Be Choose",Toast.LENGTH_SHORT).show();
                }else {
                    upload(title,desc);
                }
            }
        });

    }

    void upload(final String title,final String desc){
        final ProgressDialog progressDialog = new ProgressDialog(this) ;
        progressDialog.setMessage("Upload");
        progressDialog.setCancelable(false);
        progressDialog.show();
        storageReference = FirebaseStorage.getInstance().getReference("videos") ;

        if (TAG == 0){
            saveUri();
        }

        if ( imageUri != null && videoUri != null) {
            final StorageReference filereference = storageReference.child(idIntent).child("url_photo").child(idPos + ".png" );

            uploadTask = filereference.putFile(imageUri) ;
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException() ;
                    }
                    return filereference.getDownloadUrl() ;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult() ;
                        myPhotoUrl = downloadUri.toString() ;

                        final StorageReference videoreference = storageReference.child(idIntent).child("url_video").child(idPos + ".mp4" );
                        uploadVideoTask = videoreference.putFile(videoUri) ;
                        uploadVideoTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task2) throws Exception {
                                if (!task2.isSuccessful())
                                {
                                    throw task2.getException() ;
                                }
                                return videoreference.getDownloadUrl() ;
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task2) {
                                if (task2.isSuccessful()){
                                    Uri downloadUri = task2.getResult() ;
                                    myVideoUrl = downloadUri.toString() ;

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryVideo").child(idIntent).child("List") ;
                                    HashMap<String, Object> hashMap = new HashMap<>() ;
                                    hashMap.put("id",idPos) ;
                                    hashMap.put("title",title) ;
                                    hashMap.put("desc",desc) ;
                                    hashMap.put("photo",""+ myPhotoUrl) ;
                                    hashMap.put("video",""+ myVideoUrl) ;
                                    hashMap.put("date",String.valueOf(System.currentTimeMillis())) ;
                                    hashMap.put("duration",duration) ;
                                    reference.child(idPos).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    }) ;

                                }else
                                {
                                    Toast.makeText(getApplicationContext(),task2.getException().toString(),Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Toast.makeText(getApplicationContext(),"There is an error",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }) ;


                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(getApplicationContext(),"There is an error",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }) ;
        }
        else
        {
            if (videoUri == null)
            {
                Toast.makeText(getApplicationContext(),"Video Null",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST :{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(AddVideoActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        addVideo() ;
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Permission not granted",Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }

    void addVideo(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent,OTHER_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== OTHER_REQ &&resultCode== RESULT_OK){
            String path = data.getData().toString();
            videoUri = Uri.parse(path) ;
            String namePath = getRealPathFromURI(videoUri) ;
            Bitmap bitmapThumbNail = (Bitmap) ThumbnailUtils.createVideoThumbnail(namePath, MediaStore.Images.Thumbnails.MINI_KIND) ;
            //extensionVideo = namePath.substring(namePath.lastIndexOf("."));
            imageUri = bitmapToFile(bitmapThumbNail,getApplicationContext()) ;
            ivThumb.setImageURI(imageUri) ;

            imageBitmap = bitmapThumbNail ;
            ivThumb.setImageBitmap(bitmapThumbNail) ;

            TAG = 0 ;
            duration = String.valueOf(durationUriOfVideo(namePath)) ;
            tvDuration.setText("Duration : " + formatDuration(durationUriOfVideo(namePath)) );
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryVideo").child(idIntent).child("List") ;
            idPos= reference.push().getKey() ;
            btnSetPicture.setEnabled(true) ;

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  && resultCode == RESULT_OK )
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data) ;
            imageUri = result.getUri() ;
            TAG = 1 ;
            ivThumb.setImageURI(imageUri);

            if (idPos == null){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Videos") ;
                idPos= reference.push().getKey() ;
            }

        }

    }

    private long durationUriOfVideo(String uriOfFile){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getApplicationContext(), Uri.fromFile(new File(uriOfFile)) );
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        retriever.release() ;
        return timeInMillisec ;
    }

    private String formatDuration(long timeInMillisec){
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
                TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec))
        );
    }

    private String durationVideoUri(String uriOfFile){
        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(uriOfFile));
        int duration = mp.getDuration();
        mp.release();
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    private Uri bitmapToFile(Bitmap bitmap, Context context){
        ContextWrapper wrapper = new ContextWrapper(context) ;
        File file = wrapper.getDir("Images", Context.MODE_PRIVATE) ;
        file = new File(file,"${UUID.randomUUID()}.jpg") ;

        try{
            OutputStream stream = (OutputStream) (new FileOutputStream(file) );
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream) ;
            stream.flush() ;
            stream.close() ;
        }catch (IOException e){
            e.printStackTrace() ;
        }
        return Uri.parse(file.getAbsolutePath()) ;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void saveUri(){
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()) ;
        File file = new File(root.toString() + "/" + "VideoArticle Share" + "/" + "V&A" + String.valueOf(System.currentTimeMillis()+ ".jpeg")) ;
        try{
            FileOutputStream stream ;
            if (file.createNewFile() ){

            }
            scanner(file.getAbsolutePath());
            stream = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.close();

        }catch (IOException e) // Catch the exception
        {

        }

        imageUri = getImageContentUri(getApplicationContext(), file) ;
    }

    private void scanner(String path) {
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
