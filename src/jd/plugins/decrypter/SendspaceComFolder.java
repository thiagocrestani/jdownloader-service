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
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 8514 $", interfaceVersion = 2, names = { "Sendspace.com folder" }, urls = { "http://[\\w\\.]*?sendspace\\.com/folder/[0-9a-zA-Z]+" }, flags = { 0 })
public class SendspaceComFolder extends PluginForDecrypt {

    public SendspaceComFolder(PluginWrapper wrapper) {
        super(wrapper);
    }

    public ArrayList<DownloadLink> decryptIt(CryptedLink param, ProgressController progress) throws Exception {
        br.getPage(param.getCryptedUrl());
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        String[] files = br.getRegex("<td class=\"dl\" nowrap><a href=\"(.*?)\" title=").getColumn(0);
        for (String file : files) {
            decryptedLinks.add(createDownloadlink(file));
        }
        return decryptedLinks;
    }
}
