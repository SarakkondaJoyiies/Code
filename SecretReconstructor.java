import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import org.json.*;

public class SecretReconstructor {

    public static void main(String[] args) throws Exception {
        String jsonFile = "testcase2.json";
        JSONObject input = new JSONObject(new String(Files.readAllBytes(Paths.get(jsonFile))));

        JSONObject keys = input.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        // Read first k entries for interpolation
        int count = 0;
        for (String key : input.keySet()) {
            if (key.equals("keys")) continue;
            if (count == k) break;

            JSONObject entry = input.getJSONObject(key);
            int base = Integer.parseInt(entry.getString("base"));
            String valueStr = entry.getString("value");
            BigInteger x = new BigInteger(key);
            BigInteger y = new BigInteger(valueStr, base);

            xValues.add(x);
            yValues.add(y);
            count++;
        }

        BigInteger secret = lagrangeInterpolationAtZero(xValues, yValues);
        System.out.println("Recovered constant c (secret): " + secret);
    }

    // Lagrange interpolation at x = 0 over integers (no modulus)
    public static BigInteger lagrangeInterpolationAtZero(List<BigInteger> x, List<BigInteger> y) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < x.size(); i++) {
            BigInteger xi = x.get(i);
            BigInteger yi = y.get(i);
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < x.size(); j++) {
                if (i != j) {
                    num = num.multiply(x.get(j).negate()); // numerator *= -xj
                    den = den.multiply(xi.subtract(x.get(j))); // denominator *= (xi - xj)
                }
            }

            BigInteger term = yi.multiply(num).divide(den); // yi * (product -xj) / (product xi - xj)
            result = result.add(term);
        }

        return result;
    }
}

