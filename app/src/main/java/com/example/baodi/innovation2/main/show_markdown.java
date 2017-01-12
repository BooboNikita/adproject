package com.example.baodi.innovation2.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baodi.innovation2.MarkDown;
import com.example.baodi.innovation2.R;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class show_markdown extends AppCompatActivity {

    TextView textView;
    String name;
    Handler handler;
    String str;
    private static final int UPDATE_CONTENT=0;
    String urlString="http://101.200.59.74:8080/androidpro/deleteSQL";
    LoginActivity loginActivity;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_markdown);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name=getIntent().getStringExtra("name");
        username=loginActivity.userNameValue;
        textView=(TextView) findViewById(R.id.markdown_content);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            String newPath = URLEncoder.encode(name, "utf-8");
            FileInputStream fileInput =openFileInput(newPath);
            byte[] contents=new byte[fileInput.available()];
            fileInput.read(contents);
            String content= EncodingUtils.getString(contents, "UTF-8");
            fileInput.close();
            Log.v(content,"asd");
//            InputStream stream=fileInput;

            Spanned spanned= MarkDown.fromMarkdown(content,new Html.ImageGetter(){

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public Drawable getDrawable(String s) {
                    Drawable drawable = Drawable.createFromPath(s);

                    if(drawable!=null){
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    }

                    // 将其返回
                    return drawable;
//                    return null;
                }
            },textView);
            textView.setText(spanned);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case UPDATE_CONTENT:
                        Log.v(str,"baodi");
                        String newPath = null;
                        try {
                            newPath = URLEncoder.encode(name, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        File file=new File(newPath);
                        file.delete();
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            String newPath = URLEncoder.encode(name, "utf-8");
            FileInputStream fileInput =openFileInput(newPath);
            final byte[] contents=new byte[fileInput.available()];
            fileInput.read(contents);
            fileInput.close();
            Log.v(new String(contents),"asd");
//            InputStream stream=fileInput;

            textView.post(new Runnable() {
                @Override
                public void run() {
                    Spanned spanned = MarkDown.fromMarkdown(new String(contents), new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            Drawable drawable = new ColorDrawable(Color.LTGRAY);
                            drawable.setBounds(0, 0, textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight(), 400);
                            return drawable;
                        }
                    }, textView);
                    textView.setText(spanned);
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.editor_action,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.edit_button:
                Intent intent=new Intent(this,activity_editor_markdown.class);
                intent.putExtra("name",name);
                startActivity(intent);
                return true;
            case R.id.delete_button:
                //TODO
                connect(name);
                this.finish();
                return true;

        }
        return false;
    }
    public void connect(final String name){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String s=connectNetwork(name);
                        if(s.equals("false")) connectNetwork(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
        else{
            Toast.makeText(getApplicationContext(),"当前没有可用网络！",Toast.LENGTH_SHORT).show();
        }
    }
    private String connectNetwork(String name) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

//            String name="asd";
            Log.v(name,"name");
            name= URLEncoder.encode(name,"utf-8");
            out.writeBytes("name="+name+"&username="+username);
            InputStream in=connection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder=new StringBuilder();
            String line;
            while (((line=reader.readLine()))!=null){
                stringBuilder.append(line);
            }
            str=stringBuilder.toString();
            out.close();
            in.close();
            Message message=new Message();
            message.what=UPDATE_CONTENT;
            message.obj=name;
            handler.sendMessage(message);
            return str;
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection==null){
                connection.disconnect();
            }
        }
        return "";
    }
}
