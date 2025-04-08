const WebSocket = require('ws');
const http = require('http');
const server = http.createServer();
const wss = new WebSocket.Server({ server });

// Sample vote data
let votes = {
A: 0,
B: 0,
C: 0 };

// Listen for WebSocket connections
wss.on('connection', (ws) => {
    console.log('Client connected');

    // Send initial vote counts to the newly connected client
    ws.send(JSON.stringify({ votes }));

    // Listen for incoming vote updates from clients
    ws.on('message', (message) => {
        console.log('Received: %s', message);
        // Process vote update (e.g., increment vote count)
        if (message === 'A') {
            votes.A++;
        } else if (message === 'B') {
            votes.B++;
        } else if (message === 'C') {
            votes.C++;
        }

        // Broadcast updated votes to all connected clients
        wss.clients.forEach(client => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(JSON.stringify({ votes }));
            }
        });
    });
});

// Start the server on port 3000
server.listen(3000, () => {
    console.log('Server started on http://localhost:3000');
});
