import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by lxj on 2018/7/28.
 */
public class ConsumerTest implements Runnable{
    private static Integer count = 0;
    @Override
    public void run() {
        Properties props = new Properties();
        //bootstrap.servers是kafka集群broker的IP，这个在server.properties文件中配置port=9092(默认是9092)
        props.put("bootstrap.servers", "localhost:9092");
        // topic下的每个分区只能分配给某个group下的一个consumer
        props.put("group.id", (count++).toString());//如果固定group名称，多个consumer就是阻塞队列
        //自动提交offset，不太实用；通常在消费者处理完消息后，再手动提交offset
        //一条消息对应一个offset下标，每次消费数据的时候如果提交offset，下次消费从offset+1开始消费。
        //props.put("enable.auto.commit", "true");//后台自动提交offset
        props.put("enable.auto.commit", "false");
        /*
        earliest 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
        latest 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
        none topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
         */
        props.put("auto.offset.reset", "latest");
        //自动模式下提交offset的频率
        //props.put("auto.commit.interval.ms", "1000");
        //反序列化
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        //订阅topic
        consumer.subscribe(Arrays.asList("test"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(10);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf(Thread.currentThread().getName() +", offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            consumer.commitSync();//手动提交offset,不手动提交offset保持不变
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread one = new Thread(new ConsumerTest());
        Thread two = new Thread(new ConsumerTest());
        one.start();
        two.start();
        one.join();
        two.join();
    }
}
