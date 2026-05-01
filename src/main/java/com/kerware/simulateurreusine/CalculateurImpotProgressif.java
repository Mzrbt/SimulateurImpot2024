package com.kerware.simulateurreusine;

/**
 * Calculateur de l'impôt sur le revenu selon le barème progressif français.
 * 
 * Exigence EXG_IMPOT_04 : L'impôt sur le revenu est calculé selon un barème progressif.
 * Les tranches fiscales 2024 (revenus 2023) sont :
 * - 0% jusqu'à 11 294€
 * - 11% de 11 294€ à 28 797€
 * - 30% de 28 797€ à 82 341€
 * - 41% de 82 341€ à 177 106€
 * - 45% au-delà de 177 106€
 */
public class CalculateurImpotProgressif {

    // Limites des tranches fiscales en 2024 (revenus 2023)
    private static final int[] LIMITES_TRANCHES = {
        0,         // Borne inférieure tranche 1
        11_294,    // Borne inférieure tranche 2
        28_797,    // Borne inférieure tranche 3
        82_341,    // Borne inférieure tranche 4
        177_106,   // Borne inférieure tranche 5
        Integer.MAX_VALUE  // Borne supérieure (infini)
    };

    // Taux d'imposition pour chaque tranche
    private static final double[] TAUX_TRANCHES = {
        0.00,      // 0% tranche 1
        0.11,      // 11% tranche 2
        0.30,      // 30% tranche 3
        0.41,      // 41% tranche 4
        0.45       // 45% tranche 5
    };

    /**
     * Calcule l'impôt sur un revenu imposable selon le barème progressif.
     * 
     * @param revenuImposable le revenu imposable après abattement et avant quotient familial
     * @return l'impôt calculé avant application du quotient
     */
    public long calculerImpotSurRevenuImposable(double revenuImposable) {
        if (revenuImposable < 0) {
            throw new IllegalArgumentException("Le revenu imposable ne peut pas être négatif: " + revenuImposable);
        }

        long impot = 0;

        // Parcourir les tranches fiscales
        for (int iTranche = 0; iTranche < TAUX_TRANCHES.length; iTranche++) {
            int borneInf = LIMITES_TRANCHES[iTranche];
            int borneSup = LIMITES_TRANCHES[iTranche + 1];
            double taux = TAUX_TRANCHES[iTranche];

            // Vérifier si le revenu est dans cette tranche
            if (revenuImposable >= borneInf && revenuImposable < borneSup) {
                // Dernier intervalle : calcul jusqu'au revenu exact
                impot += (long) ((revenuImposable - borneInf) * taux);
                break;
            } else if (revenuImposable >= borneSup) {
                // Revenu dépasse cette tranche : ajouter l'impôt complet de la tranche
                impot += (long) ((borneSup - borneInf) * taux);
            } else {
                // Revenu inférieur à la borne inférieure
                break;
            }
        }

        return Math.round(impot);
    }

    /**
     * Retourne les limites des tranches fiscales pour documentation/test.
     * 
     * @return tableau des limites des tranches
     */
    public static int[] getLimitesTranches() {
        return LIMITES_TRANCHES;
    }

    /**
     * Retourne les taux d'imposition pour documentation/test.
     * 
     * @return tableau des taux
     */
    public static double[] getTauxTranches() {
        return TAUX_TRANCHES;
    }
}
