package org.pjb.newsletterscanada.genpdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

	public final static String PAGES_PATH = "../website/canada/";
	public final static String PDF_PATH = "D:/boirl/Google Drive/Canada/Newsletters/"; // "../website/canada/pdf/";
	public final static List<String> EXCLUDE = Arrays.asList(new String[] { ".", "..", "pdf", "js", "css", "img" });
	public final static String COMMAND = "C:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltopdf.exe --print-media-type";
	public static boolean REGERATE_ALL = false;

	public static void main(String[] args) {
		if (args.length == 1) {
			if (args[0].equals("--generate-all")) {
				REGERATE_ALL = true;
			} else if (args[0].equals("--help")) {
				System.out.println("Usage: java -classpath . -jar nlcgenpdf.jar");
				System.out.println("Usage: java -classpath . -jar nlcgenpdf.jar --generate-all");
				return;
			}
		}
		File folder = new File(PAGES_PATH);
		if (!folder.isDirectory()) {
			throw new RuntimeException("Bad path (" + PAGES_PATH + ").");
		}
		try {
			Files.list(Paths.get(PAGES_PATH)).filter(Files::isDirectory)
					.filter(f -> !EXCLUDE.contains(f.getFileName().toString())).forEach(Main::generatePDF);
		} catch (IOException e) {
			throw new RuntimeException("IO Exception occured. ", e);
		}
	}

	private static void generatePDF(Path file) {
		// System.out.println("generatePDF: " + file);
		String title = getTitle(file);
		String pdfFileName = PDF_PATH + title + ".pdf";
		if (!REGERATE_ALL && Files.exists(new File(pdfFileName).toPath())) {
			System.out.println("Skipping PDF file : " + title + ".pdf");
			return;
		}
		try {
			System.out.println("Generating PDF file : " + title + ".pdf ...");
			String command = COMMAND + " file:///"
					+ file.resolve("index.html").toAbsolutePath().normalize().toString().replace("\\", "/") + " \""
					+ pdfFileName + "\"";
			// System.out.println("command : " + command);
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			throw new RuntimeException("Could not generate PDF file : " + title + ".pdf", e);
		}
	}

	private static String getTitle(Path file) {
		Path index = file.resolve("index.html");
		try {
			List<String> title = Files.lines(index)
					.filter(line -> line.contains("<title>") && line.contains("</title>")).map(line -> {
						int titlePosition = line.indexOf("<title>");
						int endTitlePosition = line.indexOf("</title>");
						return line.substring(titlePosition + 7, endTitlePosition).trim();
					}).collect(Collectors.toList());
			if (title.isEmpty()) {
				throw new RuntimeException("Could not get PDF title from html page : no title found. ");
			}
			return title.get(0);
		} catch (IOException e) {
			throw new RuntimeException("Could not get PDF title from html page. ", e);
		}
	}
}
