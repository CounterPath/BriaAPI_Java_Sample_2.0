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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.counterpath.api.bria.enums.MessageDirection;

class MessageReader {

	private BlockingQueue<Message> messageQueue;

	public MessageReader(BlockingQueue<Message> messageQueue) {
		this.messageQueue = messageQueue;
	}
	
	public void readMessage(byte[] buffer) {

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(buffer);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String potentialRequestLine = reader.readLine();
			while (potentialRequestLine != null) {
				potentialRequestLine = potentialRequestLine.replace("_pipecheck", "");
				// TODO-rm validate that this is in fact a request
				// line
				if (potentialRequestLine.isEmpty() || potentialRequestLine.equals("")) {
					// There was no real content. It's not a request
					// line
					potentialRequestLine = reader.readLine();
					continue;
				}
				String validatedRequestLine = potentialRequestLine;

				// null this out. It may become non-null again if
				// there happens to be more than one response to be
				// read.
				potentialRequestLine = null;

				Map<String, String> headers = new HashMap<String, String>();
				String headerLine = reader.readLine();
				String potentialBodyLine = null;

				while (headerLine != null) {

					// process header
					String[] headerSplit = headerLine.split(":");

					if (headerSplit.length < 2) {
						// This is not actually a header.
						// We treat is as a potential body line.
						potentialBodyLine = headerLine;
						headerLine = null;

					} else {
						headers.put(headerSplit[0].trim(), headerSplit[1].trim());
						headerLine = reader.readLine();
					}

				}

				// Extracting the body

				String body = "";

				String contentLengthString = headers.get("Content-Length");
				int contentLength = 0;
				if (contentLengthString != null) {
					contentLength = Integer.parseInt(contentLengthString);
				} else {
					System.err.println("No Content-Length header found for message");
				}

				if (contentLength > 0) {

					StringBuilder sb = new StringBuilder();
					int contentReadSoFar = 0;
					byte[] lineBytes = potentialBodyLine.getBytes(Utilities.UTF8_CHARSET);

					while (contentReadSoFar + lineBytes.length < contentLength && potentialBodyLine != null
							&& !potentialBodyLine.startsWith("HTTP")
							&& !potentialBodyLine.startsWith("POST")) {
						sb.append(potentialBodyLine);
						// +1 is for the /n removed by the reader
						contentReadSoFar += lineBytes.length + 1;
						potentialBodyLine = reader.readLine();
						if (potentialBodyLine != null) {
							lineBytes = potentialBodyLine.getBytes(Utilities.UTF8_CHARSET);
						}
					}

					if (contentLength > contentReadSoFar && potentialBodyLine != null) {
						lineBytes = potentialBodyLine.getBytes(Utilities.UTF8_CHARSET);
						int remainingToFill = contentLength - contentReadSoFar;
						byte[] contentRemainder = new byte[remainingToFill];
						byte[] excessRemainder = new byte[lineBytes.length - remainingToFill];

						for (int i = 0; i < contentRemainder.length; i++) {
							contentRemainder[i] = lineBytes[i];
						}

						int offset = contentRemainder.length;
						for (int j = 0; j < excessRemainder.length; j++) {
							excessRemainder[j] = lineBytes[offset + j];
						}

						sb.append(new String(contentRemainder, Utilities.UTF8_CHARSET));
						potentialBodyLine = new String(excessRemainder, Utilities.UTF8_CHARSET);
					}
					body = sb.toString();

				} else {
					// This is a bodyless message.
				}

				// This is line is now a potential request line
				potentialRequestLine = potentialBodyLine;

				Message message = new Message(validatedRequestLine, headers, body.toString(),
						MessageDirection.INCOMING);

				messageQueue.add(message);
			}
			is.close();
		} catch (Exception e) {
			System.err.println("Error reading message from pipe: " + e);
		}
	}
	
	public BlockingQueue<Message> getMessageQueue() {
		return this.messageQueue;
	}
}
