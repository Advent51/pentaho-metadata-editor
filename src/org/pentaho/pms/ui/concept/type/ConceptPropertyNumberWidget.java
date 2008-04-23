/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
package org.pentaho.pms.ui.concept.type;

import java.math.BigDecimal;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.changed.ChangedFlag;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptPropertyInterface;
import org.pentaho.pms.schema.concept.types.number.ConceptPropertyNumber;
import org.pentaho.pms.ui.util.Const;

public class ConceptPropertyNumberWidget extends ChangedFlag implements ConceptPropertyWidgetInterface
{
    private String name;
    private Text string;
    private boolean overwrite;
    private ConceptInterface concept;
    
    /**
     * @param name
     * @param string
     */
    public ConceptPropertyNumberWidget(ConceptInterface concept, String name, Text string)
    {
        super(); // ChangeFlag()

        this.concept = concept;
        this.name = name;
        this.string = string;
    }
    
    /**
     * @return the concept
     */
    public ConceptInterface getConcept()
    {
        return concept;
    }

    /**
     * @param concept the concept to set
     */
    public void setConcept(ConceptInterface concept)
    {
        this.concept = concept;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
    }

    public ConceptPropertyInterface getValue()
    {
        if (!hasChanged()) return null; // Return null if nothing changed! 
        return new ConceptPropertyNumber(name, new BigDecimal(string.getText()));
    }

    public void setValue(ConceptPropertyInterface property)
    {
        if (property!=null && property.toString()!=null) string.setText(property.toString());
    }
    
    public void setEnabled(boolean enabled)
    {
        string.setEnabled(enabled);
    }
    
    public void setFocus()
    {
        string.setFocus();
    }


    public static final Control getControl(Composite composite, ConceptInterface concept, final String name, Control lastControl, Map<String,ConceptPropertyWidgetInterface> conceptPropertyInterfaces)
    {
        PropsUI props = PropsUI.getInstance();
        ConceptPropertyInterface property = concept.getProperty(name);

        final Text string = new Text(composite, SWT.BORDER);
        string.setToolTipText(Messages.getString("ConceptPropertyNumberWidget.USER_ENTER_PROPERTY_NUMBER_NAME", name)); //$NON-NLS-1$ 
        props.setLook(string);
        FormData fdString = new FormData();
        fdString.left  = new FormAttachment(props.getMiddlePct(), Const.MARGIN);
        fdString.right = new FormAttachment(100, 0);
        if (lastControl!=null) fdString.top   = new FormAttachment(lastControl, Const.MARGIN); else fdString.top   = new FormAttachment(0, 0);
        string.setLayoutData(fdString);
        
        final ConceptPropertyWidgetInterface widgetInterface = new ConceptPropertyNumberWidget(concept, name, string);
        conceptPropertyInterfaces.put(name, widgetInterface);
        widgetInterface.setValue(property);    
        string.addModifyListener(new ModifyListener() { public void modifyText(ModifyEvent event) { widgetInterface.setChanged(); } });

        return string;
    }

}
