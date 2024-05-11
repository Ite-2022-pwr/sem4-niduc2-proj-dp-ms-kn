package pl.pwr.ite.niduc.service.impl.simulation;

import pl.pwr.ite.niduc.service.impl.codes.Hamming;
import pl.pwr.ite.niduc.service.impl.channels.BSC;
import pl.pwr.ite.niduc.service.impl.channels.GilbertElliot;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.ArrayList;
import java.util.List;

public class SimulationHamming {

    public static void main(String[] args) {
        // Definicja wiadomości do przesłania
        String message = "this is important information that should be kept secret";

        // Definicja kanału
        String channel = "GE"; // Możliwe wartości: "BSC", "GE", "null"

        // Liczba powtórzeń symulacji
        int numSimulations = 100;

        // Błędy
        double ber = 0.2;
        double pOfErrorWhenGood = 0.01;
        double pOfGoodToBad = 0.1;
        double pOfErrorWhenBad = 0.1;
        double pOfBadToGood = 0.8;

        // Inicjalizacja sum dla obliczenia średnich
        int totalBitsAfterTransmission = 0;
        int totalBitsAfterCorrection = 0;
        int totalMessageAfterTransmission = 0;
        int totalMessageAfterCorrection = 0;
        double totalPercentBitsCorrection = 0;
        double totalPercentCharsCorrection = 0;
        double totalPercentBitsError = 0;
        double totalPercentCharsError = 0;

        // Powtórzenie symulacji
        for (int i = 0; i < numSimulations; i++) {
            // Symulacja
            List<Double> results = simulate(message, channel, ber, pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood);

            // Aktualizacja sum
            totalBitsAfterTransmission += results.get(0);
            totalBitsAfterCorrection += results.get(1);
            totalMessageAfterTransmission += results.get(2);
            totalMessageAfterCorrection += results.get(3);
            totalPercentBitsCorrection += results.get(4);
            totalPercentCharsCorrection += results.get(5);
            totalPercentBitsError += results.get(6);
            totalPercentCharsError += results.get(7);
        }

        // Obliczenie średnich
        double avgBitsAfterTransmission = (double) totalBitsAfterTransmission / numSimulations;
        double avgBitsAfterCorrection = (double) totalBitsAfterCorrection / numSimulations;
        double avgMessageAfterTransmission = (double) totalMessageAfterTransmission / numSimulations;
        double avgMessageAfterCorrection = (double) totalMessageAfterCorrection / numSimulations;
        double avgPercentBitsCorrection = totalPercentBitsCorrection / numSimulations;
        double avgPercentCharsCorrection = totalPercentCharsCorrection / numSimulations;
        double avgPercentBitsError = totalPercentBitsError / numSimulations;
        double avgPercentCharsError = totalPercentCharsError / numSimulations;


        // Wyświetlenie średnich z zaokrągleniem do 4 miejsc po przecinku i usuwaniem nieznaczących zer
        System.out.printf("Average Swapped bits after Transmission: %.4g\n", avgBitsAfterTransmission);
        System.out.printf("Average Swapped bits after Correction: %.4g\n", avgBitsAfterCorrection);
        System.out.printf("Average Swapped chars after Transmission: %.4g\n", avgMessageAfterTransmission);
        System.out.printf("Average Swapped chars after Correction: %.4g\n", avgMessageAfterCorrection);
        System.out.printf("Average Percent bits correction: %.4g%%\n", avgPercentBitsCorrection);
        System.out.printf("Average Percent chars correction: %.4g%%\n", avgPercentCharsCorrection);
        System.out.printf("Average Percent bits error: %.4g%%\n", avgPercentBitsError);
        System.out.printf("Average Percent chars error: %.4g%%\n", avgPercentCharsError);
    }

    public static List<Double> simulate(String originalMsgString, String channel, double ber, double pOfErrorWhenGood, double pOfGoodToBad, double pOfErrorWhenBad, double pOfBadToGood) {
        // Zapisanie wiadomości do przesłania w bitach
        List<Integer> originalMsgBin = Hamming.strToBin(originalMsgString);

        // Enkodowanie wiadomości
        List<Integer> encodedMsgBin = Hamming.encode(originalMsgBin);

        // Przesyłanie wiadomości przez wybrany kanał
        List<Integer> transmittedMsgBin = transmit(channel, encodedMsgBin, ber, pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood);

        // Wiadomość jako String po transmisji przez kanał
        String transmittedMsgString = Hamming.binToStr(Hamming.decode(transmittedMsgBin));

        // Obliczenie syndromu błędu
        List<Integer> syndrome = Hamming.syndrome(transmittedMsgBin);

        // Korekcja błędów
        List<Integer> correctedMsgBin = Hamming.correct(syndrome, transmittedMsgBin);

        // Dekodowanie poprawionej wiadomości
        List<Integer> decodedMsgBin = Hamming.decode(correctedMsgBin);

        // Zapisanie wiadomości po dekodowaniu jako string
        String decodedMsgString = Hamming.binToStr(decodedMsgBin);

        // Analiza wyników
        return analyzeResults(originalMsgString, originalMsgBin, encodedMsgBin, transmittedMsgBin, transmittedMsgString, decodedMsgBin, decodedMsgString);
    }

    public static List<Integer> transmit(String channel, List<Integer> encodedMsg, double ber, double pOfErrorWhenGood, double pOfGoodToBad, double pOfErrorWhenBad, double pOfBadToGood) {
        switch (channel) {
            case "BSC":
                BSC bsc = new BSC(ber, new NumberGeneratorImpl());
                return Hamming.transmitBsc(encodedMsg, bsc);
            case "GE":
                GilbertElliot ge = new GilbertElliot(pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood, new NumberGeneratorImpl());
                return Hamming.transmitGe(encodedMsg, ge);
            case "null":
                return encodedMsg; // Bez transmisji
            default:
                return null;
        }
    }


    public static List<Double> analyzeResults(String originalMessage, List<Integer> binMessage, List<Integer> encodedMsgBin, List<Integer> transmittedMsgBin,
                                               String transmittedMsgString, List<Integer> decodedMsgBin, String decodedMsgString) {
        // Analiza bitów po transmisji
        int bitsAfterTransmission = countDifferentBits(encodedMsgBin, transmittedMsgBin);

        // Analiza bitów po korekcji
        int bitsAfterCorrection = countDifferentBits(binMessage, decodedMsgBin);

        // Analiza znaków po transmisji
        int messageAfterTransmission = countDifferentChars(originalMessage, transmittedMsgString);

        // Analiza znaków po korekcji
        int messageAfterCorrection = countDifferentChars(originalMessage, decodedMsgString);

        // Obliczenie procentowej poprawy dla bitów
        double percentBitsCorrection = ((double) bitsAfterTransmission - bitsAfterCorrection) / bitsAfterTransmission * 100;

        // Obliczenie procentowej poprawy dla znaków
        double percentCharsCorrection = ((double) messageAfterTransmission - messageAfterCorrection) / messageAfterTransmission * 100;

        // Obliczenie ile procent błędu zostało - ostateczny wynik dla bitów
        double percentBitsError = ((double) bitsAfterCorrection) / binMessage.size() * 100;

        // Obliczenie ile procent błędu zostało - ostateczny wynik dla znaków
        double percentCharsError = ((double) messageAfterCorrection) / originalMessage.length() * 100;

        // Zwrócenie wyników analizy
        List<Double> results = new ArrayList<>();
        results.add((double) bitsAfterTransmission);
        results.add((double) bitsAfterCorrection);
        results.add((double) messageAfterTransmission);
        results.add((double) messageAfterCorrection);
        results.add(percentBitsCorrection);
        results.add(percentCharsCorrection);
        results.add(percentBitsError);
        results.add(percentCharsError);
        return results;
    }


    public static int countDifferentBits(List<Integer> original, List<Integer> received) {
        int count = 0;
        for (int i = 0; i < original.size(); i++) {
            if (!original.get(i).equals(received.get(i))) {
                count++;
            }
        }
        return count;
    }


    public static int countDifferentChars(String original, String received) {
        int count = 0;
        for (int i = 0; i < Math.min(original.length(), received.length()); i++) {
            if (original.charAt(i) != received.charAt(i)) {
                count++;
            }
        }
        return count + Math.abs(original.length() - received.length());
    }
}
