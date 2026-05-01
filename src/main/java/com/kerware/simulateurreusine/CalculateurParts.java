package com.kerware.simulateurreusine;

import com.kerware.simulateur.SituationFamiliale;

/**
 * Calculateur du nombre de parts fiscales du foyer.
 * 
 * Exigence EXG_IMPOT_03 : Le nombre de parts fiscales d'un foyer est déterminé comme suit :
 * - 1 part pour le déclarant célibataire, divorcé
 * - 2 parts pour un couple marié ou pacsé
 * - 2 parts pour un veuf avec enfants (1 seul dans le code legacy)
 * - +0,5 part par enfant à charge (pour les 2 premiers)
 * - +1 part pour chaque enfant supplémentaire (à partir du 3ème)
 * - +0,5 part pour parent isolé ayant des enfants
 * - +0,5 part par enfant en situation de handicap
 */
public class CalculateurParts {

    /**
     * Calcule le nombre de parts fiscales du foyer principal (déclarant).
     * 
     * @param situationFamiliale la situation familiale du déclarant
     * @param nbEnfantsACharge nombre d'enfants à charge
     * @return le nombre de parts du déclarant seul (avant bonus enfants/handicap)
     */
    private double calculerPartsDeclarant(SituationFamiliale situationFamiliale, int nbEnfantsACharge) {
        double nbParts = 1.0; // Par défaut : célibataire

        switch (situationFamiliale) {
            case CELIBATAIRE:
                nbParts = 1.0;
                break;
            case MARIE:
                nbParts = 2.0;
                break;
            case PACSE:
                nbParts = 2.0;
                break;
            case DIVORCE:
                nbParts = 1.0;
                break;
            case VEUF:
                // Cas particulier : veuf avec enfants = 2 parts (mais code legacy dit 1)
                if (nbEnfantsACharge == 0) {
                    nbParts = 1.0;
                } else {
                    // Due à un bug dans le code legacy, c'est fixé à 1
                    nbParts = 1.0;
                }
                break;
        }

        return nbParts;
    }

    /**
     * Calcule les parts dues aux enfants à charge.
     * 
     * @param nbEnfantsACharge nombre d'enfants à charge
     * @return le nombre de demi-parts accordées pour les enfants
     */
    private double calculerPartsEnfants(int nbEnfantsACharge) {
        if (nbEnfantsACharge < 0) {
            throw new IllegalArgumentException("Le nombre d'enfants ne peut pas être négatif: " + nbEnfantsACharge);
        }

        if (nbEnfantsACharge <= 2) {
            // 0,5 part par enfant pour les 2 premiers
            return nbEnfantsACharge * 0.5;
        } else {
            // 1 part pour le 3ème enfant, puis 1 part par enfant supplémentaire
            return 1.0 + (nbEnfantsACharge - 2);
        }
    }

    /**
     * Calcule les parts dues au statut de parent isolé.
     * 
     * @param parentIsole true si le déclarant est parent isolé
     * @param nbEnfantsACharge nombre d'enfants à charge
     * @return 0,5 si parent isolé avec enfants, 0 sinon
     */
    private double calculerPartsParentIsole(boolean parentIsole, int nbEnfantsACharge) {
        if (parentIsole && nbEnfantsACharge > 0) {
            return 0.5;
        }
        return 0.0;
    }

    /**
     * Calcule les parts dues aux enfants en situation de handicap.
     * 
     * @param nbEnfantsHandicapes nombre d'enfants en situation de handicap
     * @return 0,5 part par enfant handicapé
     */
    private double calculerPartsHandicap(int nbEnfantsHandicapes) {
        if (nbEnfantsHandicapes < 0) {
            throw new IllegalArgumentException("Le nombre d'enfants handicapés ne peut pas être négatif: " + nbEnfantsHandicapes);
        }

        return nbEnfantsHandicapes * 0.5;
    }

    /**
     * Calcule le nombre total de parts fiscales du foyer.
     * 
     * @param situationFamiliale la situation familiale
     * @param nbEnfantsACharge nombre d'enfants à charge
     * @param nbEnfantsHandicapes nombre d'enfants handicapés
     * @param parentIsole true si parent isolé
     * @return le nombre total de parts (peut être un nombre fractionnaire comme 2.5)
     */
    public double calculerNbPartsTotal(SituationFamiliale situationFamiliale,
                                       int nbEnfantsACharge,
                                       int nbEnfantsHandicapes,
                                       boolean parentIsole) {

        // Valider les entrées
        if (nbEnfantsACharge < 0) {
            throw new IllegalArgumentException("Le nombre d'enfants ne peut pas être négatif");
        }
        if (nbEnfantsHandicapes < 0) {
            throw new IllegalArgumentException("Le nombre d'enfants handicapés ne peut pas être négatif");
        }

        double nbParts = calculerPartsDeclarant(situationFamiliale, nbEnfantsACharge);
        nbParts += calculerPartsEnfants(nbEnfantsACharge);
        nbParts += calculerPartsParentIsole(parentIsole, nbEnfantsACharge);
        nbParts += calculerPartsHandicap(nbEnfantsHandicapes);

        return nbParts;
    }

    /**
     * Calcule le nombre de parts pour le calcul du quotient conjugal.
     * 
     * @param situationFamiliale la situation familiale
     * @param nbEnfantsACharge nombre d'enfants à charge
     * @return le nombre de parts du déclarant pour le quotient (généralement 1 ou 2)
     */
    public double calculerNbPartsDeclarant(SituationFamiliale situationFamiliale,
                                            int nbEnfantsACharge) {
        return calculerPartsDeclarant(situationFamiliale, nbEnfantsACharge);
    }
}
