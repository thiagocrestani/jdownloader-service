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
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.DownloadLink.AvailableStatus;

@HostPlugin(revision = "$Revision: 8967 $", interfaceVersion = 2, names = { "plikus.pl" }, urls = { "http://[\\w\\.]*?plikus\\.pl/zobacz_plik-.*?-\\d+\\.html" }, flags = { 0 })
public class PlikusPl extends PluginForHost {

    public PlikusPl(PluginWrapper wrapper) {
        super(wrapper);
        setStartIntervall(5000l);
    }

    // @Override
    public String getAGBLink() {
        return "http://www.osemka.pl/html/regulamin/#regulamin_plikus";
    }

    // @Override
    public AvailableStatus requestFileInformation(DownloadLink downloadLink) throws PluginException, IOException {
        setBrowserExclusive();
        br.getPage(downloadLink.getDownloadURL());
        String filesize = br.getRegex("<p><b>Rozmiar pliku:</b>(.*?)</p>").getMatch(0);
        String filename = br.getRegex("<h1><img src=.*?/>(.*?)</h1>").getMatch(0);
        if (filesize == null || filename == null) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        downloadLink.setName(filename.trim());
        downloadLink.setDownloadSize(Regex.getSize(filesize.trim()));
        return AvailableStatus.TRUE;
    }

    // @Override
    /*
     * public String getVersion() { return getVersion("$Revision: 8967 $"); }
     */

    // @Override
    public void handleFree(DownloadLink downloadLink) throws Exception {
        requestFileInformation(downloadLink);
        Form form = br.getForm(0);
        br.setFollowRedirects(false);
        if (br.getRedirectLocation() != null) throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, 10 * 60 * 1000l);
        dl = jd.plugins.BrowserAdapter.openDownload(br,downloadLink, form, true, 0);
        dl.startDownload();
    }

    // @Override
    public int getMaxSimultanFreeDownloadNum() {
        return 20;
    }

    // @Override
    public int getTimegapBetweenConnections() {
        return 2500;
    }

    // @Override
    public void reset() {
    }

    // @Override
    public void resetPluginGlobals() {

    }

    // @Override
    public void resetDownloadlink(DownloadLink link) {
    }

}
