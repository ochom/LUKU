package com.lysofts.luku;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class PhotoViewerActivity extends Activity {
    PhotoView imaveView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        imaveView = findViewById(R.id.fullscreen_content);

        String imageUrl = getIntent().getStringExtra("imageUrl");
        Picasso.get().load(imageUrl).into(imaveView);
    }
}
