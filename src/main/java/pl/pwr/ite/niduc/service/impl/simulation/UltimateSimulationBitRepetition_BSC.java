package pl.pwr.ite.niduc.service.impl.simulation;

import pl.pwr.ite.niduc.service.impl.codes.BitRepetition;
import pl.pwr.ite.niduc.service.impl.channels.BSC;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;

public class UltimateSimulationBitRepetition_BSC {

    public static void main(String[] args) {
        String message = "this is important information that should be kept secret";
        int numSimulations = 1;

        List<List<Double>> avgPercentBitsAfterTransmissionValuesList = new ArrayList<>();
        List<List<Double>> avgPercentBitsCorrectionValuesList = new ArrayList<>();

        for (int repetitionFactor = 10; repetitionFactor >= 1; repetitionFactor--) {
            List<Double> avgPercentBitsAfterTransmissionValues = new ArrayList<>();
            List<Double> avgPercentBitsCorrectionValues = new ArrayList<>();

            for (double ber = 0.05; ber <= 1.0; ber += 0.05) {

                // Inicjalizacja sum dla obliczenia średnich
                double totalPercentBitsAfterTransmission = 0;
                double totalPercentBitsCorrection = 0;

                for (int i = 0; i < numSimulations; i++) {
                    List<Double> results = simulate(message, repetitionFactor, ber);

                    totalPercentBitsAfterTransmission += results.get(0);
                    totalPercentBitsCorrection += results.get(1);
                }

                double avgPercentBitsAfterTransmission = totalPercentBitsAfterTransmission / numSimulations;
                double avgPercentBitsCorrection = totalPercentBitsCorrection / numSimulations;

                avgPercentBitsAfterTransmissionValues.add(avgPercentBitsAfterTransmission);
                avgPercentBitsCorrectionValues.add(avgPercentBitsCorrection);

                System.out.println("repetitionFactor: " + repetitionFactor);
                System.out.println("Paremetr ber: " + ber);
                System.out.println("Procent bledu po transmisji: " + avgPercentBitsAfterTransmission + "%");
                System.out.println("Procent zdolnosci naprawiania bledow: " + avgPercentBitsCorrection + "%");
                System.out.println();
            }

            avgPercentBitsAfterTransmissionValuesList.add(avgPercentBitsAfterTransmissionValues);
            avgPercentBitsCorrectionValuesList.add(avgPercentBitsCorrectionValues);
        }

        // Wyświetlanie wykresów
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Wykresy dla kodu powielania bitow i kanalu BSC")
                .xAxisTitle("Zawartosc bledu [%]").yAxisTitle("Zdolnosc korekcyjna [%]").build();

        for (int i = 0; i < avgPercentBitsAfterTransmissionValuesList.size(); i++) {
            List<Double> avgPercentBitsAfterTransmissionValues = avgPercentBitsAfterTransmissionValuesList.get(i);
            List<Double> avgPercentBitsCorrectionValues = avgPercentBitsCorrectionValuesList.get(i);

            String seriesName = "Repetition Factor " + (i + 1);
            chart.addSeries(seriesName, avgPercentBitsAfterTransmissionValues, avgPercentBitsCorrectionValues);
        }

        new SwingWrapper<>(chart).displayChart();
    }

    public static List<Double> simulate(String originalMsgString, int repetitionFactor, double ber) {
        List<Integer> originalMsgBin = BitRepetition.strToBin(originalMsgString);
        List<Integer> encodedMsgBin = BitRepetition.encode(originalMsgBin, repetitionFactor);
        List<Integer> transmittedMsgBin = transmit(encodedMsgBin, ber);
        List<Integer> decodedMsgBin = BitRepetition.decode(transmittedMsgBin, repetitionFactor);

        return analyzeResults(originalMsgBin, encodedMsgBin, transmittedMsgBin, decodedMsgBin);
    }

    public static List<Integer> transmit(List<Integer> encodedMsg, double ber) {
        BSC bsc = new BSC(ber, new NumberGeneratorImpl());
        return BitRepetition.transmitBsc(encodedMsg, bsc);

    }

    public static List<Double> analyzeResults(List<Integer> binMessage, List<Integer> encodedMsgBin,
                                              List<Integer> transmittedMsgBin, List<Integer> decodedMsgBin) {
        int bitsAfterTransmission = countDifferentBits(encodedMsgBin, transmittedMsgBin);
        double percentBitsAfterTransmission = (double) bitsAfterTransmission / encodedMsgBin.size() * 100;
        int bitsAfterCorrection = countDifferentBits(binMessage, decodedMsgBin);
        double percentBitsCorrection = ((double) bitsAfterTransmission - bitsAfterCorrection) / bitsAfterTransmission * 100;

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