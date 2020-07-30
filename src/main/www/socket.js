let ws;
let isConnected = false;
connect();

export function connect() {
    ws = new WebSocket('ws://localhost:4001')
    
    ws.onmessage = (e) => {
      showSomething(e.data);
    }

    ws.onopen = (e) => {
        sendSomething();
        isConnected = true;
    };

    ws.onclose = (e) => {
        console.log("Closing websocket...");
    };

  console.log("Connecting...");
}

export function disconnect() {
    if (ws != null) {
        ws.close();
    }
    isConnected = false;
    console.log("Disconnected");
}

export function sendSocketEvent(payload) {
  ws.send(JSON.stringify(payload))
}

function sendSomething() {
  let socketExample = {
    action: 'message',
    message: 'Testing sockets',
    timestamp: Date.now()
  }

  let addressedMessage = {
    action: 'message',
    payload: socketExample
  }

  ws.send(JSON.stringify(socketExample));
}

function showSomething(message) {
  try {
    console.log(JSON.parse(message));
  } catch (error) {
    console.log(message);
  }
}