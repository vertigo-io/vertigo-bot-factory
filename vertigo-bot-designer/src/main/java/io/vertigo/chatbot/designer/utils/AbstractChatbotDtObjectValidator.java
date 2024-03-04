package io.vertigo.chatbot.designer.utils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.vertigo.chatbot.commons.multilingual.ConstraintResources;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.definitions.DtFieldName;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.vega.webservice.validation.AbstractDtObjectValidator;
import io.vertigo.vega.webservice.validation.DefaultDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
import io.vertigo.vega.webservice.validation.DtObjectValidator;

/**
 * @author cmarechal
 * @created 25/10/2023 - 17:19
 * @project vertigo-bot-factory
 */

public abstract class AbstractChatbotDtObjectValidator<O extends DtObject> extends AbstractDtObjectValidator<O> {

    private static final DtObjectValidator<DtObject> DEFAULT_VALIDATOR = new DefaultDtObjectValidator<>();

    private final List<String> fieldsNameToNullCheck = getFieldsToNullCheck()
            .stream()
            .map(DtFieldName::name)
            .collect(Collectors.toUnmodifiableList());

    protected boolean checkDefaultValidator() {
        return false;
    }

    protected List<DtFieldName<O>> getFieldsToNullCheck() {
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
    protected void checkMonoFieldConstraints(final O dtObject, final DtField dtField, final DtObjectErrors dtObjectErrors) {
        super.checkMonoFieldConstraints(dtObject, dtField, dtObjectErrors);
        final Object value = dtField.getDataAccessor().getValue(dtObject);

        if (value == null && fieldsNameToNullCheck.contains(dtField.getName())) {
            dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
        }
    }

}