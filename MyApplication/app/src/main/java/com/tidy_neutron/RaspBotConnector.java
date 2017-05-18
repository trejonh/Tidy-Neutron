package com.tidy_neutron;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by yorel56 on 5/14/2017.
 */

public class RaspBotConnector extends AsyncTask<Void, Void, Void> implements View.OnTouchListener{
    private String SOCKET_ADDR;
    private int VIDEO_PORT;
    private int CONTROLLER_PORT;
    private Socket raspBotVideoSocket, raspBotControllerSoket;
    private DataOutputStream out;

    private boolean running;
    private static ImageView image = null;

    static{ System.loadLibrary("opencv_java"); }

    public RaspBotConnector(String addr, int videoPort,int controllerPort, ImageView image){
        SOCKET_ADDR =  addr;
        VIDEO_PORT = videoPort;
        CONTROLLER_PORT = controllerPort;
        running = true;
        this.image = image;
    }

    @Override
    protected Void doInBackground(Void... arg){
        try{
            raspBotVideoSocket = new Socket(SOCKET_ADDR,VIDEO_PORT);
            raspBotControllerSoket = new Socket(SOCKET_ADDR,CONTROLLER_PORT);
        }catch(IOException ex){
            //do nothing
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        startVideoStream();
        if(raspBotControllerSoket != null && raspBotControllerSoket.isConnected()) {
            try {
                out = new DataOutputStream(raspBotControllerSoket.getOutputStream());
            }catch (IOException ex){
                //do noting
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void startVideoStream(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(raspBotVideoSocket == null || raspBotVideoSocket.isClosed())
                        return;
                    DataInputStream fromRaspBot = new DataInputStream(raspBotVideoSocket.getInputStream());
                    byte[] data = new byte[1024];
                    while(isRunning()){
                        if(image == null)
                            return;
                        Mat imageToDisp = Mat.zeros(image.getWidth(),image.getHeight(), CvType.CV_8UC3);
                        fromRaspBot.readFully(data);
                        imageToDisp.put(0,0,data);
                        Bitmap bm = Bitmap.createBitmap(imageToDisp.width(),imageToDisp.height(),Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(imageToDisp,bm);
                        image.setImageBitmap(bm);
                    }
                    raspBotVideoSocket.close();
                }catch (IOException ex){

                }

            }
        }).start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(out == null || raspBotControllerSoket.isClosed())
                    return true;
                try{
                    out.writeUTF(((Button)v).getText().toString());
                }catch (IOException ex){
                    Toast.makeText(v.getContext(),ex.getMessage(),Toast.LENGTH_LONG);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if(out == null || raspBotControllerSoket.isClosed())
                    return true;
                try{
                    out.writeUTF("stop");
                }catch (IOException ex){
                    Toast.makeText(v.getContext(),ex.getMessage(),Toast.LENGTH_LONG);
                }
                return true;
        }
        return false;
    }
}
