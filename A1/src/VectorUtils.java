import java.util.Scanner;

public class VectorUtils {
    public static int[] readVectorObservationsFromLine(Scanner scanner) {

        String tokens[] = scanner.nextLine().split(" ");

        int size = Integer.parseInt(tokens[0]);
        int[] observationsArray = new int[size];

        for (int i = 0; i < observationsArray.length; i++) {
            observationsArray[i] = Integer.parseInt(tokens[1 + i]);
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
        return s;
    }
}
