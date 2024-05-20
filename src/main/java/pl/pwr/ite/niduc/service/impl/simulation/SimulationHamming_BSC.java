package pl.pwr.ite.niduc.service.impl.simulation;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;

import java.util.ArrayList;
import java.util.List;

public class SimulationHamming_BSC {

    public static void main(String[] args) {
        String message = "this is important information that should be kept secret";
        int numSimulations = 10;

        List<Double> avgPercentBitsCorrectionValues = new ArrayList<>();
        List<Double> avgPercentBitsAfterTransmissionValues = new ArrayList<>();

        for (double ber = 0.005; ber <= 0.1; ber += 0.005) {

            // Inicjalizacja sum dla obliczenia średnich
            double totalPercentBitsAfterTransmission = 0;
            double totalPercentBitsCorrection = 0;

            for (int i = 0; i < numSimulations; i++) {
                List<Double> results = BaseSimulation.simulateHamming(message, ber);

                totalPercentBitsAfterTransmission += results.get(0);
                totalPercentBitsCorrection += results.get(1);
            }

            double avgPercentBitsAfterTransmission = totalPercentBitsAfterTransmission / numSimulations;
            double avgPercentBitsCorrection = totalPercentBitsCorrection / numSimulations;

            avgPercentBitsAfterTransmissionValues.add(avgPercentBitsAfterTransmission);
            avgPercentBitsCorrectionValues.add(avgPercentBitsCorrection);

            System.out.println("Paremetr ber: " + ber);
            System.out.println("Procent bledu po transmisji: " + avgPercentBitsAfterTransmission + "%");
            System.out.println("Procent zdolnosci naprawiania bledow: " + avgPercentBitsCorrection + "%");
        }

        // Wyświetlanie wykresu
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Wykres dla kodu Hamminga i kanalu BSC")
                .xAxisTitle("Zawartosc bledu [%]").yAxisTitle("Zdolnosc korekcyjna [%]").build();

        chart.addSeries("Correction Capability", avgPercentBitsAfterTransmissionValues, avgPercentBitsCorrectionValues);

        new SwingWrapper<>(chart).displayChart();
    }
}
