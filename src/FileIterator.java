import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileIterator {
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	private String filename;
	
	public FileIterator(String filename) throws IOException{
        this.filename = filename;
		fileReader = new FileReader(filename);
        bufferedReader = new BufferedReader(fileReader);
	}
	
	// returns List of Integer items in basket. null if no baskets left
	public List<Integer> nextBasket() throws IOException {
		String line = null;
		line = bufferedReader.readLine();
		ArrayList<Integer> basket = new ArrayList<Integer>();
		if (line != null) {	
			for (String item : line.split("\\s")) {
				basket.add(Integer.valueOf(item));
			}
		} else {
			// end of file
			return null;
		}
		return basket;
	}

	// resets iterator to beginning of file
	public void reset() throws IOException {
		try {
			fileReader.close();
			bufferedReader.close();	
		} finally {
			fileReader = new FileReader(filename);
	        bufferedReader = new BufferedReader(fileReader);	
		}
	}
}
