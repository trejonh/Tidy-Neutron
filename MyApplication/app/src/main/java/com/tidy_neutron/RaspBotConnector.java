package com.tidy_neutron;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private DataInputStream in;

    private boolean running;
    private static ControllerActivity image = null;

    static{ System.loadLibrary("opencv_java"); }

    public RaspBotConnector(String addr, int videoPort,int controllerPort, ControllerActivity image){
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
        if(raspBotControllerSoket != null && raspBotControllerSoket.isConnected()) {
            try {
                out = new DataOutputStream(raspBotControllerSoket.getOutputStream());
                in = new DataInputStream(raspBotVideoSocket.getInputStream());
                startVideoStream();
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
                    if(image == null)
                        return;
                    //Toast.makeText(image.getContext().getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                    byte[] data = new byte[1024];// = new byte[3160];
                    Mat imageToDisp;// = Mat.zeros(320,240, CvType.CV_8UC3);
                    while(isRunning()){
                        if(image == null)
                            return;
                        in.readFully(data);
                        //imageToDisp.put(0,0,data);

                        MatOfByte mob = new MatOfByte(data);
                        imageToDisp = Highgui.imdecode(mob, Highgui.IMREAD_UNCHANGED);
                        final Bitmap bm = Bitmap.createBitmap(320,240,Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(imageToDisp,bm);
                        image.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView i = (ImageView)image.findViewById(R.id.botCamera);
                                i.setImageBitmap(bm);
                            }
                        });
                        //image.setImageBitmap(bm);
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
