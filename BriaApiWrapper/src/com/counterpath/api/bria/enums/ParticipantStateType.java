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
package com.counterpath.api.bria.enums;

import java.util.HashMap;

public enum ParticipantStateType {
	
	RINGING,
	CONNECTING,
	CONNECTED,
	FAILED,
	ENDED;
	
	private static final HashMap<String, ParticipantStateType> STRING_TO_TYPE;
	static {
		STRING_TO_TYPE = new HashMap<String, ParticipantStateType>();
		STRING_TO_TYPE.put(RINGING.toString(), RINGING);
		STRING_TO_TYPE.put(CONNECTING.toString(), CONNECTING);
		STRING_TO_TYPE.put(CONNECTED.toString(), CONNECTED);
		STRING_TO_TYPE.put(FAILED.toString(), FAILED);
		STRING_TO_TYPE.put(ENDED.toString(), ENDED);
	}
	
	public String toString() {
		String type = null;
		switch (this) {
		case RINGING:
			type = "ringing";
			break;
		case CONNECTING:
			type = "connecting";
			break;
		case CONNECTED:
			type = "connected";
			break;
		case FAILED:
			type = "failed";
			break;
		case ENDED:
			type = "ended";
			break;
		default:
			throw new IllegalStateException("Update enum toString()");
		}
		return type;
	}
	
	public static ParticipantStateType fromString(String participantState) {
		ParticipantStateType type = STRING_TO_TYPE.get(participantState);
		
		if (type == null) {
			throw new IllegalArgumentException("No type exists for: " + participantState);
		}
		
		return type;
	}
}
