package cydeo.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.sound.midi.SysexMessage;
import java.net.URL;
import java.time.Duration;

/**
 * The {@code Driver} class is a utility for managing WebDriver instances using the Singleton pattern.
 * It provides methods to get a WebDriver instance and close it.
 */
public class Driver {

    // Private constructor to prevent instantiation from outside
    private Driver() {


    }

    // Thread-local variable to hold WebDriver instances
    private static InheritableThreadLocal<WebDriver> driverPool = new InheritableThreadLocal<>();



    public  static   String gridAddress;
    private static URL url;
    private static DesiredCapabilities desiredCapabilities;




    /**
     * Gets the singleton instance of WebDriver. If the instance is not initialized, it initializes it based on the
     * browser type specified in the configuration.
     *
     * @return The WebDriver instance.
     */
    public static WebDriver getDriver() {
//getDriver basiclly gives you a driver like chrome ex
        if (driverPool.get() == null) {
            String browserType = "";


            if (System.getProperty("BROWSER") != null) {
                //if System(command line) does have mvn test -DBROWSER //have sth
                browserType = System.getProperty("BROWSER");
            } else {
                 browserType = ConfigurationReader.getProperty("browser");
            }
            System.out.println("browserType = " + browserType);


            // Initialize WebDriver based on browser type
            switch (browserType) {

///////////////////////////chrome////////////////////////////////////////
                //run in a selenium -grid server
                case "remote-chrome":
                    /**many machine GRID*/
                    try {
                        //Assign grid SERVER address ip,
                        /*for now working but if cydeo will turn off the ip, it will not work, and to check visually how it is running in browser
                          http://54.196.44.18:4444/ui/
                        * */
                        //54.196.44.18 not working
                        //54.162.50.13   //there are 2 nodes
                        //54.237.209.202  //not working
                        gridAddress = "54.162.50.13";
                        url = new URL("http://" + gridAddress + ":4444/wd/hub");
                        desiredCapabilities = new DesiredCapabilities();
                        desiredCapabilities.setBrowserName("chrome");
                        driverPool.set(new RemoteWebDriver(url, desiredCapabilities));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                //run in local machine, without selenium-grid
                case "chrome":
                    /**one machine*/
                    driverPool.set(new ChromeDriver());
                    break;
///////////////////////////////////////////////////////////////////



///////////////////////////firefox////////////////////////////////////////
                //run in a selenium -grid server
                case "remote-firefox":
                    try {
                        //Assign grid SERVER address ip
                        gridAddress = "54.162.50.13";
                        url = new URL("http://" + gridAddress + ":4444/wd/hub");
                        desiredCapabilities = new DesiredCapabilities();
                        desiredCapabilities.setBrowserName("firefox");
                        driverPool.set(new RemoteWebDriver(url, desiredCapabilities));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;


                //run in local machine, without selenium-grid
                case "firefox":
                    /**one machine*/
                    driverPool.set(new FirefoxDriver());
                    break;
///////////////////////////////////////////////////////////////////

//////////////////////////edge/////////////////////////////////////////
                case "edge":
                    /**one machine*/
                    driverPool.set(new EdgeDriver());
                    break;
///////////////////////////////////////////////////////////////////

                case "headless-chrome":
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless=new");
                    driverPool.set(new ChromeDriver(options));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid browser type specified in the configuration: " + browserType);
            }




            // Maximize the browser window and set implicit wait
            driverPool.get().manage().window().maximize();
            driverPool.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        }
            return driverPool.get();
   }





    /**
     * Closes the WebDriver instance and removes it from the thread-local variable.
     * If the instance is not null, it quits the WebDriver.
     */
    public static void closeDriver() {
        if (driverPool.get() != null) {
            driverPool.get().quit(); // Quit the WebDriver instance
            driverPool.remove(); // Remove the WebDriver instance from the thread-local variable
        }
    }




}
