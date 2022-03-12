import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.Callable;

public class MapTask implements Callable<MapClass> {
    private final File inFile;
    private int offset;
    private long readFragment;
    String separators = ";:/?~\\.,><`\\[]\\{}\\(\\)!@#\\$%\\^&-_\\+'=\\*\\\\\"\\| \t\r\n";
    private List<String> longestWord = new ArrayList<>();
    String beforeLetter;
    String lastLetter;
    String afterString;
    /* sirul de caractere inainte de seprare*/
    private String chars = null;
    /* lista de cuvinte separate corect */
    private List<String> words = new ArrayList<>();
    /* dictionarul final cu lungimea cuvintelor si nr de aparitii */
    private Map<Integer, Integer> dictionary = new HashMap<>();
    /* dictionar intermediar cu cuvant si nr de aparitii*/
    private Map<String, Integer> wordsCount = new HashMap<>();

    public MapTask(File inFile, int offset, long readFragment) {
        this.inFile = inFile;
        this.offset = offset;
        this.readFragment = readFragment;
    }

    @Override
    public MapClass call() {
        Map<Integer, Integer> sortedDictionary;
        readFragment();
        int min = 0;
        for (String word : words) {
            if (dictionary.containsKey(word.length())) {

                dictionary.put(word.length(), dictionary.get(word.length()) + 1);
            } else {
                this.dictionary.put(word.length(), 1);
            }

            if (word.length() >= min) {
                min = word.length();
            }
        }
        for (String word : words) {
            if (word.length() >= min) {
                longestWord.add(word);
            }
        }

        sortedDictionary = sortByKey(dictionary);

        return new MapClass(inFile.getName(), this.longestWord, sortedDictionary);

    }


    static Map<Integer, Integer> sortByKey(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((Comparator<Object>) (o1, o2) -> ((Comparable<Integer>) ((Map.Entry<Integer, Integer>) (o2)).getKey()).
                compareTo(((Map.Entry<Integer, Integer>) (o1)).getKey()));

        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    void readFragment() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(inFile, "r");

            randomAccessFile.seek(offset);
            byte[] dest = new byte[(int) readFragment];
            byte[] dest1 = new byte[1];
            byte[] dest2 = new byte[10];
            byte[] dest4 = new byte[1];
            randomAccessFile.read(dest);
            chars = new String(dest);
            if (offset > 0) {
                randomAccessFile.seek(offset - 1);
                randomAccessFile.read(dest1);
                beforeLetter = new String(dest1);
            }

            randomAccessFile.seek(offset + readFragment);
            randomAccessFile.read(dest2);
            afterString = new String(dest2);

            randomAccessFile.seek(offset + readFragment);
            randomAccessFile.read(dest4);
            lastLetter = new String(dest4);

            StringTokenizer st = new StringTokenizer(this.chars, separators);
            String word;
            if (beforeLetter != null && !separators.contains(beforeLetter)) {
                st.nextToken();
            }

            while (st.hasMoreTokens()) {
                words.add(st.nextToken());
            }

            String concatenatedWord;
            if (this.afterString != null) {
                StringTokenizer afterStringStr = new StringTokenizer(this.afterString, separators);
                String clean;
                if (afterStringStr.hasMoreTokens()) {
                    String toConcat = afterStringStr.nextToken();
                    clean = Objects.requireNonNull(toConcat).replaceAll("\\P{Print}", "");
                    String temp = chars.substring(chars.length() - 1);

                    if (!separators.contains(lastLetter) && !separators.contains(temp) && (words.size() > 0)) {
                        word = words.get(words.size() - 1);
                        words.remove(words.size() - 1);
                        concatenatedWord = word.concat(clean);
                        words.add(Objects.requireNonNull(concatenatedWord).replaceAll("\\P{Print}", ""));
                    }
                }

            }
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "\nTask{" +
                "inFile=" + inFile +
                ", offset=" + offset +
                ", readFragment=" + readFragment +
                ", longestWord='" + longestWord + '\'' +
                ", dictionary=" + dictionary +
                ", wordsCount=" + wordsCount +
                '}';
    }
}