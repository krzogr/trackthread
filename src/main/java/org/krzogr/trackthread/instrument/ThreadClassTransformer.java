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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Performs the instrumentation of {@code java.lang.Thread} class and thread subclasses.
 */
public class ThreadClassTransformer implements ClassFileTransformer {
  public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
      final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

    if (className.equals("java/lang/Thread")) {
      return transformThreadClass(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, false);
    } else if (ThreadTracker.isThreadSubclass(loader, className)) {
      return transformThreadClass(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, true);
    } else {
      return classfileBuffer;
    }
  }

  private byte[] transformThreadClass(final ClassLoader loader, final String className,
      final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer,
      final boolean isThreadSubclass) {
    try {
      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
      
      ClassVisitor cv = isThreadSubclass ? new ThreadSubclassVisitor(Opcodes.ASM4, cw) : new ThreadClassVisitor(
          Opcodes.ASM4, cw);

      ClassReader cr = new ClassReader(classfileBuffer);
      cr.accept(cv, ClassReader.EXPAND_FRAMES);

      return cw.toByteArray();
    } catch (Exception e) {
      System.err.println("Fatal error while transforming class " + className + ": " + e.getMessage());
      e.printStackTrace();
    }

    return classfileBuffer;
  }
}