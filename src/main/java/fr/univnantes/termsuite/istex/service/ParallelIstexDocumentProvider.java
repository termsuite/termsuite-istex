package fr.univnantes.termsuite.istex.service;

import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.model.Lang;

public class ParallelIstexDocumentProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParallelIstexDocumentProvider.class);

	
	@Inject(optional=true) @Named("istex.bufferSize")
	private int bufferSize = 5;
	
	@Inject(optional=true) @Named("istex.nbThreads")
	private int nbThreads = 2;

	@Inject(optional=true) @Named("istex.failOnError")
	private boolean failOnError = false;

	@Inject
	private List<String> documentIds;
	
	@Inject
	private Lang lang;

	@Inject
	private IstexService istexService;

	@Inject
	private GetTaskFactory factory;

	private CompletionService<IstexDocument> pool;
	
	private ExecutorService threadPool;
	
	private Queue<URL> urlQueue;
	
	public ParallelIstexDocumentProvider() {
		super();
		threadPool = Executors.newFixedThreadPool(nbThreads);
		pool = new ExecutorCompletionService<IstexDocument>(threadPool);
		urlQueue = new LinkedBlockingDeque<>();
		
		for (String documentId:documentIds) 
			urlQueue.add(istexService.getDocumentURL(documentId));

		new Thread() {
			@Override
			public void run() {
				for(URL documentURL:urlQueue) {
					if(LOGGER.isTraceEnabled())
						LOGGER.trace("Submitting retrieve task for {}", documentURL);
					pool.submit(factory.create(documentURL));
				}
			}
		}.start();

		LOGGER.debug("End of Istex collection reader initialization");
	}


	public IstexDocument next() {
		try {
			IstexDocument doc = pool.take().get();
			if(doc.getLang() !=  this.lang) {
				String msg = String.format("Bad language for document %s. Expected: %s, actual: %s", doc.getUrl(),
						this.lang.getCode(), doc.getLang().getCode());
				LOGGER.warn(msg);
				if(failOnError)
					throw new IllegalArgumentException(msg);
			}
			if(urlQueue.isEmpty())
				threadPool.shutdown();
			return doc;
		} catch (Exception e) {
			LOGGER.error("An error occurrence with the Istex client pool");
			throw new IllegalStateException(e);
		} finally {
			if(urlQueue.isEmpty())
				threadPool.shutdown();
		}
	}

}
