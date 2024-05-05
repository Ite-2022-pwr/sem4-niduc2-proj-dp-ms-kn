package pl.pwr.ite.niduc.service.impl.channels;

import java.util.Random;


// BSC
public class FinalBSC {
    private final double pOfError;
    private final double pOfBurst;
    private final double pOfCyclic;
    private final Random random;

    public FinalBSC(double pOfError, double pOfBurst, double pOfCyclic) {
        this.pOfError = pOfError;
        this.pOfBurst = pOfBurst;
        this.pOfCyclic = pOfCyclic;
        this.random = new Random();
    }

    public int[][] transmit(int[][] inputArray) {
        int rows = inputArray.length;
        int cols = inputArray[0].length;
        int[][] outputArray = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double rand = random.nextDouble();
                if (rand < pOfError) {
                    outputArray[i][j] = flipBit(inputArray[i][j]);
                } else if (rand < pOfError + pOfBurst) {
                    // Burst error
                    outputArray = introduceBurstError(outputArray, i, j);
                } else if (rand < pOfError + pOfBurst + pOfCyclic) {
                    // Cyclic error
                    outputArray = introduceCyclicError(outputArray, i, j);
                } else {
                    outputArray[i][j] = inputArray[i][j];
                }
            }
        }

        return outputArray;
    }

    private int flipBit(int bit) {
        return bit ^ 1;
    }

    private int[][] introduceBurstError(int[][] array, int row, int col) {
        // Simulate burst error by flipping neighboring bits
        int rows = array.length;
        int cols = array[0].length;
        int[][] newArray = array.clone();

        // Flip the bit at (row, col)
        newArray[row][col] = flipBit(array[row][col]);

        // Flip neighboring bits
        if (row > 0)
            newArray[row - 1][col] = flipBit(array[row - 1][col]); // Flip above
        if (row < rows - 1)
            newArray[row + 1][col] = flipBit(array[row + 1][col]); // Flip below
        if (col > 0)
            newArray[row][col - 1] = flipBit(array[row][col - 1]); // Flip left
        if (col < cols - 1)
            newArray[row][col + 1] = flipBit(array[row][col + 1]); // Flip right

        return newArray;
    }

    private int[][] introduceCyclicError(int[][] array, int row, int col) {
        // Simulate cyclic error by flipping bits in a cyclic manner
        int rows = array.length;
        int cols = array[0].length;
        int[][] newArray = array.clone();

        // Define cyclic pattern (e.g., flip every other bit in the row)
        for (int i = 0; i < cols; i += 2) {
            newArray[row][i] = flipBit(array[row][i]);
        }

        return newArray;
    }
}
