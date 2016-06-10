package com.study.chatapp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText edit_area, edit_input;
    Thread thread;
    Socket client; //대화용 소켓!! (종이컵 전화기)
    BufferedWriter buffw; //한줄씩 대화내용을 보내기 위한 스트림
    //적어도 전송 버튼을 누르기전에는 메모리에 올라와 있어야 한다..
    //따라서 접속과 동시에 스트림을 뽑아놓자!!
    BufferedReader buffr;//서버로부터 전송되어온 메세지를 청취하기 위한 스트림!!
    ClientThread clientThread;
    Handler handler; // 개발자가 정의한 쓰레드는 절대 UI를 제어할 수 없다. 즉 메인쓰레드의 역할을 침범
    //할 수 없다. 따라서 Handler를 통해 원하는 UI제어를 부탁하면 된다...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_area = (EditText) findViewById(R.id.edit_area);
        edit_input = (EditText) findViewById(R.id.edit_input);

        thread = new Thread() {
            public void run() {
                connect();
            }
        };
        thread.start();


        handler = new Handler() {
            public void handleMessage(Message message) {
                //메인액티비티의 EditText에 메세지 출력!!

                Bundle bundle = message.getData();
                String msg = bundle.getString("msg");
                edit_area.append(msg + "\n");
            }
        };

    }

    //채팅서버에 접속을 시도하자!!
    //접속을 시도하려면, 일반 소켓이 있어야 하고, ip, port번호를 알아야 접속이 가능하다
    //안드로이드는 자바표준의 java.net을 그대로 사용할 수 있다!!
    public void connect() {
        try {
            //접속을 시도!!
            client = new Socket("192.168.13.8", 7777);

            if (client.isConnected()) {
                edit_area.append("서버에 접속됨\n");

                //더이상 현재의 액티비티에서 대화처리를 담당하지 말고, 모든 것을 쓰레드에게 맡기자!!
                clientThread = new ClientThread(this, client);
                clientThread.start();//청취시작!!
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void btnClick(View view) {
        String msg = edit_input.getText().toString();
        clientThread.send(msg);
    }
}









