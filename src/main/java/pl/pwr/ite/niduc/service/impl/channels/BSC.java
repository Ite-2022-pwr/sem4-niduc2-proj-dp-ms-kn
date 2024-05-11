package pl.pwr.ite.niduc.service.impl.channels;

import pl.pwr.ite.niduc.service.NumberGenerator;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

public class BSC {

    // Deklaracja zmiennych przechowujących parametry kanału oraz generator liczb losowych
    private final double ber; // BER
    private final NumberGenerator numberGenerator; // Generator liczb losowych

    // Konstruktor klasy BSC, inicjalizuje parametry kanału i generatora liczb losowych
    public BSC(double ber, NumberGenerator numberGenerator) {
        this.ber = ber;
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

    // Metoda główna z przykładem działania
    public static void main(String[] args) {
        // Inicjalizacja parametrów kanału oraz tablicy wejściowej
        double ber = 0.1; // Bit Error Rate
        BSC bsc = new BSC(ber, new NumberGeneratorImpl()); // Inicjalizacja obiektu klasy BSC
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
