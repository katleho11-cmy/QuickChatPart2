/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchatpart2;
import java.util.Random;

/**
 *
 * @author Katleho
 */
public class Message {
    private static final Random RANDOM = new Random();
    private static int totalSent = 0;
    private final String messageID;
    private final int sequence;
    private final String recipient;
    private final String messageText;
    private String status = "Created";

    public Message(int sequence, String recipient, String messageText) {
        this(String.format("%010d", RANDOM.nextLong(10_000_000_000L)), sequence, recipient, messageText);
    }

    // Explicit ID constructor permits repeatable unit tests.
    public Message(String id, int sequence, String recipient, String messageText) {
        this.messageID = id;
        this.sequence = sequence;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("[0-9]{10}");
    }

    public String checkRecipientCell() {
        // The POE examples use +27 and 9 digits; lecturer clarified SA formatting.
        if (recipient != null && recipient.matches("\\+27[0-9]{9}")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {
        if (messageText != null && !messageText.isBlank() && messageText.length() <= 250) {
            return "Message ready to send.";
        }
        int excess = messageText == null ? 0 : Math.max(0, messageText.length() - 250);
        return excess > 0 ? "Message exceeds 250 characters by " + excess + "; please reduce the size."
                : "Please enter a message of less than 250 characters.";
    }

    public String createMessageHash() {
        if (messageText == null || messageText.isBlank()) return "";
        String[] words = messageText.trim().split("\\s+");
        String first = words[0].replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");
        String last = words[words.length - 1].replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");
        return (messageID.substring(0, 2) + ":" + sequence + ":" + first + last).toUpperCase();
    }

    public String SentMessage(int selection) {
        switch (selection) {
            case 1:
                if (!"Sent".equals(status)) totalSent++;
                status = "Sent";
                return "Message successfully sent.";
            case 2:
                status = "Disregarded";
                return "Press 0 to delete the message.";
            case 3:
                status = "Stored";
                return "Message successfully stored.";
            default:
                return "Invalid selection.";
        }
    }

    public String printMessages() {
        return "Message ID: " + messageID + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient + "\nMessage: " + messageText;
    }

    public static int returnTotalMessagess() { return totalSent; }
    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getStatus() { return status; }
    public String getMessageHash() { return createMessageHash(); }
}
