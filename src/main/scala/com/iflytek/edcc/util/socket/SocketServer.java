package com.iflytek.edcc.util.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with Intellij IDEA.
 * User: ztwu2
 * Date: 2018/4/16
 * Time: 17:37
 * Description socket服务端
 */

public class SocketServer extends Thread {

    ServerSocket server = null;
    Socket socket = null;

    public static void main(String[] args) {
        new SocketServer().start();
    }

    public SocketServer(){
        try {
            server = new ServerSocket(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = server.accept();
                new ServerThread(socket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ServerThread extends Thread {

        private Socket socket;

        ServerThread(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true){
                try {
                    OutputStream os = socket.getOutputStream();
                    PrintWriter pw = new PrintWriter(os);

                    InputStream is = socket.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    String line = br.readLine();
                    System.out.println("客户端信息："+line);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
