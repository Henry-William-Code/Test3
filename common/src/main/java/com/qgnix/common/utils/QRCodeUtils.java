package com.qgnix.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

// 二维码生成工具类
public class QRCodeUtils {

    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String character_set, String error_correction_level,
                                            String margin, int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成二维码带LOGO 与邀请码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmaplogo(String content, int width, int height,
                                                String character_set, String error_correction_level,
                                                String margin, int color_black, int color_white, Bitmap logoBm) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成图片  加上title的图片
     *
     * @param content
     * @return
     */
    public static Bitmap createImage(String content, String character_set, String sharecode, String www, String wwwinfo, Bitmap logoBm) {
        int picWidth = 1048;//生成图片的宽度
        int picHeight = 1456;//生成图片的高度
        int titleTextSize = 50;
        int contentTextSize = 40;
        int textColor = Color.BLACK;
        int qrWidth = 832;
        int qrHeight = 832;
        int paddingTop = 65;//离上面高度
        int paddingMiddle = 102;
        int paddingBottom = 24;
        int paddingLeft = 108;//字靠左边
        //最终生成的图片
        Bitmap result = Bitmap.createBitmap(picWidth, picHeight, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Canvas canvas = new Canvas(result);

        //先画一整块白色矩形块
        canvas.drawRect(0, 0, picWidth, picHeight, paint);

        //画title链接文字
        Rect bounds = new Rect();
        paint.setColor(textColor);
        paint.setTextSize(titleTextSize);
        //获取文字的字宽高，以便将文字与图片中心对齐
        paint.getTextBounds(www, 0, www.length(), bounds);
        canvas.drawText(www, paddingLeft, paddingTop + 50, paint);

        //画白色矩形块
        int qrTop = paddingTop + titleTextSize + paddingMiddle;//二维码的顶部高度

        //画二维码
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, character_set);
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix;
        Bitmap image = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            image = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.RGB_565);
            for (int x = 0; x < qrWidth; x++) {
                for (int y = 0; y < qrHeight; y++) {
                    image.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (logoBm != null) {
            image = addLogo(image, logoBm);
        }
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(image, paddingLeft, qrTop, paint);


        //画左下角大LOGO

        try {

            canvas.drawBitmap(logoBm, paddingLeft, 1118, null);


        } catch (Exception e) {
            e.printStackTrace();
        }

        //给二维码加LOGO
        if (logoBm != null) {
            image = addLogo(image, logoBm);
        }
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(image, paddingLeft, qrTop, paint);
        //画右下角文字 邀请文字
        paint.setColor(Color.BLACK);
        paint.setTextSize(contentTextSize);

        //   int lineTextCount = (int)((picWidth-500)/contentTextSize);
        //   int line = (int)(Math.ceil(Double.valueOf(title.length())/Double.valueOf(lineTextCount)));
        //int textTop = qrTop+qrHeight+paddingBottom;//地址的顶部高度
        canvas.drawText(sharecode, paddingLeft + 270, 1118 + contentTextSize, paint);

        //画右下角文字 邀请文字
        paint.setColor(Color.BLACK);
        paint.setTextSize(34);
        int lineTextCount = 40;//英文的为30个字母
        if (LanSwitchUtil.isChina()) {
            lineTextCount = 15;//中文的为15个字
        }
        //   int lineTextCount = (int)((picWidth-50)/contentTextSize);
        int line_sub = (int) (Math.ceil(Double.valueOf(wwwinfo.length()) / Double.valueOf(lineTextCount)));
        //  int textTop = qrTop+qrHeight+paddingBottom;//地址的顶部高度

        for (int i = 0; i < line_sub; i++) {
            String s;
            if (i == line_sub - 1) {
                s = wwwinfo.substring(i * lineTextCount, wwwinfo.length());
            } else {
                s = wwwinfo.substring(i * lineTextCount, (i + 1) * lineTextCount);
            }
            paint.getTextBounds(s, 0, s.length(), bounds);

            canvas.drawText(s, paddingLeft + 270, 1118 + contentTextSize + 45 * (1 + i) + 10, paint);
        }
        paint.setColor(Color.RED);
        paint.setTextSize(40);
        canvas.drawText(www, paddingLeft + 270, 1118 + contentTextSize + 45 + 45 + 45 + 20, paint);

         canvas.save();
        canvas.restore();

        return result;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存图片到指定路径
     *
     * @param context
     * @param bitmap   要保存的图片
     * @param fileName 自定义图片名称
     * @return
     */
    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        // 保存图片至指定路径
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "qrcode";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();

            //发送广播通知系统图库刷新数据
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                ToastUtil.show("保存成功");
                return true;
            } else {
                ToastUtil.show("保存失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
