
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseServlet extends HttpServlet {
    
    public PreparedStatement statement=null;
    public Connection connection = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                    
            //Gets the URL for the website from the HTML page
            String URL = request.getParameter("URL");
            
            //Substrings the URL and gets only the Domain Name
            String Domain = URL.substring(URL.indexOf(".")+1,URL.lastIndexOf("."));
            
            //Creates instance for TraceWebiste 
            TraceWebsite Trace = new TraceWebsite(request.getParameter("URL"));

        try{
            //Database Connectivity
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/BrokenLink", "root", "");
            
            //Preparing Statement for Query execution
            
            //Creates table with table name as Domain name and throws exception when the table name already exists
            statement = connection.prepareStatement("CREATE TABLE "+Domain+" (TAG varchar(10),ATTRIBUTE varchar(15),URL text, CODE int, MESSAGE varchar(30),ABOUT varchar(15), REMARKS varchar(15));");
            statement.execute();
            
            //Call _TraceWebite for tracing the link in the website
            _TraceWebite("a","href",Trace, response,Domain,true);
            _TraceWebite("img","src",Trace, response, Domain,true);
            _TraceWebite("script","src",Trace, response, Domain,true);
            _TraceWebite("link","href",Trace, response, Domain,true);
            
            statement.close();
        }
        //When Domain Name already exists then catch block will be executed
        catch(SQLException e){  
            try {
                _TraceWebite("a","href",Trace, response,Domain,false);
                _TraceWebite("img","src",Trace, response, Domain,false);
                _TraceWebite("script","src",Trace, response, Domain,false);
                _TraceWebite("link","href",Trace, response, Domain,false);
                
            } catch (Exception ex) {
            } 
        }catch(Exception ex){}
        finally{
            
            //Finally using RequestDispatcher connection and domain name sent to another BrokenLinkServlet
            request.setAttribute("Connection", connection);
            request.setAttribute("Domain",Domain);
            RequestDispatcher dispatch = request.getRequestDispatcher("BrokenLinkServlet");
            dispatch.forward(request, response);
        }
        
        
        
    }
 public void _TraceWebite(String tag, String attribute, TraceWebsite Trace, HttpServletResponse response, String Domain,boolean isNewDomain) throws InterruptedException, IOException, SQLException{
        
        //Calls the checkBrokenLinks method in TraceWebsite and stores the return value in Table array
        Table[] T = Trace.checkBrokenLinks(tag, attribute); 
        
        //When it is new Domain then it collects the data about the website and then stores in the DB
        if(isNewDomain){
            for(int i=0;i<T.length;i++){
            statement = connection.prepareStatement("INSERT INTO "+Domain+" VALUES (?,?,?,?,?,?,?)");
            statement.setString(1, T[i].TagName);
            statement.setString(2, T[i].AttributeName);
            statement.setString(3, T[i].URL);
            statement.setInt(4, T[i].ResponseCode);
            statement.setString(5, T[i].ResponseMessage);
            statement.setString(6, T[i].ErrorMessage);
            statement.setString(7, "New Link");
            statement.executeUpdate();
        }
        }
        //When Domain already exists else will be executed
        else{
            try{
                for(int i=0; i<T.length; i++){
                PrintWriter out = response.getWriter();
                statement = connection.prepareStatement("SELECT * FROM "+Domain+" WHERE URL LIKE ?");
                statement.setString(1, T[i].URL);
                
                //Checks whether the link already exists in the DB
                if(statement.execute()){
                    statement = connection.prepareStatement("SELECT ? FROM "+Domain+" WHERE CODE LIKE ?");
                    statement.setString(1,T[i].URL);
                    statement.setInt(2, 200);
                    boolean NotLike = (statement.execute());
                    
                    //Checks if the link's code changed to 200 or not
                    //If yes it updates the response code 
                    if((!NotLike)&&(T[i].ResponseCode==200)){
                        statement = connection.prepareStatement("UPDATE "+Domain+" SET CODE= ? ,MESSAGE= ?  ,ABOUT= ? REMARKS = ? WHERE URL = ?");
                        statement.setInt(1,T[i].ResponseCode);
                        statement.setString(2,T[i].ResponseMessage);
                        statement.setString(3,T[i].ErrorMessage);
                        statement.setString(4,"Resolved");
                        statement.setString(5,T[i].URL);
                        statement.executeUpdate();
                    }
                    //Else If, the link that already exists, changed to broken link than it is updated
                    else if((NotLike)&&(T[i].ResponseCode!=200)){
                        statement = connection.prepareStatement("UPDATE "+Domain+" SET CODE= ? ,MESSAGE= ?  ,ABOUT= ? REMARKS = ? WHERE URL = ?");
                        statement.setInt(1,T[i].ResponseCode);
                        statement.setString(2,T[i].ResponseMessage);
                        statement.setString(3,T[i].ErrorMessage);
                        statement.setString(4,"Newly Broken");
                        statement.setString(5,T[i].URL);
                        statement.executeUpdate();

                    }
                    //Else if the link unchanged than it updates unchanged
                    else if((NotLike)&&(T[i].ResponseCode==200)){
                        statement = connection.prepareStatement("UPDATE "+Domain+" SET REMARKS = ? WHERE URL = ?");
                        statement.setString(1,"Unchanged");
                        statement.setString(2, T[i].URL);
                        statement.executeUpdate();

                    }
                }
                //If a link appears new than it is added to the DB
                else{
                    statement = connection.prepareStatement("INSERT INTO "+Domain+" VALUES (?,?,?,?,?,?)");
                    statement.setString(1, T[i].TagName);
                    statement.setString(2, T[i].AttributeName);
                    statement.setString(3, T[i].URL);
                    statement.setInt(4, T[i].ResponseCode);
                    statement.setString(5, T[i].ResponseMessage);
                    statement.setString(6, T[i].ErrorMessage);
                    statement.setString(7, "Newly Added");
                    statement.executeUpdate();

                }
            }
            }   catch(Exception e){
                    }
            
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
