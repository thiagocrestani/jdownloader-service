package jd.controlling.reconnect.plugins.batch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;

import jd.config.SubConfiguration;
import jd.controlling.reconnect.ReconnectException;
import jd.controlling.reconnect.RouterPlugin;
import jd.gui.UserIO;
import jd.gui.swing.components.ComboBrowseFile;
import jd.utils.JDUtilities;
import jd.utils.locale.JDL;
import net.miginfocom.swing.MigLayout;

import org.appwork.utils.os.CrossSystem;
import org.appwork.utils.swing.EDTRunner;
import org.appwork.utils.swing.TextComponentChangeListener;

/**
 * Plugin to use an extern tool for reconnection
 */
public class ExternBatchReconnectPlugin extends RouterPlugin implements ActionListener {

    private static final String BATCH_TEXT              = "BATCH_COMMAND";

    private static final String TERMINAL_COMMAND        = "TERMINAL";

    private static final String EXECUTE_IN              = "EXECUTE_IN";

    private static final String WAIT_FOR_RETURN_SECONDS = "WAIT_FOR_RETURN_SECONDS";

    public static final String  ID                      = "ExternBatchReconnect";

    private JTextField          txtCommand;

    private ComboBrowseFile     browse;

    private JTextPane           txtBatch;

    public ExternBatchReconnectPlugin() {
        super();
    }

    public void actionPerformed(final ActionEvent e) {
        this.setExecuteIn(this.browse.getText());
    }

    private String getBatchText() {
        return this.getStorage().get(ExternBatchReconnectPlugin.BATCH_TEXT, SubConfiguration.getConfig("BATCHRECONNECT").getStringProperty("BATCH_TEXT", ""));
    }

    private String getExecuteIn() {
        return this.getStorage().get(ExternBatchReconnectPlugin.EXECUTE_IN, SubConfiguration.getConfig("BATCHRECONNECT").getStringProperty("RECONNECT_EXECUTE_FOLDER", ""));
    }

    @Override
    public JComponent getGUI() {

        final JPanel p = new JPanel(new MigLayout("ins 15,wrap 2", "[][grow,fill]", "[][][grow,fill][]"));
        this.txtCommand = new JTextField();

        this.txtBatch = new JTextPane();
        this.browse = new ComboBrowseFile(this.getID());
        this.browse.setEditable(true);
        this.browse.addActionListener(this);
        this.browse.setFileSelectionMode(UserIO.DIRECTORIES_ONLY);
        this.browse.setDialogType(JFileChooser.SAVE_DIALOG);
        p.add(new JLabel(JDL.L("interaction.batchreconnect.terminal", "Interpreter")), "sg left");
        p.add(this.txtCommand);

        p.add(new JLabel(JDL.L("interaction.batchreconnect.batch", "Batch Script")), "newline,spanx,sg left");
        p.add(new JScrollPane(this.txtBatch), "spanx,newline,pushx,growx");

        p.add(new JLabel(JDL.L("interaction.batchreconnect.executein", "Start in (application folder)")), "sg left");
        p.add(this.browse);
        new TextComponentChangeListener(this.txtCommand) {
            @Override
            protected void onChanged(final DocumentEvent e) {

                ExternBatchReconnectPlugin.this.setCommand(ExternBatchReconnectPlugin.this.txtCommand.getText());

            }

        };
        new TextComponentChangeListener(this.txtBatch) {
            @Override
            protected void onChanged(final DocumentEvent e) {

                ExternBatchReconnectPlugin.this.setBatchText(ExternBatchReconnectPlugin.this.txtBatch.getText());

            }
        };
        this.updateGUI();
        return p;
    }

    @Override
    public String getID() {
        return ExternBatchReconnectPlugin.ID;
    }

    @Override
    public String getName() {
        return JDL.L("jd.controlling.reconnect.plugins.batch.ExternBatchReconnectPlugin.getName", "External Batch Reconnect");
    }

    private String getTerminalCommand() {
        if (CrossSystem.isWindows()) {
            return this.getStorage().get(ExternBatchReconnectPlugin.TERMINAL_COMMAND, SubConfiguration.getConfig("BATCHRECONNECT").getStringProperty("TERMINAL", "cmd /c"));
        } else {
            return this.getStorage().get(ExternBatchReconnectPlugin.TERMINAL_COMMAND, SubConfiguration.getConfig("BATCHRECONNECT").getStringProperty("TERMINAL", "/bin/bash"));
        }
    }

    /**
     * returns how long the execution should wait for the extern process to
     * return
     * 
     * @return
     */
    private int getWaitForReturn() {

        return this.getStorage().get(ExternBatchReconnectPlugin.WAIT_FOR_RETURN_SECONDS, JDUtilities.getConfiguration().getIntegerProperty("WAIT_FOR_RETURN5", 0));

    }

    @Override
    public boolean isReconnectionEnabled() {
        return true;
    }

    @Override
    protected void performReconnect() throws ReconnectException {
        final int waitForReturn = this.getWaitForReturn();
        final String executeIn = this.getExecuteIn();

        String command = this.getTerminalCommand();
        if (command != null) {
            final String[] cmds = command.split("\\ ");
            final int cmdsLength1 = cmds.length - 1;
            command = cmds[0];
            for (int i = 0; i < cmdsLength1; i++) {
                cmds[i] = cmds[i + 1];
            }

            final String batch = this.getBatchText();

            final String[] lines = org.appwork.utils.Regex.getLines(batch);
            RouterPlugin.LOG.info("Using Batch-Mode: using " + command + " as interpreter! (default: windows(cmd.exe) linux&mac(/bin/bash) )");
            for (final String element : lines) {
                cmds[cmdsLength1] = element;
                /*
                 * if we have multiple lines, wait for each line to finish until
                 * starting the next one
                 */
                RouterPlugin.LOG.finer("Execute Batchline: " + JDUtilities.runCommand(command, cmds, executeIn, lines.length >= 2 ? waitForReturn : -1));
            }
        }
    }

    private void setBatchText(final String text) {
        this.getStorage().put(ExternBatchReconnectPlugin.BATCH_TEXT, text);
        this.updateGUI();
    }

    private void setCommand(final String text) {
        this.getStorage().put(ExternBatchReconnectPlugin.TERMINAL_COMMAND, text);
        this.updateGUI();
    }

    private void setExecuteIn(final String text) {
        this.getStorage().put(ExternBatchReconnectPlugin.EXECUTE_IN, text);
        this.updateGUI();
    }

    private void updateGUI() {
        new EDTRunner() {
            protected void runInEDT() {
                try {
                    ExternBatchReconnectPlugin.this.txtCommand.setText(ExternBatchReconnectPlugin.this.getTerminalCommand());
                } catch (final IllegalStateException e) {
                    // throws an java.lang.IllegalStateException if the caller
                    // is a changelistener of this field's document
                }
                try {
                    ExternBatchReconnectPlugin.this.txtBatch.setText(ExternBatchReconnectPlugin.this.getBatchText());
                } catch (final IllegalStateException e) {
                    // throws an java.lang.IllegalStateException if the caller
                    // is a changelistener of this field's document
                }
                try {
                    ExternBatchReconnectPlugin.this.browse.setText(ExternBatchReconnectPlugin.this.getExecuteIn());
                } catch (final IllegalStateException e) {
                    // throws an java.lang.IllegalStateException if the caller
                    // is a changelistener of this field's document
                }
            }

        };

    }

}
