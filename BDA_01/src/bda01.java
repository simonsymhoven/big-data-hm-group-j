import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class bda01 {

	public static void main(String[] args) throws IOException {
		Random rd = new Random();
		int[] arr = new int[1000000];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = rd.nextInt();
		}
		System.out.println("Array size: " + arr.length);

		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter("file.txt"));
		for (int i = 0; i < arr.length; i++) {
			outputWriter.write(Integer.toString(arr[i]));
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
	}

}
