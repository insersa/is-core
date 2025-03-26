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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynamic.util.VOInfo;
import ch.inser.dynaplus.bo.AbstractBusinessObject;
import ch.inser.dynaplus.bo.IBusinessObject;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.tools.StringTools;

/**
 * @author INSER SA
 * @version 1.0
 * @created 09-juillet-2009 16:30:00
 */
public class AbstractMailInfoBusinessObject extends AbstractBusinessObject implements IMaiInfoBusinessObject {

    /**
     * UID
     */
    private static final long serialVersionUID = 5402532197087205609L;

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(AbstractMailInfoBusinessObject.class);

    /**
     * Context manager
     */
    private IContextManager iContextManager;

    /**
     * Constructor
     *
     * @param name
     *            business object name
     * @param info
     *            value object information
     */
    public AbstractMailInfoBusinessObject(String name, VOInfo info) {
        super(name, info);
    }

    @Override
    public Object executeMethode(String aNameMethode, Object anObject, ILoggedUser aUser, Connection aConnection) throws ISException {
        if ("getMailInfo".equals(aNameMethode)) {
            Object[] obj = (Object[]) anObject;
            try {
                if (obj != null) {
                    if (obj.length == 2) {
                        return getMailInfo((String) obj[0], (Locale) obj[1], aConnection, aUser);
                    }
                    if (obj.length == 4) {
                        return getMailInfo((String) obj[0], (Object[]) obj[1], (Object[]) obj[2], (Locale) obj[3], aConnection, aUser);
                    }

                }
            } catch (SQLException e) {
                logger.error("Error getting mailInfo", e);
                throw new ISException(e);
            }

        }

        return getDao().executeMethode(aNameMethode, anObject, aUser, aConnection);
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynaplus.bo.IMaiInfoBusinessObject#getMailInfo(java.lang.String, java.util.Locale, java.sql.Connection,
     * ch.inser.dynamic.common.AbstractLoggedUser)
     */
    @Override
    public IValueObject getMailInfo(String businessId, Locale locale, Connection con, ILoggedUser user) throws SQLException {
        try {
            return getMailInfoValue(businessId, null, null, locale, con, user);
        } catch (ISException e) {
            throw (SQLException) e.getCause();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.inser.dynaplus.bo.IMaiInfoBusinessObject#getMailInfo(java.lang.String, java.lang.Object[], java.lang.Object[],
     * java.util.Locale, java.sql.Connection, ch.inser.dynamic.common.AbstractLoggedUser)
     */
    @Override
    public IValueObject getMailInfo(String businessId, Object[] subjectArguments, Object[] textArguments, Locale locale, Connection con,
            ILoggedUser user) throws SQLException {
        try {
            return getMailInfoValue(businessId, subjectArguments, textArguments, locale, con, user);
        } catch (ISException e) {
            throw (SQLException) e.getCause();
        }
    }

    /**
     * Get Mail Info value object
     *
     * @param businessId
     *            business ID, identifying a mail
     * @param subjectArguments
     *            subject arguments
     * @param textArguments
     *            text body arguments
     * @param locale
     *            locale (language) for getting mail info
     * @param con
     *            connection
     * @param user
     *            logged user
     * @return Mail Info value object
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     * @throws ISException
     *             error retrieving mail info
     */
    private IValueObject getMailInfoValue(String businessId, Object[] subjectArguments, Object[] textArguments, Locale locale,
            Connection con, ILoggedUser user) throws SQLException, ISException {
        IValueObject initVO = getInitVO(user, con, false);
        IValueObject voMailInfo = initVO;
        voMailInfo.setProperty("mai_business_id", businessId);
        Collection<?> mailInfoCollection = getList(voMailInfo, user, con).getListObject();

        if (!mailInfoCollection.isEmpty()) {
            IValueObject currentMailInfoValueObject = (IValueObject) mailInfoCollection.iterator().next();
            IValueObject mailInfoValueObject = getInitVO(user, con, false);
            mailInfoValueObject.setProperty("mai_id", currentMailInfoValueObject.getId());

            Locale mailInfoLocale = getMailInfoLocale(locale);

            return doWork(mailInfoValueObject, subjectArguments, textArguments, mailInfoLocale, con, user);
        }

        return null;
    }

    /**
     * Do the work to get the relevant information about an email (i.e. value object)
     *
     * @param mailInfoValueObject
     *            mail info value object
     * @param subjectArguments
     *            subject arguments
     * @param textArguments
     *            text body arguments
     * @param mailInfoLocale
     *            locale (language) for getting mail info
     * @param con
     *            connection
     * @param user
     *            logged user
     * @return MailInfo value object
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     * @throws ISException
     *             error retrieving mail info from the database
     */
    private IValueObject doWork(IValueObject mailInfoValueObject, Object[] subjectArguments, Object[] textArguments, Locale mailInfoLocale,
            Connection con, ILoggedUser user) throws SQLException, ISException {
        // Get information from MailDescription business object
        IValueObject voMailDescription = getValueObject("MailDescription", mailInfoValueObject.getId(), "mds_mai_id", "mds_lang_code",
                mailInfoLocale, con, user);
        if (voMailDescription != null) {
            mailInfoValueObject.setProperty("MailDescription.description", voMailDescription.getProperty("mds_description"));
        }

        // Get information from MailSubject business object
        IValueObject voMailSubject = getValueObject("MailSubject", mailInfoValueObject.getId(), "msu_mai_id", "msu_lang_code",
                mailInfoLocale, con, user);
        if (voMailSubject != null) {
            if (subjectArguments != null) {
                mailInfoValueObject.setProperty("MailSubject.subjectContent",
                        getFormattedStringValue(voMailSubject.getProperty("msu_subject_content"), subjectArguments));
            } else {
                mailInfoValueObject.setProperty("MailSubject.subjectContent", voMailSubject.getProperty("msu_subject_content"));
            }
        }

        // Get information from MailText business object
        IValueObject voMailText = getValueObject("MailText", mailInfoValueObject.getId(), "mte_mai_id", "mte_lang_code", mailInfoLocale,
                con, user);
        if (voMailText != null) {
            if (textArguments != null) {
                mailInfoValueObject.setProperty("MailText.lobTextContent",
                        getFormattedStringValue(voMailText.getProperty("mte_lob_text_content"), textArguments));
            } else {
                mailInfoValueObject.setProperty("MailText.lobTextContent", voMailText.getProperty("mte_lob_text_content"));
            }

            mailInfoValueObject.setProperty("MailText.mimeType", voMailText.getProperty("mte_mime_type"));
        }

        // Get information from MailAttachement business object
        Collection<IValueObject> voMailAttachementCollection = getValueObjects("MailAttachement", mailInfoValueObject.getId(), "mat_mai_id",
                "mat_lang_code", mailInfoLocale, con, user);
        if (!voMailAttachementCollection.isEmpty()) {
            for (IValueObject voMailAttachement : voMailAttachementCollection) {
                setPropertyValues(mailInfoValueObject, "MailAttachement", "title", "mat_title", voMailAttachement);
                setPropertyValues(mailInfoValueObject, "MailAttachement", "fileName", "mat_filename", voMailAttachement);
                setPropertyValues(mailInfoValueObject, "MailAttachement", "dateUpload", "mat_date_upload", voMailAttachement);
                setPropertyValues(mailInfoValueObject, "MailAttachement", "lobDoc", "mat_lob_doc", voMailAttachement);
            }
        }

        // Get information from MailFrom, MailAddress and MailAddressDescription
        // business object
        IValueObject voMailFrom = getValueObject("MailFrom", mailInfoValueObject.getId(), "mfr_mai_id", "mfr_lang_code", mailInfoLocale,
                con, user);
        if (voMailFrom != null) {
            IValueObject mailAddressValueObject = getMailAddress(voMailFrom.getProperty("mfr_mad_id"), con, user);
            IValueObject voMailAddressDescription = getValueObject("MailAddressDescription", mailAddressValueObject.getId(), "mde_mad_id",
                    "mde_lang_code", mailInfoLocale, con, user);
            setAddressValues(mailInfoValueObject, "MailFrom", "mailAddress", "mad_email_address", mailAddressValueObject,
                    "mde_address_description", voMailAddressDescription);
        }

        // Get information from MailCC, MailAddress and MailAddressDescription
        // business object
        Collection<IValueObject> voMailCCCollection = getValueObjects("MailCC", mailInfoValueObject.getId(), "mcc_mai_id", "mcc_lang_code",
                mailInfoLocale, con, user);
        if (!voMailCCCollection.isEmpty()) {
            for (IValueObject voMailCC : voMailCCCollection) {
                IValueObject mailAddressValueObject = getMailAddress(voMailCC.getProperty("mcc_mad_id"), con, user);
                IValueObject voMailAddressDescription = getValueObject("MailAddressDescription", mailAddressValueObject.getId(),
                        "mde_mad_id", "mde_lang_code", mailInfoLocale, con, user);
                setAddressValues(mailInfoValueObject, "MailCC", "mailAddress", "mad_email_address", mailAddressValueObject,
                        "mde_address_description", voMailAddressDescription);
            }
        }

        // Get information from MailBCC, MailAddress and MailAddressDescription
        // business object
        Collection<IValueObject> voMailBCCCollection = getValueObjects("MailBCC", mailInfoValueObject.getId(), "mbc_mai_id",
                "mbc_lang_code", mailInfoLocale, con, user);
        if (!voMailBCCCollection.isEmpty()) {
            for (IValueObject voMailBCC : voMailBCCCollection) {
                IValueObject mailAddressValueObject = getMailAddress(voMailBCC.getProperty("mbc_mad_id"), con, user);
                IValueObject voMailAddressDescription = getValueObject("MailAddressDescription", mailAddressValueObject.getId(),
                        "mde_mad_id", "mde_lang_code", mailInfoLocale, con, user);
                setAddressValues(mailInfoValueObject, "MailBCC", "mailAddress", "mad_email_address", mailAddressValueObject,
                        "mde_address_description", voMailAddressDescription);
            }
        }

        // Get information from MailReplyTo, MailAddress and
        // MailAddressDescription business object
        Collection<IValueObject> voMailReplyToCollection = getValueObjects("MailReplyTo", mailInfoValueObject.getId(), "mre_mai_id",
                "mre_lang_code", mailInfoLocale, con, user);
        if (!voMailReplyToCollection.isEmpty()) {
            for (IValueObject voMailReplyTo : voMailReplyToCollection) {
                IValueObject mailAddressValueObject = getMailAddress(voMailReplyTo.getProperty("mre_mad_id"), con, user);
                IValueObject voMailAddressDescription = getValueObject("MailAddressDescription", mailAddressValueObject.getId(),
                        "mde_mad_id", "mde_lang_code", mailInfoLocale, con, user);
                setAddressValues(mailInfoValueObject, "MailReplyTo", "mailAddress", "mad_email_address", mailAddressValueObject,
                        "mde_address_description", voMailAddressDescription);
            }
        }

        // Get information from MailTo, MailAddress and
        // MailAddressDescription business object
        Collection<IValueObject> voMailToCollection = getValueObjects("MailTo", mailInfoValueObject.getId(), "mto_mai_id", "mto_lang_code",
                mailInfoLocale, con, user);
        if (!voMailToCollection.isEmpty()) {
            for (IValueObject voMailTo : voMailToCollection) {
                IValueObject mailAddressValueObject = getMailAddress(voMailTo.getProperty("mto_mad_id"), con, user);
                IValueObject voMailAddressDescription = getValueObject("MailAddressDescription", mailAddressValueObject.getId(),
                        "mde_mad_id", "mde_lang_code", mailInfoLocale, con, user);
                setAddressValues(mailInfoValueObject, "MailTo", "mailAddress", "mad_email_address", mailAddressValueObject,
                        "mde_address_description", voMailAddressDescription);
            }
        }

        return mailInfoValueObject;
    }

    /**
     * Get a spécific mail info, ex. mail title
     *
     * @param boName
     *            business object name
     * @param fkId
     *            foreign key value
     * @param fkProperty
     *            mail name of foreign key referring to the template id
     * @param langCode
     *            language code property
     * @param mailInfoLocale
     *            locale (language) for getting mail info
     * @param con
     *            connection
     * @param user
     *            logged user
     * @return vo with mail info from a given table, ex. mail title
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     * @throws ISException
     *             error retrieving mail info
     */
    private IValueObject getValueObject(String boName, Object fkId, String fkProperty, String langCode, Locale mailInfoLocale,
            Connection con, ILoggedUser user) throws SQLException, ISException {

        IBusinessObject bo = getBOFactory().getBO(boName);

        IValueObject vo = bo.getInitVO(user, con, false);
        vo.setProperty(fkProperty, fkId);
        vo.setProperty(langCode, mailInfoLocale.getLanguage());

        Collection<?> voCollection = bo.getList(vo, user, con).getListObject();
        if (voCollection.isEmpty()) {
            return null;
        }
        return (IValueObject) voCollection.iterator().next();
    }

    /**
     * Get list of value objects
     *
     * @param boName
     *            business object name, ex. MailSubject, MailTo
     * @param fkId
     *            foreign key value
     * @param fkProperty
     *            property name that represents the foreign key
     * @param langCode
     *            language code property
     * @param mailInfoLocale
     *            Locale (language)
     * @param con
     *            connection
     * @param user
     *            logged user
     * @return list of vos of a given business type (MailTo, MailSubject) for a given mail template
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     * @throws ISException
     *             error retrieving mail info
     */
    private List<IValueObject> getValueObjects(String boName, Object fkId, String fkProperty, String langCode, Locale mailInfoLocale,
            Connection con, ILoggedUser user) throws SQLException, ISException {
        List<IValueObject> voList = new ArrayList<>();
        IBusinessObject boMailDescription = getBOFactory().getBO(boName);

        IValueObject vo = boMailDescription.getInitVO(user, con, false);
        vo.setProperty(fkProperty, fkId);
        vo.setProperty(langCode, mailInfoLocale.getLanguage());

        Collection<?> voCollection = boMailDescription.getList(vo, user, con).getListObject();
        for (Object object : voCollection) {
            IValueObject currentVo = (IValueObject) object;
            voList.add(currentVo);
        }

        return voList;
    }

    /**
     * Set property values in Mail value object
     *
     * @param mailInfoValueObject
     *            vo with mail info
     *
     * @param boName
     *            business object name of mail info, ex MailTo, MailSubject, MailText, MailAttachement
     * @param mailInfoPropertyName
     *            property name of mail info, ex. mailAddress, lobText, lobDoc
     * @param propertyName
     *            property name of property to be retrieved
     * @param valueObject
     *            vo with property to be retrieved (ex. t_mail_attachement)
     */
    @SuppressWarnings("unchecked")
    private void setPropertyValues(IValueObject mailInfoValueObject, String boName, String mailInfoPropertyName, String propertyName,
            IValueObject valueObject) {
        if (mailInfoValueObject.getProperty(boName + "." + mailInfoPropertyName) == null) {
            mailInfoValueObject.setProperty(boName + "." + mailInfoPropertyName, new ArrayList<>());
        }
        List<Object> propertyValues = (List<Object>) mailInfoValueObject.getProperty(boName + "." + mailInfoPropertyName);
        Object propertyValue = valueObject.getProperty(propertyName);
        propertyValues.add(propertyValue);
    }

    /**
     * Set property values for email address and description in Mail value object
     *
     * @param mailInfoValueObject
     *            mail info value object
     * @param boName
     *            business object name, that client of Mail value object uses as a part of the property name, to retrieve information about
     *            an email
     * @param mailInfoEmailPropertyName
     *            property name, that client of Mail value object uses as a part of the property name, to retrieve information about an
     *            email
     * @param emailAddressPropertyName
     *            email address property name
     * @param emailAddressValueObject
     *            email address value object
     * @param emailDescriptionPropertyName
     *            email description property name
     * @param emailDescriptionValueObject
     *            email description value object
     */
    @SuppressWarnings("unchecked")
    private void setAddressValues(IValueObject mailInfoValueObject, String boName, String mailInfoEmailPropertyName,
            String emailAddressPropertyName, IValueObject emailAddressValueObject, String emailDescriptionPropertyName,
            IValueObject emailDescriptionValueObject) {
        Map<String, String> emailAddresses;

        Object emailAddressPropertyValue = emailAddressValueObject.getProperty(emailAddressPropertyName);
        if (emailAddressPropertyValue == null) {
            logger.warn("The email address is null");
            return;
        }

        Object emailDescriptionPropertyValue = null;
        if (emailDescriptionValueObject != null) {
            emailDescriptionPropertyValue = emailDescriptionValueObject.getProperty(emailDescriptionPropertyName);
        }

        if (mailInfoValueObject.getProperty(boName + "." + mailInfoEmailPropertyName) == null) {
            emailAddresses = new HashMap<>();

            if (emailDescriptionPropertyValue != null) {
                emailAddresses.put(emailAddressPropertyValue.toString(), emailDescriptionPropertyValue.toString());
            } else {
                emailAddresses.put(emailAddressPropertyValue.toString(), null);
            }

            mailInfoValueObject.setProperty(boName + "." + mailInfoEmailPropertyName, emailAddresses);
        } else {
            emailAddresses = (Map<String, String>) mailInfoValueObject.getProperty(boName + "." + mailInfoEmailPropertyName);

            if (emailDescriptionPropertyValue != null) {
                emailAddresses.put(emailAddressPropertyValue.toString(), emailDescriptionPropertyValue.toString());
            } else {
                emailAddresses.put(emailAddressPropertyValue.toString(), null);
            }
        }
    }

    /**
     * Get value object MailAddress (i.e. email address)
     *
     * @param id
     *            Id of value object MailAddress
     * @param con
     *            connection
     * @param user
     *            logged user
     * @return value object MailAddress (i.e. email address)
     * @throws SQLException
     *             en cas de problème au niveau de la requête à la base de données.
     */
    private IValueObject getMailAddress(Object id, Connection con, ILoggedUser user) throws SQLException {
        IBusinessObject boMailAddress = getBOFactory().getBO("MailAddress");

        IValueObject mailAddress = boMailAddress.getRecord(id, con, user, false).getValueObject();
        return mailAddress;
    }

    /**
     * Get locale for retrieving email info
     *
     * @param locale
     *            Locale
     * @return locale to use to retrieve email info
     */
    private Locale getMailInfoLocale(Locale locale) {
        try {
            String mailInfoDefaultLanguage = iContextManager.getProperty("mailInfoDefaultLanguage");
            String mailInfoSupportedLanguages = iContextManager.getProperty("mailInfoSupportedLanguages");
            Collection<String> supportedLanguages = Arrays.asList(StringTools.split(mailInfoSupportedLanguages, ", "));

            if (locale == null || !supportedLanguages.contains(locale.getLanguage())) {
                return new Locale(mailInfoDefaultLanguage);
            }

            return locale;
        } catch (@SuppressWarnings("unused") Exception e) {
            // There is an error, don't log it just give de default locale!
            return Locale.getDefault();
        }
    }

    /**
     * Get formatted, final string value
     *
     * @param parametrizedTextAsObject
     *            parametrized text to format as object
     * @param arguments
     *            arguments used for formatting
     * @return formatted, final string value
     */
    private String getFormattedStringValue(Object parametrizedTextAsObject, Object[] arguments) {
        String finalStringValue = null;
        String parametrizedText = null;

        try {
            parametrizedText = (String) parametrizedTextAsObject;
            parametrizedText = getParametrizedTextForMessageFormat(parametrizedText);
            finalStringValue = MessageFormat.format(parametrizedText, arguments);
        } catch (Exception e) {
            logger.error("An exception occured while formatting " + parametrizedTextAsObject, e);
        }

        return finalStringValue;
    }

    /**
     * Get parametrized text to pass for MessageFormat <br>
     * Remark: The apostrohphes must be doubled for use by MessageFormat
     *
     * @param parametrizedText
     *            parametrized text to format
     * @return parametrized text to pass for MessageFormat
     */
    private String getParametrizedTextForMessageFormat(String parametrizedText) {
        return parametrizedText.replace("'", "''");
    }

    /**
     * @return the contextManager
     */
    @Override
    public IContextManager getContextManager() {
        return iContextManager;
    }

    /**
     * @param aContextManager
     *            the contextManager to set
     */
    @Override
    public void setContextManager(IContextManager aContextManager) {
        iContextManager = aContextManager;
    }
}
