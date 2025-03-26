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

package ch.inser.jsl.tools;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Classe pour la manipulation de mails.
 *
 * @author INSER SA
 */
public class MailTools {

    /**
     * Envoie un mail.
     *
     * @param aTo
     *            Adresse du destinataire
     * @param aFrom
     *            Adresse de l'envoyeur
     * @param aSubject
     *            Sujet du mail
     * @param aText
     *            Text du mail
     * @param aHost
     *            Serveur mail
     *
     * @throws MessagingException
     */
    public static void sendEmail(String aTo, String aFrom, String aSubject, String aText, String aHost) throws MessagingException {

        // create a message
        Properties props = new Properties();
        props.put("mail.smtp.host", aHost);
        MimeMessage msg = new MimeMessage(Session.getDefaultInstance(props, null));
        msg.setFrom(new InternetAddress(aFrom));
        InternetAddress[] address = { new InternetAddress(aTo) };
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSentDate(new java.util.Date());
        msg.setSubject(aSubject, "ISO-8859-1");
        msg.setText(aText, "ISO-8859-1");

        // send message
        Transport.send(msg);
    }
}