package com.example.baodi.innovation2.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.baodi.innovation2.R;
import com.example.baodi.innovation2.main.MainActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 23147 on 2016/12/15.
 */

public class LoginActivity extends Activity {
    private EditText username;//输入框中的用户名
    private EditText userpassword;//输入框中的密码
    private CheckBox remember;//记住密码
    private CheckBox autologin;//要不要自动登录
    private Button login;//登陆按钮
    private Button register;//注册按钮
    public int user_id;//用户id，每一个注册用户都有一个id
    private SharedPreferences sp;//用作应用内数据共享
    Handler handler;
    String str;
    static public String userNameValue=new String();
    String passwordValue;
    String urlString="http://101.200.59.74:8080/androidpro/login";
    private static final int LOGIN_CONTENT=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);//首先打开登录界面

        // 初始化用户名、密码、记住密码、自动登录、登录按钮、注册按钮
        username = (EditText) findViewById(R.id.username);
        userpassword = (EditText) findViewById(R.id.userpassword);
        remember = (CheckBox) findViewById(R.id.remember);
        autologin = (CheckBox) findViewById(R.id.autologin);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        //有没有曾经登陆过的消息？有没有勾选上记住密码或者自动登录？
        sp = getSharedPreferences("userInfo", 0);
        String name=sp.getString("USER_NAME", "");
        final String pass =sp.getString("PASSWORD", "");
        boolean choseRemember =sp.getBoolean("remember", false);
        boolean choseAutoLogin =sp.getBoolean("autologin", false);

        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if(choseRemember){
            username.setText(name);
            userpassword.setText(pass);
            remember.setChecked(true);
        }
        //如果上次登录选了自动登录，那进入登录页面也自动勾选自动登录，但是需要确认
        if(choseAutoLogin){
            autologin.setChecked(true);
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("提示").
                    setMessage("您确认自动登录吗？").
                    setIcon(R.drawable.touxiang).
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }).
                    setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            autologin.setChecked(false);
                            dialog.dismiss();
                        }
                    }).
                    create();
            alertDialog.show();
        }
        register.setOnClickListener(new OnClickListener() {
            //注册账号
            @Override
            public void onClick(View arg0) {
                userNameValue = username.getText().toString();
                passwordValue = userpassword.getText().toString();
                Toast.makeText(LoginActivity.this, "注册成功！",
                        Toast.LENGTH_SHORT).show();
                connect(userNameValue,passwordValue);
            }
        });
        login.setOnClickListener(new OnClickListener() {
            //登陆
            @Override
            public void onClick(View arg0) {
                userNameValue = username.getText().toString();
                passwordValue = userpassword.getText().toString();
                connect(userNameValue,passwordValue);
            }
        });
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LOGIN_CONTENT:
                        SharedPreferences.Editor editor =sp.edit();
                        if (msg.obj.toString().equals("true")) {
                            Toast.makeText(LoginActivity.this, "登录成功",
                                    Toast.LENGTH_SHORT).show();
                            //保存用户名和密码
                            editor.putString("USER_NAME", userNameValue);
                            editor.putString("PASSWORD", passwordValue);
                            //是否记住密码
                            if(remember.isChecked()){
                                editor.putBoolean("remember", true);
                            }else{
                                editor.putBoolean("remember", false);
                            }
                            //是否自动登录
                            if(autologin.isChecked()){
                                editor.putBoolean("autologin", true);
                            }else{
                                editor.putBoolean("autologin", false);
                            }
                            editor.commit();
                            //跳转
                            Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            if(userNameValue.equals("")||passwordValue.equals("")) {
                                Toast.makeText(LoginActivity.this, "用户名或密码为空，请填写完整!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新登录!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }
    public void connect(final String name, final String password){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectNetwork(name,password);
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
    private void connectNetwork(String name,String password) throws IOException {
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
            Log.v(name,"name");
            name= URLEncoder.encode(name,"utf-8");
            out.writeBytes("username="+name+"&password="+password);
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
            message.what=LOGIN_CONTENT;
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
}
