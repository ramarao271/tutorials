package com.baeldung.java.nio2.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncEchoClient {
    private AsynchronousSocketChannel client;
    private Future<Void> future;
    private static AsyncEchoClient instance;

    private AsyncEchoClient() {
        try {
            client = AsynchronousSocketChannel.open();
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 4999);
            future = client.connect(hostAddress);
            start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AsyncEchoClient getInstance() {
        if (instance == null)
            instance = new AsyncEchoClient();
        return instance;
    }

    private void start() {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String message) throws Exception {
        byte[] byteMsg = message.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
        Future<Integer> writeResult = client.write(buffer);

        //run some code
		writeResult.get();
        buffer.flip();
        Future<Integer> readResult = client.read(buffer);
        
		//run some code
		readResult.get();
        String echo = new String(buffer.array()).trim();
        buffer.clear();
        return echo;
    }

    public void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        AsyncEchoClient client = AsyncEchoClient.getInstance();
        client.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        System.out.println("Message to server:");
        while ((line = br.readLine()) != null) {
            String response = client.sendMessage(line);
            System.out.println("response from server: " + response);
            System.out.println("Message to server:");
        }
    }

}
