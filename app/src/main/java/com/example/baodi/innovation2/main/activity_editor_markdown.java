package com.example.baodi.innovation2.main;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Message;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;

import com.example.baodi.innovation2.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;

public class activity_editor_markdown extends AppCompatActivity {
    TextView textView;
    EditText editText;
    Button save_button;
    Button clear_button;
    String name;
    String image_path;
    private android.os.Handler handler;
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private final int IMAGE_CODE = 0;
    String str;
    String urlString="http://101.200.59.74:8080/androidpro/findExistedFile";
    String urlString2="http://101.200.59.74:8080/androidpro/insertNewFile";
    private static final int UPDATE_CONTENT=0;
    private static final int INSERT_CONTENT=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_markdown);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        editText=(EditText) findViewById(R.id.editor_editview);
        save_button=(Button) findViewById(R.id.save_button);
        clear_button=(Button) findViewById(R.id.clear_button);
        actionBar.setDisplayHomeAsUpEnabled(true);
        name=getIntent().getStringExtra("name");
        if(name!=null){
            try {
                FileInputStream fileInput = openFileInput(name);
                byte[] contents = new byte[fileInput.available()];
                fileInput.read(contents);
                editText.setText(new String(contents));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        handler=new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case UPDATE_CONTENT:
                        Log.v(str,"123");
                        if(str.equals("false")||name!=null){
                            String FILE_NAME=(String) msg.obj;
                            Log.v(FILE_NAME,"filename");
                            try {
                                FileOutputStream fileOutputStream=openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
                                String content=editText.getText().toString();
                                fileOutputStream.write(content.getBytes());
                                fileOutputStream.close();
                                Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"文件重名了，请修改",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case INSERT_CONTENT:
                        Log.v(str,"234");
                        break;
                }
            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.add_image,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void saveFile(View view){
        String str=editText.getText().toString();
        if(str.equals("")){
            Toast.makeText(getApplicationContext(),"输入为空，不能保存",Toast.LENGTH_SHORT).show();
        }
        else{
            LayoutInflater inflater=LayoutInflater.from(this);
            final View v=inflater.inflate(R.layout.alertdialog,null);
            if(name==null){
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
                alertDialog.setView(v);
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText dialog_edit=(EditText) v.findViewById(R.id.file_name);
                        String n=dialog_edit.getText().toString();
                        connect(n);

                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
            }
            else {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
                alertDialog.setTitle("保存");
                alertDialog.setMessage("确认保存吗？");
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        connect(name);
                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();

            }



        }
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
                        if(s.equals("false")) insertFile(name);
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
            out.writeBytes("name="+name);
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
    private void insertFile(String name) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString2);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            name= URLEncoder.encode(name,"utf-8");
            out.writeBytes("name="+name);
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
            message.what=INSERT_CONTENT;
            handler.sendMessage(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection==null){
                connection.disconnect();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.add_image:
                setImage();
                return true;
        }
        return false;
    }
    private void setImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, IMAGE_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub


        Bitmap bm = null;

        ContentResolver resolver = getContentResolver();

        if (requestCode == IMAGE_CODE) {

            try {

                Uri originalUri = data.getData();

                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                image_path = cursor.getString(column_index);
                String text=editText.getText().toString();
                text+="![]("+image_path+")";
                Log.v(text,"text");
                editText.setText(text);
            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());
            }

            finally {
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}
