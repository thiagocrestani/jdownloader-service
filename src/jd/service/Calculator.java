//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.service;

/**
 * @author Dominik Psenner <dpsenner@gmail.com>
 *
 */
public interface Calculator {
    /**
     * a simple method that does a invoke and returns how often
     * the invoke was called. Ideally it should be incrementally. ;-)
     * @return
     */
    public int calls();
    
    /**
     * adds two integers.
     * @param i1
     * @param i2
     * @return
     */
    public int add(int i1, int i2);
    
    /**
     * subtracts one integer from a second.
     * @param i1
     * @param i2
     * @return
     */
    public int subtract(int i1, int i2);
}
