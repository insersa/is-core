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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.bo.IBusinessObject;

/**
 * @author INSER SA
 * @version 1.0
 * @created 09-juillet-2009 16:30:00
 */
public interface IMaiInfoBusinessObject extends IBusinessObject {

    /**
     * Get IValueObject object with all relevant information about a mail
     * 
     * @param businessId
     *            Unique business Id
     * @param locale
     *            Locale, which contains language information about user
     * @param con
     *            connection
     * @param user
     *            logged user
     * @return IValueObject object with all relevant information about a mail.
     * 
     *         Remark: The method getProperty of the IValueObject allows to access the following properties: <br>
     *         MailDescription.description: description of email <br>
     *         MailSubject.subjectContent: subject of email <br>
     *         MailText.lobTextContent: body text of email <br>
     *         MailText.mimeType: mime type of body text <br>
     *         MailAttachement.title: List of titles of attached files <br>
     *         MailAttachement.fileName: List of file names of attached files <br>
     *         MailAttachement.dateUpload: List of upload dates of attached files <br>
     *         MailAttachement.lobDoc: List of attached files <br>
     *         MailFrom.mailAddress: Map with Email address and personal name for sender of email <br>
     *         MailCC.mailAddress: Map with Email address and personal name for CC recipient of email <br>
     *         MailBCC.mailAddress: Map with Email address and personal name for CC recipient of email <br>
     *         MailReplyTo.mailAddress: Map with Email address and personal name for replyTo recipient of email <br>
     *         MailTo.mailAddress: Map with Email address and personal name for To recipient of email <br>
     * 
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    IValueObject getMailInfo(String businessId, Locale locale, Connection con, ILoggedUser user) throws SQLException;

    /**
     * Get IValueObject object with all relevant information about a mail
     * 
     * @param businessId
     *            Unique business Id
     * @param subjectArguments
     *            arguments used for parameterized subject
     * @param textArguments
     *            arguments used for parameterized body text
     * @param locale
     *            Locale, which contains language information about user
     * @param con
     *            connection
     * @param user
     *            logged user
     * @return IValueObject object with all relevant information about a mail.
     * 
     *         Remark: The method getProperty of the IValueObject allows to access the following properties: <br>
     *         MailDescription.description: description of email <br>
     *         MailSubject.subjectContent: subject of email <br>
     *         MailText.lobTextContent: body text of email <br>
     *         MailText.mimeType: mime type of body text <br>
     *         MailAttachement.title: List of titles of attached files <br>
     *         MailAttachement.fileName: List of file names of attached files <br>
     *         MailAttachement.dateUpload: List of upload dates of attached files <br>
     *         MailAttachement.lobDoc: List of attached files <br>
     *         MailFrom.mailAddress: Map with Email address and personal name for sender of email <br>
     *         MailCC.mailAddress: Map with Email address and personal name for CC recipient of email <br>
     *         MailBCC.mailAddress: Map with Email address and personal name for CC recipient of email <br>
     *         MailReplyTo.mailAddress: Map with Email address and personal name for replyTo recipient of email <br>
     *         MailTo.mailAddress: Map with Email address and personal name for To recipient of email <br>
     * 
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    IValueObject getMailInfo(String businessId, Object[] subjectArguments, Object[] textArguments, Locale locale, Connection con,
            ILoggedUser user) throws SQLException;
}
