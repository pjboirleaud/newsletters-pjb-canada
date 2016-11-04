package org.pjb.newsletterscanada.templates;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pjb.newsletterscanada.config.Config;
import org.pjb.newsletterscanada.config.ConfigKeys;

public class Main {

	public static String PAGES_PATH = Config.getConfig().getString(ConfigKeys.PAGES_PATH,
			ConfigKeys.DEFAULT.PAGES_PATH);
	public static List<String> EXCLUDE_FOLDERS = Config.getConfig().getStringList(ConfigKeys.EXCLUDE_FOLDERS,
			ConfigKeys.DEFAULT.EXCLUDE_FOLDERS);
	public static String FOR_EACH_NEWSLETTER_START = Config.getConfig().getString(ConfigKeys.FOR_EACH_NEWSLETTER_START,
			ConfigKeys.DEFAULT.FOR_EACH_NEWSLETTER_START);
	public static String FOR_EACH_NEWSLETTER_END = Config.getConfig().getString(ConfigKeys.FOR_EACH_NEWSLETTER_END,
			ConfigKeys.DEFAULT.FOR_EACH_NEWSLETTER_END);
	public static String N = Config.getConfig().getString(ConfigKeys.N, ConfigKeys.DEFAULT.N);
	public static String DATE = Config.getConfig().getString(ConfigKeys.DATE, ConfigKeys.DEFAULT.DATE);
	public static String TITLE = Config.getConfig().getString(ConfigKeys.TITLE, ConfigKeys.DEFAULT.TITLE);

	private final static Map<String, String> title = new HashMap<String, String>();
	private final static Map<String, String> date = new HashMap<String, String>();

	public static void main(String[] args) {
		File folder = new File(PAGES_PATH);
		if (!folder.isDirectory()) {
			throw new RuntimeException("Bad path (" + PAGES_PATH + ").");
		}
		fetchAllData();
		applyTemplates();
	}

	private static void fetchAllData() {
		System.out.println("Fetching data from pages...");
		try {
			Files.list(Paths.get(PAGES_PATH)).filter(Files::isDirectory)
					.filter(f -> !EXCLUDE_FOLDERS.contains(f.getFileName().toString())).forEach(Main::fetchData);
		} catch (IOException e) {
			throw new RuntimeException("IO Exception occured. ", e);
		}
		System.out.println("All data fetched !");
	}

	private static void fetchData(Path file) {
		String n = file.getFileName().toString();
		title.put(n, getTitle(file));
		date.put(n, getDate(file));
		System.out.println(
				"   - Newsletter #" + n + " -> " + "title : \"" + title.get(n) + "\", date : \"" + date.get(n) + "\"");
	}

	private static void applyTemplates() {
		System.out.println();
		System.out.println("Applying templates...");
		try {
			Files.list(Paths.get(PAGES_PATH)).filter(Files::isRegularFile)
					.filter(f -> f.getFileName().toString().endsWith(".template")).forEach(Main::applyTemplate);
		} catch (IOException e) {
			throw new RuntimeException("IO Exception occured. ", e);
		}
		System.out.println("Applied all templates !");
	}

	private static void applyTemplate(Path template) {
		String destination = template.toString().substring(0, template.toString().length() - ".template".length());
		System.out.println("   - Apply template : " + template + " -> " + destination);

		try {
			String templateContent = new String(Files.readAllBytes(template));

			do {
				int boucle_start = templateContent.indexOf(FOR_EACH_NEWSLETTER_START);
				int boucle_end = templateContent.indexOf(FOR_EACH_NEWSLETTER_END);

				if (boucle_start == -1 || boucle_end == -1) {
					break;
				}

				String toEval = templateContent.substring(boucle_start + FOR_EACH_NEWSLETTER_START.length(),
						boucle_end);
				String compiled = "";

				for (String n : title.keySet()) {
					String t = title.get(n), d = date.get(n);
					compiled += toEval.replace(N, n).replace(TITLE, t).replace(DATE, d);
				}

				templateContent = compiled = templateContent.substring(0, boucle_start) + compiled
						+ templateContent.substring(boucle_end + FOR_EACH_NEWSLETTER_END.length());

			} while (templateContent.indexOf(FOR_EACH_NEWSLETTER_START) != -1);

			List<String> lines = new ArrayList<String>();
			lines.add(templateContent);
			Files.write(new File(destination).toPath(), lines, Charset.forName("UTF-8"));

		} catch (IOException e) {
			throw new RuntimeException("IO Exception occured. ", e);
		}
	}

	private static String getTitle(Path file) {
		Path index = file.resolve("index.html");
		try {
			List<String> title = Files.lines(index)
					.filter(line -> line.contains("<h1>") && line.contains("<span class=\"title-date\">")).map(line -> {
						int titlePosition = line.indexOf("<h1>");
						int endTitlePosition = line.indexOf("<span class=\"title-date\">");
						return line.substring(titlePosition + 4, endTitlePosition).trim();
					}).collect(Collectors.toList());
			if (title.isEmpty()) {
				throw new RuntimeException("Could not get title from html page : no title found. ");
			}
			return title.get(0);
		} catch (IOException e) {
			throw new RuntimeException("Could not get title from html page. ", e);
		}
	}

	private static String getDate(Path file) {
		Path index = file.resolve("index.html");
		try {
			List<String> title = Files.lines(index)
					.filter(line -> line.contains("<span class=\"title-date\">") && line.contains("</span>"))
					.map(line -> {
						int titlePosition = line.indexOf("<span class=\"title-date\">");
						int endTitlePosition = line.indexOf("</span>");
						return line.substring(titlePosition + 25, endTitlePosition).trim();
					}).collect(Collectors.toList());
			if (title.isEmpty()) {
				throw new RuntimeException("Could not get date from html page : no date found. ");
			}
			return title.get(0);
		} catch (IOException e) {
			throw new RuntimeException("Could not get date from html page. ", e);
		}
	}
}
