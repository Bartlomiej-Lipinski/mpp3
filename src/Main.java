import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    static int accuracy = 0;  
    static int iter = 0;
    public static void main(String[] args) throws FileNotFoundException {
        Perceptron polski = new Perceptron();
        Perceptron niemiecki = new Perceptron();
        Perceptron francuski = new Perceptron();
        LayerManagment.addPerceptron(polski, niemiecki, francuski);
        while (accuracy != 30){
            accuracy = 0;
            File folderPL = new File("Training_Set/Polish_Training_Set");
            for (File plik : Objects.requireNonNull(folderPL.listFiles())) {
                String tekst = readText(plik);
                Perceptron p = maximumSelector(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)));
                try {
                    if (p.equals(polski)) {
                        accuracy++;
                        continue;
                    } else if (p.equals(niemiecki)) {
                        accuracy--;
                    } else if (p.equals(francuski)) {
                        accuracy--;
                    }
                }catch (NullPointerException e) {
                    accuracy--;
                }
                polski.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 1);
                niemiecki.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
                francuski.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
            }
            File folderGM = new File("Training_Set/German_Training_Set");
            for (File plik : Objects.requireNonNull(folderGM.listFiles())) {
                String tekst = readText(plik);
                Perceptron p = maximumSelector(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)));
                try {
                    if (p.equals(polski)) {
                        accuracy--;
                    } else if (p.equals(niemiecki)) {
                        accuracy++;
                        continue;
                    } else if (p.equals(francuski)) {
                        accuracy--;
                    }
                }catch (NullPointerException e) {
                    accuracy--;
                }
                niemiecki.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 1);
                polski.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
                francuski.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
            }
            File folderFR = new File("Training_Set/French_Training_Set");
            for (File plik : Objects.requireNonNull(folderFR.listFiles())) {
                String tekst = readText(plik);
                Perceptron p = maximumSelector(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)));
                try {
                    if (p.equals(polski)) {
                        accuracy--;
                    } else if (p.equals(niemiecki)) {
                        accuracy--;
                    } else if (p.equals(francuski)) {
                        accuracy++;
                        continue;
                    }
                }catch (NullPointerException e) {
                    accuracy--;
                }
                francuski.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 1);
                polski.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
                niemiecki.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
            }
            System.out.println("Dokladnosc: " + accuracy);
            iter++;
            System.out.println("iteracja: " + iter);
            if(accuracy==30 && iter >275) {
                break;
            }
        }
        System.out.println("Trening zakonczony");
        LayerManagment.normalizeWeights();
        File folderTest = new File("Test_Set");
        for (File podfolder : Objects.requireNonNull(folderTest.listFiles())) {
            System.out.println(podfolder.getName());
            for (File plik : Objects.requireNonNull(podfolder.listFiles())) {
                String tekst = readText(plik);
                ArrayList<Double> inputs = mapToDoubleArray(mapString(tekst), lengthOfText(tekst));
                normalizeInput(inputs);
                Perceptron p = maximumSelector(inputs);
                try {
                    if (p.equals(polski)) {
                        System.out.println("Polski");
                    } else if (p.equals(niemiecki)) {
                        System.out.println("Niemiecki");
                    } else if (p.equals(francuski)) {
                        System.out.println("Francuski");
                    }
                }catch (NullPointerException e) {
                    System.out.println("Nieznany");
                }
            }
        }
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("exit")) {
            String tekst = input;
            ArrayList<Double> inputs = mapToDoubleArray(mapString(tekst), lengthOfText(tekst));
            normalizeInput(inputs);
            Perceptron p = maximumSelector(inputs);
            try {
                if (p.equals(polski)) {
                    System.out.println("Polski");
                } else if (p.equals(niemiecki)) {
                    System.out.println("Niemiecki");
                } else if (p.equals(francuski)) {
                    System.out.println("Francuski");
                }
            }catch (NullPointerException e) {
                System.out.println("Nieznany");
            }
            input = scanner.next();
        }
    }

    public static String readText(File file) throws FileNotFoundException {
        String text = "";
        Scanner scanner = new Scanner(file);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }
        text = stringBuilder.toString();
        text = text.toLowerCase();
        text = text.replaceAll("\\.", "");
        text = text.replaceAll(",", "");
        text = text.replaceAll("[^a-z]", "");
        text = text.replaceAll(" ", "");
        return text;
    }

    public static int lengthOfText(String text) {
        return text.length();
        
    }

    public static HashMap<String, Integer> mapString(String text) {
        HashMap<String, Integer> map = new HashMap<>();
        String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        for (String s : alphabet) {
            map.put(s, 0);
        }
        for (int i = 0; i < text.length(); i++) {
            String s = text.substring(i, i + 1);
            map.put(s, map.get(s) + 1);
        }
        return map;
    }

    public static ArrayList<Double> mapToDoubleArray(HashMap<String, Integer> map, int textLength) {
        ArrayList<Double> list = new ArrayList<>();
        String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        for (String s : alphabet) {
            list.add((double) map.get(s));
        }
        list.replaceAll(aDouble -> aDouble / textLength);
        return list;
    }

    public static void normalizeInput(ArrayList<Double> inputs) {
        double vectorLength = 0;
        for (double weight : inputs) {
            vectorLength += weight * weight;
        }
        vectorLength=Math.sqrt(vectorLength);
        for (int i = 0; i < inputs.size(); i++) {
            inputs.set(i, inputs.get(i) / vectorLength);
        }
    }
    public static Perceptron maximumSelector(ArrayList<Double> input){
        Perceptron max = null;
        double maxVal = 0;
        for (Perceptron p : LayerManagment.getPerceptrons()) {
            double val = p.guess(input);
            if (val > maxVal) {
                maxVal = val;
                max = p;
            }
        }
        return max;
    }
}
