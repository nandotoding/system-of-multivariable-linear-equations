import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SystemOfMultivariableLinearEquations {
    public static void main(String[] args) {
        String inputFilePath = "./input.txt";
        execute(inputFilePath);
    }

    static void execute(String inputFilePath) {
        List<List<Double>> matrix = readInputFile(inputFilePath);
        printMatrix("ORIGINAL", matrix);
        createLowerTriangle(matrix);
        createUpperTriangle(matrix);
        printMatrix("DECOMPOSED", matrix);
        printSolution("SOLUTION", matrix);
    }

    static void createLowerTriangle(List<List<Double>> matrix) {
        int m = matrix.size();

        for (int j = 0; j < m; j++) {
            for (int i = j; i < m; i++) {
                if (i == j) {
                    makeElementOne(matrix, i, j);
                } else {
                    makeElementZero(matrix, i, j, j);
                }
            }
        }
    }

    static void createUpperTriangle(List<List<Double>> matrix) {
        int m = matrix.size();

        for (int j = m - 1; j >= 0; j--) {
            for (int i = j; i >= 0; i--) {
                if (i == j) {
                    makeElementOne(matrix, i, j);
                } else {
                    makeElementZero(matrix, i, j, j);
                }
            }
        }
    }

    static void makeElementOne(List<List<Double>> matrix, int i, int j) {
        if (matrix.get(i).get(j) != 1) {
            double f = 1/matrix.get(i).get(j);
            doEro2(matrix, i, f);
        }
    }

    static void makeElementZero(List<List<Double>> matrix, int i1, int i2, int j) {
        if (matrix.get(i1).get(j) != 0) {
            double c = -matrix.get(i1).get(j);
            doEro3(matrix, i1, i2, c);
        }
    }

    static void doEro2(List<List<Double>> matrix, int i, double c) {
        int n = matrix.get(i).size();

        for (int j = 0; j < n; j++) {
            matrix.get(i).set(j, matrix.get(i).get(j) * c);
        }
    }

    static void doEro3(List<List<Double>> matrix, int i1, int i2, double c) {
        int n = matrix.get(i1).size();

        for (int j = 0; j < n; j++) {
            matrix.get(i1).set(j, matrix.get(i2).get(j) * c + matrix.get(i1).get(j));
        }
    }

    static List<List<Double>> readInputFile(String path) {
        List<List<Double>> matrix = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            int i = 0;
            int n = 0;

            while ((line = br.readLine()) != null) {
                if (i == 0) {
                    n = getNValue(line);
                } else {
                    matrix.add(parseEquation(line, n));
                }

                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrix;
    }

    public static List<Double> parseEquation(String equation, int numberOfVars) {
        equation = equation.replaceAll("\\s+", "");
        String[] parts = equation.split("=");
        String leftSide = parts[0];
        double constant = Double.parseDouble(parts[1]);
        Map<Integer, Double> coefficientMap = new HashMap<>();
        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d+)?(x\\d+)");
        Matcher matcher = pattern.matcher(leftSide);
        
        while (matcher.find()) {
            String coefficientStr = matcher.group(1);
            String variable = matcher.group(2);
            double coefficient = coefficientStr == null || coefficientStr.isEmpty() 
                                 ? (variable.startsWith("-") ? -1.0 : 1.0) 
                                 : Double.parseDouble(coefficientStr);
            int varIndex = Integer.parseInt(variable.substring(1));
            coefficientMap.put(varIndex, coefficientMap.getOrDefault(varIndex, 0.0) + coefficient);
        }

        List<Double> coefficients = new ArrayList<>(Collections.nCopies(numberOfVars, 0.0));

        for (Map.Entry<Integer, Double> entry : coefficientMap.entrySet()) {
            int index = entry.getKey() - 1;
            if (index < numberOfVars) {
                coefficients.set(index, entry.getValue());
            }
        }

        coefficients.add(constant);

        return coefficients;
    }

    static int getNValue(String line) {
        Pattern pattern = Pattern.compile("n\\s*=\\s*(\\d+)");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new RuntimeException("Invalid n Value at the file header");
        }
    }

    static void printMatrix(String header, List<List<Double>> matrix) {
        int m = matrix.size();

        System.out.println("\n" + header);

        for (int i = 0; i < m; i++) {
            System.out.println(matrix.get(i));
        }
    }

    static void printSolution(String header, List<List<Double>> matrix) {
        System.out.println("\n" + header);
        System.out.println(getStringSolution(matrix));
    }

    static String getStringSolution(List<List<Double>> matrix) {
        int m = matrix.size();
        StringBuilder builder = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        builder.append("(");
        builder2.append("(");

        for (int i = 0; i < m; i++) {
            builder.append("x" + (i + 1));
            builder2.append(String.format("%.2f", matrix.get(i).get(m)));

            if (i < m - 1) {
                builder.append(", ");
                builder2.append(", ");
            }
        }

        builder.append(") = ");
        builder2.append(")");
        builder.append(builder2);

        return builder.toString();
    }
}