package keymaster.service;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.List;

public interface RobotService {

  public void pressAndReleaseKey(Integer key);

  public void pressAndReleaseKeys(List<Integer> keys);

  public void pressKey(Integer key);

  public void releaseKey(Integer key);

  public BufferedImage createScreenCapture(Rectangle rectangle);

  public class RobotServiceImpl implements RobotService {

    private Robot robot;

    public RobotServiceImpl() {
      try {
        this.robot = new Robot();
      } catch (AWTException e) {
        throw new RuntimeException(e);
      }
    }

    public void pressAndReleaseKey(Integer key) {
      robot.keyPress(key);
      robot.keyRelease(key);
    }

    public void pressAndReleaseKeys(List<Integer> keys) {
      for (Integer key : keys) {
        robot.keyPress(key);
      }

      for (int i = keys.size() - 1; i >= 0; i--) {
        robot.keyRelease(keys.get(i));
      }
    }

    public BufferedImage createScreenCapture(Rectangle rectangle) {
      return robot.createScreenCapture(rectangle);
    }

    public void pressKey(Integer key) {
      robot.keyPress(key);
    }

    public void releaseKey(Integer key) {
      robot.keyRelease(key);
    }
  }
}
