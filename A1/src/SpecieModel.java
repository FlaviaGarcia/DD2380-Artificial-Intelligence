import java.util.ArrayList;

/**
 * This class represents a specie. Contains an HMM that will be trained from observations,
 * and an array of all observations
 */
public class SpecieModel {
    public static final int N_STATES_CLASSIFICATION = 1;
    public static final int N_EMISSIONS_CLASSIFICATION = 9;

    private final int MAX_LEARNING_ITERATIONS = 100;
    private final double LEARNING_PRECISION = 0.0001;
    private final int UNIFORM_PRECISION = 100;


    private ArrayList<Integer> observations;
    public HMM specieModel;
    private int birdsObserved = 0;

    SpecieModel() {
        observations = new ArrayList<>(500);
    }

    void addObservations(ArrayList<Integer> observations) {
        this.observations.addAll(observations);
    }

    int getNobservations() {
        return observations.size();
    }

    public void learn() {
        specieModel = new HMM(N_STATES_CLASSIFICATION, N_EMISSIONS_CLASSIFICATION, UNIFORM_PRECISION);
        specieModel.setObservations(observations);
        specieModel.computeAlpha(true);
        specieModel.computeBeta();
        specieModel.learnFromObservations(MAX_LEARNING_ITERATIONS, LEARNING_PRECISION);
    }

    /**
     * Returns the likelyhood that the set of observations comes from this specie,
     * i.e. P(observations | this specie)
     *
     * @param observations array of >1 observations
     * @return P(observations | this specie)
     */
    double logLikelyhoodOf(ArrayList<Integer> observations) {
        if (this.getNobservations() <= 1)
            return Double.NEGATIVE_INFINITY;
        else
            return specieModel.logProbabilityObservations(observations);
    }

    void increaseObservedBirds() {
        birdsObserved++;
    }

    int getNumberObservedBirds() {
        return birdsObserved;
    }
}
