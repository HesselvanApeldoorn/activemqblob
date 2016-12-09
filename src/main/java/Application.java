import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;

import javax.jms.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

/**
 * Created by hessel on 23-11-16.
 */


public class Application {

    public static void main(String[] args) throws Exception {
        Application application = new Application();

    }

    private Application() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.img");
//        String xml = Application.convertStreamToString(inputStream);
//        this.processMessage("aaa");
//        this.processInputstream(inputStream);
        this.alternative();
    }

    private void alternative()  throws Exception {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        //Establish the connection
        Connection connection = factory.createConnection();

        File file = File.createTempFile("amq-data-file-", ".dat");
        // lets write some data
        String content = "hello world " + System.currentTimeMillis();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.append(content);
        writer.close();

        file = new File("/home/hessel/git/javatraining/untitled/src/main/resources/test.img");

        ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("Test3");
        javax.jms.MessageProducer producer = session.createProducer(queue);
        BlobMessage message = session.createBlobMessage(file);

        message.setName("fileName");

        producer.send(message);
        Thread.sleep(1000);
    }

    private void processInputstream(InputStream inputStream) {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
            //Establish the connection
            Connection connection = factory.createConnection();
            ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("Test2");
            javax.jms.MessageProducer producer = session.createProducer(queue);
            BlobMessage message = session.createBlobMessage(inputStream);

            producer.send(message);
            System.out.println("sent blob message");
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    private void processMessage(String xml) {
        try {
            //created ConnectionFactory object for creating connection
            ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
            //Establish the connection
            Connection connection = factory.createConnection();
            ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("Test");
            //Added as a producer
            javax.jms.MessageProducer producer = session.createProducer(queue);
            // Create and send the message
            TextMessage msg = session.createTextMessage();
            msg.setText(xml);
            producer.send(msg);
            System.out.println("sent message");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
