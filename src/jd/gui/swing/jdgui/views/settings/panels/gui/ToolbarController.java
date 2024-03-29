//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.gui.swing.jdgui.views.settings.panels.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import jd.config.ConfigContainer;
import jd.config.ConfigEntry;
import jd.config.ConfigGroup;
import jd.gui.swing.GuiRunnable;
import jd.gui.swing.jdgui.GUIUtils;
import jd.gui.swing.jdgui.actions.ActionController;
import jd.gui.swing.jdgui.actions.CustomToolbarAction;
import jd.gui.swing.jdgui.actions.ToolBarAction;
import jd.gui.swing.jdgui.actions.ToolBarAction.Types;
import jd.gui.swing.jdgui.components.toolbar.MainToolBar;
import jd.gui.swing.jdgui.components.toolbar.ToolBar;
import jd.gui.swing.jdgui.views.settings.ConfigPanel;
import jd.utils.locale.JDL;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.JRendererLabel;

public class ToolbarController extends ConfigPanel {
    private static final ArrayList<String> WHITELIST        = new ArrayList<String>();
    static {
        // controls
        WHITELIST.add("toolbar.control.start");
        WHITELIST.add("toolbar.control.pause");
        WHITELIST.add("toolbar.control.stop");

        // Awesomebar at the beginning (because of the fadeout)
        WHITELIST.add("separator");
        WHITELIST.add("addons.awesomebar");

        // move
        WHITELIST.add("separator");
        WHITELIST.add("action.downloadview.movetotop");
        WHITELIST.add("action.downloadview.moveup");
        WHITELIST.add("action.downloadview.movedown");
        WHITELIST.add("action.downloadview.movetobottom");

        // config & log
        WHITELIST.add("separator");
        WHITELIST.add("action.settings");
        WHITELIST.add("action.log");

        // quickconfig
        WHITELIST.add("separator");
        WHITELIST.add("toolbar.quickconfig.clipboardoberserver");
        WHITELIST.add("toolbar.quickconfig.reconnecttoggle");
        WHITELIST.add("toolbar.control.stopmark");
        WHITELIST.add("premiumMenu.toggle");

        // addons
        WHITELIST.add("separator");
        WHITELIST.add("addonsMenu.configuration");
        WHITELIST.add("scheduler");
        WHITELIST.add("langfileditor");
        WHITELIST.add("chat");
        WHITELIST.add("livescripter");
        WHITELIST.add("infobar");
        WHITELIST.add("feedme");
        WHITELIST.add("gui.jdshutdown.toggle");
        WHITELIST.add("optional.jdunrar.menu.extract.singlefils");
        WHITELIST.add("packagecustomizer");
        WHITELIST.add("routereditor");

        // removes
        WHITELIST.add("separator");
        WHITELIST.add("action.remove.links");
        WHITELIST.add("action.remove.packages");
        WHITELIST.add("action.remove_dupes");
        WHITELIST.add("action.remove_disabled");
        WHITELIST.add("action.remove_offline");
        WHITELIST.add("action.remove_failed");

        // actions
        WHITELIST.add("separator");
        WHITELIST.add("action.addurl");
        WHITELIST.add("action.load");

        WHITELIST.add("toolbar.interaction.reconnect");
        WHITELIST.add("toolbar.interaction.update");

        WHITELIST.add("action.opendlfolder");
        WHITELIST.add("action.restore");
        WHITELIST.add("premiumMenu.configuration");
        WHITELIST.add("action.passwordlist");
        WHITELIST.add("action.premiumview.addacc");
        WHITELIST.add("action.premium.buy");

        WHITELIST.add("action.about");
        WHITELIST.add("action.help");
        WHITELIST.add("action.changes");
        WHITELIST.add("action.restart");
        WHITELIST.add("action.exit");
    }
    private static final long              serialVersionUID = -7024581410075950497L;
    private static final String            JDL_PREFIX       = "jd.gui.swing.jdgui.settings.panels.gui.ToolbarController.";

    public static String getTitle() {
        return JDL.L(JDL_PREFIX + "toolbarController.title", "Toolbar Manager");
    }

    public static String getIconKey() {
        return "gui.images.toolbar";
    }

    private JXTable                  table;

    private InternalTableModel       tableModel;

    private ArrayList<ToolBarAction> actions;

    private ArrayList<String>        list;

    public ToolbarController() {
        super();

        actions = new ArrayList<ToolBarAction>();

        init();
    }

    @Override
    public void onShow() {
        super.onShow();
        list = setActions(actions = ActionController.getActions());

        new GuiRunnable<Object>() {

            @Override
            public Object runSave() {
                tableModel.fireTableDataChanged();
                return null;
            }

        }.start();
    }

    /**
     * filters the available actions
     * 
     * @param actions2
     */
    public static ArrayList<String> setActions(ArrayList<ToolBarAction> actions2) {
        String[] originalList = MainToolBar.getInstance().getList();

        Collections.sort(actions2, new Comparator<ToolBarAction>() {
            public int compare(ToolBarAction o1, ToolBarAction o2) {
                int ia = WHITELIST.indexOf(o1.getID());
                int ib = WHITELIST.indexOf(o2.getID());
                return ia < ib ? -1 : 1;
            }
        });

        ArrayList<String> list = new ArrayList<String>(GUIUtils.getConfig().getGenericProperty("TOOLBAR", ToolBar.DEFAULT_LIST));

        boolean resortRequired = false;
        for (Iterator<ToolBarAction> it = actions2.iterator(); it.hasNext();) {
            ToolBarAction a = it.next();
            if (a.force() && !list.contains(a.getID())) {
                list.add(a.getID());
                resortRequired = true;
            }
            if (a instanceof CustomToolbarAction) continue;
            if (a.getType() == Types.SEPARATOR) {
                it.remove();
                continue;
            }
            if (a.getValue(ToolBarAction.IMAGE_KEY) == null) {
                it.remove();
                list.remove(a.getID());
                continue;
            }
            if (!WHITELIST.contains(a.getID())) {
                it.remove();
                list.remove(a.getID());
                continue;
            }
        }
        if (resortRequired) list = resort(list);

        String[] newList = list.toArray(new String[] {});
        if (!Arrays.equals(originalList, newList)) MainToolBar.getInstance().setList(newList);

        return list;
    }

    public static ArrayList<String> resort(ArrayList<String> list) {
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {
                int ia = WHITELIST.indexOf(o1);
                int ib = WHITELIST.indexOf(o2);
                return ia < ib ? -1 : 1;
            }
        });
        while (list.remove("toolbar.separator")) {
        }
        // adds separatores based on WHITELIST order
        for (int i = 1; i < list.size(); i++) {
            int b = WHITELIST.indexOf(list.get(i));
            int a = WHITELIST.indexOf(list.get(i - 1));
            if (a > 0 && b > 0) {
                for (int ii = a; ii < b; ii++) {
                    if (WHITELIST.get(ii).equals("separator")) {
                        list.add(i, "toolbar.separator");
                        i++;
                        break;
                    }
                }
            }
        }
        return list;
    }

    @Override
    protected ConfigContainer setupContainer() {
        tableModel = new InternalTableModel();
        table = new JXTable(tableModel) {
            private static final long serialVersionUID = -7914266013067863393L;

            @Override
            public TableCellRenderer getCellRenderer(int row, int col) {
                if (col == 0) return super.getCellRenderer(row, col);
                return new TableRenderer();
            }
        };
        table.setSortable(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumn column = table.getColumnModel().getColumn(0);
        column.setMinWidth(50);
        column.setPreferredWidth(50);
        column.setMaxWidth(50);

        ConfigContainer container = new ConfigContainer();

        container.setGroup(new ConfigGroup(getTitle(), getIconKey()));
        container.addEntry(new ConfigEntry(ConfigContainer.TYPE_COMPONENT, new JScrollPane(table), "growy, pushy"));

        return container;
    }

    private class InternalTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1155282457354673850L;

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Boolean.class;
            return ToolBarAction.class;
        }

        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return JDL.L(JDL_PREFIX + "column.use", "Use");
            case 1:
                return JDL.L(JDL_PREFIX + "column.name", "Name");
            case 2:
                return JDL.L(JDL_PREFIX + "column.desc", "Description");
            case 3:
                return JDL.L(JDL_PREFIX + "column.hotkey", "Hotkey");
            }
            return super.getColumnName(column);
        }

        public int getRowCount() {
            return actions.size();
        }

        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (columnIndex == 0) {
                if (actions.get(rowIndex).force()) return true;
                return list.contains(actions.get(rowIndex).getID());
            }
            return actions.get(rowIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0 && !actions.get(rowIndex).force();
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 0) {
                if ((Boolean) value) {
                    if (!list.contains(actions.get(row).getID())) list.add(actions.get(row).getID());
                } else {
                    while (list.remove(actions.get(row).getID())) {
                    }
                }
                GUIUtils.getConfig().setProperty("TOOLBAR", list);
                GUIUtils.getConfig().save();
                list = resort(list);
                MainToolBar.getInstance().setList(list.toArray(new String[] {}));
            }
        }
    }

    private class TableRenderer extends DefaultTableRenderer {

        private static final long serialVersionUID = 1L;

        private JRendererLabel    label;

        public TableRenderer() {
            label = new JRendererLabel();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ToolBarAction action = (ToolBarAction) value;
            switch (column) {
            case 1:
                label.setIcon(action.getIcon());
                label.setText(action.getTitle());
                return label;
            case 2:
                label.setIcon(null);
                label.setText(action.getTooltipText());
                return label;
            case 3:
                label.setIcon(null);
                label.setText(action.getShortCutString());
                return label;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }
}
