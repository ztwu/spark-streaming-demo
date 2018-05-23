package com.iflytek.edcc

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.slf4j.LoggerFactory

/**
  * Created with Intellij IDEA.
  * User: ztwu2
  * Date: 2017/12/4
  * Time: 16:41
  * Description
  */

object KafkaStream {

  private val logger = LoggerFactory.getLogger(this.getClass)

  def app(): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[3]")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(2))
    ssc.checkpoint("word-count-checkpoint")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.1.100:9092,192.168.1.101:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("ztwu")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

//    val data = stream.flatMap(x=>{
//      x.value().split(" ")
//    }).
//      map(x=>{
//        (x,1)
//      }).
//      reduceByKey((x,y)=>{x+y})
//    data.print()
//    data.saveAsTextFiles("D:\\project\\edu_edcc\\ztwu2\\stream\\data")

    //updateStateByKey和mapWithState来实现这种有状态的流管理
    //把前一段时间的结果持久化，而不是数据计算过后就抛弃
    val data = stream.flatMap(x=>{
      x.value().split(" ")
//      x.value()//针对字符串拆分为字节数组
    }).
      map(x=>{
        (x,1)
      })

    //mapWithState为PairDStreamFunctions
    val mapFunction = (key:String, one:Option[Int], state:State[Int]) => {
      val sum = one.getOrElse(0)+state.getOption().getOrElse(0)
      val output = (key, sum)
      state.update(sum)
      output
    }
//    data.mapWithState(StateSpec.function(mapFunction)).print()

    //updateStateByKey
    val updateFunc = (values: Seq[Int], state: Option[Int]) => {
      val currentCount = values.sum
      val previousCount = state.getOrElse(0)
      Some(currentCount + previousCount)
    }
    data.updateStateByKey(updateFunc).print()

    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    app()
  }

}
