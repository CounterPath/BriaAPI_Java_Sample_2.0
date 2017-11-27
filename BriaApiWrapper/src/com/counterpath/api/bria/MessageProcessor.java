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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.counterpath.api.bria.enums.MessageBodyType;


public class MessageProcessor implements MessageHandler {

	private Map<String, MessageHandler> transactionHandlers;
	private Map<MessageBodyType, MessageHandler> messageHandlers;
	
	private BlockingQueue<Message> messageQueue;
	private Executor processingExecutor;
	private boolean isProcessing;
	
	public MessageProcessor(BlockingQueue<Message> messageQueue) {
		this.transactionHandlers = new ConcurrentHashMap<String, MessageHandler>();
		this.messageHandlers = new ConcurrentHashMap<MessageBodyType, MessageHandler>();
		this.messageQueue = messageQueue;
		this.processingExecutor = Executors.newSingleThreadExecutor();
		this.isProcessing = false;
	}

	public void startProcessing() {
		if (isProcessing) {
			return;
		}
		
		this.isProcessing = true;
		final MessageProcessor thisProcessor = this;
		
		Runnable processingRunnable = new Runnable() {

			public void run() {
				while (isProcessing) {
					try {
						Message message = thisProcessor.messageQueue.take();
						thisProcessor.handle(message);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		this.processingExecutor.execute(processingRunnable);
	}
	
	public void handle(Message message) {
		
		// Check if it has a transaction ID
		// If it does, see if there's a specific handler for it
		String transactionId = message.getHeaders().get("Transaction-ID");
		if (transactionId != null && this.transactionHandlers.get(transactionId) != null) {
			this.transactionHandlers.get(transactionId).handle(message);
			return;
		} else {
			// Otherwise, try a type handler
			MessageBodyType type = message.getMessageBodyType();
			MessageHandler handler = this.messageHandlers.get(type);
			if (handler != null) {
				handler.handle(message);
			} else {
				System.err.println("No handler found for message type: " + type);
			}
			return;
		}				
	}
	
	public void registerHandlerForTransaction(String transactionId, MessageHandler handler) {
		this.transactionHandlers.put(transactionId, handler);
	}
	
	public void removeHandlerForTransaction(String transactionId) {
		this.transactionHandlers.remove(transactionId);
	}
	
	public void registerHandlerForMessageType(MessageBodyType messageType, MessageHandler handler) {
		this.messageHandlers.put(messageType, handler);
	}
	
	public void removeHandlerForMessageType(MessageBodyType messageType) {
		this.messageHandlers.remove(messageType);
	}
}
