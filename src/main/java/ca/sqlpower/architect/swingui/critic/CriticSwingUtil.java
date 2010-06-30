/*
 * Copyright (c) 2010, SQL Power Group Inc.
 *
 * This file is part of SQL Power Architect.
 *
 * SQL Power Architect is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * SQL Power Architect is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package ca.sqlpower.architect.swingui.critic;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import ca.sqlpower.architect.ddl.critic.CriticismBucket;
import ca.sqlpower.architect.ddl.critic.QuickFix;
import ca.sqlpower.architect.ddl.critic.CriticAndSettings.Severity;
import ca.sqlpower.architect.swingui.ArchitectSwingSession;
import ca.sqlpower.swingui.SPSUtils;

public class CriticSwingUtil {
    
    /**
     * Error icon to go along with criticisms that are flagged to be errors.
     */
    public static final ImageIcon ERROR_ICON = SPSUtils.createIcon("error", "error badge");
    
    /**
     * Warning icon to go along with criticisms that are flagged to be warnings.
     */
    public static final ImageIcon WARNING_ICON = SPSUtils.createIcon("warning", "warning badge");

    private CriticSwingUtil() {
        //utility class
    }

    /**
     * Returns a table that displays all of the critics in the system including
     * letting users be able to apply quick fixes to criticisms.
     * 
     * @param session
     *            The session that contains the critic manager and its settings.
     * @param bucket
     *            The bucket that stores the critics in the system. As this
     *            bucket is updated the table model will update with it.
     */
    public static JTable createCriticTable(ArchitectSwingSession session, CriticismBucket bucket) {
        final CriticismTableModel tableModel = new CriticismTableModel(session, bucket);
        final JTable errorTable = new JTable(tableModel);
        errorTable.setDefaultRenderer(Severity.class, new SeverityTableCellRenderer());
        final QuickFixListCellRenderer renderer = new QuickFixListCellRenderer();
        errorTable.setDefaultRenderer(List.class, renderer);
        errorTable.addMouseListener(new MouseListener() {
            
            public void mouseReleased(MouseEvent e) {
                final Point point = e.getPoint();
                int row = errorTable.rowAtPoint(point);
                int col = errorTable.columnAtPoint(point);
                Object clickedVal = tableModel.getValueAt(row, col);
                if (clickedVal instanceof List<?>) {
                    List<?> list = (List<?>) clickedVal;
                    final JPopupMenu menu = new JPopupMenu();
                    for (Object o : list) {
                        final QuickFix fix = (QuickFix) o;
                        menu.add(new AbstractAction(fix.getDescription()) {
                            public void actionPerformed(ActionEvent e) {
                                fix.apply();
                            }
                        });
                    }
                    menu.show(errorTable, point.x, point.y);
                }
            }
            
            public void mousePressed(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseClicked(MouseEvent e) {}
        });
        return errorTable;
    }
}