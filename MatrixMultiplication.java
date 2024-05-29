import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class MatrixMultiplication {

    static class MatrixWorker implements Callable<long[]> {
        private final int row;
        private final int[][] A;
        private final int[][] B;
        private final int[][] C;
        private final long[] times;

        public MatrixWorker(int row, int[][] A, int[][] B, int[][] C) {
            this.row = row;
            this.A = A;
            this.B = B;
            this.C = C;
            this.times = new long[2]; // [start_time, end_time]
        }

        @Override
        public long[] call() {
            times[0] = System.currentTimeMillis();
            int size = A.length;
            for (int j = 0; j < size; j++) {
                C[row][j] = 0;
                for (int k = 0; k < size; k++) {
                    C[row][j] += A[row][k] * B[k][j];
                }
            }
            times[1] = System.currentTimeMillis();
            return times;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the size of the matrices: ");
        int size = scanner.nextInt();

        int[][] A = generateRandomMatrix(size);
        int[][] B = generateRandomMatrix(size);
        int[][] C = new int[size][size];

        ExecutorService executor = Executors.newFixedThreadPool(size);
        List<Future<long[]>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < size; i++) {
            futures.add(executor.submit(new MatrixWorker(i, A, B, C)));
        }

        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            System.err.println("Tasks did not finish in the specified time.");
        }

        long endTime = System.currentTimeMillis();

        for (int i = 0; i < size; i++) {
            long[] times = futures.get(i).get();
            System.out.println("Thread " + (i + 1) + " execution time: " + (times[1] - times[0]) + " ms");
        }

        System.out.println("Total processing time: " + (endTime - startTime) + " ms");

        System.out.println("Result matrix:");
        printMatrix(C);
    }

    private static int[][] generateRandomMatrix(int size) {
        Random random = new Random();
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }
        return matrix;
    }

    private static void printMatrix(int[][] matrix) {
        int maxLength = 0;
        for (int[] row : matrix) {
            for (int element : row) {
                int length = String.valueOf(element).length();
                if (length > maxLength) {
                    maxLength = length;
                }
            }
        }

        for (int[] row : matrix) {
            System.out.print("[");
            for (int j = 0; j < row.length; j++) {
                System.out.print(String.format("%" + maxLength + "d", row[j]));
                if (j < row.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }
}
