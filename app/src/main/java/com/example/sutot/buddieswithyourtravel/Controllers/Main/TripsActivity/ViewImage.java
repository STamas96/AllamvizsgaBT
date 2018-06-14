package com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_image);
        ImageView fullscreen = findViewById(R.id.ImageViewFullScreen);
        String img = getIntent().getStringExtra("img");
        StorageReference pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(img);
        Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(pictureref)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(fullscreen);
    }
}
