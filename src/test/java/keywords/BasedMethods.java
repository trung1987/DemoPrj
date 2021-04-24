package keywords;

import java.io.File;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.model.Media;

public class BasedMethods {
	
	WebDriver driver; //local driver : get from objclass <-> get testcase class <-> based test

	SoftAssert softAss = new SoftAssert();

	public BasedMethods(WebDriver globalDriver) {
		driver = globalDriver;
	}
	
	protected void inputText(String elementXpath, String sValue, ExtentTest log) {
		WebElement element = driver.findElement(By.xpath(elementXpath));
		element.clear();
		element.sendKeys(sValue);
		System.out.println("Enter " + sValue + " successfully");
		log.info("Enter " + sValue + " successfully");
		
		enableHighlight(element);
		log.info(MediaEntityBuilder.createScreenCaptureFromPath(screenshot()).build());
		disableHighLight(element);
		
	}

	protected void clickElement(String elementXpath, ExtentTest log) {
		WebElement element = driver.findElement(By.xpath(elementXpath));
		System.out.println("Click on " + checkEmpty(element));
		log.info("Click on " + checkEmpty(element));
		
		enableHighlight(element);
		log.info(MediaEntityBuilder.createScreenCaptureFromPath(screenshot()).build());
		disableHighLight(element);
		
		element.click();
	}

	public String getText(String elementXpath, ExtentTest log) {
		WebElement element = driver.findElement(By.xpath(elementXpath));
		log.info("Get text: " + element.getText());
		return element.getText();
	}

	public void goToUrl(String url, ExtentTest log) {
		driver.get(url);
		System.out.println("Go to: " + url);
		log.info("Go to url: " + url);
	}
	
	// Screenshot method
	protected String screenshot() {
		int rd = new Random().nextInt();
		String imgName = "screenshot" + rd + ".png";
		String imgPath = System.getProperty("user.dir") + File.separator + "HtmlReport" + File.separator + imgName;

		File scrsht = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		try {
			FileUtils.copyFile(scrsht, new File(imgPath));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "." + File.separator + imgName;
	}
	
	// Enable highlight method
	protected void enableHighlight(WebElement element) {
		if (driver instanceof JavascriptExecutor) {
			((JavascriptExecutor) driver).executeScript("arguments[0].style='border: 5px solid red;'", element);
		}

		//JavascriptExecutor js = (JavascriptExecutor) driver;
		//js.executeScript("arguments[0].style='border: 5px sloix red;'", element);
	}

	// Disable highlight method
	protected void disableHighLight(WebElement element) {
		if (driver instanceof JavascriptExecutor) {
			((JavascriptExecutor) driver).executeScript("arguments[0].style='border: none'", element);
		}
	}
	
	// check empty name
	private String checkEmpty(WebElement element) {
		String sElementName = element.getText();
		if (sElementName.isEmpty() == true) {
			sElementName = element.getAttribute("class");
		}
		return sElementName;
	}
	
	/*
	protected WebElement findEle(String locator, String value) {
		WebElement element = null;
		switch (locator) {
		case "id":
			element = driver.findElement(By.id(value));
			break;
		case "name":
			element = driver.findElement(By.name(value));
			break;
		default:
			element = driver.findElement(By.xpath(value));
			break;
		}
		return element;
	}
	
	protected void demo(String locator, String elementXpath, ExtentTest log) {
		WebElement element = findEle(locator, elementXpath);
	}
	*/
}
