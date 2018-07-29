import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by lxj on 2018/7/29.
 */
public class AdminClientTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        //broker地址
        props.put("bootstrap.servers", "localhost:9092");
        AdminClient adminClient = AdminClient.create(props);
        NewTopic topic = new NewTopic("newTopic",1, (short)1);//主题名称，分区数目，备份数目
        //创建topic
        adminClient.createTopics(Arrays.asList(topic));
        //枚举所有topic
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        for (String str : listTopicsResult.names().get()){
            System.out.println("topicName: " + str);
        }
        //删除topic 慎用 不能完全删除
        //adminClient.deleteTopics(Arrays.asList("newTopic"));
    }
}
