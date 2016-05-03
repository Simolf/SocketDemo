package com.example.cxx.socketdemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText ip,content;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = (EditText) findViewById(R.id.ip);
        content = (EditText) findViewById(R.id.content);
        text = (TextView) findViewById(R.id.tv);
        findViewById(R.id.link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader  = null;
    public void connect(){
        final AsyncTask<Void,String,Void> read=new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    socket = new Socket(ip.getText().toString(),12345);
                    writer =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                    publishProgress("@success");
                    String line = null;
                    while((line=reader.readLine())!=null){
                        publishProgress(line);
                        System.out.println(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this,"无法建立链接",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if(values[0].equals("@success")) {
                    Toast.makeText(MainActivity.this, "链接成功", Toast.LENGTH_SHORT).show();
                }
                text.append("别人说："+values[0]+"\n");
                super.onProgressUpdate(values);
            }
        } ;
        read.execute();
    }
    public void send(){
        try {
            text.append("我说："+content.getText().toString()+"\n");
            writer.write(content.getText().toString()+"\n");
            writer.flush();
            content.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
