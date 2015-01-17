package com.dpc.messager.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientWindow extends JFrame implements IControllable {
    private JButton send;
    private JTextArea conversationArea;
    private JTextField messageBox;
    private JScrollPane messageScroll;
    private JPanel messagingComponents;
    private Set<IWindowListener> subscribers;
    
    public ClientWindow() {
        subscribers = new LinkedHashSet<IWindowListener>();
        
        send = new JButton("Send");
        
        send.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for(IWindowListener subscriber : subscribers) {
                    subscriber.onSendClick(messageBox.getText().toString());
                }
            }
            
        });
        
        conversationArea = new JTextArea(10, 40);
        conversationArea.setEditable(false);
        
        messageScroll = new JScrollPane(conversationArea);
        
        messageBox = new JTextField("Message", 10);
        
        messagingComponents = new JPanel();
        messagingComponents.add(messageBox);
        messagingComponents.add(send);
        
        add(messagingComponents, BorderLayout.NORTH);
        add(messageScroll, BorderLayout.CENTER);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Client");
        setVisible(true);
        pack();
    }
    
    public String getName() {
        return JOptionPane.showInputDialog("What's your name?");
    }
    
    public boolean subscribe(IWindowListener listener) {
        return subscribers.add(listener);
    }
    
    public void unsubscribe(IWindowListener listener) {
        subscribers.remove(listener);
    }
    
    @Override
    public void postMessage(String message) {
        conversationArea.append(message + "\n");
    }
    
    public static void main(String[] args) {
        new ClientWindow();
    }
}
