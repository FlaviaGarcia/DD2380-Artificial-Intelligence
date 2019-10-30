import java.util.Scanner;

public class HMM0 {

    public static Matrix calculateEachObservationProb(Matrix Pi, Matrix A, Matrix B){
        Matrix stateProb = calculateStateProb(Pi, A);
        Matrix observationProb = Matrix.product(stateProb, B);
        return observationProb;
    }

    public static Matrix calculateStateProb(Matrix Pi, Matrix A){
        Matrix stateProb = Matrix.product(Pi, A);
        return stateProb;
    }




    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Matrix A = Matrix.readMatrixFromLine(scanner);
        Matrix B = Matrix.readMatrixFromLine(scanner);
        Matrix Pi = Matrix.readMatrixFromLine(scanner);

        Matrix result = calculateEachObservationProb(Pi, A, B);

        System.out.println(result.getNrows() + " " + result.getNcols() +  " " + result.toString());

    }
}
