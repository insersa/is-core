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

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Description d'un bean applicatif pour l'exploitation des données d'aide
 * 
 * @author INSER SA
 * 
 */
public interface IApplicationHelpBean {

    /**
     * Retourne une Map de help_key, help_shorttext dans la langue demandée. Si la langue n'existe pas, retourne la langue par défaut.
     * 
     * @param aLocale
     * @return
     */
    public Map<String, String> getHelpShortTexts(Locale aLocale);

    /**
     * Retourne une Map de help_key, help_text dans la langue demandée. Si la langue n'existe pas, retourne la langue par défaut.
     * 
     * @param aLocale
     * @return
     */
    public Map<String, String> getHelpTexts(Locale aLocale);

    public Map<String, String> getLabelTexts(Locale aLocale);

    public Locale getDefaultLanguage();

    /**
     * Getter
     * 
     * @return liste de langues de l'application
     */
    public List<String> getLanguageList();

    public void init();

}
