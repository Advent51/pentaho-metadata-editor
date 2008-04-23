package org.pentaho.pms.ui.concept.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.PhysicalColumn;
import org.pentaho.pms.schema.PhysicalTable;
import org.pentaho.pms.schema.concept.ConceptInterface;
import org.pentaho.pms.schema.concept.ConceptUtilityBase;
import org.pentaho.pms.schema.concept.ConceptUtilityInterface;
import org.pentaho.pms.util.ObjectAlreadyExistsException;
import org.pentaho.pms.util.UniqueList;


public class BusinessTableModel extends AbstractTableModel {

  // ~ Static fields/initializers ======================================================================================

  private static final Log logger = LogFactory.getLog(BusinessTableModel.class);

  // ~ Instance fields =================================================================================================

  private BusinessTable table;

  private PhysicalTable physicalTable;
  
  private BusinessModel businessModel;

  // ~ Constructors ====================================================================================================

  public BusinessTableModel(final BusinessTable table, final BusinessModel businessModel) {
    this(table, table.getPhysicalTable(), businessModel);
  }

  public BusinessTableModel(final BusinessTable table, final PhysicalTable physicalTable, final BusinessModel businessModel) {
    super();
    this.table = table;
    setParent(physicalTable);
    this.businessModel=businessModel;
  }

  // ~ Methods =========================================================================================================

  public void addAllColumns(final ConceptUtilityInterface[] columns) throws ObjectAlreadyExistsException {
    // TODO mlowery should make this rollback on exception
    for (int i = 0; i < columns.length; i++) {
      table.addBusinessColumn((BusinessColumn) columns[i]);
      fireTableModificationEvent(createAddColumnEvent(columns[i].getId()));
    }
  }

  /**
   * Here the id is the name of the physical column on which to base the new business column.
   */
  public void addColumn(final String id, final String localeCode) throws ObjectAlreadyExistsException {
    if (id != null) {
      PhysicalColumn physicalColumn = physicalTable.findPhysicalColumn(localeCode, id);
      String newBusinessColumnId = BusinessColumn.proposeId(localeCode, table, physicalColumn);
      BusinessColumn businessColumn = new BusinessColumn(newBusinessColumnId, physicalColumn, table);
      
      // TODO: HACK to force new unique ID on new columns; this can still be a problem, because 
      // business column ids need to be unique across all columns in the model... this 
      // only guarantees that they are unique to this table.
      UniqueList columns = businessModel != null ? businessModel.getAllBusinessColumns() : table.getBusinessColumns();
      businessColumn = businessColumn.cloneUnique(localeCode, columns);
      businessColumn.addIDChangedListener(ConceptUtilityBase.createIDChangedListener(table.getBusinessColumns()));
      table.addBusinessColumn(businessColumn);
      fireTableModificationEvent(createAddColumnEvent(newBusinessColumnId));
    }
  }

  public ConceptUtilityInterface[] getColumns() {
    return (ConceptUtilityInterface[]) table.getBusinessColumns().toArray(new ConceptUtilityInterface[0]);
  }

  public ConceptInterface getConcept() {
    return table.getConcept();
  }

  public String getId() {
    return table.getId();
  }

  public ConceptUtilityInterface getWrappedTable() {
    return table;
  }

  public boolean isColumn(final ConceptUtilityInterface column) {
    return column instanceof BusinessColumn;
  }

  public void removeAllColumns() {
    String[] ids = table.getColumnIDs();
    for (int i = 0; i < ids.length; i++) {
      removeColumn(ids[i]);
    }
  }

  public void removeColumn(final String id) {
    BusinessColumn column = table.findBusinessColumn(id);
    if (null != column) {
      int index = table.indexOfBusinessColumn(column);
      table.removeBusinessColumn(index);
      fireTableModificationEvent(createRemoveColumnEvent(id));
    }
  }

  public void setParent(final ConceptUtilityInterface parent) {
    if (null == parent) {
      return;
    }
    if (parent instanceof PhysicalTable) {
      physicalTable = (PhysicalTable) parent;
      table.setPhysicalTable(physicalTable);
      if (logger.isDebugEnabled()) {
        logger.debug(Messages.getString("BusinessTableModel.DEBUG_SET_PARENT_TABLE", physicalTable.toString())); //$NON-NLS-1$
      }
    } else {
      throw new IllegalArgumentException(Messages.getString("BusinessTableModel.ERROR_0001_ARGUMENT_MUST_BE_PHYSICAL_TABLE")); //$NON-NLS-1$
    }
  }

  public Object clone() throws CloneNotSupportedException {
    return new BusinessTableModel((BusinessTable) table.clone(), (PhysicalTable) physicalTable.clone(), businessModel);
  }

  public ITableModel getParentAsTableModel() {
    if (null != physicalTable) {
      return new PhysicalTableModel(physicalTable);
    } else {
      return null;
    }
  }

  public ConceptUtilityInterface getParent() {
    return physicalTable;
  }
}
