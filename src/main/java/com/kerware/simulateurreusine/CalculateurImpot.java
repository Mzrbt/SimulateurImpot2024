package com.kerware.simulateurreusine;

import com.kerware.simulateur.ICalculateurImpot;
import com.kerware.simulateur.SituationFamiliale;

/**
 * Calculateur réusiné de l'impôt sur le revenu.
 * 
 * Cette classe implémente l'interface ICalculateurImpot avec une architecture
 * refactorisée, modulaire et de haute qualité. Elle orchestre les calculs
 * via des classes spécialisées :
 * 
 * 1. CalculateurAbattement : calcul de l'abattement fiscal
 * 2. CalculateurParts : détermination du nombre de parts fiscales
 * 3. CalculateurImpotProgressif : application du barème progressif
 * 4. CalculateurPlafondGain : application du plafond de gain
 * 5. CalculateurDecote : application de la décote pour bas revenus
 */
public class CalculateurImpot implements ICalculateurImpot {

    // Données du foyer fiscal
    private int revenuNet;
    private SituationFamiliale situationFamiliale;
    private int nbEnfantsACharge;
    private int nbEnfantsSituationHandicap;
    private boolean parentIsole;

    // Résultats intermédiaires
    private int abattement;
    private int revenuFiscalReference;
    private double nbPartsDeclarant;
    private double nbPartsTotal;
    private long impotDeclarant;
    private long impotFoyerSansDecote;
    private long decote;
    private long impotFinal;

    // Calculateurs spécialisés
    private final CalculateurAbattement calcAbattement = new CalculateurAbattement();
    private final CalculateurParts calcParts = new CalculateurParts();
    private final CalculateurImpotProgressif calcImpotProgressif = new CalculateurImpotProgressif();
    private final CalculateurPlafondGain calcPlafond = new CalculateurPlafondGain();
    private final CalculateurDecote calcDecote = new CalculateurDecote();

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

    /**
     * Effectue le calcul complet de l'impôt sur le revenu.
     * 
     * Décompose le calcul en étapes claires :
     * 1. Calcul de l'abattement (EXG_IMPOT_02)
     * 2. Calcul du revenu fiscal de référence
     * 3. Calcul du nombre de parts fiscales (EXG_IMPOT_03)
     * 4. Calcul de l'impôt par quotient familial (EXG_IMPOT_04)
     * 5. Application du plafond de gain (EXG_IMPOT_05)
     * 6. Application de la décote (EXG_IMPOT_06)
     */
    @Override
    public void calculImpotSurRevenuNet() {
        // Étape 1 : Calcul de l'abattement
        this.abattement = calcAbattement.calculer(revenuNet);
        
        // Étape 2 : Calcul du revenu fiscal de référence
        this.revenuFiscalReference = calcAbattement.calculerRevenuFiscalReference(revenuNet);

        // Étape 3 : Calcul du nombre de parts fiscales
        this.nbPartsDeclarant = calcParts.calculerNbPartsDeclarant(situationFamiliale, nbEnfantsACharge);
        this.nbPartsTotal = calcParts.calculerNbPartsTotal(
            situationFamiliale,
            nbEnfantsACharge,
            nbEnfantsSituationHandicap,
            parentIsole
        );

        // Étape 4a : Calcul de l'impôt pour le déclarant seul
        double revenuImposableDeclarant = (double) revenuFiscalReference / nbPartsDeclarant;
        this.impotDeclarant = calcImpotProgressif.calculerImpotSurRevenuImposable(revenuImposableDeclarant);
        this.impotDeclarant = Math.round(impotDeclarant * nbPartsDeclarant);

        // Étape 4b : Calcul de l'impôt pour le foyer complet
        double revenuImposableFoyer = (double) revenuFiscalReference / nbPartsTotal;
        this.impotFoyerSansDecote = calcImpotProgressif.calculerImpotSurRevenuImposable(revenuImposableFoyer);
        this.impotFoyerSansDecote = Math.round(impotFoyerSansDecote * nbPartsTotal);

        // Étape 5 : Application du plafond de gain
        long baisseImpot = impotDeclarant - impotFoyerSansDecote;
        long plafondGain = calcPlafond.calculerPlafondGain(nbPartsDeclarant, nbPartsTotal);

        if (baisseImpot >= plafondGain) {
            // La baisse est plafondée : impôt = impôt déclarant - plafond
            this.impotFoyerSansDecote = impotDeclarant - plafondGain;
        }
        // Sinon on garde impotFoyerSansDecote tel quel

        // Étape 6 : Application de la décote pour bas revenus
        this.decote = calcDecote.calculerDecote(impotFoyerSansDecote, nbPartsDeclarant);
        this.impotFinal = impotFoyerSansDecote - decote;

        // S'assurer que l'impôt n'est pas négatif
        if (this.impotFinal < 0) {
            this.impotFinal = 0;
        }
    }

    @Override
    public int getRevenuFiscalReference() {
        return revenuFiscalReference;
    }

    @Override
    public int getAbattement() {
        return abattement;
    }

    @Override
    public int getNbPartsFoyerFiscal() {
        return (int) Math.round(nbPartsTotal * 2) / 2;
    }

    @Override
    public int getImpotAvantDecote() {
        return (int) impotFoyerSansDecote;
    }

    @Override
    public int getDecote() {
        return (int) decote;
    }

    @Override
    public int getImpotSurRevenuNet() {
        return (int) impotFinal;
    }
}
