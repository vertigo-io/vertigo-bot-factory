package io.vertigo.chatbot.executor.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.DocumentaryResourceExport;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.QueryParam;

@PathPrefix("/docres")
public class DocumentaryResourceWebService implements WebServices {

    @Inject
    private ExecutorManager executorManager;

    @GET("/getDocumentaryResources")
    public DtList<DocumentaryResourceExport> getDocumentaryResourceList() {
        return executorManager.getDocumentaryResourceList();
    }

    @GET("/getDocumentaryResourceFile")
    public VFile getDocumentaryResourceFileFromAttId(@QueryParam("attId") final Long attId) {
        return executorManager.getDocumentaryResourceFileFromAttId(attId);
    }
}