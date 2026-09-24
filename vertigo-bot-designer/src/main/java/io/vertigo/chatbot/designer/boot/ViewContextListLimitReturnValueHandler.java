package io.vertigo.chatbot.designer.boot;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.vertigo.chatbot.designer.utils.ListUtils;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewContextReturnValueAndArgumentResolver;
import io.vertigo.ui.impl.springmvc.util.UiRequestUtil;

/**
 * Enforces the Vertigo UI list-size invariant before an AJAX ViewContext is
 * serialized.
 *
 * @author Chatbot Team
 */
public final class ViewContextListLimitReturnValueHandler implements HandlerMethodReturnValueHandler {

	private final ViewContextReturnValueAndArgumentResolver delegate = new ViewContextReturnValueAndArgumentResolver();

	/**
	 * Determines whether the return value is a Vertigo ViewContext.
	 *
	 * @param returnType controller return type
	 * @return {@code true} for ViewContext return values
	 */
	@Override
	public boolean supportsReturnType(final MethodParameter returnType) {
		return delegate.supportsReturnType(returnType);
	}

	/**
	 * Truncates oversized UI lists and delegates serialization to Vertigo UI.
	 *
	 * @param returnValue controller return value
	 * @param returnType controller return type
	 * @param mavContainer model and view container
	 * @param webRequest current web request
	 * @throws Exception when Vertigo cannot serialize the response
	 */
	@Override
	public void handleReturnValue(final Object returnValue, final MethodParameter returnType,
			final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest) throws Exception {
		final ViewContext viewContext = (ViewContext) Objects.requireNonNull(returnValue);
		ListUtils.listLimitReached(viewContext, UiRequestUtil.obtainCurrentUiMessageStack());
		delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
	}
}
