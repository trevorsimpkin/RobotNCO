import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        String url = "http://pcairnoise.com/bayonne/admin/?ss=fieldreport&id=1303&arean=3&paraml=L1&lookupdate=2018-02-19";
        String [] inputs = {"1", "85 Newark Ave", "Generator, manlift, hand tools. Acoustic noise blankets lining the eastern and western perimeters of the work site.","Light traffic on Newark Avenue."};
        int intervals = 8;
        String [] times = new String [intervals*2];
        double [] data = readCSVFile("/home/trevorsimpkin/IdeaProjects/RobotNCO/first.rnd","2018/02/20 10:30:00",intervals, times);
        /*for (int i = 0; i < data.length; i++) {
            System.out.println(times[i]);
            System.out.println(data[i]);
        }*/
        fillOutForm(url, data, times, inputs);
    }

    public static void fillOutForm(String url, double [] data, String [] times, String [] input) {
        String exePath = "/home/trevorsimpkin/IdeaProjects/RobotNCO/selenium-java-3.9.1/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        login(driver);
        driver.get(url);
        WebElement moreLinesLink = driver.findElement(By.linkText("Show more lines..."));
        //*[@id="main"]/div[1]/div/form[2]/table[1]/tbody/tr[21]/td/a
        moreLinesLink.click();
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
            siteNumElement.sendKeys(input[0]);
            WebElement dropdownLink = driver.findElement(By.cssSelector("#main > div.row > div > form:nth-child(14) > table:nth-child(3) > tbody > tr:nth-child(" + (id +1) + ") > td:nth-child(2) > div > a.current"));
            dropdownLink.click();
            WebElement dropdown= driver.findElement(By.cssSelector("#main > div.row > div > form:nth-child(14) > table:nth-child(3) > tbody > tr:nth-child("+(id+1)+") > td:nth-child(2) > div > ul > li:nth-child(19)"));
            dropdown.click();
            //Select dropdown = new Select (driver.findElement(By.xpath("//select[@name='monitoringlocation" + id + "']")));;
            //dropdown.selectByVisibleText(input[1]);
            //System.out.println(input[1]);
            //dropdown.sendKeys("85 Newark Ave");
            WebElement startTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodstart" + id + "']"));
            startTimeElement.sendKeys(times[j]);
            WebElement endTimeElement=driver.findElement(By.xpath("//input[@name='locationtimeperiodend" + id + "']"));
            endTimeElement.sendKeys(times[j+1]);
            WebElement leqElement=driver.findElement(By.xpath("//input[@name='locationleq" + id + "']"));
            leqElement.sendKeys(""+data[j]+"-"+data[j+1]);
            WebElement projNotationElement = driver.findElement(By.xpath("//textarea[@name='notations" + id + "']"));
            projNotationElement.sendKeys(input[2]);
            WebElement nonProjNotationElement = driver.findElement(By.xpath("//textarea[@name='extrafield2" + id + "']"));
            nonProjNotationElement.sendKeys(input[3]);
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
    public static double [] readCSVFile(String csvFile, String startTime, int intervals, String [] times) {
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
                times[2*i]=data[1].substring(11,16);
                times[(2*i)+1]=times[2*i].substring(0,4) + "9";
                for (int j = 1; j < 10; j++) {
                    line = br.readLine();
                    data = line.split(",");
                    temp = Double.parseDouble(data[5]);
                    if (temp < min) {
                        min = temp;
                    } else if (temp > max) {
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
