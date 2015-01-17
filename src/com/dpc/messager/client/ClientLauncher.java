package com.dpc.messager.client;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.dpc.messager.gui.ClientWindow;
import com.dpc.messager.gui.WindowController;

public class ClientLauncher {
    
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    new Client(new WindowController(new ClientWindow()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        
    }
}
