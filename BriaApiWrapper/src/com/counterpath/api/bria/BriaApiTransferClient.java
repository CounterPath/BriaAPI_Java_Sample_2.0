package com.counterpath.api.bria;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLContext;

import com.counterpath.api.bria.enums.CallType;
import com.counterpath.api.bria.enums.MessageBodyType;
import com.counterpath.api.bria.enums.StatusType;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

public class BriaApiTransferClient extends WebSocketFactory{
    private static final int TIMEOUT = 5000;
    private WebSocket curWs;
    private URI serverURI;
	private MessageWriter messageWriter;
	private MessageReader messageReader;
	private MessageProcessor messageProcessor;
	private BriaApiClientOpenListener clientListener;
	private Charset charset = Charset.forName("UTF-8");

	public BriaApiTransferClient(URI serverUri, BriaApiClientOpenListener listener) {
		try {
			this.clientListener = listener;	
			this.serverURI = serverUri;
			SSLContext context = NaiveSSLContext.getInstance("TLS");
			this.setSSLContext(context);
			this.setVerifyHostname(false);
			
			curWs = createNewSocket();
			
			LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
			this.messageProcessor = new MessageProcessor(messageQueue);
			this.messageReader = new MessageReader(messageQueue);
			this.messageWriter = new MessageWriter(curWs);
		} catch (IOException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connectSocket() {
		try {
			if(curWs.getState() != WebSocketState.CREATED)
				curWs = curWs.recreate();
			curWs.connect();
		} catch (WebSocketException | IOException e) {
			e.printStackTrace();
			clientListener.onConnectionError(e.toString());
		}
	}

	private WebSocket createNewSocket() throws IOException {
		return this.createSocket(serverURI, TIMEOUT)
				.addListener(new BriaWpiWebSocketAdapter());
	}
	public MessageProcessor getMessageProcessor() {
		return this.messageProcessor;
	}

	private ByteBuffer str_to_bb(String msg, Charset charset){
	    return ByteBuffer.wrap(msg.getBytes(charset));
	}

	public void writeMessage(Message message) {
		this.messageWriter.sendMessage(message);
	}
	
	private void registerDefaultMessageEventHandlers() {

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_AUDIO_SETTINGS_CHANGE,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(StatusType.AUDIO_PROPERTIES).build();
						messageWriter.sendMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_AUTHENTICATION_CHANGE,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().authenticationStatus().build();
						messageWriter.sendMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_CALL_HISTORY_CHANGE, new MessageHandler() {
			public void handle(Message message) {
				Message request = new MessageBuilder().callHistory(100, CallType.ALL).build();
				messageWriter.sendMessage(request);
			}
		});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_CALL_OPTION_CHANGE, new MessageHandler() {
			public void handle(Message message) {
				Message request = new MessageBuilder().status(StatusType.CALL_OPTIONS).build();
				messageWriter.sendMessage(request);
			}
		});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_CALL_STATUS_CHANGE, new MessageHandler() {
			public void handle(Message message) {
				Message request = new MessageBuilder().status(StatusType.CALL).build();
				messageWriter.sendMessage(request);
			}
		});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_MISSED_CALL_OCCURRED,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(StatusType.MISSED_CALL).build();
						messageWriter.sendMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_MWI_COUNT_CHANGE, new MessageHandler() {
			public void handle(Message message) {
				Message request = new MessageBuilder().status(StatusType.VOICEMAIL).build();
				messageWriter.sendMessage(request);
			}
		});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.EVENT_PHONE_STATUS_CHANGE, new MessageHandler() {
			public void handle(Message message) {
				Message request = new MessageBuilder().status(StatusType.PHONE).build();
				messageWriter.sendMessage(request);
			}
		});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.BODYLESS, new MessageHandler() {
			public void handle(Message message) {

				if (message.getResponseCode() > 0 && message.getResponseCode() != 200) {
					System.err
							.println("Received an error message from the Bria client. You may wish to override a MessageBodyType.BODYLESS handler to handle any error responses. Message: ");
					System.err.println(Utilities.xmlDocumentToString(message.getXmlDocument()));
				}
			}
		});
	}
	
	private class Listener implements WebSocketListener{

		@Override
		public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
				WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextMessage(WebSocket websocket, String text) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames)
				throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed)
				throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers)
				throws Exception {
			// TODO Auto-generated method stub
			
		}
		
	}

	private class BriaWpiWebSocketAdapter extends WebSocketAdapter{
		public BriaWpiWebSocketAdapter() {
			super();
		}
		
		 @Override
		 public void onTextMessage(WebSocket ws, String message) throws Exception {
			 super.onTextMessage(ws, message);
		    // Received a response. Print the received message.
		    System.out.println(message);

		    byte[] bytes = str_to_bb(message, charset).array();
		    System.out.println("received message: " + bytes);
			messageReader.readMessage(bytes);
		    // Close the WebSocket connection.
		 }
		 
		@Override
		public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
			super.onConnected(websocket, headers);
			System.out.println("socket connected");
			registerDefaultMessageEventHandlers();
			clientListener.onClientConnected();
			messageWriter.sendDelayedMessages();
		}

		@Override
		public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
			super.onError(websocket, cause);
			System.err.println("an error occurred:" + cause);
			websocket.sendClose();
		}
		
	}
	
	public interface BriaApiClientOpenListener {
		public abstract void onClientConnected();
		public abstract void onConnectionError(String errorMsg);
	}
	
}
