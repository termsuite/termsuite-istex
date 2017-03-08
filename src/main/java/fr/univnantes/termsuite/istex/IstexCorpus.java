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
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import fr.univnantes.termsuite.api.TermSuiteException;
import fr.univnantes.termsuite.api.TextualCorpus;
import fr.univnantes.termsuite.istex.model.IstexDocument;
import fr.univnantes.termsuite.istex.service.IstexService;
import fr.univnantes.termsuite.model.Document;
import fr.univnantes.termsuite.model.Lang;

public class IstexCorpus implements TextualCorpus {

	
	private Lang lang;
	private List<String> documentIds;
	private IstexService istexService;
	
	
	private LoadingCache<String, Document> docCache = CacheBuilder.newBuilder()
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

	private LoadingCache<URL, IstexDocument> istexCache = CacheBuilder.newBuilder()
		       .maximumSize(16)
		       .expireAfterWrite(1, TimeUnit.MINUTES)
		       .build(
		           new CacheLoader<URL, IstexDocument>()  {
		             public IstexDocument load(URL documentURL) throws IOException {
		            	 return istexService.getDocument(documentURL);
		             }
	             }
	           );

	
	@Inject
	public IstexCorpus(
			IstexService istexService,
			@Assisted Lang lang,
			@Assisted List<String> documentIds
			) {
		super();
		this.lang = lang;
		this.documentIds = documentIds;
		this.istexService = istexService;
	}

	@Override
	public Stream<Document> documents() {
		Stream<String> stream = documentIds.stream();
		
		return stream
			.map(id -> 
				istexService.getDocumentURL(id)
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

	@Override
	public String readDocumentText(Document doc) {
		IstexDocument istexDocument;
		try {
			istexDocument = istexCache.get(new URL(doc.getUrl()));
			return String.format("%s. %s.", istexDocument.getTitle(), istexDocument.getAbstract());
		} catch (Exception e) {
			throw new TermSuiteException(e);
		}
	}

	public List<String> getDocumentIds() {
		return documentIds;
	}
}
