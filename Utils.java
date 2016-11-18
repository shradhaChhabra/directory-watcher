package com.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Utils {

	private int count = 0;

	/**
	 * Count files in a directory (including files in all subdirectories)
	 * 
	 * @param directory
	 *            the directory to start in
	 * @return the total number of files
	 */
	public List<File> countFilesInDirectory(String directoryName) {
		File directory = new File(directoryName);

		List<File> resultList = new ArrayList<File>();
		// get all the files from a directory
		File[] fList = directory.listFiles();		
		for (File file : fList) {
			if (file.isFile() && (file.getName().endsWith(".txt"))) {
			    resultList.addAll(Arrays.asList(file));
				count++;
				// System.out.println(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				resultList.addAll(countFilesInDirectory(file.getAbsolutePath()));
			}
		}
		//System.out.println(resultList);
		return resultList;
	}
	
	public long countingLines(File resultList) {
		long lineCount = 0;
		// for (File paths : resultList) {
		try (Stream<String> stream = Files.lines(resultList.toPath())) {
			lineCount = stream.filter(line -> !line.startsWith("//")).count();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return lineCount;
		//list = list + lineCount;
	}

	public void printJavaFilesCount() {
		System.out.println("Java Files: " + count);
	}

	public void printLinesCount(List<File> file) {
		long lc = 0;
		long totalCount = 0;
		for (File files : file) {
			//if (!files.isDirectory()&& files.getName().endsWith(".txt")) {
				{
				    lc = countingLines(files);
			}
			//}
			totalCount = totalCount + lc;
		}
		System.out.println("Source lines: " + totalCount);
	}
}
