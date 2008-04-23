package org.pentaho.pms.ui.concept.editor;

import java.util.EventListener;

/**
 * Notified when a concept is modified.
 * @author mlowery
 * @see ConceptModificationEvent
 */
public interface IConceptModificationListener extends EventListener {
  void conceptModified(final ConceptModificationEvent e);
}
