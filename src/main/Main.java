package main;

import action.Action;
import checker.Checker;
import common.Constants;
import database.Database;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Class used to run the code
 */
public final class Main {
    public static final int DECIMAL = 10;
    public static final int POSSIBLYUNIT = 11;

    private Main() {
        ///constructor for checkstyle
    }
    /**
     * This method is used to call the checker which calculates the score
     * @param args
     *          the arguments used to call the main method
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.OUTPUT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filename = file.toString();
            String filepath = getOutputName(filename);
            System.out.println(filename);
            action(file.getAbsolutePath(), filepath);
        }
        Checker.calculateScore();
    }

    private static String getOutputName(final String filename) {
        StringBuilder testNumber = new StringBuilder();
        testNumber.append(filename.charAt(DECIMAL));
        if (filename.charAt(POSSIBLYUNIT) != '.') {
            testNumber.append(filename.charAt(POSSIBLYUNIT));
        }
        return Constants.OUTPUT_PATH + testNumber + ".json";
    }

    /**
     *
     * @param filePath1 absolute path of the input file
     * @param filePath2 path for the output
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        // My EntryPoint
        Database database = Database.getDatabase();
        database.addData(input);
        Action.solve(database, fileWriter, filePath2);
    }
}
