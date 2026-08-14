const express = require('express');
const pool = require('./db'); // PostgreSQL connection
const router = express.Router();


// 1. Add a Mood Log
router.post('/mood-logs', async (req, res) => {
  const { user_id, mood, note } = req.body;

  if (!user_id || !mood) {
    return res.status(400).json({ message: 'User ID and mood are required.' });
  }

  try {
    const query = `
      INSERT INTO mood_logs (user_id, mood, note)
      VALUES ($1, $2, $3)
      RETURNING *;
    `;
    const result = await pool.query(query, [user_id, mood, note]);
    res.status(201).json({ message: 'Mood logged successfully!', log: result.rows[0] });
  } catch (err) {
      console.error('Error logging mood:', err); // Log the exact error
      res.status(500).json({ message: 'Failed to log mood.', error: err.message });
    }

});

// 2. Get All Mood Logs for a User
router.get('/mood-logs', async (req, res) => {
  const { user_id } = req.query;

  if (!user_id) {
    return res.status(400).json({ message: 'User ID is required.' });
  }

  try {
    const query = 'SELECT * FROM mood_logs WHERE user_id = $1 ORDER BY logged_at DESC;';
    const result = await pool.query(query, [user_id]);
    res.status(200).json(result.rows);
  } catch (err) {
    console.error('Error fetching mood logs:', err);
    res.status(500).json({ message: 'Failed to fetch mood logs.' });
  }
});

// 3. Get Analytics Data
router.get('/mood-logs/analytics', async (req, res) => {
  const { user_id } = req.query;

  if (!user_id) {
    return res.status(400).json({ message: 'User ID is required.' });
  }

  try {
    const query = `
      SELECT mood, COUNT(*) AS count
      FROM mood_logs
      WHERE user_id = $1
      GROUP BY mood
      ORDER BY count DESC;
    `;
    const result = await pool.query(query, [user_id]);

    const analytics = {};
    result.rows.forEach(row => {
      analytics[row.mood] = parseInt(row.count);
    });

    res.status(200).json(analytics);
  } catch (err) {
    console.error('Error fetching analytics:', err);
    res.status(500).json({ message: 'Failed to fetch analytics.' });
  }
});

router.get('/mood-logs/latest', async (req, res) => {
  const { user_id } = req.query;

  console.log("Received user_id:", user_id);

  if (!user_id) {
    return res.status(400).json({ message: 'User ID is required.' });
  }

  try {
    const query = `
      SELECT mood FROM mood_logs
      WHERE user_id = $1
      ORDER BY logged_at DESC
      LIMIT 1;
    `;
    const result = await pool.query(query, [user_id]);

    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'No mood logs found.' });
    }

    res.status(200).json({ mood: result.rows[0].mood });
  } catch (err) {
    console.error('Error fetching latest mood:', err);
    res.status(500).json({ message: 'Failed to fetch latest mood.' });
  }
});


module.exports = router;
