/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 * 
 * History
 *   02.02.2007 (Kilian Thiel): created
 */
package org.knime.base.node.mine.mds.distances;

import org.knime.base.node.mine.mds.DataPoint;
import org.knime.core.data.DataRow;

/**
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public class ManhattanDistanceManager implements DistanceManager {
    
    private boolean m_fuzzy;
    
    private boolean m_ignoreType = false;

    /**
     * Creates instance of <code>ManhattanDistanceManager</code>, which
     * computes Manhattan distances between <code>DataRow</code>s and
     * <code>SotaTreeCell</code>s. If fuzzy is set <code>true</code>, only
     * fuzzy columns are considered, if <code>false</code> only number
     * columns.
     * 
     * @param fuzzy if <code>true</code> only fuzzy data is respected, if
     *            <code>false</code> only number data
     */
    public ManhattanDistanceManager(final boolean fuzzy) {
        m_fuzzy = fuzzy;
    }
    
    /**
     * Creates instance of <code>ManhattanDistanceManager</code>, which 
     * computes the manhattan distances between rows and cells. The type 
     * (fuzzy or number) will be ignored. When dealing with fuzzy values the 
     * center of gravity is used, otherwise the numerical value.
     */
    public ManhattanDistanceManager() {
        m_fuzzy = false;
        m_ignoreType = true;
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDistance(final DataRow row1, final DataRow row2) {
        if (m_ignoreType) {
            return Distances.getManhattanDistance(row1, row2);
        }
        return Distances.getManhattanDistance(row1, row2, m_fuzzy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDistance(final DataPoint point1, final DataPoint point2) {
        return Distances.getManhattanDistance(point1, point2);
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return DistanceManagerFactory.MANHATTAN_DIST;
    }
        
    /**
     * @return the ignoreType
     */
    public boolean getIgnoreType() {
        return m_ignoreType;
    }

    /**
     * @param ignoreType the ignoreType to set
     */
    public void setIgnoreType(final boolean ignoreType) {
        m_ignoreType = ignoreType;
    }    
}
