import {fetchPost} from "@/net";

export const apiChatWithAI = async (context, onMessage, onError, onComplete) => {
    try {
        console.log('发送AI请求，上下文:', context);
        const response = await fetchPost('/api/ai/chat', context);

        console.log('AI响应状态:', response.status, response.statusText);
        if (!response.ok) {
            console.error('AI响应错误:', response);
            throw new Error('Network response was not ok');
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) {
                if (buffer.trim()) {
                    processSSEBuffer(buffer, onMessage);
                }
                break;
            }
            
            buffer += decoder.decode(value, { stream: true });
            const messages = buffer.split('\n\n');
            if (messages.length > 1) {
                for (let i = 0; i < messages.length - 1; i++) {
                    processSSEBuffer(messages[i], onMessage);
                }
                buffer = messages[messages.length - 1];
            }
        }
        onComplete();
    } catch (e) {
        console.error('SSE error:', e);
        onError(e);
    }
};


function processSSEBuffer(buffer, onMessage) {
    const lines = buffer.split('\n');
    let message = '';

    for (const line of lines) {
        if (line.startsWith('data:')) {
            message += line.substring(5).trim();
        }
    }

    if (message) {
        onMessage(message);
    }
}
