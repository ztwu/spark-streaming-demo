package com.iflytek.edcc

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created with Intellij IDEA.
  * User: ztwu2
  * Date: 2017/12/1
  * Time: 10:57
  * Description
  */

object SocketTextStream {

  def main(args: Array[String]) {

    //本地多线程运行,需要一个线程去监听读取数据，还有需要实时计算，local[k] k>2
    val conf = new SparkConf().setAppName("TextFileStream").setMaster("local[3]")
    val sc =new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(2))

    val lines = ssc.socketTextStream("127.0.0.1",9999)
    val wordCount = lines.flatMap(_.split(" ")).map(x => (x,1)).reduceByKey(_+_)
    wordCount.print()

    ssc.start()
    ssc.awaitTermination()

  }
}
