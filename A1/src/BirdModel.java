import java.util.ArrayList;

public class BirdModel {
    private final int N_STATES_SHOOTING = 2;
    private final int N_EMISSIONS_SHOOTING = Constants.COUNT_MOVE;
    private final int MAX_LEARNING_ITERATIONS = 40;
    private final double LEARNING_PRECISION = 0.0001;
    private final int UNIFORM_PRECISION = 1000000;

    private ArrayList<Integer> observations;
    public HMM birdModel;
    double[] probabilityOfSpecie = new double[Constants.COUNT_SPECIES];

    public BirdModel() {
        birdModel = new HMM(N_STATES_SHOOTING, N_EMISSIONS_SHOOTING, UNIFORM_PRECISION);
        observations = new ArrayList<>(100);
    }

    void addObservation(int o) {
        if (o >= 0 && o < Constants.COUNT_MOVE)
            observations.add(o);
    }

    ArrayList<Integer> getObservations() {
        return observations;
    }

    void learn() {
        birdModel = new HMM(N_STATES_SHOOTING, N_EMISSIONS_SHOOTING, UNIFORM_PRECISION);
        birdModel.setObservations(observations);
        birdModel.computeAlpha(true);
        birdModel.computeBeta();
        birdModel.learnFromObservations(MAX_LEARNING_ITERATIONS, LEARNING_PRECISION);
    }

    int mostLikelySpecies() {
        return VectorUtils.argmax(probabilityOfSpecie);
    }

    double pNotBS() {
        double sum = 0;
        for (int i = 0; i < probabilityOfSpecie.length; i++) {
            if (i != Constants.SPECIES_BLACK_STORK)
                sum += i;
        }
        return sum;
    }

    /**
     * @return
     */
    Move mostLikelyNextMove() {
        double maxProbability = Double.NEGATIVE_INFINITY;
        int bestMove = Constants.MOVE_DEAD;

        for (int move = 0; move < Constants.COUNT_MOVE; move++) {
            double pMove = probabilityOfMove(move);
            if (pMove > maxProbability) {
                maxProbability = pMove;
                bestMove = move;
            }
        }
        return new Move(bestMove, maxProbability);
    }

    /**
     * @param move
     * @return
     */
    private double probabilityOfMove(int move) {
        double ksum = 0;
        for (int k = 0; k < N_STATES_SHOOTING; k++) {
            double hsum = 0;
            for (int h = 0; h < N_STATES_SHOOTING; h++) {
                hsum += birdModel.getA().get(h, k) * birdModel.alpha.alphaMatrix.getLastColumn()[h];
            }
            ksum += birdModel.getB().get(k, move) * hsum;
        }
        return ksum;
    }

    static class Move{
        int move;
        double probability;

        public Move(int move, double probability) {
            this.move = move;
            this.probability = probability;
        }
    }

}
