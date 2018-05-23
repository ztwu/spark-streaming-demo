package com.iflytek.edcc.util.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with Intellij IDEA.
 * User: ztwu2
 * Date: 2018/4/17
 * Time: 9:44
 * Description socket客户端
 */

public class SocketClient extends Thread {
    Socket sk = null;
    BufferedReader reader = null;
    PrintWriter wtr = null;
    BufferedReader keyin = null;

    public SocketClient()
    {
        keyin = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            sk = new Socket("127.0.0.1", 9999);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void run()
    {
        try
        {
            reader = new BufferedReader(new InputStreamReader(sk
                    .getInputStream()));
            wtr = new PrintWriter(sk.getOutputStream());
//            String get = keyin.readLine();
            String get = "hello world one\n";

            while (true){
                System.out.print("发送信息1："+get);
                wtr.println(get);
                wtr.flush();
                Thread.sleep(3000);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String [] args){
        Thread t1 = new SocketClient();
        t1.start();
    }
}
