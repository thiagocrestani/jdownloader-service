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

import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

/**
 * @author Dominik Psenner <dpsenner@gmail.com>
 *
 */
public class Server {
    private int port = 8080;
    private WebServer server;
    
    public Server() throws XmlRpcException {
       this(8080);
    }
    
    public Server(int port) throws XmlRpcException {
        this.port = port;
        server = setup(port);
    }
    
    /**
     * configures a webserver with the given configuration.
     * @throws XmlRpcException 
     * 
     */
    private WebServer setup(int port) throws XmlRpcException {
        WebServer webServer = new WebServer(port);
        setupWebServerConfiguration(webServer);
        return webServer;
    }

    /**
     * @param webServer
     * @throws XmlRpcException
     */
    private void setupWebServerConfiguration(WebServer webServer) throws XmlRpcException {
        PropertyHandlerMapping pMapping = createPropertyHandlerMapping();
        webServer.getXmlRpcServer().setHandlerMapping(pMapping);
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) webServer.getXmlRpcServer().getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);
    }

    /**
     * @return
     * @throws XmlRpcException
     */
    private PropertyHandlerMapping createPropertyHandlerMapping() throws XmlRpcException {
        PropertyHandlerMapping pMapping = new PropertyHandlerMapping();
        pMapping.setRequestProcessorFactoryFactory(createFactoryFactory());
        pMapping.setVoidMethodEnabled(true);
        pMapping.addHandler(Service.class.getName(), ServiceImpl.class);
        return pMapping;
    }

    /**
     * @return
     */
    private ServerRequestProcessorFactoryFactory createFactoryFactory() {
        ServiceImpl calculator = new ServiceImpl();
        ServerRequestProcessorFactoryFactory factoryFactory = new ServerRequestProcessorFactoryFactory(calculator);
        return factoryFactory;
    }
    
    /**
     * starts the server and blocks the invoker.
     * @throws IOException
     */
    public void start() throws IOException {
        server.start();
    }
    
    /**
     * retrieves the configured port.
     * @return
     */
    public int getPort() {
        return port;
    }
}
