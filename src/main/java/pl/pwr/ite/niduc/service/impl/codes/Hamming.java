package pl.pwr.ite.niduc.service.impl.codes;

import pl.pwr.ite.niduc.service.impl.channels.BSC;
import pl.pwr.ite.niduc.service.impl.channels.GilbertElliot;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.ArrayList;
import java.util.List;

public class Hamming {

    // Definicja stałych dla kodu Hamminga(7,4)
    public static final int MESSAGE_BITS = 8; // Całkowita liczba bitów w wiadomości
    public static final int DATA_BITS = 4; // Liczba bitów danych
    public static final int PARITY_BITS = 3; // Liczba bitów parzystości
    public static final int CODE_BITS = DATA_BITS + PARITY_BITS; // Całkowita liczba bitów w jednym bloku kodowym


    // Metoda zamieniająca ciąg znaków na binarną listę
    public static List<Integer> strToBin(String string) {
        List<Integer> ret = new ArrayList<>();
        for (char c : string.toCharArray()) {
            String binary = Integer.toBinaryString(c);
            while (binary.length() < CODE_BITS) {
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
        for (int i = 0; i < binary.size(); i += CODE_BITS) {
            StringBuilder binaryChar = new StringBuilder();
            for (int j = i; j < i + CODE_BITS; j++) {
                if (j < binary.size()) {
                    binaryChar.append(binary.get(j));
                }
            }
            int charCode = Integer.parseInt(binaryChar.toString(), 2);
            output.append((char) charCode);
        }
        return output.toString();
    }


    // Metoda kodująca wiadomość przy użyciu kodu Hamminga(7,4)
    public static List<Integer> encode(List<Integer> msg) {
        // Wyrównanie długości wiadomości do wielokrotności liczby bitów danych
        while (msg.size() % DATA_BITS != 0) {
            msg.add(0);
        }

        // Podział wiadomości na bloki danych
        List<List<Integer>> msgChunks = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += DATA_BITS) {
            List<Integer> chunk = new ArrayList<>(msg.subList(i, Math.min(i + DATA_BITS, msg.size())));
            msgChunks.add(chunk);
        }

        // Macierz przejścia dla generowania bitów parzystości dla Hamminga(7,4)
        int[][] transition = new int[][]{
                {1, 0, 0, 0, 0, 1, 1, 1},
                {0, 1, 0, 0, 1, 0, 1, 1},
                {0, 0, 1, 0, 1, 1, 0, 1},
                {0, 0, 0, 1, 1, 1, 1, 0}
        };

        List<Integer> result = new ArrayList<>();
        for (List<Integer> chunk : msgChunks) {
            int[] chunkArray = chunk.stream().mapToInt(Integer::intValue).toArray();
            int[] encodedChunk = new int[MESSAGE_BITS];
            for (int i = 0; i < DATA_BITS; i++) {
                for (int j = 0; j < MESSAGE_BITS; j++) {
                    encodedChunk[j] += chunkArray[i] * transition[i][j];
                }
            }

            // Obliczenie reszty z dzielenia przez 2, aby uzyskać wartości binarne
            for (int i = 0; i < MESSAGE_BITS; i++) {
                encodedChunk[i] %= 2;
            }

            for (int bit : encodedChunk) {
                result.add(bit);
            }
        }
        return result;
    }


    // Metoda generująca syndrom
    public static List<Integer> syndrome(List<Integer> msg) {
        // Podział wiadomości na bloki danych
        List<List<Integer>> msgChunks = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += MESSAGE_BITS) {
            List<Integer> chunk = new ArrayList<>(msg.subList(i, Math.min(i + MESSAGE_BITS, msg.size())));
            msgChunks.add(chunk);
        }

        // Macierz przejścia dla generowania syndromu dla Hamminga(7,4)
        int[][] transition = new int[][] {
                {0, 1, 1, 1, 1, 0, 0, 0},
                {1, 0, 1, 1, 0, 1, 0, 0},
                {1, 1, 0, 1, 0, 0, 1, 0},
                {1, 1, 1, 0, 0, 0, 0, 1}
        };

        List<Integer> result = new ArrayList<>();
        for (List<Integer> chunk : msgChunks) {
            int[] chunkArray = chunk.stream().mapToInt(Integer::intValue).toArray();
            int[] syndrome = new int[DATA_BITS];
            for (int i = 0; i < DATA_BITS; i++) {
                for (int j = 0; j < MESSAGE_BITS; j++) {
                    syndrome[i] += chunkArray[j] * transition[i][j];
                }
            }

            // Obliczenie reszty z dzielenia przez 2, aby uzyskać wartości binarne
            for (int i = 0; i < DATA_BITS; i++) {
                syndrome[i] %= 2;
                result.add(syndrome[i]);
            }
        }
        return result;
    }

    // Metoda poprawiająca błędy w wiadomości
    private static List<Integer> correct(List<Integer> syndrome, List<Integer> msg) {
        // Podział wiadomości na bloki danych
        int[][] msgChunks = new int[(msg.size() + CODE_BITS) / MESSAGE_BITS][MESSAGE_BITS];
        for (int i = 0; i < msg.size(); i++) {
            msgChunks[i / MESSAGE_BITS][i % MESSAGE_BITS] = msg.get(i);
        }

        // Podział syndromu na bloki danych
        int[][] syndromeChunks = new int[(syndrome.size() + PARITY_BITS) / DATA_BITS][DATA_BITS];
        for (int i = 0; i < syndrome.size(); i++) {
            syndromeChunks[i / DATA_BITS][i % DATA_BITS] = syndrome.get(i);
        }

        // Macierz przejścia dla generowania syndromu dla Hamminga(7,4)
        int[][] transition = new int[][] {
                {0, 1, 1, 1, 1, 0, 0, 0},
                {1, 0, 1, 1, 0, 1, 0, 0},
                {1, 1, 0, 1, 0, 0, 1, 0},
                {1, 1, 1, 0, 0, 0, 0, 1}

        };

        int change;

        for(int x = 0; x < (syndromeChunks.length); x++) {
            for(int y = 0; y < MESSAGE_BITS; y++) {
                change = 0;
                for(int z = 0; z < syndromeChunks[0].length; z++) {
                    if(transition[z][y] == syndromeChunks[x][z]) {
                        change++;
                    }
                }
                if  (change == transition.length) {
                    int currentVal = msgChunks[x][y];
                    int newVal = (currentVal + 1) % 2;
                    msgChunks[x][y] = newVal;
                }
            }
        }

        // Konwersja poprawionych bloków wiadomości z powrotem na listę
        List<Integer> correctedMsg = new ArrayList<>();
        for (int[] msgChunk : msgChunks) {
            for (int i : msgChunk) {
                correctedMsg.add(i);
            }
        }
        return correctedMsg;
    }


    // Metoda dekodująca zakodowaną wiadomość
    public static List<Integer> decode(List<Integer> msg) {
        // Podział wiadomości na bloki danych
        List<List<Integer>> msgChunks = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += MESSAGE_BITS) {
            List<Integer> chunk = new ArrayList<>(msg.subList(i, Math.min(i + MESSAGE_BITS, msg.size())));
            msgChunks.add(chunk);
        }

        // Definicja macierzy R dla Hamminga(7,4)
        int[][] r = new int[][] {
                {1, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0}
        };

        List<Integer> result = new ArrayList<>();
        for (List<Integer> chunk : msgChunks) {
            // Wykonanie mnożenia macierzowego r * msg
            int[][] resArray = matrixMultiplication(r, transpose(chunk));

            // Konwersja wyniku z powrotem do listy liczb całkowitych
            for (int[] ints : resArray) {
                for (int anInt : ints) {
                    result.add(anInt);
                }
            }
        }

        return result;
    }


    // Funkcja wykonująca mnożenie macierzowe
    public static int[][] matrixMultiplication(int[][] A, int[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        int[][] C = new int[rowsA][colsB];
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }


    // Funkcja transponująca macierz reprezentowaną jako lista list
    public static int[][] transpose(List<Integer> matrix) {
        int cols = matrix.size();
        int[][] result = new int[cols][1];
        for (int i = 0; i < cols; i++) {
            result[i][0] = matrix.get(i);
        }
        return result;
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
        // Wiadomość do przesłania
        String message = "this is important information that should be kept secret";
        System.out.println("Original Message - string:");
        System.out.println(message);
        System.out.println();

        // Wiadomość do przesłania w bitach
        List<Integer> binMessage = strToBin(message);
        System.out.println("Original Message:");
        System.out.println(binMessage);
        System.out.println();

        // Zakodowanie wiadomości za pomocą kodu Hamminga (7,4)
        List<Integer> encodedMsg = encode(binMessage);
        System.out.println("Encoded Message:");
        System.out.println(encodedMsg);
        System.out.println();

        // Inicjalizacja parametrów kanału
        double pOfError = 0.02;
        double pOfBurst = 0.02;
        double pOfCyclic = 0.02;

        // Symulacja transmisji przez wybrany kanał
        String channel = "GE";
        List<Integer> transmittedMsg = switch (channel) {
            case "BSC" -> {
                // Symulacja transmisji przez kanał BSC
                BSC bsc = new BSC(pOfError, pOfBurst, pOfCyclic, new NumberGeneratorImpl());
                yield transmitBsc(encodedMsg, bsc);
            }
            case "GE" -> {
                // Symulacja transmisji przez kanał Gilberta-Elliota
                GilbertElliot ge = new GilbertElliot(pOfError, pOfBurst, pOfCyclic, new NumberGeneratorImpl());
                yield transmitGe(encodedMsg, ge);
            }
            case "null" -> encodedMsg;
            default -> null;
        };

        // Wiadomość po transmisji
        System.out.println("Transmitted message:");
        System.out.println(transmittedMsg);
        System.out.println();

        // Dekodowanie zakodowanej wiadomości po transmisji
        System.out.println("Decoded message:");
        System.out.println(binToStr(decode(transmittedMsg)));
        System.out.println();

        // Obliczenie syndromu błędu
        List<Integer> syndrome = syndrome(transmittedMsg.subList(0, transmittedMsg.size()));
        System.out.println("Error Syndrome:");
        System.out.println(syndrome);
        System.out.println();

        // Korekcja błędów
        List<Integer> correctedMessage = correct(syndrome, transmittedMsg);

        // Wydrukowanie skorygowanej wiadomości
        System.out.println("Corrected message:");
        System.out.println(correctedMessage);
        System.out.println();

        // Dekodowanie poprawionej wiadomości
        List<Integer> decodedMessage = decode(correctedMessage);

        // Wydrukowanie zdekodowanej wiadomości
        System.out.println("Decoded message:");
        System.out.println(decodedMessage);
        System.out.println();

        // Konwersja listy binarnej na ciąg znaków - ostateczna wiadomość końcowa
        String decodedStr = binToStr(decodedMessage);
        System.out.println("Decoded message - string:");
        System.out.println(decodedStr);
        System.out.println();
    }
}
