package org.springframework.web.servlet;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author twogoods
 * @since 2024/11/28
 */
public class AdhesiveServlet {
    public DispatcherServlet dispatcherServlet;

    public AdhesiveServlet(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    public String test() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/echo");
//        request.addParameter("name","ttt");
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        dispatcherServlet.doService(request, response);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/post");
        request.addHeader("name", "ttt");
        request.setContentType("application/json");
        request.setContent("{\"name\":\"sad\",\"age\":1}".getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();
        dispatcherServlet.doService(request, response);
        String res = response.getContentAsString();
        System.out.println(response.getHeaderNames());
        return res;
    }

}
