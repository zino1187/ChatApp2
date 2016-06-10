package com.study.chatapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/*
 * 메인쓰레드는 절대로 무한루프나, 대기상태에 빠지거나,
 * 네트워크 작업을 해서는 안된다!!!
 * 또한, 전송 버튼을 누르지 않더라도 서버측에서 보내는 메세지를 실시간으로
 * 청취하려면 어느 누군가가 무한루프를 돌면서, 계속 입력을 감시해야 한다.
 * 바로--> 개발자 정의 쓰레드가 해답!!
 */
public class ClientThread extends Thread{
    String TAG=this.getClass().getName();
    MainActivity mainActivity;
    Socket client;
    BufferedReader buffr; //듣고
    BufferedWriter buffw;//말하고

    public ClientThread(MainActivity activity, Socket client){
        this.mainActivity=activity;
        this.client=client;
        try {
            buffr = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
            buffw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //메세지 보내기!!! ( 현재 실행중인 프로그램에서 데이터가 나가는 것이므로 = 출력이다)
    public void send(String msg){
        try {
            buffw.write(msg+"\n"); //반드시 반드시 줄바꿈 표시가 있어야 버퍼스트림의 문장의 끝임을 이해한다.
            buffw.flush(); //퍼버처리된 출력스트림 계열은 버퍼를 싹!! 비워주자!!

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //실시간 청취한다!!!
    public void listen(){
        String msg=null;

        try {
            msg=buffr.readLine(); //서버가 보낸 메세지 한줄을 읽어들인다!!
            Log.d(TAG, msg);
            //서버가 보낸 메세지를 메인화면의 EditText에 출력하자!!
            //메인액티비티의EditText.append(msg+"\n");
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("msg", msg); //대화내용 탑재!!
            message.setData(bundle);

            mainActivity.handler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            listen();
        }
    }
}
