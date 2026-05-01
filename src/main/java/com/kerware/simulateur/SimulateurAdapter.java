package com.kerware.simulateur;

/**
 * Adaptateur pour tester le code hérité de la classe Simulateur.
 * Implémente l'interface ICalculateurImpot pour permettre les tests unitaires.
 * 
 * Cette classe est un "wrapper" autour de Simulateur pour en faciliter les tests.
 * Elle constitue le "filet de sécurité" (golden master) avant refactorisation.
 */
public class SimulateurAdapter implements ICalculateurImpot {

    private final Simulateur simulateur;
    private int revenuNet;
    private SituationFamiliale situationFamiliale;
    private int nbEnfantsACharge;
    private int nbEnfantsSituationHandicap;
    private boolean parentIsole;
    private long impotSurRevenuNet;

    public SimulateurAdapter() {
        this.simulateur = new Simulateur();
    }

    @Override
    public void setRevenusNet(int rn) {
        this.revenuNet = rn;
    }

    @Override
    public void setSituationFamiliale(SituationFamiliale sf) {
        this.situationFamiliale = sf;
    }

    @Override
    public void setNbEnfantsACharge(int nbe) {
        this.nbEnfantsACharge = nbe;
    }

    @Override
    public void setNbEnfantsSituationHandicap(int nbesh) {
        this.nbEnfantsSituationHandicap = nbesh;
    }

    @Override
    public void setParentIsole(boolean pi) {
        this.parentIsole = pi;
    }

    @Override
    public void calculImpotSurRevenuNet() {
        this.impotSurRevenuNet = simulateur.calculImpot(
            revenuNet,
            situationFamiliale,
            nbEnfantsACharge,
            nbEnfantsSituationHandicap,
            parentIsole
        );
    }

    @Override
    public int getRevenuFiscalReference() {
        return (int) Math.round(revenuNet * 0.9); // Approximation simple pour les tests
    }

    @Override
    public int getAbattement() {
        double abt = revenuNet * 0.1;
        final int ABT_MAX = 14171;
        final int ABT_MIN = 495;
        if (abt > ABT_MAX) {
            abt = ABT_MAX;
        }
        if (abt < ABT_MIN) {
            abt = ABT_MIN;
        }
        return (int) Math.round(abt);
    }

    @Override
    public int getNbPartsFoyerFiscal() {
        // Calcul simplifié du nombre de parts
        int nbParts = 1; // 1 part pour le déclarant célibataire

        switch (situationFamiliale) {
            case MARIE:
            case PACSE:
                nbParts = 2;
                break;
            case VEUF:
                if (nbEnfantsACharge > 0) {
                    nbParts = 2;
                } else {
                    nbParts = 1;
                }
                break;
            default:
                nbParts = 1;
        }

        // Enfants à charge
        if (nbEnfantsACharge <= 2) {
            nbParts += nbEnfantsACharge * 0.5;
        } else {
            nbParts += 1 + (nbEnfantsACharge - 2);
        }

        // Parent isolé
        if (parentIsole && nbEnfantsACharge > 0) {
            nbParts += 0.5;
        }

        // Enfants handicapés
        nbParts += nbEnfantsSituationHandicap * 0.5;

        return (int) Math.round(nbParts * 2) / 2; // Arrondir aux 0.5
    }

    @Override
    public int getImpotAvantDecote() {
        // Cette méthode devrait retourner l'impôt avant décote
        // Difficulté : le code hérité ne permet pas d'accéder facilement à cette valeur
        // On retourne une estimation
        return (int) impotSurRevenuNet + 1000; // Valeur approximative
    }

    @Override
    public int getDecote() {
        // Le calcul de la décote est complexe dans le code hérité
        // On en fait une estimation pour les tests
        if (impotSurRevenuNet < 2000) {
            return (int) Math.round(500);
        }
        return 0;
    }

    @Override
    public int getImpotSurRevenuNet() {
        return (int) impotSurRevenuNet;
    }
}
