import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class bda01 {

	static ArrayList<String> fileNames =  new ArrayList<String>();
	static HashMap<String, Long> times = new HashMap<String, Long>();
	static HashMap<String, Long> timesParallel = new HashMap<String, Long>();

	public static void main(String[] args) {
		createFiles();
		try {
			processFiles();
		} catch (NumberFormatException e) {
			System.out.println("ERROR: Wrong number format");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("ERROR: Wrong number format");
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			System.out.println("ERROR: Wrong number format");
			e.printStackTrace();
			System.exit(1);
		}

		evaluateTimes();
	}

	//Create files containing varying amounts of random numbers
	private static void createFiles() {
		System.out.println("Beginning to Create Files....");

		int stepSize = 100000;
		int startingSize = 100000;
		try {
			for(int i = startingSize; i <= 10000000; i += stepSize) {
				String name =  "dataFiles/randomData" + i + ".txt";
				createRandomArray(i, name);
				fileNames.add(name);
			}
		} catch (IOException e) {
			System.out.println("####ERROR: Could not Write to File");
			System.exit(1);
		}

		System.out.println("Finished!");
	}


	private static void processFiles() throws NumberFormatException, IOException, InterruptedException{
		System.out.println("Processing Data...");
		for(String file : fileNames) {
			ArrayList<Integer> arrList = readFile(file);
			measureAndLogTimes(arrList, file);
		}
		System.out.println("Finished\r\n__________________________________________________");
	}

	private static void evaluateTimes() {
		for(String file : fileNames) {
			System.out.println("Time for " + file.substring(10, file.length() - 4) + " Datapoints");
			Long time = times.get(file);
			Long timeParallel = timesParallel.get(file);
			double ratio = (double) time/timeParallel;

			System.out.println("Time: " + time);
			System.out.println("TimeParallel: " + time);
			System.err.println("Time/TimeParallel: " + ratio);
			System.err.println("_________________________________________________");
		}
	}

	//Read a file and turn it into an ArrayList
	private static ArrayList<Integer> readFile(String file) throws NumberFormatException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line;
			
		ArrayList<Integer> arrList = new ArrayList<Integer>();
		while((line = bufferedReader.readLine()) != null){
			arrList.add(Integer.parseInt(line));
		}

		bufferedReader.close();

		return arrList;
	}

	//Measures time in Miliseconds and Logs them to the respected ArrayLists "times" and "timesParallel"
	private static void measureAndLogTimes(ArrayList<Integer> arrList, String file) throws InterruptedException {

		long start = System.nanoTime();
		sort(arrList, arrList.size());
		long finish = System.nanoTime();
		long timeElapsed = finish - start;
		times.putIfAbsent(file, timeElapsed);
			
		start = System.nanoTime();
		sortParallel(arrList, arrList.size());
		finish = System.nanoTime();
		timeElapsed = finish - start;
		timesParallel.putIfAbsent(file, timeElapsed);
	}

	//Creates an Array of random Numbers and writes them as .txt files
	private static void createRandomArray(int sampleSize, String fileName) throws IOException  {
		Random rd = new Random();
		int[] arr = new int[sampleSize];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = rd.nextInt();
		}
		
		BufferedWriter outputWriter = null;
		FileWriter fileWriter = new FileWriter(fileName);
		outputWriter = new BufferedWriter(fileWriter);
		for (int i = 0; i < arr.length; i++) {
			outputWriter.write(Integer.toString(arr[i]));
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
	}
	
	//Splits the List in Half and executes the mergeSort on two threads
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

	//Splits the List in Half and recursively Sorts the Array
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

	//Merges the split halves of the list together in a sorted manner
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
