package pl.pwr.ite.niduc.service.channels;

import java.util.Random;

public class GilbertElliotChannel {

    public static int[][] gilbert(int[][] inputArray, double pOfErrorWhenGood, double pOfGoodToBad, double pOfErrorWhenBad, double pOfBadToGood) {
        // Inicjalizacja tablicy wyjściowej, która przechowuje dane wyjściowe po transmisji
        int[][] outputArray = new int[inputArray.length][inputArray[0].length];
        // Inicjalizacja zmiennej stanu jako prawda, oznaczająca stan poprawnej transmisji
        boolean goodState = true; // true oznacza stan poprawnej transmisji, false - stan przekłamań
        // Inicjalizacja generatora liczb losowych
        Random rand = new Random();

        // Pętla iterująca po wszystkich wierszach tablicy wejściowej
        for (int i = 0; i < inputArray.length; i++) {
            // Pętla iterująca po wszystkich kolumnach tablicy wejściowej
            for (int j = 0; j < inputArray[i].length; j++) {
                // Sprawdzenie, czy aktualny stan jest dobry
                if (goodState) { // jestesmy w dobrym stanie
                    // Jeśli losowa liczba jest mniejsza niż prawdopodobieństwo błędu w dobrym stanie
                    if (rand.nextDouble() < pOfErrorWhenGood) {
                        // Przekłamanie bitu: zamiana 0 na 1 lub 1 na 0
                        outputArray[i][j] = (inputArray[i][j] == 0) ? 1 : 0;
                    } else {
                        // Bez błędu: przekazanie danych bez zmiany
                        outputArray[i][j] = inputArray[i][j];
                    }
                    // Losowanie stanu na podstawie prawdopodobieństwa przejścia ze stanu dobrego do złego
                    goodState = rand.nextDouble() > pOfGoodToBad;
                } else { // jestesmy w zlym stanie
                    // Jeśli losowa liczba jest mniejsza niż prawdopodobieństwo błędu w złym stanie
                    if (rand.nextDouble() < pOfErrorWhenBad) {
                        // Przekłamanie bitu: zamiana 0 na 1 lub 1 na 0
                        outputArray[i][j] = (inputArray[i][j] == 0) ? 1 : 0;
                    } else {
                        // Bez błędu: przekazanie danych bez zmiany
                        outputArray[i][j] = inputArray[i][j];
                    }
                    // Losowanie stanu na podstawie prawdopodobieństwa przejścia ze stanu złego do dobrego
                    goodState = rand.nextDouble() > (1 - pOfBadToGood);
                }
            }
        }
        // Zwrócenie tablicy wyjściowej po zakończeniu iteracji
        return outputArray;
    }


    public static void main(String[] args) {
        // Przykładowe użycie funkcji gilbert
        int[][] inputArray = {{0, 1, 1, 0, 0, 1}, {1, 0, 1, 0, 0, 1}};
        double pOfErrorWhenGood = 0.1;
        double pOfGoodToBad = 0.2;
        double pOfErrorWhenBad = 0.3;
        double pOfBadToGood = 0.4;

        System.out.println("Input: ");
        for (int[] ints : inputArray) {
            for (int anInt : ints) {
                System.out.print(anInt + " ");
            }
            System.out.println();
        }

        int[][] outputArray = gilbert(inputArray, pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood);

        // Wyświetlenie wynikowej tablicy
        System.out.println("Output: ");
        for (int[] ints : outputArray) {
            for (int anInt : ints) {
                System.out.print(anInt + " ");
            }
            System.out.println();
        }
    }
}
// Kanał Gilberta, który jest modelem dwustanowym używanym do symulowania zjawiska przekłamań
// w transmisji danych. Funkcja ta przyjmuje jako argumenty tablicę wejściową danych
// do transmisji oraz cztery parametry określające prawdopodobieństwa różnych zdarzeń w modelu.
//W każdej iteracji sprawdza, czy aktualny stan kanału jest dobry (goodState). Jeśli tak:

//Generuje losową liczbę i porównuje ją z pOfErrorWhenGood.
// Jeśli liczba jest mniejsza niż pOfErrorWhenGood, to wystąpi błąd transmisji
// i wartość w outputArray zostaje zmieniona (0 na 1 lub 1 na 0).
// W przeciwnym razie dane są przekazywane bez zmian.
//Następnie losuje, czy kanał przejdzie do złego stanu, na podstawie pOfGoodToBad.
