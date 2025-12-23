package keymaster.worker;

import java.awt.event.KeyEvent;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import keymaster.model.GameState;
import keymaster.service.GameStateService;
import keymaster.service.RobotService;
import keymaster.service.ServiceLocator;

public class KeyMasterWorker implements Runnable {

  static Logger logger = LogManager.getLogger(KeyMasterWorker.class);

  public static final int SLEEP = 100;
  private static final int HOLD_CHECK_INTERVAL_MS = 250;

  private boolean isHoldingKey = false;
  private long lastCheckTime = 0;
  private boolean autoHoldEnabled = false;
  private int holdKey = KeyEvent.VK_EQUALS;
  private boolean spamMode = false;
  private int spamInterval = 300;

  public void setAutoHoldEnabled(boolean autoHoldEnabled) {
    this.autoHoldEnabled = autoHoldEnabled;
  }

  public void setHoldKey(int holdKey) {
    this.holdKey = holdKey;
  }

  public void setSpamMode(boolean spamMode) {
    this.spamMode = spamMode;
  }

  public void setSpamInterval(int spamInterval) {
    this.spamInterval = spamInterval;
  }

  public void run() {
    try {

      while (!Thread.currentThread().isInterrupted()) {
        Thread.sleep(SLEEP);

        try {
          GameState gs = ServiceLocator.getInstance().getService(GameStateService.class).getGameState();

          if (gs.getAddonFound() && gs.getAddonCompatible()) {


            RobotService robotService = ServiceLocator.getInstance().getService(RobotService.class);

            if (gs.getAttack() != null && gs.getAttack() > 0) {
              if (spamMode) {
                // Spam mode - continuously press and release the key at the specified interval
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCheckTime >= spamInterval) {
                  robotService.pressKey(holdKey);
                  robotService.releaseKey(holdKey);
                  lastCheckTime = currentTime;
                }
              } else {
                // Press and hold mode - press once and keep it held
                if (!isHoldingKey) {
                  robotService.pressKey(holdKey);
                  isHoldingKey = true;
                }
              }
            } else {
              // Attack mode is OFF - release the key if holding
              if (isHoldingKey) {
                robotService.releaseKey(holdKey);
                isHoldingKey = false;
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    } catch (InterruptedException e) {
      // Thread interrupted
    }
  }
}
