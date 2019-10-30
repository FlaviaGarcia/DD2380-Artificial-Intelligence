import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class VectorUtilsTest {

    @Test
    void elementWiseVectorsProduct() {
        double[] vec1 = new double[]{0,1,2,3,5};
        double[] vec2 = new double[]{2,3,4,5,6};

        double[] expected = new double[]{0, 3, 8, 15, 30};
        double[] actual = VectorUtils.elementWiseVectorsProduct(vec1, vec2);
        assertArrayEquals(expected, actual);
    }

    @Test
    void vectorScalarProduct() {
        double[] vec1 = new double[]{0,1,2,3,5};
        double factor = 5;

        double[] expected = new double[]{0, 5, 10, 15, 25};
        double[] actual = VectorUtils.vectorScalarProduct(vec1, factor);
        assertArrayEquals(expected, actual);
    }

    @Test
    void sumVectors() {
        double[] vec1 = new double[]{0,1,2,3,5};
        double[] vec2 = new double[]{2,3,4,5,6};

        double[] expected = new double[]{2, 4, 6, 8, 11};
        double[] actual = VectorUtils.sumVectors(vec1, vec2);
        assertArrayEquals(expected, actual);
    }

    @Test
    void testNormalize() {
        double[] values = new double[] {0.7, 0.8, 1.2, 4};
        double[] normalized = VectorUtils.normalize(values);
        double sum = VectorUtils.sum(normalized);
        assertEquals(1d, sum, 0.0001);
    }

    @Test
    void sum() {
        double[] vec1 = new double[]{0,1,2,3,5};
        double expected = 11;
        double actual = VectorUtils.sum(vec1);
        assertEquals(expected, actual);
    }

    @Test
    void vec2string() {
        double[] vec1 = new double[]{0,1,2,3,5};
        String expected = "0.0 1.0 2.0 3.0 5.0";
        String actual = VectorUtils.vec2string(vec1);
        assertEquals(expected, actual);
    }

    @Test
    void rowStochasticMatrix() {
        int ncols = 7;
        Matrix m = VectorUtils.rowStochasticMatrix(5, ncols, 50);
        for (int row = 0; row < m.getNrows(); row++) {
            double sum = VectorUtils.sum(m.getRowAsArray(row));
            assertEquals(1d, sum, 1d/(100d*ncols));
        }
        System.out.println(m);
    }

    @Test
    void stressTestRowStochasticMatrix() {
        int ncols = 100000;
        Matrix m = VectorUtils.rowStochasticMatrix(5, ncols, 100);
        for (int row = 0; row < m.getNrows(); row++) {
            double sum = VectorUtils.sum(m.getRowAsArray(row));
            assertEquals(1d, sum, 1d/(100d*ncols));
        }
    }

    @Test
    void testDivideVectorBy() {
        double[] vector = new double[] {3, 4, 6, 10, 12};
        double[] expected = new double[] {1.5d, 2, 3, 5, 6};
        VectorUtils.divideVectorBy(vector, 2);
        assertArrayEquals(expected, vector);
    }

    @org.junit.jupiter.api.Test
    void testAverageMatrix() {
        Matrix a = new Matrix(2, 2, new double[] {2,5,6,8});
        Matrix b = new Matrix(2, 2, new double[] {4,10,8,12});
        ArrayList<Matrix> matrices = new ArrayList<>();
        matrices.add(a);
        matrices.add(b);

        Matrix expected = new Matrix(2,2, new double[] {3, 7.5, 7, 10});
        Matrix actual = VectorUtils.averageOf(matrices);

        assertEquals(expected, actual);
    }

    @Test
    void randomFromDistribution() {
        double[] probabilities = new double[] {0.1, 0.5, 0.2, 0.05, 0.15};
        double[] histogram = new double[probabilities.length];

        for(int i = 0; i < 1000000; i++) {
            int r = VectorUtils.randomForDistribution(probabilities);
            histogram[r] ++;
        }
        VectorUtils.normalizeInPlace(histogram);
        System.out.println(VectorUtils.vec2string(histogram));
        assertArrayEquals(probabilities, histogram, 0.01);
    }

    @Test
    void testNormalizeLogLikelyhoods() {
        double[] probabilities = new double[] {0.1, 0.4, 0.15, 0.2, 0.05, 0.1};
        double[] logs = new double[probabilities.length];
        for (int i = 0; i < logs.length; i++)
            logs[i] = Math.log(probabilities[i]);

        double[] actual = VectorUtils.normalizeLogLikelyhoods(logs);
        assertArrayEquals(probabilities, actual, 0.0000001);
    }

}