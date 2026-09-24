package io.vertigo.chatbot.designer.boot;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.vertigo.chatbot.designer.utils.ListUtils;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.util.UiRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Enforces the Vertigo UI list-size invariant before server-rendered views are
 * rendered.
 *
 * @author Chatbot Team
 */
public final class ViewContextListLimitInterceptor implements HandlerInterceptor {

	/**
	 * Truncates oversized lists after controller execution and before rendering.
	 * Requests without a Vertigo ViewContext are ignored.
	 *
	 * @param request current request
	 * @param response current response
	 * @param handler selected handler
	 * @param modelAndView selected model and view, when any
	 */
	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final ModelAndView modelAndView) {
		final Object viewContext = request.getAttribute("viewContext");
		if (viewContext instanceof final ViewContext currentViewContext) {
			ListUtils.listLimitReached(currentViewContext, UiRequestUtil.obtainCurrentUiMessageStack());
		}
	}
}
