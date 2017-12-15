package com.tidy_neutron;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by yorel56 on 5/14/2017.
 */

public class RaspBotConnector extends AsyncTask<Void, Void, Void> implements View.OnTouchListener{
    private String SOCKET_ADDR;
    private int VIDEO_PORT;
    private int CONTROLLER_PORT;
    private Socket  raspBotControllerSoket;
    private DatagramSocket raspBotVideoSocket;
    private DataOutputStream out;
    private DataInputStream in;

    private boolean running;
    private static ControllerActivity image = null;

    static{ System.loadLibrary("opencv_java"); }

    public RaspBotConnector(String addr, int videoPort,int controllerPort, ControllerActivity image){
        SOCKET_ADDR =  addr;
        //VIDEO_PORT = videoPort;
        CONTROLLER_PORT = controllerPort;
        running = true;
        this.image = image;
    }

    @Override
    protected Void doInBackground(Void... arg){
        try{
            //DatagramPacket recvPacket;
           // raspBotVideoSocket = new DatagramSocket(VIDEO_PORT,InetAddress.getByName(SOCKET_ADDR));
            //recvPacket = raspBotVideoSocket.
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
               // in = new DataInputStream(raspBotVideoSocket.getInputStream());
                //startVideoStream();
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

//    private void startVideoStream(){
//        for(;;){
//            try {
//                byte[] buf = new byte[921600];
//                DatagramPacket packet = new DatagramPacket(buf, 921600);
//                raspBotVideoSocket.receive(packet);
//                if (packet == null) continue;
//                byte[] receivedImage = packet.getData();
//                MatOfByte mob = new MatOfByte(receivedImage);
//                Mat img = Highgui.imdecode(mob, Highgui.IMREAD_UNCHANGED);// convert to bitmap:
//                Bitmap bm = Bitmap.createBitmap(img.cols(), img.rows(),Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(img, bm);
//
//                // find the imageview and draw it!
//                ImageView iv = (ImageView) image.findViewById(R.id.botCamera);
//                iv.setImageBitmap(bm);
//            }catch (IOException ex){
//                image.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(image.getApplicationContext(),"IOError",Toast.LENGTH_LONG).show();
//                    }
//                });
//
//            }
//        }
//    }

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
