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

import java.io.IOException;

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

//rghost.ru by pspzockerscene
@HostPlugin(revision = "$Revision: 12651 $", interfaceVersion = 2, names = { "rghost.ru" }, urls = { "http://[\\w\\.]*?(rghost\\.net|rghost\\.ru|phonon\\.rghost\\.ru)/(download/[0-9]+|[0-9]+/private/[a-z0-9]+|[0-9]+)" }, flags = { 0 })
public class RGhostRu extends PluginForHost {

    public RGhostRu(PluginWrapper wrapper) {
        super(wrapper);
        // this host blocks if there is no timegap between the simultan
        // downloads so waittime is 3,5 sec right now, works good!
        this.setStartIntervall(3500l);
    }

    private static final String PWTEXT = "Password: <input id=\"password\" name=\"password\" type=\"password\"";

    @Override
    public AvailableStatus requestFileInformation(DownloadLink link) throws IOException, PluginException {
        this.setBrowserExclusive();
        br.setFollowRedirects(true);
        br.getPage(link.getDownloadURL());
        if (br.containsHTML("Access to the file was restricted") || br.containsHTML("<title>404") || br.containsHTML("File was deleted")) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        String filename = br.getRegex("<meta name=\"description\" content=\"(.*?). Download").getMatch(0);
        if (filename == null) filename = br.getRegex("title=\"Comments for the file (.*?)\"").getMatch(0);
        String filesize = br.getRegex("<small>\\((.*?)\\)</small>").getMatch(0);
        if (filesize == null) filesize = br.getRegex("class=\"filesize\">\\((.*?)\\)</span>").getMatch(0);
        if (filename == null || filesize == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        String md5 = br.getRegex("<b>MD5</b></td><td>(.*?)</td></tr>").getMatch(0);
        if (md5 != null) link.setMD5Hash(md5.trim());
        String sha1 = br.getRegex("<b>SHA1</b></td><td>(.*?)</td></tr>").getMatch(0);
        if (sha1 != null) link.setSha1Hash(sha1.trim());
        link.setName(filename);
        link.setDownloadSize(Regex.getSize(filesize));
        if (br.containsHTML(PWTEXT)) link.getLinkStatus().setStatusText("This file is password protected");
        return AvailableStatus.TRUE;
    }

    @Override
    public void handleFree(DownloadLink link) throws Exception {
        requestFileInformation(link);
        br.setFollowRedirects(false);
        String dllink = br.getRegex("class=\"header_link\">.*?<a href=\"([^\"]*?/download/\\d+.*?)\"").getMatch(0);
        if (dllink == null) dllink = br.getRegex("<a href=\"([^\"]*?/download/\\d+.*?)\"").getMatch(0);
        String passCode = null;
        if (dllink == null && br.containsHTML(PWTEXT)) {
            Form pwform = br.getForm(2);
            if (pwform == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);

            if (link.getStringProperty("pass", null) == null) {
                passCode = Plugin.getUserInput("Password?", link);
            } else {
                /* gespeicherten PassCode holen */
                passCode = link.getStringProperty("pass", null);
            }
            pwform.put("password", passCode);
            br.submitForm(pwform);
            dllink = br.getRegex("class=\"header_link\">.*?<a href=\"([^\"]*?/download/\\d+.*?)\"").getMatch(0);
            if (dllink == null) dllink = br.getRegex("<a href=\"([^\"]*?/download/\\d+.*?)\"").getMatch(0);
            if (dllink == null) {
                link.setProperty("pass", null);
                logger.info("DownloadPW wrong!");
                throw new PluginException(LinkStatus.ERROR_RETRY);
            }
        }
        if (dllink == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        dl = jd.plugins.BrowserAdapter.openDownload(br, link, dllink, true, 0);
        if (!(dl.getConnection().isContentDisposition())) {
            br.followConnection();
            if (br.containsHTML(">409</div>")) {
                sleep(20000l, link);
                throw new PluginException(LinkStatus.ERROR_RETRY);
            }
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        if (passCode != null) link.setProperty("pass", passCode);
        dl.startDownload();
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return -1;
    }

    @Override
    public String getAGBLink() {
        return "http://rghost.ru/tos";
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetPluginGlobals() {
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }

}
