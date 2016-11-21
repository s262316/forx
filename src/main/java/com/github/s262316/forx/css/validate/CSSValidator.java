package com.github.s262316.forx.css.validate;

import java.util.HashMap;
import java.util.Map;

import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

public class CSSValidator
{
    static Map<String, PropertyValidator> propertyValidators;

    static
    {
        propertyValidators=new HashMap<String, PropertyValidator>();

	propertyValidators.put("background-color", new BackgroundColourValidator());
	propertyValidators.put("background-image", new BackgroundImageValidator());
	propertyValidators.put("background-repeat", new BackgroundRepeatValidator());
	propertyValidators.put("background-attachment", new BackgroundAttachmentValidator());
	propertyValidators.put("background-position", new BackgroundPositionValidator());
    }

    public static boolean validateProperty(String property_name, ValueList values)
    {
	return propertyValidators.get(property_name).validate(values);
    }

	public boolean validateProperty(String property_name, Value value)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
