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
import jd.network.rtmp.url.RtmpUrlConnection;
import jd.parser.Regex;
import jd.plugins.DownloadLink;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.DownloadLink.AvailableStatus;

//q= parameter comes from jd.plugins.decrypter.RDMdthk
@HostPlugin(revision = "$Revision: 10857 $", interfaceVersion = 2, names = { "ardmediathek.de" }, urls = { "hrtmp://[\\w\\.]*?ardmediathek\\.de/ard/servlet/content/\\d+\\?documentId=\\d+\\&q=\\w" }, flags = { 0 })
public class ARDMediathek extends PluginForHost {

    private String[] urlValues;
    private String quality;

    public ARDMediathek(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public String getAGBLink() {
        return "http://www.ardmediathek.de/ard/servlet/content/3606532";
    }

    @Override
    public AvailableStatus requestFileInformation(DownloadLink downloadLink) throws IOException, PluginException {
        // check if rtmp link is valid here
        urlValues = new Regex(downloadLink.getDownloadURL(), "hrtmp://[\\w\\.]*?ardmediathek\\.de/ard/servlet/content/(\\d+)\\?documentId=(\\d+)\\&q=(\\w)").getRow(0);
        quality = urlValues[2];
        // call url without q dummy parameter
        br.getPage("http://www.ardmediathek.de/ard/servlet/content/" + urlValues[0] + "?documentId=" + urlValues[1]);
        // invalid content

        if (br.containsHTML("<h1>Leider konnte die gew&uuml;nschte Seite<br />nicht gefunden werden.</h1>")) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        return AvailableStatus.TRUE;
    }

    @Override
    public void handleFree(final DownloadLink downloadLink) throws Exception {
        requestFileInformation(downloadLink);
        String[][] streams = br.getRegex("mediaCollection\\.addMediaStream\\((\\d+), (\\d+), \"(.*?)\", \"(.*?)\"\\);").getMatches();

        String[] stream = null;
        for (String[] s : streams) {
            if (s[3].contains("Web-" + quality)) {
                stream = s;
                break;
            }
        }
        // quality not found
        if (stream == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        dl = new RTMPDownload(this, downloadLink, stream[2] + stream[3]);
        RtmpUrlConnection rtmp = ((RTMPDownload) dl).getRtmpConnection();
        rtmp.setPlayPath(stream[3]);
        rtmp.setApp("ardfs/");
        ((RTMPDownload) dl).startDownload();
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
