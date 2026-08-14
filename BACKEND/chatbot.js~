const express = require('express');
const axios = require('axios');
const { Pool } = require('pg');
require('dotenv').config();

const router = express.Router();

// PostgreSQL Database Connection
const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
});

// Function to send user message to Gemini API
async function getGeminiResponse(userMessage) {
  try {
    const response = await axios.post(
      'https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent',
      {
        contents: [{ parts: [{ text: userMessage }] }]
      },
      {
        headers: { 'Content-Type': 'application/json' },
        params: { key: process.env.GEMINI_API_KEY }, // Ensure your API key is correct
      }
    );

    return response.data.candidates?.[0]?.content?.parts?.[0]?.text || "I'm sorry, I couldn't generate a response.";
  } catch (error) {
    console.error('Error calling Gemini API:', error.response?.data || error.message);
    return "I'm sorry, something went wrong.";
  }
}


// Chatbot Route (Handles User Queries)
router.post('/chatbot', async (req, res) => {

  const { user_id, message } = req.body;

  if (!user_id || !message) {
    return res.status(400).json({ error: 'User ID and message are required.' });
  }

  try {
    const botResponse = await getGeminiResponse(message);

    // Store chat in the database
    await pool.query(
      `INSERT INTO chat_logs (user_id, message, response) VALUES ($1, $2, $3)`,
      [user_id, message, botResponse]
    );

    res.json({ reply: botResponse });
  } catch (error) {
    console.error('Error processing chat:', error);
    res.status(500).json({ error: 'Failed to process chat.' });
  }
});

module.exports = router;
