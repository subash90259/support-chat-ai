# 🤖 Support Chat AI

An AI-powered support assistant built using **Spring Boot, Spring AI, Ollama, JavaScript, JavaFX, and Whisper**.

The application provides a normal chat interface and supports AI-powered intent classification, tool calling, local file-system operations, browser-related actions, and voice-based interaction.

## 🚀 Features

* 💬 AI-powered chat interface
* 🧠 Intent classification and routing
* 🛠️ AI tool calling
* 📁 File-system operations
* 🌐 Browser tool integration
* 🎙️ Voice recording
* 🗣️ Speech-to-text using Whisper
* ☕ Spring Boot backend
* 🤖 Local LLM using Ollama
* 🖥️ JavaFX desktop integration
* 🌐 Java ↔ JavaScript communication through `VoiceBridge`

## 🛠️ Technologies Used

| Technology  | Purpose                         |
| ----------- | ------------------------------- |
| Java        | Backend development             |
| Spring Boot | Application framework           |
| Spring AI   | AI integration                  |
| Ollama      | Local LLM runtime               |
| Llama 3.2   | Local AI model                  |
| JavaScript  | Chat UI and browser interaction |
| HTML / CSS  | Frontend                        |
| JavaFX      | Desktop application             |
| Whisper     | Speech-to-text                  |
| Maven       | Build and dependency management |

## 🏗️ Project Structure

```text
support-chat-ai
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.support_chat_ai
│   │   │       ├── DTO
│   │   │       │   ├── ChatRequest.java
│   │   │       │   └── ChatResponse.java
│   │   │       │
│   │   │       ├── controller
│   │   │       │   └── ChatController.java
│   │   │       │
│   │   │       ├── service
│   │   │       │   ├── ChatService.java
│   │   │       │   ├── IntentClassifierService.java
│   │   │       │   └── IntentRouterService.java
│   │   │       │
│   │   │       ├── tool
│   │   │       │   ├── BrowserTool.java
│   │   │       │   └── FileSystemTool.java
│   │   │       │
│   │   │       ├── VoiceBridge.java
│   │   │       ├── VoiceRecorder.java
│   │   │       ├── WhisperService.java
│   │   │       ├── DesktopApp.java
│   │   │       └── SupportChatAiApplication.java
│   │   │
│   │   └── resources
│   │       ├── application.properties
│   │       └── static
│   │           ├── index.html
│   │           ├── index.css
│   │           └── script.js
│   │
│   └── test
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── .gitignore
```

## 🔄 AI Chat Flow

```text
User
  │
  ▼
Chat UI
  │
  ▼
ChatController
  │
  ▼
ChatService
  │
  ▼
Spring AI ChatClient
  │
  ▼
Ollama
  │
  ▼
Llama 3.2
  │
  ▼
AI Response / Tool Request
  │
  ├───────────────┐
  ▼               ▼
Normal Response   Tool
                  │
                  ├── FileSystemTool
                  │
                  └── BrowserTool
                  │
                  ▼
              Tool Result
                  │
                  ▼
              AI Response
```

## 🧠 Intent Classification

The application contains an intent-based routing flow.

```text
User Message
     │
     ▼
IntentClassifierService
     │
     ▼
Identify User Intent
     │
     ▼
IntentRouterService
     │
     ├── Chat
     ├── File operation
     ├── Browser operation
     └── Other supported actions
```

This allows the application to route different types of user requests to the appropriate service or tool.

## 🛠️ AI Tool Calling

The application provides tools that can be used by the AI when required.

### FileSystemTool

Supports operations related to local file-system interaction.

Examples:

* Check whether a file/folder exists
* Open a folder
* Copy a folder
* Perform supported local file operations

### BrowserTool

Provides browser-related functionality that can be integrated into the assistant workflow.

The AI determines when a supported tool is required and the backend executes the corresponding Java method.

## 🎙️ Voice Chat Flow

The application also supports voice interaction.

```text
User Voice
    │
    ▼
VoiceRecorder
    │
    ▼
Audio File
    │
    ▼
WhisperService
    │
    ▼
Speech → Text
    │
    ▼
Java Backend
    │
    ▼
AI Chat Processing
    │
    ▼
Response
```

## 🔗 Java ↔ JavaScript Communication

The desktop application uses JavaFX `WebView` to load the web-based chat interface.

`VoiceBridge` provides communication between Java and JavaScript.

```text
JavaFX DesktopApp
       │
       ▼
     WebView
       │
       ▼
 JavaScript Chat UI
       │
       ▼
    VoiceBridge
       │
       ▼
   Java Backend
```

## 🤖 Ollama Configuration

The application uses Ollama as the local AI runtime.

Example configuration:

```properties
spring.application.name=support-chat-ai

spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2

assistant.browser.enabled=false
```

Make sure Ollama is installed and the required model is available locally.

Example:

```bash
ollama pull llama3.2
```

Then start Ollama before running the Spring Boot application.

## ▶️ How to Run

### 1. Clone the repository

```bash
git clone https://github.com/subash90259/support-chat-ai.git
```

### 2. Open the project

```bash
cd support-chat-ai
```

### 3. Start Ollama

Make sure Ollama is running.

Verify the model:

```bash
ollama list
```

If required:

```bash
ollama pull llama3.2
```

### 4. Run the Spring Boot application

Windows:

```bash
mvnw.cmd spring-boot:run
```

Or using Maven:

```bash
mvn spring-boot:run
```

### 5. Open the application

The web application can be accessed through the configured Spring Boot server.

```text
http://localhost:8080/
```

## 🔌 API

### Chat API

**POST**

```text
/api/chat
```

Example request:

```json
{
  "message": "Hello"
}
```

Example response:

```json
{
  "response": "Hello! How can I help you?"
}
```

> The exact request/response fields may change as the project evolves.

## 🔐 Security Notes

This project is intended for local development and demonstration.

Do not commit:

* API keys
* Passwords
* Access tokens
* Secret keys
* Personal credentials
* Environment-specific secrets

Local audio files and generated runtime files are excluded from Git where appropriate.

## 📌 Future Enhancements

* 🔐 User authentication
* 💾 Conversation history
* 🗄️ Database integration
* 🎤 Improved real-time voice conversation
* 🔊 Text-to-speech responses
* 🧩 Additional AI tools
* 🌐 More browser automation capabilities
* 📊 Conversation analytics
* 🖥️ Improved desktop UI

## 👨‍💻 Author

**Subash M**

Java Backend Developer

### GitHub

https://github.com/subash90259

---

⭐ If you find this project interesting, feel free to explore the repository and its implementation.
