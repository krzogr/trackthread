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

/**
 * Represents thread listener which can be registered at runtime to capture thread related events.
 * 
 * To register the listener use {@code ThreadListeners.addListener()}.
 */
public interface ThreadListener {
  /**
   * Called when new thread is about to be started.
   * 
   * This method is called from within {@code Thread.start()} before the new thread is actually started. It is called in
   * the context of the parent thread which is executing {@code Thread.start()}.
   * 
   * @param thread New thread which is about to be started
   */
  void onThreadStarting(Thread thread);

  /**
   * Called just after the new thread has been started.
   * 
   * This method will be called in the context of the child thread. It is called from within child thread's run method
   * before the actual thread code.
   * 
   * @param thread New thread which has just been started.
   */
  void onThreadRunning(Thread thread);

  /**
   * Called when the thread is about to be renamed.
   * 
   * This method will be called in the context of the thread which is calling {@code Thread.setName()}.
   * 
   * @param thread Thread which is about to be renamed.
   * @param newName New name which will be assigned to the thread.
   */
  void onThreadRename(Thread thread, String newName);

  /**
   * Called just before thread termination.
   * 
   * This method will be called in the context of the child thread. It is called from within child thread's run method
   * just before exiting it. It is called in try/finally fashion to intercept all exit paths from the run method.
   * 
   * @param thread Thread which is about to be terminated.
   */
  void onThreadStopping(Thread thread);
}
