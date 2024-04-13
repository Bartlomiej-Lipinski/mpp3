import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    static int iter = 0;
    static int testaccuracy = 0;
    static int trainaccuracy = 0;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            trainaccuracy = 0;
            testaccuracy = 0;
            train();
            iter++;
            System.out.println("iteracja: " + iter);
            for (Perceptron p : LayerManagment.getPerceptrons()) {
                if (p.isTrained() && p.getAccuracy() == 10) {
                    trainaccuracy++;
                }
            }
            if (trainaccuracy == LayerManagment.getPerceptrons().size()) {
                System.out.println("Wszystkie perceptrony sa nauczone");
                for (Perceptron p : LayerManagment.getPerceptrons()) {
                    System.out.println(p.getName() + " " + p.isTrained() + " " + p.getAccuracy()+" "+p.getThreshold());
                }
                break;
            }
            for (Perceptron p : LayerManagment.getPerceptrons()) {
                System.out.println(p.getName() + " " + p.isTrained() + " " + p.getAccuracy());
            }
            LayerManagment.resetPerceptronsAccuracyAndTrained();
        }
        System.out.println("Trening zakonczony");
        LayerManagment.normalizeWeights();
        Test();
        System.out.println("Test accuracy: " + testaccuracy);
        System.out.println("Test zakonczony");
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            manualTest(input);
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
        text = getText(text);
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
        vectorLength = Math.sqrt(vectorLength);
        for (int i = 0; i < inputs.size(); i++) {
            inputs.set(i, inputs.get(i) / vectorLength);
        }
    }

    public static Perceptron maximumSelector(ArrayList<Double> input) {
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

    private static String getText(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("\\.", "");
        text = text.replaceAll(",", "");
        text = text.replaceAll("[^a-z]", "");
        text = text.replaceAll(" ", "");
        return text;
    }

    public static void Test() throws FileNotFoundException {
        File folderTest = new File("Test_Set");
        for (File podfolder : Objects.requireNonNull(folderTest.listFiles())) {
            System.out.println(podfolder.getName());
            for (File plik : Objects.requireNonNull(podfolder.listFiles())) {
                String tekst = readText(plik);
                ArrayList<Double> inputs = mapToDoubleArray(mapString(tekst), lengthOfText(tekst));
                normalizeInput(inputs);
                try {
                    Perceptron p = maximumSelector(inputs);
                    if (p.getName().equals(podfolder.getName().split("_")[0])) {
                        testaccuracy++;
                    } 
                    System.out.println(p.getName());
                } catch (NullPointerException e) {
                    System.out.println("Nieznany jezyk");
                }
            }
        }
    }

    public static void train() throws FileNotFoundException {
        File folderTest = new File("Training_Set");
        if (iter == 0) {
            for (File podfolder : Objects.requireNonNull(folderTest.listFiles())) {
                Perceptron p = new Perceptron();
                String s = podfolder.getName().split("_")[0];
                p.setName(s);
                LayerManagment.addPerceptron(p);
            }
        }
        for (File podfolder : Objects.requireNonNull(folderTest.listFiles())) {
            for (File plik : Objects.requireNonNull(podfolder.listFiles())) {
                String tekst = readText(plik);
                Perceptron choice = maximumSelector(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)));
                if (choice == null) {
                    LayerManagment.getPerceptrons().forEach(perceptron -> {
                        if (perceptron.getName().equals(podfolder.getName().split("_")[0])) {
                            perceptron.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 1);
                            perceptron.setAccuracy(perceptron.getAccuracy() - 1);
                        } else {
                            perceptron.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
                        }
                    });
                } else if (choice.getName().equals(podfolder.getName().split("_")[0])) {
                    choice.setAccuracy(choice.getAccuracy() + 1);
                    if (choice.getAccuracy() == 10) {
                        choice.trained();
                    }
                } else {
                    LayerManagment.getPerceptrons().forEach(perceptron -> {
                        if (perceptron.getName().equals(podfolder.getName().split("_")[0])) {
                            perceptron.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 1);
                            perceptron.setAccuracy(perceptron.getAccuracy() - 1);
                        } else {
                            perceptron.train(mapToDoubleArray(mapString(tekst), lengthOfText(tekst)), 0);
                        }
                    });
                }
            }
        }
    }

    public static void manualTest(String input) {
        String tekst = getText(input);
        ArrayList<Double> inputs = mapToDoubleArray(mapString(tekst), lengthOfText(tekst));
        normalizeInput(inputs);
        Perceptron p = maximumSelector(inputs);
        if (p == null) {
            System.out.println("Nieznany jezyk");
        } else {
            System.out.println(p.getName());
        }
    }
}