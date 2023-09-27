package com.example.ass2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Objects;

public class singlepicture extends AppCompatActivity {
    public Button btnBack;
    public Button deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepicture2);

        // 获取照片路径
        String photoPath = getIntent().getStringExtra("photo_path");
        long photoId = getIntent().getLongExtra("photo_id",1);
        int position = getIntent().getIntExtra("photo_position",2);
        // 在布局文件中添加一个 ImageView，这里假设它的 id 为 imageView
        ImageView imageView = findViewById(R.id.imageView);

        // 加载高分辨率照片到 ImageView
        Bitmap highResBitmap = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(highResBitmap);

        /**
         *Specify the activity that returns to the album applet.
         * */
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个Intent，指定返回到相册小程序的activity
                Intent intent = new Intent(singlepicture.this, MainActivity.class);
                startActivity(intent);
                finish(); // 结束当前的activity
            }
        });
        /**
         *  delete photo
         * */
        deleteButton = findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete photo
              deletePhoto(position);
            }
        });
        /**
         * zoom in
         * */
        Button zoomButton = findViewById(R.id.btnZoom);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里实现对照片施加pitch和zoom的功能
                // 可以调用相关方法或执行相应的操作
                ImageView imageView = findViewById(R.id.imageView);
                // 缩放
                float scale = 1.2f; // 缩放比例
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
                // 放大
                imageView.setRotation(0);
                float zoomFactor = 1.5f; // 放大因子
                imageView.animate().scaleXBy(zoomFactor).scaleYBy(zoomFactor).setDuration(200).start();
            }
        });
        /**
         * zoom out
         * */
        Button pitchButton = findViewById(R.id.btnPitch);
        pitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里实现对照片施加pitch和zoom的功能
                // 可以调用相关方法或执行相应的操作
                ImageView imageView = findViewById(R.id.imageView);

                // 缩放
                float scale = 0.8f; // 缩放比例
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
                // 缩小
                imageView.setRotation(0);
                float zoomFactor = -1.5f; //缩小因子，负数才会出现缩小动画，rotation让画面在转回来
                imageView.animate().rotation(180).scaleXBy(zoomFactor).scaleYBy(zoomFactor).setDuration(200).start();
            }
        });

    }
    /**
    This method is used to delete a photo at the specified position.
    It removes the photo from the adapter's data list,
    updates the adapter's data set, and refreshes the view.
    Finally, it closes the current activity or performs other operations.
    */
    @SuppressLint("NotifyDataSetChanged")
    private void deletePhoto(int position) {
        // 在这里执行删除照片的操作，根据照片的唯一标识符删除相应的照片
        MainActivity.adapter.removeItem(position);
        MainActivity.adapter.notifyItemRemoved(position);
        // 更新主Activity的适配器
        MainActivity.adapter.notifyDataSetChanged();

        // 关闭当前Activity或执行其他操作
        finish();
    }
}
