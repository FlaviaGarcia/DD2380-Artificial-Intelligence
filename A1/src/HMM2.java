import java.util.Scanner;

public class HMM2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Matrix A = Matrix.readMatrixFromLine(scanner);
        Matrix B = Matrix.readMatrixFromLine(scanner);
        Matrix Pi = Matrix.readMatrixFromLine(scanner);
        int[] observations = VectorUtils.readVectorObservationsFromLine(scanner);
        HMM hmm = new HMM(A, B, Pi, observations);
        int[] seq = hmm.decode();
        for (int i : seq) {
            System.out.print(i + " ");
        }
    }
}
