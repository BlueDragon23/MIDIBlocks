package io;

import MIDIBlocks.Note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVReader {

    private static CSVReader csvReader;

    private CSVReader() {

    }

    /**
     * Get an instance of the CSVReader
     * 
     * @return
     */
    public static CSVReader getInstance() {
        if (csvReader != null) {
            return csvReader;
        } else {
            csvReader = new CSVReader();
            return csvReader;
        }
    }

    /**
     * Read a CSV file and return a list of scales. This is represented as a
     * list of lists of note names
     * 
     * @param filename
     *            the filename for the scales csv
     * @return A list of scales
     * @throws FileNotFoundException
     */
    public ArrayList<ArrayList<String>> readFile(String filename)
                    throws FileNotFoundException {
        Scanner scan = new Scanner(new BufferedReader(new FileReader(new File(
                        filename))));
        ArrayList<ArrayList<String>> scales = new ArrayList<>();
        ArrayList<String> scale;
        while (scan.hasNextLine()) {
            scale = readLine(scan.nextLine());
            if (scale.size() > 0) {
                boolean validScale = true;
                String str;
                for (int i = 1; i < scale.size(); i++) {
                    str = scale.get(i);
                    if (!(Note.noteNames.contains(str))) {
                        /* Not a real note */
                        validScale = false;
                        break;
                    }
                }
                if (validScale) {
                    scales.add(scale);
                }
            }
        }
        scan.close();
        return scales;
    }

    /**
     * Reads a line of csv. Returns an empty list if there is an error
     * 
     * @param line
     *            the line of csv to read
     * @return an ArrayList containing the mode, followed by a full octave of
     *         notes, or an empty list
     */
    private ArrayList<String> readLine(String line) {
        ArrayList<String> scale = new ArrayList<>();
        Scanner scan = new Scanner(line);
        scan.useDelimiter(",");
        if (scan.hasNext()) {
            String mode = scan.next();
            scale.add(mode);
        } else {
            scan.close();
            return scale;
        }
        String rootNote;
        if (scan.hasNext()) {
            rootNote = scan.next();
            scale.add(rootNote);
        } else {
            scan.close();
            scale.clear();
            return scale;
        }
        String note;
        while (scan.hasNext()) {
            note = scan.next();
            if (note.equals(rootNote)) {
                scale.add(note);
                scan.close();
                return scale;
            } else {
                scale.add(note);
            }
        }
        /* The root note wasn't repeated before end of line, not a valid scale */
        scale.clear();
        scan.close();
        return scale;
    }

}
