import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @Author
 * Created by lxj on 2018/7/28.
 */
public class ProducerTest {
    public static void main(String[] args) {
        //producer有一个发送缓冲区，缓冲区中存放还未向kafka集群发送的消息
        Properties props = new Properties();
        //bootstrap.servers是kafka集群broker的IP，这个在server.properties文件中配置port=9092(默认是9092)
        props.put("bootstrap.servers", "localhost:9092");
        //acks=0表示生产者不需要来自server的确认；acks=1表示server端将消息保存后即可发送ack，而不必等到其他follower角色的都收到了该消息；
        //acks=all(or acks=-1)意味着server端将等待所有的副本都被接收后才发送确认
        props.put("acks", "all");
        //生产者发送失败后，重试的次数
        props.put("retries", 0);
        //当多条消息发送到同一个partition时，该值控制生产者批量发送消息的大小
        props.put("batch.size", 16384);
        //默认情况下缓冲区的消息会被立即发送到服务端，即使缓冲区的空间并没有被用完。可以将该值设置为大于0的值，这样发送者将等待一段时间后，再向服务端发送请求，以实现每次请求可以尽可能多的发送批量消息
        props.put("linger.ms", 1);
        //生产者缓冲区的大小，保存的是还未来得及发送到server端的消息，如果生产者的发送速度大于消息被提交到server端的速度，该缓冲区将被耗尽。
        props.put("buffer.memory", 33554432);
        //序列化方式将用户提供的key和vaule值序列化成字节
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //Producer API
        Producer<String,String> producer = new KafkaProducer<>(props);
        for(int i=0; i<100; i++){
            //send将发送消息到指定的topic test中，send返回future，可以在future上阻塞等待消息
            //先要创建topic才能发消息: kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
            producer.send(new ProducerRecord<String, String>("test","new "+ Integer.toString(i), Integer.toString(i)));
        }
        //关闭producer，方法将被阻塞，直到之前的发送请求已经完成
        producer.close();
    }
}
