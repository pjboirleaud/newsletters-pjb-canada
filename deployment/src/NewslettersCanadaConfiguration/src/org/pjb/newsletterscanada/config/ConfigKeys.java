package org.pjb.newsletterscanada.config;

public class ConfigKeys {

	public final static String PAGES_PATH = "pages.path";
	public final static String PDF_PATH = "pdf.path";
	public final static String EXCLUDE_FOLDERS = "exclude.folders";

	public final static String PDF_COMMAND = "pdf.command";

	public final static String FOR_EACH_NEWSLETTER_START = "for.each.newsletter.start";
	public final static String FOR_EACH_NEWSLETTER_END = "for.each.newsletter.end";
	public final static String N = "n";
	public final static String DATE = "date";
	public final static String TITLE = "title";

	public static class DEFAULT {
		public final static String PAGES_PATH = "../website/canada/";
		public final static String PDF_PATH = "D:/boirl/Google Drive/Canada/Newsletters/";
		public final static String EXCLUDE_FOLDERS = "., .., pdf, js, css, img";

		public final static String PDF_COMMAND = "C:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltopdf.exe --print-media-type";

		public final static String FOR_EACH_NEWSLETTER_START = "${forEachNewsletter('start')}";
		public final static String FOR_EACH_NEWSLETTER_END = "${forEachNewsletter('end')}";
		public final static String N = "${N}";
		public final static String DATE = "${DATE}";
		public final static String TITLE = "${TITLE}";
	}
}
