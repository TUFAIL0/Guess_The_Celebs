package com.example.guesstheceleb;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebsurls = new ArrayList<String>();
    ArrayList<String> celebsNames = new ArrayList<String>();
    int chosencelebs;
    ImageView image;
    int location_correct = 0;
    String[] answer = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void chosen(View view) {

        if (view.getTag().toString().equals(Integer.toString(location_correct)))
        {
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Wrong it was" + celebsNames.get(chosencelebs), Toast.LENGTH_LONG).show();

        }

    }


    public class ImageDownload extends AsyncTask<String,Void, Bitmap>
    {

        /**
         * @param urls
         * @deprecated
         */
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                Bitmap myBit = BitmapFactory.decodeStream(in);

                return myBit;



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }



    public class DownloadTask extends AsyncTask<String,Void,String>{

        /**
         * @param urls
         * @deprecated
         */
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url=new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in =urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data!=-1){

                    char current = (char) data;

                    result+=current;
                    data= reader.read();

                }

                return result;

            }
            catch(Exception e){
                e.printStackTrace();
            }


            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("https://www.forbes.com/celebrities/").get();
            String[] spiltResult = result.split("<div class = \"sidebarContainer\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(spiltResult[0]);

            while(m.find()){

              celebsurls.add(m.group(1));


            }

            p=Pattern.compile("alt=\"(.*?)\"");
            m=p.matcher(spiltResult[0]);


            while(m.find()){

               celebsNames.add(m.group(1));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void createnewQuestion(){

        Random random = new Random();
        chosencelebs = random.nextInt(celebsurls.size());

        ImageDownload imagetask = new ImageDownload();
        Bitmap celebImage;
        try {
            celebImage=imagetask.execute(celebsurls.get(chosencelebs)).get();


            image.setImageBitmap(celebImage);


            location_correct = random.nextInt(4);

            int incorrect;

            for(int i=0;i<4;i++){
                if(i==location_correct){
                    answer[i] = celebsNames.get(chosencelebs);
                }
                else{

                    incorrect = random.nextInt(celebsurls.size());

                    while(incorrect == chosencelebs){
                        incorrect = random.nextInt(celebsurls.size());
                    }

                    answer[i] = celebsNames.get(incorrect);


                }
            }

            button0.setText(answer[0]);
            button1.setText(answer[1]);
            button2.setText(answer[2]);
            button3.setText(answer[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}