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

package jd.plugins.decrypter;

import java.util.ArrayList;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.FilePackage;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 8391 $", interfaceVersion = 2, names = { "hsbsitez.com" }, urls = { "http://[\\w\\.]*?hsbsitez\\.com/files/\\d+/.*" }, flags = { 0 })
public class Hsbstzcm extends PluginForDecrypt {

    public Hsbstzcm(PluginWrapper wrapper) {
        super(wrapper);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ArrayList<DownloadLink> decryptIt(CryptedLink parameter, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        br.getPage(parameter.toString());
        String[] links = br.getRegex("<b>Original Url: </b> 	&nbsp; (http://.*?)<BR>").getColumn(0);
        progress.setRange(links.length);
        FilePackage fp = FilePackage.getInstance();

        for (String data : links) {

            decryptedLinks.add(createDownloadlink(data));
            progress.increase(1);
        }
        String fpname = null;
        fpname = br.getRegex("<TITLE>(.*?) \\| FilesCollection</TITLE>").getMatch(0);
        fp.setName(fpname);
        fp.addLinks(decryptedLinks);
        return decryptedLinks;
    }
}
