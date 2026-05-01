package com.kerware.simulateurreusine;

/**
 * Calculateur de la décote pour les bas revenus.
 * 
 * Exigence EXG_IMPOT_06 : Une décote est appliquée aux personnes ayant un impôt faible.
 * 
 * La décote fonctionne comme suit :
 * - Pour les célibataires : si impôt < 1 929€ → décote = 873€ - (impôt × 0,4525)
 * - Pour les couples : si impôt < 3 191€ → décote = 1 444€ - (impôt × 0,4525)
 * - La décote est plafonnée au montant de l'impôt (on ne peut pas avoir d'impôt négatif)
 */
public class CalculateurDecote {

    // Seuils et montants de décote pour 2024
    private static final double SEUIL_DECOTE_CELIBATAIRE = 1_929;
    private static final double SEUIL_DECOTE_COUPLE = 3_191;

    private static final double MONTANT_DECOTE_CELIBATAIRE = 873;
    private static final double MONTANT_DECOTE_COUPLE = 1_444;

    private static final double TAUX_CALCUL_DECOTE = 0.4525;

    /**
     * Calcule la décote applicable en fonction du nombre de parts du déclarant.
     * 
     * @param impotSansDecote l'impôt avant décote
     * @param nbPartsDeclarant nombre de parts du déclarant (1 pour célibataire, 2 pour couple)
     * @return la décote calculée, plafonnée à l'impôt
     */
    public long calculerDecote(long impotSansDecote, double nbPartsDeclarant) {
        if (impotSansDecote < 0) {
            throw new IllegalArgumentException("L'impôt ne peut pas être négatif: " + impotSansDecote);
        }

        double decote = 0;

        if (nbPartsDeclarant == 1.0) {
            // Célibataire
            if (impotSansDecote < SEUIL_DECOTE_CELIBATAIRE) {
                decote = MONTANT_DECOTE_CELIBATAIRE - (impotSansDecote * TAUX_CALCUL_DECOTE);
            }
        } else if (nbPartsDeclarant == 2.0) {
            // Couple (marié ou pacsé)
            if (impotSansDecote < SEUIL_DECOTE_COUPLE) {
                decote = MONTANT_DECOTE_COUPLE - (impotSansDecote * TAUX_CALCUL_DECOTE);
            }
        }

        // Arrondir la décote
        decote = Math.round(decote);

        // La décote ne peut pas dépasser l'impôt (pas d'impôt négatif)
        if (decote > impotSansDecote) {
            decote = impotSansDecote;
        }

        // La décote ne peut pas être négative
        if (decote < 0) {
            decote = 0;
        }

        return (long) decote;
    }
}
