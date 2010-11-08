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

package jd.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.WebServer;

public class ServletServer {
    private static final int port = 8081;

    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer(port);
        
        PropertyHandlerMapping pMapping = new PropertyHandlerMapping();
        pMapping.addHandler("Calculator", Calculator.class);
        webServer.getXmlRpcServer().setHandlerMapping(pMapping);
        
        Thread clt = new Thread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            public void run() {
                while(true) {
                    try {
                        XmlRpcClient xmlrpc = new XmlRpcClient();
                        xmlrpc.setConfig(new XmlRpcClientConfigImpl() {
                            private static final long serialVersionUID = -130700980053934808L;
                            @Override
                            public URL getServerURL() {
                                try {
                                    return new URL("http", "localhost", port, "");
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            }
                            
                        });
                        
                        Vector<Integer> params = new Vector<Integer> ();
                        params.addElement (3);
                        params.addElement(4);
                        
                        // print result
                        System.out.println(xmlrpc.execute ("Calculator.add", params));
                    }
                    catch(Exception e) {
                        System.out.println(e);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        });
        
        try {
            clt.start();
            webServer.start();
        } finally {
            //webServer.shutdown();
        }
    }
}
