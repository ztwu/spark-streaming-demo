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

object TextFileStream {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("TextFileStream").setMaster("local[2]")
    //设置数据自动覆盖
    conf.set("spark.hadoop.validateOutputSpecs", "false")

    val sc =new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(2))

    //实时监听hdfs文件夹
    val lines = ssc.textFileStream("/project/edu_edcc/ztwu2/temp/spark-stream/")
    val wordCount = lines.flatMap(_.split(" ")).map(x => (x,1)).reduceByKey(_+_)
    wordCount.print()

    wordCount.saveAsTextFiles("/project/edu_edcc/ztwu2/temp/spark-result/result")

    ssc.start()
    ssc.awaitTermination()

  }
}
