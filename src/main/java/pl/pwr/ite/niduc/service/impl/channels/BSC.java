package pl.pwr.ite.niduc.service.impl.channels;

import pl.pwr.ite.niduc.service.NumberGenerator;
import pl.pwr.ite.niduc.service.impl.NumberGeneratorImpl;

public class BSC {

    // Deklaracja zmiennych przechowujących parametry kanału oraz generator liczb losowych
    private final double ber; // BER
    private final double independentErrors; // Błąd niezależny
    private final double groupErrors; // Błąd grupowy
    private final NumberGenerator numberGenerator; // Generator liczb losowych


    // Konstruktor klasy BSC, inicjalizuje parametry kanału i generatora liczb losowych
    public BSC(double ber, double independentErrors, double groupErrors, NumberGenerator numberGenerator) {
        this.ber = ber;
        this.independentErrors = independentErrors;
        this.groupErrors = groupErrors;
        this.numberGenerator = numberGenerator;
    }


    // Metoda symulująca działanie kanału BSC
    public int[][] transmit(int[][] inputArray) {
        int rows = inputArray.length; // Liczba wierszy tablicy wejściowej
        int cols = inputArray[0].length; // Liczba kolumn tablicy wejściowej
        int[][] outputArray = new int[rows][cols]; // Tablica na dane wyjściowe

        // Pętla iterująca po elementach tablicy wejściowej
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double rand = numberGenerator.nextInteger(0, 1000) / 1000.0; // Losowanie liczby
                // Sprawdzenie, czy ma wystąpić błąd bitu na podstawie zadanego BER
                if (rand < ber) {
                    outputArray[i][j] = flipBit(inputArray[i][j]); // Odwrócenie bitu, jeśli ma wystąpić błąd
                } else if (rand < ber + independentErrors) {
                    outputArray = introduceGroupError(outputArray, i, j); // Wprowadzenie błędów grupowych
                } else if (rand < ber + independentErrors + groupErrors) {
                    outputArray = introduceIndependentError(outputArray, i); // Wprowadzenie błędów cyklicznych
                } else {
                    outputArray[i][j] = inputArray[i][j]; // Pozostawienie bitu bez zmian
                }
            }
        }

        return outputArray; // Zwrócenie tablicy z danymi wyjściowymi po transmisji
    }


    // Metoda odwracająca bit
    private int flipBit(int bit) {
        return bit ^ 1;
    }


    // Metoda wprowadzająca błędy grupowe
    private int[][] introduceGroupError(int[][] array, int row, int col) {
        int rows = array.length;
        int cols = array[0].length;
        int[][] newArray = deepCopy(array); // Głębokie kopiowanie tablicy

        // Odwrócenie bitu w wybranym miejscu
        newArray[row][col] = flipBit(array[row][col]);

        // Odwrócenie bitów w otoczeniu wybranego miejsca
        if (row > 0)
            newArray[row - 1][col] = flipBit(array[row - 1][col]);
        if (row < rows - 1)
            newArray[row + 1][col] = flipBit(array[row + 1][col]);
        if (col > 0)
            newArray[row][col - 1] = flipBit(array[row][col - 1]);
        if (col < cols - 1)
            newArray[row][col + 1] = flipBit(array[row][col + 1]);

        return newArray; // Zwrócenie tablicy z wprowadzonymi błędami
    }


    // Metoda wprowadzająca błędy niezależne
    private int[][] introduceIndependentError(int[][] array, int row) {
        int cols = array[0].length;
        int[][] newArray = deepCopy(array); // Głębokie kopiowanie tablicy

        // Odwrócenie co drugiego bitu w wybranym wierszu
        for (int i = 0; i < cols; i += 2) {
            newArray[row][i] = flipBit(array[row][i]);
        }

        return newArray; // Zwrócenie tablicy z wprowadzonymi błędami
    }


    // Metoda dokonująca głębokiego kopiowania tablicy
    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }


    // Metoda główna z przykładem działania
    public static void main(String[] args) {
        // Inicjalizacja parametrów kanału oraz tablicy wejściowej
        double ber = 0.1; // Bit Error Rate
        double independentErrors = 0.2; // Błąd niezależny
        double groupErrors = 0.1; // Błąd grupowy
        BSC bsc = new BSC(ber, independentErrors, groupErrors, new NumberGeneratorImpl()); // Inicjalizacja obiektu klasy BSC
        int[][] inputArray = {{0, 1, 1, 0, 0, 1}, {1, 0, 1, 0, 0, 1}}; // Tablica wejściowa

        // Wyświetlenie tablicy wejściowej
        System.out.println("Input:");
        printArray(inputArray);

        // Symulacja transmisji przez kanał BSC i wyświetlenie danych wyjściowych
        int[][] outputArray = bsc.transmit(inputArray);
        System.out.println("\nOutput:");
        printArray(outputArray);
    }

    // Metoda wyświetlająca tablicę
    private static void printArray(int[][] array) {
        for (int[] row : array) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
