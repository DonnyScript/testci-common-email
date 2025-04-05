package org.apache.commons.mail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static org.junit.Assert.*;

import java.util.Date;

public class EmailTest {

    // Array of test email addresses to be used in various tests
    private static final String[] TEST_EMAILS = {"ab@BC.com", "a.b@c.org",
            "asdfaklsdfalskfdlasdfk@asdlfaksdfj.com.bd"};

    // Two instances of EmailConcrete used for testing
    private EmailConcrete email;
    private EmailConcrete mimeEmail;

    // This method runs before each test to set up new EmailConcrete objects
    @Before
    public void setUpEmailTest() throws Exception {
        email = new EmailConcrete();
        mimeEmail = new EmailConcrete();
    }

    // This method runs after each test to tear down (nullify) the email objects
    @After
    public void tearDownEmailTest() throws Exception {
        email = null;
        mimeEmail = null;
    }

    // Test adding multiple valid BCC emails using an array
    @Test
    public void testAddBccWithValidEmails() throws Exception {
        email.addBcc(TEST_EMAILS);
        // Check if the BCC address list has 3 addresses
        assertEquals(3, email.getBccAddresses().size());
    }

    // Test that passing null to addBcc throws an EmailException
    @Test(expected = EmailException.class)
    public void testAddBccWithNull() throws Exception {
        email.addBcc((String[]) null);
    }

    // Test that passing an empty array to addBcc throws an EmailException
    @Test(expected = EmailException.class)
    public void testAddBccWithEmptyArray() throws Exception {
        email.addBcc(new String[]{});
    }

    // Test adding a single valid BCC email address
    @Test
    public void testAddBccWithSingleEmail() throws Exception {
        email.addBcc("test@example.com");
        // Expecting exactly one BCC address
        assertEquals(1, email.getBccAddresses().size());
    }

    // Test adding multiple valid CC emails using an array
    @Test
    public void testAdCcWithValidEmails() throws Exception {
        email.addCc(TEST_EMAILS);
        // Check if the CC address list has 3 addresses
        assertEquals(3, email.getCcAddresses().size());
    }

    // Test that passing null to addCc throws an EmailException
    @Test(expected = EmailException.class)
    public void testAddCcWithNull() throws Exception {
        email.addCc((String[]) null);
    }

    // Test that passing an empty array to addCc throws an EmailException
    @Test(expected = EmailException.class)
    public void testAddCcWithEmptyArray() throws Exception {
        email.addCc(new String[]{});
    }

    // Test adding a single valid CC email address
    @Test
    public void testAddCcWithSingleEmail() throws Exception {
        email.addCc("test@example.com");
        // Expecting exactly one CC address
        assertEquals(1, email.getCcAddresses().size());
    }

    // Test adding a header to the email
    @Test
    public void testAddHeaderToEmail() throws Exception {
        email.addHeader("Don", "don@gmail.com");
        // Check if the header list size is 1 after adding one header
        assertEquals(1, email.getHeaders().size());
    }

    // Test that adding a header with an empty name throws IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyName() throws Exception {
        email.addHeader("", "don@gmail.com");
    }

    // Test that adding a header with an empty value throws IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderWithEmptyArray() throws Exception {
        email.addHeader("don", "");
    }

    // Test adding a single reply-to email address
    @Test
    public void testaddReplyToSingleEmail() throws Exception {
        email.addReplyTo("don@gmail.com");
        // Verify that one reply-to address is added
        assertEquals(1, email.getReplyToAddresses().size());
    }

    // Test building a MIME message; building it twice should throw an EmailException
    @Test(expected = EmailException.class)
    public void testMimeMessage() throws Exception {
        mimeEmail.addBcc(TEST_EMAILS);
        mimeEmail.addCc(TEST_EMAILS);
        mimeEmail.addHeader("Don", "don@gmail.com");

        mimeEmail.setHostName("localhost");
        mimeEmail.setSubject("Test Subject");
        mimeEmail.setBounceAddress("donMimeTest@gmail.com");
        mimeEmail.setMsg((String) null);

        // Build MIME message first time
        mimeEmail.buildMimeMessage();
        // Building the MIME message a second time should cause an exception
        mimeEmail.buildMimeMessage(); // This should throw IllegalStateException wrapped in EmailException

        // This assertion should not be reached because an exception is expected above
        assertEquals(1, mimeEmail.getMimeMessage().getSize());
    }

    // Test building a MIME message with an empty subject and verifying recipients count
    @Test
    public void testMimeMessageEmptySubject() throws Exception {
        mimeEmail.addBcc(TEST_EMAILS);
        mimeEmail.addCc(TEST_EMAILS);
        mimeEmail.addHeader("Don", "don@gmail.com");
        mimeEmail.addReplyTo("jared@gmail.com");

        mimeEmail.setHostName("localhost");
        mimeEmail.setSubject("Test Subject");
        mimeEmail.setBounceAddress("donMimeTest@gmail.com");
        mimeEmail.setMsg("TEST");

        mimeEmail.buildMimeMessage();
        MimeMessage msg = mimeEmail.getMimeMessage();

        // Verify that BCC and CC recipient counts are both 3
        assertEquals(3, msg.getRecipients(Message.RecipientType.BCC).length); 
        assertEquals(3, msg.getRecipients(Message.RecipientType.CC).length);
    }

    // Test building a MIME message with plain text content and a specific charset (UTF-8)
    @Test
    public void testBuildMimeMessageTextPlainWithCharset() throws Exception {
        mimeEmail.setHostName("localhost");
        mimeEmail.setFrom("sender@gmail.com");
        mimeEmail.addTo("recipient@gmail.com");
        mimeEmail.setCharset("UTF-8");
        mimeEmail.setContent("Test Content", "text/plain");

        mimeEmail.buildMimeMessage();

        MimeMessage msg = mimeEmail.getMimeMessage();

        // Check if the content is exactly "Test Content"
        assertEquals("Test Content", msg.getContent());
        // Verify that the content type contains "text/plain"
        assertTrue(msg.getContentType().contains("text/plain"));
    }

    // Test building a MIME message with plain text content without specifying charset explicitly
    @Test
    public void testBuildMimeMessageTextPlainNoCharset() throws Exception {
        mimeEmail.setHostName("localhost");
        mimeEmail.setFrom("sender@gmail.com");
        mimeEmail.addTo("recipient@gmail.com");
        mimeEmail.setContent("Test Content", EmailConstants.TEXT_PLAIN);

        mimeEmail.buildMimeMessage();

        MimeMessage msg = mimeEmail.getMimeMessage();
        // Assert that the content is "Test Content"
        assertEquals("Test Content", msg.getContent());
        // Verify that the content type includes "text/plain"
        assertTrue(msg.getContentType().contains("text/plain"));
    }

    // Test building a MIME message with non-string content (an integer)
    @Test
    public void testBuildMimeMessageNonStringContent() throws Exception {

        mimeEmail.setHostName("localhost");
        mimeEmail.setFrom("sender@gmail.com");
        mimeEmail.addTo("recipient@gmail.com");
        mimeEmail.setCharset("UTF-8");
        mimeEmail.setContent(42, "text/plain");

        mimeEmail.buildMimeMessage();

        MimeMessage msg = mimeEmail.getMimeMessage();

        // Check that the content is the integer 42
        assertEquals(42, msg.getContent());
        // Verify that the content type contains "text/plain"
        assertTrue(msg.getContentType().contains("text/plain"));
    }

    // Test building a MIME message using a MimeMultipart content without a defined content type
    @Test
    public void testBuildMimeMessageWithMultipartNoContentType() throws Exception {
        mimeEmail.setHostName("localhost");
        mimeEmail.setFrom("sender@gmail.com");
        mimeEmail.addTo("recipient@gmail.com");
        MimeMultipart multipart = new MimeMultipart();
        mimeEmail.setContent(multipart);

        mimeEmail.buildMimeMessage();

        MimeMessage msg = mimeEmail.getMimeMessage();

        // Verify that the content is an instance of MimeMultipart
        assertTrue(msg.getContent() instanceof MimeMultipart);
        // Also, check that a default content type containing "text/plain" is set
        assertTrue(msg.getContentType().contains("text/plain"));
    }

    // Test updating the content type by passing null, which should default to "text/plain"
    @Test
    public void testUpdateContentTypeNull() throws Exception {
        mimeEmail.setHostName("localhost");
        mimeEmail.setFrom("sender@gmail.com");
        mimeEmail.addTo("recipient@gmail.com");
        mimeEmail.setContent("Test Content", null);

        mimeEmail.buildMimeMessage();

        MimeMessage msg = mimeEmail.getMimeMessage();
        // Verify content remains "Test Content"
        assertEquals("Test Content", msg.getContent());
        // Default content type should include "text/plain"
        assertTrue(msg.getContentType().contains("text/plain"));
        // Confirm the host name is still "localhost"
        assertEquals("localhost", mimeEmail.getHostName());
    }
    
    // Test that the getHostName method returns the correct host name
    @Test
    public void testGetHostName() throws Exception 
    {
        mimeEmail.setHostName("localhost");
        assertEquals("localhost", mimeEmail.getHostName());
    }
    
    // Test that the host name remains unchanged after building the MIME message even if content type is null
    @Test
    public void testContetnNameNull() throws Exception {
        mimeEmail.setHostName("localhost");
        mimeEmail.setFrom("sender@gmail.com");
        mimeEmail.addTo("recipient@gmail.com");
        mimeEmail.setContent("Test Content", null);

        mimeEmail.buildMimeMessage();

        // Host name should still be "localhost"
        assertEquals("localhost", mimeEmail.getHostName());
    }
    
    // Test that setting a null host name results in an EmailException when trying to get the host name
    @Test(expected = EmailException.class)
    public void testHostNameNullNull() throws Exception {
        mimeEmail.setHostName(null);
        mimeEmail.setFrom("sender@gmail.com");
        mimeEmail.addTo("recipient@gmail.com");
        mimeEmail.setContent("Test Content", null);

        // This assertion is expected to fail because host name is null and should throw an exception
        assertEquals("localhost", mimeEmail.getHostName());
    }
    
    // Test that attempting to get a mail session with a null host name throws an EmailException
    @Test(expected = EmailException.class)
    public void testGetMailSessionHostNameNull() throws Exception 
    {
         mimeEmail.setHostName(null);
         mimeEmail.setFrom("sender@gmail.com");
         mimeEmail.addTo("recipient@gmail.com");
         mimeEmail.setContent("Test Content", null);

         // Attempt to retrieve the mail session; should throw an EmailException because host name is null
         mimeEmail.getMailSession();
    }
    
    // Test setting and getting the sent date of the MIME message
    @Test
    public void testGetSentDate() throws Exception
    {	
            mimeEmail.setHostName("localhost");
            mimeEmail.setFrom("sender@gmail.com");
            mimeEmail.addTo("recipient@gmail.com");
            mimeEmail.setSubject("Test Subject");
            mimeEmail.setMsg("Test Content");

            // Create a specific date: March 18, 2025 (year is offset by 1900)
            Date expectedDate = new Date(2025 - 1900, 2, 18); // March 18, 2025
            mimeEmail.setSentDate(expectedDate);

            mimeEmail.buildMimeMessage();

            MimeMessage msg = mimeEmail.getMimeMessage();
            // Check if the sent date in the MIME message matches the expected date
            assertEquals(expectedDate, msg.getSentDate());
    }
    
    // Test setting and retrieving the socket connection timeout value
    @Test
    public void testGetSocketConnectionTimeout() throws Exception
    {
            mimeEmail.setHostName("localhost");
            mimeEmail.setFrom("sender@gmail.com");
            mimeEmail.addTo("recipient@gmail.com");
            mimeEmail.setSubject("Test Subject");
            mimeEmail.setMsg("Test Content");
            // Set the socket connection timeout to 10
            mimeEmail.setSocketConnectionTimeout(10);
	    
            // Verify that the timeout value is set correctly
            assertEquals(10, mimeEmail.getSocketConnectionTimeout());
    }
    
}
