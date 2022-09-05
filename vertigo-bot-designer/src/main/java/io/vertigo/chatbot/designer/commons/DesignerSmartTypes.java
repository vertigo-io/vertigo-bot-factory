package io.vertigo.chatbot.designer.commons;

import io.vertigo.basics.formatter.FormatterDefault;
import io.vertigo.core.lang.BasicType;
import io.vertigo.datamodel.smarttype.annotations.Adapter;
import io.vertigo.datamodel.smarttype.annotations.Formatter;
import io.vertigo.datamodel.smarttype.annotations.SmartTypeDefinition;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.FileInfoURIAdapter;

public enum DesignerSmartTypes {

	@SmartTypeDefinition(FileInfoURI.class)
	@Formatter(clazz = FormatterDefault.class)
	@Adapter(clazz = FileInfoURIAdapter.class, targetBasicType = BasicType.String)
	FileInfoURI;
}
