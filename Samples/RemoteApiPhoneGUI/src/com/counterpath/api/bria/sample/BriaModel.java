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
package com.counterpath.api.bria.sample;

import java.util.List;

import com.counterpath.api.bria.data.AudioProperties;
import com.counterpath.api.bria.data.Call;
import com.counterpath.api.bria.data.CallHistoryEntry;
import com.counterpath.api.bria.data.CallOptions;
import com.counterpath.api.bria.data.PhoneStatus;
import com.counterpath.api.bria.data.VoicemailAccount;

public class BriaModel {
	
	private AudioProperties audioProperties;
	private List<Call> activeCalls;
	private List<CallHistoryEntry> callHistoryEntries;
	private CallOptions callOptions;
	private PhoneStatus phoneStatus;
	private List<VoicemailAccount> voicemailAccounts;
	
	private int numberMissedCalls;
	
	public List<VoicemailAccount> getVoicemailAccounts() {
		return voicemailAccounts;
	}
	
	public AudioProperties getAudioProperties() {
		return audioProperties;
	}
	
	public void setAudioProperties(AudioProperties audioProperties) {
		this.audioProperties = audioProperties;
	}
	
	public List<Call> getActiveCalls() {
		return activeCalls;
	}
	
	public void setActiveCalls(List<Call> activeCalls) {
		this.activeCalls = activeCalls;
	}
	
	public List<CallHistoryEntry> getCallHistoryEntries() {
		return callHistoryEntries;
	}
	
	public void setCallHistoryEntries(List<CallHistoryEntry> callHistoryEntries) {
		this.callHistoryEntries = callHistoryEntries;
	}
	
	public CallOptions getCallOptions() {
		return callOptions;
	}
	
	public void setCallOptions(CallOptions callOptions) {
		this.callOptions = callOptions;
	}
	
	public PhoneStatus getPhoneStatus() {
		return phoneStatus;
	}
	
	public void setPhoneStatus(PhoneStatus phoneStatus) {
		this.phoneStatus = phoneStatus;
	}
	
	public List<VoicemailAccount> getVoicemailAccount() {
		return voicemailAccounts;
	}
	
	public void setVoicemailAccounts(List<VoicemailAccount> voicemailAccounts) {
		this.voicemailAccounts = voicemailAccounts;
	}
	
	public int getNumberMissedCalls() {
		return numberMissedCalls;
	}
	
	public void setNumberMissedCalls(int numberMissedCalls) {
		this.numberMissedCalls = numberMissedCalls;
	}
}
