import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PCY extends FrequentPairsGenerator {

	private int numBuckets;
	private BitSet bitBuckets;
	
	public PCY(String filename, double samplePercent, double supportPercent) throws Exception {
		super(filename, samplePercent, supportPercent);
	}

	@Override
	public FrequentItemSets generateFrequentItemSets() throws IOException {
    	frequentItemSets = new FrequentItemSets();
    	
    	int[] buckets = firstPass();
    	
    	// Transform buckets to bit vector (PCY)
        bitBuckets = new BitSet(numBuckets);
    	for (int i = 0; i < numBuckets; i ++) {
    		if (buckets[i] >= support) {
    			bitBuckets.set(i);
    		}
    	}
    	
    	secondPass();
    	
    	return frequentItemSets;
	}
	
	// Passes over file basket by basket, counting items and buckets. Populates frequent items and returns int buckets
    private int[] firstPass() throws IOException {
    	Map<Integer, Integer> candidateItems = new HashMap<Integer, Integer>();
    	
    	// Fill rest of memory with buckets (4-byte integers)
    	numBuckets = (int) Runtime.getRuntime().freeMemory() / 4;
    	int[] buckets = new int[numBuckets];
    	
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
        	
        	// PCY hashing
        	for (int i = 0; i < basket.size(); i++) {
        		for (int j = i + 1; j < basket.size(); j++) {
        			int item1 = basket.get(i);
        			int item2 = basket.get(j);
        			Integer[] pair = {item1, item2};
        			buckets[hash(pair)]++;
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
        
        return buckets;
    }
    
	// Populates most frequent pairs using bit buckets and frequent items
	private void secondPass() {
		if (frequentItemSets.items == null) {
			return;
		}
		// Create pairs from frequent items
		List<HashSet<Integer>> candidatePairs = createPairs(frequentItemSets.items);
		
		// Add candidate pairs that are hashed to a frequent bucket
        frequentItemSets.pairs = new ArrayList<HashSet<Integer>>();
        for (HashSet<Integer> pair : candidatePairs) {
        	int hashCode = hash(pair.toArray(new Integer[pair.size()]));
        	if (bitBuckets.get(hashCode)) {
        		frequentItemSets.pairs.add(pair);
        	}
        }
	}
	
	private int hash(Integer[] itemSet) {
		int hashCode = 1;
		for (int item : itemSet)
			hashCode *= item;
		return hashCode % numBuckets;
	}
	
	// Demo PCY
	public static void main(String[] args) {
    	String file = "retail.txt";
    	double samplePercent = 0.3;
    	double supportPercent = 0.05;
    
		try {
			PCY fisGenerator = new PCY(file, samplePercent, supportPercent);
			
	    	long startTime = System.nanoTime();
	    	FrequentItemSets frequentItemSets = fisGenerator.generateFrequentItemSets();    
	    	long endTime = System.nanoTime();

	    	long durationMs = (endTime - startTime) / 1000000;
	    	
	    	// Print frequent sets with time
	    	System.out.println("PCY: " + durationMs + " ms");
	    	System.out.println(frequentItemSets);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
