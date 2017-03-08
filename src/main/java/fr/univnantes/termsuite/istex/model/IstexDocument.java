package fr.univnantes.termsuite.istex.model;

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fr.univnantes.termsuite.istex.json.FulltextDeserializer;
import fr.univnantes.termsuite.istex.json.LanguageDeserializer;
import fr.univnantes.termsuite.istex.json.RawType;
import fr.univnantes.termsuite.istex.json.SubjectDeserializer;
import fr.univnantes.termsuite.model.Lang;

@JsonIgnoreProperties(ignoreUnknown=true)
public class IstexDocument {
	
	@JsonProperty
	private String publicationDate;
	
	@JsonProperty
	private String title;
	
	@JsonProperty("abstract")
	private String _abstract;
	
	@JsonProperty
	private String corpusName;

	@JsonIgnore
	private URL url;

	@JsonProperty("author")
	private List<Author> authors;

	@JsonProperty("subject")
	@JsonDeserialize(using=SubjectDeserializer.class)
	private List<String> subjects;

	@JsonProperty("language")
	@JsonDeserialize(using=LanguageDeserializer.class)
	private Lang lang;

	@JsonProperty("fulltext")
	@JsonDeserialize(using=FulltextDeserializer.class)
	private Map<RawType, URL> fulltext;

	public String getPublicationDate() {
		return publicationDate;
	}
	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstract() {
		return _abstract;
	}
	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}
	public String getCorpusName() {
		return corpusName;
	}
	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public Lang getLang() {
		return lang;
	}
	
	public void setLang(Lang lang) {
		this.lang = lang;
	}
	
	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	public Map<RawType, URL> getFulltext() {
		return fulltext;
	}
	public void setFulltext(Map<RawType, URL> fulltext) {
		this.fulltext = fulltext;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}
}
