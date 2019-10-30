
import java.util.ArrayList;
import java.util.Scanner;

public class HMM3 {


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Scanner scanner = new Scanner(System.in);
        Matrix A = Matrix.readMatrixFromLine(scanner);
        Matrix B = Matrix.readMatrixFromLine(scanner);
        Matrix Pi = Matrix.readMatrixFromLine(scanner);
        ArrayList<Integer> observations = VectorUtils.readVectorObservationsFromLine(scanner);
        HMM hmm = new HMM(A, B, Pi, new ArrayList<>(observations.subList(0,5)));
        hmm.setObservations(observations);
        hmm.computeAlphaBeta();
//        for (int i = 20; i<observations.size(); i++) {
//            hmm.addObservation(observations.get(i), true);
//        }
        //int iters = hmm.learnFromObservations(20, 0.1);
        //System.out.println("Learned in "+iters);
        System.out.println(hmm.probabilityOfObservations());
        long endTime = System.currentTimeMillis();
        long timeRequired = endTime - startTime;
    }
}
