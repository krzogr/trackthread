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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Class visitor which performs the instrumentation of {@code java.lang.Thread}.
 */
public class ThreadClassVisitor extends ClassVisitor {
  public ThreadClassVisitor(final int api, final ClassVisitor cv) {
    super(api, cv);
  }

  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
      final String[] exceptions) {
    if (StartMethodVisitor.matches(access, name, desc)) {
      return new StartMethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions));
    } else if (SetNameMethodVisitor.matches(access, name, desc)) {
      return new SetNameMethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions));
    } else if (RunMethodVisitor.matches(access, name, desc)) {
      return new RunMethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions), access,
          name, desc);
    } else {
      return super.visitMethod(access, name, desc, signature, exceptions);
    }
  }
}