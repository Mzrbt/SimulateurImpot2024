package com.kerware.simulateurreusine;

/**
 * Calculateur du plafond de gain sur demi-parts supplémentaires.
 * 
 * Exigence EXG_IMPOT_05 : Le plafond de gain est limité à 1 759€ par demi-part supplémentaire.
 * 
 * Cela limite l'économie d'impôt accordée par les demi-parts pour enfants
 * et autres (parent isolé, handicap, etc.).
 * 
 * La baisse d'impôt accordée par le quotient familial ne peut pas dépasser
 * le nombre de demi-parts × 1 759€.
 */
public class CalculateurPlafondGain {

    // Plafond de gain par demi-part supplémentaire (2024)
    private static final double PLAFOND_PAR_DEMI_PART = 1_759;

    /**
     * Calcule le plafond maximal de gain d'impôt accordé par le quotient familial.
     * 
     * @param nbPartsDeclarant nombre de parts du déclarant (généralement 1 ou 2)
     * @param nbPartsTotal nombre total de parts du foyer
     * @return le plafond de gain d'impôt en euros
     */
    public long calculerPlafondGain(double nbPartsDeclarant, double nbPartsTotal) {
        if (nbPartsDeclarant < 0 || nbPartsTotal < 0) {
            throw new IllegalArgumentException("Le nombre de parts ne peut pas être négatif");
        }

        if (nbPartsTotal <= nbPartsDeclarant) {
            // Pas de demi-parts supplémentaires
            return 0;
        }

        // Écart de parts (nombre de demi-parts supplémentaires)
        double ecartParts = nbPartsTotal - nbPartsDeclarant;

        // Le plafond est : (écart / 0.5) * 1759
        // Qui devient : écart * 2 * 1759
        long plafond = Math.round(ecartParts / 0.5 * PLAFOND_PAR_DEMI_PART);

        return plafond;
    }

    /**
     * Applique le plafond de gain à la réduction d'impôt.
     * 
     * @param reduImpot la réduction d'impôt avant plafond
     * @param plafondGain le plafond maximal de gain
     * @return la réduction d'impôt après application du plafond
     */
    public long appliquerPlafond(long reduImpot, long plafondGain) {
        if (reduImpot > plafondGain) {
            return plafondGain;
        }
        return reduImpot;
    }
}
