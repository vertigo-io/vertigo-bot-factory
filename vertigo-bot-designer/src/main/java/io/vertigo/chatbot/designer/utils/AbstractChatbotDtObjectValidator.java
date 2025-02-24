package io.vertigo.chatbot.designer.utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.vertigo.chatbot.commons.multilingual.ConstraintResources;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.datamodel.data.definitions.DataField;
import io.vertigo.datamodel.data.definitions.DataFieldName;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.vega.webservice.validation.AbstractDtObjectValidator;
import io.vertigo.vega.webservice.validation.DefaultDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
import io.vertigo.vega.webservice.validation.DtObjectValidator;

/**
 * @author cmarechal
 * @created 25/10/2023 - 17:19
 * @project vertigo-bot-factory
 */

public abstract class AbstractChatbotDtObjectValidator<O extends DataObject> extends AbstractDtObjectValidator<O> {

    private static final DtObjectValidator<DataObject> DEFAULT_VALIDATOR = new DefaultDtObjectValidator<>();

    private final List<String> fieldsNameToNullCheck = getFieldsToNullCheck()
            .stream()
            .map(DataFieldName::name)
            .collect(Collectors.toUnmodifiableList());

    protected boolean checkDefaultValidator() {
        return false;
    }

    protected List<DataFieldName<O>> getFieldsToNullCheck() {
        return List.of();
    }

    @Override
    public void validate(final O dtObject, final Set<String> modifiedFieldNames, final DtObjectErrors dtObjectErrors) {
        if (checkDefaultValidator()) {
            DEFAULT_VALIDATOR.validate(dtObject, modifiedFieldNames, dtObjectErrors);
        }
        super.validate(dtObject, modifiedFieldNames, dtObjectErrors);
    }

    @Override
    @SuppressWarnings("fb-contrib:DLC_DUBIOUS_LIST_COLLECTION")
    protected void checkMonoFieldConstraints(final O dtObject, final DataField dtField, final DtObjectErrors dtObjectErrors) {
        super.checkMonoFieldConstraints(dtObject, dtField, dtObjectErrors);
        final Object value = dtField.getDataAccessor().getValue(dtObject);

        if (value == null && fieldsNameToNullCheck.contains(dtField.name())) {
            dtObjectErrors.addError(dtField.name(), LocaleMessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
        }
    }

}