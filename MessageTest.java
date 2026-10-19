/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchatpart2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Katleho
 */
public class MessageTest {
    @Test
    public void testValidMessageID() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hello");

        assertTrue(message.checkMessageID());
    }

    @Test
    public void testInvalidMessageID() {
        Message message = new Message(
                "12345", 0, "+27718693002", "Hello");

        assertFalse(message.checkMessageID());
    }

    @Test
    public void testValidRecipient() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hello");

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell());
    }

    @Test
    public void testInvalidRecipient() {
        Message message = new Message(
                "0012345678", 0, "08575975889", "Hello");

        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                message.checkRecipientCell());
    }

    @Test
    public void testValidMessageLength() {
        Message message = new Message(
                "0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");

        assertEquals(
                "Message ready to send.",
                message.checkMessageLength());
    }

    @Test
    public void testMessageTooLong() {
        String longMessage = "A".repeat(260);

        Message message = new Message(
                "0012345678", 0, "+27718693002", longMessage);

        assertEquals(
                "Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    @Test
    public void testMessageHash() {
        Message message = new Message(
                "0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");

        assertEquals(
                "00:0:HITONIGHT",
                message.createMessageHash());
    }

    @Test
    public void testSendMessage() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hello");

        assertEquals(
                "Message successfully sent.",
                message.SentMessage(1));

        assertEquals("Sent", message.getStatus());
    }

    @Test
    public void testDisregardMessage() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hello");

        assertEquals(
                "Press 0 to delete the message.",
                message.SentMessage(2));

        assertEquals("Disregarded", message.getStatus());
    }

    @Test
    public void testStoreMessage() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hello");

        assertEquals(
                "Message successfully stored.",
                message.SentMessage(3));

        assertEquals("Stored", message.getStatus());
    }

    @Test
    public void testPrintMessages() {
        Message message = new Message(
                "0012345678", 0, "+27718693002", "Hello");

        String result = message.printMessages();

        assertTrue(result.contains("0012345678"));
        assertTrue(result.contains("00:0:HELLOHELLO"));
        assertTrue(result.contains("+27718693002"));
        assertTrue(result.contains("Hello"));
    }

    @Test
    public void testGeneratedMessageID() {
        Message message = new Message(
                0, "+27718693002", "Hello");

        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
    }
}
