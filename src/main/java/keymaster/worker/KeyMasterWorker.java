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

  public void setAutoHoldEnabled(boolean autoHoldEnabled) {
    this.autoHoldEnabled = autoHoldEnabled;
  }

  public void setHoldKey(int holdKey) {
    this.holdKey = holdKey;
  }

  public void run() {
    try {

      while (!Thread.currentThread().isInterrupted()) {
        Thread.sleep(SLEEP);

        try {
          GameState gs = ServiceLocator.getInstance().getService(GameStateService.class).getGameState();

          if (gs.getAddonFound() && gs.getAddonCompatible()) {


            if (gs.getAttack() != null && gs.getAttack() > 0) {
              // Attack mode is ON - hold the '=' key, re-asserting every 250ms to recover from interruptions
              long currentTime = System.currentTimeMillis();
              if (currentTime - lastCheckTime >= HOLD_CHECK_INTERVAL_MS) {
                RobotService robotService = ServiceLocator.getInstance().getService(RobotService.class);
                // Release first, then press again to recover from any interruptions
                robotService.releaseKey(holdKey);
                robotService.pressKey(holdKey);
                isHoldingKey = true;
                lastCheckTime = currentTime;
              }
            } else {
              // Attack mode is OFF - release the key if holding
              if (isHoldingKey) {
                RobotService robotService = ServiceLocator.getInstance().getService(RobotService.class);
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
