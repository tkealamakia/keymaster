package keymaster.ui;

import net.sf.ehcache.CacheManager;
import keymaster.model.GameState;
import keymaster.service.GameStateService;
import keymaster.service.ServiceLocator;
import keymaster.worker.KeyMasterWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KeyMaster extends JFrame {

  private static final long serialVersionUID = 1L;

  private ExecutorService executorService;

  private JButton startButton;
  private JButton stopButton;
  private JTextField autoHoldKeyField;
  private JCheckBox spamModeCheckbox;
  private JTextField spamIntervalField;
  private JTextArea consoleOutput;

  public KeyMaster() {
    initializeUI();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        CacheManager.getInstance().shutdown();
      }
    });
  }

  private void initializeUI() {
    setTitle("Key Master");
    setSize(400, 250);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    JPanel mainPanel = new JPanel();
    getContentPane().add(mainPanel);

    mainPanel.setLayout(new GridBagLayout());

    GridBagConstraints gc = new GridBagConstraints();
    gc.insets = new Insets(1, 1, 1, 1);
    gc.gridx = GridBagConstraints.RELATIVE;
    gc.gridy = GridBagConstraints.RELATIVE;
    gc.anchor = GridBagConstraints.CENTER;
    gc.ipadx = 0;
    gc.ipady = 0;
    gc.fill = GridBagConstraints.BOTH;

    JPanel controlPanel = new JPanel();

    startButton = new JButton("Start");
    startButton.setEnabled(true);
    startButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        if (executorService == null) {
          GameState gameState = ServiceLocator.getInstance().getService(GameStateService.class).getGameState();
          if (gameState == null || !gameState.getAddonFound()) {
            System.out.println("Addon not found.");
          }
          else if (!gameState.getAddonCompatible()) {
            System.out.println("Incompatible version of the addon");
          }
          else {
            startButton.setEnabled(false);
            autoHoldKeyField.setEnabled(false);
            spamModeCheckbox.setEnabled(false);
            spamIntervalField.setEnabled(false);
            executorService = Executors.newSingleThreadExecutor();
            KeyMasterWorker keyMasterWorker = new KeyMasterWorker();

            // Parse the key from the text field
            String keyText = autoHoldKeyField.getText();
            if (keyText != null && !keyText.isEmpty()) {
              char keyChar = keyText.charAt(0);
              int keyCode = java.awt.event.KeyEvent.getExtendedKeyCodeForChar(keyChar);
              keyMasterWorker.setHoldKey(keyCode);
            }

            // Set spam mode and interval
            keyMasterWorker.setSpamMode(spamModeCheckbox.isSelected());
            try {
              int interval = Integer.parseInt(spamIntervalField.getText());
              keyMasterWorker.setSpamInterval(interval);
            } catch (NumberFormatException e) {
              System.out.println("Invalid interval value, using default 300ms");
              keyMasterWorker.setSpamInterval(300);
            }

            executorService.execute(keyMasterWorker);
            stopButton.setEnabled(true);
          }
        }
      }
    });
    controlPanel.add(startButton);

    stopButton = new JButton("Stop");
    stopButton.setEnabled(false);
    stopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        stopButton.setEnabled(false);
        ServiceLocator.getInstance().getService(GameStateService.class).resetAddonForNextSelectedRotation();
        executorService.shutdownNow();
        try {
          executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

        }
        executorService = null;
        startButton.setEnabled(true);
        autoHoldKeyField.setEnabled(true);
        spamModeCheckbox.setEnabled(true);
        spamIntervalField.setEnabled(spamModeCheckbox.isSelected());
      }
    });
    controlPanel.add(stopButton);

    autoHoldKeyField = new JTextField("=", 2);
    controlPanel.add(autoHoldKeyField);

    gc.weightx = 0.0;
    gc.weighty = 0.0;
    gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.gridheight = 1;
    mainPanel.add(controlPanel, gc);

    JPanel spamPanel = new JPanel();
    spamModeCheckbox = new JCheckBox("Spam Mode");
    spamModeCheckbox.setSelected(true);
    spamModeCheckbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        spamIntervalField.setEnabled(spamModeCheckbox.isSelected());
      }
    });
    spamPanel.add(spamModeCheckbox);

    JLabel intervalLabel = new JLabel("Interval ms:");
    spamPanel.add(intervalLabel);
    spamIntervalField = new JTextField("300", 5);
    spamIntervalField.setEnabled(true);
    spamPanel.add(spamIntervalField);

    gc.weightx = 0.0;
    gc.weighty = 0.0;
    gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.gridheight = 1;
    mainPanel.add(spamPanel, gc);

    JPanel consolePanel = new JPanel();
    consolePanel.setLayout(new BorderLayout());
    JLabel consoleLabel = new JLabel("Console Output:");
    consolePanel.add(consoleLabel, BorderLayout.NORTH);

    consoleOutput = new JTextArea(3, 30);
    consoleOutput.setEditable(false);
    consoleOutput.setLineWrap(true);
    consoleOutput.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(consoleOutput);
    consolePanel.add(scrollPane, BorderLayout.CENTER);

    gc.weightx = 1.0;
    gc.weighty = 1.0;
    gc.gridwidth = GridBagConstraints.REMAINDER;
    gc.gridheight = 1;
    mainPanel.add(consolePanel, gc);

    // Redirect System.out to the text area
    PrintStream printStream = new PrintStream(new OutputStream() {
      @Override
      public void write(int b) {
        consoleOutput.append(String.valueOf((char) b));
        consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
      }

      @Override
      public void write(byte[] b, int off, int len) {
        consoleOutput.append(new String(b, off, len));
        consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
      }
    });
    System.setOut(printStream);
    System.setErr(printStream);
  }

  public static void main(String args[]) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        KeyMaster keyMasterFrame = new KeyMaster();
        keyMasterFrame.setVisible(true);
      }
    });
  }

}
