package com.sgh.demo.general.redirect;//package com.demo.redirect;
//
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author Song gh on 2023/11/27.
// */
//public class redirect {
//    @Override
//    protected void doGet(HttpServletRequest req,
//                         HttpServletResponse resp) throws ServletException, IOException {
//        ServletContext context = this.getServletContext();
//        //设置name=罗德
//        context.setAttribute("name","罗德");
//        //getRequestDispatcher:返回一个RequestDispatcher对象，该对象充当位于给定路径的资源的包装器。
//        //RequestDispatcher对象可用于将请求转发到资源或在响应中包含该资源。
//        //资源可以是动态的，也可以是静态的。 路径名必须以“/”开头，并解释为相对于当前上下文根。
//
//        //forward:将来自servlet的请求转发到服务器上的另一个资源（servlet、JSP文件或HTML文件）。
//        //此方法允许一个servlet对请求进行初步处理，并允许另一个资源生成响应。
//        context.getRequestDispatcher("/gethello").forward(req,resp);
//    }
//
//}
