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

package org.krzogr.trackthread.instrument;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Performs tracking of all thread subclasses at runtime.
 * 
 * Thread tracking is needed in order to ensure that every thread subclass has its bytes instrumented. Since it is hard
 * to determine whether a class is thread subclass during initial instrumentation, the actual instrumentation is delayed
 * and performed lazily on demand. Before starting a new thread, the code checks whether this new thread class has
 * already been instrumented. If not, it is instrumented on demand just before the new thread is started.
 */
public class ThreadTracker {
  private static Instrumentation instrumentation;

  private static Map<ClassLoader, Set<String>> threadSubclasses = new WeakHashMap<ClassLoader, Set<String>>();

  public static void initialize(final Instrumentation inst) {
    instrumentation = inst;
  }

  /**
   * Called just before new thread is started.
   * 
   * It checks if this thread class has already been instrumented. If not, the instrumentation is triggered.
   * 
   * @param threadClass The class of the thread which is about to be started.
   */
  public static void threadStarting(final Class<?> threadClass) {
    String internalName = threadClass.getName().replaceAll("\\.", "/");

    if (registerThreadSubclass(threadClass.getClassLoader(), internalName)) {
      try {
        instrumentation.retransformClasses(threadClass);
      } catch (Exception e) {
        System.err.println("Fatal error while transforming class " + threadClass.getName() + ": " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * Registers the specified thread subclass internally. Returns true if this class hasn't been registered before.
   * 
   * @param loader Class loader of the thread class.
   * @param internalName Internal name of the thread class.
   * @return TRUE if this class hasn't been registered before, FALSE otherwise.
   */
  private synchronized static boolean registerThreadSubclass(final ClassLoader loader, final String internalName) {
    Set<String> classNames = threadSubclasses.get(loader);
    if (classNames == null) {
      classNames = new HashSet<String>();
      threadSubclasses.put(loader, classNames);
    }

    return classNames.add(internalName);
  }

  /**
   * Returns TRUE if the specified class represents thread subclass.
   * 
   * @param loader Class loader.
   * @param internalName Internal class name.
   * @return TRUE if the specified class is thread subclass. FALSE otherwise.
   */
  public synchronized static boolean isThreadSubclass(final ClassLoader loader, final String internalName) {
    Set<String> classNames = threadSubclasses.get(loader);
    return classNames != null && classNames.contains(internalName);
  }
}
