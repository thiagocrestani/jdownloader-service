/*
 * Copyright (C) 2002 - 2005 Leonardo Ferracci
 *
 * This file is part of JAxe.
 *
 * JAxe is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * JAxe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with JAxe; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.  Or, visit http://www.gnu.org/copyleft/gpl.html
 */

package jd.plugins.optional.hjsplit.jaxe;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class UnixSplitFileFilter extends FileFilter {
    public static String getJoinedFileName(String s) {
        int i = s.lastIndexOf("aa");

        if (i == -1) {
            return s;
        } else {
            return s.substring(0, i - 1);
        }
    }

    public static boolean isSplitFile(String s) {
        return s.endsWith("aa");
    }

    public UnixSplitFileFilter() {
    }

    //@Override
    public boolean accept(File f) {
        return UnixSplitFileFilter.isSplitFile(f.getName()) || f.isDirectory();
    }

    //@Override
    public String getDescription() {
        return "Files split using Unix split";
    }
}
