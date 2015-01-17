package com.dpc.messager.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dpc.constants.Ports;
import com.dpc.messager.gui.IWindowListener;
import com.dpc.messager.gui.WindowController;

public class Client {
    private ExecutorService threadPool;
    private Input input;
    private Output output;
    private Socket socket;
    private WindowController windowController;
    private volatile boolean running;
    
    public Client(WindowController windowController) throws UnknownHostException, IOException {
        this.windowController = windowController;
        running = true;
        
        socket = new Socket("localhost", Ports.PORT);
        
        threadPool = Executors.newFixedThreadPool(2);
        
        input = new Input();
        output = new Output();
        
        threadPool.submit(input);
        threadPool.submit(output);
        
        String name = windowController.getName();
        output.writer.println(name);
        output.writer.flush();
    }
    
    private class Input implements Runnable {
        private BufferedReader stream;
        
        public Input() throws IOException {
            stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        
        @Override
        public void run() {
            while(true) {
                if(!running) {
                    return;
                }
                
                try {
                    windowController.postMessage(stream.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        public void close() throws IOException {
            stream.close();
        }
    }
    
    private class Output implements Runnable, IWindowListener {
        private PrintWriter writer;
        
        public Output() {
            windowController.subscribe(this);
        }
        
        @Override
        public void run() {
            try {
                writer = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
        
        @Override
        public void onSendClick(String message) {
            writer.println(message);
            writer.flush();
        }
        
        public void close() {
            writer.close();
            windowController.unsubscribe(this);
        }
    }
    
    public void close() throws IOException {
        running = false;
        
        input.close();
        output.close();
    }
}
