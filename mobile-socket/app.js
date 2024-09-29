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

        function formatDate(arr) {
            let [year, month, day] = arr;
            
            // Pad month and day with leading zeros if necessary
            month = String(month).padStart(2, '0');
            day = String(day).padStart(2, '0');
        
            return `${year}-${month}-${day}`;
        }

        await channel.assertQueue(queue, { durable: false });

        console.log('Waiting for messages in %s. To exit press CTRL+C', queue);

        channel.consume("notification_mobile_queue", (msg) => {
            if (msg.content) {
                const notificationMessage = JSON.parse(msg.content.toString());
                console.log('Received message from RabbitMQ:', notificationMessage);

                // Send notification to all connected clients
                io.emit(`/user/${notificationMessage.receiverId}/private/notification`, notificationMessage);

                // Acknowledge the message
                channel.ack(msg);
            }
        });

        channel.consume("real_time_counseling_slot", (msg) => {
            if (msg.content) {
                let realTimeSlotMsg = JSON.parse(msg.content.toString());
                realTimeSlotMsg = {...realTimeSlotMsg, dateChange: formatDate(realTimeSlotMsg.dateChange)}
                console.log('Received message from RabbitMQ:', realTimeSlotMsg);
                console.log(`/user/${realTimeSlotMsg.dateChange}/${realTimeSlotMsg.counselorId}/slot`)
                // Send notification to all connected clients
                io.emit(`/user/${realTimeSlotMsg.dateChange}/${realTimeSlotMsg.counselorId}/slot`, realTimeSlotMsg);

                // Acknowledge the message
                channel.ack(msg);
            }
        });

        channel.consume("real_time_counseling_appointment", (msg) => {
            if (msg.content) {
                let realTimeSlotMsg = JSON.parse(msg.content.toString());
                console.log('Received message from RabbitMQ:', realTimeSlotMsg);
                // Send notification to all connected clients
                console.log(`/user/${realTimeSlotMsg.studentId}/appointment`)
                console.log(`/user/${realTimeSlotMsg.counselorId}/appointment`)
                io.emit(`/user/${realTimeSlotMsg.studentId}/appointment`, "Update");
                io.emit(`/user/${realTimeSlotMsg.counselorId}/appointment`, "Update");

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
