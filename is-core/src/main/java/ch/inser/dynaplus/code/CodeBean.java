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

package ch.inser.dynaplus.code;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.inser.dynamic.common.DAOParameter;
import ch.inser.dynamic.common.DAOParameter.Name;
import ch.inser.dynamic.common.IValueObject;
import ch.inser.dynaplus.auth.SuperUser;
import ch.inser.dynaplus.bo.BPFactory;
import ch.inser.dynaplus.util.Constants.Entity;
import ch.inser.dynaplus.vo.IVOFactory;
import ch.inser.jsl.exceptions.ISException;

/**
 * Bean pour les codes d'une langue. Ce code bean est utilisé par la couche BP, p.ex. pour formatter une liste de vos en fichier CSV.
 *
 * Il peut aussi être utilisé par le TranslateResource pour fournir les traductions des codes au frontend Angular
 *
 * Implémentation avec objets standards CODE et CODETEXT
 *
 * @author INSER SA
 *
 */
public class CodeBean implements Serializable {

    /**
     * UID
     */
    private static final long serialVersionUID = 4292884329473251153L;

    /**
     * Codes courts field->code->label
     */
    private HashMap<String, Map<Object, String>> iCodes;

    /**
     * Codes longs
     */
    private HashMap<String, Map<Object, String>> iCodesLong;

    /**
     * Locale
     */
    private Locale iLocale;

    /**
     * BPFactory
     */
    private transient BPFactory iBPFactory;

    /**
     * VO factory
     */
    private transient IVOFactory iVOFactory;

    /**
     *
     * @param aLocale
     *            Locale des codes à charger
     */
    public CodeBean(Locale aLocale) {
        iLocale = aLocale;
    }

    /**
     * Initialise les codes
     *
     * @throws ISException
     *             erreur
     */
    public void init() throws ISException {
        // Reset the maps
        iCodes = new HashMap<>();
        iCodesLong = new HashMap<>();

        // Fill the maps with the codes
        List<IValueObject> listCodes = getCodeList(Entity.CODETEXT.toString());
        for (IValueObject vo : listCodes) {
            addCode(vo, "ctx_textcourt", iCodes);
            addCode(vo, "ctx_textlong", iCodesLong);
        }

    }

    /**
     * Ajoute un code.
     *
     * @param aVo
     *            vo code
     * @param aTextField
     *            nom du champ à ajouter (text court, text long, commentaire)
     * @param aCodes
     *            map avec les codes
     */
    private void addCode(IValueObject aVo, String aTextField, Map<String, Map<Object, String>> aCodes) {
        String textField = aTextField;
        String fieldName = (String) aVo.getProperty("cod_fieldname");

        // Get the field
        Map<Object, String> field = aCodes.computeIfAbsent(fieldName, k -> new HashMap<>());

        // Add the code
        if (aVo.getProperty(textField) != null) {
            field.put(aVo.getProperty("cod_code"), (String) aVo.getProperty(textField));
        }
    }

    /**
     *
     * @param aEntity
     *            nom de l'objet métier
     * @return listes de codes selon le nom de l'objet code (CODE, CODE_INTERNE etc.)
     * @throws ISException
     *             Problème DB
     */
    private List<IValueObject> getCodeList(String aEntity) throws ISException {
        IValueObject qVo = iVOFactory.getVO(aEntity);
        qVo.setProperty("ctx_lang", iLocale.getLanguage());
        return iBPFactory.getBP(aEntity).getList(qVo, new SuperUser(), new DAOParameter(Name.ROWNUM_MAX, 0)).getListObject();
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
     * @param aLocale
     *            langue
     */
    public void setLocale(Locale aLocale) {
        iLocale = aLocale;
    }

    /**
     *
     * @param aVOF
     *            VO factory
     */
    public void setVOFactory(IVOFactory aVOF) {
        iVOFactory = aVOF;
    }

    /**
     *
     * @return noms courts de codes
     */
    public Map<String, Map<Object, String>> getCodes() {
        return iCodes;
    }

    /**
     *
     * @return noms longs de codes
     */
    public Map<String, Map<Object, String>> getCodesLong() {
        return iCodesLong;
    }

}
