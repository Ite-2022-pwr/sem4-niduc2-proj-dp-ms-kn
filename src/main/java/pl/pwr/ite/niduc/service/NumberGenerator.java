package pl.pwr.ite.niduc.service;

public interface NumberGenerator {

    int nextInteger();

    int nextInteger(int min, int max);

    long getSeed();
}
