package MVB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import com.google.api.client.json.Json;
import com.google.api.client.json.JsonObjectParser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.*;

public final class App {
    public static final String URL = "https://sudoku.com/api/getLevel/easy";

    public static void main(String[] args) throws Exception {
        // solveWebSudoku();
        solveCSVSudoku();
    }

    public static void solveCSVSudoku() {
        Iterator<CSVRecord> sudokuIterator = null;
        try {
            Reader in = new FileReader(
                    "C:\\Users\\Matt\\Documents\\Code\\sudoku\\Sudoku\\src\\main\\java\\MVB\\sudoku.csv");
            CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT.withHeader("Problem", "Solution"));
            sudokuIterator = parser.iterator();
            sudokuIterator.next();
        } catch (IOException e) {
            System.err.println("IO ERROR ON CSV: " + e.getLocalizedMessage());

        }
        if (sudokuIterator == null) {
            return;
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter("newFile.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (writer == null) {
            return;
        }
        while (sudokuIterator.hasNext()) {
            CSVRecord record = sudokuIterator.next();
            String problem = record.get("Problem");
            Sudoku s = new Sudoku(problem);
            s.solve();
            try {
                writer.write(record.getRecordNumber() + "," + s.toCheckString().equals(record.get("Solution")) + ","
                        + s.toCheckString() + "," + record.get("Solution") + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void solveWebSudoku() {
        String imported_sudoku = getSudoku();
        if (imported_sudoku == null) {
            return;
        }
        JSONObject sudoku_json = new JSONObject(imported_sudoku);
        JSONArray sudoku_array = (JSONArray) sudoku_json.get("desc");
        Sudoku s = new Sudoku((String) sudoku_array.get(0));
        s.solve();
        System.out.println(sudoku_array.get(1).equals(s.toCheckString()));
    }

    private static String getSudoku() {
        String imported_sudoku = null;
        try {
            HttpsURLConnection con = get_sudoku_connection(URL);
            imported_sudoku = get_string_from_connection(con);
        } catch (MalformedURLException e) {
            System.err.println("BAD URL");
        } catch (IOException e) {
            System.err.println("BAD CONNECTION");
        }
        return imported_sudoku;
    }

    private static HttpsURLConnection get_sudoku_connection(String Url) throws MalformedURLException, IOException {
        URL url = new URL(Url);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        return con;
    }

    private static String get_string_from_connection(HttpsURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String s = null;
        StringBuilder sb = new StringBuilder();
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        return sb.toString();
    }
}
