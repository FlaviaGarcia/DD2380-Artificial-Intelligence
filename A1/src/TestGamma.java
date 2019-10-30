public class TestGamma {

    public static void main(String[] args) {
        Matrix A = new Matrix(2, 2, new double[] {0.4, 0.6, 0.3, 0.7});
        Matrix B = new Matrix(2, 2, new double[] {0.1, 0.9, 0.8, 0.2});
        Matrix Pi = new Matrix(1, 2, new double[] {0.3, 0.7});
        int[] observationsArray = new int[] {0, 1, 0};

        HMM hmm = new HMM(A, B, Pi, observationsArray);
        hmm.learnFromObservations(5,1);

        Matrix digammaZeroActual = hmm.digamma.get(0);

        Matrix digammaOneActual = hmm.digamma.get(1);

        System.out.println("digamma0:");
        System.out.println(digammaZeroActual);
        System.out.println();
        System.out.println("digamma1");
        System.out.println(digammaOneActual);
        System.out.println();
        System.out.println("gamma");
        System.out.println(hmm.gamma);
        System.out.println();

        System.out.println("---------");
        System.out.println(hmm.A);
        System.out.println();
        System.out.println(hmm.B);
    }

    public static Matrix digammaForT(Matrix A, Matrix B, Matrix alpha, Matrix beta, int[] obs, int time) {
        Matrix digamma = new Matrix(A.getNrows(), A.getNcols());
        for (int stateFrom = 0; stateFrom < A.getNrows(); stateFrom++) {
            for (int stateTo = 0; stateTo < A.getNrows(); stateTo++) {
                //System.out.println("SETTING digamma("+stateFrom+","+stateTo+")");
                //System.out.println("    alpha("+stateFrom+","+time+") = "+alpha.get(stateFrom, time));
                //System.out.println("    A("+stateFrom+","+stateTo+") = "+A.get(stateFrom,stateTo));
                //System.out.println("    B("+stateTo+","+obs[time]+") = "+B.get(stateTo, obs[time +1]));
                //System.out.println("    beta("+stateTo+","+(time+1)+") = "+beta.get(stateTo, time + 1));
                double val = alpha.get(stateFrom, time) * A.get(stateFrom, stateTo) * B.get(stateTo, obs[time+1]) * beta.get(stateTo, time + 1);
                //System.out.println("    Result: "+val);
                digamma.set(stateFrom, stateTo, val);
            }
        }
        return digamma;
    }

    public static Matrix gammaForT(Matrix digamma) {
        Matrix gamma = new Matrix(digamma.getNrows(), 1);
        for (int row = 0; row < gamma.getNrows(); row++) {
            int sum = 0;
            for (int i = 0; i < digamma.getNrows(); i++) {
                sum+= digamma.get(row, i);
            }
            gamma.set(row, 0, sum);
        }
        return gamma;
    }
}
