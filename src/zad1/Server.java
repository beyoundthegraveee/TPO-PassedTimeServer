/**
 *
 *  @author Kurzau Kiryl S24911
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalTime;
import java.util.*;

public class Server {

    private String host;

    private volatile String serverLog;

    private ServerSocketChannel ssc = null;

    private volatile boolean isServerRunning = true;

    private volatile Map<SocketChannel, String> clientLogs;

    private volatile Map<SocketChannel, String> clientIds;

    private Selector selector = null;
    private int port;
    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.serverLog = "";
        this.clientLogs = new HashMap<>();
        this.clientIds = new HashMap<>();
        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(this.host, this.port));
            selector = Selector.open();
            ssc.register(selector,SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer(){
        new Thread(()-> {
            while (isServerRunning) {
                try {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = ssc.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            clientLogs.put(socketChannel, "");
                            continue;
                        }
                        if (key.isReadable()){
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            serviceRequest(socketChannel);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void stopServer() throws IOException {
        isServerRunning = false;
        ssc.close();
    }

    public String getServerLog() {
        return serverLog;
    }

    private void serviceRequest(SocketChannel socketChannel) {
        if (!socketChannel.isOpen()){
            return;
        }
        try {
            ByteBuffer buffer = ByteBuffer.allocate(256);
            socketChannel.read(buffer);
            String request = new String(buffer.array()).trim();
            if (request.matches("login\\s.+")){
                String id = request.split("\\s")[1];
                clientIds.put(socketChannel, id);
                serverLog+=id + " logged in at " +LocalTime.now() + "\n";
                String response = "logged in\n";
                String clog = clientLogs.get(socketChannel);
                clog += "=== " + id + " log start ===\n";
                clog += "logged in\n";
                clientLogs.put(socketChannel, clog);
                writeMessage(socketChannel, response);
            } else if(request.equals("bye and log transfer")){
                String id = clientIds.get(socketChannel);
                serverLog+=id +" logged out at "+LocalTime.now() + "\n";

                String clog = clientLogs.get(socketChannel);
                clog += "logged out\n";
                clog += "=== " + id + " log end ===\n";
                clientLogs.put(socketChannel, clog);

                String response = clog;
                writeMessage(socketChannel, response);
            }else{
                String id = clientIds.get(socketChannel);
                serverLog+= id + " request at " + LocalTime.now() + ": \"" + request + "\"\n";
                String d1 = request.split(" ")[0];
                String d2 = request.split(" ")[1];
                String response = Time.passed(d1, d2);
                String clog = clientLogs.get(socketChannel);
                clog += "Request: " + request + "\n";
                clog += "Result:\n" + response + "\n";
                clientLogs.put(socketChannel, clog);
                writeMessage(socketChannel, response);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void writeMessage(SocketChannel socketChannel, String response){
        try {
            ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
            while (responseBuffer.hasRemaining()) {
                socketChannel.write(responseBuffer);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }








}
