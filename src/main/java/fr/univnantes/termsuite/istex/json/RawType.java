package fr.univnantes.termsuite.istex.json;

public enum RawType {
	TEI("tei", "application/tei+xml"),
	TXT("txt", "text/plain"),
	ZIP("zip", "application/zip"),
	PDF("pdf", "application/pdf"),
	;
	
	private String name;
	private String mime;
	private RawType(String name, String mime) {
		this.name = name;
		this.mime = mime;
	}
	public String getName() {
		return name;
	}
	public String getMime() {
		return mime;
	}
	public static RawType forName(String name) {
		for(RawType type:values())
			if(type.name.equals(name) || type.mime.equals(name))
				return type;
		return null;
	}
}
