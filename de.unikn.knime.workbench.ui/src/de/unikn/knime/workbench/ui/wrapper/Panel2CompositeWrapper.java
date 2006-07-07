/* 
 * -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 * 
 * Copyright, 2003 - 2006
 * Universitaet Konstanz, Germany.
 * Lehrstuhl fuer Angewandte Informatik
 * Prof. Dr. Michael R. Berthold
 * 
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner.
 * -------------------------------------------------------------------
 * 
 * History
 *   09.02.2005 (georg): created
 */
package de.unikn.knime.workbench.ui.wrapper;

import java.awt.Frame;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Wrapper Composite that uses the SWT_AWT bridge to embed an AWT Panel into a
 * SWT composite.
 * 
 * @author Florian Georg, University of Konstanz
 */
public class Panel2CompositeWrapper extends Composite {

    private Frame m_awtFrame;

    private JPanel m_awtPanel;

    /**
     * Creates a new wrapper.
     * 
     * @param parent The parent composite
     * @param panel The AWT panel to wrap
     * @param style Style bits, ignored so far
     */
    public Panel2CompositeWrapper(final Composite parent,
            final JPanel panel, final int style) {
        super(parent, style | SWT.EMBEDDED);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        m_awtFrame = SWT_AWT.new_Frame(this);
        // use panel as root
        m_awtPanel = panel;
        m_awtFrame.add(m_awtPanel);
        
        // Pack the frame
        m_awtFrame.pack();
        m_awtFrame.setVisible(true);

    }

    /**
     * @see org.eclipse.swt.widgets.Widget#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    /**
     * @see org.eclipse.swt.widgets.Widget#checkSubclass()
     */
    protected void checkSubclass() {
    }

    /**
     * @return The wrapped panel, as it can be used within legacy AWT/Swing code
     */
    public JPanel getAwtPanel() {
        return m_awtPanel;
    }

    
}
