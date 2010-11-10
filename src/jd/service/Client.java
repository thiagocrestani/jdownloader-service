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

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

/**
 * @author Dominik Psenner <dpsenner@gmail.com>
 *
 */
public class Client {
    private int port = 8080;
    private Thread worker; 
    
    public Client() {
        this(8080);
    }
    
    public Client(int port) {
        worker = setup(port);
    }
    
    private Thread setup(final int port) {
        Thread clt = new Thread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            public void run() {
                while(true) {
                    try {
                        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                        config.setServerURL(new URL("http", "localhost", port, ""));
                        
                        XmlRpcClient xmlrpcClient = new XmlRpcClient();
                        xmlrpcClient.setConfig(config);
                        
                        ClientFactory factory = new ClientFactory(xmlrpcClient);
                        
                        Calculator proxy = (Calculator) factory.newInstance(Calculator.class);
                        
                        // print result
                        System.out.println(proxy.calls());
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
        
        return clt;
    }
    
    public void start() {
        worker.start();
    }
    
    public int getPort() {
        return port;
    }
}
