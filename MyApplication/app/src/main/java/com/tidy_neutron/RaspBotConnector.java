package com.tidy_neutron;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.VideoView;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by yorel56 on 5/14/2017.
 */

public class RaspBotConnector extends AsyncTask<ImageView, Void, Void>{
    private String SOCKET_ADDR;
    private int SOCKET_PORT;

    private boolean running;
    private static ImageView image = null;

    static{ System.loadLibrary("opencv_java"); }

    public RaspBotConnector(String addr, int port){
        SOCKET_ADDR =  addr;
        SOCKET_PORT = port;
        running = true;
    }

    @Override
    protected Void doInBackground(ImageView... images){
        if(images.length>0)
            image = images[0];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket raspBotSocket = new Socket(SOCKET_ADDR, SOCKET_PORT);
                    DataInputStream fromRaspBot = new DataInputStream(raspBotSocket.getInputStream());
                    byte[] data = new byte[1024];
                    while(isRunning()){
                        if(image == null)
                            continue;
                        Mat imageToDisp = Mat.zeros(image.getWidth(),image.getHeight(), CvType.CV_8UC3);
                        fromRaspBot.readFully(data);
                        imageToDisp.put(0,0,data);
                        Bitmap bm = Bitmap.createBitmap(imageToDisp.width(),imageToDisp.height(),Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(imageToDisp,bm);
                        image.setImageBitmap(bm);
                    }
                    raspBotSocket.close();
                }catch (IOException ex){

                }

            }
        }).start();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}
