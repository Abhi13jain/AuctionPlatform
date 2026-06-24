import { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

/**
 * How WebSocket differs from normal HTTP requests and why real-time bidding needs it:
 * Normal HTTP is like sending a letter and waiting for a reply; the browser has to keep asking the server, "Are there any new bids?" (polling).
 * WebSocket is like a phone call. The line stays open, so the moment someone else bids, the server instantly shouts the new price down the line to everyone, without the browser asking.
 * 
 * What STOMP and SockJS do in simple terms:
 * Raw WebSockets just send messy text. STOMP is a protocol (a set of rules) that organizes the text into neat envelopes with destinations (like "/topic/auction/1").
 * SockJS is a fallback. If a user is on a restrictive corporate network that blocks WebSockets, SockJS pretends to be a WebSocket so the app doesn't break.
 */
const useAuctionSocket = (auctionId, token, onMessageReceived) => {
  const [connectionStatus, setConnectionStatus] = useState('Connecting...');
  const stompClient = useRef(null);

  useEffect(() => {
    if (!auctionId || !token) return;

    // We create a new STOMP Client
    const client = new Client({
      // We point it to our Spring Boot backend WebSocket endpoint. We strip '/api' because the WS endpoint is usually at root '/ws'
      webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_URL.replace('/api', '')}/ws`),
      
      // We pass our JWT token so the backend knows exactly who is placing the bid!
      connectHeaders: {
        Authorization: `Bearer ${token}` 
      },
      debug: (str) => console.log(str),
      
      /**
       * How automatic reconnection works if the socket disconnects:
       * If the user loses internet service, the connection drops.
       * STOMP automatically tries to reconnect every 5000 milliseconds until it gets back online, keeping the user in the action.
       */
      reconnectDelay: 5000,
      
      onConnect: () => {
        setConnectionStatus('Connected');
        
        /**
         * What "subscribing to a topic" means:
         * Think of a topic like a radio station channel (e.g., Channel 1 for Auction #1).
         * By subscribing, we are telling the backend: "Please send me every message broadcasted on this specific channel."
         * 
         * How bid messages travel from browser -> backend -> all connected users:
         * 1. User A clicks "Bid". The browser sends the message to the backend.
         * 2. The backend validates the bid and saves it to the database.
         * 3. The backend then acts as a loudspeaker, broadcasting the new price to the `/topic/auction/1` channel.
         * 4. User B, C, and D are subscribed to this channel, so they instantly receive the message and their screens update.
         */
        client.subscribe(`/topic/auction/${auctionId}`, (message) => {
          const data = JSON.parse(message.body);
          onMessageReceived(data);
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        setConnectionStatus('Error Connecting');
      },
      onWebSocketClose: () => {
        setConnectionStatus('Disconnected. Trying to reconnect...');
      }
    });

    client.activate();
    stompClient.current = client;

    // Cleanup: when the user leaves the room, we hang up the phone call so we don't waste server memory.
    return () => {
      if (stompClient.current) {
        stompClient.current.deactivate();
      }
    };
  }, [auctionId, token]);

  const placeBid = (amount, userEmail) => {
    if (stompClient.current && stompClient.current.connected) {
      // We send the bid to the specific auction queue on the backend
      stompClient.current.publish({
        destination: `/app/auction/${auctionId}/bid`,
        body: JSON.stringify({ auctionId: parseInt(auctionId), amount: parseFloat(amount), userEmail: userEmail })
      });
    }
  };

  return { connectionStatus, placeBid };
};

export default useAuctionSocket;
