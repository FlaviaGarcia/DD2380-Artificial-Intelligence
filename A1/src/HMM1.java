import java.util.Arrays;
import java.util.Scanner;


public class HMM1 {

    public static double getProbabilityObservations(Matrix A, Matrix B, Matrix Pi, int[] observationsArray){
        double probabilityObservations = 0;
        double[] lastAlpha = getLastAlpha(A, B, Pi, observationsArray);

        for(int i=0; i<lastAlpha.length; i++){
            probabilityObservations += lastAlpha[i];
        }
        return probabilityObservations;
    }


    public static double[] getLastAlpha(Matrix A, Matrix B, Matrix Pi, int[] observationsArray){
        Matrix alphaPerT = calculateAlphaPerT(A,B,Pi, observationsArray);
        double[] lastAlpha = alphaPerT.getColumn(alphaPerT.getNcols() - 1);

        return lastAlpha;

    }


    public static Matrix calculateAlphaPerT(Matrix A, Matrix B, Matrix Pi, int[] observationsArray){
        int numberStates = A.getNcols();
        int numberObservations = observationsArray.length;
        Matrix alphaPerT = new Matrix(numberStates, numberObservations);

        double[] initializedAlpha = initializeAlpha(Pi, B, observationsArray[0]);

        alphaPerT.setColumnToMatrixValues(initializedAlpha, 0);

        double[] previousAlpha;

        for(int t=1; t<alphaPerT.getNcols(); t++){
            previousAlpha = alphaPerT.getColumn(t-1);
            double[] AlphatT = getAlphaAtT(previousAlpha, A, B, observationsArray[t]);
            alphaPerT.setColumnToMatrixValues(AlphatT, t);
        }

        return alphaPerT;
    }
    

    public static double[] initializeAlpha(Matrix Pi, Matrix B, int FirstObservation){
        double[] PiArray = Pi.matrixToArray();
        double[] initializedAlpha = elementWiseVectorsProduct(PiArray, B.getColumn(FirstObservation));
        return initializedAlpha;
    }


    public static double[] elementWiseVectorsProduct(double[] vectorA, double[] vectorB){
        if(vectorA.length == vectorB.length){
            int resultVectorLen = vectorA.length;
            double[] resultVector = new double[resultVectorLen];
            for(int i = 0; i < resultVectorLen; i++){
                resultVector[i] = vectorA[i] * vectorB[i];
            }
            return resultVector;
        }else{
            throw new IllegalArgumentException("The arrays do not have the same length, therefore it is not possible to compute the element-wise product");
        }
    }


    public static double[] getAlphaAtT(double[] previousAlpha, Matrix A, Matrix B, int observation){
        double[] predictionAtT = getNextStatePrediction(previousAlpha, A);
        double[] alpha = elementWiseVectorsProduct(predictionAtT, B.getColumn(observation));
        return alpha;
    }


    public static double[] getNextStatePrediction(double[] previousAlpha, Matrix A){
        int nextStatePredictionLength = previousAlpha.length;
        double[] nextStatePrediction = new double[nextStatePredictionLength];
        Arrays.fill(nextStatePrediction, 0.0);
        for (int row=0; row<nextStatePredictionLength; row++){
            for(int i=0; i<nextStatePredictionLength; i++){
                nextStatePrediction[row] += previousAlpha[i] * A.getMatrixValues()[i][row];
            }
        }
        return nextStatePrediction;
    }


    public static void main(String[] args) {
        // Read inputs
        Scanner scanner = new Scanner(System.in);
        Matrix A = Matrix.readMatrixFromLine(scanner);
        Matrix B = Matrix.readMatrixFromLine(scanner);
        Matrix Pi = Matrix.readMatrixFromLine(scanner);
        int[] observationsArray = VectorUtils.readVectorObservationsFromLine(scanner);

        // Compute probability observations
        double probabilityObservations = getProbabilityObservations(A, B, Pi, observationsArray);

        System.out.println(probabilityObservations);
    }
}
