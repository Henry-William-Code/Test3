package com.qgnix.live.news.download;


import com.qgnix.live.news.Call2;
import com.qgnix.live.news.Callback2;
import com.qgnix.live.news.OkHttpClient2;
import com.qgnix.live.news.Request2;
import com.qgnix.live.news.Response2;
import com.qgnix.live.news.ThreadPoolManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件下载工具类（单例模式）
 */

public class DownloadUtil2 {

    private static DownloadUtil2 downloadUtil;
    private final OkHttpClient2 okHttpClient;

    public static DownloadUtil2 get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil2();
        }
        return downloadUtil;
    }

    public DownloadUtil2() {
        okHttpClient = new OkHttpClient2.Builder().build();
    }


    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称，后面记得拼接后缀，否则手机没法识别文件类型
     * @param listener     下载监听
     */

    public void download(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {

        Request2 request = new Request2.Builder()
                .url(url)
                .build();



        //OkHttpClient client = new OkHttpClient();

        /*try {
            //Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //异步请求
        okHttpClient.newCall(request).enqueue(new Callback2() {
            @Override
            public void onFailure(Call2 call, IOException e) {
                // 下载失败监听回调
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call2 call, Response2 response) throws IOException {
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;

                        //储存下载文件的目录
                        File dir = new File(destFileDir);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, destFileName);

                        try {

                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                //int progress = (int) (sum * 1.0f / total * 100);
                                //下载中更新进度条
                                //listener.onDownloading(progress);
                            }
                            fos.flush();
                            //下载完成
                            listener.onDownloadSuccess(file);
                        } catch (Exception e) {
                            listener.onDownloadFailed(e);
                        } finally {

                            try {
                                if (is != null) {
                                    is.close();
                                }
                                if (fos != null) {
                                    fos.close();
                                }
                            } catch (IOException e) {

                            }

                        }


                    }*/
                        byteToFile(response.body().bytes(),destFileDir,destFileName);

                    }
                });

            }
        });
    }

    /**
     根据byte数组，生成文件
     */
    public void byteToFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath+ File.separator+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }



    public interface OnDownloadListener{

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */

        void onDownloadFailed(Exception e);

    }
}