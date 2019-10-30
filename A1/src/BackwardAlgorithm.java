import javax.annotation.processing.SupportedSourceVersion;
import java.util.Arrays;

public class BackwardAlgorithm {


    public static Matrix calculateBetaPerT(Matrix A, Matrix B, int[] observationsArray, double[] scaleFactors){
        int numberStates = A.getNcols();
        int numberObservations = observationsArray.length;

        Matrix betaPerT = new Matrix(numberStates, numberObservations);

        double[] initializedBeta = initializeBeta(numberStates);
        betaPerT.setColumnToMatrixValues(initializedBeta, numberObservations-1);
        if (scaleFactors != null)
            betaPerT.divideColumnBy(scaleFactors[numberObservations - 1], numberObservations - 1);


        for(int t = numberObservations-2; t>=0; t--){
            double[] nextBeta = betaPerT.getColumn(t+1);
            double[] betaAtT = calculateBetaAtT(nextBeta, A, B, observationsArray[t+1]);
            betaPerT.setColumnToMatrixValues(betaAtT, t);
            if (initializedBeta != null) {
                betaPerT.divideColumnBy(scaleFactors[t], t);
            }
        }
        return betaPerT;
    }

    public static double[] initializeBeta(int numberStates){
        double[] initializedBeta = new double[numberStates];
        Arrays.fill(initializedBeta, 1);
        return initializedBeta;
    }


    public static double[] calculateBetaAtT(double[] nextBeta, Matrix A, Matrix B, int nextObservation){
        double[] beta = new double[nextBeta.length];
        Arrays.fill(beta, 0.0);

        for (int state=0; state<A.getNcols(); state++){
            double[] prediction = VectorUtils.vectorScalarProduct(A.getColumn(state), B.get(state, nextObservation));
            double[] weightedPrediction = VectorUtils.vectorScalarProduct(prediction, nextBeta[state]);
            beta = VectorUtils.sumVectors(beta, weightedPrediction);
        }

        return beta;
    }


    /**public static void main(String[] args) {
        // Read inputs
        Scanner scanner = new Scanner(System.in);
        Matrix A = Matrix.readMatrixFromLine(scanner);
        Matrix B = Matrix.readMatrixFromLine(scanner);
        Matrix Pi = Matrix.readMatrixFromLine(scanner);
        int[] observationsArray = VectorUtils.readVectorObservationsFromLine(scanner);

        // Compute probability observations
        Matrix betaPerT = calculateBetaPerT(A, B, Pi, observationsArray);

        System.out.println(betaPerT.toString());

    }**/


}
