package io.vertigo.chatbot.commons;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Utility class for HTML input sanitization.
 * Provides methods to clean and secure HTML content using OWASP HTML Sanitizer.
 *
 * @author Chatbot Team
 */
public class HtmlInputUtils {

	private HtmlInputUtils() {
		//utils class
	}

	/**
	 * Sanitize HTML content using OWASP HTML Sanitizer
	 *
	 * @param in HTML content to sanitize
	 * @param targetBlankLinks if true, adds target="_blank" to all links
	 * @return sanitized HTML content
	 */
	public static String sanitizeHtml(final String in, final Boolean targetBlankLinks) {
		PolicyFactory sanitizer = Sanitizers.FORMATTING
				.and(Sanitizers.BLOCKS)
				.and(Sanitizers.LINKS)
				.and(Sanitizers.STYLES)
				.and(Sanitizers.IMAGES)
				.and(new HtmlPolicyBuilder()
						.allowElements("font", "hr")
						.allowAttributes("size").onElements("font")
						.allowAttributes("class").onElements("img").allowElements("img")
						.allowAttributes("target").onElements("a").allowElements("a")
						.toFactory());
		if (targetBlankLinks) {
			sanitizer = sanitizer.and(new HtmlPolicyBuilder()
					.allowElements(
							(elementName, attrs) -> {
								if ("a".equals(elementName)) {
									int targetIndex = attrs.indexOf("target");
									if (targetIndex < 0) {
										attrs.add("target");
										attrs.add("_blank");
									} else {
										attrs.set(targetIndex + 1, "_blank");
									}
								}
								return elementName;
							},
							"a"
					)
					.toFactory());
		}

		return sanitizer.sanitize(in);
	}
}