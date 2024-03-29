//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
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

import java.io.IOException;
import java.util.ArrayList;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 7387 $", interfaceVersion = 2, names = { "falinks.com" }, urls = { "http://[\\w\\.]*?falinks\\.com/(\\?fa=link&id=\\d+|link/\\d+/?)" }, flags = { 0 })
public class FlnksCm extends PluginForDecrypt {

    public FlnksCm(PluginWrapper wrapper) {
        super(wrapper);
    }

    // @Override
    public ArrayList<DownloadLink> decryptIt(CryptedLink param, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        String parameter = param.toString();

        try {
            br.getPage(parameter);
            String pw = br.getRegex("<b>Passwort =(.*?)</b>").getMatch(0);
            String[] links = br.getRegex("\\<input type=\"hidden\" name=\"url\" value=\"(.*?)\" \\/\\>").getColumn(0);
            progress.setRange(links.length);
            for (String link : links) {
                DownloadLink dlLink = createDownloadlink(link);
                if (pw != null) dlLink.addSourcePluginPassword(pw.trim());
                decryptedLinks.add(dlLink);
                progress.increase(1);
            }
        } catch (IOException e) {
            logger.log(java.util.logging.Level.SEVERE, "Exception occurred", e);
            return null;
        }
        return decryptedLinks;
    }

    // @Override

}
