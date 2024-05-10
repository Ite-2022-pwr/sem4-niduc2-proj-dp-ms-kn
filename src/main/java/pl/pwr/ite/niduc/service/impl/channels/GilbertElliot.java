package pl.pwr.ite.niduc.service.impl.channels;

import pl.pwr.ite.niduc.service.NumberGenerator;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

public class GilbertElliot {

    // Deklaracja zmiennych przechowujących parametry kanału oraz generator liczb losowych
    private final double ber; // BER
    private final double independentErrors; // Błąd niezależny
    private final double groupErrors; // Błąd grupowy
    private final NumberGenerator numberGenerator; // Generator liczb losowych


    // Konstruktor klasy Gilberta-Elliota, inicjalizuje parametry kanału i generatora liczb losowych
    public GilbertElliot(double ber, double independentErrors, double groupErrors, NumberGenerator numberGenerator) {
        this.ber = ber;
        this.independentErrors = independentErrors;
        this.groupErrors = groupErrors;
        this.numberGenerator = numberGenerator;
    }


    // Metoda symulująca działanie kanału Gilberta-Elliot
    public int[][] transmit(int[][] inputArray) {

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


    // Metoda główna z przykładem działania
    public static void main(String[] args) {
        // Inicjalizacja parametrów kanału oraz tablicy wejściowej
        double ber = 0.1; // Bit Error Rate
        double independentErrors = 0.2; // Błąd niezależny
        double groupErrors = 0.1; // Błąd grupowy
        GilbertElliot ge = new GilbertElliot(ber, independentErrors, groupErrors, new NumberGeneratorImpl()); // Inicjalizacja obiektu klasy BSC
        int[][] inputArray = {{0, 1, 1, 0, 0, 1}, {1, 0, 1, 0, 0, 1}}; // Tablica wejściowa

        // Wyświetlenie tablicy wejściowej
        System.out.println("Input:");
        printArray(inputArray);

        // Symulacja transmisji przez kanał BSC i wyświetlenie danych wyjściowych
        int[][] outputArray = ge.transmit(inputArray);
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
