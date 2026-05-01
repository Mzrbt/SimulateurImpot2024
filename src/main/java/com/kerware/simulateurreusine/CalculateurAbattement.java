package com.kerware.simulateurreusine;

/**
 * Calculateur d'abattement fiscal.
 * 
 * Exigence EXG_IMPOT_02 : L'abattement est fixé à 10% du revenu net
 * avec un minimum de 495€ et un maximum de 14171€ (2024).
 * 
 * L'abattement est appliqué avant le calcul de l'impôt sur le revenu.
 */
public class CalculateurAbattement {

    private static final double TAUX_ABATTEMENT = 0.10;
    private static final int ABATTEMENT_MINIMUM = 495;
    private static final int ABATTEMENT_MAXIMUM = 14171;

    /**
     * Calcule l'abattement applicable au revenu net.
     * 
     * @param revenuNet le revenu net avant abattement
     * @return l'abattement calculé, limité entre 495€ et 14171€
     */
    public int calculer(int revenuNet) {
        if (revenuNet < 0) {
            throw new IllegalArgumentException("Le revenu net ne peut pas être négatif: " + revenuNet);
        }

        double abattement = revenuNet * TAUX_ABATTEMENT;

        // Appliquer le plafond minimum
        if (abattement < ABATTEMENT_MINIMUM) {
            abattement = ABATTEMENT_MINIMUM;
        }

        // Appliquer le plafond maximum
        if (abattement > ABATTEMENT_MAXIMUM) {
            abattement = ABATTEMENT_MAXIMUM;
        }

        return (int) Math.round(abattement);
    }

    /**
     * Retourne le revenu fiscal de référence après application de l'abattement.
     * 
     * EXG_IMPOT_02 : Le revenu fiscal de référence = revenu net - abattement
     * 
     * @param revenuNet le revenu net
     * @return le revenu fiscal de référence
     */
    public int calculerRevenuFiscalReference(int revenuNet) {
        int abattement = calculer(revenuNet);
        return revenuNet - abattement;
    }
}
