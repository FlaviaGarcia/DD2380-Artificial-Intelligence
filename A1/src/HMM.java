import java.util.ArrayList;

public class HMM {

    public Matrix A;
    public Matrix B;
    private Matrix pi;
    private int[] observations;
    // Forward probability structures
    private ForwardAlgorithm.Alpha alpha;
    // Backward probability structures
    private Matrix beta;
    // Decoding structures
    public Matrix delta;
    public Matrix statesMaxDelta;
    // Learning structures
    public ArrayList<Matrix> digamma;
    public Matrix gamma;

    public HMM(Matrix a, Matrix b, Matrix pi) {
        A = a;
        B = b;
        this.pi = pi;
    }

    public HMM(Matrix a, Matrix b, Matrix pi, int[] observations) {
        this.A = a;
        this.B = b;
        this.pi = pi;
        this.observations = observations;
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

    public void setObservations(int[] observations) { this.observations = observations; }

    public int nStates() {
        return A.getNrows();
    }
    // --- end getters and setters ---

    /**
     * Returns the most likely state sequence from the observations
     *
     * @return array of states
     */
    public int[] decode() {
        int nStates = A.getNrows();
        int nObservations = observations.length;
        delta = new Matrix(nStates, nObservations);
        statesMaxDelta = new Matrix(nStates, nObservations);
        int[] stateSequence = new int[observations.length];

        // Initialize first column of sigma
        for (int state = 0; state < delta.getNrows(); state++) {
            delta.set(state, 0, Math.log(pi.get(0, state) * B.get(state, observations[0])));
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
            double partialProbability = Math.log(A.get(j, state)) + delta.get(j, time - 1) + Math.log(B.get(state, observations[time]));
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

    public void learnFromObservations(int maxIterations, double threshold) {
        double lastLogProb = 0;
        for (int i = 0; i < maxIterations; i++) {
            alpha = ForwardAlgorithm.calculateAlphaPerT(A, B, pi, observations, true);
            beta = BackwardAlgorithm.calculateBetaPerT(A, B, observations, alpha.scaleFactors);
            digamma = computeDigammaMatrix();
            gamma = computeGammaMatrix();
            A = updateA();
            B = updateB();
            pi = new Matrix(pi.getNrows(), pi.getNcols(), gamma.getColumn(0));
            double newLogProb = logProbability(alpha.scaleFactors);
            double difference = Math.abs(newLogProb - lastLogProb);
            if (difference < threshold) {
                break;
            }
            lastLogProb = newLogProb;
        }
    }

    /**
     * Initializes the array of digamma matrices.
     * @return array of Matrix in which the i-th matrix is digamma at time i
     */
    private ArrayList<Matrix> computeDigammaMatrix() {
        ArrayList<Matrix> digamma = new ArrayList<>(observations.length - 1);
        for (int time = 0; time < observations.length - 1; time++)
            digamma.add(computeDigammaMatrixPerT(time));
        return digamma;
    }

    private Matrix computeDigammaMatrixPerT(int time) {
        Matrix digammaT = new Matrix(nStates(), nStates());
        for (int stateFrom = 0; stateFrom < nStates(); stateFrom++) {
            for (int stateTo = 0; stateTo < nStates(); stateTo++) {
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
        double numerator = alpha.alphaMatrix.get(stateFrom, time) * A.get(stateFrom, stateTo) * B.get(stateTo, observations[time + 1]) * beta.get(stateTo, time + 1);
        return numerator;
    }

    private Matrix computeGammaMatrix() {
        Matrix res = new Matrix(nStates(), observations.length - 1);
        for (int time = 0; time < observations.length - 1; time++) {
            for (int state = 0; state < nStates(); state++) {
                double gammaCell = gammaNumerator(time, state);
                res.set(state, time, gammaCell);
            }
        }
        return res;
    }

    private double gammaNumerator(int time, int state) {
        double res = 0;
        for (int i = 0; i < nStates(); i++) {
            res += digamma.get(time).get(state, i);
        }
        return res;
    }

    private Matrix updateA() {
        Matrix newA = new Matrix(nStates(), nStates());
        for (int i = 0; i < nStates(); i++) {
            for (int j = 0; j < nStates(); j++) {
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

        for (int time = 0; time < observations.length - 1; time++) {
            upperSum += digamma.get(time).get(stateFrom, stateTo);
            lowerSum += gamma.get(stateFrom, time);
        }

//        System.out.println("A("+stateFrom+","+stateTo+")"+" = "+upperSum+"/"+lowerSum+" = "+(upperSum/lowerSum));
        return upperSum / lowerSum;
    }

    private Matrix updateB() {
        Matrix newB = new Matrix(nStates(), B.getNcols());
        for (int state = 0; state < nStates(); state++) {
            for (int emission = 0; emission < B.getNcols(); emission++) {
                double newValue = newBat(state, emission);
                newB.set(state, emission, newValue);
            }
        }
        return newB;
    }

    private double newBat(int state, int emission) {
        double upperSum = 0;
        double lowerSum = 0;

        for (int time = 0; time < observations.length - 1; time++) {
            if (observations[time] == emission)
                upperSum += gamma.get(state, time);
            lowerSum += gamma.get(state, time);
        }
        return upperSum / lowerSum;
    }

    public double logProbability(double[] scaleFactors) {
        double sum = 0;
        for (double factor : scaleFactors) {
            sum+=-Math.log(factor);
        }
        return sum;
    }

}
