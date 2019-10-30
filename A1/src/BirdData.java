import java.util.ArrayList;

class BirdData {

    // For each specie we have the bird alpha matrix given the specie and observations
    AlphaColumn[] alphaForSpecie = new AlphaColumn[Constants.COUNT_SPECIES];
    ArrayList<Integer> observations = new ArrayList<>(); // Observations for this bird
    private double[] probabilityOfSpecie = new double[Constants.COUNT_SPECIES];

    BirdData(int nStates) {
        for (int i = 0; i < alphaForSpecie.length; i++) {
            alphaForSpecie[i] = new AlphaColumn(nStates);
        }
    }

    void setProbabilityOfSpecie(double[] probabilities) {
        this.probabilityOfSpecie = probabilities;
    }

    double logProbabilityForSpecie(int specie) {
        return probabilityOfSpecie[specie];
    }

    int mostLikelySpecie() {
        return VectorUtils.argmax(probabilityOfSpecie);
    }

    double pNotBS() {
        double[] normalized = VectorUtils.normalizeLogLikelyhoods(probabilityOfSpecie);
        double sum = 0;
        for (int i = 0; i < normalized.length; i++) {
            if (i != Constants.SPECIES_BLACK_STORK)
                sum+= normalized[i];
        }
        return sum;
    }

    static class AlphaColumn {
        double[] alphaColumn;
        ArrayList<Double> scaleFactors = new ArrayList<>();

        AlphaColumn(int nStates) {
            alphaColumn = new double[nStates];
        }

        double logPObservations() {
            double sum = 0;
            for (double f : scaleFactors)
                sum += Math.log(f);
            return sum;
        }

    }
}
