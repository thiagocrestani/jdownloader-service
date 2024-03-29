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

import java.util.ArrayList;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.http.Browser;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 7387 $", interfaceVersion = 2, names = { "sdx.cc" }, urls = { "http://[\\w\\.]*?sdx\\.cc/infusions/(pro_download_panel|user_uploads)/download\\.php\\?did=\\d+" }, flags = { 0 })
public class SdxCc extends PluginForDecrypt {

    public SdxCc(PluginWrapper wrapper) {
        super(wrapper);
    }

    // @Override
    public ArrayList<DownloadLink> decryptIt(CryptedLink cryptedLink, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        br.getPage(cryptedLink.toString());
        String pw = br.getRegex("Passwort:<br.*?/>(.*?)</td>").getMatch(0);
        pw = pw != null ? pw.trim() : "sdx.cc";
        br.setFollowRedirects(false);
        for (String link : br.getRegex("align='center'.*?><a href=\"(.*?file.+?)\"").getColumn(0)) {
            Browser brc = br.cloneBrowser();
            brc.getPage(link);
            String red = brc.getRedirectLocation();
            decryptedLinks.add(createDownloadlink(red));
        }

        String[] links = br.getRegex("<center><a href='(.*?)'").getColumn(0);
        for (String link : links) {
            decryptedLinks.add(createDownloadlink(link));
        }
        links = br.getRegex("<br />.*?<a href='(http.*?)'").getColumn(0);
        for (String link : links) {
            decryptedLinks.add(createDownloadlink(link));
        }

        for (DownloadLink dlLink : decryptedLinks) {
            dlLink.addSourcePluginPassword(pw);
            dlLink.setDecrypterPassword(pw);
        }
        return decryptedLinks.size() > 0 ? decryptedLinks : null;
    }

    // @Override

}
