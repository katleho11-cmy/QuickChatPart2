/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchatpart2;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.swing.JOptionPane;


/** QuickChat POE Part 2: pop-up user interface with a separate Message model. */
public class QuickChatPart2 {
    private static final Path JSON_FILE = Path.of("messages.json");
    private static String accountName;
    private static String accountPassword;
    private static int messageSequence = 0;
    private static int maximumMessages = 0;

    public static void main(String[] args) {
        if (!createAccount() || !signIn()) return;
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");
        String count = JOptionPane.showInputDialog(null, "How many messages would you like to enter?");
        if (count == null) return;
        try {
            maximumMessages = Integer.parseInt(count.trim());
            if (maximumMessages < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Enter a valid non-negative whole number.");
            return;
        }
        showMenu();
    }

    private static boolean createAccount() {
        while (true) {
            accountName = JOptionPane.showInputDialog(null, "Create username (underscore and at most 5 characters):");
            if (accountName == null) return false;
            if (accountName.contains("_") && accountName.length() <= 5) break;
            JOptionPane.showMessageDialog(null, "Username must contain _ and have at most 5 characters.");
        }
        while (true) {
            accountPassword = JOptionPane.showInputDialog(null, "Create password (8+ characters, capital, digit, special):");
            if (accountPassword == null) return false;
            if (accountPassword.matches("(?s)^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$")) break;
            JOptionPane.showMessageDialog(null, "Password does not meet the requirements.");
        }
        while (true) {
            String phone = JOptionPane.showInputDialog(null, "Enter SA cellphone (+27 followed by 9 digits):");
            if (phone == null) return false;
            if (phone.matches("\\+27[0-9]{9}")) break;
            JOptionPane.showMessageDialog(null, "Invalid international SA cellphone number.");
        }
        JOptionPane.showMessageDialog(null, "Registration successful.");
        return true;
    }

    private static boolean signIn() {
        for (int attempts = 0; attempts < 3; attempts++) {
            String name = JOptionPane.showInputDialog(null, "Login username:");
            if (name == null) return false;
            String password = JOptionPane.showInputDialog(null, "Login password:");
            if (password == null) return false;
            if (accountName.equals(name) && accountPassword.equals(password)) {
                JOptionPane.showMessageDialog(null, "Login successful.");
                return true;
            }
            JOptionPane.showMessageDialog(null, "Incorrect username or password.");
        }
        return false;
    }

    private static void showMenu() {
        while (true) {
            String choice = JOptionPane.showInputDialog(null,
                    "1) Send Messages\n2) Show recently sent messages\n3) Quit\n\nChoose 1-3:");
            if (choice == null || choice.trim().equals("3")) break;
            switch (choice.trim()) {
                case "1" -> composeMessage();
                case "2" -> JOptionPane.showMessageDialog(null, "Coming Soon.");
                default -> JOptionPane.showMessageDialog(null, "Choose 1, 2 or 3.");
            }
        }
        JOptionPane.showMessageDialog(null, "Total messages sent: " + Message.returnTotalMessagess());
    }

    private static void composeMessage() {
        if (messageSequence >= maximumMessages) {
            JOptionPane.showMessageDialog(null, "You have reached your message limit.");
            return;
        }
        String recipient = JOptionPane.showInputDialog(null, "Recipient (+27 followed by 9 digits):");
        if (recipient == null) return;
        String body = JOptionPane.showInputDialog(null, "Write a message (maximum 250 characters):");
        if (body == null) return;
        Message draft = new Message(messageSequence, recipient, body);
        if (!draft.checkMessageID()) {
            JOptionPane.showMessageDialog(null, "Unable to generate a valid message ID.");
            return;
        }
        if (!"Cell phone number successfully captured.".equals(draft.checkRecipientCell())) {
            JOptionPane.showMessageDialog(null, draft.checkRecipientCell());
            return;
        }
        if (!"Message ready to send.".equals(draft.checkMessageLength())) {
            JOptionPane.showMessageDialog(null, draft.checkMessageLength());
            return;
        }
        String choice = JOptionPane.showInputDialog(null,
                "1) Send Message\n2) Disregard Message\n3) Store Message to send later");
        if (choice == null) return;
        int selected;
        try { selected = Integer.parseInt(choice.trim()); }
        catch (NumberFormatException ex) { selected = -1; }
        if (selected < 1 || selected > 3) {
            JOptionPane.showMessageDialog(null, "Invalid selection.");
            return;
        }
        if (selected == 2) {
            String confirm = JOptionPane.showInputDialog(null, "Press 0 to delete the message");
            if (!"0".equals(confirm)) return;
        }
        String result = draft.SentMessage(selected);
        if (selected == 3) {
            try { storeMessage(draft); }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Could not store message: " + ex.getMessage());
                return;
            }
        }
        messageSequence++;
        JOptionPane.showMessageDialog(null, result);
        if (selected == 1) JOptionPane.showMessageDialog(null, draft.printMessages());
        if (selected == 1) JOptionPane.showMessageDialog(null, "Message sent");
    }

    /** Append a JSON object to a valid JSON array, preserving existing stored records. */
    private static void storeMessage(Message message) throws IOException {
        String item = "{\"messageID\":\"" + escape(message.getMessageID())
                + "\",\"messageHash\":\"" + escape(message.getMessageHash())
                + "\",\"recipient\":\"" + escape(message.getRecipient())
                + "\",\"message\":\"" + escape(message.getMessageText()) + "\"}";
        String old = Files.exists(JSON_FILE) ? Files.readString(JSON_FILE, StandardCharsets.UTF_8).trim() : "[]";
        if (!old.startsWith("[") || !old.endsWith("]")) {
            throw new IOException("Existing messages.json is not a JSON array; original file was preserved.");
        }
        String inner = old.substring(1, old.length() - 1).trim();
        String updated = "[\n" + (inner.isEmpty() ? "" : inner + ",\n") + "  " + item + "\n]\n";
        Files.writeString(JSON_FILE, updated, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
