package com.iflytek.edcc

import com.iflytek.edcc.util.MyReceiver
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created with Intellij IDEA.
  * User: ztwu2
  * Date: 2018/4/16
  * Time: 17:19
  * Description
  */

object MyDStream {

  def main(args:Array[String]):Unit = {
    val conf = new SparkConf()
    conf.setAppName(this.getClass.getName)
    conf.setMaster("local[4]")
    val ssc = new StreamingContext(conf, Seconds(1))
    //其中自订阅接收器类实例的第一个参数“hadoop01”代表自定义数据源服务端的ip或者主机名，第二个参数代表8888代表数据源服务端的端口号，第三个参数false代表不启动超时设置，第四个参数0代表超时时间。
    val stream = ssc.receiverStream(new MyReceiver("127.0.0.1",9999))

    stream.flatMap(x=>x.split("")).map(word=>(word,1)).reduceByKey((x,y)=>{x+y}).print()

    ssc.start()
    ssc.awaitTermination()

  }

}
