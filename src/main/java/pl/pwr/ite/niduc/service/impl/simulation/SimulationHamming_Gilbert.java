package pl.pwr.ite.niduc.service.impl.simulation;

import pl.pwr.ite.niduc.service.impl.channels.GilbertElliot;
import pl.pwr.ite.niduc.service.impl.codes.BitRepetition;
import pl.pwr.ite.niduc.service.impl.codes.Hamming;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;

public class SimulationHamming_Gilbert {

    public static void main(String[] args) {
        String message = "this is important information that should be kept secret";
        int numSimulations = 10;

        List<Double> avgPercentBitsCorrectionValues = new ArrayList<>();
        List<Double> avgPercentBitsAfterTransmissionValues = new ArrayList<>();

        double pGoodToBad = 0.005;
        double pBadToGood = 0.1;
        double pErrorWhenGood = 0.005;
        double pErrorWhenBad = 0.005;

        while(pGoodToBad <= 0.1 && pBadToGood >= 0.005 && pErrorWhenGood <= 0.1 && pErrorWhenBad <= 0.1) {

            // Inicjalizacja sum dla obliczenia średnich
            double totalPercentBitsAfterTransmission = 0;
            double totalPercentBitsCorrection = 0;

            for (int i = 0; i < numSimulations; i++) {
                List<Double> results = simulate(message, pGoodToBad, pBadToGood, pErrorWhenGood, pErrorWhenBad);

                totalPercentBitsAfterTransmission += results.get(0);
                totalPercentBitsCorrection += results.get(1);
            }
            double avgPercentBitsAfterTransmission = totalPercentBitsAfterTransmission / numSimulations;
            double avgPercentBitsCorrection = totalPercentBitsCorrection / numSimulations;

            avgPercentBitsAfterTransmissionValues.add(avgPercentBitsAfterTransmission);
            avgPercentBitsCorrectionValues.add(avgPercentBitsCorrection);

            System.out.println("Paremetr bledow: " + pGoodToBad + ", " + pBadToGood + ", " + pErrorWhenGood + ", " + pErrorWhenBad);
            System.out.println("Procent bledu po transmisji: " + avgPercentBitsAfterTransmission + "%");
            System.out.println("Procent zdolnosci naprawiania bledow: " + avgPercentBitsCorrection + "%");
            System.out.println();

            pGoodToBad += 0.005;
            pBadToGood -= 0.005;
            pErrorWhenGood += 0.005;
            pErrorWhenBad += 0.005;
        }

        // Wyświetlanie wykresu
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Wykres dla kodu Hamminga i kanalu Gilberta-Elliota")
                .xAxisTitle("Zawartosc bledu [%]").yAxisTitle("Zdolnosc korekcyjna [%]").build();

        chart.addSeries("Correction Capability", avgPercentBitsAfterTransmissionValues, avgPercentBitsCorrectionValues);

        new SwingWrapper<>(chart).displayChart();
    }

    public static List<Double> simulate(String originalMsgString, double pGoodToBad, double pBadToGood, double pErrorWhenGood, double pErrorWhenBad) {
        List<Integer> originalMsgBin = Hamming.strToBin(originalMsgString);
        List<Integer> encodedMsgBin = Hamming.encode(originalMsgBin);
        List<Integer> transmittedMsgBin = transmit(encodedMsgBin, pGoodToBad, pBadToGood, pErrorWhenGood, pErrorWhenBad);
        List<Integer> syndrome = Hamming.syndrome(transmittedMsgBin);
        List<Integer> correctedMsgBin = Hamming.correct(syndrome, transmittedMsgBin);
        List<Integer> decodedMsgBin = Hamming.decode(correctedMsgBin);

        return analyzeResults(originalMsgBin, encodedMsgBin, transmittedMsgBin, decodedMsgBin);
    }

    public static List<Integer> transmit(List<Integer> encodedMsg, double pOfGoodToBad, double pOfBadToGood, double pOfErrorWhenGood, double pOfErrorWhenBad) {
        GilbertElliot gilbertElliot = new GilbertElliot(pOfErrorWhenGood, pOfGoodToBad, pOfErrorWhenBad, pOfBadToGood, new NumberGeneratorImpl());
        return BitRepetition.transmitGe(encodedMsg, gilbertElliot);

    }

    public static List<Double> analyzeResults(List<Integer> binMessage, List<Integer> encodedMsgBin,
                                              List<Integer> transmittedMsgBin, List<Integer> decodedMsgBin) {
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
