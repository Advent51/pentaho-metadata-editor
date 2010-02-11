/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.pms.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pms.messages.Messages;
import org.pentaho.pms.schema.BusinessColumn;
import org.pentaho.pms.schema.BusinessModel;
import org.pentaho.pms.schema.BusinessTable;
import org.pentaho.pms.schema.RelationshipMeta;
import org.pentaho.pms.ui.util.Const;
import org.pentaho.pms.ui.util.GUIResource;

public class RelationshipDialog extends Dialog {
  private Label wlFrom;

  private CCombo wFrom;

  private FormData fdlFrom, fdFrom;

  private Label wlTo;

  private CCombo wTo;

  private FormData fdlTo, fdTo;

  private CCombo wFromField;

  private FormData fdFromField;

  private CCombo wToField;

  private FormData fdToField;

  private Button wGuess;

  private FormData fdGuess;

  private Listener lsGuess;

  private Label wlRelation;

  private CCombo wRelation;

  private FormData fdlRelation, fdRelation;

  private Button wGuessRel;

  private FormData fdGuessRel;

  private Listener lsGuessRel;

  private Label wlComplex;

  private Button wComplex;

  private FormData fdlComplex, fdComplex;

  private Label wlComplexJoin;
  private Text wComplexJoin;
  private FormData fdlComplexJoin, fdComplexJoin;

  private Label wlDescription;
  private Text wDescription;
  private FormData fdlDescription, fdDescription;

  private Label wlJoinType;
  private CCombo wJoinType;
  
  private Label wlJoinOrderKey;
  private Text  wJoinOrderKey;

  private Button wOK, wCancel;

  private Listener lsOK, lsCancel;

  private RelationshipMeta relationshipMeta;

  private Shell shell;

  private BusinessModel businessModel;

  private BusinessTable fromtable, totable;

  private ModifyListener lsMod;

  private boolean changed, backupComplex;

private FormData fdlJoinType;

  public RelationshipDialog(Shell parent, int style, LogChannelInterface l, RelationshipMeta relationshipMeta,
      BusinessModel businessModel) {
    super(parent, style);
    this.relationshipMeta = relationshipMeta;
    this.businessModel = businessModel;

    fromtable = relationshipMeta.getTableFrom();
    totable = relationshipMeta.getTableTo();
  }

  public Object open() {
    PropsUI props = PropsUI.getInstance();
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
    shell.setBackground(GUIResource.getInstance().getColorBackground());

    lsMod = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        relationshipMeta.setChanged();
      }
    };
    changed = relationshipMeta.hasChanged();
    backupComplex = relationshipMeta.isComplex();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout(formLayout);
    shell.setText(Messages.getString("RelationshipDialog.USER_HOP_FROM_TO")); //$NON-NLS-1$

    int middle = props.getMiddlePct();
    int length = 350;
    int margin = Const.MARGIN;

    //////////////////////////////////////////////////////////////////////
    // From step line
    //
    wFrom = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wFrom.setText(Messages.getString("RelationshipDialog.USER_SELECT_SOURCE_TABLE")); //$NON-NLS-1$
    props.setLook(wFrom);
    for (int i = 0; i < businessModel.nrBusinessTables(); i++) {
      BusinessTable ti = businessModel.getBusinessTable(i);
      wFrom.add(ti.getId());
    }
    wFrom.addModifyListener(lsMod);
    wFrom.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        // grab the new fromtable:
        fromtable = businessModel.findBusinessTable(wFrom.getText());
        refreshFromFields();
      }
    });
    fdFrom = new FormData();
    fdFrom.left = new FormAttachment(middle, margin);
    fdFrom.top = new FormAttachment(0, margin);
    fdFrom.right = new FormAttachment(60, 0);
    wFrom.setLayoutData(fdFrom);

    wlFrom = new Label(shell, SWT.RIGHT);
    wlFrom.setText(Messages.getString("RelationshipDialog.USER_FROM_TABLE_FIELD")); //$NON-NLS-1$
    props.setLook(wlFrom);
    fdlFrom = new FormData();
    fdlFrom.left = new FormAttachment(0, 0);
    fdlFrom.right = new FormAttachment(middle, -margin);
    fdlFrom.top = new FormAttachment(wFrom, 0, SWT.CENTER);
    wlFrom.setLayoutData(fdlFrom);

    //////////////////////////////////////////////////////////////////////
    // From field...
    //
    wFromField = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wFromField.setText(""); //$NON-NLS-1$
    props.setLook(wFromField);
    refreshFromFields();
    wFromField.addModifyListener(lsMod);
    fdFromField = new FormData();
    fdFromField.left = new FormAttachment(wFrom, margin * 2);
    fdFromField.top = new FormAttachment(0, margin);
    fdFromField.right = new FormAttachment(100, 0);
    wFromField.setLayoutData(fdFromField);

    //////////////////////////////////////////////////////////////////////
    // To line
    //
    wTo = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
    wTo.setText(Messages.getString("RelationshipDialog.USER_SELECT_DESTINATION_TABLE")); //$NON-NLS-1$
    props.setLook(wTo);
    for (int i = 0; i < businessModel.nrBusinessTables(); i++) {
      BusinessTable ti = businessModel.getBusinessTable(i);
      wTo.add(ti.getId());
    }
    wTo.addModifyListener(lsMod);
    wTo.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        // grab the new fromtable:
        totable = businessModel.findBusinessTable(wTo.getText());
        refreshToFields();
      }
    });
    fdTo = new FormData();
    fdTo.left = new FormAttachment(middle, margin);
    fdTo.top = new FormAttachment(wFrom, margin);
    fdTo.right = new FormAttachment(60, 0);
    wTo.setLayoutData(fdTo);

    wlTo = new Label(shell, SWT.RIGHT);
    wlTo.setText(Messages.getString("RelationshipDialog.USER_TO_TABLE_FIELD")); //$NON-NLS-1$
    props.setLook(wlTo);
    fdlTo = new FormData();
    fdlTo.left = new FormAttachment(0, 0);
    fdlTo.right = new FormAttachment(middle, -margin);
    fdlTo.top = new FormAttachment(wTo, 0, SWT.CENTER);
    wlTo.setLayoutData(fdlTo);

    //////////////////////////////////////////////////////////////////////
    // ToField step line
    //
    wToField = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wToField.setText(Messages.getString("RelationshipDialog.USER_SELECT_THE_FIELD")); //$NON-NLS-1$
    props.setLook(wToField);
    refreshToFields();
    wToField.addModifyListener(lsMod);
    fdToField = new FormData();
    fdToField.left = new FormAttachment(wTo, margin * 2);
    fdToField.top = new FormAttachment(wFromField, margin);
    fdToField.right = new FormAttachment(100, 0);
    wToField.setLayoutData(fdToField);

    //////////////////////////////////////////////////////////////////////
    // The "Guess matching fields" button
    //
    wGuess = new Button(shell, SWT.PUSH);
    wGuess.setText(Messages.getString("RelationshipDialog.USER_GUESS_MATCHING_FIELDS")); //$NON-NLS-1$
    lsGuess = new Listener() {
      public void handleEvent(Event e) {
        guess();
      }
    };
    wGuess.addListener(SWT.Selection, lsGuess);
    fdGuess = new FormData();
    fdGuess.left = new FormAttachment(wTo, margin * 2);
    fdGuess.top = new FormAttachment(wToField, margin);
    wGuess.setLayoutData(fdGuess);

    //////////////////////////////////////////////////////////////////////
    // Relation line
    //
    wRelation = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wRelation);
    wRelation.setItems(RelationshipMeta.typeRelationshipDesc);
    fdRelation = new FormData();
    fdRelation.left = new FormAttachment(middle, margin);
    fdRelation.top = new FormAttachment(wGuess, margin * 6);
    fdRelation.right = new FormAttachment(60, 0);
    wRelation.setLayoutData(fdRelation);
    
    wGuessRel = new Button(shell, SWT.PUSH);
    wGuessRel.setText(Messages.getString("RelationshipDialog.USER_GUESS_RELATIONSHIP")); //$NON-NLS-1$
    lsGuessRel = new Listener() {
      public void handleEvent(Event e) {
        guessRelationship();
      }
    };
    wGuessRel.addListener(SWT.Selection, lsGuessRel);
    fdGuessRel = new FormData();
    fdGuessRel.left = new FormAttachment(wRelation, margin * 2);
    fdGuessRel.top = new FormAttachment(wRelation, 0, SWT.CENTER);
    wGuessRel.setLayoutData(fdGuessRel);

    wlRelation = new Label(shell, SWT.RIGHT);
    wlRelation.setText(Messages.getString("RelationshipDialog.USER_RELATIONSHIP")); //$NON-NLS-1$
    props.setLook(wlRelation);
    fdlRelation = new FormData();
    fdlRelation.left = new FormAttachment(0, 0);
    fdlRelation.right = new FormAttachment(middle, -margin);
    fdlRelation.top = new FormAttachment(wRelation, 0, SWT.CENTER);
    wlRelation.setLayoutData(fdlRelation);

    //////////////////////////////////////////////////////////////////////
    // JoinType line
    //
    wJoinType = new CCombo(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
    props.setLook(wJoinType);
    wJoinType.setItems(RelationshipMeta.typeJoinDesc);
    wJoinType.addModifyListener(lsMod);
    FormData fdJoinType = new FormData();
    fdJoinType.left = new FormAttachment(middle, 0);
    fdJoinType.top = new FormAttachment(wGuessRel, margin * 2);
    fdJoinType.right = new FormAttachment(60, 0);
    wJoinType.setLayoutData(fdJoinType);
    wlJoinType = new Label(shell, SWT.RIGHT);
    wlJoinType.setText(Messages.getString("RelationshipDialog.USER_JOINTYPE")); //$NON-NLS-1$
    props.setLook(wlJoinType);
    fdlJoinType = new FormData();
    fdlJoinType.left = new FormAttachment(0, 0);
    fdlJoinType.right = new FormAttachment(middle, -margin);
    fdlJoinType.top = new FormAttachment(wJoinType, 0, SWT.CENTER);
    wlJoinType.setLayoutData(fdlJoinType);

    //////////////////////////////////////////////////////////////////////
    // Add the join sort order next to the join type...
    //
    wlJoinOrderKey = new Label(shell, SWT.LEFT);
    wlJoinOrderKey.setText(Messages.getString("RelationshipDialog.USER_JOIN_SORT_KEY")); //$NON-NLS-1$
    props.setLook(wlJoinOrderKey);
    FormData fdlJoinOrderKey = new FormData();
    fdlJoinOrderKey.left = new FormAttachment(wJoinType, margin*2);
    fdlJoinOrderKey.top = new FormAttachment(wJoinType, 0, SWT.CENTER);
    wlJoinOrderKey.setLayoutData(fdlJoinOrderKey);
    
    wJoinOrderKey = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook(wJoinOrderKey);
    wJoinOrderKey.addModifyListener(lsMod);
    FormData fdJoinOrderKey = new FormData();
    fdJoinOrderKey.left = new FormAttachment(wlJoinOrderKey, margin);
    fdJoinOrderKey.top = new FormAttachment(wJoinType, 0, SWT.CENTER);
    fdJoinOrderKey.right = new FormAttachment(100, 0);
    wJoinOrderKey.setLayoutData(fdJoinOrderKey);

    
    //////////////////////////////////////////////////////////////////////
    // Complex check box
    //
    wComplex = new Button(shell, SWT.CHECK);
    props.setLook(wComplex);
    fdComplex = new FormData();
    fdComplex.left = new FormAttachment(middle, 0);
    fdComplex.top = new FormAttachment(wJoinType, margin*6);
    wComplex.setLayoutData(fdComplex);
    wComplex.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        relationshipMeta.flipComplex();
        relationshipMeta.setChanged();
        setComplex();
      }
    });

    wlComplex = new Label(shell, SWT.RIGHT);
    wlComplex.setText(Messages.getString("RelationshipDialog.USER_COMPLEX_JOIN")); //$NON-NLS-1$
    props.setLook(wlComplex);
    fdlComplex = new FormData();
    fdlComplex.left = new FormAttachment(0, 0);
    fdlComplex.right = new FormAttachment(middle, -margin);
    fdlComplex.top = new FormAttachment(wComplex, 0, SWT.CENTER);
    wlComplex.setLayoutData(fdlComplex);

    
    // ComplexJoin line
    wlComplexJoin = new Label(shell, SWT.RIGHT);
    wlComplexJoin.setText(Messages.getString("RelationshipDialog.USER_COMPLEX_JOIN_EXPRESSION")); //$NON-NLS-1$
    props.setLook(wlComplexJoin);
    fdlComplexJoin = new FormData();
    fdlComplexJoin.left = new FormAttachment(0, 0);
    fdlComplexJoin.right = new FormAttachment(middle, -margin);
    fdlComplexJoin.top = new FormAttachment(wComplex, margin);
    wlComplexJoin.setLayoutData(fdlComplexJoin);
    wComplexJoin = new Text(shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    wComplexJoin.setText(""); //$NON-NLS-1$
    props.setLook(wComplexJoin);
    wComplexJoin.addModifyListener(lsMod);
    fdComplexJoin = new FormData();
    fdComplexJoin.left = new FormAttachment(middle, margin);
    fdComplexJoin.right = new FormAttachment(100, 0);
    fdComplexJoin.top = new FormAttachment(wlComplexJoin, margin);
    fdComplexJoin.bottom = new FormAttachment(wlComplexJoin, 100);
    wComplexJoin.setLayoutData(fdComplexJoin);

    // Description
    wlDescription = new Label(shell, SWT.RIGHT);
    wlDescription.setText(Messages.getString("RelationshipDialog.USER_DESCRIPTION")); //$NON-NLS-1$
    props.setLook(wlDescription);
    fdlDescription = new FormData();
    fdlDescription.left = new FormAttachment(0, 0);
    fdlDescription.right = new FormAttachment(middle, -margin);
    fdlDescription.top = new FormAttachment(wComplexJoin, margin);
    wlDescription.setLayoutData(fdlDescription);
    wDescription = new Text(shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    wDescription.setText(""); //$NON-NLS-1$
    props.setLook(wDescription);
    wDescription.addModifyListener(lsMod);
    fdDescription = new FormData();
    fdDescription.left = new FormAttachment(middle, margin);
    fdDescription.right = new FormAttachment(100, 0);
    fdDescription.top = new FormAttachment(wlDescription, margin);
    fdDescription.bottom = new FormAttachment(100, -50);
    wDescription.setLayoutData(fdDescription);

    
    // Some buttons
    wOK = new Button(shell, SWT.PUSH);
    wOK.setText(Messages.getString("General.USER_OK")); //$NON-NLS-1$
    wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(Messages.getString("General.USER_CANCEL")); //$NON-NLS-1$

    BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent(Event e) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent(Event e) {
        ok();
      }
    };
    
    // If someone changes the relationship, we automatically modify the join type.
    //
    wRelation.addSelectionListener(new SelectionAdapter() {
    	public void widgetSelected(SelectionEvent arg0) {
    		wJoinType.select(RelationshipMeta.getJoinType(wRelation.getSelectionIndex()));
		}
	});
    
    // If someone changes the join type (informational only) we change the relationship.
    // This is a lossy process, but it's better than nothing.
    //
    wJoinType.addSelectionListener(new SelectionAdapter() {
    	public void widgetSelected(SelectionEvent event) {
    		wRelation.select(RelationshipMeta.getRelationType(wJoinType.getSelectionIndex()));
		}
	});
    
    wOK.addListener(SWT.Selection, lsOK);
    wCancel.addListener(SWT.Selection, lsCancel);

    // Detect [X] or ALT-F4 or something that kills this window...
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        cancel();
      }
    });

    getData();
    relationshipMeta.setChanged(changed);

    shell.layout();
    
    WindowProperty winprop = props.getScreen(shell.getText());
    if (winprop != null)
      winprop.setShell(shell);
    else
      shell.pack();

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    return relationshipMeta;
  }

  public void setComplex() {
    wFromField.setEnabled(relationshipMeta.isRegular());
    wToField.setEnabled(relationshipMeta.isRegular());
    wComplexJoin.setEnabled(relationshipMeta.isComplex());
    wlComplexJoin.setEnabled(relationshipMeta.isComplex());

    /*
     if (relationshipMeta.isRegular())
     {
     wFromField.setBackground(bg);
     wToField.setBackground(bg);
     wComplexJoin.setBackground(gray);
     }
     else
     {
     wFromField.setBackground(gray);
     wToField.setBackground(gray);
     wComplexJoin.setBackground(bg);
     }
     */
  }

  public void refreshFromFields() {
    wFromField.removeAll();
    if (fromtable != null) {
      for (int i = 0; i < fromtable.nrBusinessColumns(); i++) {
        BusinessColumn f = fromtable.getBusinessColumn(i);
        wFromField.add(f.getId());
      }
    }
  }

  public void refreshToFields() {
    wToField.removeAll();
    if (totable != null) {
      for (int i = 0; i < totable.nrBusinessColumns(); i++) {
        BusinessColumn f = totable.getBusinessColumn(i);
        wToField.add(f.getId());
      }
    }
  }

  public void dispose() {
    PropsUI.getInstance().setScreen(new WindowProperty(shell));
    shell.dispose();
  }

  /**
   * Copy information from the meta-data relationshipMeta to the dialog fields.
   */
  public void getData() {
    if (relationshipMeta.getTableFrom() != null)
      wFrom.setText(relationshipMeta.getTableFrom().getId());
    if (relationshipMeta.getTableTo() != null)
      wTo.setText(relationshipMeta.getTableTo().getId());

    if (relationshipMeta.getFieldFrom() != null) {
      int idx = wFromField.indexOf(relationshipMeta.getFieldFrom().getId());
      if (idx >= 0)
        wFromField.select(idx);
    }
    if (relationshipMeta.getFieldTo() != null) {
      int idx = wToField.indexOf(relationshipMeta.getFieldTo().getId());
      if (idx >= 0)
        wToField.select(idx);
    }

    wRelation.select(relationshipMeta.getType());
    wJoinType.select(relationshipMeta.getJoinType());
    wComplex.setSelection(relationshipMeta.isComplex());
    if (relationshipMeta.getComplexJoin() != null)
      wComplexJoin.setText(relationshipMeta.getComplexJoin());
    setComplex();
    
    wDescription.setText(Const.NVL(relationshipMeta.getDescription(), ""));
    wJoinOrderKey.setText(Const.NVL(relationshipMeta.getJoinOrderKey(), ""));
  }

  private void cancel() {
    relationshipMeta.setChanged(changed);
    relationshipMeta.setComplex(backupComplex);
    relationshipMeta = null;
    dispose();
  }

  private void ok() {
    BusinessTable tableFrom = businessModel.findBusinessTable(wFrom.getText());
    relationshipMeta.setTableFrom(tableFrom);

    BusinessTable tableTo = businessModel.findBusinessTable(wTo.getText());
    relationshipMeta.setTableTo(tableTo);

    if (tableFrom != null) {
      BusinessColumn fieldFrom = tableFrom.findBusinessColumn(wFromField.getText());
      relationshipMeta.setFieldFrom(fieldFrom);
    }
    if (tableTo != null) {
      BusinessColumn fieldTo = tableTo.findBusinessColumn(wToField.getText());
      relationshipMeta.setFieldTo(fieldTo);
    }

    relationshipMeta.setType(wRelation.getSelectionIndex());
    
    relationshipMeta.setComplexJoin(wComplexJoin.getText());
    
    relationshipMeta.setDescription(wDescription.getText());
    relationshipMeta.setJoinOrderKey(wJoinOrderKey.getText());

    if (relationshipMeta.getTableFrom() == null) {
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING);
      
      // Is there a problem with the chosen table name, or did the user not choose a table at all?
      if (wFrom.getText().equals(Messages.getString("RelationshipDialog.USER_SELECT_SOURCE_TABLE")))
        mb.setMessage(Messages.getString("RelationshipDialog.USER_SELECT_SOURCE_TABLE")); //$NON-NLS-1$ 
      else
        mb.setMessage(Messages.getString("RelationshipDialog.USER_WARNING_TABLE_DOESNT_EXIST", wFrom.getText())); //$NON-NLS-1$ 
      
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      mb.open();
      return;
    } 

    if (relationshipMeta.getTableTo() == null) {
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING);

      // Is there a problem with the chosen table name, or did the user not choose a table at all?
      if (wTo.getText().equals(Messages.getString("RelationshipDialog.USER_SELECT_DESTINATION_TABLE")))
        mb.setMessage(Messages.getString("RelationshipDialog.USER_SELECT_DESTINATION_TABLE")); //$NON-NLS-1$ 
      else
        mb.setMessage(Messages.getString("RelationshipDialog.USER_WARNING_TABLE_DOESNT_EXIST", wTo.getText())); //$NON-NLS-1$ 

      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      mb.open();
      return;
    } 
    
    if (relationshipMeta.getTableFrom().getId().equalsIgnoreCase(relationshipMeta.getTableTo().getId())) {
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("RelationshipDialog.USER_WARNING_RELATIONSHIP_SAME_TABLE_NOT_ALLOWED")); //$NON-NLS-1$
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      mb.open();
      return;
    }
    
    if(!relationshipMeta.isComplex() && relationshipMeta.getFieldFrom()==null){
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("RelationshipDialog.USER_WARNING_FROM_FIELD_NOT_DEFINED")); //$NON-NLS-1$
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      mb.open();
      return;      
    }
    
    if(!relationshipMeta.isComplex() && relationshipMeta.getFieldTo()==null){
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("RelationshipDialog.USER_WARNING_TO_FIELD_NOT_DEFINED")); //$NON-NLS-1$
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      mb.open();
      return;      
    }

    if(relationshipMeta.getType()<=0){
      MessageBox mb = new MessageBox(shell, SWT.YES | SWT.ICON_WARNING);
      mb.setMessage(Messages.getString("RelationshipDialog.USER_WARNING_TYPE_NOT_DEFINED")); //$NON-NLS-1$
      mb.setText(Messages.getString("General.USER_TITLE_WARNING")); //$NON-NLS-1$
      mb.open();
      return;      
    }

    dispose();
  }

  // Try to find fields with the same name in both tables...
  public void guess() {
    String from[] = wFromField.getItems();
    String to[] = wToField.getItems();

    // What is the longest string?
    int longest = -1;
    for (int i = 0; i < from.length; i++)
      if (from[i].length() > longest)
        longest = from[i].length();
    for (int i = 0; i < to.length; i++)
      if (to[i].length() > longest)
        longest = to[i].length();

    for (int length = longest; length > 3; length--) {
      for (int i = 0; i < from.length; i++) {

        for (int j = 0; j < to.length; j++) {
          String one = wFromField.getItem(i);
          String two = wToField.getItem(j);

          int endOne = length;
          if (endOne > one.length())
            endOne = one.length();
          int endTwo = length;
          if (endTwo > two.length())
            endTwo = two.length();

          String leftOne = one.substring(0, endOne);
          String leftTwo = two.substring(0, endTwo);

          if (leftOne.equalsIgnoreCase(leftTwo)) {
            wFromField.select(i);
            wToField.select(j);
            return;
          }

          int startOne = one.length() - length;
          if (startOne < 0)
            startOne = 0;
          int startTwo = two.length() - length;
          if (startTwo < 0)
            startTwo = 0;

          String rightOne = one.substring(startOne, one.length());
          String rightTwo = two.substring(startTwo, two.length());

          if (rightOne.equalsIgnoreCase(rightTwo)) {
            wFromField.select(i);
            wToField.select(j);
            return;
          }
        }
      }
    }
  }

  // Try to find fields with the same name in both tables...
  public void guessRelationship() {
    if (fromtable != null && totable != null) {
      if (fromtable.isFactTable() && totable.isDimensionTable())
        wRelation.select(RelationshipMeta.TYPE_RELATIONSHIP_N_1);
      if (fromtable.isDimensionTable() && totable.isFactTable())
        wRelation.select(RelationshipMeta.TYPE_RELATIONSHIP_1_N);
      if (fromtable.isFactTable() && totable.isFactTable())
        wRelation.select(RelationshipMeta.TYPE_RELATIONSHIP_N_N);
    }
  }
}
