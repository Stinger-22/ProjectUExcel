package com.projectuexcel.concurrency;

public class WaitForString {
    private String string;

    private final Monitor monitor = new Monitor();

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void doWait() {
        synchronized (monitor) {
            try {
                monitor.wait();
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void doNotify() {
        synchronized (monitor) {
            monitor.notify();
        }
    }
}
