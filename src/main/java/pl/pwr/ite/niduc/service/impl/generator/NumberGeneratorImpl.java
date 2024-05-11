package pl.pwr.ite.niduc.service.impl.generator;

import pl.pwr.ite.niduc.service.NumberGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

public class NumberGeneratorImpl implements NumberGenerator {

    private final long modulus = 7829;
    private final long multiplier = 378;
    private final long increment = 2310;
    private long seed;

    public NumberGeneratorImpl() {
        this.seed = generateSeed("C:\\Users\\Gosia\\Documents\\photos\\flowers.JPG");
    }

    @Override
    public int nextInteger() {
        return this.nextInteger(0, 1000);
    }

    @Override
    public int nextInteger(int min, int max) {
        long oldSeed, newSeed;
        oldSeed = this.seed;
        newSeed = (multiplier * oldSeed + increment) % modulus;
        this.seed = newSeed;
        return (int) (newSeed + min) % max;
    }

    protected long generateSeed(String sourcePath) {
        try {
           var buffer = ImageIO.read(new File(sourcePath));
           int imageWidth = buffer.getWidth();
           int imageHeight = buffer.getHeight();
           long pixelSum = 0;

           for(int row = 0; row < imageHeight; row++) {
               for(int column = 0; column < imageWidth; column++) {
                   var pixelColor = new Color(buffer.getRGB(column, row));
                   pixelSum += pixelColor.getRed() + pixelColor.getGreen() + pixelColor.getBlue();
               }
           }
           return System.currentTimeMillis() % pixelSum;
        } catch (IOException ex) {
            throw new IllegalArgumentException(String.format("File with path %s not found.", sourcePath));
        }
    }

    @Override
    public long getSeed() {
        return seed;
    }
}
