package io.vertigo.chatbot.executor.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.DocumentaryResourceExport;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.chatbot.executor.model.IncomeDocumentaryResource;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.QueryParam;

import java.util.Map;
import java.util.UUID;

/**
 * Web service for documentary resources.
 * Provides endpoints to retrieve documentary resources based on context,
 * download resource files, and track clicks.
 *
 * @author Chatbot Team
 */
@PathPrefix("/docres")
public class DocumentaryResourceWebService implements WebServices {

    @Inject
    private ExecutorManager executorManager;

    /**
     * Get documentary resources filtered by context
     *
     * @param context context map for filtering resources
     * @return list of documentary resources matching the context
     */
    @POST("/getDocumentaryResources")
    public DtList<DocumentaryResourceExport> getDocumentaryResourceList(final Map<String, String> context) {
        return executorManager.getDocumentaryResourceList(context);
    }

    /**
     * Get documentary resource file by attachment ID
     *
     * @param attId attachment ID
     * @return file
     */
    @GET("/getDocumentaryResourceFile")
    public VFile getDocumentaryResourceFileFromAttId(@QueryParam("attId") final Long attId) {
        return executorManager.getDocumentaryResourceFileFromAttId(attId);
    }

	/**
	 * Track a documentary resource click for analytics
	 *
	 * @param sessionId session identifier
	 * @param incomeDocumentaryResource documentary resource click information
	 */
	@POST("/stats/{sessionId}")
	public void stats(@PathParam("sessionId") final UUID sessionId,
					  final IncomeDocumentaryResource incomeDocumentaryResource) {
		executorManager.trackDocumentaryResourceClick(
				sessionId, 
				incomeDocumentaryResource.dreId(), 
				incomeDocumentaryResource.title(), 
				incomeDocumentaryResource.dreTypeCd());
	}
}