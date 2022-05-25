
package brokenlink;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class BrokenLink {


    public static void main(String[] args) throws InterruptedException, MalformedURLException, IOException {
        Scanner scan = new Scanner(System.in);
        System.out.print("ENTER THE WEBSITE LINK : ");
        String url = scan.next();
        
        System.setProperty("webdriver.chrome.driver", "C:\\Driver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        
        
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url);
        
        //Waits for 5 seconds to get into the url given
        Thread.sleep(5000);
        
        //findElements used to get the WebElement by Tag Name "A"
        checkBrokenLinks("a","href",driver);
        //Now getting the WebElements with Tag Name "IMG"
        checkBrokenLinks("img","src",driver);
        //Now getting the WebElements with Tag Name "SCRIPT"
        checkBrokenLinks("script","src",driver);
        //Now getting the WebElements with Tag Name "LINK"
        checkBrokenLinks("link","href",driver);


        
    }
    public static void checkBrokenLinks(String tag,String attribute,WebDriver driver) throws InterruptedException,IOException, MalformedURLException{
        List<WebElement> links = driver.findElements(By.tagName(tag));
        List<WebElement> activeLinks = new ArrayList<>();
        

        //This prints the total number of LINKS available in the website given
        System.out.println("\n\nTotal Number of Links with tag '"+tag+"' : "+links.size()+"\n\n");
        
        //Filter only active Links which means the tag should contain the link it should not be empty
         for(int i=0; i<links.size(); i++){
            if(!links.get(i).getAttribute(attribute).isEmpty()){
                activeLinks.add(links.get(i));
            }
         }
         
        System.out.println("Total Number of Active Links with tag '"+tag+"' : "+activeLinks.size()+"\n\n");
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
           
           
           // If the responseCode is equal to 200 then it is a good link when responseCode is greater than 400 than it is broken or bad link
           if(responseCode>=400){
               System.err.println(_url+" - IS "+httpConnection.getResponseMessage()+" WITH RESPONSE CODE : "+responseCode);
           }else{
               System.out.println(_url+" - "+"IS "+httpConnection.getResponseMessage()+" WITH RESPONSE CODE : "+responseCode);
              
           }
           
           //This disconnects the connected httpConnection
           httpConnection.disconnect();
        }
    }
}
