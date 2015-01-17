package com.dpc.messager.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerClient {
    private Server parentServer;
    private Socket socket;
    private Input input;
    private Output output;
    private String name;
    private ExecutorService threadPool;
    private volatile boolean running;
    
    private class Input implements Runnable {
        private BufferedReader stream;
        private byte[] buffer;
        
        public Input() throws IOException {
            stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            while(true) {
                try {
                    if(!running) {
                        return;
                    }
                  
                    String str = name + ":  " + stream.readLine();
                    System.out.println("REPLY FROM " + str);
                    
                    parentServer.notifyClients(str);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    
                    try {
                        stream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        
        public void close() throws IOException {
            stream.close();
        }
    }
    
    protected class Output implements Runnable {
        private volatile PrintWriter writer;

        public Output() throws IOException {
            writer = new PrintWriter(socket.getOutputStream());
        }

        @Override
        public void run() {
            while(true) {
                if(!running) {
                    return;
                }
            }
        }
        
        public synchronized void write(String message) {
            writer.println(message);
            writer.flush();
        }
        
        public void close() {
            writer.close();
        }
    }
    
    public ServerClient(Socket socket, Server parentServer, String name) throws IOException {
        this.socket = socket;
        this.parentServer = parentServer;
        this.name = name;
        
        running = true;
        
        input = new Input();
        output = new Output();   
        
        threadPool = Executors.newFixedThreadPool(2);
        
        threadPool.submit(input);
        threadPool.submit(output);
    }
 
    public Output getOutput() {
        return output;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void close() throws IOException {
        input.close();
        output.close();
        running = false;
    }
}
