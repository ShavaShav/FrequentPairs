import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class APriori extends FrequentPairsGenerator {
	
	public APriori(String filename, double samplePercent, double supportPercent) throws Exception {
		super(filename, samplePercent, supportPercent);
	}

	@Override
	public FrequentItemSets generateFrequentItemSets() throws IOException {
    	frequentItemSets = new FrequentItemSets();
    	
    	firstPass();
    	secondPass();
    	
    	return frequentItemSets;
	}
    	
	// Passes over file basket by basket, counting items. Returns most frequent according to support
    private void firstPass() throws IOException {
    	Map<Integer, Integer> candidateItems = new HashMap<Integer, Integer>();
    	
    	// Will only store current basket in memory, instead of whole file
        List<Integer> basket = null;
        int currentBasket = 0;
    	baskets.reset();

        // Read baskets, keep counts of each item and store frequent items
        while((basket = baskets.nextBasket()) != null && currentBasket < numBaskets) {
            // go through each item, tallying them up         
        	for (Integer item : basket) {
        		if (candidateItems.containsKey(item)) {
        			candidateItems.put(item, candidateItems.get(item) + 1);
        		} else {
        			candidateItems.put(item, 1);
        		}
        	}
        	currentBasket++;
        }   

		// Add the candidates that meet min support to frequent items list
        frequentItemSets.items = new ArrayList<Integer>();

        for (Map.Entry<Integer, Integer> entry : candidateItems.entrySet()) {
			if (entry.getValue() >= support) {
				frequentItemSets.items.add(entry.getKey());
			}
		}
        Collections.sort(frequentItemSets.items);
    }
    
	// Returns most frequent pairs according to support and list of candidate pairs
	private void secondPass() throws IOException {
    	Map<HashSet<Integer>, Integer> supportMap = new HashMap<HashSet<Integer>, Integer>();
    	List<HashSet<Integer>> candidatePairs = createPairs(frequentItemSets.items);
    	
        // Read baskets, keep counts of each item and store frequent pairs
        int currentBasket = 0;
        List<Integer> basket = null;
    	baskets.reset();

		while((basket = baskets.nextBasket()) != null && currentBasket < numBaskets) {       	
            // check if basket contains any candidatePairs. Count them if so
    		for (HashSet<Integer> candidatePair : candidatePairs) {
    			if (basket.containsAll(candidatePair)) {
            		if (supportMap.containsKey(candidatePair)) {
            			supportMap.put(candidatePair, supportMap.get(candidatePair) + 1);
            		} else {
            			supportMap.put(candidatePair, 1);
            		}
    			}
    		}
        	
        	currentBasket++;
        }   

		// Add the candidate pairs that meet min support to frequent items list
        frequentItemSets.pairs = new ArrayList<HashSet<Integer>>();

        for (Entry<HashSet<Integer>, Integer> entry : supportMap.entrySet()) {
			if (entry.getValue() >= support) {
				frequentItemSets.pairs.add(entry.getKey());
			}
		}
	}

	// Demo Apriori
	public static void main(String[] args) {
    	String file = "retail.txt";
    	double samplePercent = 0.3;
    	double supportPercent = 0.05;
    
		try {
			APriori fisGenerator = new APriori(file, samplePercent, supportPercent);
			
	    	long startTime = System.nanoTime();
	    	FrequentItemSets frequentItemSets = fisGenerator.generateFrequentItemSets();    
	    	long endTime = System.nanoTime();

	    	long durationMs = (endTime - startTime) / 1000000;
	    	
	    	// Print frequent sets with time
	    	System.out.println("APriori: " + durationMs + " ms");
	    	System.out.println(frequentItemSets);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
