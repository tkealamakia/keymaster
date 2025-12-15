package uberpwn.service;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uberpwn.model.Coordinate;
import uberpwn.model.GameState;

public interface GameStateService {

  public GameState getGameState();

  public void resetAddonForNextSelectedRotation();

  public class GameStateServiceImpl implements GameStateService {
    static Logger logger = LogManager.getLogger(GameStateService.class);

    public static final int ADDON_VERSION = 1;

    public static final Color UBERPWN_START_MARKER = new Color(51, 102, 204);
    public static final Color UBERPWN_END_MARKER = new Color(204, 102, 51);
    public static final Color UBERPWN_GRID_MARKER1 = new Color(255, 255, 250);
    public static final Color UBERPWN_GRID_MARKER2 = new Color(255, 255, 247);

    public static final int ROWS = 3;
    public static final int COLS = 20;

    private Rectangle addonRectangle;
    private List<Integer> xPixels;
    private List<Integer> yPixels;

    public GameState getGameState() {
      GameState gameState = new GameState();

      if (addonRectangle == null) {
        findAddon();
      }

      BufferedImage image = null;
      if (addonRectangle != null) {
        image = ServiceLocator.getInstance().getService(RobotService.class).createScreenCapture(addonRectangle);
      }

      if (image != null && new Color(image.getRGB(0, 0)).equals(UBERPWN_START_MARKER)) {
        gameState.setAddonFound(true);

        // Read pixel 0: attack
        Integer attack = getPixelValue(image, 0, 0);
        gameState.setAttack(attack);

        // Read pixel 2: addon version
        Integer version = getPixelValue(image, 2, 0);
        gameState.setAddonCompatible(version != null && version == ADDON_VERSION);
      } else {
        gameState.setAddonFound(false);
      }

      return gameState;
    }

    @Override
    public void resetAddonForNextSelectedRotation() {
      addonRectangle = null;
    }

    private Integer getPixelValue(BufferedImage image, int col, int row) {
      Color color = new Color(image.getRGB(xPixels.get(col), yPixels.get(row)));
      if (color.equals(Color.WHITE)) {
        return null;
      } else {
        return ((color.getBlue() * 65536) + (color.getGreen() * 256) + color.getRed());
      }
    }

    private void findAddon() {
      BufferedImage image = ServiceLocator.getInstance().getService(RobotService.class)
          .createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

      Coordinate coordinate = findAddonCoordinate(image);
      if (coordinate != null) {
        int width = findAddonWidth(image, coordinate);
        int height = findAddonHeight(image, coordinate);
        addonRectangle = new Rectangle(coordinate.getX().intValue(), coordinate.getY().intValue(), width, height);

        System.out.println("Addon found at (" + coordinate.getX() + "," + coordinate.getY() + ") width=" + width + " height=" + height);

        setPixelMappings(image.getSubimage(addonRectangle.x, addonRectangle.y, addonRectangle.width, addonRectangle.height));
      }
    }

    private Coordinate findAddonCoordinate(BufferedImage image) {
      for (int x = 0; x < image.getWidth(); x++) {
        for (int y = 0; y < image.getHeight(); y++) {
          Color c = new Color(image.getRGB(x, y));
          if (c.equals(UBERPWN_START_MARKER)) {
            return new Coordinate(new Double(x), new Double(y));
          }
        }
      }
      return null;
    }

    private int findAddonWidth(BufferedImage image, Coordinate addonCoordinate) {
      boolean endMarkerFound = false;
      for (int x = addonCoordinate.getX().intValue(); x < Math.min(addonCoordinate.getX().intValue() + 500, image.getWidth()); x++) {
        Color c = new Color(image.getRGB(x, addonCoordinate.getY().intValue()));
        if(endMarkerFound && !(c.equals(UBERPWN_END_MARKER))) {
          return x - addonCoordinate.getX().intValue();
        }
        if(c.equals(UBERPWN_END_MARKER)) {
          endMarkerFound = true;
        }
      }
      return 0;
    }

    private int findAddonHeight(BufferedImage image, Coordinate addonCoordinate) {
      boolean endMarkerFound = false;
      for (int y = addonCoordinate.getY().intValue(); y < Math.min(addonCoordinate.getY().intValue() + 200, image.getHeight()); y++) {
        Color c = new Color(image.getRGB(addonCoordinate.getX().intValue(), y));
        if(endMarkerFound && !(c.equals(UBERPWN_END_MARKER))) {
          return y - addonCoordinate.getY().intValue();
        }
        if(c.equals(UBERPWN_END_MARKER)) {
          endMarkerFound = true;
        }
      }
      return 0;
    }

    private void setPixelMappings(BufferedImage image) {
      xPixels = new ArrayList<Integer>();
      yPixels = new ArrayList<Integer>();

      Color currentColor = null;
      Integer colorStart = null;
      for (int x = 0; x < image.getWidth(); x++) {
        Color color = new Color(image.getRGB(x, 0));
        if(!color.equals(currentColor)) {
          if(currentColor != null) {
            xPixels.add(((x-colorStart)/2)+colorStart);
            currentColor = null;
            colorStart = null;
          }

          if(color.equals(UBERPWN_GRID_MARKER1) || color.equals(UBERPWN_GRID_MARKER2) || color.equals(UBERPWN_END_MARKER)) {
            currentColor = color;
            colorStart = x;
          }
        }
      }
      if(currentColor != null) {
        xPixels.add(((image.getWidth()-colorStart)/2)+colorStart);
      }

      System.out.print(" cols=" + xPixels.size() + "  ");
      for (int x : xPixels) {
        System.out.print(x + " ");
      }
      System.out.println();

      currentColor = null;
      colorStart = null;
      for (int y = 0; y < image.getHeight(); y++) {
        Color color = new Color(image.getRGB(0, y));

        if(!color.equals(currentColor)) {
          if(currentColor != null) {
            yPixels.add(((y-colorStart)/2)+colorStart);
            currentColor = null;
            colorStart = null;
          }

          if(color.equals(UBERPWN_GRID_MARKER1) || color.equals(UBERPWN_GRID_MARKER2) || color.equals(UBERPWN_END_MARKER)) {
            currentColor = color;
            colorStart = y;
          }
        }
      }
      if(currentColor != null) {
        yPixels.add(((image.getHeight()-colorStart)/2)+colorStart);
      }

      System.out.print(" rows=" + yPixels.size() + "  ");
      for (int y : yPixels) {
        System.out.print(y + " ");
      }
      System.out.println();
    }
  }
}
