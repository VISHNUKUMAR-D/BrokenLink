
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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


public class TraceWebsite {
    public  WebDriver driver;
    TraceWebsite(String URL){
        System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver.exe");
            this.driver = new ChromeDriver();
            this.driver.manage().window().maximize();
            this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            this.driver.get(URL);
    }
    
    public  Table[] checkBrokenLinks(String tag, String attribute) throws InterruptedException,IOException, MalformedURLException{
        List<WebElement> links = driver.findElements(By.tagName(tag));
        List<WebElement> activeLinks = new ArrayList<>();     
        //This prints the total number of LINKS available in the website given
        //System.out.println("\n\nTotal Number of Links with tag '"+tag+"' : "+links.size()+"\n\n");
        //System.out.println();
        //Filter only active Links which means the tag should contain the link it should not be empty
         for(int i=0; i<links.size(); i++){
            if(!links.get(i).getAttribute(attribute).isEmpty()){
                activeLinks.add(links.get(i));
            }
         }
        Table[] t = new Table[activeLinks.size()];
        
        //System.out.println("Total Number of Active Links with tag '"+tag+"' : "+activeLinks.size()+"\n\n");
        for(int i=0; i<activeLinks.size(); i++){
            
           //Getting the links from the WebElements
           String _url = activeLinks.get(i).getAttribute(attribute);
           
           //Passing it as arguements to the class URL
           URL link = new URL(_url);
           
           //HttpURLConnection is a abstract class for which object cannot be created
           
           //URLConnection urlconnection = link.openConnection(); or
           
           //HttpURLConnection is used to openConnection for HTTP while HttpsURLConnection is used to openConnection for HTTPS
           
           HttpURLConnection httpConnection =(HttpURLConnection) link.openConnection();
           
           //Here program execution waits for 2 seconds in order to avoid TimoutException when there is low internet connection
           httpConnection.setConnectTimeout(2000);
           
           //This connects the link to the server
           httpConnection.connect();
           
           //Gets the ResponseCode for the httpConnection
           int responseCode = httpConnection.getResponseCode();
           
            t[i] = new Table();
           // If the responseCode is equal to 200 then it is a good link when responseCode is greater than 400 than it is broken or bad link
            t[i].AttributeName=attribute;
            t[i].TagName = tag;
            t[i].ResponseCode = responseCode;
            t[i].ResponseMessage = httpConnection.getResponseMessage();
            t[i].URL = _url;
           if(responseCode>=400){
                t[i].ErrorMessage = "Error";
           }else{
                t[i].ErrorMessage = "Good";
           }
           
           //This disconnects the connected httpConnection
           httpConnection.disconnect();
        }
        return t;
    }
    
}
