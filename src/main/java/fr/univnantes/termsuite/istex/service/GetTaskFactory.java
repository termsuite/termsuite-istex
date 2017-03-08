package fr.univnantes.termsuite.istex.service;

import java.net.URL;

import fr.univnantes.termsuite.istex.GetDocumentTask;

public interface GetTaskFactory {

	public GetDocumentTask create(URL documentUrl);
}
