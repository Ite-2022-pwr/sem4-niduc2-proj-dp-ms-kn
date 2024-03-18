package pl.pwr.ite.niduc;

import pl.pwr.ite.niduc.service.NumberGenerator;
import pl.pwr.ite.niduc.service.impl.NumberGeneratorImpl;

public class Main {

    private static NumberGenerator numberGenerator = new NumberGeneratorImpl();

    public static void main(String[] args) {
        for(int i = 0; i < 100; i++) {
            System.out.println(numberGenerator.nextInteger(5, 100));
        }
    }
}