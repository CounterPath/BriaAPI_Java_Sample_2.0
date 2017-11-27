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

public enum CallHoldStatusType {

	OFF_HOLD,
	LOCAL_HOLD,
	REMOTE_HOLD;
	
	private static final HashMap<String, CallHoldStatusType> STRING_TO_TYPE;
	static {
		STRING_TO_TYPE = new HashMap<String, CallHoldStatusType>();
		STRING_TO_TYPE.put(OFF_HOLD.toString(), OFF_HOLD);
		STRING_TO_TYPE.put(LOCAL_HOLD.toString(), LOCAL_HOLD);
		STRING_TO_TYPE.put(REMOTE_HOLD.toString(), REMOTE_HOLD);
	}
	
	public String toString() {
		String type = null;
		switch (this) {
		case OFF_HOLD:
			type = "offHold";
			break;
		case LOCAL_HOLD:
			type = "localHold";
			break;
		case REMOTE_HOLD:
			type = "remoteHold";
			break;
		default:
			throw new IllegalStateException("Update enum toString()");
		}
		return type;
	}
	
	public static CallHoldStatusType fromString(String holdStatus) {
		CallHoldStatusType type = STRING_TO_TYPE.get(holdStatus);
		
		if (type == null) {
			throw new IllegalArgumentException("No type exists for: " + holdStatus);
		}
		
		return type;
	}
}
