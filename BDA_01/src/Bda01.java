import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Bda01 {

	static int GENERATE_DATA_STEP_SIZE = 10000;
	static int GENERATE_DATA_START_SIZE = 10000;
	static int GENERATE_DATA_STOP_SIZE = 1000000;
	static int GENERATE_DATA_ITERATIONS = 100;

	static List<String> fileNames =  new ArrayList<String>();
	static Map<String, List<Long>> times = new HashMap<String, List<Long>>();
	static Map<String,  List<Long>> timesParallel = new HashMap<String,  List<Long>>();
	static Map<String, List<Long>> averageTimes = new HashMap<String, List<Long>>();

	
	public static void main(String[] args) {
		createFiles();
		prepareMaps();
		try {
			processFiles();
		} catch (NumberFormatException | IOException | InterruptedException e) {
			System.out.println("ERROR: Wrong number format");
			e.printStackTrace();
			System.exit(1);
		}

		evaluateTimes();
		generateCSV();
	}

	//Create files containing varying amounts of random numbers
	private static void createFiles() {
		System.out.println("Beginning to Create Files....");

		String folderName = "dataFiles";
		File file = new File(folderName);

		if (!file.exists()) {
			file.mkdir();
		}

		try {
			for(int i = GENERATE_DATA_START_SIZE; i <= GENERATE_DATA_STOP_SIZE; i += GENERATE_DATA_STEP_SIZE) {
				String name =  folderName + File.separator + "randomData" + i + ".txt";
				createRandomArray(i, name);
				fileNames.add(name);
			}
		} catch (IOException e) {
			System.out.println("####ERROR: Could not Write to File");
			System.exit(1);
		}

		System.out.println("Finished!");
	}

	//Fills the maps with the fileNames as keys and empty Lists
	private static void prepareMaps() {
		for(String fileName : fileNames){
			times.put(fileName, new ArrayList<Long>());
			timesParallel.put(fileName, new ArrayList<Long>());
			averageTimes.put(fileName, new ArrayList<Long>());
		}
	}

	//Measures the times for sorting with and without multithreading
	private static void processFiles() throws NumberFormatException, IOException, InterruptedException{
		System.out.println("Processing Data...");
		for(String file : fileNames) {
			List<Integer> arrList = readFile(file);
			for(int i = 0; i < GENERATE_DATA_ITERATIONS; i++){
				measureAndLogTimes(arrList, file);
			}
		}
		buildAverages();
		System.out.println("Finished\r\n__________________________________________________");
	}

	//Outputs the times on the console
	private static void evaluateTimes() {
		for(String file : fileNames) {
			System.out.println("Time for " + file.substring(20, file.length() - 4) + " Datapoints");
			Long time = averageTimes.get(file).get(0);
			Long timeParallel = averageTimes.get(file).get(1);
			double ratio = (double) time/timeParallel;

			System.out.println("Average Time: " + time);
			System.out.println("Average TimeParallel: " + timeParallel);
			System.err.println("Time/TimeParallel: " + ratio);
			System.err.println("_________________________________________________");
		}
	}

	//Generates a CSV with the times inside
	private static void generateCSV(){
		String eol = System.getProperty("line.separator");

		try (Writer writer = new FileWriter("timesData.csv")) {
			writer.append("Anzahl Zeichen;Zeit Normal;Zeit Parallel" + eol);
  			for (Map.Entry<String, List<Long>> entry : averageTimes.entrySet()) {
    			writer.append(entry.getKey().substring(20, entry.getKey().length() - 4))
          		.append(';')
          		.append(Long.toString(entry.getValue().get(0)))
				.append(';')
				.append(Long.toString(entry.getValue().get(1)))
            	.append(eol);
 			}
		} catch (IOException ex) {
  			ex.printStackTrace(System.err);
		}
	}

	//Read a file and turn it into an ArrayList
	private static List<Integer> readFile(String file) throws NumberFormatException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line;

		List<Integer> arrList = new ArrayList<Integer>();
		while((line = bufferedReader.readLine()) != null){
			arrList.add(Integer.parseInt(line));
		}

		bufferedReader.close();

		return arrList;
	}

	//Measures time in Miliseconds and Logs them to the respected ArrayLists "times" and "timesParallel"
	private static void measureAndLogTimes(List<Integer> arrList, String file) throws InterruptedException {

		long start = System.currentTimeMillis();
		sort(arrList, arrList.size());
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		List<Long> measuredTimes = times.get(file);
		measuredTimes.add(timeElapsed);
		times.put(file, measuredTimes);
			
		start = System.currentTimeMillis();
		sortParallel(arrList, arrList.size());
		finish = System.currentTimeMillis();
		timeElapsed = finish - start;
		List<Long> measuredTimesParallel = timesParallel.get(file);
		measuredTimesParallel.add(timeElapsed);
		timesParallel.put(file, measuredTimesParallel);
	}

	//Builds the average times the merge algorithms took
	private static void buildAverages(){
		for(String fileName : averageTimes.keySet()){

			Long timeSum = 0L;
			for(Long time : times.get(fileName)){
				timeSum += time;
			}

			Long timeSumParallel = 0L;
			for(Long time : timesParallel.get(fileName)){
				timeSumParallel += time;
			}
			Long averageTime = timeSum / times.get(fileName).size();
			Long averageTimeParallel = timeSumParallel / timesParallel.get(fileName).size();
			averageTimes.get(fileName).add(averageTime);
			averageTimes.get(fileName).add(averageTimeParallel);
		}
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
	public static void sortParallel(List<Integer> a, int n) throws InterruptedException {
		if (n < 2) {
			return;
		}
		int mid = n / 2;
		List<Integer> l = new ArrayList<Integer>();
		List<Integer> r = new ArrayList<Integer>();

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
	public static void sort(List<Integer> a, int n) {
		if (n < 2) {
			return;
		}
		int mid = n / 2;
		List<Integer> l = new ArrayList<Integer>();
		List<Integer> r = new ArrayList<Integer>();

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
	public static void merge(List<Integer> a, List<Integer> l, List<Integer> r, int left, int right) {

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
