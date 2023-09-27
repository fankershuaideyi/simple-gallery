package com.example.ass2;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


public class photoAdapter extends RecyclerView.Adapter<photoAdapter.ViewHolder> {

    private List<PhotoItem> photoItems;
    private LruCache<String, Bitmap> imageCache;
    int thumbnailSize = 400;
    public photoAdapter(){

    }
    public photoAdapter(List<PhotoItem> photoItems) {
        this.photoItems = photoItems;
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8; // 使用内存的1/8作为缓存大小
        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_item_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagelayout,null);
        return new ViewHolder(view);
    }
    /**
     Binds the data to the ViewHolder and handles click events.
     Loads the image into the holder.
     Sets the click listener to handle the click event on the item.
     Opens the image details or navigates to a new screen based on the click event.
     Starts a new activity to display the photo.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 在这里加载图片和设置其他数据到网格项中
        String photoPath = photoItems.get(position).getPhotoPath();
        long id = photoItems.get(position).getId();
        int set = position;
        // 加载图片到 holder.imageView
        new LoadPhotoTask(holder.imageView).execute(photoPath);
        // add clocked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 处理点击事件   Handle click event
                // 在这里可以执行打开图片详情、跳转到新界面等操作  Here you can perform actions such as opening image details, navigating to a new screen, etc.
                // 可以使用 position 获取被点击项的位置  You can use the position parameter to get the position of the clicked item.

                Intent intent = new Intent(view.getContext(), singlepicture.class);
                intent.putExtra("photo_path", photoPath);
                intent.putExtra("photo_id", id);
                intent.putExtra("photo_position", set);
                // 启动新的 Activity 来显示照片
                view.getContext().startActivity(intent);

            }
        });
    }
    /**
     Load the image using a background thread
     */
    private class LoadPhotoTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public LoadPhotoTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String photoPath = params[0];

            // Check if the image is in the cache
            Bitmap cachedBitmap = imageCache.get(photoPath);

            if (cachedBitmap != null) {
                return cachedBitmap; // Return cached bitmap if available
            }

            // If not in cache, load it from file
            Bitmap originalBitmap = loadBitmapFromFile(photoPath);

            if (originalBitmap != null) {
                // Generate thumbnail
                Bitmap thumbnailBitmap = createThumbnail(photoPath, thumbnailSize);

                // Add the thumbnail to the cache
                imageCache.put(photoPath, thumbnailBitmap);

                return thumbnailBitmap;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference.get() != null && bitmap != null) {
                ImageView imageView = imageViewReference.get();
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    /**
     Creates a thumbnail bitmap from the given photo path with the specified thumbnail size.
     @param photoPath The path of the photo file.
     @param thumbnailSize The desired size of the thumbnail in pixels.
     @return The thumbnail bitmap.
     */
    public Bitmap createThumbnail(String photoPath, int thumbnailSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        // 计算缩放比例
        int scale = 1;
        if (options.outWidth > thumbnailSize || options.outHeight > thumbnailSize) {
            scale = Math.min(options.outWidth / thumbnailSize, options.outHeight / thumbnailSize);
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        // 加载原始照片
        Bitmap originalBitmap = BitmapFactory.decodeFile(photoPath, options);

        // 裁剪或缩放到指定的尺寸
        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(originalBitmap, thumbnailSize, thumbnailSize, false);
        // 释放原始照片资源
        originalBitmap.recycle();

        return thumbnailBitmap;
    }
    /**
     Loads a bitmap from the given file path.
     */
    public Bitmap loadBitmapFromFile(String filePath) {
        try {
            // 使用 BitmapFactory.decodeFile 方法从文件加载位图
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888; // 设置位图格式，这里使用ARGB_8888，可以根据需要调整
            return BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError e) {
            // 处理内存不足的情况
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // 处理其他异常情况
            e.printStackTrace();
            return null;
        }
    }
    public void removeItem(int position){
        photoItems.remove(position);
    }
}

