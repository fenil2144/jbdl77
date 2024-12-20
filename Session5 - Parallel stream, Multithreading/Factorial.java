import java.math.BigInteger;
import java.util.Arrays;

public class Factorial {

    public static void main(String[] args) {
        Integer[] numbers = {10000, 20000, 50000, 30000, 43000, 65000, 42000};
        Integer[] shortNumbers = {1, 2, 3, 4, 5, 6, 7, 8};

        long start = System.currentTimeMillis();
        Arrays.stream(numbers)
                .map(Factorial::calculateFactorial).forEach(System.out::println);

        long end = System.currentTimeMillis();
        System.out.println("The total time is: " + (end - start) + " millis");
    }

    public static BigInteger calculateFactorial(int number) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= number; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
