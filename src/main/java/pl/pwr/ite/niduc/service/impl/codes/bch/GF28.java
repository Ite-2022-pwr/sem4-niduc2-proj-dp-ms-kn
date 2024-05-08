package pl.pwr.ite.niduc.service.impl.codes.bch;
import java.util.*;

// Klasa reprezentująca nadciało Galois nad ciałem GF(2^8)
public class GF28 {
    // Tablice przechowujące wyniki operacji oraz flagi informujące, czy wyniki zostały już obliczone
    public static char[][] adds; // Tablica dodawań
    public static char[][] mults; // Tablica mnożeń
    public static char[] inverse; // Tablica odwrotności
    public static boolean[][] addsDone; // Tablica flag dla dodawań
    public static boolean[][] multsDone; // Tablica flag dla mnożeń
    public static boolean[] inverseDone; // Tablica flag dla odwrotności

    // Stała reprezentująca wielomian PX = x^8 + x^4 + x^3 + x + 1
    public static final char PX = (char)0x11B;

    // Metoda inicjująca tablice i flagi
    public static void init() {
        int max = 1 << 8;
        adds = new char[max][max];
        mults = new char[max][max]; //przechowują wyniki operacji dodawania i mnożenia dla wszystkich możliwych kombinacji wartości (w tym przypadku od 0 do 255).
        addsDone = new boolean[max][max];
        multsDone = new boolean[max][max]; //addsDone i multsDone są tablicami flag, które śledzą, czy wyniki operacji zostały już obliczone
        inverse = new char[max]; //inverse przechowuje odwrotności dla wszystkich możliwych wartości
        inverseDone = new boolean[max]; //inverseDone śledzi, czy odwrotności zostały już obliczone
        // Wyzerowanie tablic i ustawienie flag na false
        for(int i = 0; i < adds.length; i++) {
            inverse[i] = 0;
            inverseDone[i] = false;
            for(int j = 0; j < adds[0].length; j++) {
                adds[i][j] = 0;
                mults[i][j] = 0;
                addsDone[i][j] = false;
                multsDone[i][j] = false;
            }
        }//inicjalizacja tablic
    }

    // Metoda wykonująca operację dodawania
    public static char add(char a, char b) {
        return (char)(a ^ b); // XOR dwóch wartości
    }

    // Metoda wykonująca operację mnożenia
    public static char mult(char a, char b) {
        int p = a;
        int r = 0;
        while(b != 0) {
            if((b & 1) == 1) r = r ^ p; // Jeśli ostatni bit b jest 1, wykonaj operację XOR
            b = (char)(b >>> 1); // Przesuń b o jeden bit w prawo
            p = p << 1; // Przesuń p o jeden bit w lewo
            if((p & 0x100) == 0x100) p = p ^ PX; // Jeśli najstarszy bit p jest 1, wykonaj XOR z wielomianem PX
        }
        return (char)r; // Zwróć wynik
    }

    // Konstruktor obiektu GF28
    char val; //reprezentacja GF28 - może byc to dowolna wartość od 0 - 2^8
    public GF28(char val) {
        this.val = val;
    }

    // Metoda dodająca wartość do obiektu GF28
    public GF28 add(char b) {
        if(!addsDone[val][b]) {
            adds[val][b] = add(val, b);
            adds[b][val] = adds[val][b];
            addsDone[val][b] = true;
            addsDone[b][val] = true;
        }
        return new GF28(adds[val][b]);
    }

    // Metoda dodająca dwa obiekty GF28
    public GF28 add(GF28 b) {
        return add(b.val);
    }

    // Metoda mnożąca obiekt GF28 przez wartość
    public GF28 mult(char b) {
        if(!multsDone[val][b]) {
            mults[val][b] = mult(val, b);
            mults[b][val] = mults[val][b];
            multsDone[val][b] = true;
            multsDone[b][val] = true;
        }
        return new GF28(mults[val][b]);
    }

    // Metoda mnożąca dwa obiekty GF28
    public GF28 mult(GF28 b) {
        return mult(b.val);
    }

    // Metoda zwracająca odwrotność obiektu GF28
    public GF28 getInverse() {
        return inverse(val);
    }

    // Metoda obliczająca odwrotność wartości, czyli takie a, dla ktorego a*b = 1
    public static GF28 inverse(char a) {
        if(!inverseDone[a]) {
            GF28 ga = new GF28(a);
            for(char i = 0; i < (1 << 8); i++) {
                if(ga.mult(i).val == 1) {
                    inverseDone[a] = true;
                    inverseDone[i] = true;
                    inverse[a] = i;
                    inverse[i] = a;
                    break;
                }
            }
        }
        return new GF28(inverse[a]);
    }

    // Metoda konwertująca char na jego reprezentację binarną w postaci Stringa
    public static String getBinaryString(char c) {
        String s = "";
        for(int i = 0; i < 8; i++) {//petla wykonuje sie 8 razy, bo char to 8 bitow
            s = (c % 2) + s; //dzieleienie modulo 2
            c = (char)(c >> 1); //dzieki przesunieciu kolejna iteracja odnosi sie do kolejnego bitu
        }
        return "0b" + s;
    }
    // Metoda znajdująca generatory dla nadciała Galois
    public static ArrayList<Integer> findGenerators() {
        ArrayList<Integer> gs = new ArrayList<>(); //bedzie przechowywac znalezione generatory
        for(int j = 1; j < 1 << 8; j++) { //iterujemy od 1 do 256, dla kazdej wartosci szukamy generator
            GF28 gz = new GF28((char)j); // tworzymy obiekt o wartosci j, czyli reprezentujemy kazda mozliwa wartosc w tym ciele
            HashSet<Character> hs = new HashSet<>(); //bedzie przechowwywal unikalne wartosci ciala
            hs.add(gz.val);
            for(int i = 2; i <= 1 << 8; i++) {
                gz = gz.mult((char)j); //mnozymy gz przez kolejne potegi aby znalezc wszystkie elementy generowane przez j
                hs.add(gz.val);
            }
            if(hs.size() == 255) gs.add(j); //sprawdzamy czy zbior jest wielkosci 255, jesli tak to znaczy ze wygenerowamo wszystkie elementy i
        }
        return gs;
    }
}
