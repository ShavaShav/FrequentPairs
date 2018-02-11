import java.util.HashSet;
import java.util.List;

// Holds results of algorithms
public class FrequentItemSets {
	public List<Integer> items;
	public List<HashSet<Integer>> pairs;
	
	public String toString() {
		return "Frequent Items: " + items + "\n"
				+ "Frequent Pairs: " + pairs;
	}
}