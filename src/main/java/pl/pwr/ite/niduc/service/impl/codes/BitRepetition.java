package pl.pwr.ite.niduc.service.impl.codes;

import pl.pwr.ite.niduc.service.impl.channels.BSC;
import pl.pwr.ite.niduc.service.impl.channels.GilbertElliot;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.ArrayList;
import java.util.List;

public class BitRepetition {

    // Metoda zamieniająca ciąg znaków na binarną listę
    public static List<Integer> strToBin(String string) {
        List<Integer> ret = new ArrayList<>();
        for (char c : string.toCharArray()) {
            String binary = Integer.toBinaryString(c);
            while (binary.length() < 7) {
                binary = "0" + binary; // Uzupełnienie zerami, aby uzyskać stałą długość bloku kodowego
            }
            for (char bit : binary.toCharArray()) {
                ret.add(Character.getNumericValue(bit));
            }
        }
        return ret;
    }


    // Metoda zamieniająca binarną listę na ciąg znaków
    public static String binToStr(List<Integer> binary) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < binary.size(); i += 7) {
            StringBuilder binaryChar = new StringBuilder();
            for (int j = i; j < i + 7; j++) {
                if (j < binary.size()) {
                    binaryChar.append(binary.get(j));
                }
            }
            int charCode = Integer.parseInt(binaryChar.toString(), 2);
            output.append((char) charCode);
        }
        return output.toString();
    }


    // Metoda kodująca wiadomość przy użyciu kodu powielania bitów
    public static List<Integer> encode(List<Integer> msg, int repetitionFactor) {
        List<Integer> encodedMsg = new ArrayList<>();
        for (int bit : msg) {
            // Powielenie każdego bitu wiadomości
            for (int i = 0; i < repetitionFactor; i++) {
                encodedMsg.add(bit);
            }
        }
        return encodedMsg;
    }


    // Metoda dekodująca zakodowaną wiadomość przy użyciu kodu powielania bitów
    public static List<Integer> decode(List<Integer> msg, int repetitionFactor) {
        List<Integer> decodedMsg = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += repetitionFactor) {
            int zeros = 0;
            int ones = 0;
            for (int j = i; j < i + repetitionFactor; j++) {
                if (j < msg.size()) {
                    if (msg.get(j) == 0) {
                        zeros++;
                    } else {
                        ones++;
                    }
                }
            }
            // Sprawdzenie większości bitów
            int decodedBit = (zeros > ones) ? 0 : 1;
            decodedMsg.add(decodedBit);
        }
        return decodedMsg;
    }



    // Metoda przekazująca zakodowaną wiadomość przez kanał BSC
    public static List<Integer> transmitBsc(List<Integer> encodedMsg, BSC bsc) {
        // Konwersja listy na tablicę intów
        int[][] encodedArray = new int[1][encodedMsg.size()];
        for (int i = 0; i < encodedMsg.size(); i++) {
            encodedArray[0][i] = encodedMsg.get(i);
        }

        // Przekazanie zakodowanej wiadomości do kanału BSC
        int[][] transmittedArray = bsc.transmit(encodedArray);

        // Konwersja z powrotem do listy
        List<Integer> transmittedList = new ArrayList<>();
        for (int bit : transmittedArray[0]) {
            transmittedList.add(bit);
        }

        return transmittedList;
    }


    // Metoda przekazująca zakodowaną wiadomość przez kanał Gilberta-Elliota
    public static List<Integer> transmitGe(List<Integer> encodedMsg, GilbertElliot ge) {
        // Konwersja listy na tablicę intów
        int[][] encodedArray = new int[1][encodedMsg.size()];
        for (int i = 0; i < encodedMsg.size(); i++) {
            encodedArray[0][i] = encodedMsg.get(i);
        }

        // Przekazanie zakodowanej wiadomości do kanału Gilberta-Elliota
        int[][] transmittedArray = ge.transmit(encodedArray);

        // Konwersja z powrotem do listy
        List<Integer> transmittedList = new ArrayList<>();
        for (int bit : transmittedArray[0]) {
            transmittedList.add(bit);
        }

        return transmittedList;
    }

    public static void main(String[] args) {
        // Parameter powtarzania bitów podczas enkodowania
        int repetitonFactor = 3;

        // Wiadomość do przesłania
        //String message = "this is important information that should be kept secret";
        String message = "a";
        System.out.println("Original Message - string:");
        System.out.println(message);
        System.out.println();

        // Wiadomość do przesłania w bitach
        List<Integer> binMessage = strToBin(message);
        System.out.println("Original Message:");
        System.out.println(binMessage);
        System.out.println();

        // Zakodowanie wiadomości za pomocą kodu Hamminga (7,4)
        List<Integer> encodedMsg = encode(binMessage, repetitonFactor);
        System.out.println("Encoded Message:");
        System.out.println(encodedMsg);
        System.out.println();

        // Inicjalizacja parametrów kanału
        double ber = 1.0;
        double pOfErrorWhenGood = 0.01;
        double pOfGoodToBad = 0.1;
        double pOfErrorWhenBad = 0.1;
        double pOfBadToGood = 0.8;

        // Symulacja transmisji przez wybrany kanał
        String channel = "BSC";
        List<Integer> transmittedMsg = switch (channel) {
            case "BSC" -> {
                // Symulacja transmisji przez kanał BSC
                BSC bsc = new BSC(ber, new NumberGeneratorImpl());
                yield transmitBsc(encodedMsg, bsc);
            }
            case "GE" -> {
                // Symulacja transmisji przez kanał Gilberta-Elliota
                GilbertElliot ge = new GilbertElliot(pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood, new NumberGeneratorImpl());
                yield transmitGe(encodedMsg, ge);
            }
            case "null" -> encodedMsg;
            default -> null;
        };

        // Wiadomość po transmisji
        System.out.println("Transmitted message:");
        System.out.println(transmittedMsg);
        System.out.println();

        // Dekodowanie wiadomości połączone z poprawianiem jej
        List<Integer> decodedMessage = decode(transmittedMsg, repetitonFactor);

        // Wydrukowanie zdekodowanej wiadomości
        System.out.println("Decoded and corrected message:");
        System.out.println(decodedMessage);
        System.out.println();

        // Konwersja listy binarnej na ciąg znaków - ostateczna wiadomość końcowa
        String decodedStr = binToStr(decodedMessage);
        System.out.println("Decoded message - string:");
        System.out.println(decodedStr);
        System.out.println();

        int bitsAfterTransmission = countDifferentBits(encodedMsg, transmittedMsg);
        System.out.println("bits after transmition = " + bitsAfterTransmission);
        double percentBitsAfterTransmission = (double) bitsAfterTransmission / encodedMsg.size() * 100;
        System.out.println("after transmission = " + percentBitsAfterTransmission);

        List<Integer> encodedDecoded = encode(decodedMessage, repetitonFactor);
        int bitsAfterCorrection = countDifferentBits(encodedDecoded, encodedMsg);
        System.out.println("bits after correction = " + bitsAfterCorrection);
        System.out.println(encodedDecoded);
        double percentBitsCorrection = ((double) bitsAfterTransmission - (double) bitsAfterCorrection) / (double) bitsAfterTransmission * 100;
        System.out.println(percentBitsCorrection);
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
}
