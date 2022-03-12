public final class ReduceClass {
    private final String inFile;
    private final float documentRank;
    private final int longestWord;
    private final int longestWordCount;

    public ReduceClass(String inFile, float documentRank, int longestWord, int longestWordCount) {
        this.inFile = inFile;
        this.documentRank = documentRank;
        this.longestWord = longestWord;
        this.longestWordCount = longestWordCount;
    }

    public String getInFile() {
        return inFile;
    }

    public float getDocumentRank() {
        return documentRank;
    }

    public int getLongestWord() {
        return longestWord;
    }

    public int getLongestWordCount() {
        return longestWordCount;
    }

    @Override
    public String toString() {
        return "\nReduceClass{" +
                "inFile=" + inFile +
                ", documentRank=" + documentRank +
                ", longestWord=" + longestWord +
                ", longestWordCount=" + longestWordCount +
                '}';
    }
}
