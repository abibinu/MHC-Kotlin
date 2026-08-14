-- Mental Health Companion PostgreSQL Initialization Schema

-- 1. Create Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 2. Create Mood Logs Table
CREATE TABLE IF NOT EXISTS mood_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    mood VARCHAR(50) NOT NULL,
    note TEXT,
    logged_at TIMESTAMP DEFAULT NOW()
);

-- 3. Create Chat Logs Table
CREATE TABLE IF NOT EXISTS chat_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    response TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
