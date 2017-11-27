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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.counterpath.api.bria.enums.MessageBodyType;
import com.counterpath.api.bria.enums.MessageDirection;


public class Message {

	private static Map<String, MessageBodyType> eventTypeMap;
	static {
		eventTypeMap = new HashMap<String, MessageBodyType>();
		eventTypeMap.put("phone", MessageBodyType.EVENT_PHONE_STATUS_CHANGE);
		eventTypeMap.put("call", MessageBodyType.EVENT_CALL_STATUS_CHANGE);
		eventTypeMap.put("callHistory", MessageBodyType.EVENT_CALL_HISTORY_CHANGE);
		eventTypeMap.put("missedCall", MessageBodyType.EVENT_MISSED_CALL_OCCURRED);
		eventTypeMap.put("voiceMail", MessageBodyType.EVENT_MWI_COUNT_CHANGE);
		eventTypeMap.put("audioProperties", MessageBodyType.EVENT_AUDIO_SETTINGS_CHANGE);
		eventTypeMap.put("callOptions", MessageBodyType.EVENT_CALL_OPTION_CHANGE);
		eventTypeMap.put("authentication", MessageBodyType.EVENT_AUTHENTICATION_CHANGE);
		eventTypeMap.put("nWayConference", MessageBodyType.EVENT_NWAY_CONFERENCE);
	}

	private static Map<String, MessageBodyType> statusTypeMap;
	static {
		statusTypeMap = new HashMap<String, MessageBodyType>();
		statusTypeMap.put("authentication", MessageBodyType.RESPONSE_STATUS_AUTHENTICATION);
		statusTypeMap.put("phone", MessageBodyType.RESPONSE_STATUS_PHONE);
		statusTypeMap.put("systemSettings", MessageBodyType.RESPONSE_STATUS_SYSTEM_SETTINGS);
		statusTypeMap.put("call", MessageBodyType.RESPONSE_STATUS_CALL);
		statusTypeMap.put("audioProperties", MessageBodyType.RESPONSE_STATUS_AUDIO_PROPERTIES);
		statusTypeMap.put("callOptions", MessageBodyType.RESPONSE_STATUS_CALL_OPTIONS);
		statusTypeMap.put("callHistory", MessageBodyType.RESPONSE_STATUS_CALL_HISTORY);
		statusTypeMap.put("missedCall", MessageBodyType.RESPONSE_STATUS_MISSED_CALL);
		statusTypeMap.put("voiceMail", MessageBodyType.RESPONSE_STATUS_VOICEMAIL);
	}

	private String startLine;
	private Map<String, String> headers;
	private String body;
	private MessageDirection direction;
	private MessageBodyType messageType;

	private Document xmlDocument;

	public Message(String startLine, Map<String, String> headers, String body, MessageDirection direction) {
		this.startLine = startLine;
		this.headers = new HashMap<String, String>(headers);
		this.body = body;
		this.messageType = MessageBodyType.BODYLESS;
		this.direction = direction;
	}

	public Message(String startLine, Map<String, String> headers, Document bodyDoc, MessageDirection direction) {
		this.startLine = startLine;
		this.headers = new HashMap<String, String>(headers);
		this.body = "";
		this.xmlDocument = bodyDoc;
		this.messageType = MessageBodyType.BODYLESS;
		this.direction = direction;
	}

	public Document getXmlDocument() {

		if (this.xmlDocument == null && this.body != null && !this.body.equals("")) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document xmlDoc = dBuilder.parse(new ByteArrayInputStream(this.body.getBytes(Utilities.UTF8_CHARSET)));

				// See this answer for details of why we normalize
				// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				xmlDoc.getDocumentElement().normalize();

				this.xmlDocument = xmlDoc;

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return this.xmlDocument;
	}

	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public MessageBodyType getMessageBodyType() {

		Document xmlDoc = this.getXmlDocument();
		
		if (xmlDoc == null) {
			return MessageBodyType.BODYLESS;
		}

		NodeList eventTags = xmlDoc.getElementsByTagName("event");
		NodeList statusTags = xmlDoc.getElementsByTagName("status");
		if (eventTags.getLength() > 0) {

			Element event = (Element) eventTags.item(0);
			String eventTypeString = event.getAttribute("type");
			MessageBodyType eventType = stringToEventType(eventTypeString);
			this.messageType = eventType;

		} else if (statusTags.getLength() > 0) {

			Element status = (Element) statusTags.item(0);
			String statusTypeString = status.getAttribute("type");
			MessageBodyType statusType = stringToStatusType(statusTypeString);
			this.messageType = statusType;

		} else {
			this.messageType = MessageBodyType.BODYLESS;
		}


		return this.messageType;
	}

	public String getStartLine() {
		return this.startLine;
	}

	public String getTransactionId() {
		return this.headers.get("Transaction-ID");
	}
	
	public MessageDirection getMessageDirection() {
		return this.direction;
	}
	
	/**
	 * Only applicable to messages with {@code MessageBodyType}s prefixed 
	 * with {@code RESPONSE_} or of type BODYLESS
	 * 
	 * @return the response code parsed from the startLine, or 
	 * -1 if it could not be parsed.
	 */
	public int getResponseCode() {
		
		int response = -1;
		
		MessageBodyType type = this.getMessageBodyType();
		switch (type) {
		case RESPONSE_STATUS_AUTHENTICATION:
		case RESPONSE_STATUS_PHONE:
		case RESPONSE_STATUS_SYSTEM_SETTINGS:
		case RESPONSE_STATUS_CALL:
		case RESPONSE_STATUS_AUDIO_PROPERTIES:
		case RESPONSE_STATUS_CALL_OPTIONS:
		case RESPONSE_STATUS_CALL_HISTORY:
		case RESPONSE_STATUS_MISSED_CALL:
		case RESPONSE_STATUS_VOICEMAIL:
		case BODYLESS:
			String[] splitStartLine = this.getStartLine().split(" ");
			if (splitStartLine.length >= 2) {
				String potentialCodeString = splitStartLine[1];
				try {
					response = Integer.valueOf(potentialCodeString);
				} catch (NumberFormatException e) {
					response = -1;
					System.err.println("Unable to determine response code for message:" + Utilities.xmlDocumentToString(xmlDocument));
				}
			}
			break;

		default:
			break;
		}
		
		return response;
	}

	public String toString() {
		return this.startLine + " " + this.headers + " " + direction;
	}

	private static MessageBodyType stringToStatusType(String statusTypeString) {

		MessageBodyType type = statusTypeMap.get(statusTypeString);
		if (type == null) {
			System.err.println("Unable to determine MessageBodyType for status with type:" + statusTypeString);
			type = MessageBodyType.BODYLESS;
		}
		return type;
	}

	private static MessageBodyType stringToEventType(String eventTypeString) {

		MessageBodyType type = eventTypeMap.get(eventTypeString);
		if (type == null) {
			System.err.println("Unable to determine MessageBodyType for event with type: " + eventTypeString);
			type = MessageBodyType.BODYLESS;
		}
		return type;
	}	
}
