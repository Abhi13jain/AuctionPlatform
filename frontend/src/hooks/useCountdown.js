import { useState, useEffect } from 'react';

/**
 * What this hook does and why it exists:
 * This custom hook calculates the exact time remaining until an auction ends. It exists so we can reuse this complex math logic in any component without copy-pasting it.
 * 
 * How the countdown timer calculates remaining time and updates the UI:
 * 1. It takes the `endTime` of the auction from the database.
 * 2. It sets up a `setInterval` (a repeating clock) that wakes up every 1000 milliseconds (1 second).
 * 3. Every second, it calculates: (Target Time) - (Current Time).
 * 4. It converts those raw milliseconds into days, hours, minutes, and seconds.
 * 5. By calling `setTimeLeft()`, it forces the React component to re-render, creating the illusion of a ticking live clock on the screen!
 */
const useCountdown = (targetDate) => {
  const [timeLeft, setTimeLeft] = useState('');

  useEffect(() => {
    if (!targetDate) return;

    const calculateTimeLeft = () => {
      const difference = new Date(targetDate).getTime() - new Date().getTime();

      if (difference <= 0) {
        setTimeLeft('Auction Ended');
        return;
      }

      const days = Math.floor(difference / (1000 * 60 * 60 * 24));
      const hours = Math.floor((difference / (1000 * 60 * 60)) % 24);
      const minutes = Math.floor((difference / 1000 / 60) % 60);
      const seconds = Math.floor((difference / 1000) % 60);

      if (days > 0) {
        setTimeLeft(`${days}d ${hours}h ${minutes}m`);
      } else {
        setTimeLeft(`${hours}h ${minutes}m ${seconds}s`);
      }
    };

    calculateTimeLeft(); // Initial calculation
    const timer = setInterval(calculateTimeLeft, 1000); // Update every second

    // Cleanup: when the user leaves the page, destroy the clock so it doesn't cause memory leaks
    return () => clearInterval(timer);
  }, [targetDate]);

  return timeLeft;
};

export default useCountdown;
