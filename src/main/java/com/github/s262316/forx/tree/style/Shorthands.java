package com.github.s262316.forx.tree.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.s262316.forx.common.InferenceTable;
import com.github.s262316.forx.css.CSSPropertiesReference;
import com.github.s262316.forx.css.validate.CSSValidator;
import com.github.s262316.forx.tree.style.util.ValuesHelper;
import com.github.s262316.forx.util.InferenceTable2;

import com.google.common.base.Predicates;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Shorthands
{
	@Autowired
	private CSSPropertiesReference propertiesReference;
	
	public HashBasedTable<Value, String, Boolean> tableOfPropertyValueCombos(List<Value> valuesToAssign, String... possiblePropertyNames)
	{
		Set<String> componentProperties=ImmutableSet.copyOf(possiblePropertyNames);
		Set<Value> values=ImmutableSet.copyOf(valuesToAssign);
		boolean validationResult;

		HashBasedTable<Value, String, Boolean> table=HashBasedTable.create();
		
		for(List<Object> l : Sets.cartesianProduct(componentProperties, values))
		{
			validationResult=propertiesReference.validate((String)l.get(0), (Value)l.get(1));
			table.put((Value)l.get(1), (String)l.get(0), validationResult);
		}
		
		return table;
	}
	
	public static boolean strictTest(HashBasedTable<Value, String, Boolean> table)
	{
		// any rows contain all falses then fail validation
		for(Map<String, Boolean> mmm : table.rowMap().values())
		{
			if(Iterables.all(mmm.values(), Predicates.equalTo(false)))
				return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param toExpand
	 * @param strict true = all properties must be assigned
	 * @return
	 */
	public List<Declaration> expandShorthandProperty2(Declaration toExpand, boolean strict)
	{
		List<Declaration> expanded = new ArrayList<>();
		ValueList valuelist = null;

		valuelist=ValuesHelper.asValueList(toExpand.getValue());
		
		if(toExpand.getProperty().equals("border"))
		{
			HashBasedTable<Value, String, Boolean> table=tableOfPropertyValueCombos(valuelist.members, "border-top-style", "border-top-color", "border-top-width");

			if(strict && !strictTest(table))
				return Collections.<Declaration>emptyList();
			
			Map<String, Value> matched=InferenceTable2.infer(table);

			for(Entry<String, Value> e : matched.entrySet())
			{
				String props[]=null;
				
				if(e.getKey().endsWith("-style"))
					props=new String[]{"border-top-style", "border-bottom-style", "border-left-style", "border-right-style"};
				else if(e.getKey().endsWith("-color"))
					props=new String[]{"border-top-color", "border-bottom-color", "border-left-color", "border-right-color"};
				else if(e.getKey().endsWith("-width"))
					props=new String[]{"border-top-width", "border-bottom-width", "border-left-width", "border-right-width"};
				
				for(String p : props)
					expanded.add(new Declaration(p, e.getValue(), toExpand.isImportant()));
			}
		}
		
		return expanded;
	}
	
	@Deprecated
	public static List<Declaration> expandShorthandProperty(Declaration toExpand)
	{
		List<Declaration> expanded = new ArrayList<>();
		ValueList valuelist = null;
		Value value = toExpand.getValue();
		String property = toExpand.getProperty();
		boolean important = toExpand.isImportant();

		if (toExpand.getValue().getClass().equals(ValueList.class))
		{
			valuelist = (ValueList) value;
		}
		else
		{
			valuelist = new ValueList();
			valuelist.members.add(value);
		}

		if (property.equals("background"))
		{
			// [<'background-color'> ||
			// <'background-image'> ||
			// <'background-repeat'> ||
			// <'background-attachment'> ||
			// <'background-position'>] | inherit

			boolean resultGrid[][] = new boolean[5][5];
			String properties[] = new String[]
			{ "background-color", "background-image", "background-repeat",
					"background-attachment", "background-position" };
			Value value_array[];
			Declaration dec;
			int i, j;

			// two values could be meant for background-position
			for (j = 0; j < valuelist.size(); j++)
			{
				resultGrid[4][j] = CSSValidator.validateProperty(
						"background-position", valuelist.extract(j, j));
				if (resultGrid[4][j] == true && j < valuelist.size() - 1)
				{
					boolean resultOf2;

					resultOf2 = CSSValidator.validateProperty(
							"background-position", valuelist.extract(j, j + 1));
					if (resultOf2)
					{
						// valuelist[j] and valuelist[j+1] are 1 value
						ValueList position_value = new ValueList();
						position_value.members.add(valuelist.members.get(j));
						position_value.members
								.add(valuelist.members.get(j + 1));

						valuelist.members.set(j, position_value);
						valuelist.members.remove(valuelist.members.get(j + 1));
					}
				}
			}

			for (i = 0; i < 4; i++)
			{
				for (j = 0; j < valuelist.size(); j++)
				{
					resultGrid[i][j] = CSSValidator.validateProperty(
							properties[i], valuelist.extract(j, j));
				}
			}

			value_array = new Value[valuelist.size()];
			for (i = 0; i < valuelist.size(); i++)
				value_array[i] = valuelist.members.get(i);

			InferenceTable<String, Value> inferer = new InferenceTable<String, Value>(
					resultGrid, 5, 5);
			Map<String, Value> mapping = inferer.resolve(properties,
					value_array);

			for (Entry<String, Value> e : mapping.entrySet())
			{
				ValueList vl;
				if (e.getValue().getClass().equals(ValueList.class))
				{
					vl = (ValueList) e.getValue();
					if (vl.size() == 1)
						e.setValue(vl.get(0));
				}

				// cout << "adding dec " << it.first << " ";
				// cout << "value : ";
				// it.second.print();
				dec = new Declaration(e.getKey(), e.getValue(), important);
				expanded.add(dec);
			}
		}
		else if (property.equals("border-color"))
		{
			// [ <color> | transparent ]{1,4} | inherit
			// 1: all, 2: top/bottom, left/right
			// 3: top, left/right,bottom, 4: top,right,bottom,left
			switch (valuelist.members.size())
			{
			case 1:
				// applies to all sides
				expanded.add(new Declaration("border-top-color",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-color",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-bottom-color",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-left-color",
						valuelist.members.get(0), important));
				break;
			case 2:
				// value1: top & bottom. value2: left & right
				expanded.add(new Declaration("border-top-color",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-color",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-color",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-left-color",
						valuelist.members.get(1), important));
				break;
			case 3:
				// value1: top. value2: left & right. value3: bottom
				expanded.add(new Declaration("border-top-color",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-color",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-color",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("border-left-color",
						valuelist.members.get(1), important));
				break;
			case 4:
				// value1: top. value2: right. value3: bottom. value4: left
				expanded.add(new Declaration("border-top-color",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-color",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-color",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("border-left-color",
						valuelist.members.get(3), important));
				break;
			}
		}
		else if (property.equals("border-style"))
		{
			// <border-style>{1,4} | inherit
			switch (valuelist.members.size())
			{
			case 1:
				// applies to all sides
				expanded.add(new Declaration("border-top-style",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-style",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-bottom-style",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-left-style",
						valuelist.members.get(0), important));
				break;
			case 2:
				// value1: top & bottom. value2: left & right
				expanded.add(new Declaration("border-top-style",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-style",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-style",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-left-style",
						valuelist.members.get(1), important));
				break;
			case 3:
				// value1: top. value2: left & right. value3: bottom
				expanded.add(new Declaration("border-top-style",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-style",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-style",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("border-left-style",
						valuelist.members.get(1), important));
				break;
			case 4:
				// value1: top. value2: right. value3: bottom. value4: left
				expanded.add(new Declaration("border-top-style",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-style",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-style",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("border-left-style",
						valuelist.members.get(3), important));
				break;
			}
		}
		else if (property.equals("border-top"))
		{
			expanded = validate_border_X_property(property, valuelist,
					"border-top-width", "border-top-style", "border-top-color");
		}
		else if (property.equals("border-right"))
		{
			expanded = validate_border_X_property(property, valuelist,
					"border-right-width", "border-right-style",
					"border-right-color");
		}
		else if (property.equals("border-bottom"))
		{
			expanded = validate_border_X_property(property, valuelist,
					"border-bottom-width", "border-bottom-style",
					"border-bottom-color");
		}
		else if (property.equals("border-left"))
		{
			expanded = validate_border_X_property(property, valuelist,
					"border-left-width", "border-left-style",
					"border-left-color");
		}
		else if (property.equals("border-width"))
		{
			// <border-width>{1,4} | inherit
			// 1: all, 2: top/bottom, left/right
			// 3: top, left/right,bottom, 4: top,right,bottom,left
			switch (valuelist.members.size())
			{
			case 1:
				// applies to all sides
				expanded.add(new Declaration("border-top-width",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-width",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-bottom-width",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-left-width",
						valuelist.members.get(0), important));
				break;
			case 2:
				// value1: top & bottom. value2: left & right
				expanded.add(new Declaration("border-top-width",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-width",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-width",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-left-width",
						valuelist.members.get(1), important));
				break;
			case 3:
				// value1: top. value2: left & right. value3: bottom
				expanded.add(new Declaration("border-top-width",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-width",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-width",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("border-left-width",
						valuelist.members.get(1), important));
				break;
			case 4:
				// value1: top. value2: right. value3: bottom. value4: left
				expanded.add(new Declaration("border-top-width",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("border-right-width",
						valuelist.members.get(1), important));
				expanded.add(new Declaration("border-bottom-width",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("border-left-width",
						valuelist.members.get(3), important));
				break;
			}
		}
		else if (property.equals("font"))
		{
		}
		else if (property.equals("list-style"))
		{
		}
		else if (property.equals("margin"))
		{
			// <margin-width> | inherit
			// 1: all, 2: top/bottom, left/right
			// 3: top, left/right,bottom, 4: top,right,bottom,left
			switch (valuelist.members.size())
			{
			case 1:
				// applies to all sides
				expanded.add(new Declaration("margin-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("margin-right", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("margin-bottom", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("margin-left", valuelist.members
						.get(0), important));
				break;
			case 2:
				// value1: top & bottom. value2: left & right
				expanded.add(new Declaration("margin-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("margin-right", valuelist.members
						.get(1), important));
				expanded.add(new Declaration("margin-bottom", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("margin-left", valuelist.members
						.get(1), important));
				break;
			case 3:
				// value1: top. value2: left & right. value3: bottom
				expanded.add(new Declaration("margin-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("margin-right", valuelist.members
						.get(1), important));
				expanded.add(new Declaration("margin-bottom", valuelist.members
						.get(2), important));
				expanded.add(new Declaration("margin-left", valuelist.members
						.get(1), important));
				break;
			case 4:
				// value1: top. value2: right. value3: bottom. value4: left
				expanded.add(new Declaration("margin-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("margin-right", valuelist.members
						.get(1), important));
				expanded.add(new Declaration("margin-bottom", valuelist.members
						.get(2), important));
				expanded.add(new Declaration("margin-left", valuelist.members
						.get(3), important));
				break;
			}

		}
		else if (property.equals("outline"))
		{
		}
		else if (property.equals("padding"))
		{
			// <padding-width>{1,4} | inherit
			// 1: all, 2: top/bottom, left/right
			// 3: top, left/right,bottom, 4: top,right,bottom,left
			switch (valuelist.members.size())
			{
			case 1:
				// applies to all sides
				expanded.add(new Declaration("padding-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("padding-right", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("padding-bottom",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("padding-left", valuelist.members
						.get(0), important));
				break;
			case 2:
				// value1: top & bottom. value2: left & right
				expanded.add(new Declaration("padding-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("padding-right", valuelist.members
						.get(1), important));
				expanded.add(new Declaration("padding-bottom",
						valuelist.members.get(0), important));
				expanded.add(new Declaration("padding-left", valuelist.members
						.get(1), important));
				break;
			case 3:
				// value1: top. value2: left & right. value3: bottom
				expanded.add(new Declaration("padding-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("padding-right", valuelist.members
						.get(1), important));
				expanded.add(new Declaration("padding-bottom",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("padding-left", valuelist.members
						.get(1), important));
				break;
			case 4:
				// value1: top. value2: right. value3: bottom. value4: left
				expanded.add(new Declaration("padding-top", valuelist.members
						.get(0), important));
				expanded.add(new Declaration("padding-right", valuelist.members
						.get(1), important));
				expanded.add(new Declaration("padding-bottom",
						valuelist.members.get(2), important));
				expanded.add(new Declaration("padding-left", valuelist.members
						.get(3), important));
				break;
			}

		}
		
		return expanded;
	}

	private static List<Declaration> validate_border_X_property(
			String shorthand_property, ValueList values, String width,
			String style, String color)
	{
		boolean resultGrid[][] = new boolean[3][3];
		String properties[] = new String[]{ width, style, color };
		Value value_array[] = new Value[]
		{ values.extract(0, 0).get(0), values.extract(1, 1).get(0), values.extract(2, 2).get(0) };
		List<Declaration> expanded = new LinkedList<Declaration>();
		Declaration dec;
		int i, j;

		for (i = 0; i < 3; i++)
		{
			for (j = 0; j < 3; j++)
			{
				resultGrid[i][j] = CSSValidator.validateProperty(properties[i],
						values.extract(j, j));
			}
		}

		InferenceTable<String, Value> inferer = new InferenceTable<String, Value>(
				resultGrid, 3, 3);
		Map<String, Value> mapping = inferer.resolve(properties, value_array);

		for (Entry<String, Value> e : mapping.entrySet())
		{
			ValueList vl;
			if (e.getValue().getClass().equals(ValueList.class))
			{
				vl = (ValueList) values;
				if(vl.size()==1)
					e.setValue(vl.get(0));
			}

			dec = new Declaration(e.getKey(), e.getValue(), false);
			expanded.add(dec);
		}

		return expanded;
	}
}

