package com.github.s262316.forx.core;

import java.io.IOException;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

public class XHTMLResourceResolver implements XMLResolver
{
	private final static Logger logger=LoggerFactory.getLogger(XHTMLResourceResolver.class);
	
	@Override
	public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException
	{
		try
		{
			logger.debug("resolveEntity(\"{}\",\"{}\",\"{}\",\"{}\")", publicID, systemID, baseURI, namespace);
			
			switch(systemID)
			{
			case "xhtml-lat1.ent":
				return Resources.getResource("com/github/s262316/forx/core/xhtml-lat1.ent").openStream();

			case "xhtml-special.ent":
				return Resources.getResource("com/github/s262316/forx/core/xhtml-special.ent").openStream();

			case "xhtml-symbol.ent":
				return Resources.getResource("com/github/s262316/forx/core/xhtml-symbol.ent").openStream();
			
			case "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd":
				return Resources.getResource("com/github/s262316/forx/core/xhtml1-frameset.dtd").openStream();
			
			case "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd":
				return Resources.getResource("com/github/s262316/forx/core/xhtml1-strict.dtd").openStream();
			
			case "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd":
				return Resources.getResource("com/github/s262316/forx/core/xhtml1-transitional.dtd").openStream();
			
			case "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml11.dtd").openStream();
				
			case "xhtml-attribs-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-attribs-1.mod").openStream();
			case "xhtml-base-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-base-1.mod").openStream();
			case "xhtml-bdo-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-bdo-1.mod").openStream();
			case "xhtml-blkphras-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-blkphras-1.mod").openStream();
			case "xhtml-blkpres-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-blkpres-1.mod").openStream();
			case "xhtml-blkstruct-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-blkstruct-1.mod").openStream();
			case "xhtml-charent-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-charent-1.mod").openStream();
			case "xhtml-csismap-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-csismap-1.mod").openStream();
			case "xhtml-datatypes-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-datatypes-1.mod").openStream();
			case "xhtml-edit-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-edit-1.mod").openStream();
			case "xhtml-events-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-events-1.mod").openStream();
			case "xhtml-form-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-form-1.mod").openStream();
			case "xhtml-framework-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-framework-1.mod").openStream();
			case "xhtml-hypertext-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-hypertext-1.mod").openStream();
			case "xhtml-image-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-image-1.mod").openStream();
			case "xhtml-inlphras-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-inlphras-1.mod").openStream();
			case "xhtml-inlpres-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-inlpres-1.mod").openStream();
			case "xhtml-inlstruct-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-inlstruct-1.mod").openStream();
			case "xhtml-inlstyle-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-inlstyle-1.mod").openStream();
			case "xhtml-link-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-link-1.mod").openStream();
			case "xhtml-list-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-list-1.mod").openStream();
			case "xhtml-meta-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-meta-1.mod").openStream();
			case "xhtml-notations-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-notations-1.mod").openStream();
			case "xhtml-object-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-object-1.mod").openStream();
			case "xhtml-param-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-param-1.mod").openStream();
			case "xhtml-pres-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-pres-1.mod").openStream();
			case "xhtml-qname-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-qname-1.mod").openStream();
			case "xhtml-ruby-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-ruby-1.mod").openStream();
			case "xhtml-script-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-script-1.mod").openStream();
			case "xhtml-ssismap-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-ssismap-1.mod").openStream();
			case "xhtml-struct-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-struct-1.mod").openStream();
			case "xhtml-style-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-style-1.mod").openStream();
			case "xhtml-table-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-table-1.mod").openStream();
			case "xhtml-text-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml-text-1.mod").openStream();
			case "xhtml11-model-1.mod":
				return Resources.getResource("com/github/s262316/forx/core/xhtml11/xhtml11-model-1.mod").openStream();
				
				
			default:
				throw new XMLStreamException(
						StrSubstitutor.replace("cannot find publicID='${publicID}', systemID='${systemID}', baseURI='${baseURI}', namespace='${namespace}'",
								ImmutableMap.of(
										"publicID", MoreObjects.firstNonNull(publicID, "null"),
										"systemID", MoreObjects.firstNonNull(systemID, "null"),
										"baseURI", MoreObjects.firstNonNull(baseURI, "null"),
										"namespace", MoreObjects.firstNonNull(namespace, "null")
										)
								));
			}
		}
		catch(IOException ioe)
		{
			throw new XMLStreamException(ioe);
		}
	}
}
