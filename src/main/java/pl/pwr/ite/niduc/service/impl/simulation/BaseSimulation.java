package pl.pwr.ite.niduc.service.impl.simulation;

import pl.pwr.ite.niduc.service.impl.codes.BitRepetition;
import pl.pwr.ite.niduc.service.impl.codes.Hamming;
import pl.pwr.ite.niduc.service.impl.channels.BSC;
import pl.pwr.ite.niduc.service.impl.channels.GilbertElliot;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.ArrayList;
import java.util.List;

import static pl.pwr.ite.niduc.service.impl.codes.BitRepetition.encode;

public class BaseSimulation {

    public static List<Double> simulateBitRepetitionBSC(String originalMsgString, int repetitionFactor, double ber) {
        List<Integer> originalMsgBin = BitRepetition.strToBin(originalMsgString);
        List<Integer> encodedMsgBin = BitRepetition.encode(originalMsgBin, repetitionFactor);
        List<Integer> transmittedMsgBin = transmitBSC(encodedMsgBin, ber);
        List<Integer> decodedMsgBin = BitRepetition.decode(transmittedMsgBin, repetitionFactor);

        return analyzeResults(originalMsgBin, encodedMsgBin, transmittedMsgBin, decodedMsgBin);
    }

    public static List<Double> simulateBitRepetitionGilbert(String originalMsgString, int repetitionFactor, double pGoodToBad, double pBadToGood, double pErrorWhenGood, double pErrorWhenBad) {
        List<Integer> originalMsgBin = BitRepetition.strToBin(originalMsgString);
        List<Integer> encodedMsgBin = BitRepetition.encode(originalMsgBin, repetitionFactor);
        List<Integer> transmittedMsgBin = transmitGE(encodedMsgBin, pGoodToBad, pBadToGood, pErrorWhenGood, pErrorWhenBad);
        List<Integer> decodedMsgBin = BitRepetition.decode(transmittedMsgBin, repetitionFactor);

        return analyzeResults(originalMsgBin, encodedMsgBin, transmittedMsgBin, decodedMsgBin);
    }

    public static List<Double> simulateHamming(String originalMsgString, double ber) {
        List<Integer> originalMsgBin = Hamming.strToBin(originalMsgString);
        List<Integer> encodedMsgBin = Hamming.encode(originalMsgBin);
        List<Integer> transmittedMsgBin = transmitBSC(encodedMsgBin, ber);
        List<Integer> syndrome = Hamming.syndrome(transmittedMsgBin);
        List<Integer> correctedMsgBin = Hamming.correct(syndrome, transmittedMsgBin);
        List<Integer> decodedMsgBin = Hamming.decode(correctedMsgBin);

        return analyzeResults(originalMsgBin, encodedMsgBin, transmittedMsgBin, decodedMsgBin);
    }

    public static List<Double> simulateHammingGilbert(String originalMsgString, double pGoodToBad, double pBadToGood, double pErrorWhenGood, double pErrorWhenBad) {
        List<Integer> originalMsgBin = Hamming.strToBin(originalMsgString);
        List<Integer> encodedMsgBin = Hamming.encode(originalMsgBin);
        List<Integer> transmittedMsgBin = transmitGE(encodedMsgBin, pGoodToBad, pBadToGood, pErrorWhenGood, pErrorWhenBad);
        List<Integer> syndrome = Hamming.syndrome(transmittedMsgBin);
        List<Integer> correctedMsgBin = Hamming.correct(syndrome, transmittedMsgBin);
        List<Integer> decodedMsgBin = Hamming.decode(correctedMsgBin);

        return analyzeResults(originalMsgBin, encodedMsgBin, transmittedMsgBin, decodedMsgBin);
    }

    private static List<Integer> transmitBSC(List<Integer> encodedMsg, double ber) {
        BSC bsc = new BSC(ber, new NumberGeneratorImpl());
        return Hamming.transmitBsc(encodedMsg, bsc);
    }

    private static List<Integer> transmitGE(List<Integer> encodedMsg, double pGoodToBad, double pBadToGood, double pErrorWhenGood, double pErrorWhenBad) {
        GilbertElliot gilbertElliot = new GilbertElliot(pErrorWhenGood, pGoodToBad, pErrorWhenBad, pBadToGood, new NumberGeneratorImpl());
        return BitRepetition.transmitGe(encodedMsg, gilbertElliot);
    }

    private static List<Double> analyzeResults(List<Integer> binMessage, List<Integer> encodedMsgBin, List<Integer> transmittedMsgBin, List<Integer> decodedMsgBin) {

        int bitsAfterTransmission = countDifferentBits(encodedMsgBin, transmittedMsgBin);
        double percentBitsAfterTransmission = (double) bitsAfterTransmission / encodedMsgBin.size() * 100;

        List<Integer> encodedDecoded = Hamming.encode(decodedMsgBin);
        int bitsAfterCorrection = countDifferentBits(encodedDecoded, encodedMsgBin);
        double percentBitsCorrection = ((double) bitsAfterTransmission - (double) bitsAfterCorrection) / (double) bitsAfterTransmission * 100;

        List<Double> results = new ArrayList<>();
        results.add(percentBitsAfterTransmission);
        results.add(percentBitsCorrection);
        return results;
    }

    private static int countDifferentBits(List<Integer> original, List<Integer> received) {
        int count = 0;
        for (int i = 0; i < original.size(); i++) {
            if (!original.get(i).equals(received.get(i))) {
                count++;
            }
        }
        return count;
    }
}
