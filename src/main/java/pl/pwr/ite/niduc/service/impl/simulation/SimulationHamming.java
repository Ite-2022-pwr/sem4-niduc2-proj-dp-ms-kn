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
        double pOfErrorWhenGood = 0.1;
        double pOfGoodToBad = 0.2;
        double pOfErrorWhenBad = 0.3;
        double pOfBadToGood = 0.4;

        // Inicjalizacja sum dla obliczenia średnich
        int totalBitsAfterTransmission = 0;
        int totalBitsAfterCorrection = 0;
        int totalMessageAfterTransmission = 0;
        int totalMessageAfterCorrection = 0;
        double totalPercentBitsCorrection = 0;
        double totalPercentCharsCorrection = 0;

        // Powtórzenie symulacji
        for (int i = 0; i < numSimulations; i++) {
            // Symulacja
            List<Integer> results = simulate(message, channel, ber, pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood);

            // Aktualizacja sum
            totalBitsAfterTransmission += results.get(0);
            totalBitsAfterCorrection += results.get(1);
            totalMessageAfterTransmission += results.get(2);
            totalMessageAfterCorrection += results.get(3);
            totalPercentBitsCorrection += results.get(4);
            totalPercentCharsCorrection += results.get(5);
        }

        // Obliczenie średnich
        double avgBitsAfterTransmission = (double) totalBitsAfterTransmission / numSimulations;
        double avgBitsAfterCorrection = (double) totalBitsAfterCorrection / numSimulations;
        double avgMessageAfterTransmission = (double) totalMessageAfterTransmission / numSimulations;
        double avgMessageAfterCorrection = (double) totalMessageAfterCorrection / numSimulations;
        double avgPercentBitsCorrection = totalPercentBitsCorrection / numSimulations;
        double avgPercentCharsCorrection = totalPercentCharsCorrection / numSimulations;

        // Wyświetlenie średnich
        System.out.println("Average Swapped bits after Transmission: " + avgBitsAfterTransmission);
        System.out.println("Average Swapped bits after Correction: " + avgBitsAfterCorrection);
        System.out.println("Average Swapped chars after Transmission: " + avgMessageAfterTransmission);
        System.out.println("Average Swapped chars after Correction: " + avgMessageAfterCorrection);
        System.out.printf("Average Percent bits correction: %.2f%%\n", avgPercentBitsCorrection);
        System.out.printf("Average Percent chars correction: %.2f%%\n", avgPercentCharsCorrection);
    }

    public static List<Integer> simulate(String originalMsgString, String channel, double ber, double pOfErrorWhenGood, double pOfGoodToBad, double pOfErrorWhenBad, double pOfBadToGood) {
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
        return analyzeResults(originalMsgString, originalMsgBin, transmittedMsgBin, transmittedMsgString, correctedMsgBin, decodedMsgBin, decodedMsgString);
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


    public static List<Integer> analyzeResults(String originalMessage, List<Integer> binMessage, List<Integer> transmittedMsgBin,
                                               String transmittedMsgString, List<Integer> correctedMsgBin, List<Integer> decodedMsgBin, String decodedMsgString) {
        // Analiza bitów po transmisji
        int bitsAfterTransmission = countDifferentBits(binMessage, transmittedMsgBin);

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

        // Zwrócenie wyników analizy
        List<Integer> results = new ArrayList<>();
        results.add(bitsAfterTransmission);
        results.add(bitsAfterCorrection);
        results.add(messageAfterTransmission);
        results.add(messageAfterCorrection);
        results.add((int) percentBitsCorrection);
        results.add((int) percentCharsCorrection);
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
