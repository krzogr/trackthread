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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Visitor which performs instrumentation for Thread.setName() method.
 */
public class SetNameMethodVisitor extends MethodVisitor {
  public SetNameMethodVisitor(final int api, final MethodVisitor mv) {
    super(api, mv);
  }

  public static boolean matches(final int access, final String name, final String desc) {
    return (access & Opcodes.ACC_PUBLIC) != 0 && "setName".equals(name) && "(Ljava/lang/String;)V".equals(desc);
  }

  @Override
  public void visitCode() {
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/krzogr/trackthread/ThreadListeners", "fireThreadRename",
        "(Ljava/lang/Thread;Ljava/lang/String;)V", false);

    super.visitCode();
  }
}
