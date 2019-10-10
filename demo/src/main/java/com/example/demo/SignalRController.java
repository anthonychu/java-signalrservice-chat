package com.example.demo;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kong.unirest.Unirest;

/**
 * SignalRController
 */
@RestController
public class SignalRController {

    private String signalRServiceKey = "";
    // https://foo.service.signalr.net
    private String signalRServiceBaseEndpoint = "";
    private String hubName = "chat";

    @PostMapping("/signalr/negotiate")
    public SignalRConnectionInfo negotiate() {
        String hubUrl = signalRServiceBaseEndpoint + "/client/?hub=" + hubName;
        String userId = "12345"; // optional
        String accessKey = generateJwt(hubUrl, userId);
        return new SignalRConnectionInfo(hubUrl, accessKey);
    }

    @PostMapping("/api/messages")
    public void sendMessage(@RequestBody ChatMessage message) {
        String hubUrl = signalRServiceBaseEndpoint + "/api/v1/hubs/" + hubName;
        String accessKey = generateJwt(hubUrl, null);

        Unirest.post(hubUrl)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + accessKey)
            .body(new SignalRMessage("newMessage", new Object[] { message }))
            .asEmpty();
    }

    private String generateJwt(String audience, String userId) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + (30 * 30 * 1000);
        Date exp = new Date(expMillis);

        byte[] apiKeySecretBytes = signalRServiceKey.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
            .setAudience(audience)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(signingKey);

        if (userId != null) {
            builder.claim("nameid", userId);
        }
        
        return builder.compact();
    }
}