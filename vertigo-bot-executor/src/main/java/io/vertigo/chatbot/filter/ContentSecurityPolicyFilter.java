package io.vertigo.chatbot.filter;

import io.vertigo.core.node.Node;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentSecurityPolicyFilter implements Filter {

	private ParamManager paramManager;

	private String frameAncestors;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);
		frameAncestors = paramManager.getOptionalParam("FRAME_ANCESTORS")
				.orElse(Param.of("FRAME_ANCESTORS", "*")).getValueAsString();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setHeader("Content-Security-Policy", "frame-ancestors " + frameAncestors);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}
