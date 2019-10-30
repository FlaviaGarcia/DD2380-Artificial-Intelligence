
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HMMTest {

    @Test
    void testProbEmissionSequence() {
        Matrix A = new Matrix(4,4, new double[] {0.0, 0.8, 0.1, 0.1, 0.1, 0.0 ,0.8, 0.1, 0.1 ,0.1, 0.0, 0.8, 0.8, 0.1, 0.1, 0.0});
        Matrix B = new Matrix(4, 4, new double[] {0.9, 0.1, 0.0, 0.0, 0.0, 0.9, 0.1, 0.0, 0.0, 0.0, 0.9, 0.1, 0.1, 0.0, 0.0, 0.9});
        Matrix pi = new Matrix(1, 4, new double[] {1, 0, 0, 0});
        ArrayList<Integer> observations= new ArrayList<Integer>(Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3}));
        double expectedProbability = 0.090276d;

        HMM hmm = new HMM(A, B, pi);
        hmm.setObservations(observations);
        hmm.computeAlpha(true);
        hmm.computeBeta();
        double actual = hmm.probabilityObservations();
        assertEquals(expectedProbability, actual, 0.001d);
    }

    @Test
    void testLogProbEmissionSequence() {
        Matrix A = new Matrix(4,4, new double[] {0.0, 0.8, 0.1, 0.1, 0.1, 0.0 ,0.8, 0.1, 0.1 ,0.1, 0.0, 0.8, 0.8, 0.1, 0.1, 0.0});
        Matrix B = new Matrix(4, 4, new double[] {0.9, 0.1, 0.0, 0.0, 0.0, 0.9, 0.1, 0.0, 0.0, 0.0, 0.9, 0.1, 0.1, 0.0, 0.0, 0.9});
        Matrix pi = new Matrix(1, 4, new double[] {1, 0, 0, 0});
        ArrayList<Integer> observations= new ArrayList<Integer>(Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3}));
        double expectedProbability = Math.log(0.090276);

        HMM hmm = new HMM(A, B, pi);
        hmm.setObservations(observations);
        hmm.computeAlpha(true);
        hmm.computeBeta();
        double actual = hmm.logProbabilityObservations();
        assertEquals(expectedProbability, actual, 0.001d);
    }
}