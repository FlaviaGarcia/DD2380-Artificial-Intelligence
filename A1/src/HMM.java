import java.util.ArrayList;

public class HMM {

    public Matrix A;
    public Matrix B;
    private Matrix pi;
    public ArrayList<Integer> observations = new ArrayList<>();
    // Forward probability structures
    public ForwardAlgorithm.Alpha alpha;
    // Backward probability structures
    public Matrix beta;
    // Decoding structures
    public Matrix delta;
    public Matrix statesMaxDelta;
    // Learning structures
    public ArrayList<Matrix> digamma;
    public Matrix gamma;

    public HMM(Matrix a, Matrix b, Matrix pi) {
        this.A = a;
        this.B = b;
        this.pi = pi;
    }

    public HMM(Matrix a, Matrix b, Matrix pi, ArrayList<Integer> observations) {
        this.A = a;
        this.B = b;
        this.pi = pi;
        this.observations = observations;
    }

    public HMM(int nstates, int nemissions, int precision) {
        this.A = VectorUtils.rowStochasticMatrix(nstates, nstates, precision);
        this.B = VectorUtils.rowStochasticMatrix(nstates, nemissions, precision);
        this.pi = VectorUtils.rowStochasticMatrix(1, nstates, precision);
    }

    // --- start getters and setters ---
    public Matrix getA() {
        return A;
    }

    public void setA(Matrix a) {
        A = a;
    }

    public Matrix getB() {
        return B;
    }

    public void setB(Matrix b) {
        B = b;
    }

    public Matrix getPi() {
        return pi;
    }

    public void setPi(Matrix pi) {
        this.pi = pi;
    }

    public int numberOfStates() {
        return A.getNrows();
    }

    public int observationsLength() { return observations.size(); }

    public void setObservations(ArrayList<Integer> observations) {
        this.observations = observations;
    }

    public void computeAlpha(boolean normalize) {
        this.alpha = ForwardAlgorithm.forward(A, B, pi, observations,true);
    }

    public void computeBeta() {
        this.beta = BackwardAlgorithm.calculateBetaPerT(A, B, observations, alpha.scaleFactors);
    }

    /**
     * Returns the most likely state sequence from the observations
     *
     * @return array of states
     */
    public int[] decode() {
        int nStates = A.getNrows();
        int nObservations = observations.size();
        delta = new Matrix(nStates, nObservations);
        statesMaxDelta = new Matrix(nStates, nObservations);
        int[] stateSequence = new int[observations.size()];

        // Initialize first column of sigma
        for (int state = 0; state < delta.getNrows(); state++) {
            delta.set(state, 0, Math.log(pi.get(0, state) * B.get(state, observations.get(0))));
        }

        // Computing delta and statesMaxDelta from col 1 to end
        for (int time = 1; time < nObservations; time++) {
            for (int state = 0; state < nStates; state++) {
                Argmax partialMax = argmaxDeltaT(state, time, nStates);
                delta.set(state, time, partialMax.maxValue);
                statesMaxDelta.set(state, time, partialMax.maxIdx);
            }
        }

        // The state that maximize the last column of sigma is the (most likely) final state
        Argmax lastStateMax = argmaxColumn(delta, nObservations - 1);
        stateSequence[nObservations - 1] = lastStateMax.maxIdx;

        // We fill the rest of the sequence by backtracking values of sigmaIdx
        for (int t = nObservations - 1; t > 0; t--) {
            stateSequence[t - 1] = (int) statesMaxDelta.get(stateSequence[t], t);
        }

        return stateSequence;
    }

    /**
     * Represents the result of an argmax operation.
     * Contains the maximized value and its index.
     */
    private static class Argmax {
        int maxIdx = 0;
        double maxValue = Double.NEGATIVE_INFINITY;
    }

    /**
     * Calculates sigma_t(state), returns its value and the index that maximized it
     *
     * @param state   state for which sigma must be computed
     * @param time    time step for which sigma must be computed
     * @param nstates total number of states
     * @return object containing the maximized value of sigma_t(state) and the state index that maximized it
     */
    private Argmax argmaxDeltaT(int state, int time, int nstates) {
        Argmax res = new Argmax();
        for (int j = 0; j < nstates; j++) {
            double partialProbability = Math.log(A.get(j, state)) + delta.get(j, time - 1) + Math.log(B.get(state, observations.get(time)));
            if (partialProbability > res.maxValue) {
                res.maxValue = partialProbability;
                res.maxIdx = j;
            }
        }
        return res;
    }

    /**
     * Return maximum value and its index for column col of the given matrix
     *
     * @param matrix a non null Matrix
     * @param col    0 <= col < matrix.getNcols()
     * @return Argmax object containing maximum value of the column and its index
     */
    private Argmax argmaxColumn(Matrix matrix, int col) {
        Argmax res = new Argmax();
        for (int row = 0; row < matrix.getNrows(); row++) {
            if (matrix.get(row, col) > res.maxValue) {
                res.maxValue = matrix.get(row, col);
                res.maxIdx = row;
            }
        }
        return res;
    }

    public int learnFromObservations(int maxIterations, double threshold) {
        double lastLogProb = 0;
        int iterations = 0;
        for (int i = 0; i < maxIterations; i++) {
            if (alpha == null || alpha.alphaMatrix.getNcols() < observations.size())
                alpha = ForwardAlgorithm.forward(A, B, pi, observations, true);
            if (beta == null || beta.getNcols() < observations.size())
                beta = BackwardAlgorithm.calculateBetaPerT(A, B, observations, alpha.scaleFactors);
            digamma = computeDigammaMatrices();
            gamma = computeGammaMatrix();
            A = updateA();
            B = updateB();
            pi = new Matrix(pi.getNrows(), pi.getNcols(), gamma.getColumn(0));
            double newLogProb = logProbabilityObservations();
            double difference = Math.abs(Math.abs(newLogProb) - Math.abs(lastLogProb));
            if (difference < threshold) {
                break;
            }
            lastLogProb = newLogProb;
            iterations++;
            alpha = ForwardAlgorithm.forward(A, B, pi, observations, true);
            beta = BackwardAlgorithm.calculateBetaPerT(A, B, observations, alpha.scaleFactors);
        }
        return iterations;
    }

    /**
     * Initializes the array of digamma matrices.
     * @return array of Matrix in which the i-th matrix is digamma at time i
     */
    private ArrayList<Matrix> computeDigammaMatrices() {
        ArrayList<Matrix> digamma = new ArrayList<>(observations.size() - 1);
        for (int time = 0; time < observations.size() - 1; time++)
            digamma.add(computeDigammaMatrix(time));
        return digamma;
    }

    private Matrix computeDigammaMatrix(int time) {
        Matrix digammaT = new Matrix(numberOfStates(), numberOfStates());
        for (int stateFrom = 0; stateFrom < numberOfStates(); stateFrom++) {
            for (int stateTo = 0; stateTo < numberOfStates(); stateTo++) {
                double digammaNum = diGammaNumerator(stateFrom, stateTo, time);
                digammaT.set(stateFrom, stateTo, digammaNum);
            }
        }
        return digammaT;
    }

    /**
     * Computes the numerator of the diGamma function, that represents the probability of going from a given state at
     * time t to the other given state at time t+1, given the observations
     *
     * @param time
     * @param stateFrom
     * @param stateTo
     * @return numerator of digamma_t(stateFrom, stateTo)
     */
    private double diGammaNumerator(int stateFrom, int stateTo, int time) {
        double alphaVal = alpha.alphaMatrix.get(stateFrom, time);
        double aVal = A.get(stateFrom, stateTo);
        double bVal = B.get(stateTo, observations.get(time + 1));
        double betaVal = beta.get(stateTo, time+1);
        return alphaVal * aVal * bVal * betaVal;
    }

    private Matrix computeGammaMatrix() {
        Matrix res = new Matrix(numberOfStates(), observations.size() - 1);
        for (int time = 0; time < observations.size() - 1; time++) {
            for (int state = 0; state < numberOfStates(); state++) {
                double gammaCell = gammaNumerator(time, state);
                res.set(state, time, gammaCell);
            }
        }
        return res;
    }

    private double gammaNumerator(int time, int state) {
        double res = 0;
        for (int i = 0; i < numberOfStates(); i++) {
            res += digamma.get(time).get(state, i);
        }
        return res;
    }

    private Matrix updateA() {
        Matrix newA = new Matrix(numberOfStates(), numberOfStates());
        for (int i = 0; i < numberOfStates(); i++) {
            for (int j = 0; j < numberOfStates(); j++) {
                double newValue = newAat(i, j);
                newA.set(i, j, newValue);
            }
        }
        return newA;
    }

    private double newAat(int stateFrom, int stateTo) {
        // Computing sum over t of digamma_t(stateFrom, stateTo)
        double upperSum = 0;
        double lowerSum = 0;

        for (int time = 0; time < observations.size() - 1; time++) {
            upperSum += digamma.get(time).get(stateFrom, stateTo);
            lowerSum += gamma.get(stateFrom, time);
        }
        return upperSum / lowerSum;
    }

    private Matrix updateB() {
        Matrix newB = new Matrix(numberOfStates(), B.getNcols());
        boolean[] normalizeRow = new boolean[B.getNrows()];
        for (int state = 0; state < numberOfStates(); state++) {
            for (int emission = 0; emission < B.getNcols(); emission++) {
                double upperSum = 0;
                double lowerSum = 0;
                double res;
                for (int time = 0; time < observations.size() - 1; time++) {
                    if (observations.get(time) == emission)
                        upperSum += gamma.get(state, time);
                    lowerSum += gamma.get(state, time);
                }
                if (upperSum == 0) {
                    upperSum += Double.MIN_VALUE * 1000;
                    normalizeRow[state] = true;
                }
                res = upperSum / lowerSum;
                newB.set(state, emission, res);
            }
        }

        for (int row = 0; row < newB.getNrows(); row++) {
            if (normalizeRow[row])
                newB.normalizeRow(row);
        }
        return newB;
    }

    public double logProbabilityObservations() {
        return alpha.logPObservations();
    }

    /**
     * Probability of the observed sequence. Be careful: can underflow in few hundreds of observations
     * @return P(observations)
     */
    public double probabilityObservations() {
        return Math.exp(logProbabilityObservations());
    }

    public double logProbabilityObservations(ArrayList<Integer> observations) {
        ForwardAlgorithm.Alpha alpha = ForwardAlgorithm.forward(A, B, pi, observations, true);
        return alpha.logPObservations();
    }

    ArrayList<Integer> generateObservations(int n) {
        ArrayList<Integer> result = new ArrayList<>();
        int state = VectorUtils.randomForDistribution(pi.matrixToArray());
        for (int i = 0; i < n; i++) {
            int observation = VectorUtils.randomForDistribution(B.getRowAsArray(state));
            result.add(observation);
            state = VectorUtils.randomForDistribution(A.getRowAsArray(state));
        }
        return result;
    }

}
