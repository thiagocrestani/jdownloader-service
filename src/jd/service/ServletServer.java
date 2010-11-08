/**
 * 
 */
package jd.service;

import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class ServletServer {
    private static final int port = 8080;

    public static void main(String[] args) throws Exception {
        XmlRpcServlet servlet = new XmlRpcServlet();
        ServletWebServer webServer = new ServletWebServer(servlet, port);
        webServer.start();
    }
}
