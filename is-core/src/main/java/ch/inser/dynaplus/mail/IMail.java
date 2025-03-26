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

import java.sql.Blob;
import java.util.Collection;
import java.util.Map;

import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.IValueObject;

/**
 * @author INSER SA
 * @version 1.0
 * @created 29-juillet 2009 16:30:00
 */
public interface IMail {

    /**
     * Send an email
     *
     * @param fromAddressInfo
     *            sender email address and related personal name
     * @param toAddressInfo
     *            recipient email addresses and related personal names
     * @param ccAddressInfo
     *            cc email addresses and related personal names
     * @param bccAddressInfo
     *            bcc email addresses and related personal names
     * @param replyToAddressInfo
     *            reply email addresses and related personal names
     * @param subjet
     *            subject of email
     * @param message
     *            mail content
     * @param mimeTypeMessage
     *            mime type of mail content
     * @param fileNames
     *            attached file names
     * @param attachements
     *            attached files
     */
    public void sendMail(Map<String, String> fromAddressInfo, Map<String, String> toAddressInfo, Map<String, String> ccAddressInfo,
            Map<String, String> bccAddressInfo, Map<String, String> replyToAddressInfo, String subjet, String message,
            String mimeTypeMessage, Collection<String> fileNames, Collection<Blob> attachements);

    /**
     * Send an email
     *
     * @param fromAddressInfo
     *            sender email address and related personal name
     * @param toAddressInfo
     *            recipient email addresses and related personal names
     * @param ccAddressInfo
     *            cc email addresses and related personal names
     * @param bccAddressInfo
     *            bcc email addresses and related personal names
     * @param replyToAddressInfo
     *            reply email addresses and related personal names
     * @param subjet
     *            subject of email
     * @param message
     *            mail content
     * @param mimeTypeMessage
     *            mime type of mail content
     * @param fileNames
     *            attached file names
     * @param attachements
     *            attached files
     * @param inlineImages
     *            images to add in the body part of mail
     * @param mimeTypeImage
     *            mime type of inline images
     */
    public void sendMail(Map<String, String> fromAddressInfo, Map<String, String> toAddressInfo, Map<String, String> ccAddressInfo,
            Map<String, String> bccAddressInfo, Map<String, String> replyToAddressInfo, String subjet, String message,
            String mimeTypeMessage, Collection<String> fileNames, Collection<Blob> attachements, Map<String, byte[]> inlineImages,
            String mimeTypeImage);

    /**
     * Envoi d'email au passant uniquement un vo, ce vo possède les renseignements pour l'envoi d'un mail
     *
     * @param aVoMail
     *            vo avec les informations du mail
     * @return DAOResult avec nbr d'eregistrements: 1 ok, -1 EncodingException -2 AddressException -3 MessagingException -4 Exception
     */
    public DAOResult sendMail(IValueObject aVoMail);

    /**
     * Envoi de plusieurs e-mails simultanément dans la même session telnet
     *
     * @param aMails
     *            liste de mail infos
     * @return nombre de mails -1 EncodingException -2 AddressException -3 MessagingException -4 Autres exceptions
     */
    public DAOResult sendMails(Collection<IValueObject> aMails);
}
