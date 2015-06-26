/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examples;

import java.util.Arrays;
import javax.swing.JTextArea;

/**
 *
 * @author Bassel Bakr
 */
public class Matrix
{

    int rows, columns;
    float[][] data;

    public Matrix(int rows, int columns)
    {
        this.rows = rows;
        this.columns = columns;
        data = new float[rows][columns];
    }

    public Matrix(String matrix)
    {
        String[] rowsArray = matrix.split("\n");
        rows = rowsArray.length;
        columns = rowsArray[0].split("[,]").length;
        data = new float[rows][columns];
        for (int i = 1; i <= rows; i++) {
            insertRow(rowsArray[i - 1], i);
        }
    }

    @Override
    public Matrix clone()
    {
        Matrix x = new Matrix(rows, columns);
        for (int i = 1; i <= rows; i++) {
            x.insertRow(Arrays.toString(getRow(i)), i);
        }
        return x;
    }

    public static boolean isSquare(Matrix x)
    {
        return x.rows == x.columns;
    }

    public static boolean hasProduct(Matrix a, Matrix b)
    {
        return a.columns == b.rows;
    }

    public static Matrix transpose(Matrix x)
    {
        Matrix xTranspose = new Matrix(x.columns, x.rows);
        for (int i = 1; i <= x.rows; i++) {
            for (int j = 1; j <= x.getRow(i).length; j++) {
                xTranspose.insert(x.getRow(i)[j - 1], j, i);
            }
        }
        return xTranspose;
    }

    public static Matrix multiply(Matrix a, Matrix b)
    {
        if (!hasProduct(a, b)) {
            return null;
        }
        Matrix x = new Matrix(a.rows, b.columns);
        float datum = 0;
        for (int i = 1; i <= a.rows; i++) {
            for (int j = 1; j <= b.columns; j++) {
                try {
                    int k = 1;
                    float f;
                    while ((f = a.get(i, k) * b.get(k, j)) != ~0) {
                        //System.out.printf("a[%d,%d] * b[%d,%d] = %f * %f = %f\n", i, k, k, j, a.get(i, k), b.get(k, j), f);
                        datum += f;
                        k++;
                    }
                }
                catch (IllegalArgumentException e) {
                    //System.out.printf("\n%f\n", datum);
                    x.insert(datum, i, j);
                    datum = 0;
                }
            }
        }
        return x;
    }

    public Matrix add(float datum, int row, int column)
    {
        data[row - 1][column - 1] = get(row, column) + datum;
        return this;
    }

    public static float solve(Matrix x)
    {
        if (x.rows != x.columns) {
            throw new IllegalArgumentException("Must be sqare matrix");
        }
        float value = 1;
        Matrix y = x.clone();

        for (int i = 1; i <= y.columns; i++) {
            value *= y.get(i, i);
            y.divideRow(y.get(i, i), i);
            float[] tmp = y.getRow(i);
            for (int j = 1; j <= y.rows; j++) {
                if (i == j) {
                    continue;
                }
                //value /= (y.get(j, i) * -1);
                y.multiplyRow(y.get(j, i) * -1, i)
                        .addToRow(y.getRow(i), j)
                        .insertRow(Arrays.toString(tmp), i);
            }
        }

        return value;
    }

    public static Matrix solveGuess(Matrix x)
    {
        if (x.columns <= x.rows) {
            throw new IllegalArgumentException("Columns must be > Rows!");
        }
        Matrix y = x.clone();

        for (int i = 1; i <= y.rows; i++) {
            y.divideRow(y.get(i, i), i);
            float[] tmp = y.getRow(i);
            for (int j = 1; j <= y.rows; j++) {
                if (i == j) {
                    continue;
                }
                y.multiplyRow(y.get(j, i) * -1, i)
                        .addToRow(y.getRow(i), j)
                        .insertRow(Arrays.toString(tmp), i);
            }
        }

        return y;
    }

    public static Matrix solveSteps(Matrix x, JTextArea area)
    {
        if (x.columns <= x.rows) {
            throw new IllegalArgumentException("Columns must be > Rows!");
        }
        float value = 1;
        Matrix y = x.clone();

        for (int i = 1; i <= y.rows; i++) {
            area.append(String.format("Divide row number (%d) by (%s):\n\n", i, toFraction(y.get(i, i))));
            value *= y.get(i, i);
            y.divideRow(y.get(i, i), i);
            area.append(y.toString());
            float[] tmp = y.getRow(i);
            for (int j = 1; j <= y.rows; j++) {
                if (i == j) {
                    continue;
                }

                area.append(String.format("Multiply row number (%d) by (%s):\n\n", i, toFraction(y.get(j, i) * -1)));
                y.multiplyRow(y.get(j, i) * -1, i);
                area.append(y.toString());

                area.append(String.format("Add row number (%d) to row number (%d):\n\n", i, j));
                y.addToRow(y.getRow(i), j);
                area.append(y.toString());

                area.append(String.format("Restore row number (%d):\n\n", i));
                y.insertRow(Arrays.toString(tmp), i);
                area.append(y.toString());
            }

            //area.append(String.format("Restore row number (%d):\n\n", y.rows));
            //y.multiplyRow(value, y.rows);
            //area.append(y.toString());
        }

        return y;
    }

    public Matrix insert(float datum, int row, int column)
    {
        data[row - 1][column - 1] = datum;
        return this;
    }

    public Matrix insertColumn(String datum, int column)
    {
        String[] values = datum.replace('[', ' ').replace(']', ' ').split("[,]");
        for (int i = 1; i <= rows; i++) {
            insert(fromFraction(values[i - 1]), i, column);
        }
        return this;
    }

    public Matrix insertRow(String datum, int row)
    {
        String[] values = datum.replace('[', ' ').replace(']', ' ').split("[,]");
        for (int i = 1; i <= columns; i++) {
            insert(fromFraction(values[i - 1]), row, i);
        }
        return this;
    }

    public Matrix addToRow(float[] values, int row)
    {
        for (int i = 1; i <= columns; i++) {
            data[row - 1][i - 1] += values[i - 1];
        }
        return this;
    }

    public Matrix addToColumn(float[] values, int column)
    {
        for (int i = 1; i <= columns; i++) {
            data[i - 1][column - 1] += values[i - 1];
        }
        return this;
    }

    public Matrix remFromRow(float[] values, int row)
    {
        for (int i = 1; i <= columns; i++) {
            data[row - 1][i - 1] -= values[i - 1];
        }
        return this;
    }

    public Matrix remFromColumn(float[] values, int column)
    {
        for (int i = 1; i <= columns; i++) {
            data[i - 1][column - 1] -= values[i - 1];
        }
        return this;
    }

    public Matrix divideRow(float num, int row)
    {
        for (int i = 1; i <= columns; i++) {
            data[row - 1][i - 1] /= num;
        }
        return this;
    }

    public Matrix divideColumn(float num, int column)
    {
        for (int i = 1; i <= rows; i++) {
            data[i - 1][column - 1] /= num;
        }
        return this;
    }

    public Matrix multiplyRow(float num, int row)
    {
        for (int i = 1; i <= columns; i++) {
            data[row - 1][i - 1] *= num;
        }
        return this;
    }

    public Matrix multiplyColumn(float num, int column)
    {
        for (int i = 1; i <= rows; i++) {
            data[i - 1][column - 1] *= num;
        }
        return this;
    }

    public Matrix zero(int row, int column)
    {
        data[row - 1][column - 1] = 0;
        return this;
    }

    public float get(int row, int column)
    {
        if (row < 1
                || column < 1
                || row > rows
                || column > columns) {
            throw new IllegalArgumentException("Index " + row + "x" + column + " out of reach");
        }
        return data[row - 1][column - 1];
    }

    public float[] getColumn(int column)
    {
        float[] c = new float[rows];
        for (int i = 0; i < rows; i++) {
            c[i] = data[column - 1][i];
        }
        return c;
    }

    public float[] getRow(int row)
    {
        float[] r = new float[columns];
        for (int i = 0; i < columns; i++) {
            r[i] = data[row - 1][i];
        }
        return r;
    }

    public static float fromFraction(String fraction)
    {
        float f = 1;
        if (fraction.contains("/")) {
            String[] parts = fraction.split("[/]");
            f *= Float.parseFloat(parts[0]);
            f /= Float.parseFloat(parts[1]);
        }
        else {
            f *= Float.valueOf(fraction);
        }
        return f;
    }

    public static String toFraction(float d)
    {
        StringBuilder sb = new StringBuilder("");
        if (d < 0) {
            sb.append("-");
            d = -d;
        }
        long l = (long) d;
        if (l != 0) {
            sb.append(l);
        }
        d -= l;
        double error = Math.abs(d);
        int bestDenominator = 1;
        for (int i = 2; i <= 10; i++) {
            double error2 = Math.abs(d - (double) Math.round(d * i) / i);
            if (error2 < error) {
                error = error2;
                bestDenominator = i;
                if (error2 == 0) {
                    break;
                }
            }
        }
        if (bestDenominator > 1) {
            sb.append((l != 0 ? ' ' : "")).append(Math.round(d * bestDenominator)).append('/').append(bestDenominator);
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                sb.append(get(i, j) == 0 ? 0 : toFraction(get(i, j)))
                        .append("\t");
            }
            sb.append("\r\n\r\n");
        }
        return sb.toString();
    }
}
