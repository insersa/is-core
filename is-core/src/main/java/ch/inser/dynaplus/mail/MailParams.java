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

/**
 * Renseignement de tous les paramètres du comptre mail
 */
package ch.inser.dynaplus.mail;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author INSER SA
 *
 */
public class MailParams {

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(MailParams.class);

    /** Host for sending email (SMTP-Server) */
    private String iHost;
    /** Port number for sending email (SMTP-Server) */
    private String iPortNumber;
    /** Protocol for transporting an email */
    private String iTransportProtocol;
    /** Login du compte, dès fois différent de l'email */
    private String iUsername;
    /** Password du compte mail */
    private String iPassword;
    /** Avec autorisation valeur true ou false */
    private String iAuth;
    /** Encryption tls */
    private String iStarttls;
    /** Protocol ssl supporté */
    private String iSslProtocols;
    /** Trace for session (true or false) */
    private String iDebug;
    /** Affiche TEST en préfix dans le sujet */
    private String iSubjectTest;
    /** Deactivate e-Mail send (default false) */
    private String iDeactivate;
    /** false string value */
    private static final String DEFAULT_FALSE = "false";

    /**
     * Après renseignement il faut assembler les paramètres dans un objet properties
     */
    private Properties iProperties;

    /**
     * Constructeur
     */
    public MailParams() {
        // Valeurs par défaut
        iPortNumber = "25";
        iTransportProtocol = "smtp";
        iSslProtocols = "TLSv1.2";
        iSubjectTest = DEFAULT_FALSE;
        iDebug = DEFAULT_FALSE;
        iDeactivate = DEFAULT_FALSE;
    }

    /**
     * Ajout des properties
     */
    public void buildProperties() {
        // Création de l'objet des proprietés du compte mail
        iProperties = new Properties();

        // Envoyer les informations du compte dans les logs
        logger.debug("Informations mail account");
        logger.debug("mailHost : " + iHost);
        logger.debug("mailPortNumber : " + iPortNumber);
        logger.debug("mailTransportProtocol : " + iTransportProtocol);
        logger.debug("mailUsername : " + iUsername);
        // logger.debug("mailPassword : " + iPassword);
        logger.debug("mailAuth : " + iAuth);
        logger.debug("mailStarttls : " + iStarttls);
        logger.debug("mailSslProtocols : " + iSslProtocols);
        logger.debug("mailDebug : " + iDebug);
        logger.debug("mailSubjectTest : " + iSubjectTest);
        logger.debug("mailDeactivate : " + iDeactivate);

        // Mise des paramètres dans les properties utilisés par l'objet Session
        // de javax.mail
        iProperties.put("mail.smtp.host", iHost);
        iProperties.put("mail.smtp.port", iPortNumber);
        iProperties.put("mail.transport.protocol", iTransportProtocol);
        iProperties.put("mail.user", iUsername);
        iProperties.put("mail.password", iPassword);
        iProperties.put("mail.smtp.auth", iAuth);
        iProperties.put("mail.smtp.starttls.enable", iStarttls);
        iProperties.put("mail.smtp.ssl.protocols", iSslProtocols);
        iProperties.put("mail.session.debug", iDebug);

    }

    /**
     * @return the hostName
     */
    public String getHost() {
        return iHost;
    }

    /**
     * @param aHost
     *            the hostName to set
     */
    public void setHost(String aHost) {
        iHost = aHost;
    }

    /**
     * @return the portNumber
     */
    public String getPortNumber() {
        return iPortNumber;
    }

    /**
     * @param aPortNumber
     *            the portNumber to set
     */
    public void setPortNumber(String aPortNumber) {
        iPortNumber = aPortNumber;
    }

    /**
     * @return the transportProtocol
     */
    public String getTransportProtocol() {
        return iTransportProtocol;
    }

    /**
     * @param aTransportProtocol
     *            the transportProtocol to set
     */
    public void setTransportProtocol(String aTransportProtocol) {
        iTransportProtocol = aTransportProtocol;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return iUsername;
    }

    /**
     * @param aUsername
     *            the username to set
     */
    public void setUsername(String aUsername) {
        iUsername = aUsername;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return iPassword;
    }

    /**
     * @param aPassword
     *            the password to set
     */
    public void setPassword(String aPassword) {
        iPassword = aPassword;
    }

    /**
     * @return the auth
     */
    public String getAuth() {
        return iAuth;
    }

    /**
     * @param aAuth
     *            the auth to set
     */
    public void setAuth(String aAuth) {
        iAuth = aAuth;
    }

    /**
     * @return the starttls
     */
    public String getStarttls() {
        return iStarttls;
    }

    /**
     * @param aStarttls
     *            the starttls to set
     */
    public void setStarttls(String aStarttls) {
        iStarttls = aStarttls;
    }

    /**
     * @return the debug
     */
    public String getDebug() {
        return iDebug;
    }

    /**
     * @param aDebug
     *            the debug to set
     */
    public void setDebug(String aDebug) {
        iDebug = aDebug;
    }

    public boolean isDebug() {
        if ("true".equalsIgnoreCase(iDebug)) {
            return true;
        }
        return false;
    }

    /**
     * @return the subjectTest
     */
    public String getSubjectTest() {
        return iSubjectTest;
    }

    /**
     * @param aSubjectTest
     *            the subjectTest to set
     */
    public void setSubjectTest(String aSubjectTest) {
        iSubjectTest = aSubjectTest;
    }

    public boolean isSubjectTest() {
        if ("true".equalsIgnoreCase(iSubjectTest)) {
            return true;
        }
        return false;
    }

    /**
     * @return the deactivate
     */
    public String getDeactivate() {
        return iDeactivate;
    }

    /**
     * @param aDeactivate
     *            the deactivate to set
     */
    public void setDeactivate(String aDeactivate) {
        iDeactivate = aDeactivate;
    }

    /**
     * Retourne en format boolean de l'information si le compte mail est désactivé
     *
     * @return true pas de mail envoyé, false mail envoyé
     */
    public boolean isDeactivate() {
        if ("true".equalsIgnoreCase(iDeactivate)) {
            return true;
        }
        return false;
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return iProperties;
    }

    /**
     * @return le protocole ssl utilisé
     */
    public String getSslProtocols() {
        return iSslProtocols;
    }

    /**
     * @param aSslProtocols
     *            le protocole ssl utilise
     */
    public void setSslProtocols(String aSslProtocols) {
        iSslProtocols = aSslProtocols;
    }

}
