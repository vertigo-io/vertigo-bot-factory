package io.vertigo.chatbot.designer.analytics.services;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimeSeriesDataBaseManager;

@Transactional
public class AnalyticsServices implements Component {

	@Inject
	private TimeSeriesDataBaseManager timeSeriesDataBaseManager;

	public void test() {
		final List<String> measures = Arrays.asList("confidence:mean");
		final TabularDatas test = timeSeriesDataBaseManager.getTabularData("chatbot-test", measures, DataFilter.builder("\"user\"").build(), TimeFilter.builder("now() - 2w", "now()").build(), "intent");

		System.out.println(test);
	}
}