package com.qylk.mp.bus.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static File getExternalImageFile(String subDir) {
        if (!FileUtils.isSdCardMounted()) {
            return null;
        }
        File mediaStorageDir;
        if (TextUtils.isEmpty(subDir)) {
            mediaStorageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
        } else {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), subDir);
        }
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = DATE_FORMAT.format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath(), "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    public static final Bitmap getCompressBitmapByUri(Context context, Uri uri, int reqWith, int reqHeight) {
        BitmapFactory.Options options = getBoundOptionByUri(context, uri);
        int sampleSize = findSampleSizeSmallerThanDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        return getBitmapByUri(context, uri, options, sampleSize);
    }

    public static final Bitmap getCompressBitmapByUri2(Context context, Uri uri, int reqWith, int reqHeight) {
        BitmapFactory.Options options = getBoundOptionByUri(context, uri);
        int sampleSize = findSampleSizeLargerThanDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        return getBitmapByUri(context, uri, options, sampleSize);
    }

    public static final Bitmap getCompressBitmapByBytes(byte[] bytes, int reqWith, int reqHeight) {
        BitmapFactory.Options options = getBoundOptionByBytes(bytes);
        int sampleSize = findSampleSizeSmallerThanDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        return getBitmapByBytes(bytes, options, sampleSize);
    }

    public static final Bitmap getCompressBitmapByBytes2(byte[] bytes, int reqWith, int reqHeight) {
        BitmapFactory.Options options = getBoundOptionByBytes(bytes);
        int sampleSize = findSampleSizeLargerThanDesire(options.outWidth, options.outHeight, reqWith, reqHeight);
        return getBitmapByBytes(bytes, options, sampleSize);
    }

    public static Bitmap getBitmapByUri(Context context, Uri uri, BitmapFactory.Options options, int sampleSize) {
        Bitmap result = null;
        String filePath = getPathFromUri(context, uri);
        if (FileUtils.isFileExists(filePath)) {
            InputStream is = null;
            try {
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                is = new FileInputStream(filePath);
                result = BitmapFactory.decodeStream(is, null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError oome) {
                oome.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return result;
    }

    private static final Bitmap getBitmapByBytes(byte[] bytes, BitmapFactory.Options options, int sampleSize) {
        Bitmap result = null;
        if (bytes != null && bytes.length > 0) {
            try {
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static final BitmapFactory.Options getBoundOptionByUri(Context context, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return options;
    }

    public static final BitmapFactory.Options getBoundOptionByBytes(byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        return options;
    }

    public static final int findSampleSizeSmallerThanDesire(int actualWidth, int actualHeight, int desireWidth, int desireHeight) {
        double wr = (double) actualWidth / desireWidth;
        double hr = (double) actualHeight / desireHeight;
        double ratio = Math.max(wr, hr);
        float n = 1.0f;
        while (n < ratio) {
            n *= 2;
        }
        return (int) n;
    }

    public static final int findSampleSizeLargerThanDesire(int actualWidth, int actualHeight, int desireWidth, int desireHeight) {
        double wr = (double) actualWidth / desireWidth;
        double hr = (double) actualHeight / desireHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap, int compressQuality) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap.hasAlpha()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, compressQuality, localByteArrayOutputStream);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, localByteArrayOutputStream);
        }
        byte[] result = localByteArrayOutputStream.toByteArray();
        IOUtils.closeQuietly(localByteArrayOutputStream);
        return result;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        String uriScheme = uri.getScheme();
        if (ContentResolver.SCHEME_FILE.equals(uriScheme)) {
            path = uri.getSchemeSpecificPart();
        } else if (ContentResolver.SCHEME_CONTENT.equals(uriScheme)) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(0);
                }
                cursor.close();
            }
        } else {
            path = uri.getPath();
        }
        return path;
    }

    public static float getScale(View imageView, Bitmap bitmap) {
        float scale = 1.0f;
        if (imageView != null && bitmap != null) {
            float photoViewWidth = imageView.getWidth();
            float photoViewHeight = imageView.getHeight();
            float bitmapWidth = bitmap.getWidth();
            float bitmapHeight = bitmap.getHeight();
            float widthScale = 1.0f, heightScale = 1.0f;
            if (photoViewWidth != 0) {
                widthScale = bitmapWidth / photoViewWidth;
            }
            if (photoViewHeight != 0) {
                heightScale = bitmapHeight / photoViewHeight;
            }
            scale = Math.max(widthScale, heightScale);
            if (bitmapWidth < photoViewWidth) {
                scale = scale / widthScale;
            }
        }
        return scale;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static Bitmap createReflectedImage(Bitmap originalImage, int number) {
        final int reflectionGap = 0; // 倒影和原图片间的距离
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        double reflectHeight = number / 100.00;

        number = (int) (height * reflectHeight);
        // 倒影部分
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, number, width, number, matrix, false);
        // 要返回的倒影图片
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + number), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        // 画原来的图片
        canvas.drawBitmap(originalImage, 0, 0, null);
        // 画倒影部分
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    public static Bitmap addFrame(Bitmap bitmap, int width, int color) {
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);
        Rect rect = canvas.getClipBounds();
        rect.bottom -= width;
        rect.right -= width;
        Paint recPaint = new Paint();
        recPaint.setColor(color);
        recPaint.setStrokeWidth(width);
        recPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect, recPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return bitmap2;
    }
}
