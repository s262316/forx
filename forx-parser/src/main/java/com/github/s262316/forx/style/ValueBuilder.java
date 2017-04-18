package com.github.s262316.forx.style;

import com.google.common.base.Preconditions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ValueBuilder
{
    private List<Value> v=new ArrayList<>();

    public Value build()
    {
        Preconditions.checkState(!v.isEmpty());

        if(v.size()==1)
            return v.get(0);
        else
            return buildAsList();
    }

    public ValueList buildAsList()
    {
        ValueList list=new ValueList();
        list.members.addAll(v);

        return list;
    }

    public ValueBuilder identifier(String ident)
    {
        v.add(new Identifier(ident));
        return this;
    }

    public ValueBuilder value(Value value)
    {
        v.add(value);
        return this;
    }

    public ValueBuilder string(String str)
    {
        v.add(new StringValue(str));
        return this;
    }

    public ValueBuilder uri(String uri) throws MalformedURLException
    {
        v.add(new UrlValue(new URL(uri)));
        return this;
    }

    public ValueBuilder counter(String countername)
    {
        ValueList args=new ValueBuilder().identifier(countername).buildAsList();
        v.add(new FunctionValue("counter", args));
        return this;
    }

    public ValueBuilder counter(String countername, String style)
    {
        ValueList args=new ValueBuilder()
                .identifier(countername)
                .identifier(style)
                .buildAsList();

        v.add(new FunctionValue("counter", args));
        return this;
    }


    public ValueBuilder counters(String countername, String separator)
    {
        ValueList args=new ValueBuilder()
                .identifier(countername)
                .string(separator)
                .buildAsList();

        v.add(new FunctionValue("counters", args));
        return this;
    }

    public ValueBuilder counters(String countername, String separator, String style)
    {
        ValueList args=new ValueBuilder()
                .identifier(countername)
                .string(separator)
                .identifier(style)
                .buildAsList();

        v.add(new FunctionValue("counters", args));
        return this;
    }

    public ValueBuilder attr(String myattr)
    {
        ValueList args=new ValueBuilder().identifier(myattr).buildAsList();
        v.add(new FunctionValue("attr", args));
        return this;
    }

    public ValueBuilder function(String funcName, Value arg)
    {
        ValueList args=new ValueBuilder()
                .value(arg)
                .buildAsList();

        v.add(new FunctionValue(funcName, args));
        return this;
    }

    public ValueBuilder function(String funcName, Value arg1, Value arg2)
    {
        ValueList args=new ValueBuilder()
                .value(arg1)
                .value(arg2)
                .buildAsList();

        v.add(new FunctionValue(funcName, args));
        return this;
    }

    public ValueBuilder function(String funcName, Value arg1, Value arg2, Value arg3)
    {
        ValueList args=new ValueBuilder()
                .value(arg1)
                .value(arg2)
                .value(arg3)
                .buildAsList();

        v.add(new FunctionValue(funcName, args));
        return this;
    }
}
