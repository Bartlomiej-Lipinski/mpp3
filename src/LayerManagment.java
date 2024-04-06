import java.util.ArrayList;
import java.util.Arrays;

public class LayerManagment {
    private static ArrayList<Perceptron> perceptrons = new ArrayList<>();

    public static void normalizeWeights() {
        double vectorLength = 0;
        for (Perceptron p : perceptrons) {
            p.getWeightsVector().replaceAll(aDouble -> aDouble * 100);
            for (double weight : p.getWeightsVector()) {
                vectorLength += weight * weight;
            }
            vectorLength = Math.sqrt(vectorLength);
            for (int i = 0; i < p.getWeightsVector().size(); i++) {
                p.getWeightsVector().set(i, p.getWeightsVector().get(i) / vectorLength);
            }
        }
    }

    public static void addPerceptron(Perceptron... p) {
        perceptrons.addAll(Arrays.asList(p));
    }
    public static ArrayList<Perceptron> getPerceptrons() {
        return perceptrons;
    }
}
