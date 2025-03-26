/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package ch.inser.dynaplus.mail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.IValueObject;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-juillet 2009 16:30:00
 */
public class AbstractMail implements IMail {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(AbstractMail.class);

    /** Le context manager */
    private static IContextManager cContextManager;
    /** Map avec configuration des comptes mails */
    private Map<String, MailParams> iMapMailAccount;

    /**
     * Constructor
     */
    public AbstractMail() {
        // Création de la map
        iMapMailAccount = new HashMap<>();

        List<String> lstAddress = new ArrayList<>();

        // Création des différents comptes
        if (cContextManager.getProperty("mailAddress") != null) {
            // Découpage des adresses mails lorsque l'adresse et le login n'est
            // pas identique
            String[] address_arr = cContextManager.getProperty("mailAddress").split(",");
            // Insérer les adresses
            for (String address : address_arr) {
                iMapMailAccount.put(address, new MailParams());
                lstAddress.add(address);
            }
            if (cContextManager.getProperty("mailUsername") != null) {
                // insérer le username
                String[] usernames = cContextManager.getProperty("mailUsername").split(",");
                for (int i = 0; i < usernames.length; i++) {
                    MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                    mailParams.setUsername(usernames[i]);
                }
            } else {
                logger.error("Parameter mailUsername is empty");
            }
        } else {
            if (cContextManager.getProperty("mailUsername") != null) {
                // Découpage des adresses mails lorsque l'adresse mail et le
                // login du compte est identique
                String[] address_arr = cContextManager.getProperty("mailUsername").split(",");
                // Insérer les adresses
                for (String address : address_arr) {
                    MailParams mailParams = new MailParams();
                    mailParams.setUsername(address);
                    iMapMailAccount.put(address, mailParams);
                    lstAddress.add(address);
                }
            } else {
                logger.error("Parameter mailAddress or mailUsername are empty ...");
            }

        }

        // Découpage des propriétés pour les comptes mails
        // mailHost*****
        if (cContextManager.getProperty("mailHost") != null) {
            String[] hosts = cContextManager.getProperty("mailHost").split(",");
            for (int i = 0; i < hosts.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setHost(hosts[i]);
            }
        }
        // mailPortNumber*****
        if (cContextManager.getProperty("mailPortNumber") != null) {
            String[] portNumbers = cContextManager.getProperty("mailPortNumber").split(",");
            for (int i = 0; i < portNumbers.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setPortNumber(portNumbers[i]);
            }
        }
        // mailTransportProtocol*****
        if (cContextManager.getProperty("mailTransportProtocol") != null) {
            String[] transportProtocols = cContextManager.getProperty("mailTransportProtocol").split(",");
            for (int i = 0; i < transportProtocols.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setTransportProtocol(transportProtocols[i]);
            }
        }
        // mailPassword*****
        if (cContextManager.getProperty("mailPassword") != null) {
            String[] passwords = cContextManager.getProperty("mailPassword").split(",");
            for (int i = 0; i < passwords.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setPassword(passwords[i]);
            }
        }
        // mailAuth*****
        if (cContextManager.getProperty("mailAuth") != null) {
            String[] auths = cContextManager.getProperty("mailAuth").split(",");
            for (int i = 0; i < auths.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setAuth(auths[i]);
            }
        }
        // mailStarttls*****
        if (cContextManager.getProperty("mailStarttls") != null) {
            String[] starttls = cContextManager.getProperty("mailStarttls").split(",");
            for (int i = 0; i < starttls.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setStarttls(starttls[i]);
            }
        }
        // mailSslProtocols*****
        if (cContextManager.getProperty("mailSslProtocols") != null) {
            String[] sslProtocols = cContextManager.getProperty("mailSslProtocols").split(",");
            for (int i = 0; i < sslProtocols.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setTransportProtocol(sslProtocols[i]);
            }
        }
        // Si a true, on peut voir sur les traces les dialogues smtp avec le
        // serveur
        // mailDebug*****
        if (cContextManager.getProperty("mailDebug") != null) {
            String[] debugs = cContextManager.getProperty("mailDebug").split(",");
            for (int i = 0; i < debugs.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setDebug(debugs[i]);
            }
        }
        // Insère un TEST comme préfixe du sujet pour applications de test
        // mailTest*****
        if (cContextManager.getProperty("mailSubjectTest") != null) {
            String[] subjectTests = cContextManager.getProperty("mailSubjectTest").split(",");
            for (int i = 0; i < subjectTests.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setSubjectTest(subjectTests[i]);
            }
        }
        // Activation des envois d'e-mail
        // mailDeactivate*****
        if (cContextManager.getProperty("mailDeactivate") != null) {
            String[] deactivates = cContextManager.getProperty("mailDeactivate").split(",");
            for (int i = 0; i < deactivates.length; i++) {
                MailParams mailParams = iMapMailAccount.get(lstAddress.get(i));
                mailParams.setDeactivate(deactivates[i]);
            }
        }

        // Construction des propriétés de chaque compte mail
        for (String address : lstAddress) {
            logger.debug("Create account MAIL : " + address);
            iMapMailAccount.get(address).buildProperties();
        }

    }

    /**
     * Va rechercher l'adresse mail utilisé dans le FROM, pour informer le mail à utiliser
     *
     * @param aFromAddressInfo
     *            map of senders account->name
     * @return le compte à utiliser
     */
    private MailParams searchMailAccount(Map<String, String> aFromAddressInfo) {
        for (String key : aFromAddressInfo.keySet()) {
            if (iMapMailAccount.get(key) != null) {
                logger.debug("The mail will be send with the account : " + key);
                return iMapMailAccount.get(key);
            }
            logger.error("Account mail doesn't exist : " + key);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynaplus.util.IMail#sendMail(java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map,
     * java.lang.String, java.lang.String, java.lang.String, java.util.Collection)
     */
    @Override
    public void sendMail(Map<String, String> fromAddressInfo, Map<String, String> toAddressInfo, Map<String, String> ccAddressInfo,
            Map<String, String> bccAddressInfo, Map<String, String> replyToAddressInfo, String subjet, String messageBody,
            String mimeTypeMessageBody, Collection<String> filenames, Collection<Blob> attachements) {

        sendMail(fromAddressInfo, toAddressInfo, ccAddressInfo, bccAddressInfo, replyToAddressInfo, subjet, messageBody,
                mimeTypeMessageBody, filenames, attachements, null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DAOResult sendMail(IValueObject aVoMail) {

        MailParams mailAccount = searchMailAccount((Map<String, String>) aVoMail.getProperty("MailFrom.mailAddress"));

        // uniquement si l'envoi d'email est desactivé
        if (mailAccount == null || mailAccount.isDeactivate()) {
            return new DAOResult(1);
        }

        // Authentification
        Authenticator authenticator = new SMTPAuthenticator(mailAccount.getUsername(), mailAccount.getPassword());

        // create session object to model mail session
        Session session = Session.getInstance(mailAccount.getProperties(), authenticator);

        // Visualiser les transactions avec le serveur
        session.setDebug(mailAccount.isDebug());

        // Valeur de retour
        int ret = 0;

        try {

            Transport tr = session.getTransport(mailAccount.getTransportProtocol());
            // Ouverture de la connexion
            tr.connect();

            // Passage des propriétés de l'email
            Message message = new MimeMessage(session);
            // create email addresses
            Address[] fromEmailAddresses = getEmailAddresses((Map<String, String>) aVoMail.getProperty("MailFrom.mailAddress"));
            Address[] toEmailAddresses = getEmailAddresses((Map<String, String>) aVoMail.getProperty("MailTo.mailAddress"));
            Address[] ccEmailAddresses = getEmailAddresses((Map<String, String>) aVoMail.getProperty("MailCC.mailAddress"));
            Address[] bccEmailAddresses = getEmailAddresses((Map<String, String>) aVoMail.getProperty("MailBCC.mailAddress"));
            Address[] replyToEmailAddresses = getEmailAddresses((Map<String, String>) aVoMail.getProperty("MailReplyTo.mailAddress"));

            // create the text body part (message body and attachements)
            Multipart multipart = new MimeMultipart();
            addMessageBody(multipart, (String) aVoMail.getProperty("MailText.lobTextContent"),
                    (String) aVoMail.getProperty("MailText.mimeType"));

            addAttachements(multipart, (Collection<String>) aVoMail.getProperty("MailAttachement.fileName"),
                    (Collection<Blob>) aVoMail.getProperty("MailAttachement.lobDoc"));

            // attachement format inputstream
            addAttachementsInputStream(multipart, (Map<String, ByteArrayInputStream>) aVoMail.getProperty("MailAttachement.inputStream"));

            // add email addresses to e-mail headers
            addEmailAddresses(message, fromEmailAddresses, toEmailAddresses, ccEmailAddresses, bccEmailAddresses, replyToEmailAddresses);

            // set subject
            if (mailAccount.isSubjectTest()) {
                message.setSubject("TEST :" + (String) aVoMail.getProperty("MailSubject.subjectContent"));
            } else {
                message.setSubject((String) aVoMail.getProperty("MailSubject.subjectContent"));
            }

            // set content (message body and attachements)
            message.setContent(multipart);

            // Get recipients for server (RCTP commands)
            Address[] recipients = getAllEmailAddresses(toEmailAddresses, ccEmailAddresses, bccEmailAddresses);

            tr.sendMessage(message, recipients);

            // Fermeture de la connexion
            tr.close();
            ret = 1;
            logger.debug("Emails have been send nb :" + ret);
        } catch (UnsupportedEncodingException e) {
            logger.error("Invalid Internet address: The email could not be sent", e);
            ret = -1;
        } catch (AddressException e) {
            logger.error("Invalid Internet address: The email could not be sent", e);
            ret = -2;
        } catch (MessagingException e) {
            logger.error("Invalid message: The email could not be sent", e);
            ret = -3;
        } catch (IOException | SQLException e) {
            logger.error("Error occured: The email could not be sent", e);
            ret = -4;
        }

        return new DAOResult(ret);
    }

    /**
     * Merges the three arrays mailTo, mailCC and mailBBC to one array Recipients
     *
     * @param aToEmailAddresses
     *            array addresses mailTo
     * @param aCcEmailAddresses
     *            array addresses mailCC
     * @param aBccEmailAddresses
     *            array addresses mailBBC
     * @return array with all recipients
     */
    private Address[] getAllEmailAddresses(Address[] aToEmailAddresses, Address[] aCcEmailAddresses, Address[] aBccEmailAddresses) {

        List<Address> recipientList = new ArrayList<>();
        if (aToEmailAddresses != null) {
            recipientList.addAll(Arrays.asList(aToEmailAddresses));
        }
        if (aCcEmailAddresses != null) {
            recipientList.addAll(Arrays.asList(aCcEmailAddresses));
        }
        if (aBccEmailAddresses != null) {
            recipientList.addAll(Arrays.asList(aBccEmailAddresses));
        }
        return recipientList.toArray(new Address[0]);

    }

    /**
     * Envoi de plusieurs mails avec la même adresse mail
     */
    @SuppressWarnings("unchecked")
    @Override
    public DAOResult sendMails(Collection<IValueObject> aMails) {

        MailParams mailAccount = searchMailAccount((Map<String, String>) aMails.iterator().next().getProperty("MailFrom.mailAddress"));

        // uniquement si l'envoi d'email est activé
        if (mailAccount == null || mailAccount.isDeactivate()) {
            return new DAOResult(Status.NOTHING_TODO);
        }

        // Authentification
        Authenticator authenticator = new SMTPAuthenticator(mailAccount.getUsername(), mailAccount.getPassword());

        // create session object to model mail session
        Session session = Session.getInstance(mailAccount.getProperties(), authenticator);

        // Visualiser les transactions avec le serveur
        session.setDebug(mailAccount.isDebug());

        // Valeur de retour
        int ret = 0;

        try {

            Transport tr = session.getTransport(mailAccount.getTransportProtocol());
            // Ouverture de la connexion
            tr.connect();

            // Envoi des emails
            for (IValueObject voMail : aMails) {

                // Passage des propriétés de l'email
                Message message = new MimeMessage(session);
                // create email addresses
                Address[] fromEmailAddresses = getEmailAddresses((Map<String, String>) voMail.getProperty("MailFrom.mailAddress"));
                Address[] toEmailAddresses = getEmailAddresses((Map<String, String>) voMail.getProperty("MailTo.mailAddress"));
                Address[] ccEmailAddresses = getEmailAddresses((Map<String, String>) voMail.getProperty("MailCC.mailAddress"));
                Address[] bccEmailAddresses = getEmailAddresses((Map<String, String>) voMail.getProperty("MailBCC.mailAddress"));
                Address[] replyToEmailAddresses = getEmailAddresses((Map<String, String>) voMail.getProperty("MailReplyTo.mailAddress"));

                // create the text body part (message body and attachements)
                Multipart multipart = new MimeMultipart();
                addMessageBody(multipart, (String) voMail.getProperty("MailText.lobTextContent"),
                        (String) voMail.getProperty("MailText.mimeType"));
                addAttachements(multipart, (Collection<String>) voMail.getProperty("MailAttachement.fileName"),
                        (Collection<Blob>) voMail.getProperty("MailAttachement.lobDoc"));

                // add email addresses
                addEmailAddresses(message, fromEmailAddresses, toEmailAddresses, ccEmailAddresses, bccEmailAddresses,
                        replyToEmailAddresses);

                // set subject
                if (mailAccount.isSubjectTest()) {
                    message.setSubject("TEST : " + (String) voMail.getProperty("MailSubject.subjectContent"));
                } else {
                    message.setSubject((String) voMail.getProperty("MailSubject.subjectContent"));
                }

                // set content (message body and attachements)
                message.setContent(multipart);

                // Get recipients for server (RCTP commands)
                Address[] recipients = getAllEmailAddresses(toEmailAddresses, ccEmailAddresses, bccEmailAddresses);

                tr.sendMessage(message, recipients);
                ret++;
            }
            // Fermeture de la connexion
            tr.close();

            logger.debug("Emails have been send nb :" + ret);
        } catch (UnsupportedEncodingException e) {
            logger.error("Invalid Internet address: The email could not be sent", e);
            ret = -1;
        } catch (AddressException e) {
            logger.error("Invalid Internet address: The email could not be sent", e);
            ret = -2;
        } catch (MessagingException e) {
            logger.error("Invalid message: The email could not be sent", e);
            ret = -3;
        } catch (IOException | SQLException e) {
            logger.error("Error attaching documents", e);
            ret = -4;
        }

        return new DAOResult(ret);
    }

    /**
     * Get array of Address (email adresses)
     *
     * @param addressInfo
     *            Map with email address and corresponding personal name
     * @return array of Address (email adresses)
     * @throws AddressException
     *             invalid Internet address
     * @throws UnsupportedEncodingException
     *             bad encoding in internet address
     */
    private Address[] getEmailAddresses(Map<String, String> addressInfo) throws AddressException, UnsupportedEncodingException {
        Address[] emailAddresses = null;

        if (addressInfo != null && !addressInfo.isEmpty()) {
            List<Address> emailAddressesList = new ArrayList<>();

            for (Entry<String, String> addressInfoEntry : addressInfo.entrySet()) {
                String emailAddress = addressInfoEntry.getKey();
                String personalName = addressInfoEntry.getValue();

                if (personalName == null || "".equals(personalName)) {
                    emailAddressesList.add(new InternetAddress(emailAddress));
                } else {
                    emailAddressesList.add(new InternetAddress(emailAddress, personalName));
                }
            }

            emailAddresses = emailAddressesList.toArray(new Address[0]);
            return emailAddresses;
        }

        return emailAddresses;
    }

    /**
     * Add message body
     *
     * @param multipart
     *            Multipart object with elements of the mail content
     * @param messageBody
     *            message body text
     * @param mimeTypeMessageBody
     *            mime type of message body text
     * @throws MessagingException
     *             error adding text body part
     */
    private void addMessageBody(Multipart multipart, String messageBody, String mimeTypeMessageBody) throws MessagingException {
        BodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setContent(messageBody, mimeTypeMessageBody + "; charset=iso-8859-1");
        textBodyPart.setDisposition(Part.INLINE);
        multipart.addBodyPart(textBodyPart);
    }

    /**
     * Add attachements
     *
     * @param aMultipart
     *            Multipart object pour les éléments du mail
     * @param aFileNames
     *            noms des fichiers attachés
     * @param aAttachements
     *            Attachements (i.e. files to attach)
     * @throws MessagingException
     *             erreur d'insertion du attachement dans le multipart
     * @throws SQLException
     *             erreur de récuperation de binarystream du blob
     * @throws IOException
     *             erreur de lecture de du stream
     */
    private void addAttachements(Multipart aMultipart, Collection<String> aFileNames, Collection<Blob> aAttachements)
            throws MessagingException, IOException, SQLException {
        if (aAttachements == null) {
            return;
        }
        String[] filenames = aFileNames.toArray(new String[aFileNames.size()]);
        int index = 0;
        for (Blob blob : aAttachements) {

            if (blob != null) {
                try (InputStream blobStream = blob.getBinaryStream()) {
                    addFileAttachmentFromStream(aMultipart, filenames[index], blobStream, (int) blob.length());
                }
            }
            index++;
        }
    }

    /**
     * Attache tout la liste de fichier au mail
     *
     * @param aMultipart
     *            multipart object with different elements of the mail
     * @param aLstFile
     *            list of files to attach
     * @throws MessagingException
     *             error adding files to the multipart object
     * @throws IOException
     *             error reading file stream
     */
    private void addAttachementsInputStream(Multipart aMultipart, Map<String, ByteArrayInputStream> aLstFile)
            throws MessagingException, IOException {

        if (aLstFile != null) {
            for (Map.Entry<String, ByteArrayInputStream> file : aLstFile.entrySet()) {
                addFileAttachmentFromStream(aMultipart, file.getKey(), file.getValue(), file.getValue().available());
            }
            logger.debug("add e-mail attachement inputStream");
        }
    }

    /**
     * Add a file attachment to the email message from an input stream.
     *
     * @param aMultipart
     *            multipart object with elements of the mail
     * @param aFileName
     *            filename of the file to be attached
     * @param aFile
     *            The input stream containing the file attachment to add to the file
     * @param aStreamLen
     *            The length of the data in the input stream in bytes
     *
     * @throws MessagingException
     *             If an error occurs.
     * @throws IOException
     *             If an error occurs.
     */
    public void addFileAttachmentFromStream(Multipart aMultipart, String aFileName, InputStream aFile, int aStreamLen)
            throws MessagingException, IOException {

        byte[] b = new byte[aStreamLen];
        byte[] enc_b = new byte[aStreamLen * 3];

        BufferedInputStream bistrm = new BufferedInputStream(aFile);
        int bytes_read = 0;
        bytes_read = bistrm.read(b, 0, aStreamLen);

        logger.debug("Bytes Read From Stream: " + Integer.toString(bytes_read));

        enc_b = Base64.encodeBase64(b);

        logger.debug("Encoded Byte Count: " + Integer.toString(enc_b.length));

        InternetHeaders hdr = new InternetHeaders();
        hdr.addHeader("Content-Type", "application/octet-stream; name=\"" + aFileName + "\"");
        hdr.addHeader("Content-Transfer-Encoding", "base64");
        hdr.addHeader("Content-Disposition", "inline; filename=\"" + aFileName + "\"");

        MimeBodyPart mbp_file = new MimeBodyPart(hdr, enc_b);
        aMultipart.addBodyPart(mbp_file);

    }

    /**
     * Add email addresses to email message
     *
     * @param message
     *            Email message
     * @param fromEmailAddresses
     *            Map with email address and personal name for senders
     * @param toEmailAddresses
     *            Map with email address and personal name for recipients
     * @param ccEmailAddresses
     *            Map with email address and personal name for CC recipients
     * @param bccEmailAddresses
     *            Map with email address and personal name for BCC recipients
     * @param replyToEmailAddresses
     *            Map with email address and personal name for replyTo recipients
     * @throws MessagingException
     *             error adding address elements to the mail message
     */
    private void addEmailAddresses(Message message, Address[] fromEmailAddresses, Address[] toEmailAddresses, Address[] ccEmailAddresses,
            Address[] bccEmailAddresses, Address[] replyToEmailAddresses) throws MessagingException {
        message.addFrom(fromEmailAddresses);
        message.addRecipients(RecipientType.TO, toEmailAddresses);

        if (ccEmailAddresses != null) {
            message.addRecipients(RecipientType.CC, ccEmailAddresses);
        }

        if (bccEmailAddresses != null) {
            message.addRecipients(RecipientType.BCC, bccEmailAddresses);
        }

        if (replyToEmailAddresses != null) {
            message.setReplyTo(replyToEmailAddresses);
        }
    }

    /**
     * @param aContextManager
     *            the contextManager to set
     */
    public static void setContextManager(IContextManager aContextManager) {
        cContextManager = aContextManager;
    }

    /**
     * @author INSER SA
     * @version 1.0
     * @created 29-juillet 2009 16:30:00
     */
    class SMTPAuthenticator extends javax.mail.Authenticator {

        /**
         * Username of mail account
         */
        private String iUser;
        /**
         * Password of mail account
         */
        private String iPassword;

        /**
         * Constructor
         *
         * @param aUser
         *            Username of mail account
         * @param aPassword
         *            Password of mail account
         */
        public SMTPAuthenticator(String aUser, String aPassword) {
            iUser = aUser;
            iPassword = aPassword;
        }

        /**
         * Get password authentication
         *
         * @return password authentication
         */
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(iUser, iPassword);
        }
    }

    @Override
    public void sendMail(Map<String, String> aFromAddressInfo, Map<String, String> aToAddressInfo, Map<String, String> aCcAddressInfo,
            Map<String, String> aBccAddressInfo, Map<String, String> aReplyToAddressInfo, String aSubjet, String aMessage,
            String aMimeTypeMessage, Collection<String> aFileNames, Collection<Blob> aAttachements, Map<String, byte[]> aInlineImages,
            String aMimeTypeImage) {

        MailParams mailAccount = searchMailAccount(aFromAddressInfo);

        // uniquement si l'envoi d'email est activé
        if (mailAccount == null || mailAccount.isDeactivate()) {
            logger.error("mailAccount == null or isDeactivate == true");
            return;
        }

        Authenticator authenticator = new SMTPAuthenticator(mailAccount.getUsername(), mailAccount.getPassword());

        // create session object to model mail session
        Session session = Session.getInstance(mailAccount.getProperties(), authenticator);

        session.setDebug(mailAccount.isDebug());

        // create message object to model email message
        Message message = new MimeMessage(session);

        try {
            // create email addresses
            Address[] fromEmailAddresses = getEmailAddresses(aFromAddressInfo);
            Address[] toEmailAddresses = getEmailAddresses(aToAddressInfo);
            Address[] ccEmailAddresses = getEmailAddresses(aCcAddressInfo);
            Address[] bccEmailAddresses = getEmailAddresses(aBccAddressInfo);
            Address[] replyToEmailAddresses = getEmailAddresses(aReplyToAddressInfo);

            // create the text body part (message body and attachements)
            Multipart multipart = new MimeMultipart();
            addMessageBody(multipart, aMessage, aMimeTypeMessage);
            addInlineImages(multipart, aInlineImages, aMimeTypeImage);
            addAttachements(multipart, aFileNames, aAttachements);

            // add email addresses
            addEmailAddresses(message, fromEmailAddresses, toEmailAddresses, ccEmailAddresses, bccEmailAddresses, replyToEmailAddresses);

            // set subject
            if (mailAccount.isSubjectTest()) {
                message.setSubject("TEST :" + aSubjet);
            } else {
                message.setSubject(aSubjet);
            }

            // set content (message body and attachements)
            message.setContent(multipart);

            // transport (sending) of email
            Transport.send(message);

            logger.debug("Email was sent successfully");
        } catch (UnsupportedEncodingException e) {
            logger.error("Invalid Internet address: The email could not be sent", e);
        } catch (AddressException e) {
            logger.error("Invalid Internet address: The email could not be sent", e);
        } catch (MessagingException e) {
            logger.error("Invalid message: The email could not be sent", e);
        } catch (IOException | SQLException e) {
            logger.error("Erreur d'insertion de attachements", e);
        }

    }

    /**
     * Add images into the body part of the mail
     *
     * @param aMultipart
     *            multipart object with elements of the mail
     * @param aInlineImages
     *            the images
     * @param aMimeTypeImage
     *            mime type of the inline images
     * @throws MessagingException
     *             errors
     */
    private void addInlineImages(Multipart aMultipart, Map<String, byte[]> aInlineImages, String aMimeTypeImage) throws MessagingException {
        // adds inline image attachments
        if (aInlineImages != null && aInlineImages.size() > 0 && aMimeTypeImage != null && !"".equals(aMimeTypeImage)) {
            Set<String> setImageID = aInlineImages.keySet();

            // https://stackoverflow.com/questions/36920697/i-want-to-send-an-emailjavamailwith-image-attachment
            for (String contentId : setImageID) {
                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(aInlineImages.get(contentId), aMimeTypeImage)));
                imagePart.setContentID("<" + contentId + ">");
                aMultipart.addBodyPart(imagePart);
            }
        }
    }
}