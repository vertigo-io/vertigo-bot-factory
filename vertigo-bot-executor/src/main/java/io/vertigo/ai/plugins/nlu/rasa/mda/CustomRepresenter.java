package io.vertigo.ai.plugins.nlu.rasa.mda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.representer.Representer;

public class CustomRepresenter extends Representer {

	public CustomRepresenter() {
		super();
	}

	@Override
	protected Set<Property> getProperties(final Class<? extends Object> type) {
		Set<Property> propertySet;
		if (typeDefinitions.containsKey(type)) {
			propertySet = typeDefinitions.get(type).getProperties();
		}

		propertySet = getPropertyUtils().getProperties(type);

		final List<Property> propsList = new ArrayList<>(propertySet);
		Collections.reverse(propsList);

		return new LinkedHashSet<>(propsList);
	}
}
