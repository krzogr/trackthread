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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Visitor which performs instrumentation for Thread.run() method.
 */
public class RunMethodVisitor extends AdviceAdapter {
  private Label startFinally = new Label();

  public RunMethodVisitor(final int api, final MethodVisitor mv, final int access, final String name, final String desc) {
    super(api, mv, access, name, desc);
  }

  public static boolean matches(final int access, final String name, final String desc) {
    return (access & Opcodes.ACC_PUBLIC) != 0 && "run".equals(name) && "()V".equals(desc);
  }

  @Override
  protected void onMethodEnter() {
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/krzogr/trackthread/ThreadListeners", "fireThreadRunning",
        "(Ljava/lang/Thread;)V", false);
  }

  @Override
  public void visitCode() {
    super.visitCode();
    mv.visitLabel(startFinally);
  }

  @Override
  public void visitMaxs(final int maxStack, final int maxLocals) {
    Label endFinally = new Label();
    mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
    mv.visitLabel(endFinally);
    onFinally(Opcodes.ATHROW);
    mv.visitInsn(Opcodes.ATHROW);
    mv.visitMaxs(maxStack, maxLocals);
  }

  @Override
  protected void onMethodExit(final int opcode) {
    if (opcode != Opcodes.ATHROW) {
      onFinally(opcode);
    }
  }

  private void onFinally(final int opcode) {
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/krzogr/trackthread/ThreadListeners", "fireThreadStopping",
        "(Ljava/lang/Thread;)V", false);
  }
}
