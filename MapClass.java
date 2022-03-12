import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapClass {
    private final String inFile;
    private final List<String> longestWord;
    private Map<Integer, Integer> dictionary = new HashMap<>();


    public MapClass(String inFile, List<String> longestWord, Map<Integer, Integer> dictionary) {
        super();
        this.inFile = inFile;
        this.longestWord = longestWord;
        this.dictionary = dictionary;
    }

    public String getInFile() {
        return inFile;
    }

    public List<String> getLongestWord() {
        return longestWord;
    }

    public Map<Integer, Integer> getDictionary() {
        return dictionary;
    }

    @Override
    public String toString() {
        return "\nMapClass{" +
                "inFile=" + inFile +
                ", longestWord='" + longestWord + '\'' +
                ", dictionary=" + dictionary +
                '}';
    }
}
