package com.example.rmaahmadov.celebrationguess;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebretionsUrls = new ArrayList<>();
    ArrayList<String> celebretionsNames = new ArrayList<>();
    int choosenCelebretion = 0;
    private ImageView imageView;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    int locationOfCorrectAnswear = 0;
    String[] answears = new String[4];


    public void celebretionChoosen(View view) throws ExecutionException, InterruptedException {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswear))) {
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebretionsNames.get(choosenCelebretion), Toast.LENGTH_LONG).show();
        }
        createNewQuession();
    }

    public void resultTask() {
        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebretionsUrls.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebretionsNames.add(m.group(1));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createNewQuession() throws ExecutionException, InterruptedException {
        Random random = new Random();
        choosenCelebretion = random.nextInt(celebretionsUrls.size());
        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebImage;
        celebImage = imageTask.execute(celebretionsUrls.get(choosenCelebretion)).get();
        imageView.setImageBitmap(celebImage);
        locationOfCorrectAnswear = random.nextInt(4);
        int locationOfIncorrectAnswears;
        for (int i = 0; i < 4; i++) {
            if (i == locationOfCorrectAnswear) {
                answears[i] = celebretionsNames.get(choosenCelebretion);
            } else {
                locationOfIncorrectAnswears = random.nextInt(celebretionsUrls.size());
                while (locationOfIncorrectAnswears == choosenCelebretion) {
                    locationOfIncorrectAnswears = random.nextInt(celebretionsUrls.size());
                }
                answears[i] = celebretionsNames.get(locationOfIncorrectAnswears);
            }
            button1.setText(answears[0]);
            button2.setText(answears[1]);
            button3.setText(answears[2]);
            button4.setText(answears[3]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.btnVariant1);
        button2 = findViewById(R.id.btnVariant2);
        button3 = findViewById(R.id.btnVariant3);
        button4 = findViewById(R.id.btnVariant4);
        resultTask();
        try {
            createNewQuession();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
