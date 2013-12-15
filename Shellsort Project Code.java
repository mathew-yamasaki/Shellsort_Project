/******************************************************************************
 *    Student: Mathew Yamasaki
 *       Date: Sunday, September 16, 2012
 *      Class: Design and Analysis of Computer Algorithms (CMSC 451)
 * Instructor: Dr. Duane Jarc
 * 
 * Class Description: This program compares the performance of iterative and 
 * recursive Shellsort algorithms.  Benchmarking is performed by averaging the 
 * execution times of 50 iterative and recursive Shellsort runs on randomly 
 * generated sets of numbers contained in 10 arrays of different sizes.  The 
 * average critical operation count, standard deviation count, average execution
 * time, and standard deviation time are displayed in nanoseconds.
 *
 ******************************************************************************/
package shellsort;

import java.util.Arrays;
import java.util.Random;

/** Implements recursive and iterative shell sort, and benchmarks them */
public class Shellsort {
    static interface ShellSortI {
        public String getName();
        /** Returns number of item comparisons made */
        public long sort(int[] arr);
    }
    
    /** Iterative shell sort with gap sequence floor(n/(2^k)) (Shell, 1959) */
    static class IterativeShellSort implements ShellSortI {
        public String getName() {
            return "Iterative Shell Sort";
        }
        public long sort(int[] arr) {
            long cmp_count = 0;
            for (int gap = arr.length/2; gap > 0; gap /= 2) {
                // insertion sort with given gap
                for (int i = gap; i < arr.length; i += gap) {
                    int item = arr[i];
                    int slot = i;
                    while (slot >= gap && arr[slot-gap] > item) {
                        ++cmp_count;
                        arr[slot] = arr[slot-gap];
                        slot -= gap;
                    }
                    ++cmp_count;
                    arr[slot] = item;
                }
            }
            return cmp_count;
        }
    }
    
    /** Recursive shell sort with gap sequence 2^k - 1 (Hibbard, 1963) */
    static class RecursiveShellSort implements ShellSortI {
        public String getName() {
            return "Recursive Shell Sort";
        }
        /** Wrapper around sort_helper */
        public long sort(int[] arr) {
            return sort_helper(arr, 1);
        }
        /** Recursive function where sorting is actually done */
        public long sort_helper(int[] arr, int gap) {
            if (gap > arr.length) return 0;
            // recurse
            long cmp_count = sort_helper(arr, (gap+1)*2 - 1);
            // insertion sort with given gap
            for (int i = gap; i < arr.length; i += gap) {
                int item = arr[i];
                int slot = i;
                while (slot >= gap && arr[slot-gap] > item) {
                    ++cmp_count;
                    arr[slot] = arr[slot-gap];
                    slot -= gap;
                }
                ++cmp_count;
		arr[slot] = item;
            }
            return cmp_count;
        }
    }
    
    /** Method checks for a sorted array */
    static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length-1; ++i) {
            if (arr[i] > arr[i+1]) return false;
        }
        return true;
    }
     /** Main */
    public static void main(String[] args) {
        final int N_RUNS = 50;
        final int sizes[] = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        ShellSortI[] sortTypes = new ShellSortI[]{
            new IterativeShellSort(),
            new RecursiveShellSort()
        };
        Random random = new Random();
       
        // print header
        System.out.println("Note: All units of measurement are in nanoseconds.\n");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("  Data   |                          Iterative                              |                           Recursive");
        System.out.println("  Set    |                                                                 |");
        System.out.println(" Size n  |                                                                 |");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("         |      Average        Standard        Average        Standard     |        Average        Standard        Average        Standard");
        System.out.println("         |     Critical      Deviation of     Execution     Deviation of   |       Critical      Deviation of     Execution     Deviation of");
        System.out.println("         |     Operation        Count           Time            Time       |       Operation        Count           Time            Time");
        System.out.println("         |       Count                                                     |         Count");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------");

        for (int size : sizes) {
            System.out.printf("%6d ", size);
            for (int sortTypeIdx = 0; sortTypeIdx < 2; ++sortTypeIdx) {
                ShellSortI sortType = sortTypes[sortTypeIdx];
                double runtimes[] = new double[N_RUNS];
		long nCmps[] = new long[N_RUNS];
                // perform benchmark
		for (int run = 0; run < N_RUNS; ++run) {
                    int[] arr = new int[size];
                    for (int i = 0; i < arr.length; ++i) {
                        arr[i] = random.nextInt();
                    }		
                    long start = System.nanoTime();
                    long cmps = sortType.sort(arr);
                    long end = System.nanoTime();
                    // Check that the data is actually sorted
                    if (!isSorted(arr)) {
                        System.err.printf("Error: sorting function %s did not result in sorted array.\n",
                                sortType.getName());
                        System.err.printf(Arrays.toString(arr));
                        System.exit(1);
                    }
                    runtimes[run] = (end - start)*1e-6;
                    nCmps[run] = cmps;
                }
				
                // Calculate statistics
                double avgRunTime = 0;
		double avgNCmps = 0;
		double stdRunTime = 0;
                double stdNCmps = 0;
                for (int i = 0; i < N_RUNS; ++i) {
                    avgRunTime += runtimes[i];
                    avgNCmps += nCmps[i];
                }
                avgRunTime /= N_RUNS;
                avgNCmps /= N_RUNS;
                for (int i = 0; i < N_RUNS; ++i) {
                    stdRunTime += Math.pow(runtimes[i] - avgRunTime, 2);
                    stdNCmps += Math.pow(nCmps[i] - avgNCmps, 2);
                }
                stdRunTime = Math.sqrt(stdRunTime/(N_RUNS-1));
                stdNCmps = Math.sqrt(stdNCmps/(N_RUNS-1));
                // Output
                if (sortTypeIdx == 0) 
                    System.out.printf("  | ");
                else 
                    System.out.printf("     |   ");
                
                System.out.printf("%13.4f ", avgNCmps);
                System.out.printf("%13.4f ", stdNCmps);
                System.out.printf("%14.4f ", avgRunTime);
                System.out.printf("%14.4f  ", stdRunTime);
            }
            System.out.println();
        }
    } // End main
}
