//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
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

package jd.plugins.optional.customizer;

import java.util.ArrayList;

import jd.plugins.optional.customizer.columns.DLPriorityColumn;
import jd.plugins.optional.customizer.columns.DownloadDirColumn;
import jd.utils.locale.JDL;

import org.appwork.utils.swing.table.ExtTableModel;
import org.appwork.utils.swing.table.columns.ExtCheckColumn;
import org.appwork.utils.swing.table.columns.ExtLongColumn;
import org.appwork.utils.swing.table.columns.ExtTextEditorColumn;

public class CustomizerTableModel extends ExtTableModel<CustomizeSetting> {

    private static final long   serialVersionUID = -8877812970684393642L;
    private static final String JDL_PREFIX       = "jd.plugins.optional.customizer.CustomizerTableModel.";

    public CustomizerTableModel() {
        super("customizer");
    }

    protected void initColumns() {
        this.addColumn(new ExtTextEditorColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "name", "Name"), this) {

            private static final long serialVersionUID = -8945184634371898061L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            protected String getStringValue(CustomizeSetting value) {
                return value.getName();
            }

            @Override
            protected void setStringValue(String value, CustomizeSetting object) {
                object.setName(value);
            }

        });
        this.addColumn(new ExtCheckColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "enabled", "Enabled"), this) {

            private static final long serialVersionUID = -755486233284215838L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            public boolean isEditable(CustomizeSetting obj) {
                return true;
            }

            @Override
            protected boolean getBooleanValue(CustomizeSetting value) {
                return value.isEnabled();
            }

            @Override
            protected void setBooleanValue(boolean value, CustomizeSetting object) {
                object.setEnabled(value);
            }

        });
        this.addColumn(new ExtTextEditorColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "regex", "Regex"), this) {

            private static final long serialVersionUID = 4211754232119068701L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            protected void setStringValue(String value, CustomizeSetting object) {
                object.setRegex(value);
            }

            @Override
            protected String getStringValue(CustomizeSetting value) {
                return value.getRegex();
            }

            @Override
            protected String getToolTip(CustomizeSetting obj) {
                if (obj.isRegexValid()) return super.getToolTip(obj);
                return JDL.LF(JDL_PREFIX + "regex.malformed", "Malformed Regex!");
            }

        });
        this.addColumn(new ExtTextEditorColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "packageName", "FilePackage name"), this) {

            private static final long serialVersionUID = 7315104566941756777L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            protected String getStringValue(CustomizeSetting value) {
                return value.getPackageName();
            }

            @Override
            protected String getToolTip(CustomizeSetting obj) {
                return JDL.L("jd.plugins.optional.customizer.columns.PackageNameColumn.toolTip", "The name of the filepackage, if the link matches the regex. Leave it empty to use the default name!");
            }

            @Override
            protected void setStringValue(String value, CustomizeSetting object) {
                object.setPackageName(value);
            }

        });
        this.addColumn(new DownloadDirColumn(JDL.L(JDL_PREFIX + "downloadDir", "Download directory"), this));
        this.addColumn(new ExtCheckColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "subDirectory", "Use SubDirectory"), this) {

            private static final long serialVersionUID = 5660615874659705475L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            public boolean isEditable(CustomizeSetting obj) {
                return true;
            }

            @Override
            protected boolean getBooleanValue(CustomizeSetting value) {
                return value.isUseSubDirectory();
            }

            @Override
            protected void setBooleanValue(boolean value, CustomizeSetting object) {
                object.setUseSubDirectory(value);
            }

        });
        this.addColumn(new ExtCheckColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "postProcessing", "Post Processing"), this) {

            private static final long serialVersionUID = -6837005675767011587L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            public boolean isEditable(CustomizeSetting obj) {
                return true;
            }

            @Override
            protected boolean getBooleanValue(CustomizeSetting value) {
                return value.isPostProcessing();
            }

            @Override
            protected void setBooleanValue(boolean value, CustomizeSetting object) {
                object.setPostProcessing(value);
            }

        });
        this.addColumn(new ExtTextEditorColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "password", "Password"), this) {

            private static final long serialVersionUID = 6345445804247730821L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            public boolean isEditable(CustomizeSetting obj) {
                return isEnabled(obj);
            }

            @Override
            protected void setStringValue(String value, CustomizeSetting object) {
                object.setPassword(value);
            }

            @Override
            protected String getStringValue(CustomizeSetting value) {
                return value.getPassword();
            }

        });
        this.addColumn(new DLPriorityColumn(JDL.L(JDL_PREFIX + "dlPriority", "Download Priority"), this));
        this.addColumn(new ExtLongColumn<CustomizeSetting>(JDL.L(JDL_PREFIX + "matchCount", "Match count from Start"), this) {

            private static final long serialVersionUID = -8673582883080206266L;

            @Override
            public boolean isEnabled(CustomizeSetting obj) {
                return obj.isEnabled();
            }

            @Override
            protected long getLong(CustomizeSetting value) {
                return value.getMatchCount();
            }

        });
    }

    protected void refreshData() {
        final ArrayList<CustomizeSetting> tmp = CustomizeSetting.getSettings();

        final ArrayList<CustomizeSetting> selection = this.getSelectedObjects();
        tableData = tmp;
        refreshSort();

        fireTableStructureChanged();

        setSelectedObjects(selection);
    }

}
