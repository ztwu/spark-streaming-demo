package com.iflytek.edcc.util

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
import java.nio.charset.StandardCharsets

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

/**
  * Created with Intellij IDEA.
  * User: ztwu2
  * Date: 2018/4/16
  * Time: 17:14
  * Description
  */

class MyReceiver(host: String, port: Int) extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) {

  override def onStart() {
    println("start .................")
    // Start the thread that receives data over a connection
    new Thread("Socket Receiver") {
      override def run() {
        receive()//开启线程调receiver()方法
      }
    }.start()
  }

  override def onStop() {
    // There is nothing much to do as the thread calling receive()
    // is designed to stop by itself if isStopped() returns false
  }

  /** Create a socket connection and receive data until receiver is stopped */
  private def receive() {

    var socket: Socket = null
    var userInput: String = null
    try {
      // Connect to host:port
      println("receive1 .................")
      socket = new Socket(host, port)
      println("receive2 ................."+socket.getReceiveBufferSize)
      // Until stopped or connection broken continue reading
      val reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
      println("receive3 ................."+reader)
      userInput = reader.readLine()
      println("receive4 .................")
      println("用户socket输出数据："+userInput)
      while(!isStopped && userInput != null) {
        store(userInput)//每读一行，存一次，一直循环
        userInput = reader.readLine()
      }
      reader.close()
      socket.close()
      // Restart in an attempt to connect again when server is active again
      restart("Trying to connect again")
    } catch {
      case e: java.net.ConnectException =>
        // restart if could not connect to server
        println("receive error 1 .................")
        restart("Error connecting to " + host + ":" + port, e)
      case t: Throwable =>
        // restart if there is any other error
        println("receive error 1 .................")
        restart("Error receiving data", t)
    }
  }

}
