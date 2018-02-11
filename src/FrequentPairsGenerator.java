import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class FrequentPairsGenerator {
	
	protected FileIterator baskets; // iterator for file, returns basket by basket
	protected int support;
	protected int numBaskets;
	protected FrequentItemSets frequentItemSets; // results

    
	// Calculates frequent pairs using A Priori and PCY. Times execution of each algorithm
    public FrequentPairsGenerator (String filename, double samplePercent, double supportPercent) throws Exception {
    	this.baskets = new FileIterator(filename);
    	this.numBaskets = calcNumBaskets(samplePercent, filename);
    	this.support = calcSupport(supportPercent);
    }
    
    // returns number of lines in file.
	private int calcNumBaskets(double samplePercent, String filename) throws Exception {
    	if (samplePercent > 1.0 || samplePercent <= 0.0) {
    		throw new Exception("Sample size is invalid");
    	} 
    	
        LineNumberReader lineReader = new LineNumberReader(new FileReader(Paths.get(filename).toFile()));
        lineReader.skip(Long.MAX_VALUE);

        int numBaskets = (int) ( ( lineReader.getLineNumber() + 1 ) * samplePercent);

        lineReader.close();
        
        return numBaskets;
    }
    
    // returns the minimum support count according to number of baskets and given support percentage
    private int calcSupport(double supportPercent) throws Exception {
    	if (supportPercent > 1.0 || supportPercent < 0.0) {
    		throw new Exception("Support is invalid");
    	} else {
        	return (int) ( supportPercent * numBaskets );
    	}
    }
    
    public abstract FrequentItemSets generateFrequentItemSets() throws IOException;
    
	// Returns combinations of frequent items, for use in second pass
    protected List<HashSet<Integer>> createPairs(List<Integer> frequentItems) {
    	List<HashSet<Integer>> candidatePairs = new ArrayList<HashSet<Integer>>();
    	
    	for (int i = 0; i < frequentItems.size(); i++) {
    		for (int j = i + 1; j < frequentItems.size(); j++) {
    			HashSet<Integer> pair = new HashSet<Integer>();
    			pair.add(frequentItems.get(i));
    			pair.add(frequentItems.get(j));
    			candidatePairs.add(pair);
    		}
    	}
    	
    	return candidatePairs;
    }

}
