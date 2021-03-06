package com.github.s262316.forx.css;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CSSPropertiesReference
{
	private static final Logger logger=LoggerFactory.getLogger(CSSPropertiesReference.class);

	@Autowired
	private Map<String, ShorthandPropertyReference> shorthandPropertyTable;
	@Autowired
	private Map<String, PropertyReference> propertyTable;
	@Autowired
	private BorderStyles borderStyles;

	public boolean isShorthand(String propertyName)
	{
		return shorthandPropertyTable.containsKey(propertyName);
	}
	
	public PropertyReference getPropertyDescriptor(String propertyName)
	{
		return propertyTable.get(propertyName);
	}

	public ShorthandPropertyReference getShorthandPropertyDescriptor(String propertyName)
	{
		return shorthandPropertyTable.get(propertyName);
	}

	public List<Declaration> expandShorthand(Declaration dec)
	{
		// temporary workaround for properties without validation
		if(shorthandPropertyTable.containsKey(dec.getProperty()) && shorthandPropertyTable.get(dec.getProperty()).getExpander()==null)
		{
			logger.warn("no property reference for "+dec.getProperty());			
			return Collections.<Declaration>emptyList();
		}
		return shorthandPropertyTable.get(dec.getProperty()).getExpander().expand(dec);
	}

	public boolean validate(Declaration dec)
	{
		logger.debug("validate({})", dec);

		// temporary workaround for properties without validation
		if(propertyTable.containsKey(dec.getProperty()) && propertyTable.get(dec.getProperty()).getValidator()==null)
		{
			logger.warn("no property reference for "+dec.getProperty());
			return true;
		}

		if(!propertyTable.containsKey(dec.getProperty()))
		    return false;

		return propertyTable.get(dec.getProperty()).getValidator().validate(dec.getValue());
	}

	public boolean validate(String propertyName, Value propertyValue)
	{
		// temporary workaround for properties without validation
		if(propertyTable.containsKey(propertyName) && propertyTable.get(propertyName).getValidator()==null)
		{
			logger.warn("no property reference for "+propertyName);
			return true;
		}
		
		return propertyTable.get(propertyName).getValidator().validate(propertyValue);
	}

}

