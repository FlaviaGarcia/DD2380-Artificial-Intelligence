import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MatrixTest {

    @org.junit.jupiter.api.Test
    void testSizeAtInit() {
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,4,5,6});
        assertEquals(m.getNcols(), 3);
        assertEquals(m.getNrows(), 2);
    }

    @org.junit.jupiter.api.Test
    void testFormat() {
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,1,2,3});
        String expected = "2 3 1.0 2.0 3.0 1.0 2.0 3.0";
        assertEquals(expected, m.format());
    }

    @org.junit.jupiter.api.Test
    void getColumn() {
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,1,2,3});
        double[] actualColumn = m.getColumn(2);
        double[] expectedColumn = new double[]{3,3};
        assertArrayEquals(actualColumn, expectedColumn);
    }

    @org.junit.jupiter.api.Test
    void getRow() {
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,4,5,6});
        ArrayList<Double> actualRow = m.getRowAsList(1);
        ArrayList<Double> expectedRow = new ArrayList<>(Arrays.asList(new Double[]{4.0,5.0,6.0}));
        assertIterableEquals(actualRow, expectedRow);
    }

    @org.junit.jupiter.api.Test
    void testSizeAtAppendCol() {
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,4,5,6});
        m.appendColumn(new double[] {7,8});
        assertEquals(m.getNcols(), 4);
    }

    @org.junit.jupiter.api.Test
    void matrixToArrayHorizontal() {
        Matrix m = new Matrix(1, 5, new double[] {1,2,3,4,5});
        ArrayList<Double> actual = m.getRowAsList(0);
        ArrayList<Double> expected = new ArrayList<>(Arrays.asList(new Double[]{1.0, 2.0, 3.0, 4.0, 5.0}));
        assertIterableEquals(actual, expected);
    }

    @org.junit.jupiter.api.Test
    void matrixToArrayVertical() {
        double[] expected = new double[] {1,2,3,4,5};
        Matrix m = new Matrix(5, 1, expected);
        double[] actual = m.getColumn(0);
        assertArrayEquals(actual, expected);
    }

    @org.junit.jupiter.api.Test
    void testSet() {
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,4,5,6});
        double expected = 123;
        m.set(0,0, expected);
        double actual = m.get(0,0);
        assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void get() {
        double expected = 15;
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,expected,5,6});
        double acutal = m.get(1, 0);
        assertEquals(expected,acutal);
    }

    @org.junit.jupiter.api.Test
    void product() {
        Matrix a = new Matrix(2, 3, new double[]{1,2,3,4,5,6});
        Matrix b = new Matrix(3, 2, new double[]{1, 0, 3, 10, 7, -2});
        Matrix actualProduct = Matrix.product(a, b);
        Matrix expected = new Matrix(2,2,new double[] {28, 17, 61, 94});
    }


    @org.junit.jupiter.api.Test
    void setColumnToMatrixValues() {
        Matrix m = new Matrix(2, 3, new double[]{1,2,3,4,5,6});
        double[] expected = new double[] {9,8};
        m.setColumn(1, expected);
        double actual[] = m.getColumn(1);
        assertArrayEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void appendColumn() {
        Matrix m = new Matrix(3, 2, new double[]{1,2,3,4,5,6});
        double expected[] = new double[]{100, 99, 98};
        m.appendColumn(expected);
        double[] actual = m.getColumn(m.getNcols()-1);
        assertArrayEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void testSizeAfterAppendColumn() {
        Matrix m = new Matrix(3, 2, new double[]{1,2,3,4,5,6});
        double newCol[] = new double[]{100, 99, 98};
        m.appendColumn(newCol);
        int expectedRows = 3;
        int expectedCols = 3;
        assertEquals(expectedRows, m.getNrows());
        assertEquals(expectedCols, m.getNcols());
    }

    @org.junit.jupiter.api.Test
    void divideColumnBy() {
        Matrix m = new Matrix(3, 2, new double[]{1,2,3,4,6,8});
        m.divideColumnBy(2, 1);
        double[] expected = new double[]{1, 2, 4};
        double[] actual = m.getColumn(1);
        assertArrayEquals(expected,actual);
    }

    @org.junit.jupiter.api.Test
    void testDivideRowBy() {
        Matrix m = new Matrix(3, 2, new double[]{10,20,3,4,6,8});
        m.divideRowBy(5, 0);
        double[] expected = new double[]{2, 4};
        double[] actual = m.getRowAsArray(0);
        assertArrayEquals(expected, actual);
    }
}