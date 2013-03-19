package com.giacomodrago.ruzzlesolverpro.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

public class DictionaryBuilder {

	private static final String INPUT_DIRECTORY = "dict_sources";
	private static final String OUTPUT_FILE = "dict_output.txt";

	public static void main(String[] argv) throws IOException {

		File destFile = new File(OUTPUT_FILE);
		PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(destFile)), true);
		
		File folder = new File(INPUT_DIRECTORY);
		File[] listOfFiles = folder.listFiles();
		Set<String> words = new TreeSet<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			File f = listOfFiles[i];
			if (f.isHidden() || f.getName().startsWith(".")) {
				continue;
			}
			System.out.println("Reading file: " + f.getName());
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(f)));
			String word;
			while ((word = br.readLine()) != null) {
				word = word.trim().toLowerCase();
				if (!word.isEmpty() && !words.contains(word)) {
					words.add(word);
				}
			}
			br.close();
		}
		
		for (String word : words) {
			writer.println(word);
		}
		
		System.out.println("Done. Written "+words.size()+" words in file " + OUTPUT_FILE);
		
		writer.flush();
		writer.close();

	}

}
