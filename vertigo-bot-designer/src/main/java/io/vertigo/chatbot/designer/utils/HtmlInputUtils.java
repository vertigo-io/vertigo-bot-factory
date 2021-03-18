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
						.allowElements( // force target _blank https://github.com/OWASP/java-html-sanitizer/issues/147
								(elementName, attrs) -> {
									final int targetIndex = attrs.indexOf("target");
									if (targetIndex < 0) {
										attrs.add("target");
										attrs.add("_blank");
									} else {
										attrs.set(targetIndex + 1, "_blank");
									}
									return elementName;
								},
								"a")
						.toFactory());

		return sanitizer.sanitize(in);
	}
}
