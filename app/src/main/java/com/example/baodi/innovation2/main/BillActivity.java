package com.example.baodi.innovation2.main;

/**
 * Created by baodi on 2016/12/15.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import java.util.*;
import java.text.*;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BillActivity extends AppCompatActivity {
    private RadioButton rb;
    private RadioGroup group;
    private Button resetbutton;
    private Button submitbutton;
    private EditText editmoney;
    private EditText editanother;
    private ImageView returnsign;
    private static final String myurl = "http://101.200.59.74:8080/myservlet/Bill";// ?
    private static final int UPDATE_CONTENT= 0;
    private String spintext1="8";
    private String spintext2="今天";
    private String spintext3="网络";
    private String moneytext="0";
    private String anothertext="";
    private String radiotext="支出";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_add);

        group = (RadioGroup) findViewById(R.id.group);
        resetbutton = (Button)findViewById(R.id.resetbutton);
        editmoney  = (EditText) findViewById(R.id.editmoney);
        editanother = (EditText) findViewById(R.id.editanother);
        submitbutton = (Button)findViewById(R.id.submitbutton);
        returnsign = (ImageView) findViewById(R.id.returnsign) ;
        //reset
        ResetallData();
        //获取系统时间
        SimpleDateFormat formatter = new SimpleDateFormat ("MM");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        int i=Integer.parseInt(str);
        //月份spin------------>begin
        List<String> list4month = new ArrayList<String>();
        list4month.add(String.valueOf(i));
        list4month.add(String.valueOf(i-1));
        list4month.add(String.valueOf(i-2));
        list4month.add(String.valueOf(i-3));
        list4month.add(String.valueOf(i-4));
        ArrayAdapter<String> adapter4month = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list4month);
        adapter4month.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Spinner sp4month = (Spinner) findViewById(R.id.Spinner4month);
        sp4month.setAdapter(adapter4month);
        sp4month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // parent： 为控件Spinner   view：显示文字的TextView   position：下拉选项的位置从0开始
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取Spinner控件的适配器
                ArrayAdapter<String> adapter4month = (ArrayAdapter<String>) parent.getAdapter();
                spintext1 = adapter4month.getItem(position);
            }
            //没有选中时的处理
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //--------------->  end

        //天spin------------>begin
        List<String> list4day = new ArrayList<String>();
        list4day.add("今天");
        list4day.add("昨天");
        list4day.add("前天");
        ArrayAdapter<String> adapter4day = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list4day);
        adapter4day.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Spinner sp4day = (Spinner) findViewById(R.id.Spinner4day);
        sp4day.setAdapter(adapter4day);
        sp4day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // parent： 为控件Spinner   view：显示文字的TextView   position：下拉选项的位置从0开始
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取Spinner控件的适配器
                ArrayAdapter<String> adapter4day = (ArrayAdapter<String>) parent.getAdapter();
                //获取系统时间
                SimpleDateFormat formatterday = new SimpleDateFormat ("dd");
                Date curDateday = new Date(System.currentTimeMillis());//获取当前时间
                String strday = formatterday.format(curDateday);
                int i4day=Integer.parseInt(strday);
                spintext2 = adapter4day.getItem(position);
                if(spintext2.equals("今天")){
                    spintext2 = String.valueOf(i4day);
                }
                else if(spintext2.equals("昨天")){
                    spintext2 = String.valueOf(i4day-1);
                }
                else{
                    spintext2 = String.valueOf(i4day-2);
                }
            }
            //没有选中时的处理
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //--------------->  end

        //消费类型spin------------>begin
        List<String> list4style = new ArrayList<String>();
        list4style.add("网络");
        list4style.add("饮食");
        list4style.add("游戏");
        list4style.add("缴费");
        list4style.add("出行");
        list4style.add("其他");
        ArrayAdapter<String> adapter4style = new ArrayAdapter<String>(BillActivity.this, android.R.layout.simple_spinner_item, list4style);
        adapter4style.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Spinner sp4style = (Spinner) findViewById(R.id.Spinner4style);
        sp4style.setAdapter(adapter4style);
        sp4style.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // parent： 为控件Spinner   view：显示文字的TextView   position：下拉选项的位置从0开始
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取Spinner控件的适配器
                ArrayAdapter<String> adapter4style = (ArrayAdapter<String>) parent.getAdapter();
                spintext3 = adapter4style.getItem(position);
            }
            //没有选中时的处理
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //--------------->  end

        //radiogroup
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                rb = (RadioButton) findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                TextView text4style = (TextView) findViewById(R.id.text4style);
                if(rb.getText().equals("收入")){
                    text4style.setText("收入类型：");
                    //收入类型spin------------>begin
                    List<String> list4style = new ArrayList<String>();
                    list4style.add("工资");
                    list4style.add("还钱");
                    list4style.add("红包");
                    list4style.add("股票");
                    list4style.add("彩票");
                    list4style.add("其他");
                    ArrayAdapter<String> adapter4style = new ArrayAdapter<String>(BillActivity.this, android.R.layout.simple_spinner_item, list4style);
                    adapter4style.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                    Spinner sp4style = (Spinner) findViewById(R.id.Spinner4style);
                    sp4style.setAdapter(adapter4style);
                    sp4style.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        // parent： 为控件Spinner   view：显示文字的TextView   position：下拉选项的位置从0开始
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //获取Spinner控件的适配器
                            ArrayAdapter<String> adapter4style = (ArrayAdapter<String>) parent.getAdapter();
                            spintext3 = adapter4style.getItem(position);
                        }
                        //没有选中时的处理
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    //--------------->  end
                }
                else{
                    text4style.setText("消费类型：");
                    //消费类型spin------------>begin
                    List<String> list4style = new ArrayList<String>();
                    list4style.add("网络");
                    list4style.add("饮食");
                    list4style.add("游戏");
                    list4style.add("缴费");
                    list4style.add("出行");
                    list4style.add("其他");
                    ArrayAdapter<String> adapter4style = new ArrayAdapter<String>(BillActivity.this, android.R.layout.simple_spinner_item, list4style);
                    adapter4style.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                    Spinner sp4style = (Spinner) findViewById(R.id.Spinner4style);
                    sp4style.setAdapter(adapter4style);
                    sp4style.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        // parent： 为控件Spinner   view：显示文字的TextView   position：下拉选项的位置从0开始
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //获取Spinner控件的适配器
                            ArrayAdapter<String> adapter4style = (ArrayAdapter<String>) parent.getAdapter();
                            spintext3 = adapter4style.getItem(position);
                        }
                        //没有选中时的处理
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    //--------------->  end
                }
            }
        });

        //提交表单内容
        submitbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null&& networkInfo.isConnected()){
                    sendRequestWithHttpURLConnection();
                }
                else{
                    Toast.makeText(BillActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        returnsign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BillActivity.this.finish();
            }
        });
    }

    private void ResetallData(){
        resetbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                editmoney.setText("");
                editanother.setText("");
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
                try{
                    URL url = new URL(myurl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();

                    out = new DataOutputStream(conn.getOutputStream());
                    //获取表单数据
                    moneytext = editmoney.getText().toString();
                    anothertext = editanother.getText().toString();
                    rb = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                    radiotext = rb.getText().toString();
                    System.out.println("666");
                    if(radiotext.equals("收入")){
                        radiotext = "+";
                        switch (spintext3) {
                            case "工资":
                                spintext3 = "1";
                                break;
                            case "还钱":
                                spintext3 = "2";
                                break;
                            case "红包":
                                spintext3 = "3";
                                break;
                            case "股票":
                                spintext3 = "4";
                                break;
                            case "彩票":
                                spintext3 = "5";
                                break;
                            default:
                                spintext3 = "6";
                                break;
                        }
                    }
                    else{
                        radiotext = "-";
                        switch (spintext3) {
                            case "网络":
                                spintext3 = "1";
                                break;
                            case "饮食":
                                spintext3 = "2";
                                break;
                            case "游戏":
                                spintext3 = "3";
                                break;
                            case "缴费":
                                spintext3 = "4";
                                break;
                            case "出行":
                                spintext3 = "5";
                                break;
                            default:
                                spintext3 = "6";
                                break;
                        }
                    }
                    spintext1 = URLEncoder.encode(spintext1,"UTF-8");
                    spintext2 = URLEncoder.encode(spintext2,"UTF-8");
                    spintext3 = URLEncoder.encode(spintext3,"UTF-8");
                    moneytext = URLEncoder.encode(moneytext,"UTF-8");
                    anothertext = URLEncoder.encode(URLEncoder.encode(anothertext,"UTF-8"));//encoding两次！！！
                    radiotext = URLEncoder.encode(radiotext,"UTF-8");
                    out.write(new String("id="+"0"+"&time4month="+spintext1
                            +"&time4day="+spintext2
                            +"&style="+spintext3
                            +"&plus_minus="+radiotext
                            +"&number="+moneytext
                            +"&another="+anothertext).getBytes());
                    System.out.println("id="+"0"+"&time4month="+spintext1
                            +"&time4day="+spintext2
                            +"&style="+spintext3
                            +"&plus_minus="+radiotext
                            +"&number="+moneytext
                            +"&another="+anothertext);
                    out.close();

                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    response = "";
                    String line;
                    while((line = reader.readLine())!=null){
                        response+=line;
                    }
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
                message.obj = response;
                //传递message
                handler.sendMessage(message);
            }
        }).start();
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case UPDATE_CONTENT:
                    String response = (String)message.obj;
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(BillActivity.this);
                    alertDialog.setMessage(response);
                    alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            BillActivity.this.finish();
                        }
                    });
                    alertDialog.show();
                    break;
                default:
                    break;
            }
        }
    };
}
