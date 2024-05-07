package pl.pwr.ite.niduc.service.impl.channels;

import pl.pwr.ite.niduc.service.NumberGenerator;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

// Deklaracja klasy GilbertElliot
public class GilbertElliot {

    // Metoda symulująca działanie kanału Gilberta-Elliot
    public static int[][] gilbert(int[][] inputArray, double ber, double independentErrors, double groupErrors, NumberGenerator numberGenerator) {
        // Inicjalizacja tablicy wyjściowej o takich samych wymiarach jak tablica wejściowa
        int[][] outputArray = new int[inputArray.length][inputArray[0].length];
        // Inicjalizacja zmiennej określającej stan kanału (dobry lub zły)
        boolean goodState = true;

        // Pętla iterująca po wierszach tablicy wejściowej
        for (int i = 0; i < inputArray.length; i++) {
            // Pętla iterująca po elementach w danym wierszu tablicy wejściowej
            for (int j = 0; j < inputArray[i].length; j++) {
                // Sprawdzenie aktualnego stanu kanału
                if (goodState) {
                    // Jeśli kanał jest w dobrym stanie, sprawdzane jest, czy ma wystąpić błąd bitu na podstawie zadanego BER
                    if (numberGenerator.nextInteger(0, 1000) / 1000.0 < ber) {
                        // Jeśli błąd ma wystąpić, bit jest odwracany (jeśli był 0, to staje się 1, i odwrotnie)
                        outputArray[i][j] = flipBit(inputArray[i][j]);
                    } else {
                        // W przeciwnym razie bit pozostaje niezmieniony
                        outputArray[i][j] = inputArray[i][j];
                    }
                    // Zmiana stanu kanału na podstawie prawdopodobieństwa przejścia ze stanu dobrego do złego
                    goodState = nextState(numberGenerator, independentErrors);
                } else {
                    // Analogiczne działanie, gdy kanał jest w złym stanie, ale z innym prawdopodobieństwem przejścia
                    if (numberGenerator.nextInteger(0, 1000) / 1000.0 < ber) {
                        outputArray[i][j] = flipBit(inputArray[i][j]);
                    } else {
                        outputArray[i][j] = inputArray[i][j];
                    }
                    goodState = nextState(numberGenerator, groupErrors);
                }
            }
        }
        // Zwrócenie tablicy wyjściowej po zakończeniu symulacji
        return outputArray;
    }

    // Metoda odwracająca bit (0 na 1 i odwrotnie)
    private static int flipBit(int bit) {
        return bit ^ 1;
    }

    // Metoda określająca następny stan kanału na podstawie prawdopodobieństwa
    private static boolean nextState(NumberGenerator numberGenerator, double probability) {
        // Losowanie liczby i porównanie z prawdopodobieństwem
        return numberGenerator.nextInteger(0, 1000) / 1000.0 > probability;
    }

    // Metoda testująca, wyświetlająca tablicę
    public static void main(String[] args) {
        // Inicjalizacja tablicy wejściowej oraz parametrów BER, błędów niezależnych i grupowych
        int[][] inputArray = {{0, 1, 1, 0, 0, 1}, {1, 0, 1, 0, 0, 1}};
        double ber = 0.1; // Bit Error Rate
        double independentErrors = 0.2; // Probability of Transition from Good to Bad
        double groupErrors = 0.1; // Probability of Transition from Bad to Good

        // Inicjalizacja generatora liczb losowych
        NumberGenerator numberGenerator = new NumberGeneratorImpl();

        // Wyświetlenie tablicy wejściowej
        System.out.println("Input: ");
        printArray(inputArray);

        // Wywołanie metody symulującej kanał Gilberta-Elliot
        int[][] outputArray = gilbert(inputArray, ber, independentErrors, groupErrors, numberGenerator);

        // Wyświetlenie tablicy wyjściowej
        System.out.println("\nOutput: ");
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
