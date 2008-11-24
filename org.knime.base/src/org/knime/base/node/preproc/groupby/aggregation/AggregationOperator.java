/*
 * -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2008
 * University of Konstanz, Germany
 * Chair for Bioinformatics and Information Mining (Prof. M. Berthold)
 * and KNIME GmbH, Konstanz, Germany
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * -------------------------------------------------------------------
 *
 * History
 *    29.06.2007 (Tobias Koetter): created
 */

package org.knime.base.node.preproc.groupby.aggregation;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataType;

import org.knime.base.node.preproc.groupby.GroupByTable;


/**
 * Abstract class which has to be extended by all aggregation method operators
 * in the {@link AggregationMethod} enumeration to be used in
 * the {@link GroupByTable} class.
 *
 * @author Tobias Koetter, University of Konstanz
 */
public abstract class AggregationOperator {

    private final int m_maxUniqueValues;

    private boolean m_skipped;

    private final String m_label;
    private final String m_shortLabel;
    private final boolean m_numerical;
    private final boolean m_usesLimit;
    private final boolean m_keepColSpec;

    /**The String to use by concatenation operators.*/
    public static final String CONCATENATOR = ", ";

    /**Constructor for class AggregationOperator.
     * @param label user readable label which is also used for the column name
     * @param numerical <code>true</code> if the operator is only suitable
     * for numerical columns
     * @param usesLimit <code>true</code> if the method checks the number of
     * unique values limit.
     * @param keepColSpec <code>true</code> if the original column specification
     * should be kept if possible
     * @param maxUniqueValues the maximum number of unique values
     */
    public AggregationOperator(final String label, final boolean numerical,
            final boolean usesLimit, final boolean keepColSpec,
            final int maxUniqueValues) {
        this(label, label, numerical, usesLimit, keepColSpec, maxUniqueValues);
    }

    /**Constructor for class AggregationOperator.
     * @param label user readable label
     * @param shortLabel the short label used for the column name
     * @param numerical <code>true</code> if the operator is only suitable
     * for numerical columns
     * @param usesLimit <code>true</code> if the method checks the number of
     * unique values limit.
     * @param keepColSpec <code>true</code> if the original column specification
     * should be kept if possible
     * @param maxUniqueValues the maximum number of unique values
     */
    public AggregationOperator(final String label, final String shortLabel,
            final boolean numerical, final boolean usesLimit,
            final boolean keepColSpec, final int maxUniqueValues) {
        m_label = label;
        m_shortLabel = shortLabel;
        m_numerical = numerical;
        m_usesLimit = usesLimit;
        m_keepColSpec = keepColSpec;
        m_maxUniqueValues = maxUniqueValues;
    }

    /**
     * Creates a new instance of this operator.
     *
     * @param origColSpec the {@link DataColumnSpec} of the original column
     * @param maxUniqueValues the maximum number of unique values
     * @return a new instance of this operator
     */
    public abstract AggregationOperator createInstance(
            DataColumnSpec origColSpec, final int maxUniqueValues);

    /**
     * @return the maxUniqueValues
     */
    public int getMaxUniqueValues() {
        return m_maxUniqueValues;
    }

    /**
     * @return <code>true</code> if this operator was skipped
     */
    public boolean isSkipped() {
        return m_skipped;
    }

    /**
     * @param cell the {@link DataCell} to consider during computing
     */
    public void compute(final DataCell cell) {
        if (m_skipped) {
            return;
        }
        m_skipped = computeInternal(cell);
    }

    /**
     * @param cell the {@link DataCell} to consider during computing the cell
     * can't be <code>null</code>.
     * @return <code>true</code> if this column should be skipped in further
     * calculations
     */
    protected abstract boolean computeInternal(final DataCell cell);

    /**
     * @param colName the name of the new column
     * @param origSpec the original {@link DataColumnSpec}
     * @return the new {@link DataColumnSpecCreator} for the aggregated column
     */
    public DataColumnSpec createColumnSpec(final String colName,
            final DataColumnSpec origSpec) {
        if (origSpec == null) {
            throw new NullPointerException(
                    "Original column spec must not be null");
        }
        final DataType newType = getDataType(origSpec.getType());
        final DataColumnSpecCreator specCreator;
        if (m_keepColSpec && (newType == null
                || origSpec.getType().equals(newType))) {
             specCreator = new DataColumnSpecCreator(origSpec);
        } else {
            final DataType type;
            if (newType == null) {
                type = origSpec.getType();
            } else {
                type = newType;
            }
            specCreator = new DataColumnSpecCreator(colName, type);
        }
        specCreator.setName(colName);
        return specCreator.createSpec();
    }

    /**
     * @param origType the {@link DataType} of the original column to aggregate
     * @return the {@link DataType} of the aggregation result
     */
    protected abstract DataType getDataType(final DataType origType);

    /**
     * @return the result {@link DataCell}
     */
    public DataCell getResult() {
        if (m_skipped) {
            return DataType.getMissingCell();
        }
        return getResultInternal();
    }

    /**
     * @return the result {@link DataCell}
     */
    protected abstract DataCell getResultInternal();

    /**
     * Should reset the operator to the start values.
     */
    public void reset() {
        m_skipped = false;
        resetInternal();
    }

    /**
     * Should reset the operator to the start values.
     */
    protected abstract void resetInternal();

    /**
     * @return the label
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * @return the short label which is used in the column name
     */
    public String getShortLabel() {
        return m_shortLabel;
    }


    /**
     * @return the numerical
     */
    public boolean isNumerical() {
        return m_numerical;
    }

    /**
     * @return <code>true</code> if this method checks the maximum unique
     * values limit.
     */
    public boolean isUsesLimit() {
        return m_usesLimit;
    }
}
