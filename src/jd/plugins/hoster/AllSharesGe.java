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
import jd.nutils.encoding.Encoding;
import jd.parser.Regex;
import jd.parser.html.Form;
import jd.parser.html.HTMLParser;
import jd.plugins.DownloadLink;
import jd.plugins.DownloadLink.AvailableStatus;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;

@HostPlugin(revision = "$Revision: 12369 $", interfaceVersion = 2, names = { "allshares.ge" }, urls = { "http://[\\w\\.]*?allshares\\.ge/(\\?d|download\\.php\\?id)=[A-Z0-9]+" }, flags = { 0 })
public class AllSharesGe extends PluginForHost {

    public AllSharesGe(PluginWrapper wrapper) {
        super(wrapper);
    }

    // MhfScriptBasic 1.0
    @Override
    public String getAGBLink() {
        return COOKIE_HOST + "/rules.php";
    }

    public String finalLink = null;
    private static final String COOKIE_HOST = "http://allshares.ge";

    @Override
    public AvailableStatus requestFileInformation(DownloadLink parameter) throws Exception {
        this.setBrowserExclusive();
        br.setFollowRedirects(true);
        br.setCookie(COOKIE_HOST, "mfh_mylang", "en");
        br.setCookie(COOKIE_HOST, "yab_mylang", "en");
        br.getPage(parameter.getDownloadURL());
        if (br.containsHTML("Your requested file is not found")) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        String filename = br.getRegex("<b>File name:</b></td>.*?<td align=.*?width=.*?>(.*?)</td>").getMatch(0);
        if (filename == null) {
            br.getRegex("\"Click this to report for(.*?)\"").getMatch(0);
            if (filename == null) {
                br.getRegex("<title>(.*?)</title>").getMatch(0);
            }
            if (filename == null) {
                br.getRegex("content=\"(.*?), The best file hosting service").getMatch(0);
            }
        }
        String filesize = br.getRegex("<b>File size:</b></td>.*?<td align=.*?>(.*?)</td>").getMatch(0);
        if (filename == null) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        parameter.setFinalFileName(filename.trim());
        if (filesize != null) parameter.setDownloadSize(Regex.getSize(filesize));
        return AvailableStatus.TRUE;
    }

    public void findLink(DownloadLink link) throws Exception {
        String[] sitelinks = HTMLParser.getHttpLinks(br.toString(), null);
        if (sitelinks == null || sitelinks.length == 0) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        for (String alink : sitelinks) {
            alink = Encoding.htmlDecode(alink);
            if (alink.contains("access_key=") || alink.contains("getfile.php?")) {
                finalLink = alink;
                break;
            }
        }
    }

    @Override
    public void handleFree(DownloadLink link) throws Exception {
        this.setBrowserExclusive();
        requestFileInformation(link);
        String passCode = null;
        for (int i = 0; i <= 3; i++) {
            Form captchaform = br.getFormbyProperty("name", "myform");
            if (captchaform == null) {
                captchaform = br.getFormbyProperty("name", "validateform");
                if (captchaform == null) {
                    captchaform = br.getFormbyProperty("name", "valideform");
                }
            }
            String captchaurl = COOKIE_HOST + "/captcha.php";
            if (captchaform == null || !br.containsHTML("captcha.php")) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
            if (br.containsHTML("downloadpw")) {
                if (link.getStringProperty("pass", null) == null) {
                    passCode = getUserInput(null, link);

                } else {
                    /* gespeicherten PassCode holen */
                    passCode = link.getStringProperty("pass", null);
                }
                captchaform.put("downloadpw", passCode);
            }
            String code = getCaptchaCode(captchaurl, link);
            captchaform.put("captchacode", code);
            br.submitForm(captchaform);
            if (br.containsHTML("Password Error")) {
                logger.warning("Wrong password!");
                link.setProperty("pass", null);
                continue;
            }
            if (br.containsHTML("Captcha number error") || br.containsHTML("captcha.php") && !br.containsHTML("You have got max allowed bandwidth size per hour")) {
                logger.warning("Wrong captcha or wrong password!");
                link.setProperty("pass", null);
                continue;
            }
            break;
        }
        if (br.containsHTML("Password Error")) {
            logger.warning("Wrong password!");
            link.setProperty("pass", null);
            throw new PluginException(LinkStatus.ERROR_RETRY);
        }
        if (br.containsHTML("Captcha number error") || br.containsHTML("captcha.php") && !br.containsHTML("You have got max allowed bandwidth size per hour")) {
            logger.warning("Wrong captcha or wrong password!");
            link.setProperty("pass", null);
            throw new PluginException(LinkStatus.ERROR_CAPTCHA);
        }
        if (passCode != null) {
            link.setProperty("pass", passCode);
        }
        if (br.containsHTML("You have got max allowed bandwidth size per hour")) throw new PluginException(LinkStatus.ERROR_IP_BLOCKED, null, 10 * 60 * 1001l);
        findLink(link);
        if (finalLink == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        dl = jd.plugins.BrowserAdapter.openDownload(br, link, finalLink, true, 1);
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
    public void resetDownloadlink(DownloadLink link) {
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return -1;
    }

}
