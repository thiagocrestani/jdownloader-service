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
import jd.nutils.encoding.Encoding;
import jd.parser.Regex;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 7387 $", interfaceVersion = 2, names = { "protectlinks.com" }, urls = { "http://[\\w\\.]*?protectlinks\\.com/\\d+" }, flags = { 0 })
public class PrtctLnksCm extends PluginForDecrypt {

    public PrtctLnksCm(PluginWrapper wrapper) {
        super(wrapper);
    }

    // @Override
    public ArrayList<DownloadLink> decryptIt(CryptedLink param, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        String parameter = param.toString();
        String redirectid = new Regex(parameter, ".*?protectlinks\\.com/([^/]*)").getMatch(0);
        br.setFollowRedirects(true);
        br.getPage("http://www.protectlinks.com/redirect.php?id=" + redirectid);
        String link = br.getRegex("<iframe name=\"pagetext\"[^>]* src=\"\\s*(.*?)\"").getMatch(0);
        if (link == null) return null;
        decryptedLinks.add(createDownloadlink(Encoding.htmlDecode(link)));

        return decryptedLinks;
    }

    // @Override

}
