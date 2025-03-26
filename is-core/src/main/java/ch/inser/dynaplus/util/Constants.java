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

package ch.inser.dynaplus.util;

/**
 * Classe contenant diverses constantes utilisées dans le cadre de la librairie dynaplus.
 *
 * @version 1.0
 * @author INSER SA
 */
public class Constants extends ch.inser.dynamic.util.Constants {

    /**
     * Constructeur caché, toute les méthodes étant statiques!
     */
    private Constants() {
        // Pour cacher le constructeur
    }

    /**
     * Modes de travail
     */
    public enum Mode {
        none, search, consult, create, multicreate, edit, delete, multiedit, list, report, delcascade, searchlist;
    }

    /**
     * Constantes d'actions
     */
    public enum Action {
        EXECUTE,

        CANCEL,

        EDIT,

        MULTIEDIT,

        SEARCH,

        DELETE,

        RESET,

        SELECT,

        CREATE,

        MULTICREATE,

        LIST_DELETE,

        LIST_CREATE,

        LIST_SAVE,

        LIST_REFERANT,

        LIST_SELECT,

        REPORT,

        DELETECASCADE,

        SAVE,

        SAVECLOSE,

        CLOSE,

        /** Aller sur le prochain enregistrement dans la liste */
        NEXT,

        /** Aller sur l'enregistrement précédent dans la liste */
        PREVIOUS,

        /** Retourner à la liste de recherche */
        RETURNLIST;
    }

    /**
     * Objets spéciales crées par les factories (VOFactory, BOFactory etc.)
     *
     * @author INSER SA
     *
     */
    public enum Entity {
        /**
         * Objet anonyme pour accéder directement à une table, p.ex. une table temporaire
         */
        anonymous,

        /** Document attaché */
        DOCUMENT,

        /** Code */
        CODE,

        /** Code */
        CODETEXT,

        /** Commentaire incrémentale */
        COMMENT,

        /** Journal */
        JOURNAL,

        /** Help key */
        HELPKEY,

        /** Help text */
        HELPTEXT,

        /** Alert text */
        ALERTTEXT,

        /** Menu item */
        MENUITEM,

        /** Action item */
        ACTITEM,

        /** Permission sur menu */
        MENUPERM,

        /** Permission sur action sur menu */
        ACTPERM,

        /** Items dans la table T_TABITEM */
        TABITEM,

        /** Permissions sur les champs */
        DATAPERM;

        @Override
        public String toString() {
            if (DOCUMENT.equals(this)) {
                return "Document";
            }
            if (CODE.equals(this)) {
                return "Code";
            }
            if (CODETEXT.equals(this)) {
                return "Codetext";
            }
            if (COMMENT.equals(this)) {
                return "Comment";
            }
            if (JOURNAL.equals(this)) {
                return "Journal";
            }
            if (HELPKEY.equals(this)) {
                return "Help_key";
            }
            if (HELPTEXT.equals(this)) {
                return "Help_text";
            }
            if (this == ALERTTEXT) {
                return "AlertText";
            }
            if (this == MENUITEM) {
                return "MenuItem";
            }
            if (this == ACTITEM) {
                return "ActItem";
            }
            if (this == MENUPERM) {
                return "MenuPerm";
            }
            if (this == ACTPERM) {
                return "ActPerm";
            }
            if (this == TABITEM) {
                return "TabItem";
            }
            if (this == DATAPERM) {
                return "DataPerm";
            }
            return super.toString();
        }
    }

    /**
     * Format de retour pour des méthodes comme getRecord et getList
     *
     * @author INSER SA
     *
     */
    public enum ResultFormat {
        /** Format PDF pour un enregistrement */
        PDF,
        /** Format csv pour une liste */
        CSV;
    }

    /**
     * Types d'items dans la table T_TABLITEM
     *
     * @author INSER SA
     *
     */
    public enum TabItem {

        /** Database */
        DATABASE(0),

        /** Table */
        TABLE(1),

        /** Field */
        FIELD(2);

        /** Valeur code */
        private int iValue;

        /**
         *
         * @param aValue
         *            valeur code
         */
        private TabItem(int aValue) {
            iValue = aValue;
        }

        /**
         *
         * @return valeur code
         */
        public long getValue() {
            return iValue;
        }
    }

    /**
     * Droits de lecture et écriture de champs
     *
     * @author INSER SA
     *
     */
    public enum FieldPerm {
        /** Droit de lire */
        READ(0),
        /** Droit de lire et écrire */
        WRITE(1);

        /** Valeur numérique de la permission */
        private int iValue;

        /**
         *
         * @param aValue
         *            Valeur numérique de la permission
         */
        private FieldPerm(int aValue) {
            iValue = aValue;
        }

        /**
         *
         * @return Valeur numérique de la permission
         */
        public int getValue() {
            return iValue;
        }

    }

    /**
     *
     * Droit d'édition sur les données
     *
     * @author INSER SA
     *
     */
    public enum DataPermType {

        /** Droit de lire */
        READ(0),

        /** Droit d'écrire et lire */
        WRITE(1);

        /**
         * Valeur code
         */
        private int iValue;

        /**
         *
         * @param aValue
         *            valeur code
         */
        private DataPermType(int aValue) {
            iValue = aValue;
        }

        /**
         *
         * @return valeur code
         */
        public long getValue() {
            return iValue;
        }
    }

    /**
     * Valeurs "code" pour les booleans
     *
     * @author INSER SA
     *
     */
    public enum CodeBoolean {
        /**
         * Boolean true
         */
        OUI(1),
        /**
         * Boolean false
         */
        NON(0);

        /**
         * Valeur numérique du code
         */
        private long iValue;

        /**
         *
         * @param aValue
         *            valeur numérique du code
         */
        private CodeBoolean(long aValue) {
            iValue = aValue;
        }

        /**
         *
         * @return valeur numérique du boolean
         */
        public long getValue() {
            return iValue;
        }

        public static CodeBoolean parse(Long aValue) {
            switch (aValue.intValue()) {
                case 1:
                    return OUI;
                case 0:
                    return NON;
                default:
                    return null;
            }
        }
    }

    /**
     * Niveaux d'alert
     *
     * @author INSER SA
     *
     */
    public enum CodeAlertLevel {
        /**
         * Boolean true
         */
        INFO(0),
        /**
         * Boolean false
         */
        WARN(1);

        /**
         * Valeur numérique du code
         */
        private long iValue;

        /**
         *
         * @param aValue
         *            valeur numérique du code
         */
        private CodeAlertLevel(long aValue) {
            iValue = aValue;
        }

        /**
         *
         * @return valeur numérique du boolean
         */
        public long getValue() {
            return iValue;
        }

        /**
         *
         * @param aValue
         *            valeur "code"
         * @return
         */
        public static CodeAlertLevel parse(Long aValue) {
            switch (aValue.intValue()) {
                case 0:
                    return INFO;
                case 1:
                    return WARN;
                default:
                    return null;
            }
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
