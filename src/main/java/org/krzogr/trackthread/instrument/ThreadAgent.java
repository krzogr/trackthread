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

import org.krzogr.trackthread.ThreadListener;
import org.krzogr.trackthread.ThreadListeners;

/**
 * Instrumentation agent used to setup all thread related instrumentation at runtime.
 */
public class ThreadAgent {
  public static void premain(final String agentArgs, final Instrumentation inst) {
    ThreadTracker.initialize(inst);
    
    inst.addTransformer(new ThreadClassTransformer(), true);
    
    if(agentArgs != null) {
      addThreadListener(agentArgs);
    }
  
    transformJavaLangThreadClass(inst);
  }

  private static void addThreadListener(final String className) {
    try {
      Class<?> listenerClass = Class.forName(className);
      ThreadListener listener = (ThreadListener) listenerClass.newInstance();
      ThreadListeners.addListener(listener);
    } catch (Exception e) {
      System.err.println("Fatal error when creating thread listener '" + className + "': " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private static void transformJavaLangThreadClass(final Instrumentation inst) {
    try {
      inst.retransformClasses(Class.forName("java.lang.Thread"));
    } catch (Exception e) {
      System.err.println("Fatal error when transforming class java.lang.Thread: " + e.getMessage());
      e.printStackTrace();
    }
  }
}