/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author BLACKHOLE
 */
public class BrokenLinkServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MalformedURLException {
        
        try(PrintWriter out = response.getWriter()){
            TraceWebsite Trace = new TraceWebsite(request.getParameter("URL"));
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Broken Links</title><style>table,th,td{border:1px solid black}</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Result of Links in the Website :</h1>");
            _TraceWebite("a","href",Trace, response);
            _TraceWebite("img","src",Trace, response);
            _TraceWebite("script","src",Trace, response);
            _TraceWebite("link","href",Trace, response);
            out.println("</body>");
            out.println("</html>");
        
        } catch (Exception ex) {
            Logger.getLogger(BrokenLinkServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
    }
    public void _TraceWebite(String tag, String attribute,TraceWebsite Trace,HttpServletResponse response) throws InterruptedException, IOException{
        Table[] T = Trace.checkBrokenLinks(tag, attribute);  
        
        PrintWriter out = response.getWriter();      
        out.println("<table><tr><th>S.no</th><th>TAG NAME</th><th>ATTRIBUTE NAME</th><th>URL</th><th>RESPONSE MESSAGE</th><th>RESPONSE CODE</th><th>REMARKS</th></tr>");
        for(int i=0;i<T.length;i++){
            out.println("<tr><td>"+(i+1)+"</td><td>"+T[i].TagName+"</td><td>"+T[i].AttributeName+"</td><td>"+T[i].URL+"</td><td>"+T[i].ResponseMessage+"</td><td>"+T[i].ResponseCode+"</td><td>"+T[i].ErrorMessage+"</td></tr>");
        }
        out.println("</table>");

    }

}
