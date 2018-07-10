/*
 * ------------------------------------------------------------------------
 *
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
 *   Jul 10, 2018 (hornm): created
 */
package org.knime.workbench.ui.async;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.knime.core.node.NodeLogger;
import org.knime.core.ui.node.workflow.NodeContainerUI;
import org.knime.core.ui.node.workflow.WorkflowManagerUI;
import org.knime.core.ui.node.workflow.async.AsyncNodeContainerUI;
import org.knime.core.ui.node.workflow.async.AsyncWorkflowManagerUI;

/**
 * TODO!!!
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class AsyncSwitch {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(AsyncSwitch.class);

    private AsyncSwitch() {
        //utility class
    }

    /**
     * TODO
     *
     * @param syncWfm
     * @param asyncWfm
     * @param wfm
     * @param waitingMessage
     * @return
     */
    public static <T> T wfmAsyncSwitch(final Function<WorkflowManagerUI, T> syncWfm,
        final Function<AsyncWorkflowManagerUI, Future<T>> asyncWfm, final WorkflowManagerUI wfm,
        final String waitingMessage) {
        if (wfm instanceof AsyncWorkflowManagerUI) {
            AtomicReference<T> ref = new AtomicReference<T>();
            try {
                PlatformUI.getWorkbench().getProgressService().busyCursorWhile((monitor) -> {
                    monitor.beginTask(waitingMessage, 100);
                    try {
                        ref.set(asyncWfm.apply((AsyncWorkflowManagerUI)wfm).get());
                    } catch (ExecutionException e) {
                        openDialogAndLog(e, waitingMessage);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                openDialogAndLog(ex, waitingMessage);
            }
            return ref.get();
        } else {
            return syncWfm.apply(wfm);
        }
    }

    public static void wfmAsyncSwitchVoid(final Consumer<WorkflowManagerUI> syncWfm,
        final Function<AsyncWorkflowManagerUI, Future<Void>> asyncWfm, final WorkflowManagerUI wfm,
        final String waitingMessage) {
        if (wfm instanceof AsyncWorkflowManagerUI) {
            try {
                PlatformUI.getWorkbench().getProgressService().busyCursorWhile((monitor) -> {
                    monitor.beginTask(waitingMessage, 100);
                    try {
                        asyncWfm.apply((AsyncWorkflowManagerUI)wfm).get();
                    } catch (ExecutionException e) {
                        //TODO!!!
                        throw new RuntimeException(e);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                openDialogAndLog(ex, waitingMessage);
            }
        } else {
            syncWfm.accept(wfm);
        }
    }

    /**
     * TODO
     *
     * @param syncNc
     * @param asyncNc
     * @param nc
     * @param waitingMessage
     * @param exceptionClass the class of the exception to re-throw
     * @return
     */
    public static <T, E extends Exception> T ncAsyncSwitchRethrow(final RethrowFunction<NodeContainerUI, T, E> syncNc,
        final RethrowFunction<AsyncNodeContainerUI, Future<T>, E> asyncNc, final NodeContainerUI nc, final String waitingMessage,
        final Class<E> exceptionClass) throws E {
        if (nc instanceof AsyncNodeContainerUI) {
            AtomicReference<T> ref = new AtomicReference<T>();
            AtomicReference<E> exception = new AtomicReference<E>();
            try {
                PlatformUI.getWorkbench().getProgressService().busyCursorWhile((monitor) -> {
                    monitor.beginTask(waitingMessage, 100);
                    try {
                        ref.set(asyncNc.apply((AsyncNodeContainerUI)nc).get());
                    } catch (ExecutionException ex) {
                        openDialogAndLog(ex, waitingMessage);
                    } catch (Exception ex) {
                        if (exceptionClass.isAssignableFrom(ex.getClass())) {
                            exception.set((E)ex);
                        } else {
                            openDialogAndLog(ex, waitingMessage);
                        }
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                openDialogAndLog(ex, waitingMessage);
            }
            if (exception.get() != null) {
                throw exception.get();
            }
            return ref.get();
        } else {
            return syncNc.apply(nc);
        }
    }

    @FunctionalInterface
    public static interface RethrowFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    private static void openDialogAndLog(final Exception e, final String waitingMessage) {
        String message = "An unexpected problem occurred while '" + waitingMessage + "': " + e.getMessage();
        final Shell shell = Display.getCurrent().getActiveShell();
        MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
        mb.setText("Unexpected Problem");
        mb.setMessage(message + "\nSee log for details.");
        mb.open();
        LOGGER.error(message, e);
    }
}
