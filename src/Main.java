import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        String [] inputNames = {"user_name", "user_password"};
        String [] inputs = {"trevor", "trev8790"};
        String url = "http://pcairnoise.com/bayonne/admin/?ss=fieldreport&id=1303&arean=3&paraml=L1&lookupdate=2018-02-19";

        double [] data = readCSVFile("/home/trevorsimpkin/IdeaProjects/RobotNCO/test.rnd","2017/09/28 09:40:00",2);
        /*for (int i = 0; i < data.length; i++) {
            System.out.println(data[i]);
        }*/
        fillOutForm(inputNames, inputs, url, data);
    }

    public static void fillOutForm(String inputNames [], String inputs[], String url, double [] data) {
        String exePath = "/home/trevorsimpkin/IdeaProjects/RobotNCO/selenium-java-3.9.1/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        login(driver);
        driver.get(url);
        int id = 1;
        WebElement element=driver.findElement(By.xpath("//input[@name='sitenumber" + id + "']"));
        String value = element.getAttribute("value");

        //Get first open line in report
        while (!value.equals("")) {
            id++;
            element=driver.findElement(By.xpath("//input[@name='sitenumber" + id + "']"));
            value = element.getAttribute("value");
        }
        for (int j = 0; j <data.length; j+=2) {
            WebElement siteNumElement=driver.findElement(By.xpath("//input[@name='sitenumber" + id + "']"));
            siteNumElement.sendKeys("1");
            siteNumElement.sendKeys(Keys.TAB);
            siteNumElement.click();
            WebElement dropdownLink = driver.findElement(By.cssSelector("#main > div.row > div > form:nth-child(14) > table:nth-child(3) > tbody > tr:nth-child(" + (id +1) + ") > td:nth-child(2) > div > a.current"));
            dropdownLink.click();
            Select dropdown = new Select (driver.findElement(By.xpath("//select[@name='monitoringlocation" + id + "']")));;
            dropdown.selectByVisibleText("85 Newark Ave");
            //dropdown.sendKeys("85 Newark Ave");
            WebElement startTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodstart" + id + "']"));
            startTimeElement.sendKeys("09:00");
            WebElement endTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodend" + id + "']"));
            endTimeElement.sendKeys("09:09");
            WebElement leqElement=driver.findElement(By.xpath("//input[@name='locationleq" + id + "']"));
            leqElement.sendKeys(""+data[j]+"-"+data[j+1]);
            id++;

        }
    }
    public static void login(WebDriver driver) {
        WebElement usernameElement=driver.findElement(By.xpath("//input[@name='user_name']"));
        usernameElement.sendKeys("trevor");
        WebElement passwordElement=driver.findElement(By.xpath("//input[@name='user_password']"));
        passwordElement.sendKeys("trev8790");
        WebElement button=driver.findElement(By.xpath("//input[@name='submit']"));
        button.click();
    }
    public static double [] readCSVFile(String csvFile, String startTime, int intervals) {
        BufferedReader br = null;
        String line = "";
        int i = 0;
        double min;
        double max;
        double temp;
        boolean startReached = false;
        double [] dataToPrint = new double[2*intervals];
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while (!startReached&&(line = br.readLine())!=null) {
                String [] lineData = line.split(",");
                startReached = lineData[1].equals(startTime);
            }
            while (line!=null&&i<intervals) {
                String [] data = line.split(",");
                min = max = Double.parseDouble(data[5]);
                for (int j = 1; j < 10; j++) {
                    line = br.readLine();
                    data = line.split(",");
                    temp = Double.parseDouble(data[5]);
                    if(temp<min) {
                        min = temp;
                    }
                    else if(temp>max) {
                        max = temp;
                    }
                }
                dataToPrint[2*i] = min;
                dataToPrint[(2*i)+1] = max;
                i++;
                line = br.readLine();
            };
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataToPrint;
    }


}
