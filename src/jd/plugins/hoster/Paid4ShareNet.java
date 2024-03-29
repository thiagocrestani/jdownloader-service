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

package jd.plugins.hoster;

import jd.PluginWrapper;
import jd.parser.Regex;
import jd.parser.html.Form;
import jd.plugins.DownloadLink;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.Plugin;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.DownloadLink.AvailableStatus;

@HostPlugin(revision = "$Revision: 9411 $", interfaceVersion = 2, names = { "paid4share.net" }, urls = { "http://[\\w\\.]*?paid4share\\.net/(download\\.php\\?id=[0-9]+&type=[0-9]|file/[0-9]+/)" }, flags = { 0 })
public class Paid4ShareNet extends PluginForHost {

    public Paid4ShareNet(PluginWrapper wrapper) {
        super(wrapper);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getAGBLink() {
        return "http://paid4share.net/contact.php";
    }

    @Override
    public AvailableStatus requestFileInformation(DownloadLink parameter) throws Exception {
        this.setBrowserExclusive();
        br.setFollowRedirects(true);
        br.getPage(parameter.getDownloadURL() + "?setlang=en");
        // If this host blocks your if, you sometimes can't even reach the page
        // (you get an empety page), for this we check if the browser even
        // contains html code
        if (br.containsHTML("No htmlCode read")) throw new PluginException(LinkStatus.ERROR_IP_BLOCKED, null, 10 * 60 * 1001l);
        if (br.containsHTML("Your requested file is not found")) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        String filename = br.getRegex("<b>File name:</b></td>.*?<td align=.*?width=[0-9]+px>(.*?)</td>").getMatch(0);
        String filesize = br.getRegex("<b>File size:</b></td>.*?<td align=.*?>(.*?)</td>").getMatch(0);
        if (filename == null || filesize == null) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        parameter.setName(filename.trim());
        parameter.setDownloadSize(Regex.getSize(filesize.replaceAll(",", "\\.")));
        return AvailableStatus.TRUE;
    }

    @Override
    public void handleFree(DownloadLink link) throws Exception {
        requestFileInformation(link);
        br.setFollowRedirects(true);
        if (br.containsHTML("downloadpw")) {
            Form pwform = br.getFormbyProperty("name", "myform");
            if (pwform == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
            String passCode = null;
            {
                if (link.getStringProperty("pass", null) == null) {
                    passCode = Plugin.getUserInput("Password?", link);

                } else {
                    /* gespeicherten PassCode holen */
                    passCode = link.getStringProperty("pass", null);
                }
                pwform.put("downloadpw", passCode);
                br.submitForm(pwform);
                if (br.containsHTML("Password Error")) {
                    logger.warning("Wrong password!");
                    link.setProperty("pass", null);
                    throw new PluginException(LinkStatus.ERROR_RETRY);
                }
            }
            if (passCode != null) {
                link.setProperty("pass", passCode);
            }
        }
        // If this host blocks your if, you sometimes can't even reach the page
        // (you get an empety page), for this we check if the browser even
        // contains html code
        if (br.containsHTML("You have got max allowed download sessions from the same IP") || br.containsHTML("No htmlCode read")) throw new PluginException(LinkStatus.ERROR_IP_BLOCKED, null, 10 * 60 * 1001l);
        String dllink = br.getRegex("id=downloadfile style=\"display:none\"><h3><font color=\".*?\"><b><a href=\"(.*?)\"").getMatch(0);
        if (dllink == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        dl = jd.plugins.BrowserAdapter.openDownload(br, link, dllink, false, 1);
        dl.startDownload();
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }

    /*
     * public String getVersion() { return getVersion("$Revision: 9411 $"); }
     */

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return 20;
    }
}
