import java.util.ArrayList;
import java.util.Arrays;

public class BackwardAlgorithm {


    public static Matrix calculateBetaPerT(Matrix A, Matrix B, ArrayList<Integer> observationsArray, ArrayList<Double> scaleFactors){
        int numberStates = A.getNcols();
        int numberObservations = observationsArray.size();

        Matrix betaPerT = new Matrix(numberStates, numberObservations);

        double[] initializedBeta = initializeBeta(numberStates);
        betaPerT.setColumn(numberObservations-1, initializedBeta);
        if (scaleFactors != null)
            betaPerT.divideColumnBy(scaleFactors.get(numberObservations - 1), numberObservations - 1);


        for(int t = numberObservations-2; t>=0; t--){
            double[] nextBeta = betaPerT.getColumn(t+1);
            double[] betaAtT = calculateBetaAtT(nextBeta, A, B, observationsArray.get(t+1));
            betaPerT.setColumn(t, betaAtT);
            if (initializedBeta != null) {
                betaPerT.divideColumnBy(scaleFactors.get(t), t);
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

}
