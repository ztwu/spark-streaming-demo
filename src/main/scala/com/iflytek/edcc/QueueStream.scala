package com.iflytek.edcc

import scala.collection.mutable.Queue

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

object QueueStream {
  def main(args: Array[String]) {

    val sparkConf = new SparkConf().setAppName("QueueStream")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val rddQueue = new Queue[RDD[Int]]()
    val inputStream = ssc.queueStream(rddQueue)

    //单独处理时间维度上各个rdd
//    val mappedStream = inputStream.foreachRDD(item => {
//      val rdd = item
//      rdd.map(x => (x%10,1))
//        .reduceByKey(_+_)
//        .foreach(println(_))
//    })

    //DStream算子操作
    val mappedStream = inputStream.map(x => (x % 10, 1))
    val reducedStream = mappedStream.reduceByKey(_ + _)
    reducedStream.print()

    ssc.start()

    //循环生成rdd队列
    for (i <- 1 to 30) {
      rddQueue.synchronized {
        rddQueue += ssc.sparkContext.makeRDD(1 to 1000, 10)
      }
      Thread.sleep(1000)
    }

    ssc.stop()
  }

}