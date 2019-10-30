import java.util.Arrays;
import java.util.Scanner;

public class Matrix {

    private int nrows;
    private int ncols;
    private double[][] matrixValues;

    public Matrix(int nrows, int ncols, double[] values){
        this.nrows = nrows;
        this.ncols = ncols;
        this.matrixValues = new double[nrows][ncols];

        if (values.length == nrows*ncols){
            for(int row = 0; row<nrows; row++) {
                System.arraycopy(values, row*ncols, matrixValues[row], 0, ncols);
            }
        }else{
            throw new IllegalArgumentException("Number of values "+ values.length + " do not match (" + nrows + "," + ncols + ")");
        }
    }

    public Matrix(int nrows, int ncols){
        this.nrows = nrows;
        this.ncols = ncols;
        this.matrixValues = new double[nrows][ncols];
    }

    public Matrix(int nrows, int ncols, double defaultValue){
        this.nrows = nrows;
        this.ncols = ncols;
        this.matrixValues = new double[nrows][ncols];
        for(int row = 0; row < nrows; row++){
            Arrays.fill(matrixValues[row], defaultValue);
        }
    }

    public int getNrows(){
        return this.nrows;
    }

    public int getNcols(){
        return this.ncols;
    }


    public double[][] getMatrixValues(){
        return this.matrixValues;
    }

    public String toString(){
        String res = "";
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                res += matrixValues[row][col] + " ";
            }
            if (row < nrows-1)
                res += "\n";
        }
        return res;
    }


    public double[] getColumn(int index){
        double[] column = new double[nrows];
        for(int i=0; i<nrows; i++){
            column[i] = matrixValues[i][index];
        }
        return column;
    }

    public double[] getRow(int index){
        double[] row = matrixValues[index];
        return row;
    }

    public double[] matrixToArray(){
        if(nrows == 1){
            return getRow(0);
        }else if(ncols ==1){
            return getColumn(0);
        }else{
            throw new IllegalArgumentException("It is not possible to convert the matrix to an array of 1D");
        }
    }

    public void set(int row, int col, double value) {
        this.matrixValues[row][col] = value;
    }

    public double get(int row, int col){
        return this.matrixValues[row][col];
    }

    public static Matrix product(Matrix matrixA, Matrix matrixB){

        if(matrixA.ncols == matrixB.nrows){
            int resultMatrixNrows = matrixA.nrows;
            int resultMatrixNcols = matrixB.ncols;
            Matrix resultMatrix = new Matrix(resultMatrixNrows, resultMatrixNcols, 0.0);
            for(int resultMatrixRow = 0; resultMatrixRow < resultMatrixNrows; resultMatrixRow++){
                for(int resultMatrixCol=0; resultMatrixCol < resultMatrixNcols; resultMatrixCol++){
                    for(int iter = 0; iter < matrixA.ncols; iter++) {
                        resultMatrix.matrixValues[resultMatrixRow][resultMatrixCol] += (matrixA.matrixValues[resultMatrixRow][iter] * matrixB.matrixValues[iter][resultMatrixCol]);
                    }
                }
            }
            return resultMatrix;
        }else{
            throw new IllegalArgumentException("Not possible to multiply matrices because of their dimensions");
        }
    }

    // Not used
    public static Matrix elementWiseProduct(Matrix matrixA, Matrix matrixB){
        if(matrixA.nrows == matrixB.nrows && matrixA.ncols == matrixB.ncols){
            int resultMatrixNrows = matrixA.nrows;
            int resultMatrixNcols = matrixA.ncols;
            Matrix resultMatrix = new Matrix(resultMatrixNrows, resultMatrixNcols);
            for(int row = 0; row < resultMatrixNrows; row++){
                for (int col = 0; col < resultMatrixNcols; col++){
                    resultMatrix.matrixValues[row][col] = matrixA.matrixValues[row][col] * matrixB.matrixValues[row][col];
                }
            }
            return resultMatrix;
        }else{
            throw new IllegalArgumentException("Both matrices need to have the same dimension in order to compute the element-wise product");
        }
    }

    public void setColumnToMatrixValues(double[] columnValues, int columnIndex){
        if(ncols > columnIndex && columnIndex >= 0){
            for(int row=0; row<nrows; row++) {
                set(row, columnIndex, columnValues[row]);
            }
        }else{
            throw new ArrayIndexOutOfBoundsException("");
        }
    }

    /**
     * Appends the given array as a column to the left of the matrix.
     * @param values values.length must match getNrows()
     */
    public void appendColumn(double[] values){
        if (values.length != getNrows())
            throw new IllegalArgumentException("Column length ("+values.length+")doesn't match matrix rows ("+getNrows()+")");
        for (int row = 0; row < getNrows(); row++) {
            double[] extendedRow = new double[getNcols() + 1];
            System.arraycopy(this.getRow(row), 0, extendedRow, 0, this.getNcols());
            extendedRow[extendedRow.length - 1] = values[row];
            matrixValues[row] = extendedRow;
        }
    }
    public static Matrix readMatrixFromLine(Scanner scanner) {

        String tokens[] = scanner.nextLine().split(" ");

        int nrows = Integer.parseInt(tokens[0]);
        int ncols = Integer.parseInt(tokens[1]);
        double[] values = new double[tokens.length - 2];

        for (int i = 0; i < values.length; i++) {
            values[i] = Double.parseDouble(tokens[2 + i]);
        }
        Matrix myMatrix = new Matrix(nrows, ncols, values);
        return myMatrix;
    }

    public String format() {
        String s = "";
        s+=getNrows()+" ";
        s+=getNcols()+" ";
        for(int row = 0; row < getNrows(); row++) {
            for(int col = 0; col < getNcols(); col ++) {
                s += get(row, col)+" ";
            }
        }
        return s;
    }

    public void divideColumnBy(double val, int col) {
        for (int row = 0; row < getNrows(); row++) {
            set(row, col, get(row, col) / val);
        }
    }
}



