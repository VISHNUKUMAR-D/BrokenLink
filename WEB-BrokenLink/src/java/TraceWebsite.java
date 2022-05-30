

// Neccessary packages


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
    
    //Reference for the WebDriver 
    public  WebDriver driver;
    
    
    //Contructor for the class TraceWebsite that gets the URL as string parameter when instance for this class is created
    TraceWebsite(String URL){
        
        //Locates the ChromeDriver in the System
        System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver.exe");
        this.driver = new ChromeDriver();
        
        //Maximizes the window
        this.driver.manage().window().maximize();
        this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        
        //Opens the website
        this.driver.get(URL);
    }
    
    
    //Method to trace the links in the website and stores the information about the links in an array of type Table and returns the (Table)array
    public  Table[] checkBrokenLinks(String tag, String attribute) throws InterruptedException,IOException, MalformedURLException{
        
        //List of type WebElement contains all the Elements with given TagName
        List<WebElement> links = driver.findElements(By.tagName(tag));
        
        //Array List to store all activeLinks
        List<WebElement> activeLinks = new ArrayList<>();    
        
        //This will get all the active Links
         for(int i=0; i<links.size(); i++){
            if(!links.get(i).getAttribute(attribute).isEmpty()){
                activeLinks.add(links.get(i));
            }
         }
         
        //A Table array is created with the size of active links
        Table[] t = new Table[activeLinks.size()];
        
        
        //This will iterate the active links one by one
        for(int i=0; i<activeLinks.size(); i++){    
            
           //Gets the link as string using getAttribute method
           String _url = activeLinks.get(i).getAttribute(attribute);         
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
           
           
           //Each element of Table array is assinged with the Table object
            t[i] = new Table();
            
            //Each element gets its respective data about the link
            t[i].AttributeName=attribute;
            t[i].TagName = tag;
            t[i].ResponseCode = responseCode;
            t[i].ResponseMessage = httpConnection.getResponseMessage();
            t[i].URL = _url;
            
           // If the responseCode is equal to 200 then it is a good link when responseCode is greater than 400 than it is broken or bad link
           if(responseCode!=200){
                t[i].AboutLink = "Broken Link";
           }else{
                t[i].AboutLink = "Good Link";
           }
           
           //This disconnects the connected httpConnection
           httpConnection.disconnect();
        }
        
        //Returns the Table array 
        return t;
    }
    
}
