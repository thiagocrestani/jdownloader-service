package jd.plugins.decrypter;

import java.util.ArrayList;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.parser.Regex;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.PluginForDecrypt;

@DecrypterPlugin(revision = "$Revision: 12442 $", interfaceVersion = 2, names = { "uploading.com" }, urls = { "http://[\\w\\.]*?uploading\\.com/linklists/\\w+" }, flags = { 0 })
public class PldingFldr extends PluginForDecrypt {

    public PldingFldr(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public ArrayList<DownloadLink> decryptIt(CryptedLink parameter, ProgressController progress) throws Exception {
        ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        String code = new Regex(parameter.toString(), "linklists/(\\w+)").getMatch(0);
        int page = 1;
        while (true) {
            br.postPage("http://uploading.com/folders/main//?JsHttpRequest=" + System.currentTimeMillis() + "-xml", "action=get_files&code=" + code + "&pass=&page=" + page);
            String correctedHTML = br.toString().replace("\\", "");
            String founds[] = new Regex(correctedHTML, "(http://[\\w\\.]*?uploading\\.com/files/(get/)?\\w+)").getColumn(0);
            if (founds != null) {
                for (String found : founds) {
                    DownloadLink dLink = createDownloadlink(found);
                    decryptedLinks.add(dLink);
                }
            }
            page++;
            if (!new Regex(correctedHTML, "href=\"#\">" + page).matches()) {
                break;
            }
        }
        return decryptedLinks;
    }

}
