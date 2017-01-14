package com.example.baodi.innovation2.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baodi.innovation2.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class activity_bill extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String url4search = "http://101.200.59.74:8080/myservlet/Bill_search";
    private static final String url4delete = "http://101.200.59.74:8080/myservlet/Bill_delete";
    private static final String url4update = "http://101.200.59.74:8080/myservlet/Bill_update";
    private List<Map<String,String>> data;
    private List<Integer>data4Statistic = new ArrayList<>();
    private static final int UPDATE_CONTENT= 0;
    private static final String id = "0";
    private ListView listview;
    private SimpleAdapter simpleAdapter;
    private SearchView mSearchView;
    private String str4month;
    private Button Statistic;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_bill.this,BillActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SimpleDateFormat formatter = new SimpleDateFormat ("MM");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        str4month = formatter.format(curDate);

        listview=(ListView) findViewById(R.id.list);
        listview.getBackground().setAlpha(100);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        Statistic = (Button) findViewById(R.id.Statistic);
        data = new ArrayList<>();
        data4Statistic.add(0); //总收入
        data4Statistic.add(0); //总支出
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                str4month = query;
                mSearchView.clearFocus();
                data = new ArrayList<>();
                sendRequestWithHttpURLConnection();
                return true;
            }
            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null&& networkInfo.isConnected()){
            sendRequestWithHttpURLConnection();
        }
        else{
            Toast.makeText(getApplicationContext()
                    , "当前没有可用网络！", Toast.LENGTH_SHORT).show();
        }

//        addbutton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(),BillActivity.class);
//                startActivity(intent);
//            }
//        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l){
                LayoutInflater factory = LayoutInflater.from(getApplicationContext());
                View views = factory.inflate(R.layout.dialogactivity,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_bill.this);
                builder.setView(views);
                final TextView editanother = (TextView) views.findViewById(R.id.editanother);

                editanother.setText(data.get(i).get("another"));
                builder.show();
            }
        });

        Statistic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(getApplicationContext());
                View views = factory.inflate(R.layout.dialog4statistic,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity_bill.this);
                builder.setView(views);
                final TextView sta1 = (TextView) views.findViewById(R.id.sta1);
                final TextView sta2 = (TextView) views.findViewById(R.id.sta2);
                final TextView sta3 = (TextView) views.findViewById(R.id.sta3);
                sta1.setText(data4Statistic.get(1)+"");
                sta2.setText(data4Statistic.get(0)+"");
                int num = data4Statistic.get(0)-data4Statistic.get(1);
                sta3.setText(num+"");
                builder.show();
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity_bill.this);
                alertDialog.setMessage("是否删除？");
                alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //删除数据库中的信息
                        sendDeleteWithHttpURLConnection(data.get(i));
                        //删除列表中的信息
                        data.remove(i);
                        simpleAdapter.notifyDataSetChanged();
                    }
                });
                alertDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                return true;
            }
        });
    }
    private void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                //http请求操作
                HttpURLConnection conn = null;
                DataOutputStream out = null;
                String response = null;
                data4Statistic.set(0,0); //总收入
                data4Statistic.set(1,0); //总支出
                try{
                    System.out.println("666");
                    URL url = new URL(url4search);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();

                    out = new DataOutputStream(conn.getOutputStream());
                    out.write(new String("id="+id+"&time4month="+str4month).getBytes());
                    System.out.println(new String("id="+id+"&time4month="+str4month));
                    out.close();

                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    response = "";
                    String line;
                    while((line = reader.readLine())!=null){
                        response+=line;
                    }
                    System.out.println(response);
                    reader.close();
                }catch (Exception e){
                    System.out.println("error");
                    e.printStackTrace();
                }finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                Message message = Message.obtain();
                message.what = UPDATE_CONTENT;
                //将XML字符串放入message中
                message.obj = getdatafromstring(response);
                //传递message
                handler4search.sendMessage(message);
            }
        }).start();
    }

    private List getdatafromstring(String response){
        List<String>list = new ArrayList<>();
        if(response.equals("")){
            return list;
        }
        String[] str = response.split(";");
        for(String j:str){
            list.add(j);
        }
        return list;
    }
    private Handler handler4delete = new Handler(){
        public void handleMessage(Message message){

        }
    };
    private Handler handler4search = new Handler(){
        public void handleMessage(Message message){
            switch (message.what) {
                case UPDATE_CONTENT:
                    //listview
                    List<String> list = (List) message.obj;
                    String[] part;
                    int len = list.size();

                    for (int i = 0; i < len; i++) {
                        part = list.get(i).split(",");
                        Map<String, String> map = new LinkedHashMap<>();
                        map.put("day", part[0] + "号");
                        map.put("num", part[1] + part[2]);

                        if (part[1].equals("+")) {
                            data4Statistic.set(0,data4Statistic.get(0) + Integer.parseInt(part[2]));
                            switch (part[3]) {
                                case "1":
                                    part[3] = "工资";
                                    break;
                                case "2":
                                    part[3] = "还钱";
                                    break;
                                case "3":
                                    part[3] = "红包";
                                    break;
                                case "4":
                                    part[3] = "股票";
                                    break;
                                case "5":
                                    part[3] = "彩票";
                                    break;
                                default:
                                    part[3] = "其他";
                                    break;
                            }
                        } else {
                            data4Statistic.set(1,data4Statistic.get(1) + Integer.parseInt(part[2]));
                            switch (part[3]) {
                                case "1":
                                    part[3] = "网络";
                                    break;
                                case "2":
                                    part[3] = "饮食";
                                    break;
                                case "3":
                                    part[3] = "游戏";
                                    break;
                                case "4":
                                    part[3] = "缴费";
                                    break;
                                case "5":
                                    part[3] = "出行";
                                    break;
                                default:
                                    part[3] = "其他";
                                    break;
                            }
                        }
                        map.put("style", part[3]);
                        map.put("another", part[4]);
                        data.add(map);
                    }
                    simpleAdapter = new SimpleAdapter(
                            getApplicationContext(), data, R.layout.listitem, new String[]{"day", "num", "style"},
                            new int[]{R.id.text1, R.id.text2, R.id.text3});
                    listview.setAdapter(simpleAdapter);
                    break;
                default:
                    break;
            }
        }
    };

    private void sendDeleteWithHttpURLConnection(final Map<String,String> data4delete){
        new Thread(new Runnable(){
            @Override
            public void run(){
                //http请求操作
                HttpURLConnection conn = null;
                DataOutputStream out = null;
                String response = null;
                try{
                    URL url = new URL(url4delete);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();

                    out = new DataOutputStream(conn.getOutputStream());
                    System.out.println(data4delete.get("another"));

                    //获取表单数据
                    String another = URLEncoder.encode(URLEncoder.encode(data4delete.get("another"),"UTF-8"));
                    out.write(new String("id="+id
                            +"&time4day="+data4delete.get("day").substring(0,data4delete.get("day").length()-1)
                            +"&another="+another).getBytes());
                    System.out.println(new String("id="+id
                            +"&time4day="+data4delete.get("day").substring(0,data4delete.get("day").length()-1)
                            +"&another="+another));
                    out.close();
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    response = "";
                    String line;
                    while((line = reader.readLine())!=null){
                        response+=line;
                    }
                    reader.close();
                    Toast.makeText(activity_bill.this,response,Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    System.out.println("error");
                    e.printStackTrace();
                }finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                Message message = Message.obtain();
                message.what = UPDATE_CONTENT;
                //将XML字符串放入message中
                message.obj = "0";
                //传递message
                handler4delete.sendMessage(message);
            }
        }).start();
    }
    private void sendupdateWithHttpURLConnection(final String change,final Map<String,String> data4update){
        new Thread(new Runnable(){
            @Override
            public void run(){
                //http请求操作
                HttpURLConnection conn = null;
                DataOutputStream out = null;
                String response = null;
                try{
                    System.out.println("666");
                    URL url = new URL(url4update);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();

                    out = new DataOutputStream(conn.getOutputStream());
                    //获取系统时间
                    SimpleDateFormat formatter = new SimpleDateFormat ("MM");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);

                    out.write(new String("id="+id
                            +"&time4day="+data4update.get("day").substring(0,data4update.get("day").length()-2)
                            +"&another="+URLEncoder.encode(URLEncoder.encode(data4update.get("another"),"UTF-8"))
                            +"&change="+change).getBytes());
                    out.close();
                }catch (Exception e){
                    System.out.println("error");
                    e.printStackTrace();
                }finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
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
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.bill) {
//            Intent intent=new Intent(this,activity_bill.class);
//            startActivity(intent);

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
