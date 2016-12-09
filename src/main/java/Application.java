import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQBlobMessage;

import javax.jms.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by hessel on 23-11-16.
 */


public class Application {

    public static void main(String[] args) throws Exception {
        Application application = new Application();

    }

    private Application() throws Exception {
        this.sendBlob();
        this.receiveBlob();
    }


    private void sendBlob()  throws Exception {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
//        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616?jms.blobTransferPolicy.defaultUploadUrl=http://localhost:8161/fileserver/");

        //Establish the connection
        Connection connection = factory.createConnection();

        ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("Test");
        javax.jms.MessageProducer producer = session.createProducer(queue);
        connection.start();

        BlobMessage message = session.createBlobMessage(new URL("https://raw.githubusercontent.com/HesselvanApeldoorn/activemqblob/master/src/main/java/Application.java"));
        message.setName("blob filename");

        producer.send(message);
        Thread.sleep(1000);
        System.out.println("sent blob");

    }


    private void receiveBlob() throws Exception {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        //Establish the connection
        Connection connection = factory.createConnection();

        ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("Test");
        javax.jms.MessageConsumer consumer = session.createConsumer(queue);
        connection.start();

            Message message = consumer.receive();

            if (message instanceof ActiveMQBlobMessage) {
                ActiveMQBlobMessage blob = (ActiveMQBlobMessage) message;
                System.out.println("found blob: " + blob.getRemoteBlobUrl());
                Thread.sleep(1000);
//                blob.deleteFile();
                this.processInputstream(new URL(blob.getRemoteBlobUrl()));
            } else {
                System.out.println(" found general message: " + message.getJMSMessageID());
            }

    }

    private void processInputstream(URL url) {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
            //Establish the connection
            Connection connection = factory.createConnection();
            ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("Test2");
            javax.jms.MessageProducer producer = session.createProducer(queue);
            connection.start();
            BlobMessage message = session.createBlobMessage(url);

            producer.send(message);
            System.out.println("sent blob from queue" + queue.getQueueName());
            System.out.println(" blob content: " + Application.convertStreamToString(message.getInputStream()));
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
