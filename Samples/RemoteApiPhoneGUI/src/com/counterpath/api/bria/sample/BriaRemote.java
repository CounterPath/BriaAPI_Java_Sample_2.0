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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.Document;

import com.counterpath.api.bria.BriaApiTransferClient;
import com.counterpath.api.bria.Message;
import com.counterpath.api.bria.MessageBodyParser;
import com.counterpath.api.bria.MessageBuilder;
import com.counterpath.api.bria.MessageHandler;
import com.counterpath.api.bria.MessageProcessor;
import com.counterpath.api.bria.BriaApiTransferClient.BriaApiClientOpenListener;
import com.counterpath.api.bria.data.AudioProperties;
import com.counterpath.api.bria.data.Call;
import com.counterpath.api.bria.data.CallHistoryEntry;
import com.counterpath.api.bria.data.CallOptions;
import com.counterpath.api.bria.data.PhoneStatus;
import com.counterpath.api.bria.data.VoicemailAccount;
import com.counterpath.api.bria.enums.CallAction;
import com.counterpath.api.bria.enums.CallHoldStatusType;
import com.counterpath.api.bria.enums.CallType;
import com.counterpath.api.bria.enums.DialType;
import com.counterpath.api.bria.enums.MessageBodyType;
import com.counterpath.api.bria.enums.StatusType;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class BriaRemote implements BriaApiClientOpenListener {

	private JFrame frame;
	private JTabbedPane tabbedPane;
	private JPanel panel_1;
	private JTextField callTextField;
	private JTextField activeCallDescriptionTextField0;
	private JTextField activeCallDescriptionTextField1;
	private JTextField activeCallDescriptionTextField2;
	private JTextField activeCallDescriptionTextField3;
	private MessageProcessor messageProcessor;
	private BriaApiTransferClient apiClient;
	private BriaModel briaModel;
	private JLabel labelPhoneState;
	private JLabel labelPhoneCallingAllowed;
	private JLabel labelPhoneAccountStatus;
	private JLabel labelPhoneFailureCode;
	private JLabel labelPhoneMaxLines;
	private JLabel labelAudioPropertyMute;
	private JLabel labelAudioPropertySpeakerMute;
	private JLabel labelAudioPropertySpeaker;
	private JLabel labelAudioPropertySpeakerVolume;
	private JLabel labelAudioPropertyMicrophoneVolume;
	private JCheckBox useVideoCheckbox;
	private JSlider speakerVolumeSlider;
	private JButton speakerMuteButton;
	private JButton microphoneMuteButton;
	private JSlider microphoneVolumeSlider;
	private JButton activeCallEndButton0;
	private JButton activeCallEndButton1;
	private JButton activeCallEndButton2;
	private JButton activeCallEndButton3;
	private JButton activeCallHoldButton1;
	private JButton activeCallHoldButton2;
	private JButton activeCallHoldButton3;
	private JButton activeCallHoldButton0;
	private JButton callVoicemailButton;
	private JCheckBox anonymousCheckbox;
	private JCheckBox sendDigitAsDtmfCheckbox;
	private JTable table;
	private JScrollPane scrollPane;
	private JButton refreshCallHistoryButton;
	private JLabel missedCallsLabel;
	private JPanel panel_3;
	private JLabel lblWebsocketConnectionStatus;
	private JTextArea connectionStatusTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BriaRemote window = new BriaRemote();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BriaRemote() {
		initialize();
		this.briaModel = new BriaModel();
		initializeClient();
	}

	private void requestInitialStatuses() {
		Message audioStatusMessage = new MessageBuilder().status(StatusType.AUDIO_PROPERTIES).build();
		apiClient.writeMessage(audioStatusMessage);

		Message callStatusMessage = new MessageBuilder().status(StatusType.CALL).build();
		apiClient.writeMessage(callStatusMessage);

		Message voicemailStatusMessage = new MessageBuilder().status(StatusType.VOICEMAIL).build();
		apiClient.writeMessage(voicemailStatusMessage);

		Message callHistoryMessage = new MessageBuilder().callHistory(100, CallType.ALL).build();
		apiClient.writeMessage(callHistoryMessage);

		Message missedCallsStatusMessage = new MessageBuilder().status(StatusType.MISSED_CALL).build();
		apiClient.writeMessage(missedCallsStatusMessage);
	}

	private void initializeClient() {
			URI serverUri;
			try {
				serverUri = new URI("wss://cpclientapi.softphone.com:9002/counterpath/socketapi/v1/");
				this.apiClient = new BriaApiTransferClient(serverUri, this);
				apiClient.connectSocket();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			System.err.println("Unable to load Windows look and feel. Reverting to default.");
		}
		frame = new JFrame();
		frame.setBounds(100, 100, 438, 634);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 411, 570);
		frame.getContentPane().add(tabbedPane);

		panel_1 = new JPanel();
		tabbedPane.addTab("Call Center", null, panel_1, null);
		panel_1.setLayout(null);

		final JButton digitButton1 = new JButton("1");
		digitButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// didPushDigit(digitButton1.getText());
			}
		});
		digitButton1.setBounds(10, 52, 50, 35);
		panel_1.add(digitButton1);

		final JButton digitButton2 = new JButton("2");
		digitButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton2.getText());
			}
		});
		digitButton2.setBounds(66, 52, 50, 35);
		panel_1.add(digitButton2);

		final JButton digitButton3 = new JButton("3");
		digitButton3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton3.getText());
			}
		});
		digitButton3.setBounds(122, 52, 50, 35);
		panel_1.add(digitButton3);

		final JButton digitButton4 = new JButton("4");
		digitButton4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton4.getText());
			}
		});
		digitButton4.setBounds(10, 93, 50, 35);
		panel_1.add(digitButton4);

		final JButton digitButton5 = new JButton("5");
		digitButton5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton5.getText());
			}
		});
		digitButton5.setBounds(66, 93, 50, 35);
		panel_1.add(digitButton5);

		final JButton digitButton6 = new JButton("6");
		digitButton6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton6.getText());
			}
		});
		digitButton6.setBounds(122, 93, 50, 35);
		panel_1.add(digitButton6);

		final JButton digitButton7 = new JButton("7");
		digitButton7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton7.getText());
			}
		});
		digitButton7.setBounds(10, 135, 50, 35);
		panel_1.add(digitButton7);

		final JButton digitButton8 = new JButton("8");
		digitButton8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton8.getText());
			}
		});
		digitButton8.setBounds(66, 135, 50, 35);
		panel_1.add(digitButton8);

		final JButton digitButton9 = new JButton("9");
		digitButton9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton9.getText());
			}
		});
		digitButton9.setBounds(122, 135, 50, 35);
		panel_1.add(digitButton9);

		final JButton digitButtonHash = new JButton("#");
		digitButtonHash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButtonHash.getText());
			}
		});
		digitButtonHash.setBounds(122, 176, 50, 35);
		panel_1.add(digitButtonHash);

		final JButton digitButton0 = new JButton("0");
		digitButton0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButton0.getText());
			}
		});
		digitButton0.setBounds(66, 176, 50, 35);
		panel_1.add(digitButton0);

		final JButton digitButtonStar = new JButton("*");
		digitButtonStar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				didPushDigit(digitButtonStar.getText());
			}
		});
		digitButtonStar.setBounds(10, 176, 50, 35);
		panel_1.add(digitButtonStar);

		callTextField = new JTextField();
		callTextField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		callTextField.setBounds(10, 11, 162, 35);
		panel_1.add(callTextField);
		callTextField.setColumns(10);

		JButton btnNewButton = new JButton("Call");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String number = callTextField.getText();
				DialType dialType = useVideoCheckbox.isSelected() ? DialType.VIDEO : DialType.AUDIO;
				Message callMessage = new MessageBuilder().call(dialType, number, "DisplayName").build();
				apiClient.writeMessage(callMessage);
				callTextField.setText("");
			}
		});
		btnNewButton.setBounds(195, 11, 103, 30);
		panel_1.add(btnNewButton);

		useVideoCheckbox = new JCheckBox("Use Video");
		useVideoCheckbox.setBounds(195, 58, 97, 23);
		panel_1.add(useVideoCheckbox);

		JLabel lblNewLabel_1 = new JLabel("Active Calls");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(10, 250, 87, 14);
		panel_1.add(lblNewLabel_1);

		activeCallDescriptionTextField0 = new JTextField();
		activeCallDescriptionTextField0.setEnabled(false);
		activeCallDescriptionTextField0.setBounds(10, 276, 386, 20);
		panel_1.add(activeCallDescriptionTextField0);
		activeCallDescriptionTextField0.setColumns(10);

		activeCallHoldButton0 = new JButton("Hold");
		activeCallHoldButton0.setEnabled(false);
		activeCallHoldButton0.setBounds(107, 301, 89, 23);
		panel_1.add(activeCallHoldButton0);

		activeCallEndButton0 = new JButton("End");
		activeCallEndButton0.setEnabled(false);
		activeCallEndButton0.setBounds(10, 301, 89, 23);
		panel_1.add(activeCallEndButton0);

		activeCallHoldButton1 = new JButton("Hold");
		activeCallHoldButton1.setEnabled(false);
		activeCallHoldButton1.setBounds(107, 370, 89, 23);
		panel_1.add(activeCallHoldButton1);

		activeCallEndButton1 = new JButton("End");
		activeCallEndButton1.setEnabled(false);
		activeCallEndButton1.setBounds(10, 370, 89, 23);
		panel_1.add(activeCallEndButton1);

		activeCallDescriptionTextField1 = new JTextField();
		activeCallDescriptionTextField1.setEnabled(false);
		activeCallDescriptionTextField1.setColumns(10);
		activeCallDescriptionTextField1.setBounds(10, 345, 386, 20);
		panel_1.add(activeCallDescriptionTextField1);

		activeCallHoldButton2 = new JButton("Hold");
		activeCallHoldButton2.setEnabled(false);
		activeCallHoldButton2.setBounds(107, 441, 89, 23);
		panel_1.add(activeCallHoldButton2);

		activeCallEndButton2 = new JButton("End");
		activeCallEndButton2.setEnabled(false);
		activeCallEndButton2.setBounds(10, 441, 89, 23);
		panel_1.add(activeCallEndButton2);

		activeCallDescriptionTextField2 = new JTextField();
		activeCallDescriptionTextField2.setEnabled(false);
		activeCallDescriptionTextField2.setColumns(10);
		activeCallDescriptionTextField2.setBounds(10, 416, 386, 20);
		panel_1.add(activeCallDescriptionTextField2);

		activeCallHoldButton3 = new JButton("Hold");
		activeCallHoldButton3.setEnabled(false);
		activeCallHoldButton3.setBounds(107, 512, 89, 23);
		panel_1.add(activeCallHoldButton3);

		activeCallEndButton3 = new JButton("End");
		activeCallEndButton3.setEnabled(false);
		activeCallEndButton3.setBounds(10, 512, 89, 23);
		panel_1.add(activeCallEndButton3);

		activeCallDescriptionTextField3 = new JTextField();
		activeCallDescriptionTextField3.setEnabled(false);
		activeCallDescriptionTextField3.setColumns(10);
		activeCallDescriptionTextField3.setBounds(10, 487, 386, 20);
		panel_1.add(activeCallDescriptionTextField3);

		JLabel lblMicrophoneVolume = new JLabel("Microphone");
		lblMicrophoneVolume.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMicrophoneVolume.setHorizontalAlignment(SwingConstants.CENTER);
		lblMicrophoneVolume.setBounds(324, 19, 67, 14);
		panel_1.add(lblMicrophoneVolume);

		JLabel lblSpeakerVolume = new JLabel("Speaker");
		lblSpeakerVolume.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSpeakerVolume.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpeakerVolume.setBounds(324, 116, 67, 14);
		panel_1.add(lblSpeakerVolume);

		microphoneVolumeSlider = new JSlider();
		microphoneVolumeSlider.setEnabled(false);
		microphoneVolumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

				if (microphoneVolumeSlider.getValueIsAdjusting()) {
					// The slider is still in the process of being moved.
					return;
				}

				int microphoneVolume = microphoneVolumeSlider.getValue();

				AudioProperties properties = briaModel.getAudioProperties();
				properties.setMicrophoneVolume(microphoneVolume);
				Message audioMessage = new MessageBuilder().updateAudioProperties(properties).build();
				apiClient.writeMessage(audioMessage);
			}
		});
		microphoneVolumeSlider.setBounds(324, 41, 67, 23);
		panel_1.add(microphoneVolumeSlider);

		speakerVolumeSlider = new JSlider();
		speakerVolumeSlider.setEnabled(false);
		speakerVolumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

				int speakerVolume = speakerVolumeSlider.getValue();

				if (speakerVolumeSlider.getValueIsAdjusting()
						|| speakerVolume == briaModel.getAudioProperties().getSpeakerVolume()) {
					return;
				}

				AudioProperties properties = briaModel.getAudioProperties();
				properties.setSpeakerVolume(speakerVolume);

				Message audioMessage = new MessageBuilder().updateAudioProperties(properties).build();
				apiClient.writeMessage(audioMessage);
			}
		});
		speakerVolumeSlider.setBounds(324, 141, 67, 23);
		panel_1.add(speakerVolumeSlider);

		microphoneMuteButton = new JButton("Mute");
		microphoneMuteButton.setEnabled(false);
		microphoneMuteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				microphoneMuteButton.setEnabled(false);
				microphoneVolumeSlider.setEnabled(false);

				boolean setMute = false;
				if (microphoneMuteButton.getText().equals("Mute")) {
					setMute = true;
				} else {
					setMute = false;
				}

				AudioProperties properties = briaModel.getAudioProperties();
				properties.setMicrophoneMuted(setMute);

				Message audioMessage = new MessageBuilder().updateAudioProperties(properties).build();
				apiClient.writeMessage(audioMessage);
			}
		});
		microphoneMuteButton.setBounds(324, 75, 72, 23);
		panel_1.add(microphoneMuteButton);

		speakerMuteButton = new JButton("Unmute");
		speakerMuteButton.setEnabled(false);
		speakerMuteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				speakerMuteButton.setEnabled(false);
				speakerVolumeSlider.setEnabled(false);

				boolean setMute = false;
				if (speakerMuteButton.getText().equals("Mute")) {
					setMute = true;
				} else {
					setMute = false;
				}

				AudioProperties properties = briaModel.getAudioProperties();
				properties.setSpeakerMuted(setMute);

				Message audioMessage = new MessageBuilder().updateAudioProperties(properties).build();
				apiClient.writeMessage(audioMessage);
			}
		});
		speakerMuteButton.setBounds(324, 180, 72, 23);
		panel_1.add(speakerMuteButton);

		JButton bringToFrontButton = new JButton("Bring to front");
		bringToFrontButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Message bringToFrontMessage = new MessageBuilder().bringToFront().build();
				apiClient.writeMessage(bringToFrontMessage);
			}
		});
		bringToFrontButton.setBounds(195, 180, 103, 23);
		panel_1.add(bringToFrontButton);

		anonymousCheckbox = new JCheckBox("Anonymous");
		anonymousCheckbox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				boolean anon = anonymousCheckbox.isSelected();
				Message setAnonMessage = new MessageBuilder().setAnonymousCalling(anon).build();
				apiClient.writeMessage(setAnonMessage);
			}
		});
		anonymousCheckbox.setBounds(195, 97, 97, 23);
		panel_1.add(anonymousCheckbox);

		callVoicemailButton = new JButton("Call Voicemail");
		callVoicemailButton.setEnabled(false);
		callVoicemailButton.setBounds(195, 138, 103, 23);
		panel_1.add(callVoicemailButton);

		sendDigitAsDtmfCheckbox = new JCheckBox("Send dialpad as DTMF");
		sendDigitAsDtmfCheckbox.setBounds(10, 220, 138, 23);
		panel_1.add(sendDigitAsDtmfCheckbox);

		missedCallsLabel = new JLabel("");
		missedCallsLabel.setBounds(83, 250, 113, 14);
		panel_1.add(missedCallsLabel);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Call History", null, panel_2, null);
		panel_2.setLayout(null);

		refreshCallHistoryButton = new JButton("Refresh");
		refreshCallHistoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Message callHistoryMessage = new MessageBuilder().callHistory(100, CallType.ALL).build();
				apiClient.writeMessage(callHistoryMessage);
			}
		});
		refreshCallHistoryButton.setBounds(10, 289, 89, 23);
		panel_2.add(refreshCallHistoryButton);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 386, 267);
		panel_2.add(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setShowVerticalLines(false);
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setRowSelectionAllowed(false);
		table.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null, null }, { null, null, null, null, null },
						{ null, null, null, null, null }, { null, null, null, null, null }, },
				new String[] { "Type", "Name", "Number", "Duration", "Time Initiated" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(4).setResizable(false);
		table.setFillsViewportHeight(true);

		JPanel panel = new JPanel();
		tabbedPane.addTab("System Status", null, panel, null);
		panel.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
				new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		JLabel lblPhoneStatus = new JLabel("Phone status");
		lblPhoneStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPhoneStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
		panel.add(lblPhoneStatus, "2, 2");

		JLabel lblState = new JLabel("State:");
		lblState.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblState, "2, 4");

		labelPhoneState = new JLabel("Unknown");
		panel.add(labelPhoneState, "4, 4");

		JLabel lblNewLabel_2 = new JLabel("Calling allowed:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel_2, "2, 6");

		labelPhoneCallingAllowed = new JLabel("Unknown");
		panel.add(labelPhoneCallingAllowed, "4, 6");

		JLabel lblAccountStatus = new JLabel("Account status:");
		lblAccountStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblAccountStatus, "2, 8");

		labelPhoneAccountStatus = new JLabel("Unknown");
		panel.add(labelPhoneAccountStatus, "4, 8");

		JLabel lblFailureCode = new JLabel("Failure code:");
		lblFailureCode.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblFailureCode, "2, 10");

		labelPhoneFailureCode = new JLabel("Unknown");
		panel.add(labelPhoneFailureCode, "4, 10");

		JLabel lblMaxLines = new JLabel("Max lines:");
		lblMaxLines.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblMaxLines, "2, 12");

		labelPhoneMaxLines = new JLabel("Unknown");
		panel.add(labelPhoneMaxLines, "4, 12");

		JLabel lblAudioProperties = new JLabel("Audio properties");
		lblAudioProperties.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAudioProperties.setFont(new Font("Segoe UI", Font.BOLD, 12));
		panel.add(lblAudioProperties, "2, 14");

		JLabel lblMute = new JLabel("Mute:");
		lblMute.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblMute, "2, 16");

		labelAudioPropertyMute = new JLabel("Unknown");
		panel.add(labelAudioPropertyMute, "4, 16");

		JLabel lblSpeakerMute = new JLabel("Speaker mute:");
		lblSpeakerMute.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblSpeakerMute, "2, 18");

		labelAudioPropertySpeakerMute = new JLabel("Unknown");
		panel.add(labelAudioPropertySpeakerMute, "4, 18");

		JLabel lblSpeaker = new JLabel("Speaker:");
		lblSpeaker.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblSpeaker, "2, 20");

		labelAudioPropertySpeaker = new JLabel("Unknown");
		panel.add(labelAudioPropertySpeaker, "4, 20");

		JLabel lblSpeakerVolume_1 = new JLabel("Speaker volume:");
		lblSpeakerVolume_1.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblSpeakerVolume_1, "2, 22");

		labelAudioPropertySpeakerVolume = new JLabel("Unknown");
		panel.add(labelAudioPropertySpeakerVolume, "4, 22");

		JLabel lblMicrophoneVolume_1 = new JLabel("Microphone volume:");
		lblMicrophoneVolume_1.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblMicrophoneVolume_1, "2, 24");

		labelAudioPropertyMicrophoneVolume = new JLabel("Unknown");
		panel.add(labelAudioPropertyMicrophoneVolume, "4, 24");

		panel_3 = new JPanel();
		panel_3.setLayout(null);
		tabbedPane.addTab("Connection Status", null, panel_3, null);

		lblWebsocketConnectionStatus = new JLabel("WebSocket Connection Status");
		lblWebsocketConnectionStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblWebsocketConnectionStatus.setBounds(10, 11, 190, 14);
		panel_3.add(lblWebsocketConnectionStatus);

		connectionStatusTextField = new JTextArea();
		connectionStatusTextField.setEnabled(false);
		connectionStatusTextField.setColumns(10);
		connectionStatusTextField.setBounds(10, 37, 386, 402);
		connectionStatusTextField.setLineWrap(true);
		panel_3.add(connectionStatusTextField);

		JButton btnReconnect = new JButton("Reconnect");
		btnReconnect.setBounds(293, 450, 103, 30);
		panel_3.add(btnReconnect);
		btnReconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apiClient.connectSocket();
			}
		});
	}

	private void didPushDigit(String digit) {

		boolean sendAsDtmf = sendDigitAsDtmfCheckbox.isSelected();

		if (sendAsDtmf) {
			Message dtmfMessage = new MessageBuilder().sendDTMF(digit).build();
			apiClient.writeMessage(dtmfMessage);
		} else {
			callTextField.setText(callTextField.getText() + digit);
		}

	}

	public void onClientConnected() {
		this.messageProcessor = apiClient.getMessageProcessor();

		this.messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_PHONE,
				new MessageHandler() {
					public void handle(Message message) {

						PhoneStatus status = MessageBodyParser.parsePhoneStatusResponse(message.getXmlDocument());

						briaModel.setPhoneStatus(status);

						labelPhoneState.setText(status.isPhoneReady() ? "ready" : "notReady");
						labelPhoneCallingAllowed.setText(status.isCallAllowed() + "");
						labelPhoneAccountStatus.setText(status.getAccountStatus().toString());
						labelPhoneFailureCode.setText(status.getAccountFailureCode() + "");
						labelPhoneMaxLines.setText(status.getAccountFailureCode() + "");
					}
				});

		this.messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_AUDIO_PROPERTIES,
				new MessageHandler() {

					public void handle(Message message) {
						Document body = message.getXmlDocument();
						AudioProperties properties = MessageBodyParser.parseAudioPropertiesStatusResponse(body);

						briaModel.setAudioProperties(properties);

						labelAudioPropertyMute.setText(properties.isMicrophoneMuted() + "");
						labelAudioPropertySpeakerMute.setText(properties.isSpeakerMuted() + "");
						labelAudioPropertySpeaker.setText(properties.isSpeakerModeEnabled() + "");
						labelAudioPropertySpeakerVolume.setText(properties.getSpeakerVolume() + "");
						labelAudioPropertyMicrophoneVolume.setText(properties.getMicrophoneVolume() + "");

						speakerVolumeSlider.setValueIsAdjusting(true);
						speakerVolumeSlider.setValue(properties.getSpeakerVolume());
						speakerVolumeSlider.setValueIsAdjusting(false);

						if (properties.isSpeakerMuted()) {
							speakerVolumeSlider.setEnabled(false);
							speakerMuteButton.setText("Unmute");
						} else {
							speakerVolumeSlider.setEnabled(true);
							speakerMuteButton.setText("Mute");
						}

						speakerMuteButton.setEnabled(true);
						microphoneMuteButton.setEnabled(true);

						if (properties.isMicrophoneMuted()) {
							microphoneVolumeSlider.setEnabled(false);
							microphoneMuteButton.setText("Unmute");
						} else {
							microphoneVolumeSlider.setEnabled(true);
							microphoneMuteButton.setText("Mute");
						}
					}
				});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_CALL, new MessageHandler() {
			public void handle(Message message) {

				Document body = message.getXmlDocument();
				List<Call> calls = MessageBodyParser.parseCallStatusResponse(body);
				briaModel.setActiveCalls(calls);

				for (int i = 0; i < 4; i++) {

					JTextField descriptionField;
					final JButton endButton;
					final JButton holdButton;

					switch (i) {
					case 0:
						descriptionField = activeCallDescriptionTextField0;
						endButton = activeCallEndButton0;
						holdButton = activeCallHoldButton0;
						break;
					case 1:
						descriptionField = activeCallDescriptionTextField1;
						endButton = activeCallEndButton1;
						holdButton = activeCallHoldButton1;
						break;
					case 2:
						descriptionField = activeCallDescriptionTextField2;
						endButton = activeCallEndButton2;
						holdButton = activeCallHoldButton2;
						break;
					case 3:
						descriptionField = activeCallDescriptionTextField3;
						endButton = activeCallEndButton3;
						holdButton = activeCallHoldButton3;
						break;
					default:
						throw new IllegalStateException("Update code to handle more active calls");
					}

					if (calls.size() <= i) {
						// There is no call for this slot, so empty the fields
						// and mark as disabled
						descriptionField.setText("");
						descriptionField.setEnabled(false);

						endButton.setText("End");
						endButton.setEnabled(false);

						holdButton.setText("Hold");
						holdButton.setEnabled(false);
					} else {
						// We populate the field with call info
						Call call = calls.get(i);
						final String callId = call.getId();
						descriptionField.setText(call.toString());

						// End/answer button config
						endButton.setEnabled(true);
						ActionListener[] endListeners = endButton.getActionListeners();
						for (ActionListener listener : endListeners) {
							endButton.removeActionListener(listener);
						}

						if (call.isRinging()) {
							endButton.setText("Answer");
							endButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									endButton.setEnabled(false);
									Message answerMessage = new MessageBuilder().callAction(CallAction.ANSWER, callId)
											.build();
									apiClient.writeMessage(answerMessage);
								}
							});
						} else {
							endButton.setText("End");
							endButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									endButton.setEnabled(false);
									Message endMessage = new MessageBuilder().callAction(CallAction.END, callId)
											.build();
									apiClient.writeMessage(endMessage);
								}
							});
						}

						// Hold/resume button config
						holdButton.setEnabled(true);
						ActionListener[] listeners = holdButton.getActionListeners();
						for (ActionListener listener : listeners) {
							holdButton.removeActionListener(listener);
						}

						if (call.getHoldStatus() == CallHoldStatusType.OFF_HOLD
								|| call.getHoldStatus() == CallHoldStatusType.REMOTE_HOLD) {
							holdButton.setText("Hold");
							holdButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent arg0) {
									holdButton.setEnabled(false);
									Message holdMessage = new MessageBuilder().callAction(CallAction.HOLD, callId)
											.build();
									apiClient.writeMessage(holdMessage);
								}
							});
						} else {
							holdButton.setText("Resume");
							holdButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent arg0) {
									holdButton.setEnabled(false);
									Message resumeMessage = new MessageBuilder().callAction(CallAction.RESUME, callId)
											.build();
									apiClient.writeMessage(resumeMessage);
								}
							});
						}
					}
				}
			}
		});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_VOICEMAIL, new MessageHandler() {
			public void handle(Message message) {

				List<VoicemailAccount> voicemailAccounts = MessageBodyParser
						.parseVoicemailStatusResponse(message.getXmlDocument());
				briaModel.setVoicemailAccounts(voicemailAccounts);

				if (voicemailAccounts.size() >= 1) {

					VoicemailAccount account = voicemailAccounts.get(0);
					final String voicemailId = account.getId();
					int vmItems = account.getItemCount();

					ActionListener[] listeners = callVoicemailButton.getActionListeners();
					for (ActionListener listener : listeners) {
						callVoicemailButton.removeActionListener(listener);
					}
					callVoicemailButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Message callVoicemailMessage = new MessageBuilder().checkVoicemail(voicemailId).build();
							apiClient.writeMessage(callVoicemailMessage);
						}
					});
					callVoicemailButton.setEnabled(true);
					callVoicemailButton.setText("Call Voicemail (" + vmItems + ")");
				} else {
					callVoicemailButton.setToolTipText("No voicemail accounts configured");
				}
			}
		});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_CALL_OPTIONS,
				new MessageHandler() {
					public void handle(Message message) {
						CallOptions options = MessageBodyParser
								.parseCallOptionsStatusResponse(message.getXmlDocument());
						briaModel.setCallOptions(options);
						anonymousCheckbox.setSelected(options.isAnonymous());
					}
				});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_CALL_HISTORY,
				new MessageHandler() {
					public void handle(Message message) {
						List<CallHistoryEntry> callHistoryEntries = MessageBodyParser
								.parseCallHistoryStatusResponse(message.getXmlDocument());
						briaModel.setCallHistoryEntries(callHistoryEntries);

						DefaultTableModel model = new DefaultTableModel(
								new Object[] { "Type", "Name", "Number", "Duration", "Time Initiated" }, 0);
						model.setColumnCount(5);
						for (int i = 0; i < callHistoryEntries.size(); i++) {
							CallHistoryEntry entry = callHistoryEntries.get(i);
							Object[] rowData = { entry.getType(), entry.getDisplayName(), entry.getNumber(),
									entry.getDuration(), entry.getTimeInitiated() };
							model.insertRow(i, rowData);
						}
						table.setModel(model);
					}
				});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_MISSED_CALL,
				new MessageHandler() {
					public void handle(Message message) {
						int numMissedCalls = MessageBodyParser.parseMissedCallStatusResponse(message.getXmlDocument());
						briaModel.setNumberMissedCalls(numMissedCalls);
						if (numMissedCalls <= 0) {
							missedCallsLabel.setText("");
						} else if (numMissedCalls == 1) {
							missedCallsLabel.setText("(1 missed call)");
						} else {
							missedCallsLabel.setText("(" + numMissedCalls + " missed calls)");
						}
					}
				});

		messageProcessor.startProcessing();

		requestInitialStatuses();

		connectionStatusTextField.setText("CONNECTED");
	}

	@Override
	public void onConnectionError(String errorMsg) {
		connectionStatusTextField.setText("ERROR Could not connect to WebSocket\nClick 'Reconnect' button to try again.\n" + errorMsg);
		tabbedPane.setSelectedIndex(3);
	}
}
