
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
//        
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url);

        Thread.sleep(5000);
        List<WebElement> links = driver.findElements(By.tagName("a"));
        System.out.println(links.size());
        
        for(int i=0; i<links.size(); i++){
           WebElement element = links.get(i);
           String _url = element.getAttribute("href");
           URL link = new URL(_url);
           HttpURLConnection httpConnection =(HttpURLConnection) link.openConnection();
           Thread.sleep(2000);
           httpConnection.connect();
           int responseCode = httpConnection.getResponseCode();
           if(responseCode>=400){
               System.out.println(_url+" - "+"IS A BROKEN LINK WITH RESPONSE CODE "+responseCode);
           }else{
               System.out.println(_url+" - "+"IS A VALID LINK WITH RESPONSE CODE "+responseCode);
           }
        }
    }
    
}
