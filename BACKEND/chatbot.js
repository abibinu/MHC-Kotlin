const express = require('express');
const axios = require('axios');
const pool = require('./db');
require('dotenv').config();

const router = express.Router();

// Helper function to send user message with therapeutic prompt context to Gemini API
async function getGeminiResponse(userMessage, language = 'English', userName = 'User') {
  try {
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) {
      console.warn('GEMINI_API_KEY is not configured in .env file');
      return language === 'Malayalam'
        ? "ക്ഷമിക്കണം, Gemini API കീ കോൺഫിഗർ ചെയ്തിട്ടില്ല."
        : "I'm sorry, the Gemini API key is not configured on the server.";
    }

    const systemPrompt = language === 'Malayalam'
      ? `You are an empathetic, supportive AI virtual therapist named Companion. The user's name is ${userName}. Please respond warmly, thoughtfully, and helpfully in Malayalam language script. Keep your response conversational and comforting.`
      : `You are an empathetic, supportive AI virtual therapist named Companion. The user's name is ${userName}. Respond warmly, thoughtfully, and helpfully in English. Keep your tone comforting, compassionate, and conversational.`;

    const fullPrompt = `${systemPrompt}\n\nUser Message: ${userMessage}`;

    const response = await axios.post(
      'https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent',
      {
        contents: [{ parts: [{ text: fullPrompt }] }]
      },
      {
        headers: { 'Content-Type': 'application/json' },
        params: { key: apiKey },
      }
    );

    return response.data.candidates?.[0]?.content?.parts?.[0]?.text 
      || (language === 'Malayalam' ? "ക്ഷമിക്കണം, എനിക്ക് മറുപടി നൽകാൻ കഴിഞ്ഞില്ല." : "I'm sorry, I couldn't generate a response right now.");
  } catch (error) {
    console.error('Error calling Gemini API:', error.response?.data || error.message);
    return language === 'Malayalam'
      ? "ക്ഷമിക്കണം, എന്തോ തകരാർ സംഭവിച്ചു. വീണ്ടും ശ്രമിക്കുക."
      : "I'm sorry, I am having trouble connecting to the therapy service right now.";
  }
}

// Chatbot Route (Handles User Queries)
router.post('/chatbot', async (req, res) => {
  const { user_id, message, language, user_name } = req.body;

  if (!user_id || !message) {
    return res.status(400).json({ error: 'User ID and message are required.' });
  }

  try {
    const botResponse = await getGeminiResponse(message, language || 'English', user_name || 'User');

    // Store chat log in PostgreSQL database if user_id is valid
    if (user_id > 0) {
      await pool.query(
        `INSERT INTO chat_logs (user_id, message, response, created_at) VALUES ($1, $2, $3, NOW())`,
        [user_id, message, botResponse]
      ).catch(err => console.error('Error persisting chat log:', err.message));
    }

    res.json({ reply: botResponse });
  } catch (error) {
    console.error('Error processing chat:', error);
    res.status(500).json({ error: 'Failed to process chat.' });
  }
});

// Chat History Route
router.get('/chatbot/history', async (req, res) => {
  const { user_id } = req.query;

  if (!user_id) {
    return res.status(400).json({ error: 'User ID is required.' });
  }

  try {
    const result = await pool.query(
      `SELECT log_id, user_id, message, response, created_at FROM chat_logs WHERE user_id = $1 ORDER BY created_at ASC`,
      [user_id]
    );

    res.json({ history: result.rows });
  } catch (error) {
    console.error('Error fetching chat history:', error);
    res.status(500).json({ error: 'Failed to fetch chat history.' });
  }
});

module.exports = router;
