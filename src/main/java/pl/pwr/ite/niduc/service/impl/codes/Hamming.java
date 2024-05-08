package pl.pwr.ite.niduc.service.impl.codes;

import pl.pwr.ite.niduc.service.impl.channels.BSC;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Hamming {

    public static List<Integer> strToBin(String string) {
        List<Integer> ret = new ArrayList<>();
        for (char c : string.toCharArray()) {
            String binary = Integer.toBinaryString(c);
            while (binary.length() < 7) {
                binary = "0" + binary;
            }
            for (char bit : binary.toCharArray()) {
                ret.add(Character.getNumericValue(bit));
            }
        }
        return ret;
    }

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


    public static List<Integer> encode(List<Integer> msg) {
        // Pad the message to length
        while (msg.size() % 4 != 0) {
            msg.add(0);
        }

        // Reshape the message into 4-bit chunks
        List<List<Integer>> msgChunks = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += 4) {
            List<Integer> chunk = new ArrayList<>(msg.subList(i, Math.min(i + 4, msg.size())));
            msgChunks.add(chunk);
        }

        // Create parity bits using transition matrix
        int[][] transition = {
                {1, 0, 0, 0, 0, 1, 1, 1},
                {0, 1, 0, 0, 1, 0, 1, 1},
                {0, 0, 1, 0, 1, 1, 0, 1},
                {0, 0, 0, 1, 1, 1, 1, 0}
        };

        List<Integer> result = new ArrayList<>();
        for (List<Integer> chunk : msgChunks) {
            int[] chunkArray = chunk.stream().mapToInt(Integer::intValue).toArray();
            int[] encodedChunk = new int[8];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 8; j++) {
                    encodedChunk[j] += chunkArray[i] * transition[i][j];
                }
            }

            // Mod 2 the matrix multiplication
            for (int i = 0; i < 8; i++) {
                encodedChunk[i] %= 2;
            }

            for (int bit : encodedChunk) {
                result.add(bit);
            }
        }
        return result;
    }


    public static List<Integer> syndrome(List<Integer> msg) {
        // Reshape the message into 8-bit chunks
        List<List<Integer>> msgChunks = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += 8) {
            List<Integer> chunk = new ArrayList<>(msg.subList(i, Math.min(i + 8, msg.size())));
            msgChunks.add(chunk);
        }

        // Create syndrome generation matrix
        int[][] transition = {
                {0, 1, 1, 1, 1, 0, 0, 0},
                {1, 0, 1, 1, 0, 1, 0, 0},
                {1, 1, 0, 1, 0, 0, 1, 0},
                {1, 1, 1, 0, 0, 0, 0, 1}
        };

        List<Integer> result = new ArrayList<>();
        for (List<Integer> chunk : msgChunks) {
            int[] chunkArray = chunk.stream().mapToInt(Integer::intValue).toArray();
            int[] syndrome = new int[4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 8; j++) {
                    syndrome[i] += chunkArray[j] * transition[i][j];
                }
            }

            // Mod 2 the matrix multiplication
            for (int i = 0; i < 4; i++) {
                syndrome[i] %= 2;
                result.add(syndrome[i]);
            }
        }
        return result;
    }

    public static List<Integer> correct(List<Integer> msg, List<Integer> syndrome) {
        // Reshape the message into 8-bit chunks
        List<List<Integer>> msgChunks = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += 8) {
            List<Integer> chunk = new ArrayList<>(msg.subList(i, Math.min(i + 8, msg.size())));
            msgChunks.add(chunk);
        }

        // Syndrom matrix
        int[][] transition = {
                {0, 1, 1, 1, 1, 0, 0, 0},
                {1, 0, 1, 1, 0, 1, 0, 0},
                {1, 1, 0, 1, 0, 0, 1, 0},
                {1, 1, 1, 0, 0, 0, 0, 1}
        };

        List<Integer> result = new ArrayList<>();
        for (int synd = 0; synd < syndrome.size() / 4; synd++) {
            // If the syndrome is all zeros, there is no error
            boolean allZeros = true;
            for (int i = 0; i < 4; i++) {
                if (syndrome.get(synd * 4 + i) != 0) {
                    allZeros = false;
                    break;
                }
            }
            if (allZeros) {
                for (int i = 0; i < 8; i++) {
                    result.add(msgChunks.get(synd).get(i));
                }
                continue;
            }

            // Find the column corresponding to the error in the transition matrix
            for (int col = 0; col < transition[0].length; col++) {
                boolean isEqual = true;
                for (int row = 0; row < transition.length; row++) {
                    if (transition[row][col] != syndrome.get(synd * 4 + row)) {
                        isEqual = false;
                        break;
                    }
                }
                if (isEqual) {
                    // Correct the error - tutaj trzeba dopisac kod ktory bedzie poprawnie zmienial bity na wlasciwe w razie bledow przy transmisji
                    for (int i = 0; i < 8; i++) {
//                        int currentVal = msgChunks.get(synd).get(col * 8 + i);
//                        int newVal = (currentVal + 1) % 2;
//                        result.add(newVal);

                    }
                }
            }
        }
        return result;
    }









    public static List<Integer> decode(List<Integer> msg) {
        // Reshape the message into 8-bit chunks
        List<List<Integer>> msgChunks = new ArrayList<>();
        for (int i = 0; i < msg.size(); i += 8) {
            List<Integer> chunk = new ArrayList<>(msg.subList(i, Math.min(i + 8, msg.size())));
            msgChunks.add(chunk);
        }

        // Define the matrix R
        int[][] r = {
                {1, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0}
        };

        List<Integer> result = new ArrayList<>();
        for (List<Integer> chunk : msgChunks) {
            // Perform matrix multiplication r * msg
            int[][] resArray = matrixMultiplication(r, transpose(chunk));

            // Convert the result back to a list of integers
            for (int i = 0; i < resArray.length; i++) {
                for (int j = 0; j < resArray[i].length; j++) {
                    result.add(resArray[i][j]);
                }
            }
        }

        // Remove any trailing zeros
        while (!result.isEmpty() && result.get(result.size() - 1) == 0) {
            result.remove(result.size() - 1);
        }

        return result;
    }




    // Function to perform matrix multiplication
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

    // Function to transpose a matrix represented as a list of lists
    public static int[][] transpose(List<Integer> matrix) {
        int cols = matrix.size();
        int[][] result = new int[cols][1];
        for (int i = 0; i < cols; i++) {
            result[i][0] = matrix.get(i);
        }
        return result;
    }

    public static List<Integer> transmit(List<Integer> encodedMsg, BSC bsc) {
        // Konwertuj listę na tablicę intów
        int[][] encodedArray = new int[1][encodedMsg.size()];
        for (int i = 0; i < encodedMsg.size(); i++) {
            encodedArray[0][i] = encodedMsg.get(i);
        }

        // Przekaż zakodowaną wiadomość do kanału BSC
        int[][] transmittedArray = bsc.transmit(encodedArray);

        // Konwertuj z powrotem na listę
        List<Integer> transmittedList = new ArrayList<>();
        for (int bit : transmittedArray[0]) {
            transmittedList.add(bit);
        }

        return transmittedList;
    }




    public static void main(String[] args) {
        String message = "this is a message we plan to hide";
        System.out.println("Original Message - string:");
        System.out.println(message);
        System.out.println();

        List<Integer> binMessage = strToBin(message);
        System.out.println("Original Message:");
        System.out.println(binMessage);
        System.out.println();

        // Encode the message using Hamming (8,4) code
        List<Integer> encodedMsg = encode(binMessage);
        System.out.println("Encoded Message:");
        System.out.println(encodedMsg);
        System.out.println();


//        // Inicjalizacja parametrów kanału BSC
//        double pOfError = 0.1;
//        double pOfBurst = 0.2;
//        double pOfCyclic = 0.1;
//        BSC bsc = new BSC(pOfError, pOfBurst, pOfCyclic, new NumberGeneratorImpl());
//        // Symulacja transmisji przez kanał BSC
//        List<Integer> transmittedMsg = transmit(encodedMsg, bsc);

        // Simulate transmission by introducing noise
        List<Integer> transmittedMsg = new ArrayList<>(encodedMsg); // Copy encoded message
        transmittedMsg.set(2, 0); // Introduce noise (this simulates transmission)

        System.out.println("Transmitted message:");
        System.out.println(transmittedMsg);
        System.out.println();

        System.out.println("Transmitted message:");
        System.out.println(binToStr(decode(transmittedMsg)));
        System.out.println();

        // Calculate error syndrome
        List<Integer> syndrome = syndrome(transmittedMsg.subList(0, encodedMsg.size() / 2));
        System.out.println("Error Syndrome:");
        System.out.println(syndrome);
        System.out.println();

        // Correct the error
        List<Integer> correctedMessage = correct(encodedMsg, syndrome);

        // Print the corrected message
        System.out.println("Corrected message:");
        System.out.println(correctedMessage);
        System.out.println();

        // Decode the corrected message
        List<Integer> decodedMessage = decode(transmittedMsg);

        // Print the decoded message
        System.out.println("Decoded message:");
        System.out.println(decodedMessage);
        System.out.println();

        // Convert binary list to string
        String decodedStr = binToStr(decodedMessage);
        System.out.println("Decoded message - string:");
        System.out.println(decodedStr);
        System.out.println();
    }
}
