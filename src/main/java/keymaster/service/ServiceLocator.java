package keymaster.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import keymaster.service.GameStateService.GameStateServiceImpl;
import keymaster.service.RobotService.RobotServiceImpl;

public class ServiceLocator {

  private static ServiceLocator serviceLocator;

  private Map<Class<?>, Object> services;

  private ServiceLocator() {
    services = new ConcurrentHashMap<Class<?>, Object>();
  }

  public static ServiceLocator getInstance() {
    if (serviceLocator == null) {
      serviceLocator = new ServiceLocator();
      serviceLocator.reset();
    }

    return serviceLocator;
  }

  @SuppressWarnings("unchecked")
  public <T> T getService(Class<T> clazz) {
    return (T) services.get(clazz);
  }

  public <T> void registerService(Class<T> clazz, T object) {
    services.put(clazz, object);
  }

  public void reset() {
    services.clear();
    registerService(GameStateService.class, new GameStateServiceImpl());
    registerService(RobotService.class, new RobotServiceImpl());
  }
}
