import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import pl.pwr.ite.niduc.service.NumberGenerator;
import pl.pwr.ite.niduc.service.impl.generator.NumberGeneratorImpl;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class NumberGeneratorTest {

    private NumberGenerator numberGenerator;

    private final double[] constants = {
            0.4254, 0.2944, 0.2487, 0.2148, 0.1870, 0.1630, 0.1415,
            0.1219, 0.1036, 0.0862, 0.0697, 0.0537, 0.0381, 0.0227, 0.0076
    };

    @BeforeEach
    void setup() {
        numberGenerator = new NumberGeneratorImpl();
    }

    @RepeatedTest(100)
    public void numberGeneratorNormalDistributionTest() {
        int[] numbers = IntStream.generate(numberGenerator::nextInteger).limit(30).sorted().toArray();
       double calculatedSum = 0;
        var n = (int) Math.floor(numbers.length / 2);
        for(int i = 0; i < n; i++) {

            var a = numbers[i] - numbers[numbers.length - 1 - i];
            calculatedSum += constants[i] * a;
        }
        var w = calculateW(calculatedSum, numbers);

        assertTrue(w > 0.900);
    }

    private double calculateAverage(int[] numbers) {
        return Arrays.stream(numbers).average().orElse(0);
    }

    private double calculateW(double sum, int[] numbers) {
        return (sum*sum)/(calculateDeviation(calculateAverage(numbers), numbers));
    }

    private double calculateDeviation(double average, int[] numbers) {
        return Arrays.stream(numbers)
                .mapToDouble(i -> Math.pow(average - i, 2))
                .sum();
    }
}
