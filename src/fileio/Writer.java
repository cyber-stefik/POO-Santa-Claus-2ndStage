package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.AnnualChildren;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The class writes the output in files
 * <p>
 * DO NOT MODIFY
 */
public final class Writer {
    /**
     * The file where the data will be written
     */
    private final FileWriter file;

    public Writer(final String path) throws IOException {
        this.file = new FileWriter(path);
    }

    /**
     * Transforms the output in a JSONObject
     * @return An JSON Object
     * @throws IOException in case of exceptions to reading / writing
     */
    public void writeFile(final AnnualChildren annualChildren,
                                final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new
                                               File(filePath2), annualChildren);
    }

    /**
     * writes to the file and close it
     *
     * @param array of JSON
     */
    public void closeJSON(final JSONArray array) {
        try {
            file.write(array.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
