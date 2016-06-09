package com.study.chatapp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_area=(EditText)findViewById(R.id.edit_area);
        edit_input=(EditText)findViewById(R.id.edit_input);

        thread = new Thread(){
            public void run() {
                connect();
            }
        };
        thread.start();
    }

    //채팅서버에 접속을 시도하자!!
    //접속을 시도하려면, 일반 소켓이 있어야 하고, ip, port번호를 알아야 접속이 가능하다
    //안드로이드는 자바표준의 java.net을 그대로 사용할 수 있다!!
    public void connect(){
        try {
            //접속을 시도!!
           client=new Socket("192.168.43.30", 7777);

            if(client.isConnected()){
                edit_area.append("서버에 접속됨\n");
                buffw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                buffr = new BufferedReader(new InputStreamReader(client.getInputStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //메세지 보내기!!! ( 현재 실행중인 프로그램에서 데이터가 나가는 것이므로 = 출력이다)
    public void send(){
        String msg=edit_input.getText().toString();
        try {
            buffw.write(msg+"\n"); //반드시 반드시 줄바꿈 표시가 있어야 버퍼스트림의 문장의 끝임을 이해한다.
            buffw.flush(); //퍼버처리된 출력스트림 계열은 버퍼를 싹!! 비워주자!!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnClick(View view){
        send();
    }
}









