import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ReduceTask implements Callable<ReduceClass> {
    private final File inFile;
    private final List<Map<Integer, Integer>> localMergedDictionary = new ArrayList<>();
    private final List<String> localMergedLongestWord = new ArrayList<>();
    private final List<MapClass> reduceTasksFiles;
    private Map<Integer, Integer> mergedDictionary = new HashMap<>();
    private List<String> mergedLongestWord = new ArrayList<>();
    private int longestWordsNo;
    private int wordsCount;
    private float documentRank;
    int longestWordSize;

    public ReduceTask(File inFile, List<MapClass> reduceTasksFiles) {
        this.inFile = inFile;
        this.reduceTasksFiles = reduceTasksFiles;
    }

    private void combine() {
        Map<Integer, Integer> unsortedDictionary = new HashMap<>();

        for (MapClass task : reduceTasksFiles) {
                localMergedLongestWord.addAll(task.getLongestWord());
                localMergedDictionary.add(task.getDictionary());
            }


        for (var word : localMergedDictionary) {
            for (Map.Entry<Integer, Integer> entry : word.entrySet()) {
                if (unsortedDictionary.containsKey(entry.getKey())) {
                    unsortedDictionary.put(entry.getKey(), unsortedDictionary.get(entry.getKey()) + entry.getValue());
                } else {
                    unsortedDictionary.put(entry.getKey(), entry.getValue());
                }
            }
        }

        mergedDictionary = MapTask.sortByKey(unsortedDictionary);

        longestWordSize = mergedDictionary.keySet().stream().findFirst().get();

        for (String word : localMergedLongestWord) {
            if (word != null && word.length() == longestWordSize) {
                mergedLongestWord.add(word);
                longestWordsNo ++;
            }
        }

        wordsCount = mergedDictionary.values().stream().mapToInt(d -> d).sum();

    }

    private void process() {
        float fibonacciRankValue = 0;
        for (var word : mergedDictionary.entrySet()) {
            fibonacciRankValue += (getFibonacciValue(word.getKey() + 1) * word.getValue());
        }

        documentRank = fibonacciRankValue / wordsCount;
    }

    private float getFibonacciValue(int index) {
        if (index <= 1) {
            return index;
        }
        float result = 1;
        float previous = 1;

        for (int i = 2; i < index; i++) {
            float temp = result;
            result += previous;
            previous = temp;
        }
        return result;
    }

    @Override
    public ReduceClass call() {
        this.combine();
        this.process();
        return new ReduceClass(inFile.getName(), documentRank, longestWordSize, longestWordsNo);
    }

    @Override
    public String toString() {
        return "\nReduceTask{" +
                "inFile=" + inFile +
                ", mergedDictionary=" + mergedDictionary +
                ", mergedLongestWord=" + mergedLongestWord +
                '}';
    }
}
