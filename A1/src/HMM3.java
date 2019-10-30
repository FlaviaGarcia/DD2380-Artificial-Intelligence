import java.util.Scanner;


public class HMM3 {


    public static void main(String[] args) {
        // Read inputs
        Scanner scanner = new Scanner(System.in);
        Matrix Ao = Matrix.readMatrixFromLine(scanner);
        Matrix Bo = Matrix.readMatrixFromLine(scanner);
        Matrix Po = Matrix.readMatrixFromLine(scanner);

        int[] observationsArray = VectorUtils.readVectorObservationsFromLine(scanner);

        HMM hmm = new HMM(Ao, Bo, Po, observationsArray);
        hmm.learnFromObservations(100, 0.00001);
        System.out.println(hmm.getA().format());
        System.out.println(hmm.getB().format());
    }
}
