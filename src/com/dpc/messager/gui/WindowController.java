package com.dpc.messager.gui;

public class WindowController {
    private final ClientWindow window;
    
    public WindowController(ClientWindow window) {
        this.window = window;
    }
    
    public String getName() {
        return window.getName();
    }
    
    public boolean subscribe(IWindowListener listener) {
        return window.subscribe(listener);
    }
    
    public void unsubscribe(IWindowListener listener) {
        window.unsubscribe(listener);
    }
    
    public void postMessage(String message) {
        window.postMessage(message);
    }
}
