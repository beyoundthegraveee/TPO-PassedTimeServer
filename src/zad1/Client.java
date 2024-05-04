/**
 *
 *  @author Kurzau Kiryl S24911
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {

    private String host;

    private int port;

    private SocketChannel clientSocket;

    private ByteBuffer sendBuffer;

    private String id;
    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            clientSocket = SocketChannel.open();
            clientSocket.configureBlocking(false);
            clientSocket.connect(new InetSocketAddress(host,port));
            while (true){
                if(clientSocket.finishConnect()){
                   break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String send(String s) {
        sendBuffer = ByteBuffer.allocateDirect(s.getBytes().length);
        try {
            sendBuffer.clear();
            sendBuffer.put(s.getBytes());
            sendBuffer.flip();
            while (sendBuffer.hasRemaining()){
                clientSocket.write(sendBuffer);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(1024);
        StringBuilder response = new StringBuilder();
        boolean receivedResponse = false;
        try {
            while (!receivedResponse) {
                int tmp = clientSocket.read(receiveBuffer);
                if (tmp == -1){
                    throw new IOException();
                }
                if (tmp > 0){
                    receiveBuffer.flip();
                    byte[] receivedBytes = new byte[receiveBuffer.remaining()];
                    receiveBuffer.get(receivedBytes);
                    String receivedData = new String(receivedBytes, StandardCharsets.UTF_8);
                    response.append(receivedData);
                    receivedResponse = true;
                }
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.toString();
    }


    public String getId() {
        return id;
    }
}
