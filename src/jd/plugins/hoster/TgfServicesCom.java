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

import java.io.File;
import java.io.IOException;

import jd.PluginWrapper;
import jd.parser.Regex;
import jd.plugins.DownloadLink;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.DownloadLink.AvailableStatus;
import jd.utils.JDUtilities;

@HostPlugin(revision = "$Revision: 12589 $", interfaceVersion = 2, names = { "tgf-services.com" }, urls = { "http://[\\w\\.]*?tgf-services\\.com/UserDownloads/.+" }, flags = { 0 })
public class TgfServicesCom extends PluginForHost {

    public TgfServicesCom(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public String getAGBLink() {
        return "http://about.socifiles.com/terms-of-service";
    }

    @Override
    public AvailableStatus requestFileInformation(DownloadLink link) throws IOException, PluginException {
        this.setBrowserExclusive();
        br.setFollowRedirects(false);
        br.getPage(link.getDownloadURL());
        if (br.getRedirectLocation() != null) {
            if (br.getRedirectLocation().contains("/Warning/?err_num=15")) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        String filename = br.getRegex("<h2>Part Name:</h2></td>[\n\t\r ]+<td><h2>(.*?)</h2>").getMatch(0);
        if (filename == null) filename = br.getRegex("id=\"aname\">(.*?)</h1>").getMatch(0);
        if (filename == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        String ext = br.getRegex("<h2>Bitrate: \\d+, Frequency: \\d+, Mode: (Stereo|Mono), (.*?)</h2>").getMatch(1);
        if (ext != null) filename += "." + ext;
        link.setName(filename.trim());
        String filesize = br.getRegex("<h2>File size:</h2></td>[\t\n\r ]+<td><h2>(.*?)</h2></td>").getMatch(0);
        if (filesize != null) link.setDownloadSize(Regex.getSize(filesize.trim()));
        return AvailableStatus.TRUE;
    }

    @Override
    public void handleFree(DownloadLink downloadLink) throws Exception, PluginException {
        this.setBrowserExclusive();
        requestFileInformation(downloadLink);
        String reCaptchaID = br.getRegex("\\?k=(.*?)\"").getMatch(0);
        if (reCaptchaID == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        br.getPage("http://tgf-services.com/js/scripts.js");
        // String waittime = br.getRegex("secs=(\\d+);").getMatch(0);
        PluginForHost recplug = JDUtilities.getPluginForHost("DirectHTTP");
        jd.plugins.hoster.DirectHTTP.Recaptcha rc = ((DirectHTTP) recplug).getReCaptcha(br);
        rc.setId(reCaptchaID);
        rc.load();
        // need download if not don't work
        try {
            br.getPage("http://tgf-services.com/reg_code_process.php");
        } catch (Exception e) {
        }
        File cf = rc.downloadCaptcha(getLocalCaptchaFile());
        String c = getCaptchaCode(cf, downloadLink);
        String postURL = "http://tgf-services.com/pages/checkCapture.php?start=1&recaptcha_challenge_field=" + rc.getChallenge() + "&recaptcha_response_field=" + c.replace(" ", "%20") + "&PHPSESSID=" + br.getCookie("http://tgf-services.com", "PHPSESSID") + "&" + System.currentTimeMillis() * 10 + "-xml";
        br.postPage(postURL, "dump=1");
        // Captcha errorhandling
        if (br.containsHTML("'error': '2'")) throw new PluginException(LinkStatus.ERROR_CAPTCHA);
        // Unknown error->Plugin broken
        if (br.containsHTML("'error':")) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        // Ticket Time - not needed, can be skipped atm!
        // int tt = 50;
        // if (waittime != null) {
        // logger.info("Waittime detected, waiting " + waittime +
        // " seconds from now on...");
        // tt = Integer.parseInt(waittime);
        // }
        // sleep(tt * 1001, downloadLink);
        String theHash = new Regex(downloadLink.getDownloadURL(), "tgf-services\\.com/UserDownloads/(.+)/").getMatch(0);
        if (theHash == null) theHash = new Regex(downloadLink.getDownloadURL(), "tgf-services\\.com/UserDownloads/(.+)").getMatch(0);
        String postURL2 = "http://tgf-services.com/pages/getDownloadPage.php?start=1&hash=" + theHash + "&PHPSESSID=" + br.getCookie("http://tgf-services.com", "PHPSESSID") + "&" + System.currentTimeMillis() * 10 + "-xml";
        br.postPage(postURL2, "dump=1");
        // stupid download limit ... one file per day?
        if (br.containsHTML("You have reached your daily free downloads limits\\.")) throw new PluginException(LinkStatus.ERROR_IP_BLOCKED, 120 * 60 * 1000l);
        String dllink = br.getRegex("<a href=\\\\\"(.*?)\\\\\"").getMatch(0);

        dl = jd.plugins.BrowserAdapter.openDownload(br, downloadLink, dllink, true, 1);
        // Possible error - link change every refresh ...
        // This download link has already expired.
        if (dl.getConnection().getContentType().contains("html")) {
            br.followConnection();
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        dl.startDownload();
    }

    @Override
    public void reset() {
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return 1;
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }

}