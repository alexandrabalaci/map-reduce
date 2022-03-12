import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Tema2 {
    static long readFragment;
    static long documentsNo;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        int threadsNo = Integer.parseInt(args[0]);
        String inFile = String.valueOf(args[1]);
        String outFile = String.valueOf(args[2]);
        List<File> inFiles = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(inFile));
            readFragment = Long.parseLong(scanner.nextLine());
            documentsNo = Long.parseLong(scanner.nextLine());
            while (scanner.hasNext()) {
                String name = scanner.nextLine();
                File inpF = new File(name);
                inFiles.add(inpF);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExecutorService executor = Executors.newFixedThreadPool(threadsNo);

        List<MapTask> tasks = new ArrayList<>();
        for (int i = 0; i < documentsNo; i++) {
            long docSize = inFiles.get(i).length();
            int offset = 0;
            while (docSize > 0) {

                long bytesLeft = inFiles.get(i).length() - offset;
                if (bytesLeft >= readFragment) {
                    bytesLeft = readFragment;
                }
                MapTask task = new MapTask(inFiles.get(i), offset, readFragment);
                tasks.add(task);
                offset += readFragment;
                docSize -= bytesLeft;
            }
        }

        List<Future<MapClass>> results = null;
        List<MapClass> resultsAfterMap = new ArrayList<>();
        try {
            results = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        if (results != null) {
            for (Future<MapClass> future : results) {
                try {
                    MapClass result = future.get();
                    resultsAfterMap.add(result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        List<ReduceTask> reduceTasks = new ArrayList<>();
        Map<String, List<MapClass>> mapTaskResults = resultsAfterMap.stream().collect(Collectors.groupingBy(MapClass::getInFile));

        for (int i = 0; i < documentsNo; i++) {
            ReduceTask reduceTask = new ReduceTask(inFiles.get(i), mapTaskResults.get(inFiles.get(i).getName()));
            reduceTasks.add(reduceTask);
        }

        executor = Executors.newFixedThreadPool(threadsNo);

        List<Future<ReduceClass>> reduceResults = null;
        List<ReduceClass> reduceResultsMerged = new ArrayList<>();

        try {
            reduceResults = executor.invokeAll(reduceTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        assert reduceResults != null;
        for (Future<ReduceClass> future : reduceResults) {
            try {
                ReduceClass result = future.get();
                reduceResultsMerged.add(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        reduceResultsMerged.sort(Comparator.comparing(ReduceClass::getDocumentRank).reversed());

        for (var finalTasks : reduceResultsMerged) {
            StringBuilder builder = new StringBuilder();
            String documentName = finalTasks.getInFile();
            int longestWord = finalTasks.getLongestWord();
            builder.append(documentName).append(",").append(String.format("%.2f", finalTasks.getDocumentRank())).append(",")
                    .append(longestWord).append(",").append((finalTasks.getLongestWordCount()));

            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(outFile, true));
                writer.append(builder);
                writer.append("\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}