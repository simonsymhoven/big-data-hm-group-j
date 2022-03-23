import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class bda01 {

	public static void main(String[] args) throws IOException, InterruptedException {
		createRandomArray(1000000, "test.txt");
		
		BufferedReader bufferedReader = new BufferedReader(
                new FileReader("test.txt"));
		String line;
		
		ArrayList<Integer> arrList = new ArrayList<Integer>();
		while((line = bufferedReader.readLine()) != null){
			arrList.add(Integer.parseInt(line));
		}
		System.out.println(arrList.size());
		
		long start = System.nanoTime();
		sort(arrList, arrList.size());
		long finish = System.nanoTime();
		long timeElapsed = finish - start;
		//System.out.println(arrList.toString());
		System.out.println("Time elapsed: " + timeElapsed);
		
		start = System.nanoTime();
		sortParallel(arrList, arrList.size());
		finish = System.nanoTime();
		timeElapsed = finish - start;
		//System.out.println(arrList.toString());
		System.out.println("Time elapsed parallel: " + timeElapsed);
	}
	
	public static void createRandomArray(int sampleSize, String fileName) throws IOException  {
		Random rd = new Random();
		int[] arr = new int[sampleSize];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = rd.nextInt();
		}
		System.out.println("Array size: " + arr.length);
		
		BufferedWriter outputWriter = null;
		FileWriter fileWriter = new FileWriter(fileName);
		outputWriter = new BufferedWriter(fileWriter);
		for (int i = 0; i < arr.length; i++) {
			outputWriter.write(Integer.toString(arr[i]));
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
		
		//sort(arr, 5);
		//System.out.println(Arrays.toString(arr));
	}
	
	public static void sortParallel(ArrayList<Integer> a, int n) throws InterruptedException {
		if (n < 2) {
			return;
		}
		int mid = n / 2;
		ArrayList<Integer> l = new ArrayList<Integer>();
		ArrayList<Integer> r = new ArrayList<Integer>();

		for (int i = 0; i < mid; i++) {
			l.add(i, a.get(i));
		}
		for (int i = mid; i < n; i++) {
			r.add(i - mid, a.get(i));
		}
		Thread t1 = new Thread(() -> {
			sort(l, mid);
		});
		Thread t2 = new Thread(() -> {
			sort(r, n - mid);
		});
		t1.start();
		t2.start();
		t1.join();
		t2.join();

		merge(a, l, r, mid, n - mid);
	}

	public static void sort(ArrayList<Integer> a, int n) {
		if (n < 2) {
			return;
		}
		int mid = n / 2;
		ArrayList<Integer> l = new ArrayList<Integer>();
		ArrayList<Integer> r = new ArrayList<Integer>();

		for (int i = 0; i < mid; i++) {
			l.add(i, a.get(i));
		}
		for (int i = mid; i < n; i++) {
			r.add(i - mid, a.get(i));
		}
		sort(l, mid);
		sort(r, n - mid);

		merge(a, l, r, mid, n - mid);
	}

	public static void merge(ArrayList<Integer> a, ArrayList<Integer> l, ArrayList<Integer> r, int left, int right) {

		int i = 0, j = 0, k = 0;
		while (i < left && j < right) {
			if (l.get(i) <= r.get(j)) {
				a.set(k++,l.get(i++));
			} else {
				a.set(k++, r.get(j++));
			}
		}
		while (i < left) {
			a.set(k++, l.get(i++));
		}
		while (j < right) {
			a.set(k++, r.get(j++));
		}
	}
}
