package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SignalRController
 */
@RestController
public class SignalRController {

    @GetMapping("/signalr/negotiate")
    public SignalRConnectionInfo negotiate() {
        return new SignalRConnectionInfo("hello", "world");
    }
}