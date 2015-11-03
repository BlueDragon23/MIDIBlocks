package io;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

public class TestCSVReader {

	@Test
	public void testReadFile() {
		CSVReader csvReader = CSVReader.getInstance();
		ArrayList<ArrayList<String>> scales = null;
		try {
			scales = csvReader.readFile("scales.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assertEquals("natural minor", scales.get(0).get(0));
	}

}
