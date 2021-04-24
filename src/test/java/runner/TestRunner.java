package runner;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsNull;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import basedcommon.DataCommons;
import io.github.bonigarcia.wdm.WebDriverManager;
import keywords.BasedMethods;
import untils.ExcelUtils;


public class TestRunner {
	//String filePath, String fileName, String sheetName, int row,	int startColumn, int endColumn
	
	String filepath = System.getProperty("user.dir")+ File.separator + "Test_input";
	String fileName = "TestCaseDemo.xls";
	WebDriver driver=null;
	
	ExtentReports extent ;
	ExtentSparkReporter spark ;
	ExtentTest log;
	String TestCaseName = "";
	BasedMethods action;
	boolean isPassed = true;
	String browserName  ="";
	@BeforeSuite
	public  void beforesuie() {
		File f = new File(System.getProperty("user.dir")+"/my_report");
		if (!f.exists()) {
			f.mkdirs();
		}
		extent = new ExtentReports();
		spark =  new ExtentSparkReporter(
				System.getProperty("user.dir")+ File.separator+"htmlReport"+ File.separator+ "Spark.html");
		extent.attachReporter(spark);
	}
	
	@Test
	public void checkPOI() {
		
		//lay ra danh sach tcs can run
		ArrayList <String> lstTCs = ExcelUtils.readExcelFileAtColumn(filepath, fileName, DataCommons.sheetTestConfig, DataCommons.columnTestCases);
	
		for(int iTCs=1; iTCs<lstTCs.size() ; iTCs++ ) {
			System.out.println(DataCommons.narrow);
			System.out.println("Testcase run " + lstTCs.get(iTCs));
			
			//nhung trinh duyet ma testcase se run
			ArrayList <String> TCs = ExcelUtils.readExcelFileAtRow(filepath, fileName, DataCommons.sheetTestConfig, iTCs, DataCommons.columnFireFox, DataCommons.columnIE);
			//tring filePath, String fileName, String sheetName, int row,	int startColumn, int endColumn) {
		
			for(int j=0; j< TCs.size(); j++) {
				
				String browser = TCs.get(j);	//lay ket qua trinh duyet se run
			
				browserName = ExcelUtils.getDataAtCell(filepath, fileName, DataCommons.sheetTestConfig, 0, j+1);
				
				System.out.println("Browser " +browserName + " run " + TCs.get(j));
				
				if(browser.equalsIgnoreCase(DataCommons.yes)) {
					System.out.println(DataCommons.narrow);
					System.out.println("Start testcase " );

					//thuc thi testcase trong sheet testcase
					ArrayList <String> TestSteps = ExcelUtils.readExcelFileAtColumn(filepath, fileName, lstTCs.get(iTCs), DataCommons.Testcase_Step_COL_NUM);
				
					for (int step = 1 ; step < TestSteps.size() ; step++) {
						//System.out.println("Step " + step + " : " + TestSteps.get(step) );
						ArrayList <String> details = ExcelUtils.readExcelFileAtRow(filepath, fileName, lstTCs.get(iTCs), step, 0, DataCommons.Testcase_ActualResult_COL_NUM);
						//System.out.println(details);
						TestCaseName = details.get(0);
						executeKeyword(TestCaseName, TestSteps.get(step),browserName, details.get(DataCommons.Testcase_ByValue_COL_NUM));
						
						
					}
				}
			}
			
		}
	}

	@AfterMethod
	public void writeTCResult(ITestResult rs) {
		if (rs.getStatus() == ITestResult.SUCCESS && isPassed == true) {
			//log.pass(rs.getName() + " is PASSED");
			log.pass(MarkupHelper.createLabel(TestCaseName + "is PASSED", ExtentColor.GREEN));
		}
		else if (rs.getStatus() == ITestResult.FAILURE || isPassed == false){
			//log.fail(rs.getName() + " is FAILED");
			log.fail(MediaEntityBuilder.createScreenCaptureFromPath(screenshot()).build());
			log.fail(MarkupHelper.createLabel(TestCaseName + "is FAILED", ExtentColor.RED));
			log.fail(rs.getThrowable());
			extent.flush();
		}
		else {
			//log.skip(rs.getName() + "is SKIPPED");
			log.skip(MarkupHelper.createLabel(TestCaseName + "is SKIPPED", ExtentColor.YELLOW));
			log.fail(rs.getThrowable());
		}
	}
	
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
	
	public void executeKeyword(String TestCaseName, String keyword, String Browser,String Value) {
		
		switch (keyword) {
			case "StartTestCase()":
				System.out.println("start test case with browser " + browserName );
				log = extent.createTest(TestCaseName+"_RunWith_"+browserName );
				if (driver != null) {
					driver.close();
				}
				break;
			case "OpenBrowser()":
				System.out.println(" create new browser " + Browser);
				Browser = Browser.toLowerCase();
				
				switch (Browser) {
					case "chrome":
						WebDriverManager.chromedriver().setup();
						driver = new ChromeDriver();
						System.out.println("=== Using Chrome ===");
						break;
					case "ff": case "firefox":
						WebDriverManager.firefoxdriver().setup();
						driver = new FirefoxDriver();
						System.out.println("=== Using FF ===");
						break;
						
					default:
						WebDriverManager.chromedriver().setup();
						driver = new ChromeDriver();
						System.out.println("=== Using Default: Chrome ===");
						break;
					}
				
				driver.manage().window().maximize();
				action = new BasedMethods(driver);
				break;
			case "GotoURL()":
				System.out.println(" got to url" + Value);
				action.goToUrl(Value, log);
				
				break;	
			case "EndTestCase()":
				System.out.println("end test case : save extent report");
				extent.flush();
				
				break;	
			default:
				break;
		}
		
	}



}
