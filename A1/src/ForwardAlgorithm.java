import java.util.ArrayList;
import java.util.Arrays;

public class ForwardAlgorithm {

    public static Alpha forward(Matrix A, Matrix B, Matrix Pi, ArrayList<Integer> observationsArray, boolean normalized){
        int numberStates = A.getNcols();
        int numberObservations = observationsArray.size();
        Alpha alpha = new Alpha(numberStates, numberObservations);

        double[] initializedAlpha = initializeAlpha(Pi, B, observationsArray.get(0));
        alpha.alphaMatrix.setColumn(0, initializedAlpha);

        if (normalized) {
            double scaleFactor = VectorUtils.sum(initializedAlpha);
            alpha.alphaMatrix.divideColumnBy(scaleFactor, 0);
            alpha.scaleFactors.add(scaleFactor);
        }

        double[] previousAlpha;

        for(int t=1; t<alpha.alphaMatrix.getNcols(); t++){
            previousAlpha = alpha.alphaMatrix.getColumn(t-1);
            double[] AlphatT = calculateAlphaAtT(previousAlpha, A, B, observationsArray.get(t));
            alpha.alphaMatrix.setColumn(t, AlphatT);
            if (normalized) {
                double scaleFactor = VectorUtils.sum(AlphatT);
                alpha.alphaMatrix.divideColumnBy(scaleFactor, t);
                alpha.scaleFactors.add(scaleFactor);
            }
        }
        return alpha;
    }

    public static double[] initializeAlpha(Matrix Pi, Matrix B, int firstObservation){
        double[] PiArray = Pi.matrixToArray();
        double[] initializedAlpha = VectorUtils.elementWiseVectorsProduct(PiArray, B.getColumn(firstObservation));
        return initializedAlpha;
    }

    public static double[] calculateAlphaAtT(double[] previousAlpha, Matrix A, Matrix B, int observation){
        double[] predictionAtT = getNextStatePrediction(previousAlpha, A);
        double[] alpha = VectorUtils.elementWiseVectorsProduct(predictionAtT, B.getColumn(observation));
        return alpha;
    }

    public static double[] getNextStatePrediction(double[] previousAlpha, Matrix A){
        int nextStatePredictionLength = previousAlpha.length;
        double[] nextStatePrediction = new double[nextStatePredictionLength];
        Arrays.fill(nextStatePrediction, 0.0);
        for (int row=0; row<nextStatePredictionLength; row++){
            for(int i=0; i<nextStatePredictionLength; i++){
                nextStatePrediction[row] += previousAlpha[i] * A.get(i, row);
            }
        }
        return nextStatePrediction;
    }

    public static class Alpha {
        public Matrix alphaMatrix;
        public ArrayList<Double> scaleFactors = new ArrayList<>();

        public Alpha(int nstates, int nobservations) {
            alphaMatrix = new Matrix(nstates, nobservations);
        }

        public double logPObservations () {
            double sum = 0;
            for (double f : scaleFactors)
                sum += Math.log(f);
            return sum;
        }
    }
}
