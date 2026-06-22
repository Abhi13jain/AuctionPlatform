package com.example.auction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * What WebSocket and STOMP actually mean in simple terms:
 * Normally, the internet works like a walkie-talkie where you have to press a button (refresh the page) to get new information.
 * WebSocket changes this into a phone call—a continuous, open connection where the server can push new bids to your screen instantly without you refreshing.
 * STOMP is just the "language" (or protocol) they agree to speak over this phone call so they can understand each other's messages.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // This is the loudspeaker. If a user subscribes to a channel starting with "/topic", 
        // the server will broadcast messages to them instantly.
        config.enableSimpleBroker("/topic");
        
        // This is the inbox. When a user sends a bid, it must be sent to an address starting with "/app".
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the phone number users dial to establish the continuous connection.
        // SockJS is a fallback mechanism in case the user's browser is too old to support raw WebSockets.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
