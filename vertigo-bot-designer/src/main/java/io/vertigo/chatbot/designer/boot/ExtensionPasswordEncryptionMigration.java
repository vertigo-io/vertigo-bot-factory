package io.vertigo.chatbot.designer.boot;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.component.di.DIInjector;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.UpdateStatement;

/**
 * @author cmarechal
 * @created 10/10/2022 - 11:45
 * @project vertigo-bot-factory
 */

public class ExtensionPasswordEncryptionMigration implements CustomSqlChange, Component {

    @Inject
    private PasswordEncryptionServices passwordEncryptionServices;

    @Override
    public SqlStatement[] generateStatements(final Database database) throws CustomChangeException {
        DIInjector.injectMembers(this, Node.getNode().getComponentSpace());
        passwordEncryptionServices.start();
        final List<SqlStatement> statements = new ArrayList<>();
        final JdbcConnection connection = (JdbcConnection) database.getConnection();

        try(final Statement statement = connection.createStatement()) {
            final ResultSet confluenceSettings = statement.executeQuery("select con_set_id, password from confluence_setting");
            while (confluenceSettings.next()) {
                final SqlStatement update = new UpdateStatement(database.getDefaultCatalogName(), database.getDefaultSchemaName(), "confluence_setting")
                        .addNewColumnValue("password", passwordEncryptionServices.encryptPassword(confluenceSettings.getString("password")))
                        .setWhereClause("con_set_id = " + confluenceSettings.getLong("con_set_id"));
                statements.add(update);
            }
            final ResultSet jiraSettings = statement.executeQuery("select jir_set_id, password from jira_setting");
            while (jiraSettings.next()) {
                final SqlStatement update = new UpdateStatement(database.getDefaultCatalogName(), database.getDefaultSchemaName(), "jira_setting")
                        .addNewColumnValue("password", passwordEncryptionServices.encryptPassword(jiraSettings.getString("password")))
                        .setWhereClause("jir_set_id = " + jiraSettings.getLong("jir_set_id"));
                statements.add(update);
            }
        } catch (final SQLException | DatabaseException e) {
            throw new CustomChangeException(e);
        }
        return statements.toArray(new SqlStatement[0]);
    }

    @Override
    public String getConfirmationMessage() {
        return "All passwords were properly encrypted";
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(final ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(final Database database) {
        return null;
    }
}
