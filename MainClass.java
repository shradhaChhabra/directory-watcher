package com.mainclass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.nio.file.Paths;
import java.util.List;

import com.registering.WatchingServices;
import com.utils.Utils;

public class MainClass {
	public static String directory;

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Invalid command line, exactly one argument required");
			System.exit(1);
		}
		Path dir = Paths.get(args[0]);
		System.out.println("Folder: " + args[0]);
		directory = args[0];
		countJavaFiles();
		counter();
		new WatchingServices(dir).processEvents();

	}

	public static void countJavaFiles() {
		Utils utils = new Utils();
		utils.countFilesInDirectory(directory);
		utils.printJavaFilesCount();
	}

	public static void counter() {
		Utils utils = new Utils();
		List<File> files = utils.countFilesInDirectory(directory);
		utils.printLinesCount(files);
	}

}
