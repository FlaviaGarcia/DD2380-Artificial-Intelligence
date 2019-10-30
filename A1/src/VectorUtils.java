import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class VectorUtils {
    public static ArrayList<Integer> readVectorObservationsFromLine(Scanner scanner) {
        String tokens[] = scanner.nextLine().split(" ");
        int size = Integer.parseInt(tokens[0]);
        ArrayList<Integer> observationsArray = new ArrayList<>(size);
        for (int i = 0; i <size; i++) {
            observationsArray.add(Integer.parseInt(tokens[1 + i]));
        }
        return observationsArray;
    }

    public static double[] elementWiseVectorsProduct(double[] vectorA, double[] vectorB) {
        if (vectorA.length == vectorB.length) {
            int resultVectorLen = vectorA.length;
            double[] resultVector = new double[resultVectorLen];
            for (int i = 0; i < resultVectorLen; i++) {
                resultVector[i] = vectorA[i] * vectorB[i];
            }
            return resultVector;
        } else {
            throw new IllegalArgumentException("The arrays do not have the same length, therefore it is not possible to compute the element-wise product");
        }

    }

    public static double[] vectorScalarProduct(double[] vector, double number) {
        double[] result = new double[vector.length];
        for (int index = 0; index < vector.length; index++) {
            result[index] = vector[index] * number;
        }
        return result;
    }

    public static double[] sumVectors(double[] vectorA, double[] vectorB) {
        if (vectorA.length == vectorB.length) {
            double[] result = new double[vectorA.length];
            for (int index = 0; index < vectorA.length; index++) {
                result[index] = vectorA[index] + vectorB[index];
            }
            return result;
        } else {
            throw new IllegalArgumentException("It is not possible to sum vectors with different lengths");
        }
    }

    /**
     * Normalize an array by dividing each element by the sum of all elements in the array
     * @param array
     */
    public static double[] normalize(double[] array) {
        double[] normalized = new double[array.length];

        double sum = sum(array);

        for (int i = 0; i < array.length; i++) {
            normalized[i] = array[i] / sum;
        }
        return normalized;
    }

    public static void normalizeInPlace(double[] array) {
        double sum = 0;
        for (double v : array)
            sum += v;
        for (int i = 0; i < array.length; i++)
            array[i] /= sum;
    }

    public static double sum(double[] array) {
        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static String vec2string(double[] vector) {
        String s = "";
        for (double v : vector) {
            s += v+" ";
        }
        return s.substring(0, s.length()-1);
    }

    public static void divideVectorBy(double[] vector, double value) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / value;
        }
    }

    /**
     * Creates an almost uniform row-stochastic matrix with a normally distributed error.
     * The error mean is 0, the standard deviation is given by 1/(precision * ncols). Rows values sum to 1.
     * @param nrows
     * @param ncols
     * @param precision error stdev =  1/(precision * ncols). The higher precision, the closer values will be to 1/ncols
     * @return
     */
    public static Matrix rowStochasticMatrix(int nrows, int ncols, double precision) {
        Matrix m = new Matrix(nrows, ncols, 1d/ncols);

        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col ++) {
                Random r = new Random();
                m.sum(row, col, r.nextGaussian()/(precision * ncols));
            }
            double normFactor = VectorUtils.sum(m.getRowAsArray(row));
            m.divideRowBy(normFactor, row);
        }
        return m;
    }

    public static ArrayList<Integer> randomObservationsArray(int nemissions, int nobservations) {
        ArrayList<Integer> observations = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < nobservations; i++) {
            observations.add(r.nextInt(nemissions));
        }
        return observations;
    }

    public static Matrix averageOf(ArrayList<Matrix> matrices) {
        int nrows = matrices.get(0).getNrows();
        int ncols = matrices.get(0).getNcols();

        for (Matrix m : matrices)
            if (m.getNrows() != nrows || m.getNcols() != ncols)
                throw new ArrayIndexOutOfBoundsException("Matrices do not have the same size");

        Matrix average = new Matrix(matrices.get(0).getNrows(), matrices.get(0).getNcols());
        for (Matrix m : matrices) {
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col ++) {
                    average.sum(row, col, m.get(row, col));
                }
            }
        }

        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                average.set(row, col, average.get(row, col) / (double)matrices.size());
            }
        }
        return average;
    }

    public static double max(double[] values) {
        double max = Double.NEGATIVE_INFINITY;
        for (double value : values) {
            if (value > max)
                max = value;
        }
        return max;
    }

    //https://stats.stackexchange.com/questions/66616/converting-normalizing-very-small-likelihood-values-to-probability

    /**
     * Takes as input an array of log probabilities and returns an array of the correspondent probabilities normalized.
     * @param logProbabilities each value is log(pi)
     * @return array of pi normalized
     */
    public static double[] normalizeLogLikelyhoods(double[] logProbabilities) {
        double epsilon = Math.pow(10, -15);
        double[] res = new double[logProbabilities.length];
        double maxLog = VectorUtils.max(logProbabilities);
        for (int i = 0; i < res.length; i++) {
            res[i] = logProbabilities[i] - maxLog;
            if (res[i] > Math.log(epsilon) - Math.log(logProbabilities.length))
                res[i] = Math.exp(res[i]);
            else
                res[i] = 0;
        }

        double sum = 0;
        double[] sortedRes = new double[res.length];
        System.arraycopy(res, 0, sortedRes, 0, res.length);
        Arrays.sort(sortedRes);
        for (double v : sortedRes)
            sum+=v;
        for (int i = 0; i < res.length; i++)
            res[i] /= sum;
        return res;
    }

    static int randomForDistribution(double[] distribution) {
        double[] sigmaPoints = new double[distribution.length];
        sigmaPoints[0] = distribution[0];
        for (int i = 1; i < distribution.length; i++)
            sigmaPoints[i] = sigmaPoints[i-1] + distribution[i];

        double random = Math.random();
        for (int i = 0; i < distribution.length; i++) {
            if (random <= sigmaPoints[i])
                return i;
        }
        return -1;
    }

    static int randomForDistribution(ArrayList<Double> distribution) {
        double[] sigmaPoints = new double[distribution.size()];
        sigmaPoints[0] = distribution.get(0);
        for (int i = 1; i < distribution.size(); i++)
            sigmaPoints[i] = sigmaPoints[i-1] + distribution.get(i);

        double random = Math.random();
        for (int i = 0; i < distribution.size(); i++) {
            if (random <= sigmaPoints[i])
                return i;
        }
        return -1;
    }

    static int argmax(double[] values) {
        int pos = -1;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
                pos = i;
            }
        }
        return pos;
    }
}
