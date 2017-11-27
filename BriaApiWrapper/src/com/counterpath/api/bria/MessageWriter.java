/*******************************************************************************
 * (C) Copyright 2014 - CounterPath Corporation. All rights reserved.
 * 
 * THIS SOURCE CODE IS PROVIDED AS A SAMPLE WITH THE SOLE PURPOSE OF DEMONSTRATING A POSSIBLE
 * USE OF A COUNTERPATH API. IT IS NOT INTENDED AS A USABLE PRODUCT OR APPLICATION FOR ANY 
 * PARTICULAR PURPOSE OR TASK, WHETHER IT BE FOR COMMERCIAL OR PERSONAL USE.
 * 
 * COUNTERPATH DOES NOT REPRESENT OR WARRANT THAT ANY COUNTERPATH APIs OR SAMPLE CODE ARE FREE
 * OF INACCURACIES, ERRORS, BUGS, OR INTERRUPTIONS, OR ARE RELIABLE, ACCURATE, COMPLETE, OR 
 * OTHERWISE VALID.
 * 
 * THE COUNTERPATH APIs AND ASSOCIATED SAMPLE APPLICATIONS ARE PROVIDED "AS IS" WITH NO WARRANTY, 
 * EXPRESS OR IMPLIED, OF ANY KIND AND COUNTERPATH EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES AND 
 * CONDITIONS, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR 
 * A PARTICULAR PURPOSE, AVAILABLILTIY, SECURITY, TITLE AND/OR NON-INFRINGEMENT.  
 * 
 * YOUR USE OF COUNTERPATH APIS AND SAMPLE CODE IS AT YOUR OWN DISCRETION AND RISK, AND YOU WILL 
 * BE SOLELY RESPONSIBLE FOR ANY DAMAGE THAT RESULTS FROM THE USE OF ANY COUNTERPATH APIs OR
 * SAMPLE CODE INCLUDING, BUT NOT LIMITED TO, ANY DAMAGE TO YOUR COMPUTER SYSTEM OR LOSS OF DATA. 
 * 
 * COUNTERPATH DOES NOT PROVIDE ANY SUPPORT FOR THE SAMPLE APPLICATIONS.
 * 
 * TO OBTAIN A COPY OF THE OFFICIAL VERSION OF THE TERMS OF USE FOR COUNTERPATH APIs, PLEASE 
 * DOWNLOAD IT FROM THE WEB_SITE AT: http://www.counterpath.com/apitou
 ******************************************************************************/
package com.counterpath.api.bria;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import com.neovisionaries.ws.client.WebSocket;


class MessageWriter {
	private WebSocket ws;
	private LinkedBlockingQueue<Message> messageQueue;
	private static final String CRLF = "\r\n";

	/**
	 * 
	 * @param pipeFile
	 */
	public MessageWriter(WebSocket webSocket) {
		this.ws = webSocket;
		this.messageQueue = new LinkedBlockingQueue<Message>();
	}
	
	public void sendMessage(Message message) {
				
		StringBuilder requestString = new StringBuilder();
		
		requestString.append(message.getStartLine());
		requestString.append(CRLF);
		
		Map<String, String> headers = message.getHeaders();
		
		// Need to set the Content-Length based on the body size
		String xmlBody = Utilities.xmlDocumentToString(message.getXmlDocument());
		byte[] bytes = xmlBody.getBytes(Utilities.UTF8_CHARSET);
		int contentLength = bytes.length;
		headers.put("Content-Length", contentLength + "");
		
		for (String key : headers.keySet()) {
			requestString.append(key);
			requestString.append(": ");
			requestString.append(headers.get(key));
			requestString.append(CRLF);
		}
		
		// Then write the body
		if (bytes.length > 0) {
			requestString.append(xmlBody);
		}
		
		byte[] bytesToWrite = requestString.toString().getBytes(Utilities.UTF8_CHARSET);
		
		if(ws.isOpen()) {
			ws.sendBinary(bytesToWrite);
		} else {
			System.err.println("socket not connected");
			this.messageQueue.add(message);
		}
		
		//System.err.println("Sent message with transaction id:" + headers.get("Transaction-ID"));
		//System.err.println(new String(bytesToWrite));
	}
	
	public void sendDelayedMessages() {
		//when onConnected; if there were any delayed messages queued, send the messages to the webSocket
		for (int i = 0; i<messageQueue.size(); i++) {
			try {
				sendMessage(messageQueue.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
