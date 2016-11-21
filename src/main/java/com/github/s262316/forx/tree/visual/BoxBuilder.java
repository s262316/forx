package com.github.s262316.forx.tree.visual;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.core.real.BoxRealMapping;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.tree.style.util.GenerateABox;

public class BoxBuilder extends XMutationListener
{
	private final static Logger logger=LoggerFactory.getLogger(BoxBuilder.class);

    private BoxRealMapping screenTranslator;
	
    public BoxBuilder(XmlVElement listenee, BoxRealMapping screenTranslator)
    {
    	super(new GenerateABox(), PropagationType.ON_TARGET, listenee, EnumSet.allOf(MutationType.class));
    	
    	this.screenTranslator=screenTranslator;
    }

	@Override
	public void added(XmlMutationEvent event)
    {
        XmlVElement element;

		logger.debug("eventAdd {}", event);

        element=(XmlVElement) event.getSubject();
        if(element.parentNode()==element.getDocument())
        {
            element.plant_root();
			screenTranslator.add(element, element.visualBox());
            
		}
        else
        {
//				// temporary...
//				if(element->getName()=="orange")
//				{
//					PLength nwidth(PL_AUTO), nheight(PL_AUTO);
//					int pos, numerator, divisor;
//					double nratio;
//					string iwidth=element->getAttr("iwidth")->getValue();
//					string iheight=element->getAttr("iheight")->getValue();
//					string iratio=element->getAttr("iratio")->getValue();
//
//					if(iwidth!="none")
//					{
//						istringstream str1(iwidth, ios_base::in);
//						nwidth.specified=PL_SPECIFIED;
//						str1 >> nwidth.value;
//					}
//					else
//						nwidth=PL_AUTO;
//
//					if(iheight!="none")
//					{
//						istringstream str2(iheight, ios_base::in);
//						nheight.specified=PL_SPECIFIED;
//						str2 >> nheight.value;
//					}
//					else
//						nheight=PL_AUTO;
//
//					if(iratio!="none")
//					{
//						pos=iratio.find_first_of(":");
//
//						istringstream str3(iratio.substr(0, pos), ios_base::in);
//						str3 >> numerator;
//
//						istringstream str4(iratio.substr(pos+1), ios_base::in);
//						str4 >> divisor;
//
//						nratio=double(numerator)/double(divisor);
//					}
//					else
//						nratio=-1;
//
//					element->set_plugin_handler(new orange::OrangePlugin(nwidth, nheight, nratio));
//
//				}
//
			element.self_pollenate();
			if(element.visualBox()!=null)
				screenTranslator.add(element, element.visualBox());

			
        }
    }

	@Override
	public void removed(XmlMutationEvent event)
	{
        XmlVElement element;

        // is this the target
        element=(XmlVElement) event.getSubject();
        if(element.getDocument() == element.parentNode())
            element.unplant_root();
        else
            element.self_depollenate();
    }

	@Override
	public void connected(XmlMutationEvent event)
	{
//        XmlVElement element;
//
//        element=(XmlVElement) event.getSubject();
//        if(element.getDocument() == element.parentNode())
//            element.plant_root();
//        else
//            element.self_pollenate();
    }
    
    @Override
	public void disconnected(XmlMutationEvent event)
    {
//        XmlVElement element;
//
//        element=(XmlVElement) event.getSubject();
//        if(element.getDocument() == element.parentNode())
//            element.unplant_root();
//        else
//            element.self_depollenate();
    }
}

