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

package ch.inser.dynaplus.help;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.ILoggedUser;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.bo.BPFactory;
import ch.inser.dynaplus.bo.IBusinessProcess;
import ch.inser.dynaplus.vo.VOFactory;
import ch.inser.jsl.exceptions.ISException;
import ch.inser.jsl.list.ListHandler.Sort;

/**
 *
 * Class containing the lists of multilingual texts that figure in tooltips, labels, GUI elements, error messages. Initialized by the
 * AppInitServlet and managed by the ContextManager. Used both at BB and BO level.
 *
 * @author INSER SA
 *
 */
public class HelpBean implements IApplicationHelpBean, Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -6358052138605992402L;

    /** Définition de la catégorie de logging */
    private static final Log logger = LogFactory.getLog(HelpBean.class);

    /** BPFactory à disposition du process métier */
    private BPFactory iBPFactory;

    /**
     * Contains the list of short-form help messages according to the structure: Language -> Map of short texts <br>
     * Help key -> Short text
     */
    private Map<Locale, Map<String, String>> iHelpShortTexts = new HashMap<>();

    /**
     * Contains the list of long help messages according to the structure: Language -> Map of long texts <br>
     * Help key -> Long text
     */
    private Map<Locale, Map<String, String>> iHelpTexts = new HashMap<>();

    /**
     * Contient les labels de l'applications lorsqu'ils sont dans la BD
     */
    private Map<Locale, Map<String, String>> iLabelTexts = new HashMap<>();

    /**
     * Contains the list of available languages for the help texts
     */
    private List<String> iLanguageList = new ArrayList<>();

    private ILoggedUser iUser;

    public HelpBean() {
    }

    @Override
    public Map<String, String> getHelpShortTexts(Locale aLocale) {
        if (iHelpShortTexts.get(aLocale) != null) {
            return iHelpShortTexts.get(aLocale);
        }
        Locale loc = new Locale(aLocale.getLanguage());
        if (iHelpShortTexts.get(loc) != null) {
            return iHelpShortTexts.get(loc);
        }
        return iHelpShortTexts.get(getDefaultLanguage());
    }

    @Override
    public Map<String, String> getHelpTexts(Locale aLocale) {
        if (iHelpTexts.get(aLocale) != null) {
            return iHelpTexts.get(aLocale);
        }
        Locale loc = new Locale(aLocale.getLanguage());
        if (iHelpTexts.get(loc) != null) {
            return iHelpTexts.get(loc);
        }
        return iHelpTexts.get(getDefaultLanguage());
    }

    @Override
    public Map<String, String> getLabelTexts(Locale aLocale) {
        if (iLabelTexts.get(aLocale) != null) {
            return iLabelTexts.get(aLocale);
        }
        Locale loc = new Locale(aLocale.getLanguage());
        if (iLabelTexts.get(loc) != null) {
            return iLabelTexts.get(loc);
        }
        return iLabelTexts.get(getDefaultLanguage());
    }

    @Override
    public Locale getDefaultLanguage() {
        return Locale.FRENCH;
    }

    @Override
    public void init() {
        logger.debug("INIT");
        try {

            // Initialize languages
            IBusinessProcess bpText = iBPFactory.getBP("Help_text");
            IValueObject vo = VOFactory.getInstance().getVO("Help_text");
            List<?> listRecords = bpText.getList(vo, iUser, new DAOParameter(DAOParameter.Name.SORT_INDEX, 0),
                    new DAOParameter(DAOParameter.Name.SORT_ORIENTATION, Sort.ASCENDING), new DAOParameter(DAOParameter.Name.ROWNUM_MAX, 0))
                    .getListObject();

            for (Object obj : listRecords) {
                IValueObject voRec = (IValueObject) obj;
                if (!iLanguageList.contains(voRec.getProperty("htx_iso_lang_code"))) {
                    iLanguageList.add((String) voRec.getProperty("htx_iso_lang_code"));
                }
            }

            // Initialize help texts for each language
            for (String lang : iLanguageList) {
                IValueObject voText = VOFactory.getInstance().getVO("Help_text");
                voText.setProperty("htx_iso_lang_code", lang);

                // Demande la liste des help recherchés
                List<?> listHelpText = bpText.getList(voText, iUser, new DAOParameter(DAOParameter.Name.SORT_INDEX, 0),
                        new DAOParameter(DAOParameter.Name.SORT_ORIENTATION, Sort.ASCENDING),
                        new DAOParameter(DAOParameter.Name.ROWNUM_MAX, 0)).getListObject();

                Map<String, String> shortTextMap = new HashMap<>();
                Map<String, String> textMap = new HashMap<>();
                Map<String, String> labelMap = new HashMap<>();

                // Initialize for each key
                for (Object obj : listHelpText) {
                    IValueObject voKey = (IValueObject) obj;
                    shortTextMap.put((String) voKey.getProperty("hke_name"), (String) voKey.getProperty("htx_short_text"));
                    textMap.put((String) voKey.getProperty("hke_name"), (String) voKey.getProperty("htx_long_text"));
                    labelMap.put((String) voKey.getProperty("hke_name"), (String) voKey.getProperty("htx_label"));
                }

                getHelpShortTexts().put(new Locale(lang), shortTextMap);
                getHelpTexts().put(new Locale(lang), textMap);
                getLabelTexts().put(new Locale(lang), labelMap);

            } // end for init per language
            logger.debug("End init ApplicationHelpBean");
        } catch (ISException e) {
            logger.error("Erreur lors de l'initialisation", e);

        }

    }

    /**
     * Setter
     *
     * @param aBPFactory
     */
    public void setBPFactory(BPFactory aBPFactory) {
        iBPFactory = aBPFactory;
    }

    /**
     * Setter
     *
     * @param aUser
     */
    public void setUser(ILoggedUser aUser) {
        iUser = aUser;
    }

    /**
     * Getter
     *
     * @return short texts (tooltip)
     */
    public Map<Locale, Map<String, String>> getHelpShortTexts() {
        return iHelpShortTexts;
    }

    /**
     * Getter
     *
     * @return long texts (tooltip)
     */
    public Map<Locale, Map<String, String>> getHelpTexts() {
        return iHelpTexts;
    }

    /**
     *
     * @return Labels
     */
    public Map<Locale, Map<String, String>> getLabelTexts() {
        return iLabelTexts;
    }

    /**
     * Getter
     *
     * @return liste de langues de l'application
     */
    @Override
    public List<String> getLanguageList() {
        return iLanguageList;
    }

}
