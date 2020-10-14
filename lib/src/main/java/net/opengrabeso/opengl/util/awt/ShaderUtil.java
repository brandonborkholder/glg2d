/*
 * Copyright (c) 2009 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 */

package net.opengrabeso.opengl.util.awt;

import com.github.opengrabeso.jaagl.GL2GL3;

import java.io.PrintStream;

public class ShaderUtil {
    public static String getShaderInfoLog(final GL2GL3 gl, final int shaderObj) {
        return gl.glGetShaderInfoLog(shaderObj);
    }

    public static String getProgramInfoLog(final GL2GL3 gl, final int programObj) {
        return gl.glGetProgramInfoLog(programObj);
    }

    public static boolean isShaderStatusValid(final GL2GL3 gl, final int shaderObj, final int name, final PrintStream verboseOut) {
        final int[] ires = new int[1];
        gl.glGetShaderiv(shaderObj, name, ires, 0);

        final boolean res = ires[0] == 1;
        if (!res && null != verboseOut) {
            verboseOut.println("Shader status invalid: " + getShaderInfoLog(gl, shaderObj));
        }
        return res;
    }

    public static boolean isProgramStatusValid(final GL2GL3 gl, final int programObj, final int name) {
        final int[] ires = new int[1];
        gl.glGetProgramiv(programObj, name, ires, 0);

        return ires[0] == 1;
    }


}
