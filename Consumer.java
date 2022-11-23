import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public static void main(String[] args) {
        KafkaConsumer consumer;
        String broker = "localhost:9092";
        String topic = "natureNumber";
        Properties prop = new Properties();
        prop.put("bootstrap.servers",broker);
        prop.put("group.id","test.group");
        prop.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer(prop);
        consumer.subscribe(Arrays.asList(topic));
        while (true){
            ConsumerRecords<String,String> records = consumer.poll(100);
            for(ConsumerRecord<String,String> record:records){
                System.out.println(record.value());
                int num = Integer.parseInt(record.value());
                if (num % 2 == 0){
                    try{
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/kafka_db","root","");
                        String sql = "INSERT INTO `even`(`num`) VALUES (?)";
                        PreparedStatement stmt = con.prepareStatement(sql);
                        stmt.setInt(1,num);
                        stmt.executeUpdate();
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }

                }
            }
        }
    }
}
