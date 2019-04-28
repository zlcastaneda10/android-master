package com.team8.locky.mapMenu;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl = "https://proyectomoviles-64a71.firebaseio.com/Mapa.json";
    private String downloadFileName = "list_of_locations.geojson";
    private String path = "../assets/";

    File outputFile = null;

    public DownloadTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    @Override
    protected Void doInBackground(Void... args0)
    {
        InputStream is = null;
        OutputStream fos = null;
        HttpURLConnection c = null;

        try
        {
            URL url = new URL(downloadUrl);//Create Download URl
            c = (HttpURLConnection) url.openConnection();//Open Url Connection
            c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
            c.connect();//connect the URL Connection
            //If Connection response is not OK then show Logs
            if (c.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                        + " " + c.getResponseMessage());
            }

            outputFile = new File(path,downloadFileName);
            if (!outputFile.exists())
            {
                outputFile.mkdir();
                Log.e (TAG,"File Created!");
            }

            is = c.getInputStream();//Get InputStream for connection
            fos = context.openFileOutput(downloadFileName,Context.MODE_PRIVATE);

            byte[] buffer = new byte[4096];//Set buffer type
            int count;
            while ((count = is.read(buffer)) != -1) {
                fos.write(buffer, 0, count);//Write new file
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "Download Error Exception " + e.getMessage());
        }
        finally
        {
            try
            {
                if (fos!=null){fos.close();}
                if (is!=null){is.close();}
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, "Error de Escritura/Lectura de Archivos " + e.getMessage());
            }
            if ( c != null)
            {
                c.disconnect();
            }
        }
        return null;
    }




}