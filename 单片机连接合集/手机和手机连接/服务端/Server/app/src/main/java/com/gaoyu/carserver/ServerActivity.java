package com.gaoyu.carserver;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {
    //正在连接
    private static final int STATUS_CONNECTING = 1;
    //连接成功
    private static final int STATUS_CONNECT = 2;
    //返回消息
    private static final int STATUS_ACCEPT = 3;

    public static final int REQUEST_OPEN = 1;//打开一个蓝牙
    //一些服务器的名字
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //蓝牙设备
    private BluetoothAdapter mBluetoothAdapter;
    // 蓝牙服务端socket
    private BluetoothServerSocket mServerSocket;
    // 蓝牙客户端socket
    private BluetoothSocket mSocket;
    // 线程类

    private ReadThread mReadThread;
    private static final String TAG = "BluetoothChatService";
    private Button btn_openServer;
    private Button btn_closeServer;
    private TextView tv_accept;

    MediaPlayer mediaPlayer1 = null;
    private Spinner spinner_car_control;
    private String serverAdress;
    private ServerThread mServerThread;
    private BluetoothDevice mDevice;
    private ArrayList<BlueToothDeviceBean> mDatas;
    private MySpinnerAdapter mySpinnerAdapter;
    private Button btn_Control_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        registerBroadcast();
        initDatas();
        init();
    }
    private void initDatas() {
        mDatas = new ArrayList<BlueToothDeviceBean>();
        mySpinnerAdapter = new MySpinnerAdapter(this, mDatas);
        //实例化蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断蓝牙功能呢是否存在
        if (mBluetoothAdapter == null) {
            showToast("无蓝牙模块");
            return;
        }
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        //设备被发现广播  蓝牙扫描时，扫描到任一远程蓝牙设备时，会发送此广播。给onreceive
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, discoveryFilter);

        // 设备发现完成  蓝牙扫描过程结束
        IntentFilter foundFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, foundFilter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获得设备信息
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果绑定的状态不一样(判断给定地址下的device是否已经配对)
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mDatas.add(new BlueToothDeviceBean(device.getName() + "\n" + device.getAddress(), false));
                    mySpinnerAdapter.notifyDataSetChanged();
                }
                // 如果搜索完成了
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                if (spinner_car_control.getCount() == 0) {
                    mDatas.add(new BlueToothDeviceBean("没有发现蓝牙设备", false));
                    mySpinnerAdapter.notifyDataSetChanged();
                }
                mySpinnerAdapter.notifyDataSetChanged();
                btn_Control_search.setText("重新搜索");
            }
        }
    };








    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_openServer:
                //开启服务器
                //创建服务线程
                mServerThread = new ServerThread();
                mServerThread.start();
                break;
            case R.id.btn_closeServer:
                //关闭服务器
                shutdownServer();
                Intent intent = new Intent(this,ServerActivity.class);
                startActivity(intent);
                showToast("服务器已关闭");
                finish();
                break;

            case R.id.btn_Control_search:
                //是否正在处于扫描过程中。
                if (mBluetoothAdapter.isDiscovering()) {

                    mBluetoothAdapter.cancelDiscovery();
                    btn_Control_search.setText("重新搜索");

                } else {
                    mDatas.clear();
                    mySpinnerAdapter.notifyDataSetChanged();
                    //进行加载设备初始化数据
                    getDeviceInfo();
                    /* 开始搜索 */
                    mBluetoothAdapter.startDiscovery();
                    btn_Control_search.setText("ֹͣ停止搜索");
                }

                break;
        }

    }
    public void musicPlay(String tt){
        char[] oo=tt.toCharArray();
        for(int i=0;i<tt.length();i++){
            if(oo[i]=='1'){
                mediaPlayer1 = MediaPlayer.create(this, R.raw.a);
                mediaPlayer1.start();

            }else if(oo[i]=='2'){
                mediaPlayer1 = MediaPlayer.create(this, R.raw.b);
                mediaPlayer1.start();
            }else if(oo[i]=='3'){
                mediaPlayer1 = MediaPlayer.create(this, R.raw.c);
                mediaPlayer1.start();
            }else if(oo[i]=='4'){
                mediaPlayer1 = MediaPlayer.create(this, R.raw.d);
                mediaPlayer1.start();
            }else if(oo[i]=='5'){
                mediaPlayer1 = MediaPlayer.create(this, R.raw.e);
                mediaPlayer1.start();
            }else if(oo[i]=='6'){
                mediaPlayer1 = MediaPlayer.create(this, R.raw.f);
                mediaPlayer1.start();
            }else if(oo[i]=='7'){
                mediaPlayer1 = MediaPlayer.create(this, R.raw.g);
                mediaPlayer1.start();
            }
        }
    }


    // 开启服务器
    private class ServerThread extends Thread {
        public void run() {
            try {
                // 创建一个蓝牙服务器 参数分别：服务器名称、UUID
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
                        MY_UUID);

                Message msg = new Message();
                msg.obj = "请稍候，正在等待客户端的连接...";
                msg.what = STATUS_CONNECTING;
                mHandler.sendMessage(msg);
                //服务端接受
                mSocket = mServerSocket.accept();

                msg = new Message();
                msg.obj = "客户端已经连接上！可以发送指令。";
                msg.what = STATUS_CONNECT;
                mHandler.sendMessage(msg);
                // 启动接受数据
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 停止服务器
    private void shutdownServer() {
        new Thread() {
            public void run() {
                if (mServerThread != null) {
                    mServerThread.interrupt();
                    mServerThread = null;
                }
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                try {
                    if (mSocket != null) {
                        mSocket.close();
                        mSocket = null;
                    }
                    if (mServerSocket != null) {
                        mServerSocket.close();
                        mServerSocket = null;
                    }
                } catch (IOException e) {
                    Log.e("server", "mserverSocket.close()", e);
                }
            }
        }.start();
    }
    public synchronized void connect(BluetoothDevice device) {


        // Start the thread to connect with the given device


    }



    private void init() {
        //初始化UI
        tv_accept = (TextView) findViewById(R.id.tv_accept);
        btn_openServer = (Button) findViewById(R.id.btn_openServer);
        btn_closeServer = (Button) findViewById(R.id.btn_closeServer);
        btn_openServer.setOnClickListener(this);
        btn_closeServer.setOnClickListener(this);
        //初始化蓝牙
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btn_Control_search = (Button) findViewById(R.id.btn_Control_search);
        spinner_car_control = (Spinner) findViewById(R.id.Spinner_car_Control);
        spinner_car_control.setAdapter(mySpinnerAdapter);
        //加标题
        spinner_car_control.setPrompt("请选择相应设备");
        //条目点击
        spinner_car_control.setOnItemSelectedListener(new SpinnerOnSelectedListener());
        btn_Control_search.setOnClickListener(this);

    }

    /**
     * 信息处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String info = (String) msg.obj;
            switch (msg.what) {
                case STATUS_CONNECT:
                    //吐司出 连接状态
                    showToast(info);
                    break;
                case STATUS_CONNECTING:
                    //吐司出 连接状态
                    showToast(info);
                    break;
                case STATUS_ACCEPT:
                    //吐司出 连接状态
                    tv_accept.setText(info);
                    break;
            }
        }

    };

    /**
     * 读取数据
     */
    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = null;
            try {
                is = mSocket.getInputStream();
                while (true) {
                    if ((bytes = is.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        musicPlay(s);
                        Message msg = new Message();
                        msg.obj = s;
                        msg.what = STATUS_ACCEPT;
                        mHandler.sendMessage(msg);
                        //同样  子线程不能刷新UI
                        //tv_accept.setText(s);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

    /**
     * 吐司
     *
     * @param s
     */
    public void showToast(String s) {
        //设置吐司显示位置
        Toast toast = Toast.makeText(ServerActivity.this, s, Toast.LENGTH_SHORT);
        //设置偏移量
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }



    @Override
    public void onStart() {
        super.onStart();
        //判断蓝牙是否打开
        if (mBluetoothAdapter.isEnabled()) {
            showToast("蓝牙已经打开了");
        } else {
            //强制打开蓝牙
                    /*boolean isOpen = mBluetoothAdapter.enable();
                    showToast("" + isOpen);*/
            //调用action打开（start Activity for result）
            Intent open = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(open, REQUEST_OPEN);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        shutdownServer();
    }

    /**
     * 条目点击
     */
    private class SpinnerOnSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int i,
                                   long arg3) {
            Log.e("gaoyu", "onItemSelected");
            BlueToothDeviceBean bean = mDatas.get(i);
            String info = bean.message;
            String address = info.substring(info.length() - 17);

            serverAdress = address;

            AlertDialog.Builder stopDialog = new AlertDialog.Builder(ServerActivity.this);
            stopDialog.setTitle("连接");//标题
            stopDialog.setMessage(bean.message);
            stopDialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mBluetoothAdapter.cancelDiscovery();
                    btn_Control_search.setText("重新搜索");
                    //进行连接
                    if (!"".equals(serverAdress)) {
                        mDevice = mBluetoothAdapter.getRemoteDevice(serverAdress);
                        //打开客户端线程

                        ConnectThread mConnectThread = new ConnectThread();
                         mConnectThread.start();
                        //mServerThread = new ServerThread();
                        //mServerThread.start();
                    } else {
                        showToast("地址为空");
                    }
                    dialog.cancel();
                }
            });
            stopDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            stopDialog.show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    private void getDeviceInfo() {

        //根据适配器得到所有的设备信息
        Set<BluetoothDevice> deviceSet = mBluetoothAdapter.getBondedDevices();
        Log.e("gaoyu", "里面的设备个数" + deviceSet.size());
        if (deviceSet.size() > 0) {
            for (BluetoothDevice device : deviceSet) {
                mDatas.add(new BlueToothDeviceBean(device.getName() + "\n" + device.getAddress(), true));
                //动态刷新
                mySpinnerAdapter.notifyDataSetChanged();
            }
        } else {
            mDatas.add(new BlueToothDeviceBean("没有配对的设备", true));
            mySpinnerAdapter.notifyDataSetChanged();
        }
    }


}
