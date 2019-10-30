import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a matrix with fixed number of rows and dynamic number of columns
 */
public class Matrix {

    private int nrows;
    private int ncols;
    //Array of arraylists. matrix at i,j is matrixValues[i].get(j)
    private Table<Double> matrixValues;

    public Matrix(int nrows, int ncols, double[] values) {
        this.nrows = nrows;
        this.ncols = ncols;
        this.matrixValues = new Table<Double>(nrows);
        if (values.length == nrows * ncols) {
            for (int row = 0; row < nrows; row++) {
                for (int col = 0; col < ncols; col++) {
                    add(row, values[row * ncols + col]);
                }
            }
        } else {
            throw new IllegalArgumentException("Number of values " + values.length + " do not match (" + nrows + "," + ncols + ")");
        }
    }

    public Matrix(int nrows, int ncols) {
        this(nrows, ncols, 0.0d);
    }

    public Matrix(int nrows, int ncols, double defaultValue) {
        this.nrows = nrows;
        this.ncols = ncols;
        this.matrixValues = new Table<Double>(nrows);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                matrixValues.add(row, defaultValue);
            }
        }
    }

    public int getNrows() {
        return this.nrows;
    }

    public int getNcols() {
        return this.ncols;
    }

    public String toString() {
        String res = "";
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                res += get(row, col) + " ";
            }
            if (row < nrows - 1)
                res += "\n";
        }
        return res;
    }


    public double[] getColumn(int index) {
        double[] column = new double[nrows];
        for (int i = 0; i < nrows; i++) {
            column[i] = get(i, index);
        }
        return column;
    }

    public double[] getLastColumn() {
        return getColumn(getNcols() - 1);
    }

    public void setColumn(int col, ArrayList<Double> values) {
        for (int row = 0; row < matrixValues.getNrows(); row++)
            matrixValues.set(row, col, values.get(row));
    }

    public void setColumn(int col, double[] values) {
        for (int row = 0; row < matrixValues.getNrows(); row++)
            matrixValues.set(row, col, values[row]);
    }

    public ArrayList<Double> getRowAsList(int index) {
        return matrixValues.getRow(index);
    }

    public double[] getRowAsArray(int index) {
        double[] row = new double[getNcols()];
        for (int col = 0; col < ncols; col++)
            row[col] = get(index, col);
        return row;
    }

    /**
     * Return a 1xn or nx1 matrix as a double[]
     *
     * @return double[] with values of the unique row/column
     */
    public double[] matrixToArray() {
        if (getNrows() == 1) {
            return getRowAsArray(0);
        } else if (getNcols() == 1) {
            return getColumn(0);
        } else {
            throw new IllegalArgumentException("It is not possible to convert the matrix to an array of 1D");
        }
    }

    /**
     * Appends the given value at the left of the last element of the given row
     *
     * @param row   0<= row < getNrows()
     * @param value
     */
    public void add(int row, double value) {
        matrixValues.add(row, value);
        ncols = Math.max(matrixValues.rowLength(row), ncols);
    }

    public void set(int row, int col, double value) {
        this.matrixValues.set(row, col, value);
    }

    public double get(int row, int col) {
        return this.matrixValues.get(row, col);
    }

    /**
     * Sums the given value to matrix[row,col].
     *
     * @param row
     * @param col
     * @param value
     */
    public void sum(int row, int col, double value) {
        double previous = get(row, col);
        set(row, col, previous + value);
    }

    public static Matrix product(Matrix matrixA, Matrix matrixB) {

        if (matrixA.ncols != matrixB.nrows)
            throw new IllegalArgumentException("Not possible to multiply matrices because of their dimensions");
        int resNRows = matrixA.getNrows();
        int resNCols = matrixB.getNcols();
        Matrix resultMatrix = new Matrix(resNRows, resNCols);
        for (int resultMatrixRow = 0; resultMatrixRow < resNRows; resultMatrixRow++) {
            for (int resultMatrixCol = 0; resultMatrixCol < resNCols; resultMatrixCol++) {
                for (int iter = 0; iter < matrixA.ncols; iter++) {
                    resultMatrix.sum(resultMatrixRow, resultMatrixCol, matrixA.get(resultMatrixRow, iter) * matrixB.get(iter, resultMatrixCol));
                }
            }
        }
        return resultMatrix;
    }

    /**
     * Appends the given array as a column to the right of the matrix.
     *
     * @param values values.length must match getNrows()
     */
    public void appendColumn(double[] values) {
        if (values.length != getNrows())
            throw new IllegalArgumentException("Column length (" + values.length + ")doesn't match matrix rows (" + getNrows() + ")");
        for (int row = 0; row < getNrows(); row++) {
            add(row, values[row]);
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
        s += getNrows() + " ";
        s += getNcols() + " ";
        for (int row = 0; row < getNrows(); row++) {
            for (int col = 0; col < getNcols(); col++) {
                s += get(row, col) + " ";
            }
        }
        return s.substring(0, s.length() - 1);
    }

    public void divideColumnBy(double val, int col) {
        for (int row = 0; row < getNrows(); row++) {
            set(row, col, get(row, col) / val);
        }
    }

    public void multiplyColumnBy(double val, int col) {
        for (int row = 0; row < getNrows(); row++) {
            set(row, col, val * get(row, col));
        }
    }

    public void divideRowBy(double val, int row) {
        for (int col = 0; col < getNcols(); col++) {
            set(row, col, get(row, col) / val);
        }
    }

    public void multiplyRowBy(double val, int row) {
        for (int col = 0; col < getNcols(); col++) {
            set(row, col, get(row, col) * val);
        }
    }

    public void normalizeRow(int row) {
        double sum = 0;
        for (double val : getRowAsList(row))
            sum += val;
        for (int col = 0; col < getRowAsList(row).size(); col++)
            set(row, col, get(row, col) / sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return nrows == matrix.nrows &&
                ncols == matrix.ncols &&
                matrixValues.equals(matrix.matrixValues);
    }

}



