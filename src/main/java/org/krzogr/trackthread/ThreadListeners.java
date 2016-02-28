/*
 * Copyright (C) 2015 krzogr (krzogr@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.krzogr.trackthread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton which can be used by the application to register thread listeners at runtime.
 * 
 * Thread listeners should be registered at the very beginning during application startup.
 * 
 * This class supports concurrent access (addListener / removeListener).
 */
public class ThreadListeners {
  private static List<ThreadListener> listeners = new CopyOnWriteArrayList<ThreadListener>();

  public static void addListener(final ThreadListener listener) {
    listeners.add(listener);
  }

  public static void removeListener(final ThreadListener listener) {
    listeners.remove(listener);
  }

  public static void fireThreadStarting(final Thread thread) {
    for (ThreadListener listener : listeners) {
      try {
        listener.onThreadStarting(thread);
      } catch (Exception e) {
        System.err.println("Fatal error when calling ThreadListener: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static void fireThreadRunning(final Thread thread) {
    for (ThreadListener listener : listeners) {
      try {
        listener.onThreadRunning(thread);
      } catch (Exception e) {
        System.err.println("Fatal error when calling ThreadListener: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static void fireThreadRename(final Thread thread, final String newName) {
    for (ThreadListener listener : listeners) {
      try {
        listener.onThreadRename(thread, newName);
      } catch (Exception e) {
        System.err.println("Fatal error when calling ThreadListener: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static void fireThreadStopping(final Thread thread) {
    for (ThreadListener listener : listeners) {
      try {
        listener.onThreadStopping(thread);
      } catch (Exception e) {
        System.err.println("Fatal error when calling ThreadListener: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}
