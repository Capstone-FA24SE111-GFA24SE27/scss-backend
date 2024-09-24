const express = require('express');
const http = require('http');
const cors = require('cors');
const socketIo = require('socket.io');
const amqp = require('amqplib');

// Create Express app
const app = express();

// Use CORS
app.use(cors());

// Create HTTP server
const server = http.createServer(app);

// Initialize Socket.IO
const io = socketIo(server, {
    cors: {
        origin: '*',
        methods: ['GET', 'POST'],
        allowedHeaders: ['my-custom-header'],
        credentials: true
    }
});

// Handle Socket.IO connections
io.on('connection', (socket) => {
    console.log('New client connected');

    socket.on('disconnect', () => {
        console.log('Client disconnected');
    });
});

// RabbitMQ connection and message handling
const rabbitmqUrl = process.env.RABBITMQ_URL || 'amqp://admin:112233@localhost:5672';

async function startRabbitMQ() {
    try {
        const connection = await amqp.connect(rabbitmqUrl);
        const channel = await connection.createChannel();
        const queue = 'notification_mobile_queue'; // Adjust based on your RabbitMQ setup

        await channel.assertQueue(queue, { durable: false });

        console.log('Waiting for messages in %s. To exit press CTRL+C', queue);

        channel.consume(queue, (msg) => {
            if (msg.content) {
                const notificationMessage = JSON.parse(msg.content.toString());
                console.log('Received message from RabbitMQ:', notificationMessage);

                // Send notification to all connected clients
                io.emit(`/user/${notificationMessage.receiverId}/private/notification`, notificationMessage);

                // Acknowledge the message
                channel.ack(msg);
            }
        });
    } catch (error) {
        console.error('Error connecting to RabbitMQ:', error);
    }
}

// Start RabbitMQ connection
startRabbitMQ();

// Create a simple route for testing
app.get('/', (req, res) => {
    res.send('Hello from Socket.IO server');
});

// Start server
const PORT = process.env.PORT || 4000;
server.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
