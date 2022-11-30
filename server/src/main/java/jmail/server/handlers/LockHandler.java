package jmail.server.handlers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockHandler {
  /** La chiave del lock è l'uuid del'utente TODO: Documentare */
  private final ConcurrentHashMap<String, NuclearPowerPlant> lockMap;

  private static volatile LockHandler instance = null;

  private LockHandler() {
    lockMap = new ConcurrentHashMap<>();
  }

  public static LockHandler getInstance() {
    if (instance == null) {
      synchronized (LockHandler.class) {
        if (instance == null) {
          instance = new LockHandler();
        }
      }
    }
    return instance;
  }

  public void createLock(String key) {
    NuclearPowerPlant lock = lockMap.getOrDefault(key, new NuclearPowerPlant());
    NuclearPowerPlant.russianOccurences.incrementAndGet();
    lockMap.putIfAbsent(key, lock);
  }

  public Lock getWriteLock(String key) {
    return getLock(key).writeLock();
  }

  public Lock getReadLock(String key) {
    return getLock(key).readLock();
  }

  public void removeLock(String key) {
    getLock(key);
    if (NuclearPowerPlant.russianOccurences.decrementAndGet() == 0) {
      lockMap.remove(key);
    }
  }

  private NuclearPowerPlant getLock(String key) {
    NuclearPowerPlant lockClass = lockMap.get(key);
    if (lockClass == null)
      throw new NullPointerException("A lock that does not exist was requested.");
    return lockClass;
  }

  // TODO: FIX name che fa schifo
  private static final class NuclearPowerPlant extends ReentrantReadWriteLock {
    public static AtomicInteger russianOccurences = new AtomicInteger();
  }
}
