import java.util.Arrays;

public class ForwardAlgorithm {

    public static double getProbabilityObservations(Matrix A, Matrix B, Matrix Pi, int[] observationsArray){
        double probabilityObservations = 0;
        double[] lastAlpha = getLastAlpha(A, B, Pi, observationsArray);

        for(int i=0; i<lastAlpha.length; i++){
            probabilityObservations += lastAlpha[i];
        }
        return probabilityObservations;
    }

    public static double[] getLastAlpha(Matrix A, Matrix B, Matrix Pi, int[] observationsArray){
        Matrix alphaPerT = calculateAlphaPerT(A,B,Pi, observationsArray, false).alphaMatrix;
        double[] lastAlpha = alphaPerT.getColumn(alphaPerT.getNcols() - 1);

        return lastAlpha;

    }


    public static Alpha calculateAlphaPerT(Matrix A, Matrix B, Matrix Pi, int[] observationsArray, boolean normalized){
        int numberStates = A.getNcols();
        int numberObservations = observationsArray.length;
        Alpha alpha = new Alpha(numberStates, numberObservations);

        double[] initializedAlpha = initializeAlpha(Pi, B, observationsArray[0]);
        alpha.alphaMatrix.setColumnToMatrixValues(initializedAlpha, 0);

        if (normalized) {
            double scaleFactor = VectorUtils.sum(initializedAlpha);
            alpha.alphaMatrix.divideColumnBy(scaleFactor, 0);
            alpha.scaleFactors[0] = scaleFactor;
        }


        double[] previousAlpha;

        for(int t=1; t<alpha.alphaMatrix.getNcols(); t++){
            previousAlpha = alpha.alphaMatrix.getColumn(t-1);
            double[] AlphatT = calculateAlphaAtT(previousAlpha, A, B, observationsArray[t]);
            alpha.alphaMatrix.setColumnToMatrixValues(AlphatT, t);
            if (normalized) {
                double scaleFactor = VectorUtils.sum(AlphatT);
                alpha.alphaMatrix.divideColumnBy(scaleFactor, t);
                alpha.scaleFactors[t] = scaleFactor;
            }
        }
        return alpha;
    }


    public static double[] initializeAlpha(Matrix Pi, Matrix B, int FirstObservation){
        double[] PiArray = Pi.matrixToArray();
        double[] initializedAlpha = VectorUtils.elementWiseVectorsProduct(PiArray, B.getColumn(FirstObservation));
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
                nextStatePrediction[row] += previousAlpha[i] * A.getMatrixValues()[i][row];
            }
        }
        return nextStatePrediction;
    }

    public static class Alpha {
        public Matrix alphaMatrix;
        public double scaleFactors[] = null;

        public Alpha(int nstates, int nobservations) {
            alphaMatrix = new Matrix(nstates, nobservations);
            scaleFactors = new double[nobservations];
        }
    }
}
