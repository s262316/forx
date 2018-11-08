package com.github.s262316.forx.tree.visual;

import java.awt.Font;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.InlineBlockRootBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.box.properties.BackgroundProperties;
import com.github.s262316.forx.box.properties.BlockProperties;
import com.github.s262316.forx.box.properties.BorderDescriptor;
import com.github.s262316.forx.box.properties.CSSPropertyComputer;
import com.github.s262316.forx.box.properties.ColourDescriptor;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.FloatProperties;
import com.github.s262316.forx.box.properties.LineDescriptor;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PositionDescriptor;
import com.github.s262316.forx.box.properties.PropertyAdaptor;
import com.github.s262316.forx.box.properties.TextProperties;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.properties.WordProperties;
import com.github.s262316.forx.box.properties.BorderStylesImpl;
import com.github.s262316.forx.css.CSSPropertiesReference;
import com.github.s262316.forx.css.PropertyReference;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.PseudoElementType;

public class AnonVisual implements Visual, VElement
{
    private GraphicsContext graphics_context;
    private String style_lang;
    private VElement container;
    private CSSPropertiesReference ref;
    private AnonReason anonReason;

    public AnonVisual(VElement container, GraphicsContext gfxCtx, String sl, CSSPropertiesReference ref, AnonReason anonReason)
    {
        graphics_context=gfxCtx;
        style_lang=sl;
        this.container=container;
        this.ref=ref;
        this.anonReason=anonReason;
    }

    @Override
    public GraphicsContext getGraphicsContext()
    {
        return graphics_context;
    }

    @Override
    public void calculateBorders(PropertyAdaptor on, BorderDescriptor borderdesc)
    {
        BorderStylesImpl.resolveBorders(on, this, borderdesc, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void computeMarginProperties(PropertyAdaptor on, MarginDescriptor margindesc)
    {
        CSSPropertyComputer.computeMarginProperties(on, this, margindesc, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workOutAbsolutePosition(PropertyAdaptor on, PositionDescriptor pd)
    {
        CSSPropertyComputer.workOutAbsolutePosition(on, this, pd, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workOutFlowDimensions(PropertyAdaptor on, DimensionsDescriptor dd)
    {
        CSSPropertyComputer.workOutFlowDimensions(on, this, dd, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workOutLineProperties(PropertyAdaptor on, LineDescriptor ld, GraphicsContext graphicsContext)
    {
        CSSPropertyComputer.workOutLineProperties(on, this, ld, PseudoElementType.PE_NOT_PSEUDO, graphicsContext);
    }

    @Override
    public void workOutTextProperties(PropertyAdaptor on, TextProperties tp)
    {
        CSSPropertyComputer.workOutTextProperties(on, this, tp, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workOutWordProperties(PropertyAdaptor on, WordProperties wp)
    {
        CSSPropertyComputer.workOutWordProperties(on, this, wp, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public Font workOutFontProperties(PropertyAdaptor on)
    {
        return CSSPropertyComputer.workOutFontProperties(on, this, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workOutFloatProperties(PropertyAdaptor on, FloatProperties fp)
    {
        CSSPropertyComputer.workOutFloatProperties(on, this, fp, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workoutBlockProperties(PropertyAdaptor on, BlockProperties bp)
    {
        CSSPropertyComputer.workoutBlockProperties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workoutColours(PropertyAdaptor on, ColourDescriptor coldesc)
    {
        CSSPropertyComputer.workoutColours(on, this, coldesc, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public void workout_background_properties(PropertyAdaptor on, BackgroundProperties bp)
    {
        CSSPropertyComputer.workout_background_properties(on, this, bp, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public InlineBox createAnonInlineBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), style_lang, ref, anonReason);

        return BoxFactory.createAnonymousInlineFlowBox(anon);
    }

    @Override
    public BlockBox createAnonBlockBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), style_lang, ref, anonReason);

        return BoxFactory.createAnonymousBlockFlowBox(anon);
    }

    @Override
    public InlineBlockRootBox createAnonInlineBlockRootBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), style_lang, ref, anonReason);

        return BoxFactory.createAnonInlineBlockRootBox(anon);
    }

    @Override
    public TableRow createAnonRowBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), style_lang, ref, anonReason);

        return BoxFactory.createAnonRowBox(anon);
    }

    @Override
    public Column createAnonColBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), style_lang, ref, anonReason);

        return BoxFactory.createAnonColBox(anon);
    }

    @Override
    public TableBox createAnonTableBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), style_lang, ref, anonReason);

        return BoxFactory.createAnonTableBox(anon);
    }

    @Override
    public CellBox createAnonCellBox(AnonReason anonReason)
    {
        AnonVisual anon;

        anon=new AnonVisual(this, getGraphicsContext(), style_lang, ref, anonReason);

        return BoxFactory.createAnonCellBox(anon);
    }

    @Override
    public Value getPropertyValue(String property, MediaType mediaType, PseudoElementType pseudoType)
    {
        // does it inherit?
        PropertyReference sd=ref.getPropertyDescriptor(property);
        if(sd!=null)
        {
            if(sd.inherited)
                return container.getPropertyValue(property, mediaType, pseudoType);
            else
                return sd.def;
        }

        return null;
    }

    @Override
    public Value getPropertyValue(String property, MediaType mediaType)
    {
        return getPropertyValue(property, mediaType, PseudoElementType.PE_NOT_PSEUDO);
    }

    @Override
    public VElement find_counter(String name)
    {
        return null;
    }

    @Override
    public void inc_counter(String name, int amount)
    {
    }

    @Override
    public void reset_counter(String name, int to)
    {
    }

    @Override
    public Optional<Integer> counter_value(String name)
    {
        return Optional.of(1);
    }

    public VElement find_counter_before(String name)
    {
        return null;
    }

    @Override
    public String resolve_resource(URL url)
    {
            return "";
    }

    @Override
    public void computed_value(String property, Value value)
    {
    }

    public void clearStyles()
    {
    }

    public void setStyles(List<Declaration> decs)
    {
    }

    public String getDefaultStyleLanguage()
    {
        return "";
    }

}


