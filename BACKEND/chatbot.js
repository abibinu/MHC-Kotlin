const express = require('express');
const axios = require('axios');
const pool = require('./db');
require('dotenv').config();

const router = express.Router();

// Helper function to call Groq API (High Speed Llama-3.3-70B AI)
async function getGroqResponse(userMessage, language = 'English', userName = 'User') {
  const apiKey = process.env.GROQ_API_KEY;
  if (!apiKey) {
    throw new Error('GROQ_API_KEY is not configured');
  }

  const systemPrompt = language === 'Malayalam'
    ? `You are an empathetic, supportive AI virtual therapist named Companion. The user's name is ${userName}. Please respond warmly, thoughtfully, and helpfully in Malayalam language script. Keep your response comforting, compassionate, and conversational.`
    : `You are an empathetic, supportive AI virtual therapist named Companion. The user's name is ${userName}. Respond warmly, thoughtfully, and helpfully in English. Keep your tone comforting, compassionate, and conversational.`;

  const groqModels = ['llama-3.3-70b-versatile', 'llama3-70b-8192', 'mixtral-8x7b-32768', 'gemma2-9b-it'];

  for (const model of groqModels) {
    try {
      console.log(`Sending request to Groq model ${model}...`);
      const response = await axios.post(
        'https://api.groq.com/openai/v1/chat/completions',
        {
          model: model,
          messages: [
            { role: 'system', content: systemPrompt },
            { role: 'user', content: userMessage }
          ],
          temperature: 0.7,
          max_tokens: 1024
        },
        {
          headers: {
            'Authorization': `Bearer ${apiKey}`,
            'Content-Type': 'application/json'
          },
          timeout: 15000
        }
      );

      const replyText = response.data.choices?.[0]?.message?.content;
      if (replyText) {
        console.log(`Success using Groq model ${model}`);
        return replyText;
      }
    } catch (err) {
      console.warn(`Groq Model ${model} failed:`, err.response?.data?.error?.message || err.message);
    }
  }

  throw new Error('All Groq models failed.');
}

// Fallback Helper for Gemini API
async function getGeminiResponse(userMessage, language = 'English', userName = 'User') {
  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey) throw new Error('GEMINI_API_KEY not configured');

  const systemPrompt = language === 'Malayalam'
    ? `You are an empathetic, supportive AI virtual therapist named Companion. The user's name is ${userName}. Please respond warmly in Malayalam language script.`
    : `You are an empathetic, supportive AI virtual therapist named Companion. The user's name is ${userName}. Respond warmly in English.`;

  const fullPrompt = `${systemPrompt}\n\nUser Message: ${userMessage}`;
  const models = ['gemini-1.5-flash-latest', 'gemini-1.5-pro-latest', 'gemini-1.5-flash'];

  for (const model of models) {
    try {
      const response = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent`,
        { contents: [{ parts: [{ text: fullPrompt }] }] },
        { headers: { 'Content-Type': 'application/json' }, params: { key: apiKey }, timeout: 15000 }
      );
      const reply = response.data.candidates?.[0]?.content?.parts?.[0]?.text;
      if (reply) return reply;
    } catch (err) {
      console.warn(`Gemini model ${model} failed:`, err.message);
    }
  }
  throw new Error('All Gemini models failed.');
}

// Master Helper
async function getAIResponse(userMessage, language = 'English', userName = 'User') {
  try {
    return await getGroqResponse(userMessage, language, userName);
  } catch (groqErr) {
    console.warn('Groq failed, attempting Gemini fallback...', groqErr.message);
    try {
      return await getGeminiResponse(userMessage, language, userName);
    } catch (geminiErr) {
      console.error('All AI providers failed.');
      return language === 'Malayalam'
        ? "ക്ഷമിക്കണം, എനിക്ക് ഇപ്പോൾ മറുപടി നൽകാൻ കഴിയുന്നില്ല. വീണ്ടും ശ്രമിക്കുക."
        : "I'm sorry, I am having trouble connecting to the therapy service right now. Please try again shortly.";
    }
  }
}

// Chatbot Route (Handles User Queries)
router.post('/chatbot', async (req, res) => {
  const { user_id, message, language, user_name } = req.body;

  if (!message) {
    return res.status(400).json({ error: 'Message is required.' });
  }

  try {
    const botResponse = await getAIResponse(message, language || 'English', user_name || 'User');

    // Store chat log in PostgreSQL database if user_id exists in users table
    if (user_id && user_id > 0) {
      try {
        const userCheck = await pool.query(`SELECT user_id FROM users WHERE user_id = $1`, [user_id]);
        if (userCheck.rows.length > 0) {
          await pool.query(
            `INSERT INTO chat_logs (user_id, message, response, created_at) VALUES ($1, $2, $3, NOW())`,
            [user_id, message, botResponse]
          );
        }
      } catch (dbErr) {
        console.error('Error persisting chat log:', dbErr.message);
      }
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

  if (!user_id || user_id <= 0) {
    return res.json({ history: [] });
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
