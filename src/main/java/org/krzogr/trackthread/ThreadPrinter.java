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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.krzogr.trackthread.ThreadListener;

/**
 * Simple implementation of {@class ThreadListener} which prints thread related events to standard output.
 */
public class ThreadPrinter implements ThreadListener {
  private static AtomicInteger threadCount = new AtomicInteger();

  public void onThreadStarting(final Thread thread) {
    StringBuilder buffer = createBuffer();

    buffer.append("STARTING THREAD ");
    appendTreadInfo(buffer, thread);
    appendStackTrace(buffer, Thread.currentThread());

    System.out.println(buffer.toString());
  }

  public void onThreadRunning(final Thread thread) {
    threadCount.incrementAndGet();

    StringBuilder buffer = createBuffer();

    buffer.append("RUNNING THREAD ");
    appendTreadInfo(buffer, thread);

    System.out.println(buffer.toString());
  }

  public void onThreadRename(final Thread thread, final String newName) {
    StringBuilder buffer = createBuffer();

    buffer.append("RENAMING THREAD ");
    appendTreadInfo(buffer, thread);
    buffer.append("NewName='").append(newName).append("'");

    System.out.println(buffer.toString());
  }

  public void onThreadStopping(final Thread thread) {
    threadCount.decrementAndGet();

    StringBuilder buffer = createBuffer();

    buffer.append("STOPPING THREAD ");
    appendTreadInfo(buffer, thread);

    System.out.println(buffer.toString());
  }

  private StringBuilder createBuffer() {
    StringBuilder buffer = new StringBuilder();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    buffer.append(formatter.format(new Date())).append(" ");

    return buffer;
  }

  private void appendTreadInfo(final StringBuilder buffer, final Thread thread) {
    buffer.append("Name='").append(thread.getName()).append("' ");
    buffer.append("Id=").append(thread.getId()).append(" ");
    buffer.append("ThreadCount=").append(threadCount.get()).append(" ");
  }

  private void appendStackTrace(final StringBuilder buffer, final Thread thread) {
    buffer.append("StackTrace=[");

    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (int i = elements.length - 1; i > 0; i--) {
      if (!elements[i].getClassName().startsWith("org.krzogr.trackthread")) {
        buffer.append(elements[i].toString() + " | ");
      }
    }

    buffer.append("]");
  }
}
