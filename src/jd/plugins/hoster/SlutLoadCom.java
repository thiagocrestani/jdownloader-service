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
import jd.plugins.DownloadLink;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.DownloadLink.AvailableStatus;

@HostPlugin(revision = "$Revision: 10832 $", interfaceVersion = 2, names = { "slutload.com" }, urls = { "http://[\\w\\.]*?slutload\\.com/watch/[a-zA-Z0-9]+" }, flags = { 0 })
public class SlutLoadCom extends PluginForHost {

    public SlutLoadCom(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public String getAGBLink() {
        return "http://www.empflix.com/terms.php";
    }

    @Override
    public AvailableStatus requestFileInformation(DownloadLink downloadLink) throws IOException, PluginException {
        this.setBrowserExclusive();
        br.setFollowRedirects(false);
        br.getPage(downloadLink.getDownloadURL());
        if (br.getRedirectLocation() != null && br.getRedirectLocation().equals("http://www.slutload.com/")) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        // Sometimes the site does multiple redirects
        for (int i = 0; i <= 3; i++) {
            if (br.getRedirectLocation() != null)
                br.getPage(br.getRedirectLocation());
            else {
                downloadLink.setUrlDownload(br.getURL());
                break;
            }
        }
        if (br.getRedirectLocation() != null || !br.getURL().contains("slutload.com/")) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        String filename = br.getRegex("<h2>(.*?)</h2>").getMatch(0);
        if (filename == null || filename.length() > 50) {
            filename = br.getRegex("<h1>Video: <b>(.*?)</b>").getMatch(0);
            if (filename == null) {
                filename = br.getRegex("<title>(.*?)- SlutLoad\\.com</title").getMatch(0);
            }
        }
        String filesize = br.getRegex(">Download \\((.*?)\\)").getMatch(0);
        if (filename == null) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        filename = filename.trim();
        downloadLink.setFinalFileName(filename + ".flv");
        if (filesize != null) downloadLink.setDownloadSize(Regex.getSize(filesize));
        return AvailableStatus.TRUE;
    }

    @Override
    public void handleFree(DownloadLink downloadLink) throws Exception {
        requestFileInformation(downloadLink);
        String dllink = br.getRegex("class=\"download\" href=\"(.*?)\"").getMatch(0);
        if (dllink == null) {
            dllink = br.getRegex("\"(http://v-ec\\.slutload-media\\.com/.*?\\.flv\\?.*?)\"").getMatch(0);
        }
        if (dllink == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        dl = jd.plugins.BrowserAdapter.openDownload(br, downloadLink, dllink, true, 0);
        if (dl.getConnection().getContentType().contains("html")) {
            br.followConnection();
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        dl.startDownload();
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return -1;
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
