import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {

    public static void main(String[] args) {
        String [] inputNames = {"emailid"};
        String [] inputs = {"abc@gmail.com"};
        double [] data = readCSVFile("/home/trevorsimpkin/IdeaProjects/RobotNCO/test.rnd","2017/09/28 09:40:00",2);
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i]);
        }
        //fillOutForm(inputNames, inputs, "http://demo.guru99.com/");
    }

    public static void fillOutForm(String inputNames [], String inputs[], String url) {
        String exePath = "/home/trevorsimpkin/IdeaProjects/RobotNCO/selenium-java-3.9.1/chromedriver";
        System.setProperty("webdriver.chrome.driver", exePath);
        WebDriver driver = new ChromeDriver();
        driver.get("http://demo.guru99.com/");
        for (int i =0; i < inputNames.length; i++) {
            WebElement element=driver.findElement(By.xpath("//input[@name='" + inputNames[i] + "']"));
            element.sendKeys(inputs[i]);
        }


        WebElement button=driver.findElement(By.xpath("//input[@name='btnLogin']"));
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
