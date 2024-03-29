package jd.gui.swing.jdgui.views.settings.sidebar;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;

import jd.gui.swing.jdgui.SingletonPanel;
import jd.gui.swing.jdgui.interfaces.SwitchPanel;
import jd.gui.swing.jdgui.views.settings.ConfigPanel;
import jd.utils.JDTheme;

public class TreeEntry {

    private static final HashMap<Class<? extends ConfigPanel>, TreeEntry> PANELS = new HashMap<Class<? extends ConfigPanel>, TreeEntry>();

    /**
     * Returns the TreeEntry to a class if it has been added using the
     * {@link TreeEntry#TreeEntry(Class, String)} constructor
     * 
     * @param clazz
     * @return
     */
    public static TreeEntry getTreeByClass(final Class<?> clazz) {
        return TreeEntry.PANELS.get(clazz);
    }

    private Class<? extends ConfigPanel> clazz;
    private SwitchPanel                  panel;

    private String                       title;
    private ImageIcon                    iconSmall;
    private ImageIcon                    icon;
    private final ArrayList<TreeEntry>   entries;

    private TreeEntry() {
        this.entries = new ArrayList<TreeEntry>();
    }

    /**
     * Creates a new TreeEntry. The title and the iconKey will be obtained via
     * the <code>public static String</code> methods <code>getTitle()</code> and
     * <code>getIconKey()</code>.
     * 
     * @param clazz
     */
    public TreeEntry(final Class<? extends ConfigPanel> clazz) {
        this();

        try {
            this.setTitle(clazz.getMethod("getTitle").invoke(null).toString());
        } catch (final Exception e) {
            e.printStackTrace();
            this.setTitle(clazz.getSimpleName());
        }

        try {
            this.setIcon(clazz.getMethod("getIconKey").invoke(null).toString());
        } catch (final Exception e) {
            e.printStackTrace();
            this.setIcon("gui.images.taskpanes.configuration");
        }

        TreeEntry.PANELS.put(clazz, this);

        this.clazz = clazz;
    }

    public TreeEntry(final String title, final String iconKey) {
        this();
        this.setTitle(title);
        this.setIcon(iconKey);
    }

    /**
     * Adds a configpanel
     * 
     * @param panel
     * @param title
     * @param iconKey
     */
    public TreeEntry(final SwitchPanel panel, final String title, final String iconKey) {
        this(title, iconKey);

        this.panel = panel;
    }

    public void add(final TreeEntry treeEntry) {
        this.entries.add(treeEntry);
    }

    public Object get(final int index) {
        return this.entries.get(index);
    }

    public Class<? extends ConfigPanel> getClazz() {
        return this.clazz;
    }

    public ArrayList<TreeEntry> getEntries() {
        return this.entries;
    }

    public ImageIcon getIcon() {
        return this.icon;
    }

    public ImageIcon getIconSmall() {
        return this.iconSmall;
    }

    public SwitchPanel getPanel() {
        if (this.panel == null) {
            if (this.clazz != null) {
                this.panel = new SingletonPanel(this.clazz).getPanel();
            } else if (!this.entries.isEmpty()) {
                this.panel = this.entries.get(0).getPanel();
            }
        }
        return this.panel;
    }

    public String getTitle() {
        return this.title;
    }

    public int indexOf(final Object child) {
        return this.entries.indexOf(child);
    }

    private void setIcon(final String iconKey) {
        if (iconKey != null) {
            this.iconSmall = JDTheme.II(iconKey, 16, 16);
            this.icon = JDTheme.II(iconKey, 20, 20);
        }
    }

    private void setTitle(final String title) {
        this.title = title;
    }

    public int size() {
        return this.entries.size();
    }

}
