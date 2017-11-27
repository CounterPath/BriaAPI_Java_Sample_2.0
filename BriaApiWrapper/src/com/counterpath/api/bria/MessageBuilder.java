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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.counterpath.api.bria.data.AudioProperties;
import com.counterpath.api.bria.enums.CallAction;
import com.counterpath.api.bria.enums.CallType;
import com.counterpath.api.bria.enums.DialType;
import com.counterpath.api.bria.enums.MessageDirection;
import com.counterpath.api.bria.enums.StatusType;


public class MessageBuilder {
		
	private static final AtomicInteger COUNTER_ATOMIC_INTEGER;
	static {
		COUNTER_ATOMIC_INTEGER = new AtomicInteger(100000);
	}
	
	private static String userAgentStatic;
	static {
		userAgentStatic = "Custom-user-agent";
	}

	private String transactionId;
	private String userAgent;
	
	private String startLine;
	private Map<String, String> headers;
	private Document body;
	
	private boolean built;
	
	public MessageBuilder() {
		this(COUNTER_ATOMIC_INTEGER.getAndIncrement() + "", userAgentStatic);
	}
	
	public MessageBuilder(String transactionId, String userAgent) {
		this.transactionId = transactionId;
		this.userAgent = userAgent;
		this.startLine = "";
		this.headers = new HashMap<String, String>();
	}
	
	public Message build() {
		
		if (built == true) {
			throw new IllegalStateException("MessageBuilders can only be used once. Create a new one for each message");
		}
		Message message = new Message(this.startLine, this.headers, this.body, MessageDirection.OUTGOING);
		built = true;
		return message;
	}
	
	public MessageBuilder authenticate(String username, String password) {
		
		if (username == null) {
			username = "";
		}
		
		if (password == null) {
			password = "";
		}
		
		this.startLine = "GET /authenticate";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("authenticate");
		doc.appendChild(rootElement);
		
		Element usernameElement = doc.createElement("username");
		Element passwordElement = doc.createElement("password");
		
		usernameElement.setTextContent(username);
		passwordElement.setTextContent(password);
		
		rootElement.appendChild(usernameElement);
		rootElement.appendChild(passwordElement);
		
		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder authenticationStatus() {
		this.startLine = "GET /authenticationStatus";
		this.configureHeaders();
		return this;
	}
	
	public MessageBuilder logout() {
		this.startLine = "GET /logout";
		this.configureHeaders();
		return this;
	}
	
	public MessageBuilder bringToFront() {
		this.startLine = "GET /bringToFront";
		this.configureHeaders();
		return this;
	}

	public MessageBuilder showHistory(CallType callType, String filterText) {
		
		if(filterText == null) {
			filterText = "";
		} else {
			filterText.trim();
		}
		
		this.startLine = "GET /showHistory";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("filter");
		doc.appendChild(rootElement);
		
		Element typeElement = doc.createElement("type");
		Element textElement = doc.createElement("text");
		
		typeElement.setTextContent(callType.toString());
		textElement.setTextContent(filterText);
		
		rootElement.appendChild(typeElement);
		rootElement.appendChild(textElement);
		
		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder status(StatusType type) {
		
		if (type.toString() == null) {
			throw new IllegalArgumentException("Can't build status message with type:" + type);
		}
		
		this.startLine = "GET /status";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element statusElement = doc.createElement("status");
		doc.appendChild(statusElement);
		
		Element typeElement = doc.createElement("type");
		typeElement.setTextContent(type.toString());
		statusElement.appendChild(typeElement);
		
		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder call(DialType dialType, String number, String displayName) {
		
		if(number == null) {
			throw new IllegalArgumentException("No number provided");
		}
		
		if(displayName == null) {
			displayName = "";
		}
		
		this.startLine = "GET /call";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("dial");
		rootElement.setAttribute("type", dialType.toString());
		doc.appendChild(rootElement);
		
		Element numberElement = doc.createElement("number");
		Element displayNameElement = doc.createElement("displayName");
		
		numberElement.setTextContent(number);
		displayNameElement.setTextContent(displayName);
		
		rootElement.appendChild(numberElement);
		rootElement.appendChild(displayNameElement);

		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder callAction(CallAction action, String callId) {
		
		if(callId == null) {
			throw new IllegalArgumentException("No callId provided");
		}
		
		this.startLine = "GET /" + action.toString();
		String rootTagName = action.toString() + "Call";
		
		if (action == CallAction.END) {
			this.startLine = "GET /" + rootTagName;
		}
		
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement(rootTagName);
		doc.appendChild(rootElement);
		
		Element callIdElement = doc.createElement("callId");
		callIdElement.setTextContent(callId);
		rootElement.appendChild(callIdElement);
		
		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder sendDTMF(String digit) {
		
		if(digit == null) {
			throw new IllegalArgumentException("Digit is not acceptable for DTMF: " + digit);
		}
		
		this.startLine = "GET /dtmf";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("dtmf");
		doc.appendChild(rootElement);
		
		Element toneElement = doc.createElement("tone");
		toneElement.setAttribute("digit", digit);
		toneElement.setTextContent("pulse"); // TODO-rm is this an option?		
		rootElement.appendChild(toneElement);
		
		this.body = doc;
		
		return this;
		
	}
	
	public MessageBuilder updateAudioProperties(AudioProperties properties) {
		this.startLine = "GET /audioProperties";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("audioProperties");
		doc.appendChild(rootElement);
		
		Element muteElement = doc.createElement("mute");
		muteElement.setTextContent(properties.isMicrophoneMuted() ? "enabled" : "disabled");	
		rootElement.appendChild(muteElement);
		
		Element speakerMuteElement = doc.createElement("speakerMute");
		speakerMuteElement.setTextContent(properties.isSpeakerMuted() ? "enabled" : "disabled");
		rootElement.appendChild(speakerMuteElement);
		
		Element speakerElement = doc.createElement("speaker");
		speakerElement.setTextContent(properties.isSpeakerModeEnabled() ? "enabled" : "disabled");
		rootElement.appendChild(speakerElement);
		
		Element volume1 = doc.createElement("volume");
		volume1.setAttribute("type", "speaker");
		volume1.setTextContent(properties.getSpeakerVolume() + "");
		rootElement.appendChild(volume1);
		
		Element volume2 = doc.createElement("microphone");
		volume2.setAttribute("type", "microphone");
		volume2.setTextContent(properties.getMicrophoneVolume() + "");
		rootElement.appendChild(volume2);
		
		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder setAnonymousCalling(boolean anonymousEnabled) {
		
		String anonymousEnabledStr = anonymousEnabled ? "enabled" : "disabled";
		
		this.startLine = "GET /callOptions";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("callOptions");
		doc.appendChild(rootElement);
		
		Element anonymousElement = doc.createElement("anonymous");
		anonymousElement.setTextContent(anonymousEnabledStr);
		rootElement.appendChild(anonymousElement);
		
		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder callHistory(int count, CallType type) {
		
		this.startLine = "GET /status";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("status");
		doc.appendChild(rootElement);
		
		Element typeElement = doc.createElement("type");
		typeElement.setTextContent("callHistory");
		rootElement.appendChild(typeElement);
		
		Element countElement = doc.createElement("count");
		countElement.setTextContent(count + "");
		rootElement.appendChild(countElement);
		
		Element entryTypeElement = doc.createElement("entryType");
		entryTypeElement.setTextContent(type.toString());
		rootElement.appendChild(entryTypeElement);
		
		this.body = doc;
		
		return this;
	}
	
	public MessageBuilder checkVoicemail(String accountId) {
		
		if (accountId == null) {
			throw new IllegalArgumentException("Invalid voicemail account id:" + accountId);
		}
		
		this.startLine = "GET /checkVoiceMail";
		this.configureHeaders();
		
		Document doc = emptyXmlDocument();
		Element rootElement = doc.createElement("checkVoiceMail");
		doc.appendChild(rootElement);
		
		Element accountIdElement = doc.createElement("accountId");
		accountIdElement.setTextContent(accountId);
		doc.appendChild(rootElement);
		
		this.body = doc;
		
		return this;
	}
	
	private void configureHeaders() {
		this.headers.put("User-Agent", this.userAgent);
		this.headers.put("Transaction-ID", this.transactionId);
		this.headers.put("Content-Type", "application/xml");
		this.headers.put("Content-Length", "0"); // Actual content length is set during writing
	}
	
	private Document emptyXmlDocument() {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.err.println("Unable to construct empty XML document.");
			e.printStackTrace();
		}
		return dBuilder.newDocument();
	}
}
