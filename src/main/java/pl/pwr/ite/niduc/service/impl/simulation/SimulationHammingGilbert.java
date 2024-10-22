package pl.pwr.ite.niduc.service.impl.simulation;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;

import java.util.ArrayList;
import java.util.List;

public class SimulationHammingGilbert {

    public static void main(String[] args) {
        String message = "this is important information that should be kept secret";
        int numSimulations = 1;

        List<Double> avgPercentBitsCorrectionValues = new ArrayList<>();
        List<Double> avgPercentBitsAfterTransmissionValues = new ArrayList<>();

        double pGoodToBad = 0.05;
        double pBadToGood = 1.0;
        double pErrorWhenGood = 0.05;
        double pErrorWhenBad = 0.05;

        while(pGoodToBad <= 1.0 && pBadToGood >= 0.05 && pErrorWhenGood <= 1.0 && pErrorWhenBad <= 1.0) {

            // Inicjalizacja sum dla obliczenia średnich
            double totalPercentBitsAfterTransmission = 0;
            double totalPercentBitsCorrection = 0;

            for (int i = 0; i < numSimulations; i++) {
                List<Double> results = BaseSimulation.simulateHammingGilbert(message, pGoodToBad, pBadToGood, pErrorWhenGood, pErrorWhenBad);

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

            pGoodToBad += 0.05;
            pBadToGood -= 0.05;
            pErrorWhenGood += 0.05;
            pErrorWhenBad += 0.05;
        }

        // Wyświetlanie wykresu
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Wykres dla kodu Hamminga i kanalu Gilberta-Elliota")
                .xAxisTitle("Zawartosc bledu [%]").yAxisTitle("Zdolnosc korekcyjna [%]").build();

        chart.addSeries("Correction Capability", avgPercentBitsAfterTransmissionValues, avgPercentBitsCorrectionValues);

        new SwingWrapper<>(chart).displayChart();
    }
}
