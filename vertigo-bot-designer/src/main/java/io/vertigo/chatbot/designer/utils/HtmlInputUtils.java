package io.vertigo.chatbot.designer.utils;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlInputUtils {

	private HtmlInputUtils() {
		//utils class
	}

	public static String sanatizeHtml(final String in) {
		final PolicyFactory sanitizer = Sanitizers.FORMATTING
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

		return sanitizer.sanitize(in);
	}
}
