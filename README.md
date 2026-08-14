# Mental Health Companion

A comprehensive, full-stack mental health support application designed to provide emotional guidance, mood tracking, relaxation utilities, and crisis support.

---

##  Project Overview
In today’s fast-paced world, stress, anxiety, and mental health challenges have become increasingly prevalent. The **Mental Health Companion** is designed to serve as a secure, personal space for users to monitor their emotional well-being. By combining modern Native Android application design (Kotlin + Jetpack Compose) with secure backend microservices (Node.js/Express) and relational database persistence (PostgreSQL), the app offers a variety of tools to guide users toward better mental health.

---

##  Core Features

### 1.  Dual-Language AI Virtual Therapist
* **Bilingual Emotional Support:** Integrated with Google's **Gemini-1.5-Flash** API to act as a virtual therapist in both **English** and **Malayalam**.
* **Context-Aware Conversations:** Personalized greeting based on the user's registered name and a warm, empathetic therapy-focused prompting style.
* **Persistent Logs:** All chat histories are securely logged on the PostgreSQL server for reference.

### 2.  Mood Tracker & Interactive Analytics
* **Daily Mood Logging:** Users select from various emotional states (Happy, Sad, Angry, Calm, Anxious) and append a personal note.
* **Data Visualization:** Built-in analytics engine that groups mood history to highlight emotional trends.

### 3.  Calm Sounds Hub
* **Audio Streaming:** Calming soundscapes categorized into nature sounds (Gentle Rain, Ocean Waves, Stream Water, Calm Forest, Birds Chirping) and acoustic melodies (Soft Piano, Meditation Bells).
* **Background Playback:** Seamless background audio streaming while navigating other parts of the app.

### 4.  Relaxation Game: "Calm Tap"
* **Mindfulness Tap Utility:** An interactive game where a circle expands and contracts in a slow, rhythmic 3-second cycle simulating breathing. Users tap when the circle reaches its ideal size.
* **Immediate Feedback:** Promotes mindfulness and focus, helping users regulate breathing patterns during stressful episodes.

### 5.  Emergency Help Center
* **Crisis Hotlines:** Direct access to major Indian national helplines including Snehi, AASRA, iCall, Kiran, and the Vandrevala Foundation.
* **One-Tap Quick Actions:** Direct dial and SMS capabilities.
* **Custom Emergency Contacts:** Users can add and manage personal emergency contacts stored securely.

### 6.  Habit & Daily Task Planner
* **Daily Checklists:** An interactive task dashboard to complete micro-habits (drinking water, taking a walk, listing gratitudes).
* **Progress Tracking & Achievements:** Gamified achievements like "Goal Master" or "Halfway There" dynamically unlocked based on task completion rates.

---

##  Tech Stack

| Layer | Technology | Key Packages / Libraries |
| :--- | :--- | :--- |
| **Frontend** | **Native Android (Kotlin)** | `Jetpack Compose`, `Material3`, `Retrofit2`, `OkHttp3`, `Coroutines` |
| **Backend** | **Node.js (Express)** | `express`, `bcrypt`, `cors`, `pg` (PostgreSQL client), `axios` |
| **Database** | **PostgreSQL** | Relational queries, foreign keys, cascade deletes |
| **APIs** | **Google Gemini API** | `gemini-1.5-flash` model for conversational NLP |

---

##  System Architecture

The project employs a robust **Client-Server Architecture** utilizing HTTP REST APIs for communication.

* **Client Layer:** Native Kotlin Android App (Jetpack Compose UI)
* **Backend Layer:** Express.js REST API Server
* **Database Layer:** PostgreSQL Relational Database
* **External APIs:** Google Gemini API for NLP chatbot interactions

---

##  Database Schema

Run the following SQL commands to initialize the PostgreSQL database schema for the application:

```sql
-- 1. Create Users Table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 2. Create Mood Logs Table
CREATE TABLE mood_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    mood VARCHAR(50) NOT NULL,
    note TEXT,
    logged_at TIMESTAMP DEFAULT NOW()
);

-- 3. Create Chat Logs Table
CREATE TABLE chat_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    response TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

##  Getting Started

###  Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd BACKEND
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Configure your environment variables. Create a `.env` file in the `BACKEND` directory:
   ```env
   DB_HOST=localhost
   DB_USER=your_postgres_username
   DB_PASSWORD=your_postgres_password
   DB_NAME=mental_health_app
   DB_PORT=5432
   GEMINI_API_KEY=your_gemini_api_key
   ```
4. Start the server using Nodemon (for hot reloading):
   ```bash
   npm start
   ```
   *The server runs by default on `http://localhost:5000`.*

---

###  Frontend Setup (Native Kotlin Android CLI & USB Debugging)

This frontend is configured for building and deploying directly from the command line without requiring Android Studio.

#### Prerequisites
- **JDK 17** installed and configured in your environment.
- **Android SDK** with Platform Tools (`adb`).
- Physical Android phone with **USB Debugging** enabled in Developer Options.

#### Step 1: Connect Your Android Device via USB
1. Plug your Android device into your PC via USB.
2. Verify ADB connection:
   ```bash
   adb devices
   ```
   *(Ensure your device lists as `device` and authorize the USB debugging prompt on your phone if prompted).*

#### Step 2: Build & Install Debug APK via Command Line
1. Navigate to the frontend directory:
   ```bash
   cd FRONTEND
   ```
2. Build the debug APK using Gradle Wrapper:
   - **Windows (PowerShell/CMD):**
     ```powershell
     .\gradlew assembleDebug
     ```
   - **Linux / macOS:**
     ```bash
     ./gradlew assembleDebug
     ```

3. Install and run directly onto your connected phone:
   - **Windows (PowerShell/CMD):**
     ```powershell
     .\gradlew installDebug
     ```
   - **Linux / macOS:**
     ```bash
     ./gradlew installDebug
     ```

4. Alternatively, install the built APK using `adb`:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📁 Project Structure

```text
MHC-KOTLIN/
├── BACKEND/
│   ├── .env                  # Configuration variables
│   ├── db.js                 # PostgreSQL Pool connection initialization
│   ├── index.js              # Express core server and authentication endpoints
│   ├── chatbot.js            # Gemini API integration router
│   ├── moodLogs.js           # Mood tracking database transaction endpoints
│   └── package.json          # Node dependencies
├── FRONTEND/
│   ├── app/
│   │   ├── build.gradle.kts  # Application module configuration & dependencies
│   │   └── src/
│   │       └── main/
│   │           ├── AndroidManifest.xml
│   │           └── java/com/mhc/app/
│   │               ├── MainActivity.kt      # Main Compose Entry Point
│   │               └── ui/theme/            # Compose Theme, Color & Typography
│   ├── build.gradle.kts      # Top-level Gradle build file
│   ├── settings.gradle.kts   # Repository & project setup
│   ├── gradle.properties     # JVM & AndroidX properties
│   ├── local.properties      # Android SDK path configuration
│   └── gradlew / gradlew.bat # Gradle Wrapper executables
└── README.md
```

---

##  Future Enhancements
* **Appointment Booking:** Add scheduling modules for certified clinical psychologists and mental health counselors.
* **Sentiment Trend Alerts:** Automatic warning triggers to reach out to emergency contacts if logged mood trends decline consecutively for 7 days.
* **Offline Synchronization:** Local SQLite database support to sync local data seamlessly back to the main PostgreSQL server when network connectivity is restored.
* **Group Support Forums:** Secure, peer-to-peer anonymous community chatrooms.
