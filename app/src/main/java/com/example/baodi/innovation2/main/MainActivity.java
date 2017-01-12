package com.example.baodi.innovation2.main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baodi.innovation2.R;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Chronometer chronometer;
    private long countDownTime = 0;
    private boolean failed = false;
    private boolean counting = false;
    LoginActivity loginActivity;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=loginActivity.userNameValue;
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.nav_header_main,null);
        TextView user=(TextView) view.findViewById(R.id.username);
//        user.setText(username);
        Log.v(user.getText().toString(),"tostring");
        user.setText(username);
        Log.v(user.getText().toString(),"tostring"+username);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView time_remaining = (TextView) findViewById(R.id.countDownTimer);
        final Button start = (Button) findViewById(R.id.start);
        View five_layout = (View) findViewById(R.id.five_layout);
        View ten_layout = (View) findViewById(R.id.ten_layout);
        View twenty_layout = (View) findViewById(R.id.twenty_layout);

        five_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(failed==true){
                    time_remaining.setText("再挑战一次自己？");
                    start.setText("我忏悔");
                }
                else if(counting==true){
                    Toast.makeText(getApplicationContext(),"远离手机，专注生活",Toast.LENGTH_SHORT).show();
                }
                else{
                    countDownTime += 300;
                    setTime(countDownTime);
                }

            }
        });

        ten_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(failed==true){
                    time_remaining.setText("再挑战一次自己？");
                    start.setText("我忏悔");
                }
                else if(counting==true){
                    Toast.makeText(getApplicationContext(),"远离手机，专注生活",Toast.LENGTH_SHORT).show();
                }
                else{
                    countDownTime += 600;
                    setTime(countDownTime);
                }
            }
        });

        twenty_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(failed==true){
                    time_remaining.setText("再挑战一次自己？");
                    start.setText("我忏悔");
                }
                else if(counting==true){
                    Toast.makeText(getApplicationContext(),"远离手机，专注生活",Toast.LENGTH_SHORT).show();
                }
                else{
                    countDownTime += 1200;
                    setTime(countDownTime);
                }
            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start.getText().toString().equals("我忏悔")){
                    start.setText("重新做人");
                }
                else if(start.getText().toString().equals("重新做人")){
                    start.setText("开始计时");
                    countDownTime = 0;
                    failed = false;
                    setTime(countDownTime);
                }
                else{
                    if(counting==true){
                        Toast.makeText(getApplicationContext(),"远离手机，专注生活",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(countDownTime==0){
                            Toast.makeText(getApplicationContext(),"请选择要专注的时间",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            startCountDownTime(countDownTime);
                            counting = true;
                        }
                    }

                }

            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(),activity_editor_markdown.class);
//                startActivity(intent);
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void startCountDownTime(final long time){
        final com.example.baodi.innovation2.main.CountDownTimer timer = new com.example.baodi.innovation2.main.CountDownTimer(time*1000 , time) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(failed==true) this.cancel();
                Log.i("CountDown:", "time remaining: " + millisUntilFinished / 1000);
                setTime(millisUntilFinished / 1000);
                isBackground(getApplicationContext());
                if(isBackground(getApplicationContext())){
                    failed = true;
                    counting = false;
                    TextView time_remaining = (TextView) findViewById(R.id.countDownTimer);
                    Button restart = (Button) findViewById(R.id.start);
                    restart.setText("重新做人");
                    time_remaining.setText("放下手机，专注生活");
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {
                TextView time_remaining = (TextView) findViewById(R.id.countDownTimer);
                time_remaining.setText("成功啦！");
                counting = false;
            }
        };
        timer.start();
    }


    public void setTime(long seconds){
        TextView time_remaining = (TextView) findViewById(R.id.countDownTimer);
        long minute = seconds / 60;
        long second = seconds % 60;
        if(second>=10){
            time_remaining.setText(minute+":"+second);
        }
        else{
            time_remaining.setText(minute+":0"+second);
        }

    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName()); */
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.time_tree) {
//            Intent intent=new Intent(this,MainActivity.class);
//            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.bill) {
            Intent intent=new Intent(this,activity_bill.class);
            startActivity(intent);

        } else if (id == R.id.nav_edit) {
            Intent intent=new Intent(this,scan_list.class);
            startActivity(intent);

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
