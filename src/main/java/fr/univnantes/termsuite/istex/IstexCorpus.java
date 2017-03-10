package fr.univnantes.termsuite.istex;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fr.univnantes.termsuite.api.TermSuiteException;
import fr.univnantes.termsuite.api.TextualCorpus;
import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.istex.service.IstexService;
import fr.univnantes.termsuite.model.Document;
import fr.univnantes.termsuite.model.Lang;

public abstract class IstexCorpus implements TextualCorpus {

	Lang lang;
	
	List<String> documentIds;

	IstexService istexService;
	
	LoadingCache<String, Document> docCache = CacheBuilder.newBuilder()
		       .maximumSize(16)
		       .expireAfterWrite(1, TimeUnit.MINUTES)
		       .build(
		           new CacheLoader<String, Document>()  {
		             public Document load(String documentURL) throws IOException {
		            	IstexDocument doc;
						try {
							doc = istexCache.get(new URL(documentURL));
						} catch (ExecutionException e) {
							throw new IOException(e);
						}
		 				return new Document(doc.getLang(), documentURL);

		             }
	             }
	           );

	LoadingCache<URL, IstexDocument> istexCache = CacheBuilder.newBuilder()
			       .maximumSize(16)
			       .expireAfterWrite(1, TimeUnit.MINUTES)
			       .build(
			           new CacheLoader<URL, IstexDocument>()  {
				             public IstexDocument load(URL documentURL) throws IOException {
				            	 return istexService.getDocument(documentURL);
				             }
			           }
		           );
		
	public IstexCorpus() {
		super();
	}

	@Override
	public Stream<Document> documents() {
		Stream<String> stream = documentIds.stream();
		
		return stream
			.map(id -> 
				istexService.makeDocumentURL(id)
			)
			.map(url -> {
				try {
					return docCache.get(url.toString());
				} catch (ExecutionException e) {
					String msg = String.format("Could not retrieve document %s", url);
					throw new TermSuiteException(msg, e);
				}
			})
			;
	}

	@Override
	public Lang getLang() {
		return lang;
	}

	public List<String> getDocumentIds() {
		return documentIds;
	}

}