package service;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/* class to demonstrate use of Gmail list messages */
public class ClassGmail {
    /**
     * Application name.
     */
    private String currentFullMain;
    private static final String USER_TEST =  "wilfrieddev0@gmail.com";
    private static final String APPLICATION_NAME = "test API Gmail";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    private static final String CREDENTIALS_FILE_PATH = "client_secret.json";
    private static Gmail authorize () throws  NullPointerException, IOException, GeneralSecurityException {
        // Load client secrets.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
            new InputStreamReader(Objects.requireNonNull(ClassGmail.class.getClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH))));
            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

            // Build a new authorized API client service with credentials loaded up
            return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                    new AuthorizationCodeInstalledApp(flow, receiver).authorize("user"))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
    }
    public List<Message> getMessages () throws IOException, GeneralSecurityException {
        // Print the labels in the user's account.
        List<Message> tempMessages = new ArrayList<>();
        List<String> LabelsID = new ArrayList<>();
        LabelsID.add("SPAM");
        List<Message> messagesResponse = (List<Message>) Objects.requireNonNull(authorize()).users().messages().list(USER_TEST).setLabelIds(LabelsID).setMaxResults(8L).execute().getMessages();
        for(Message mail : messagesResponse){
            Message fullMail  = Objects.requireNonNull(authorize()).users().messages().get(USER_TEST,mail.getId()).execute();
            if (fullMail != null) {
                tempMessages.add(fullMail);
            }
        }
        return tempMessages;
    }
    public void setMessage(String id) throws IOException, GeneralSecurityException {
        Message message = Objects.requireNonNull(authorize()).users().messages().get(USER_TEST,id).setFormat("raw").execute();
        this.currentFullMain = new String(message.decodeRaw(), StandardCharsets.UTF_8);
    }
    public String getFullMessage(){
        return currentFullMain;
    }
    public void deleteMail(String id) throws GeneralSecurityException, IOException {
            authorize().users().messages().delete(USER_TEST, id).execute();
    }
    public Message sendEmail(ArrayList<String> listOfRecipients, String subject, String bodyText) throws MessagingException, IOException {
           // Create an email
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(USER_TEST));
            for (String address : listOfRecipients){
                email.addRecipient(javax.mail.Message.RecipientType.TO,new InternetAddress(address));
            }
            email.setSubject(subject);
            email.setText(bodyText);

           // Create a message for an email
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
            Message message = new Message();
            message.setRaw(encodedEmail);

        try {
            // Create send message
            message = authorize().users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
            return message;
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
    public static int sendEmail( String style,String from, ArrayList<String> listAddress,String subject,MimeMultipart mimeMultipart) throws MessagingException, IOException {
        int secretCode = 0;
        // Create an email
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);

        if (style.equals("check")){
            subject =  "Code verification de votre nouveau compte  sur l'application Association Basket";
            secretCode = ThreadLocalRandom.current().nextInt(100000, 999999 + 1);
            email.setFrom(new InternetAddress(USER_TEST));
            email.setSubject(subject);
            email.setContent(Template.confirmEmail(secretCode));
        }else{
            email.setFrom(new InternetAddress(from));
            email.setSubject(subject);
            email.setContent(mimeMultipart);
        }
            for (String address: listAddress){
                email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(address));
            }
        // Create a message for an email
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        try {
            // Create send message
            authorize().users().messages().send("me", message).execute();
            if (style.equals("check")) return secretCode;
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return secretCode;
    }
}