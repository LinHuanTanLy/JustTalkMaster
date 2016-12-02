package com.luwei.testjusttalk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/12/1.
 */
public class Helper {
    public static String getSendDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        String dir = null;
        if (fileDir != null) {
            dir = fileDir.getAbsolutePath();
        } else {
            dir = context.getFilesDir().getAbsolutePath();
        }
        dir += "/mtc/bgimage/";
        fileDir = new File(dir);
        fileDir.mkdirs();
        return dir;
    }

    public static void copyImageToSdcard(Context context, int resId, String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), resId, options);
            options.inSampleSize = computeSampleSize(options, -1, 1280 * 720);
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId, options);

            File file = new File(path);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
