package com.dpc.messager.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dpc.constants.Ports;

public class Server {
    private final ServerSocket server;
    private ExecutorService threadPool;
    private Map<String, ServerClient> clients;
    private BufferedReader reader;
    private volatile boolean running;
    
    private class ConnectionListener implements Runnable {
        
        @Override
        public void run() {
            while(true) {
                try {
                    Socket connection = server.accept();
                    
                    BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    
                    String name = stream.readLine();

                    System.out.println(name + " has connected...");
                    
                    clients.put(name, new ServerClient(connection, Server.this, name));
                
                } catch (IOException e) {
               
                    if(e instanceof SocketException) {
                        System.out.println("Exiting server thread");
                        return;
                    }
                    
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    public void notifyClients(String message) throws IOException {
        for(ServerClient client : clients.values()) {
            client.getOutput().write(message);
        }
    }
    
    public Server() throws IOException {
        running = true;
        
        server = new ServerSocket(Ports.PORT);
        
        clients = new HashMap<String, ServerClient>();
        
        threadPool = Executors.newFixedThreadPool(1);
        
        threadPool.submit(new ConnectionListener());
    }
    
    public void close() throws IOException {
        server.close();
        
        for(ServerClient client : clients.values()) {
            client.close();
        }
        
        running = false;
    }
}
