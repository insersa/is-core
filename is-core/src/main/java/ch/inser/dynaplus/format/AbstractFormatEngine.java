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

package ch.inser.dynaplus.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.DAOResult;
import ch.inser.dynamic.common.IContextManager;
import ch.inser.dynamic.common.IDAOResult;
import ch.inser.dynamic.common.IDAOResult.Status;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.bo.BPFactory;
import ch.inser.dynaplus.code.CodeBean;
import ch.inser.dynaplus.help.HelpBean;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;

/**
 * Méthodes génériques pour formatter un VO ou une liste de VOs
 *
 * @author INSER SA
 *
 */
public class AbstractFormatEngine implements IFormatEngine {

    /** Context manager */
    private IContextManager iCtx;

    /** VO factory */
    private IVOFactory iVOFactory;

    /** BP factory */
    private BPFactory iBPFactory;

    /** Séparateur dans un fichier csv, ex. ";" */
    protected String iCSVSeparator = ",";

    @Override
    public IDAOResult format(List<IValueObject> aRecords, ILoggedUser aUser, DAOParameter... aParams) throws ISException {
        if (Format.CSV.equals(DAOParameter.getValue(Name.RESULT_FORMAT, aParams))) {
            IDAOResult result = new DAOResult(Status.OK);
            result.setValue(formatCSV(aRecords, aParams, aUser));
            return result;
        }
        throw new UnsupportedOperationException("Format " + DAOParameter.getValue(Name.RESULT_FORMAT, aParams) + " not implemented!");
    }

    /**
     * Formatte une liste de vos en fichier csv
     *
     * @param aRecords
     *            enregistrements
     * @param aParams
     *            paramètres d'export (noms des champs)
     * @param aUser
     *            utilisateur logué
     * @return le fichier csv en byte array
     * @throws ISException
     *             erreur d'écriture de csv
     */
    private byte[] formatCSV(List<IValueObject> aRecords, DAOParameter[] aParams, ILoggedUser aUser) throws ISException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            Locale lang = getLanguage(aParams);
            writeHeaders(stream, aParams, lang);
            writeRecords(stream, aParams, aRecords, lang, aUser);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new ISException("Erreur de création de fichier CSV", e);
        }
    }

    /**
     *
     * @param aParams
     *            format parameters
     * @return language of formatted file
     */
    private Locale getLanguage(DAOParameter[] aParams) {
        String lang = (String) DAOParameter.getValue(Name.RESULT_LANG, aParams);
        if (lang == null) {
            lang = iCtx.getProperty("report.default.lang");
        }
        return new Locale(lang);
    }

    /**
     * Ecrit les données dans le CSV
     *
     * @param aStream
     *            le stream pour le fichier csv
     * @param aParams
     *            paramètres de la tâche d'export (noms des champs)
     * @param aRecords
     *            les enregistrements à exporter
     * @param aLang
     *            langue
     * @param aUser
     *            utilisateur logué
     * @throws ISException
     *             erreur d'écriture
     */
    private void writeRecords(ByteArrayOutputStream aStream, DAOParameter[] aParams, List<IValueObject> aRecords, Locale aLang,
            ILoggedUser aUser) throws ISException {
        if (aRecords.isEmpty()) {
            return;
        }
        String fieldsStr = (String) DAOParameter.getValue(Name.RESULT_FIELDS, aParams);
        if (fieldsStr == null) {
            return;
        }
        String[] fields = fieldsStr.split(",");
        Map<String, Map<Object, String>> codes = getCodes(aLang);
        for (IValueObject vo : aRecords) {
            StringBuilder row = new StringBuilder();

            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];

                row.append(valueToString(vo, field, codes, aUser));

                if (i < fields.length - 1) {
                    row.append(iCSVSeparator);
                }
            }
            row.append("\n");
            try {
                aStream.write(row.toString().getBytes(StandardCharsets.ISO_8859_1));
            } catch (IOException e) {
                throw new ISException("Erreur d'écriture de ligne dans le fichier CSV", e);
            }
        }
    }

    /**
     * Pour une valeur dans le vo, donne la valeur en format de string, çvd comme elle doit apparaitre dans le fichier csv. Pour un code la
     * valeur numérique et textuelle sont écrits dans deux colonnes
     *
     * @param aVo
     *            le vo d'un objet à écrire dans le fichier csv
     * @param aField
     *            nom du champ
     * @param aCodes
     *            liste de codes
     * @param aUser
     *            utilisateur logué
     * @return la valeur du champ en forme du texte
     */
    protected String valueToString(IValueObject aVo, String aField, Map<String, Map<Object, String>> aCodes, ILoggedUser aUser) {
        Object value = aVo.getProperty(aField);
        StringBuilder result = new StringBuilder();

        // Valeur original
        if (value != null && aUser.isReadField(aVo.getName(), aField)) {
            result.append("\"");
            result.append(formatValue(value));
            result.append("\"");
        }
        // Code -> ajoute le libellé
        String codetype = (String) aVo.getVOInfo().getInfo(aField, "coderef");
        if (codetype == null) {
            codetype = aField;
        }
        if (aCodes.get(codetype) != null) {
            if (value != null && aCodes.get(codetype).get(value) != null && aUser.isReadField(aVo.getName(), aField)) {
                result.append("\"");
                result.append(aCodes.get(codetype).get(value));
                result.append("\"");
            }
        }
        return result.toString();
    }

    /**
     * Fait du formattage d'une valeur en string, ex. dates et timestamps
     *
     * @param aValue
     *            valeur à écrire
     * @return valeur formatté
     */
    protected String formatValue(Object aValue) {
        if (aValue == null) {
            return null;
        }
        if (aValue instanceof Timestamp) {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(aValue);
        }
        if (aValue instanceof Date) {
            return new SimpleDateFormat("dd.MM.yyyy").format(aValue);
        }
        return aValue.toString().replace("\r", "");
    }

    /**
     * Ecrit les entêtes des colonnes du CSV
     *
     * @param aStream
     *            stream pour écrire le fichier csv
     * @param aParams
     *            paramètres de format de CSV
     * @param aLang
     *            langue des libellés
     * @throws ISException
     *             erreur d'écriture dans le fichier csv
     */
    private void writeHeaders(ByteArrayOutputStream aStream, DAOParameter[] aParams, Locale aLang) throws ISException {
        if (DAOParameter.getValue(Name.RESULT_LABEL_KEYS, aParams) == null) {
            return;
        }

        String[] labelKeys = ((String) DAOParameter.getParameter(Name.RESULT_LABEL_KEYS, aParams).getValue()).split(",");
        Map<String, String> labels = ((HelpBean) iCtx.getHelpBean()).getLabelTexts(aLang);
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < labelKeys.length; i++) {
            String key = labelKeys[i];
            if (!key.isEmpty()) {
                row.append("\"");
                row.append(labels.get(key));
                row.append("\"");
            }
            if (i < labelKeys.length - 1) {
                row.append(iCSVSeparator);
            }
        }
        row.append("\n");
        try {
            aStream.write(row.toString().getBytes(StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            throw new ISException("Erreur d'écriture des headers du CSV", e);
        }

    }

    /**
     *
     * @param aCtx
     *            context manager
     */
    public void setContextManager(IContextManager aCtx) {
        iCtx = aCtx;
        if (iCtx.getProperty("csv.value.separator") != null) {
            iCSVSeparator = iCtx.getProperty("csv.value.separator");
        }
    }

    /**
     *
     * @return context manager
     */
    public IContextManager getContextManager() {
        return iCtx;
    }

    /**
     *
     * @return vo factory
     */
    public IVOFactory getVOFactory() {
        return iVOFactory;
    }

    /**
     *
     * @return BP factory
     */
    public BPFactory getBPFactory() {
        return iBPFactory;
    }

    @Override
    public IDAOResult format(IValueObject aVo, DAOParameter... aParameters) throws ISException {
        throw new UnsupportedOperationException("Formatting of single record not implemented");
    }

    /**
     * Cherche les codes de l'application
     *
     * @param aLang
     *            langue du fichier csv
     * @return map avec les codes: field->code->label
     * @throws ISException
     *             erreur de récuperation des codes
     */
    protected Map<String, Map<Object, String>> getCodes(Locale aLang) throws ISException {
        CodeBean codes = iCtx.getCacheManager().getCache("codeCache", String.class, CodeBean.class).get(aLang.getLanguage());
        if (codes == null) {
            codes = new CodeBean(aLang);
            codes.setBPFactory(iBPFactory);
            codes.setVOFactory(iVOFactory);
            codes.init();
            iCtx.getCacheManager().getCache("codeCache", String.class, CodeBean.class).put(aLang.getLanguage(), codes);
        }
        return codes.getCodes();
    }

    /**
     *
     * @param aBP
     *            BP factory
     */
    public void setBPFactory(BPFactory aBP) {
        iBPFactory = aBP;
    }

    /**
     *
     * @param aVOF
     *            VO factory
     */
    public void setVOFactory(IVOFactory aVOF) {
        iVOFactory = aVOF;
    }

}
