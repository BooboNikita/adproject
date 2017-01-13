package com.example.baodi.innovation2.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.baodi.innovation2.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class scan_list extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String str;
    Handler handler;
    String urlString="http://101.200.59.74:8080/androidpro/findSQL";
    String urlString2="http://101.200.59.74:8080/androidpro/deleteSQL";
    private static final int UPDATE_CONTENT=0;
    private static final int DELETE_CONTENT=1;
    private static final int  UPDATE_PIC=2;
    ListView listView;
    List<Map<String,Object>> data=new ArrayList<>();
    SimpleAdapter adapter;
    String name;
    LoginActivity loginActivity;
    String username;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanlist_navidrawer);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(scan_list.this,activity_editor_markdown.class);
                startActivity(intent);
            }
        });
        username=loginActivity.userNameValue;
        Log.v(username,"username1");
        listView=(ListView) findViewById(R.id.scan_list);
        adapter=new SimpleAdapter(getApplicationContext(),data,R.layout.scan_listitem,new String[]{"name","date","img"},
                new int[]{R.id.dairy_filename,R.id.file_time,R.id.list_img});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(adapter);
        connect();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){
                    case UPDATE_CONTENT:
                        data.clear();
                        Log.v((String)msg.obj,"123");
                        String content[]=((String) msg.obj).split(" ");
                        Log.v(String.valueOf(content.length),"sad");
                        int num=0;
                        for(int i=0;i<content.length&&content.length>2;i+=4,num++){
                            Map<String ,Object> tmp=new HashMap<>();
                            bitmap=null;
                            returnBitMap(num,"http://101.200.59.74:8080/androidpro/file/"+content[i+3]);
                            tmp.put("name",content[i]);
                            tmp.put("date",content[i+2]);
                            Log.v(content[i+3],"content4");
                            tmp.put("img",bitmap);
                            data.add(tmp);
                        }
                        break;
                    case DELETE_CONTENT:
                        Log.v(str,"baodi");
                        String newPath = null;
                        try {
                            newPath = URLEncoder.encode(name, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        File file=new File(newPath);
                        file.delete();
//                        data.clear();
//                        Log.v((String)msg.obj,"123");
//                        String content1[]=((String) msg.obj).split(" ");
//                        Log.v(String.valueOf(content1.length),"sad");
//                        for(int i=0;i<content1.length&&content1.length>2;i+=3){
//                            Map<String ,Object> tmp=new HashMap<>();
//                            tmp.put("name",content1[i]);
//                            tmp.put("date",content1[i+2]);
//                            data.add(tmp);
//                        }
                        break;
                    case UPDATE_PIC:
                        data.get(msg.arg1).put("img",msg.obj);
                        break;
                    default:break;
                }
                adapter.notifyDataSetChanged();
            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String,Object> tmp= (Map<String, Object>) adapterView.getItemAtPosition(i);
                String name=tmp.get("name").toString();
                Intent intent=new Intent(scan_list.this,show_markdown.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                Map<String,Object> tmp= (Map<String, Object>) adapterView.getItemAtPosition(position);
                name=tmp.get("name").toString();
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(scan_list.this);
                alertDialog.setTitle("删除");
                alertDialog.setMessage("确认删除吗？");
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        connect1(name);
                        Log.v(name,""+position);
                        data.remove(position);
                        adapter.notifyDataSetChanged();
                        for(int j=0;j<data.size();j++){
                            Map<String,Object> tmp=data.get(j);
                            Log.v(tmp.get("name").toString(),"na");
                        }
                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
                return true;
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void returnBitMap(final int i, final String url) {
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL myFileUrl = null;
                    bitmap = null;
                    try {
                        myFileUrl = new URL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Log.e("error", "1");
                    }
                    try {
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        Log.v(url, "url");
                        InputStream is = conn.getInputStream();
//                    InputStream is=new URL(url).openStream();
                        bitmap = BitmapFactory.decodeStream(is);
                        Message msg = new Message();
                        msg.what = UPDATE_PIC;
                        msg.obj = bitmap;
                        msg.arg1 = i;
                        handler.sendMessage(msg);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("error", "2");
                    }
                }
            }).start();
        }
        else{
            Toast.makeText(getApplicationContext(),"当前没有可用网络！",Toast.LENGTH_SHORT).show();
        }
//        return bitmap;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void connect(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        printList(username);
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

    @Override
    protected void onResume() {

        connect();
        Log.v("resume","2");
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){
                    case UPDATE_CONTENT:
                        data.clear();
                        Log.v((String)msg.obj,"123");
                        if((String)msg.obj!=null){
                            String content[]=((String) msg.obj).split(" ");
                            Log.v(String.valueOf(content.length),"sad");
                            Log.v(content[0],"asd");
                            int num=0;
                            for(int i=0;i<content.length&&content.length>2;i+=4,num++){
                                Map<String ,Object> tmp=new HashMap<>();
//                                bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.beijing);
                                returnBitMap(num,"http://101.200.59.74:8080/androidpro/file/"+content[i+3]);
                                tmp.put("name",content[i]);
                                tmp.put("date",content[i+2]);
                                Log.v(content[i+3],"content4");
                                tmp.put("img",bitmap);
                                data.add(tmp);
                            }

                        }
                        break;
                    case UPDATE_PIC:
                        data.get(msg.arg1).put("img",msg.obj);
                        break;

                    default:break;
                }
                adapter.notifyDataSetChanged();
            }
        };
        super.onResume();
    }

    private void printList(String username) throws IOException {
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
//            Log.v(name,"name");
//            name= URLEncoder.encode(name,"utf-8");
            out.writeBytes("username="+username);
            InputStream in=connection.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder=new StringBuilder();
            String line;
            while (((line=reader.readLine()))!=null){
                stringBuilder.append(line);
            }
            str=stringBuilder.toString();
//            out.close();
            in.close();

            Message message=new Message();
            message.what=UPDATE_CONTENT;
            message.obj=str;
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
    public void connect1(final String name){
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
            URL url = new URL(urlString2);
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
            message.what=DELETE_CONTENT;
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.time_tree) {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.bill) {
            Intent intent=new Intent(this,activity_bill.class);
            startActivity(intent);

        } else if (id == R.id.nav_edit) {
//            Intent intent=new Intent(this,scan_list.class);
//            startActivity(intent);

        }
        else if(id==R.id.nav_instruction){
            Intent intent=new Intent(this,markdown_instruction.class);
            startActivity(intent);
        }
        else if(id==R.id.signout){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
