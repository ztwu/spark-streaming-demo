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

public class SocketClient2 extends Thread {
    Socket sk = null;
    BufferedReader reader = null;
    PrintWriter wtr = null;
    BufferedReader keyin = null;

    public SocketClient2()
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
            String get = "hello world two\n";

            while (true){
                System.out.print("发送信息2："+get);
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
        Thread t1 = new SocketClient2();
        t1.start();
    }
}
