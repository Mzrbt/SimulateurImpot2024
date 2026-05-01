package com.kerware.simulateurreusine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires pour les calculateurs refactorisés.
 * Testent chaque composant séparément pour augmenter la couverture de code.
 */
@DisplayName("Tests des calculateurs individuels")
public class CalculatorsTest {

    private CalculateurAbattement calcAbt;
    private CalculateurParts calcParts;
    private CalculateurImpotProgressif calcImp;
    private CalculateurPlafondGain calcPlaf;
    private CalculateurDecote calcDecote;

    @BeforeEach
    public void setUp() {
        calcAbt = new CalculateurAbattement();
        calcParts = new CalculateurParts();
        calcImp = new CalculateurImpotProgressif();
        calcPlaf = new CalculateurPlafondGain();
        calcDecote = new CalculateurDecote();
    }

    // ===== CalculateurAbattement =====

    @DisplayName("[EXG_IMPOT_02] Abattement: 10% du revenu")
    @Test
    public void testAbattement10Pourcent() {
        int abt = calcAbt.calculer(50000);
        Assertions.assertEquals(5000, abt);
    }

    @DisplayName("[EXG_IMPOT_02] Abattement min: 495€")
    @Test
    public void testAbattementMin() {
        int abt = calcAbt.calculer(1000);
        Assertions.assertEquals(495, abt);
    }

    @DisplayName("[EXG_IMPOT_02] Abattement max: 14171€")
    @Test
    public void testAbattementMax() {
        int abt = calcAbt.calculer(500000);
        Assertions.assertEquals(14171, abt);
    }

    @DisplayName("[EXG_IMPOT_02] RFR = revenu - abattement")
    @Test
    public void testRFR() {
        int rfr = calcAbt.calculerRevenuFiscalReference(65000);
        Assertions.assertEquals(58500, rfr);
    }

    // ===== CalculateurParts =====

    @DisplayName("[EXG_IMPOT_03] Célibataire: 1 part")
    @Test
    public void testCelibataireParts() {
        double parts = calcParts.calculerNbPartsTotal(
            com.kerware.simulateur.SituationFamiliale.CELIBATAIRE, 0, 0, false);
        Assertions.assertEquals(1.0, parts);
    }

    @DisplayName("[EXG_IMPOT_03] Marié: 2 parts")
    @Test
    public void testMarieParts() {
        double parts = calcParts.calculerNbPartsTotal(
            com.kerware.simulateur.SituationFamiliale.MARIE, 0, 0, false);
        Assertions.assertEquals(2.0, parts);
    }

    @DisplayName("[EXG_IMPOT_03] Marié + 1 enfant: 2.5 parts")
    @Test
    public void testMariePlusEnfant() {
        double parts = calcParts.calculerNbPartsTotal(
            com.kerware.simulateur.SituationFamiliale.MARIE, 1, 0, false);
        Assertions.assertEquals(2.5, parts);
    }

    @DisplayName("[EXG_IMPOT_03] Marié + 2 enfants: 3 parts")
    @Test
    public void testMarieMoreEnfants() {
        double parts = calcParts.calculerNbPartsTotal(
            com.kerware.simulateur.SituationFamiliale.MARIE, 2, 0, false);
        Assertions.assertEquals(3.0, parts);
    }

    @DisplayName("[EXG_IMPOT_03] Marié + 3 enfants: 4 parts")
    @Test
    public void testMarieThreeEnfants() {
        double parts = calcParts.calculerNbPartsTotal(
            com.kerware.simulateur.SituationFamiliale.MARIE, 3, 0, false);
        Assertions.assertEquals(4.0, parts);
    }

    @DisplayName("[EXG_IMPOT_03] Parent isolé + enfant: +0.5 part")
    @Test
    public void testParentIsole() {
        double parts = calcParts.calculerNbPartsTotal(
            com.kerware.simulateur.SituationFamiliale.CELIBATAIRE, 1, 0, true);
        Assertions.assertEquals(2.0, parts);
    }

    @DisplayName("[EXG_IMPOT_03] Enfant handicapé: +0.5 part")
    @Test
    public void testEnfantHandicape() {
        double parts = calcParts.calculerNbPartsTotal(
            com.kerware.simulateur.SituationFamiliale.MARIE, 1, 1, false);
        Assertions.assertEquals(3.0, parts);
    }

    // ===== CalculateurImpotProgressif =====

    @DisplayName("[EXG_IMPOT_04] Barème 0%: revenu < 11294")
    @Test
    public void testBareme0Pourcent() {
        long impot = calcImp.calculerImpotSurRevenuImposable(5000);
        Assertions.assertEquals(0, impot);
    }

    @DisplayName("[EXG_IMPOT_04] Barème 11%: tranche 11294-28797")
    @Test
    public void testBareme11Pourcent() {
        long impot = calcImp.calculerImpotSurRevenuImposable(20000);
        Assertions.assertTrue(impot > 0 && impot < 3000);
    }

    @DisplayName("[EXG_IMPOT_04] Barème progressif à travers tranches")
    @Test
    public void testBaremeProgressif() {
        long impot5k = calcImp.calculerImpotSurRevenuImposable(5000);
        long impot20k = calcImp.calculerImpotSurRevenuImposable(20000);
        long impot50k = calcImp.calculerImpotSurRevenuImposable(50000);

        Assertions.assertTrue(impot5k < impot20k && impot20k < impot50k);
    }

    // ===== CalculateurPlafondGain =====

    @DisplayName("[EXG_IMPOT_05] Pas de demi-parts = pas de plafond")
    @Test
    public void testPlafondZero() {
        long plafond = calcPlaf.calculerPlafondGain(1.0, 1.0);
        Assertions.assertEquals(0, plafond);
    }

    @DisplayName("[EXG_IMPOT_05] 1 demi-part = 1759€ plafond")
    @Test
    public void testPlafondUnDemiPart() {
        long plafond = calcPlaf.calculerPlafondGain(1.0, 1.5);
        Assertions.assertEquals(1759, plafond);
    }

    @DisplayName("[EXG_IMPOT_05] 3 demi-parts = 5277€ plafond")
    @Test
    public void testPlafondTroisDemiParts() {
        long plafond = calcPlaf.calculerPlafondGain(2.0, 3.5);
        Assertions.assertEquals(5277, plafond);
    }

    @DisplayName("[EXG_IMPOT_05] Application du plafond")
    @Test
    public void testApplicationPlafond() {
        long plafond = calcPlaf.calculerPlafondGain(1.0, 1.5);
        long reduApres = calcPlaf.appliquerPlafond(2000, plafond);
        Assertions.assertEquals(1759, reduApres);
    }

    // ===== CalculateurDecote =====

    @DisplayName("[EXG_IMPOT_06] Décote célibataire avec petit impôt")
    @Test
    public void testDecoteCelibataire() {
        long decote = calcDecote.calculerDecote(1000, 1.0);
        Assertions.assertTrue(decote > 0 && decote < 873);
    }

    @DisplayName("[EXG_IMPOT_06] Décote couple avec petit impôt")
    @Test
    public void testDecoteCouple() {
        long decote = calcDecote.calculerDecote(2000, 2.0);
        Assertions.assertTrue(decote > 0 && decote < 1444);
    }

    @DisplayName("[EXG_IMPOT_06] Pas de décote pour impôt élevé")
    @Test
    public void testNoDecoteHighIncome() {
        long decoteCel = calcDecote.calculerDecote(5000, 1.0);
        long decoteCou = calcDecote.calculerDecote(5000, 2.0);
        Assertions.assertEquals(0, decoteCel);
        Assertions.assertEquals(0, decoteCou);
    }
}
