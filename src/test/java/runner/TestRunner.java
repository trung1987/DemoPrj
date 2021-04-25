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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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
	BasedMethods actions;
	
	String sCurrentSheet="";
	String TestCaseName = "";
	String browserName  ="";
	int col =0;
	int row=0;
	
	//log
	ExtentReports extent;	// dang ky dung extent report: quan ly kq, tao new testcase rp...
	ExtentSparkReporter spark; // tao moi file html
	ExtentTest log; // ghi log
	
	
	
	@BeforeTest // 1. chay truoc va co id driver
	public void setUp() {
		// khoi tao report
		extent = new ExtentReports();
		spark = new ExtentSparkReporter(System.getProperty("user.dir")
				+ File.separator + "HtmlReport" + File.separator + "testReport.html");
		
		extent.attachReporter(spark); // thong bao park ket qua report
	}
	
	
	@AfterMethod
	public void writeTCResult(ITestResult rs) {
		if (rs.getStatus() == ITestResult.SUCCESS ) {
			//log.pass(rs.getName() + " is PASSED");
			log.pass(MarkupHelper.createLabel(TestCaseName+ "is PASSED", ExtentColor.GREEN));
		}
		else if (rs.getStatus() == ITestResult.FAILURE ){
			//log.fail(rs.getName() + " is FAILED");
			log.fail(MediaEntityBuilder.createScreenCaptureFromPath(screenshot()).build());
			log.fail(MarkupHelper.createLabel(TestCaseName + "is FAILED", ExtentColor.RED));
			log.fail(rs.getThrowable());
			extent.flush();
			ExcelUtils.writeData(filepath, fileName, sCurrentSheet,"Failed" , row, col);
			driver.close();
		}
		else {
			//log.skip(rs.getName() + "is SKIPPED");
			log.skip(MarkupHelper.createLabel(TestCaseName + "is SKIPPED", ExtentColor.YELLOW));
			log.fail(rs.getThrowable());
		}
	}
	
	
	
	@Test
	public void checkPOI() {
		
		//lay ra danh sach tcs can run
		ArrayList <String> lstTCs = ExcelUtils.readExcelFileAtColumn(filepath, fileName, DataCommons.sheetTestConfig, DataCommons.columnTestCases);
	
		for(int iTCs=1; iTCs<lstTCs.size() ; iTCs++ ) {
			System.out.println(DataCommons.narrow);
			System.out.println("Testcase run " + lstTCs.get(iTCs));
			sCurrentSheet = lstTCs.get(iTCs);
			
			//nhung trinh duyet ma testcase se run
			ArrayList <String> TCs = ExcelUtils.readExcelFileAtRow(filepath, fileName, DataCommons.sheetTestConfig, iTCs, DataCommons.columnFireFox, DataCommons.columnIE);
			//tring filePath, String fileName, String sheetName, int row,	int startColumn, int endColumn) {
		
			for(int j=0; j< TCs.size(); j++) {
				
				String browser = TCs.get(j);	//lay ket qua trinh duyet se run
			
				browserName = ExcelUtils.getDataAtCell(filepath, fileName, DataCommons.sheetTestConfig, 0, j+1);
				browserName = browserName.toLowerCase();
				System.out.println("Browser " +browserName + " run " + TCs.get(j));
				
				if(browser.equalsIgnoreCase(DataCommons.yes)) {
					System.out.println(DataCommons.narrow);
					System.out.println("Start testcase " );
					
					
					//thuc thi testcase trong sheet testcase
					ArrayList <String> TestSteps = ExcelUtils.readExcelFileAtColumn(filepath, fileName, sCurrentSheet, DataCommons.Testcase_Step_COL_NUM);
				
					for (row = 1 ; row < TestSteps.size() ; row++) {
						//System.out.println("Step " + step + " : " + TestSteps.get(step) );
						ArrayList <String> details = ExcelUtils.readExcelFileAtRow(filepath, fileName, sCurrentSheet, row, 0, DataCommons.Testcase_ActualResult_COL_NUM);
						//System.out.println(details);
						TestCaseName = details.get(0);
						String teststep = TestSteps.get(row);
						String xpath = details.get(DataCommons.Testcase_FindElement_COL_NUM);
						String value = details.get(DataCommons.Testcase_ByValue_COL_NUM);
										
						
						executeKeyword(TestCaseName, teststep, xpath,value);
						
						
					}
				}
			}
			
		}
	}

	
	public void executeKeyword(String TestCaseName, String keyword, String xpath, String Value) {
		
		switch (keyword) {
			case "StartTestCase()":
				System.out.println("start test case with browser " + browserName );
				log = extent.createTest(TestCaseName + "_run with_"+ browserName);
				break;
			case "OpenBrowser()":
				System.out.println(" create new browser " + browserName);
				
				switch (browserName) {
					case "chrome":
						WebDriverManager.chromedriver().setup();
						driver = new ChromeDriver();
						System.out.println("=== Using Chrome ===");
						col = DataCommons.Testcase_ResultChrome_COL_NUM; // lay duoc col result browser dang chay
						break;
					case "ff": case "firefox":
						WebDriverManager.firefoxdriver().setup();
						driver = new FirefoxDriver();
						System.out.println("=== Using FF ===");
						col = DataCommons.Testcase_ResultFF_COL_NUM;
						break;
					default:
						WebDriverManager.chromedriver().setup();
						driver = new ChromeDriver();
						System.out.println("=== Using Default: Chrome ===");
						col = DataCommons.Testcase_ResultIE_COL_NUM;
						break;
					}
				
				driver.manage().window().maximize();
				actions = new BasedMethods(driver);
				//basemethod
				log.info("Open browser " + browserName);  //html report
				
				//log xuong excel
				ExcelUtils.writeData(filepath, fileName, sCurrentSheet,"Passed" , row, col);
				
				break;
			case "GotoURL(Value)":
				actions.goToUrl(Value, log);
				ExcelUtils.writeData(filepath, fileName, sCurrentSheet, "Passed", row, col);
				
				break;	
				
				
			case "EnterText(FindElementBy,Value)":
				actions.inputText(xpath, Value, log);
				break;
		
				
			case "EndTestCase()":
				System.out.println("end test case : save extent report");
				extent.flush();
				driver.close();
				break;	
			default:
				break;
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

}
