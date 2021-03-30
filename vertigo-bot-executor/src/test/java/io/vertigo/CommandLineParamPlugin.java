package io.vertigo;

import java.util.Optional;

import io.vertigo.core.impl.param.ParamPlugin;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.param.Param;

public final class CommandLineParamPlugin implements ParamPlugin {
	/** {@inheritDoc} */
	@Override
	public Optional<Param> getParam(final String paramName) {
		Assertion.check().isNotBlank(paramName);
		//-----
		final String paramValue = System.getProperty(paramName);
		return paramValue != null ? Optional.of(Param.of(paramName, paramValue)) : Optional.empty();
	}
}
