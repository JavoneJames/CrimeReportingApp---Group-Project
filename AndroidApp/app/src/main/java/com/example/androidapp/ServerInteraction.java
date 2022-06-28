package com.example.androidapp;


import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ServerInteraction extends Thread {

    private String ip;
    private String port;
    private final Handler handler;
    private Activity activity;
    private Socket s;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private JSONArray recievedData;
    private JSONObject jsonObject;
    private String communicationType = "";
    private int radius;
    private Location location;

    public ServerInteraction(String ip, String port, Handler handler, Activity activity) {

        this.ip = ip;
        this.handler = handler;
        this.port = port;
        this.activity = activity;

    }

    public void sendJSONToServer(JSONObject JO) {
        jsonObject = JO;
    }

    public JSONArray getJSONArrayFromServer(int radius, Location location) {
        communicationType = "recieve";
        //radius in km
        this.radius = radius;
        return recievedData;
    }

    @Override
    public void run() {
        InetAddress server = null;
        try {
            server = InetAddress.getByName(ip);
            s = new Socket(server, Integer.parseInt(port));
            objectOutputStream = new ObjectOutputStream(s.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            if (jsonObject != null) {
                try {

                    objectOutputStream.writeObject(jsonObject.toString());
                    jsonObject = null;
                    objectOutputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (communicationType.equalsIgnoreCase("recieve")) {
                try {
                    JSONObject dataRequest = new JSONObject();
                    try {
                        dataRequest.put("longitude", location.getLongitude());
                        dataRequest.put("latitude", location.getLatitude());
                        dataRequest.put("radius", radius);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    objectOutputStream = new ObjectOutputStream(s.getOutputStream());
                    objectOutputStream.writeObject(dataRequest);

                    objectInputStream = new ObjectInputStream(s.getInputStream());
                    JSONArray array = (JSONArray) objectInputStream.readObject();
                    recievedData = array;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                communicationType = "";
            }
        }
    }
}
