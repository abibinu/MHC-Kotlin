const express = require('express');
const dialogflow = require('@google-cloud/dialogflow');
const { Pool } = require('pg');
const router = express.Router();
const fs = require('fs');
const path = require('path');
require('dotenv').config();

// PostgreSQL Connection
const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
});

// Load Dialogflow credentials
const CREDENTIALS = JSON.parse(fs.readFileSync(path.join(__dirname, 'dialogflow-key.json')));

const sessionClient = new dialogflow.SessionsClient({ credentials: CREDENTIALS });
const PROJECT_ID = CREDENTIALS.project_id;

// Function to send user message to Dialogflow
async function sendMessageToDialogflow(message, sessionId) {
  const sessionPath = sessionClient.projectAgentSessionPath(PROJECT_ID, sessionId);
  const request = {
    session: sessionPath,
    queryInput: {
      text: {
        text: message,
        languageCode: 'en',
      },
    },
  };

  const responses = await sessionClient.detectIntent(request);
  return responses[0].queryResult.fulfillmentText;
}

// Chatbot API Route
router.post('/chatbot', async (req, res) => {
  const { user_id, message } = req.body;

  if (!user_id || !message) {
    return res.status(400).json({ error: 'User ID and message are required.' });
  }

  try {
    const sessionId = user_id.toString();
    const botResponse = await sendMessageToDialogflow(message, sessionId);

    // Store chat in database
    await pool.query(
      `INSERT INTO chat_logs (user_id, message, response) VALUES ($1, $2, $3)`,
      [user_id, message, botResponse]
    );

    res.json({ reply: botResponse });
  } catch (error) {
    console.error('Error:', error);
    res.status(500).json({ error: 'Failed to communicate with chatbot.' });
  }
});

module.exports = router;
