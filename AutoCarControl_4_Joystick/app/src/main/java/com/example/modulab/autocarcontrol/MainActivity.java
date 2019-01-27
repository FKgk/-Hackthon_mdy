package com.example.modulab.autocarcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //1 code is added -->
    private int task_state = 0;
    TextView mMainText;
    EditText mNumEdit1;
    TcpThread mThread;
    //1 code is added <--

    //2 code is added -->
    private static final int RECOGNIZER = 1001;
    TextView mVoiceText;
    TextView mBackText;
    //2 code is added <--

    //4 code is added -->
    TextView mJoyText;
    private int joy_state = 0;
    private com.MobileAnarchy.Android.Widgets.Joystick.JoystickView joystick;
    //4 code is added <--

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1 code is added -->
        mMainText = (TextView)findViewById(R.id.connectText);
        mNumEdit1 = (EditText)findViewById(R.id.ipAddress);
        mBackText = (TextView)findViewById(R.id.stopText);

        mThread = new TcpThread(mHandler);
        mThread.setDaemon(true);
        mThread.start();
        //1 code is added <--

        //2 code is added -->
        mVoiceText = (TextView)findViewById(R.id.voiceRecognitionText);
        //2 code is added <--

        //4 code is added -->
        mJoyText = (TextView)findViewById(R.id.joyAxisText);
        joystick = (com.MobileAnarchy.Android.Widgets.Joystick.JoystickView)findViewById(R.id.joyStickMove);
        joystick.setOnJostickMovedListener(_listener);
        //4 code is added <--
    }

    //1 code is added -->
    public void onClickControl(View v) {
        Message msg;
        switch (v.getId()) {
            case R.id.bConnect:
                if(task_state == 1)  break;
                String addr = mNumEdit1.getText().toString();

                msg = new Message();
                msg.what = 0;
                msg.obj = (String)addr;
                mThread.mBackHandler.sendMessage(msg);
                break;

            case R.id.bDisconnect:
                if(task_state == 0)  break;
                msg = new Message();
                msg.what = 7;
                msg.obj = (String)mNumEdit1.getText().toString();
                mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                break;

            //2 code is added -->
            case R.id.bVoiceRecognition:
                //chkim if(task_state == 0)  break;

                VoiceSpeech();
                break;
            //2 code is added <--

            //3 code is added -->
            case R.id.bLeft:
                if(task_state == 0)  break;
                msg = new Message();
                msg.what = 1;

                mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                try { Thread.sleep(200); } catch (InterruptedException e) {;}
                break;

            case R.id.bRight:
                if(task_state == 0)  break;
                msg = new Message();
                msg.what = 2;

                mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                try { Thread.sleep(200); } catch (InterruptedException e) {;}
                break;

            case R.id.bUp:
                if(task_state == 0)  break;
                msg = new Message();
                msg.what = 3;

                mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                try { Thread.sleep(200); } catch (InterruptedException e) {;}
                break;

            case R.id.bDown:
                if(task_state == 0)  break;
                msg = new Message();
                msg.what = 4;

                mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                try { Thread.sleep(200); } catch (InterruptedException e) {;}
                break;

            case R.id.bStop:
                if(task_state == 0)  break;
                msg = new Message();
                msg.what = 6;
                //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                try { Thread.sleep(200); } catch (InterruptedException e) {;}
                break;
            //3 code is added <--
        }
    }
    //1 code is added <--

    //1 code is added -->
    Handler mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                case 0:
                    if(msg.arg1 == 0) {
                        mMainText.setText("connection successful : " + msg.obj);
                        task_state = 1;
                    }else if(msg.arg1 == 1)
                    {
                        mMainText.setText("connection fail : " + msg.obj);
                        task_state = 0;
                    }
                    break;

                case 7:
                    mMainText.setText("disconnect : ");
                    task_state = 0;
                    break;

                //2 code is added -->
               case 5:
                    mVoiceText.setText("Voice activation: ");
                    break;
                //2 code is added <--

                //3 code is added -->
                case 1:
                    mBackText.setText("Left : ");
                    break;
                case 2:
                    mBackText.setText("Right : ");
                    break;
                case 3:
                    mBackText.setText("Up : ");
                    break;
                case 4:
                    mBackText.setText("Down : ");
                    break;
                case 6:
                    mBackText.setText("Stop : ");
                    break;
                //3 code is added <--

                //4 code is added -->
                case 8:
                    //mJoyText.setText("Voice : " + ((Double)msg.obj).doubleValue());
                    //mJoyText.setText("Joystick : ");
                    mJoyText.setText("X : " + Integer.toString(msg.arg1) + "          Y : " + Integer.toString(msg.arg2) +"    " + msg.obj);
                    joy_state = 0;
                    break;
                //4 code is added <--

               default:
                    break;
            }
        }
    };

    //2 code is added -->
    public void VoiceSpeech() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH );
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,100);
            startActivityForResult(intent, RECOGNIZER);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No Speech support",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Message msg;
        if (requestCode == RECOGNIZER && resultCode == Activity.RESULT_OK) {
            // returned data is a list of matches to the speech input
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            Log.d("SPEECH", "size:" + result.size());
            String datos ;
            for (int i = result.size()-1; i >= 0 ; i--) {
                Log.d("SPEECH", "str:"+i+ ":" + result.get(i));
                datos= result.get(i);
                System.out.println(datos);

                mVoiceText = (TextView)findViewById(R.id.voiceRecognitionText);
                mVoiceText.setText((i+1)+"/"+result.size()+ ":" + result.get(i));
                //mTitle = (TextView) findViewById(R.id.textView_speech_text);
                //mTitle.setText((i+1)+"/"+result.size()+ ":" + result.get(i));
                if (datos.equals("izquierda") || datos.equals("left")
                        || datos.equals("gauche")
//                || datos.equals(getString(R.string.left_korean))) {
                        || datos.equals("왼쪽")
                        || datos.equals("����")) {
                    Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }
                else if (datos.equals("derecha") || datos.equals("right")
                        || datos.equals("droit")
//                || datos.equals(getString(R.string.right_korean))) {
                        || datos.equals("오른쪽")) {

                    Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"right";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }
                else if (datos.equals("avanzar") || datos.equals("move")
                        || datos.equals("deplacer")
//                || datos.equals(getString(R.string.move_korean))) {
                        || datos.equals("이동")) {
                    Toast.makeText(getApplicationContext(), "move", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"move";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
                else if (datos.equals("girar") || datos.equals("rotate")
                        || datos.equals("tournez")
//                || datos.equals(getString(R.string.rotate_korean))) {
                        || datos.equals("회전")) {
                    Toast.makeText(getApplicationContext(), "rotate", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"rotate";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
                else if (datos.equals("correr") || datos.equals("run")
                        || datos.equals("courir")
//                || datos.equals(getString(R.string.run_korean))) {
                        || datos.equals("전진")) {
                    Toast.makeText(getApplicationContext(), "run", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"up";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }
                else if (datos.equals("retroceder") || datos.equals("back")
                        || datos.equals("retour")
//                || datos.equals(getString(R.string.back_korean))) {
                        || datos.equals("후진")
                        || datos.equals("희진")) {
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "back", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"down";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }
                else if (datos.equals("parar") || datos.equals("stop")
                        || datos.equals("stop")
//                || datos.equals(getString(R.string.stop_korean))) {
                        || datos.equals("정지")) {
                    Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"stop";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
                else if (datos.equals("싸과") || datos.equals("서과")
                        || datos.equals("시과")
//                || datos.equals(getString(R.string.right_korean))) {
                        || datos.equals("사과")) {

                    Toast.makeText(getApplicationContext(), "apple", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"apple";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }

                else if (datos.equals("자전거") || datos.equals("자진거")
                        || datos.equals("자정거")
                        || datos.equals("자자거")) {

                    Toast.makeText(getApplicationContext(), "bicycle", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"bicycle";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }

                else if (datos.equals("바나나") || datos.equals("버너너")
                        || datos.equals("버나나")
                        || datos.equals("바너너")) {

                    Toast.makeText(getApplicationContext(), "banana", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"banana";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }

                else if (datos.equals("도그") || datos.equals("도기")
                        || datos.equals("두그")
                        || datos.equals("두기")) {

                    Toast.makeText(getApplicationContext(), "bicycle", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"dog";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }

                else if (datos.equals("트럭") || datos.equals("토럭")
                        || datos.equals("투럭")
                        || datos.equals("트러억")) {

                    Toast.makeText(getApplicationContext(), "bicycle", Toast.LENGTH_LONG).show();
                    msg = new Message();
                    msg.what = 9;
                    msg.obj = (String)"truck";
                    //msg.arg1 = Integer.parseInt(mNumEdit2.getText().toString());
                    mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
                    break;
                }
            }
        }
    }
    //2 code is added <--

    //4 code is added -->
    private com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener _listener = new com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener() {
        //Date lastSent = new Date();
        Message msg;
        public void OnMoved(int pan, int tilt) {
            mJoyText.setText("X : " + Integer.toString(pan) + "          Y : " + Integer.toString(tilt));
            //mJoyText.setText(Integer.toString(tilt));

            if(task_state == 0 || joy_state == 1)  return;
            joy_state = 1;
            msg = new Message();
            msg.what = 8;
            msg.obj = (String)" ";
            msg.arg1 = Integer.parseInt(Integer.toString(pan));
            msg.arg2 = Integer.parseInt(Integer.toString(tilt));
            mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
        }

        public void OnReleased() {
            if(task_state == 0 || joy_state == 1)  return;
            joy_state = 1;
            msg = new Message();
            msg.what = 8;
            msg.obj = (String)"released";
            msg.arg1 = 0;
            msg.arg2 = 0;
            mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
            //try { Thread.sleep(200); } catch (InterruptedException e) {;}
        }

        public void OnReturnedToCenter() {
            if(task_state == 0 || joy_state == 1)  return;
            joy_state = 1;
            msg = new Message();
            msg.what = 8;
            msg.obj = (String)"stopped";
            msg.arg1 = 0;
            msg.arg2 = 0;
            mThread.mBackHandler.sendMessage(msg); // 작업스레드에 메세지를 던진다
        };
    };
    //4 code is added <--
}

//1 code is added <--
class TcpThread extends Thread {
    Handler mMainHandler;
    Handler mBackHandler;
    private Socket sock;
    private OutputStream outs;
    private int start = 0;
    //2 code is added -->
    private String sndOpkey;
    //2 code is added <--

    TcpThread(Handler handler) {
        mMainHandler = handler;
    }

    public void run() {
        Looper.prepare(); // 작업스레드를 위한 looper 준비
        mBackHandler = new Handler() {
            public void handleMessage(Message msg) {
                Message retmsg = new Message();

                switch (msg.what) {
                    case 0:     // connect
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            ;
                        }
                        if (start == 0) {
                            try {
                                sock = new Socket((String)msg.obj, 1234);
                                //sock = new Socket("192.168.0.20", 1234);
                                outs = sock.getOutputStream();
                                start = 1;
                                // 쓰레드에서 main으로 메세지 전송 시작
                                retmsg.what = 0;
                                retmsg.arg1 = 0;                // success
                                retmsg.obj = (String) msg.obj;
                                // 쓰레드에서 main으로 메세지 전송 끝
                            } catch (Exception ex) {
                                ex.printStackTrace();

                                start = 0;
                                retmsg.what = 0;
                                retmsg.arg1 = 1;                // fail
                                retmsg.obj = (String) msg.obj;
                            }
                        }
                        break;

                    case 7:     // disconnect
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            ;
                        }
                        if (start == 1) {

                            try {

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            try {
                                sock.close();
                                sock = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            start = 0;
                            // 쓰레드에서 main으로 메세지 전송 시작
                             retmsg.what = 7;
                            // 쓰레드에서 main으로 메세지 전송 끝
                        }
                        break;

                    //2 code is added -->
                    case 5:
                        if(start == 0) break;
                        retmsg.what = 5;
                        break;

                    case 9:
                        if(start == 0) break;

                        retmsg.what = 9;
                        retmsg.obj = msg.obj;
                        retmsg.arg1 = msg.arg1;
                        retmsg.arg2 = msg.arg2;
                        sndOpkey = (String)msg.obj;
                        try {
                            outs.write(sndOpkey.getBytes("UTF-8"));
                            outs.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;
                    //2 code is added <--

                    //3 code is added -->
                    case 1:     // left
                        retmsg.what = 1;
                        retmsg.obj = msg.obj;
                        if(start == 0) break;
                        try {
                            sndOpkey = "left\n";        //"a\n"
                            outs.write(sndOpkey.getBytes("UTF-8"));
                            outs.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case 2:     // right
                        retmsg.what = 2;
                        if(start == 0) break;
                        try {
                            sndOpkey = "right";      //"b\n"
                            outs.write(sndOpkey.getBytes("UTF-8"));
                            outs.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case 3:     // up
                        retmsg.what = 3;
                        if(start == 0) break;
                        try {
                            sndOpkey = "up\n";      //"c\n"
                            outs.write(sndOpkey.getBytes("UTF-8"));
                            outs.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case 4:     // down
                        retmsg.what = 4;
                        if(start == 0) break;
                        try {
                            sndOpkey = "down\n";      //"c\n"
                            outs.write(sndOpkey.getBytes("UTF-8"));
                            outs.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case 6:     // joystick stop
                        retmsg.what = 6;
                        if(start == 0) break;
                        try {
                            sndOpkey = "stop\n";      // joystick stop
                            outs.write(sndOpkey.getBytes("UTF-8"));
                            outs.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;
                     //3 code is added <--

                    //4 code is added -->
                    case 8:
                        if(start == 0) break;
                        try { Thread.sleep(20); } catch (InterruptedException e) {;}
                        retmsg.what = 8;
                        retmsg.obj = msg.obj;
                        retmsg.arg1 = msg.arg1;
                        retmsg.arg2 = msg.arg2;
                        //Toast.makeText(run(), "0", Toast.LENGTH_LONG).show();
                        if(msg.arg1 > 8){
                            sndOpkey = "left\n";  //left "b\n"
                        }
                        else if(msg.arg1 < -8){
                            sndOpkey = "right\n";  //right "a\n"
                        }
                        else if(msg.arg2 > 8) {
                            sndOpkey = "down\n";  //down  "d\n"
                        }
                        else if(msg.arg2 < -8) {
                            sndOpkey = "up\n";  //up    "c\n"
                        }
                        else {
                            //sndOpkey = "0\n";  //up
                            break;
                        }
                        try {
                            outs.write(sndOpkey.getBytes("UTF-8"));
                            outs.flush();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;
                        //4 code is added <--

                    default:
                        break;
                }
                mMainHandler.sendMessage(retmsg); // 결과값을 다시 메인으로 보내준다
            }
        };
        Looper.loop(); // 메세지 큐에서 메세지를 꺼내 핸들러로 전달한다.
    }
}
//1 code is added <--