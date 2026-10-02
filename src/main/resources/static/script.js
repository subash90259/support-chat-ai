const messageInput = document.getElementById("messageInput");
const sendButton = document.getElementById("sendButton");
const chatMessages = document.getElementById("chatMessages");

sendButton.addEventListener("click", sendMessage);

messageInput.addEventListener("keypress", function(event) {
    if (event.key === "Enter") {
        sendMessage();
    }
});


async function sendMessage() {

    const message = messageInput.value.trim();

    if (message === "") {
        return;
    }

    // User message
    addMessage(message, "user-message");

    // Clear input
    messageInput.value = "";

    try {

        const response = await fetch("/api/chat/chat", {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                message: message
            })
        });

        const data = await response.json();

        console.log("Backend response:", data);

        // AI response
        addMessage(data.message, "ai-message");

    } catch (error) {

        addMessage(
            "Sorry, something went wrong.",
            "ai-message"
        );

        console.error(error);
    }
}


/*
 * -----------------------------------------
 * HTML Escape
 * -----------------------------------------
 *
 * Java code contains characters like:
 * < > &
 *
 * These must not be treated as HTML.
 */

function escapeHtml(text) {

    const div = document.createElement("div");

    div.textContent = text;

    return div.innerHTML;
}


/*
 * -----------------------------------------
 * AI Response Formatter
 * -----------------------------------------
 *
 * Converts Markdown response into
 * ChatGPT-style formatted HTML.
 */

function formatAIResponse(text) {

    let html = escapeHtml(text);


    /*
     * -----------------------------------------
     * Code Blocks
     * -----------------------------------------
     *
     * Example:
     *
     * ```java
     * public class Main {
     * }
     * ```
     */

    html = html.replace(
        /```([a-zA-Z0-9+#.-]*)\s*\n([\s\S]*?)```/g,
        function(match, language, code) {

            const lang = language || "code";

            return `
                <div class="code-block">

                    <div class="code-header">

                        <span class="code-language">
                            ${lang}
                        </span>

                        <button
                            class="copy-button"
                            onclick="copyCode(this)">
                            Copy
                        </button>

                    </div>

                    <pre><code>${code.trim()}</code></pre>

                </div>
            `;
        }
    );


    /*
     * -----------------------------------------
     * Bold text
     * -----------------------------------------
     *
     * **Java**
     */

    html = html.replace(
        /\*\*(.*?)\*\*/g,
        "<strong>$1</strong>"
    );


    /*
     * -----------------------------------------
     * Inline code
     * -----------------------------------------
     *
     * `StringBuilder`
     */

    html = html.replace(
        /`([^`\n]+)`/g,
        '<code class="inline-code">$1</code>'
    );


    /*
     * -----------------------------------------
     * Bullet points
     * -----------------------------------------
     */

    html = html.replace(
        /^\s*[-*]\s+(.+)$/gm,
        "<li>$1</li>"
    );


    /*
     * Wrap consecutive list items
     */

    html = html.replace(
        /(<li>.*?<\/li>)(?:<br>)?/gs,
        "<ul>$1</ul>"
    );


    /*
     * Remove unnecessary <br> after code block
     */

    html = html.replace(
        /(<\/div>)<br>/g,
        "$1"
    );


    /*
     * New lines
     */

    html = html.replace(
        /\n/g,
        "<br>"
    );


    return html;
}


/*
 * -----------------------------------------
 * Add Message
 * -----------------------------------------
 */

function addMessage(text, className) {

    const messageDiv = document.createElement("div");

    messageDiv.classList.add("message");
    messageDiv.classList.add(className);


    /*
     * User message
     *
     * Use textContent for security.
     */

    if (className === "user-message") {

        messageDiv.textContent = text;

    }


    /*
     * AI message
     *
     * Use Markdown formatter.
     */

    else {

        messageDiv.innerHTML = formatAIResponse(text);

    }


    chatMessages.appendChild(messageDiv);


    // Scroll to latest message

    chatMessages.scrollTop =
        chatMessages.scrollHeight;
}


/*
 * -----------------------------------------
 * Copy Code
 * -----------------------------------------
 */

function copyCode(button) {

    const codeBlock =
        button
            .closest(".code-block")
            .querySelector("code");

    navigator.clipboard.writeText(
        codeBlock.innerText
    );

    button.innerText = "Copied!";


    setTimeout(() => {

        button.innerText = "Copy";

    }, 1500);
}


/*
 * -----------------------------------------
 * Voice
 * -----------------------------------------
 */

const micBtn = document.getElementById("micBtn");
const voiceStatus = document.getElementById("voiceStatus");

let isRecording = false;


micBtn.addEventListener("click", () => {

    if (!isRecording) {

        // Java microphone recording START

        window.voiceBridge.startRecording();

        isRecording = true;

        micBtn.innerText = "⏹";

        voiceStatus.innerText =
            "🔴 Listening...";

        console.log("Recording started");

    }

    else {

        // Java microphone recording STOP

        window.voiceBridge.stopRecording();

        isRecording = false;

        micBtn.innerText = "🎤";

        voiceStatus.innerText =
            "Processing...";

        console.log("Recording stopped");


        setTimeout(() => {

            voiceStatus.innerText =
                "Tap to speak";

        }, 1000);
    }
});


/*
 * -----------------------------------------
 * Voice → Text → Chat
 * -----------------------------------------
 */

async function receiveVoiceText(text) {

    console.log(
        "Voice text received:",
        text
    );

    const input =
        document.getElementById("messageInput");

    input.value = text;

    input.dispatchEvent(
        new Event("input")
    );

    // Same chat flow

    await sendMessage();
}
function minimizeWindow() {

    console.log("Minimize window");

    window.desktopWindow.minimizeWindow();
}


function maximizeWindow() {

    console.log("Maximize window");

    window.desktopWindow.maximizeWindow();
}


function closeWindow() {

    console.log("Close window");

    window.desktopWindow.closeWindow();
}