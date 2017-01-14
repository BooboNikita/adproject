package com.example.baodi.innovation2.main;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;

import com.example.baodi.innovation2.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
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
    LoginActivity loginActivity;
    String username;
    String urlString="http://101.200.59.74:8080/androidpro/findExistedFile";
    String urlString2="http://101.200.59.74:8080/androidpro/insertNewFile";
    String urlString3="http://101.200.59.74:8080/androidpro/recieveImage";
    private static final int UPDATE_CONTENT=0;
    private static final int INSERT_CONTENT=1;
    private static final int INSERT_PIC=2;
    private final int GET_PHOTO_BY_CAMERA = 100;
    private final int GET_PHOTO_BY_GALLERY = 200;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
//        byte [] bis=getIntent().getByteArrayExtra("img");
//        Bitmap img= BitmapFactory.decodeByteArray(bis, 0, bis.length);
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.activity_editor_markdown);
//        linearLayout.setBackground(new BitmapDrawable(getResources(),img));
        Log.v(name,"name123");
        username=loginActivity.userNameValue;
        Log.v(username,"usernameasdasdas");
        if(name!=null){
            try {
                String newPath = URLEncoder.encode(name, "utf-8");
                FileInputStream fileInput = openFileInput(newPath);
                Log.v(newPath,"url");
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
                    case INSERT_PIC:
                        Log.v(msg.obj.toString(),"obj");
                        if(msg.obj.equals("0")){
                            Toast.makeText(getApplicationContext(),"封面上传成功",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_SHORT).show();
                        }
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
            out.writeBytes("name="+name+"&username="+username);
            Log.v(username,"username123");
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
        final CharSequence[] items = { "本地上传"};
        if(name!=null){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("上传封面图片")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if (item == 0) {
//                            Log.v(TAG, "本地上传 ");
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(intent,
                                        GET_PHOTO_BY_GALLERY);
                                dialog.cancel();
                            } else if (item == 1) {

//                            Log.v(TAG, "拍照上传 ");

                                Intent intent = new Intent(
                                        "android.media.action.IMAGE_CAPTURE");

                                startActivityForResult(intent,
                                        GET_PHOTO_BY_CAMERA);

                                dialog.cancel();
                            }
                        }
                    }).create();
            dialog.show();
        }
        else {
            Toast.makeText(getApplicationContext(),"请先保存，重新进入后上传图片",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub


        if (requestCode == GET_PHOTO_BY_GALLERY) {
            Uri uri = data.getData();
            Log.v("1", "uri: " + uri.toString());

            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            Log.v(path, "path: " + path);
            connect_pic(path);
            ContentResolver cr = getContentResolver();
//            try {
//                mBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//            } catch (FileNotFoundException e) {
//
//                e.printStackTrace();
//            }

        } else if (requestCode == GET_PHOTO_BY_CAMERA) {
            Bundle bundle = data.getExtras();
//            mBitmap = (Bitmap) bundle.get("data");
            //对于使用相机上传，我这里目前只能获取到Bitmap，无法获取到URL，欢迎交流

        }

        super.onActivityResult(requestCode, resultCode, data);

    }
    public void connect_pic(final String name){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        insertpic(name);
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
    private void insertpic(String picnname) throws IOException {
        HttpURLConnection connection = null;
        try {
            File file=new File(picnname);
            UploadUtil uploadUtil=new UploadUtil();
            String re=uploadUtil.uploadFile(file,urlString3,name,username);
            Message message=new Message();
            message.what=INSERT_PIC;
            message.obj=re;
            handler.sendMessage(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
