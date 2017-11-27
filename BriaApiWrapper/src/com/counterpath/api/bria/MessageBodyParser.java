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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.counterpath.api.bria.data.AudioProperties;
import com.counterpath.api.bria.data.Call;
import com.counterpath.api.bria.data.CallHistoryEntry;
import com.counterpath.api.bria.data.CallOptions;
import com.counterpath.api.bria.data.Participant;
import com.counterpath.api.bria.data.PhoneStatus;
import com.counterpath.api.bria.data.VoicemailAccount;
import com.counterpath.api.bria.enums.AccountStatus;
import com.counterpath.api.bria.enums.CallHistoryEntryType;
import com.counterpath.api.bria.enums.CallHoldStatusType;
import com.counterpath.api.bria.enums.ParticipantStateType;

public class MessageBodyParser {

	public static AudioProperties parseAudioPropertiesStatusResponse(Document audioPropertiesStatusBody) {
		
		String muteString = Utilities.getTextContentsFromFirstTagNamed("mute", audioPropertiesStatusBody, "disabled");
		String speakerMuteString = Utilities.getTextContentsFromFirstTagNamed("speakerMute", audioPropertiesStatusBody,
				"disabled");
		String speakerString = Utilities.getTextContentsFromFirstTagNamed("speaker", audioPropertiesStatusBody, "disabled");
		String speakerVolumeString = null;
		String microphoneVolumeString = null;
		
		
		NodeList volumeNodes = audioPropertiesStatusBody.getElementsByTagName("volume");
		for (int i = 0; i < volumeNodes.getLength(); i++) {
			Element node = (Element) volumeNodes.item(i);
			String type = node.getAttribute("type");
			if (type.equals("speaker")) {
				speakerVolumeString = node.getTextContent();
			} else if (type.equals("microphone")) {
				microphoneVolumeString = node.getTextContent();
			}
		}
		
		boolean microphoneMuted = muteString.equals("enabled") ? true : false;
		boolean speakerMuted = speakerMuteString.equals("enabled") ? true : false;
		boolean speakerModeEnabled = speakerString.equals("enabled") ? true : false;
		int speakerVolume = Integer.parseInt(speakerVolumeString);
		int microphoneVolume = Integer.parseInt(microphoneVolumeString);
		
		AudioProperties properties = new AudioProperties(microphoneMuted, speakerMuted, speakerModeEnabled, speakerVolume, microphoneVolume);
		
		return properties;
	}

	public static int parseMissedCallStatusResponse(Document missedCallStatusBody) {
		
		String countString = Utilities.getTextContentsFromFirstTagNamed("count", missedCallStatusBody, "0");
		int count = Integer.parseInt(countString);
		return count;
	}

	public static PhoneStatus parsePhoneStatusResponse(Document phoneStatusBody) {
		
		String state = Utilities.getTextContentsFromFirstTagNamed("state", phoneStatusBody, "notReady");
		String callingAllowed = Utilities.getTextContentsFromFirstTagNamed("call", phoneStatusBody, "allow");
		String accountStatusString = Utilities.getTextContentsFromFirstTagNamed("accountStatus", phoneStatusBody,
				"disabled");
		String accountFailureCodeString = Utilities.getTextContentsFromFirstTagNamed("accountFailureCode", phoneStatusBody, "0");
		String maxLinesString = Utilities.getTextContentsFromFirstTagNamed("maxLines", phoneStatusBody, "0");
		
		boolean phoneReady = state.equals("ready")? true : false;
		boolean callAllowed = callingAllowed.equals("allow")? true : false;
		AccountStatus accountStatus = AccountStatus.fromString(accountStatusString);
		int accountFailureCode = Integer.parseInt(accountFailureCodeString);
		int maxLines = Integer.parseInt(maxLinesString);
		
		PhoneStatus status = new PhoneStatus(phoneReady, callAllowed, accountStatus, accountFailureCode, maxLines);
		return status;
	}

	public static List<CallHistoryEntry> parseCallHistoryStatusResponse(Document callHistoryBody) {
	
		List<CallHistoryEntry> entries = new ArrayList<CallHistoryEntry>();
		NodeList entryTags = callHistoryBody.getElementsByTagName("callHistory");
		for (int i = 0; i < entryTags.getLength(); i++) {
			Element entryTag = (Element) entryTags.item(i);
			if (entryTag.hasChildNodes()) {
				String typeString = Utilities.getTextContentsFromFirstTagNamed("type", entryTag, "dialed");
				String numberString = Utilities.getTextContentsFromFirstTagNamed("number", entryTag, "");
				String displayNameString = Utilities.getTextContentsFromFirstTagNamed("displayName", entryTag, "");
				String durationString = Utilities.getTextContentsFromFirstTagNamed("duration", entryTag, "0");
				String timeInitiatedString = Utilities.getTextContentsFromFirstTagNamed("timeInitiated", entryTag, "0");
	
				CallHistoryEntry entry = new CallHistoryEntry(CallHistoryEntryType.fromString(typeString),
						numberString, displayNameString, Integer.parseInt(durationString),
						Integer.parseInt(timeInitiatedString));
				entries.add(entry);
			}
		}
		return entries;
	}

	public static CallOptions parseCallOptionsStatusResponse(Document callOptionsBody) {
	
		String anonymousString = Utilities.getTextContentsFromFirstTagNamed("anonymous", callOptionsBody, "disabled");
		String lettersToNumbersString = Utilities.getTextContentsFromFirstTagNamed("lettersToNumbers", callOptionsBody, "enabled");
		String autoAnswerString = Utilities.getTextContentsFromFirstTagNamed("autoAnswer", callOptionsBody, "disabled");
	
		boolean anonymous = anonymousString.equals("enabled") ? true : false;
		boolean lettersToNumbers = lettersToNumbersString.equals("enabled") ? true : false;
		boolean autoAnswer = autoAnswerString.equals("enabled") ? true : false;
	
		CallOptions options = new CallOptions(anonymous, lettersToNumbers, autoAnswer);
		return options;
	}

	public static List<VoicemailAccount> parseVoicemailStatusResponse(Document voicemailStatusBody) {
	
		ArrayList<VoicemailAccount> accounts = new ArrayList<VoicemailAccount>();
	
		NodeList voicemailTags = voicemailStatusBody.getElementsByTagName("voiceMail");
		for (int i = 0; i < voicemailTags.getLength(); i++) {
			Element voicemailTag = (Element) voicemailTags.item(i);
			if (voicemailTag.hasChildNodes()) {
				String id = Utilities.getTextContentsFromFirstTagNamed("accountId", voicemailTag, "");
				String accountName = Utilities.getTextContentsFromFirstTagNamed("accountName", voicemailTag, "");
				String count = Utilities.getTextContentsFromFirstTagNamed("count", voicemailTag, "0");
	
				VoicemailAccount account = new VoicemailAccount(id, accountName, Integer.parseInt(count));
				accounts.add(account);
			}
		}
		return accounts;
	}

	public static List<Call> parseCallStatusResponse(Document callMessageBody) {
	
		ArrayList<Call> calls = new ArrayList<Call>();
	
		NodeList callTagList = callMessageBody.getElementsByTagName("call");
	
		for (int i = 0; i < callTagList.getLength(); i++) {
			Element callTag = (Element) callTagList.item(i);
			if (callTag.hasChildNodes()) {
				String id = Utilities.getTextContentsFromFirstTagNamed("id", callTag, "");
				String holdStatus = Utilities.getTextContentsFromFirstTagNamed("holdStatus", callTag, "");
	
				ArrayList<Participant> participants = new ArrayList<Participant>();
				NodeList participantList = callTag.getElementsByTagName("participant");
	
				for (int j = 0; j < participantList.getLength(); j++) {
	
					Element participantTag = (Element) participantList.item(j);
					if (participantTag.hasChildNodes()) {
						String number = Utilities.getTextContentsFromFirstTagNamed("number", participantTag, "");
						String displayName = Utilities.getTextContentsFromFirstTagNamed("displayName", participantTag, "");
						String state = Utilities.getTextContentsFromFirstTagNamed("state", participantTag, "");
						String timeInitiated = Utilities.getTextContentsFromFirstTagNamed("timeInitiated", participantTag, "");
	
						ParticipantStateType stateType = ParticipantStateType.fromString(state);
						long timeInitiatedLong = Long.parseLong(timeInitiated);
	
						Participant participant = new Participant(number, displayName, stateType, timeInitiatedLong);
						participants.add(participant);
					}
				}
	
				CallHoldStatusType holdStatusType = CallHoldStatusType.fromString(holdStatus);
				Call call = new Call(id, holdStatusType, participants);
				calls.add(call);
			}
		}
	
		return calls;
	}

}
