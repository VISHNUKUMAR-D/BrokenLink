import java.net.URL;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.openqa.selenium.By;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import java.net.MalformedURLException;
import org.openqa.selenium.WebElement;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.ServletException;
import org.openqa.selenium.chrome.ChromeDriver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ResultTableServlet extends HttpServlet {
    
    //doPost methos used to post the content in the client side
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MalformedURLException {
        Connection connection = (Connection) request.getAttribute("Connection");
        String Domain =(String) request.getAttribute("Domain");
        try(PrintWriter out = response.getWriter()){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM "+Domain);
            ResultSet set = statement.executeQuery();
            //Create instance for TraceWebsite and pass the URL as a Constructor's parameter
            response.setContentType("text/html");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Broken Links</title><style>table,th,td{border:1px solid black}</style>");
            out.println("<link rel= 'stylesheet' href='Bootstrap/css/bootstrap.css'>");
            out.println("<link rel= 'stylesheet' href='Bootstrap/css/bootstrap.min.css'>");
            out.println("<link rel= 'stylesheet' href='Bootstrap/js/bootstrap.min.js'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<br><div class='container'><div class='card'><div class='card-header' style='text-align:center'><h3>Result of Links in the Website</h3></div></div></div><br>");
            out.println("<div class='container'><div class='card'><br><table class='table-responsive'><thead class='thead-dark'><tr><th>S.no</th><th>TAGE NAME</th><th>ATTRIBUTE NAME</th><th width='250px'>URL</th><th>RESPONSE MESSAGE</th><th>RESPONSE CODE</th><th>ABOUT LINK</th><th>REMARKS</th></tr></thead><tbody>");
            int i=0;
            while(set.next()){
                if(set.getInt("code")==200)
                    out.println("<tr class='success'><td>"+(++i)+"</td><td>"+set.getString("TAG")+"</td><td>"+set.getString("ATTRIBUTE")+"</td><td width='250px'><a href='"+set.getString("URL")+"' class="+"link-primary"+">"+set.getString("URL")+"</a></td><td>"+set.getString("MESSAGE")+"</td><td>"+set.getString("CODE")+"</td><td>"+set.getString("ABOUT")+"</td><td>"+set.getString("REMARKS")+"</td></tr>");
                else
                    out.println("<tr class='danger'><td>"+(++i)+"</td><td>"+set.getString("TAG")+"</td><td>"+set.getString("ATTRIBUTE")+"<td width='250px'>"+set.getString("URL")+"</td><td>"+set.getString("MESSAGE")+"</td><td>"+set.getString("CODE")+"</td><td>"+set.getString("ABOUT")+"</td><td>"+set.getString("REMARKS")+"</td></tr>");

            }
        out.println("</tbody></table></div></div><br><br>");
            out.println("</body>");
            out.println("</html>");
        
        } catch (Exception ex) {
            Logger.getLogger(ResultTableServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
    }
}
