package com.example.demo;

/**
 * SignalRMessage
 */
public class SignalRMessage {

    public String target;
    public String[] arguments;

    public SignalRMessage(String target, String[] arguments) {
        this.target = target;
        this.arguments = arguments;
    }
}