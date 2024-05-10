package pl.pwr.ite.niduc.service.impl.codes.bch;

import pl.pwr.ite.niduc.service.impl.NumberGeneratorImpl;
import pl.pwr.ite.niduc.service.impl.channels.GilbertElliot;
import java.util.*;

public class BCH {
    char gen;
    public BCH(char gen) {
        this.gen = gen;
    }

    public int[] gaussianElimination(int[][] A, int[] c, int k) {
        int[] m = new int[k];
        for(int i = 0; i < k; i++) {
            GF257 a = new GF257(A[i][i]);
            GF257 inv = a.getInverse();
            for(int j = i+1; j < k; j++) {

                GF257 b = new GF257(A[j][i]);
                if(A[j][i] != 0) b = new GF257(257 - A[j][i]);
                b = b.mult(inv);
                for(int p = 0; p < k; p++) {
                    GF257 v = new GF257(A[i][p]);
                    A[j][p] = v.mult(b).add(A[j][p]).val;
                }
                GF257 v = new GF257(c[i]);
                c[j] = v.mult(b).add(c[j]).val;

            }
        }

        // A is now in row-echelon form

        for(int i = k-1; i >= 0; i--) {

            GF257 sum = new GF257(0);
            for(int j = k-1; j > i; j--) {
                GF257 v = new GF257(A[i][j]);
                sum =  v.mult(m[j]).add(sum);
            }

            GF257 a = new GF257(A[i][i]);
            GF257 inv = a.getInverse();
            GF257 right = (new GF257(c[i])).minus(sum); //new GF257(GF257.minus(c[i], sum.val));
            m[i] = right.mult(inv).val;
        }


        return m;
    }

    public int[] decode(int[] allC, int k) {
        GF257 wn = new GF257(1);
        int[][] A = new int[k][k];
        int[] c = new int[k];
        int[] m;
        int cnt = 0;

        for(int i = 0; i < allC.length; i++) {
            if(i != 0) wn = wn.mult(gen);

            GF257 cur = new GF257(1);
            for(int j = 0; j < k; j++) {
                A[cnt][j] = cur.val;
                cur = cur.mult(wn);
            }
            c[cnt] = allC[i];

            cnt++;
            if(cnt >= k) break;
        }

        System.out.println("Wiadomość zakodowana po naprawie błędów: ");
        for (int element : c) {
            System.out.print(element + " ");
        }
        System.out.println();

        m = gaussianElimination(A,c,k);

        return m;
    }

    public int[] slow(String input, int s) {
        int k = input.length();
        //int n = 256; //If you want the encoded message from here to be the same as that from FFT
        int n = 2*s + k;
        char[] cin = input.toCharArray();
        int[] m = new int[n];
        for(int i = 0; i < n; i++) {
            m[i] = 0;
            if(i < cin.length) m[i] = cin[i];
        }
        int[] c = new int[n];

        // i is row of T and index of c, j is col and thus index of m
        GF257 wn = new GF257(1);
        int[][] A = new int[n][n];
        for(int i = 0; i < n; i++) {

            GF257 cur = new GF257(1);

            GF257 tot = new GF257(0);

            for(int j = 0; j < n; j++) {
                A[i][j] = cur.val;
                tot = cur.mult(m[j]).add(tot);
                cur = cur.mult(wn);
            }
            wn = wn.mult(gen);
            c[i] = tot.val;
        }
        return c;
    }


    public static void main(String[] args) {
        GF257.init();
        int s = 5; //stopien kodowania - liczba bitow redundandnych
        NumberGeneratorImpl rnd = new NumberGeneratorImpl();
        double pOfError = 0.1;
        double pOfBurst = 0.2;
        double pOfCyclic = 0.1;
        ArrayList<Integer> originMsg = new ArrayList<>();

        //losowy generator
        ArrayList<Integer> gens257 = GF257.findGenerators();
        char gen = 255;
        while(gen == 255) {
            int index = rnd.nextInteger(0,gens257.size());
            int temp = gens257.get(index);
            if(gens257.contains(temp)) gen = (char)temp;
        }
        BCH d = new BCH(gen);

        String input = "Zadzialaj pls :3";
        System.out.println("Original msg: " + input);
        System.out.println("Generator: " + (int)gen);

        int[] c257 = d.slow(input,s); //kodujemy oryginalna wiadomosc (wiadomosc oryginalna, stopien kodowania)

        System.out.println("Wiadomość przed transmisja: ");
        for (int element : c257) {
            originMsg.add(element);
            System.out.print(element + " ");
        }
        System.out.println();

        //zamiana z tablicy jednowymiarowej na dwuwymiarowa
        int[][] encodedArray = new int[1][originMsg.size()];
        for (int i = 0; i < originMsg.size(); i++) {
            encodedArray[0][i] = originMsg.get(i);
        }

        //wysylamy przez kanał
        int[][] transmittedMsg = GilbertElliot.gilbert(encodedArray,pOfError, pOfBurst, pOfCyclic, new NumberGeneratorImpl());

        //wypisujemy po transmisji
        System.out.println("Wiadomość po transmisji: ");
        for(int[] o: transmittedMsg){
            for(int p : o){
                System.out.print(p + " ");
            }
        }
        System.out.println();

        //dekodujemy wiadmosc
        int[] c4 = d.decode(c257,input.length());
        char[] c5 = new char[c4.length];
        for(int i = 0; i < c4.length; i++) {
            c5[i] = (char)(c4[i]);
        }
        String s4 = new String(c5);
        System.out.println("Wiadomość po dekodowaniu: " + s4);

    }
}