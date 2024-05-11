package pl.pwr.ite.niduc.service.impl.channels;

import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

public class GilbertElliot {
    // Prawdopodobieństwo błędu, gdy kanał jest w dobrym stanie
    private double pOfErrorWhenGood;
    // Prawdopodobieństwo przejścia z dobrego stanu do złego
    private double pOfGoodToBad;
    // Prawdopodobieństwo błędu, gdy kanał jest w złym stanie
    private double pOfErrorWhenBad;
    // Prawdopodobieństwo przejścia z złego stanu do dobrego
    private double pOfBadToGood;
    // Obiekt generatora liczb losowych
    private NumberGeneratorImpl numberGenerator;

    // Konstruktor klasy Gilberta-Elliotta
    public GilbertElliot(double pOfErrorWhenGood, double pOfGoodToBad, double pOfErrorWhenBad, double pOfBadToGood, NumberGeneratorImpl numberGenerator) {
        // Inicjalizacja parametrów kanału
        this.pOfErrorWhenGood = pOfErrorWhenGood;
        this.pOfGoodToBad = pOfGoodToBad;
        this.pOfErrorWhenBad = pOfErrorWhenBad;
        this.pOfBadToGood = pOfBadToGood;
        // Inicjalizacja generatora liczb losowych
        this.numberGenerator = new NumberGeneratorImpl();
    }

    // Metoda transmitująca dane przez kanał Gilberta-Elliotta
    public int[][] transmit(int[][] inputArray) {
        // Inicjalizacja tablicy wyjściowej, która przechowuje dane wyjściowe po transmisji
        int[][] outputArray = new int[inputArray.length][inputArray[0].length];
        // Zmienna przechowująca aktualny stan kanału (dobry lub zły)
        boolean goodState = true;

        // Iteracja po wierszach tablicy wejściowej
        for (int i = 0; i < inputArray.length; i++) {
            // Iteracja po kolumnach tablicy wejściowej
            for (int j = 0; j < inputArray[i].length; j++) {
                // Sprawdzenie, czy kanał jest w dobrym stanie
                if (goodState) {
                    // Losowanie liczby z generatora i porównanie z prawdopodobieństwem błędu w dobrym stanie
                    if (numberGenerator.nextInteger(0, 1000) < pOfErrorWhenGood * 1000) {
                        // Jeśli błąd, odwrócenie bitu
                        outputArray[i][j] = flipBit(inputArray[i][j]);
                    } else {
                        // Bez błędu, przekazanie danych bez zmiany
                        outputArray[i][j] = inputArray[i][j];
                    }
                    // Losowanie stanu na podstawie prawdopodobieństwa przejścia ze stanu dobrego do złego
                    goodState = numberGenerator.nextInteger(0, 1000) > pOfGoodToBad * 1000;
                } else {
                    // Jeśli kanał jest w złym stanie, analogicznie do powyższego
                    if (numberGenerator.nextInteger(0, 1000) < pOfErrorWhenBad * 1000) {
                        outputArray[i][j] = flipBit(inputArray[i][j]);
                    } else {
                        outputArray[i][j] = inputArray[i][j];
                    }
                    goodState = numberGenerator.nextInteger(0, 1000) > (1 - pOfBadToGood) * 1000;
                }
            }
        }
        return outputArray;
    }

    // Metoda odwracająca bit
    private int flipBit(int bit) {
        return bit ^ 1;
    }

    public static void main(String[] args) {
        // Przykładowe dane wejściowe
        int[][] inputArray = {{0, 1, 1, 0, 0, 1}, {1, 0, 1, 0, 0, 1}};
        // Inicjalizacja parametrów kanału
        double pOfErrorWhenGood = 0.1;
        double pOfGoodToBad = 0.2;
        double pOfErrorWhenBad = 0.3;
        double pOfBadToGood = 0.4;

        // Inicjalizacja obiektu klasy Gilberta-Elliotta
        GilbertElliot channel = new GilbertElliot(pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood, new NumberGeneratorImpl());
        // Transmitowanie danych przez kanał
        int[][] outputArray = channel.transmit(inputArray);

        // Wyświetlenie danych wejściowych
        System.out.println("Input: ");
        for (int[] row : inputArray) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }

        // Wyświetlenie danych wyjściowych
        System.out.println("Output: ");
        for (int[] row : outputArray) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
