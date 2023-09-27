package com.example.ass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private RecyclerView recyclerView;
    public static photoAdapter adapter;
    private List<PhotoItem> photoItems =new ArrayList<>();
    public Executor executor = Executors.newFixedThreadPool(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化gridView
        recyclerView= findViewById(R.id.recycler_view);
        checkPermission();
    }
    /**
     If the permission is already granted, it proceeds to load the photos.
     Throws a runtime exception if there is an error loading the photos.
     If the permission is already granted, it proceeds to load the photos.
     Throws a runtime exception if there is an error loading the photos.
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }else {
            try {
                loadPhotos();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }
    /**
     Loads photos from the device's external storage.
     It queries the MediaStore to retrieve the image data, including the file path, date added, ID, orientation, width, and height.
     The query is filtered to only retrieve JPEG images.
     The retrieved data is used to create PhotoItem objects, which are added to the photoItems list.
     Finally, the photoItems list is set as the data source for the photoAdapter, and the adapter is set to the recyclerView.
     @throws FileNotFoundException if the specified file path is not found.
     */
    //加载图片
    private void loadPhotos() throws FileNotFoundException {
        String[] projection = new String[] {
                MediaStore.Images.Media.DATA,   // 媒体文件路径
                MediaStore.Images.Media.DATE_ADDED,// 媒体文件添加日期
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
        };
        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
        String[] selectionArgs = new String[] { "image/jpeg" };  // 替代占位符的值，筛选JPEG类型的图片
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";  //对照片进行排序
        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
      if(cursor.moveToFirst()){
          do{
              PhotoItem photoItem = new PhotoItem();
              //获取图像路径
              int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
              String photoPath = cursor.getString(index);
              photoItem.setPhotoPath(photoPath);
              //获取图像id
              int idColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
              long mediaStoreId = cursor.getLong(idColumnIndex);
              photoItem.setId(mediaStoreId);
              //获取图像添加日期
              int dateAddedColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
              long dateAdded = cursor.getLong(dateAddedColumnIndex);
              photoItem.setTimeAdd(dateAdded);
              //获取图像orientation
              int orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
              int orientation = cursor.getInt(orientationColumnIndex);
              photoItem.setOrientation(orientation);
              //获取宽度
              int widthColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
              int width = cursor.getInt(widthColumnIndex);
              photoItem.setWidth(width);
              //获取高度
              int heightColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);
              int height = cursor.getInt(heightColumnIndex);
              photoItem.setHeight(height);
              photoItems.add(photoItem);
          }while (cursor.moveToNext());
          cursor.close();
      }
       adapter = new photoAdapter(photoItems);
       recyclerView.setAdapter(adapter);
    }
    /**
     If the requested permission is READ_EXTERNAL_STORAGE and the user grants the permission, it proceeds to load the photos.
     Additional explanations or appropriate actions can be taken in case of permission denial.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，加载照片
                try {
                    loadPhotos();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 用户拒绝了权限，可以向用户解释为什么需要这个权限，或者采取其他适当的措施
                Toast.makeText(this, "Permission denied, unable to load photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏模式 Handle the change in landscape mode.
          checkPermission();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏模式 Handle the change in portrait mode.
          checkPermission();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}