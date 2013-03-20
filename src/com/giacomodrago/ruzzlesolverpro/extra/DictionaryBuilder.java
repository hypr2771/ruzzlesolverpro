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

	private static final int MAX_WORD_LENGTH = 16;
	
	public static void main(String[] args) throws IOException {

		String inputDirectory = args[0];
		String outputFile = args[1];
		
		File destFile = new File(outputFile);
		PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(destFile)), true);
		
		File folder = new File(inputDirectory);
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
				word = word.trim().toLowerCase(); // Word to lower case
				// Replace � (for German)
				word = word.replaceAll("�", "ss");
				if (!word.isEmpty() && word.length() <= MAX_WORD_LENGTH && !words.contains(word)) {
					// Remove dashes and apostrophes
					word = word.replaceAll("['-]", "");
					words.add(word);
				}
			}
			br.close();
		}
		
		for (String word : words) {
			writer.println(word);
		}
		
		System.out.println("Done. Written "+words.size()+" words in file " + outputFile);
		
		writer.flush();
		writer.close();

	}

}
