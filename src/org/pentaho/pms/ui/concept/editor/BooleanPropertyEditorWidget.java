package org.pentaho.pms.ui.concept.editor;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class BooleanPropertyEditorWidget extends AbstractPropertyEditorWidget implements FocusListener {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(BooleanPropertyEditorWidget.class);

  // ~ Instance fields =================================================================================================

  private Button button;

  // ~ Constructors ====================================================================================================

  public BooleanPropertyEditorWidget(final Composite parent, final int style, final IConceptModel conceptModel,
      final String propertyId, final Map context) {
    super(parent, style, conceptModel, propertyId, context);
    refresh();
    if (logger.isDebugEnabled()) {
      logger.debug("created BooleanPropertyEditorWidget");
    }
  }

  // ~ Methods =========================================================================================================

  protected void createContents(final Composite parent) {
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        BooleanPropertyEditorWidget.this.widgetDisposed(e);
      }
    });
    button = new Button(parent, SWT.CHECK);
    button.setText(PredefinedVsCustomPropertyHelper.getDescription(getPropertyId()));

    FormData fdCheck = new FormData();
    fdCheck.left = new FormAttachment(0, 0);
    fdCheck.top = new FormAttachment(0, 0);
    button.setLayoutData(fdCheck);

    button.addFocusListener(this);
  }

  protected void widgetDisposed(final DisposeEvent e) {
  }

  public Object getValue() {
    return new Boolean(button.getSelection());
  }

  protected void setValue(final Object value) {
    button.setSelection(((Boolean) value).booleanValue());
  }

  protected boolean isValid() {
    return true;
  }

  public void focusGained(FocusEvent arg0) {
    // Do nothing
    
  }

  public void focusLost(FocusEvent arg0) {
    if (!getProperty().getValue().equals(new Boolean(button.getSelection()))) {
      putPropertyValue();
    }
  }

  public void refresh() {
    refreshOverrideButton();
    button.setEnabled(isEditable());
    setValue(getProperty().getValue());
  }
  
  public void cleanup() {
    button.removeFocusListener(this);
  }
}
