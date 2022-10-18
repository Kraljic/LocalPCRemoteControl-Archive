package com.example.localpcremotecontrol;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.localpcremotecontrol.communication.Config;
import com.example.localpcremotecontrol.communication.Controls;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findPcRemoteServerIp();
    }

    private void sendControl(final Controls control) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkPermission();
                    Socket socket = new Socket(Config.ip, Config.portPcRemoteServer);
                    socket.getOutputStream().write(control.getCode());
                    socket.getOutputStream().flush();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void btnLock_onClick(View view) {
        sendControl(Controls.LOCK_PC);
    }

    public void btnPlay_onClick(View view) {
        sendControl(Controls.PLAY_TOGGLE);
    }

    public void btnVolDown_onClick(View view) {
        sendControl(Controls.VOLUME_DOWN);
    }

    public void btnVolUp_onClick(View view) {
        sendControl(Controls.VOLUME_UP);
    }

    public void btnPowerOff_onClick(View view) {
        sendControl(Controls.POWER_OFF);
    }

    public void btnMute_onClick(View view) {
        sendControl(Controls.MUTE);
    }

    public void btnPrev_onClick(View view) {
        sendControl(Controls.PREV);
    }

    public void btnNext_onClick(View view) {
        sendControl(Controls.NEXT);
    }

    public void btnChangeSubtitles_onClick(View view) {
        sendControl(Controls.CHANGE_SUBTITLE);
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    123);
        }
    }

    public void btnDiscoverServer_onClick(View view) {
        findPcRemoteServerIp();
    }

    private void findPcRemoteServerIp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkPermission();

                    DatagramSocket socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    socket.setSoTimeout(5000);

                    InetAddress bcastAdr = InetAddress.getByName("255.255.255.255");

                    byte[] buffer = "HELLO MASTER".getBytes();
                    DatagramPacket packetSend = new DatagramPacket(buffer, buffer.length, bcastAdr, Config.portDiscoveryServer);
                    socket.send(packetSend);

                    buffer = new byte[15];
                    DatagramPacket packetReceive = new DatagramPacket(buffer, buffer.length, bcastAdr, Config.portDiscoveryServer);
                    socket.receive(packetReceive);

                    if (new String(packetReceive.getData()).substring(0, 14).equals("PcRemoteServer")) {
                        System.out.println("Found PCRemoteServer");
                        System.out.println(new String(packetReceive.getData()));
                        System.out.println(packetReceive.getAddress());
                        Config.ip = packetReceive.getAddress().toString().substring(1); /* Remove slash from start /127.0.0.1 */

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Server found:\n" + Config.ip + ":" + Config.portPcRemoteServer, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    } else {
                        System.out.println("Server not found!");
                    }

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Config.ip = null;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Server not found!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
