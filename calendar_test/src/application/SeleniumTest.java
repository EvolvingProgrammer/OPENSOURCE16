package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
/*
2021-05-16 수정본

각 리스트에 저장되는 데이터들은 각각 아래와 같은 변수로 읽어올 수 있음.

강의 데이터 (별도의 수강일이 강의 이름에 명시되어 있지 않은경우 주차별로만 분류하도록 함. 이러한 경우에 시작일, 시작시간, 마감일, 마감시간은 각각 해당 주차의 월요일, 0:00, 일요일, 0:00으로 셋팅되어짐.)

Week  |  title  |  start_date  |  start_time  |  deadline_date  |  deadline_time  |  subject
---------------------------------------------------------------------------------------------
주차	  | 강의 주제   |  수강 시작일	   |  수강 시작 시간    |  강의 출석 마감일       |  강의 출석 마감 시간   |  과목명


과제 데이터

assignment_name  |  deadline_date  |  deadline_time  |  subject
------------------------------------------------------------------
과제명			 |  마감일		   |  마감 시간		 |  과목명


학사 일정 데이터

event | start_year | start_month | start_day | end_year | end_month | end_day
------------------------------------------------------------------------------
행사명   | 시작년도	   | 시작월	 	 | 시작일		 | 마감년도	| 마감월		| 마감일
*/


interface DataSet {
	void saveData(String s);
	void printData();
};

public class SeleniumTest {
	
	
	
	//data_list
	public static ArrayList<CourseDataSet> courseList = new ArrayList<>();
	public static LinkedList<LessonDataSet> lessonList = new LinkedList<>();
	public static LinkedList<AssignmentDataSet> assignmentList = new LinkedList<>();
	public static LinkedList<AcademicDataSet> academicList = new LinkedList<>();
	
	//블랙보드 로그인 쿠키
	Set<Cookie> cookieL;
	
	//개강일 정보
	public String semesterStartDate = null;
	
	//WebDriver
	private WebDriver driver;
    private WebElement webElement;
    
	//Properties
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static final String WEB_DRIVER_PATH = "./chromedriver.exe";
    
	//Start URL
	private String blackboard_url;
	private String academic_url;

	public SeleniumTest() {
		super();
		//System Property SetUp
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

		blackboard_url = "https://portal.sejong.ac.kr/jsp/login/loginSSL.jsp?rtUrl=blackboard.sejong.ac.kr";
		academic_url = "http://www.sejong.ac.kr/unilife/program_01.html";     
    }    
	
//BlackBoard 데이터 파싱
	public void blackBoardCrawl() {
		try {
			//Driver SetUp
			ChromeOptions chromeOptions = new ChromeOptions();
			driver = new ChromeDriver(chromeOptions);
			
			//get page
			driver.get(blackboard_url);
			//로그인 대기 및 캘린더 항목으로 이동
			while(true) {
				try {
					driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    				while(true) {
        				if(driver.getCurrentUrl().equals("https://blackboard.sejong.ac.kr/ultra/course")) {
        					cookieL = driver.manage().getCookies();        					
        					Thread.sleep(2000);
        					break;
        				}
        				else {
        					Thread.sleep(6000);
        				}
    				}
    				break;
    			} catch(WebDriverException e) {
    				driver.quit();
    				return;
    			} catch(Exception e) {
    				//driver.switchTo().alert().accept();
    			}
			}
			
			//코스 정보 저장
    		Integer courseNum = new Integer(2);
    		while(true) {
    			try {
            		String code_xpath = "//*[@id=\'course-columns-current\']/div/div[" + courseNum.toString() + "]/bb-base-course-card/div";
            		webElement = driver.findElement(By.xpath(code_xpath));
            		courseList.add(new CourseDataSet(webElement.getAttribute("data-course-id")));
            		courseNum++;
    			} catch(Exception e) {
    				break;
    			}
    		}
    		
    		//이전 드라이버에서 로그인 후, 새로운 드라이버에 쿠키 추가
    		driver.quit();
    		chromeOptions.addArguments("--headless");
    		driver = new ChromeDriver(chromeOptions);
			driver.get("https://blackboard.sejong.ac.kr/ultra/course");
			for(Cookie temp_cookie : cookieL ){
				driver.manage().addCookie(temp_cookie);
			}
			
    		//강의 일정 데이터 파싱
    		for(int i = 0 ; i < courseList.size() ; i++) {
    			Integer num = new Integer(0);
    			String courseLink = "https://blackboard.sejong.ac.kr/webapps/blackboard/content/launchLink.jsp?course_id=" + courseList.get(i).course_code + "&tool_id=_2565_1&tool_type=TOOL&mode=view&mode=reset";
    			driver.get(courseLink);
    			Thread.sleep(1000);
    			
    			driver.switchTo().frame("contentFrame");
    			try {
    				driver.findElement(By.xpath("//*[@id='listContainer_showAllButton']")).click();
    				Thread.sleep(1000);
    			} catch(Exception e) {
    				
    			}
    			while(true) {
    				try {
    					webElement = driver.findElement(By.xpath("//*[@id='listContainer_row:" + num.toString() + "']/td[1]/span[2]"));
    					String lessonWeek = webElement.getText();
    					webElement = driver.findElement(By.xpath("//*[@id='listContainer_row:" + num.toString() + "']/td[2]/span[2]"));
    					lessonList.add(new LessonDataSet(webElement.getText(), courseList.get(i).course_name, lessonWeek));
    					num++;
        			} catch(Exception e) {
        				break;
        			}
    			}
    			driver.switchTo().defaultContent();
    		}
    		for(LessonDataSet ds : lessonList) {
    			ds.printData();
    		}
    		System.out.println("Course_data loading complete.\n");
    		
    		//캘린더 항목으로 이동
    		driver.get("https://blackboard.sejong.ac.kr/ultra/calendar");
    		Thread.sleep(1500);
    		
    		//마감일 버튼 클릭
    		webElement = driver.findElement(By.id("bb-calendar1-deadline"));
    		webElement.click();
    		Thread.sleep(1000);
    		
    		//과제 마감일 데이터 파싱
    		Integer assignmentNum = new Integer(2);
    		while(true) {
    			try {
    				String date_xpath = "//*[@id='deadlineContainer']/div/div[2]/div/div/div[" + assignmentNum.toString() + "]";
    				webElement = driver.findElement(By.xpath(date_xpath));
    				if((webElement.getText().split("\\n").length) != 2) {
    					AssignmentDataSet ds = new AssignmentDataSet(webElement.getText());
    				}
    				assignmentNum++;
    			} catch(Exception e) {
    				for(AssignmentDataSet ds : assignmentList) {
    	    			ds.printData();
    	    		}
    				System.out.println("deadline_data loading complete.\n");
    				break;
    			}
    		}		
			
		} catch (Exception e) {
			System.out.println("Blackboard_data loading failed");
		} finally {
				driver.quit();
		}
	}
//학사일정 데이터 파싱
	public void academicDateCrawl() {
		try {
			//Driver SetUp
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--headless");
			driver = new ChromeDriver(chromeOptions);
			
			//학사일정 사이트로 이동
	   		driver.get("http://www.sejong.ac.kr/unilife/program_01.html");
	   		
	   		//학사 일정 데이터 파싱
	   		for(Integer i = new Integer(3);i<17;i++) {
	   	    	String data_xpath = "//*[@id='content']/div[" + i.toString() + "]/div[2]";
	   	    	webElement = driver.findElement(By.xpath("//*[@id='content']/div[" + i.toString() + "]/h4"));
	   	    	String month = webElement.getText();
	   	    	webElement = driver.findElement(By.xpath(data_xpath));
	   	    	AcademicDataSet ds = new AcademicDataSet(month, webElement.getText());
	   		}
	   		
	   		for(AcademicDataSet ds : academicList) {
	   			ds.printData();
	   		}
	    	System.out.println("Academic_data loading complete.\n");  	
		} catch(Exception e) {
			System.out.println("Academic_data loading failed");
			e.printStackTrace();
		} finally {
			driver.quit();
		}
	}
//전체 데이터 파싱
	public void crawl() {
		academicDateCrawl();
		blackBoardCrawl();
	}

//코스 데이터
    public class CourseDataSet implements DataSet {
    	private String course_name;
    	private String course_code;
    	CourseDataSet(String cd){
    		this.course_code = cd;
    		saveData(cd);
    	}
    	@Override
    	public void saveData(String s) {
    		String name_xpath = "//*[@id=\'course-link-" + s + "']/h4" ;
    		webElement = driver.findElement(By.xpath(name_xpath));
    		this.course_name = webElement.getText();
    	}
    	@Override
    	public void printData() {
    		
    	}
    	public String getName() {
    		return this.course_name;
    	}
    	public String getCode() {
    		return this.course_code;
    	}
    }
//강의 데이터
    public class LessonDataSet implements DataSet {
    	private String Week;
    	private String title;
    	private String start_date;
    	private String start_time;
    	private String deadline_date;
    	private String deadline_time;
    	private String subject;
    	LessonDataSet(String s, String sub, String wk) throws ParseException{
    		this.Week = wk;
    		this.title = null;
    		this.start_date = null;
    		this.start_time = null;
    		this.deadline_date = null;
    		this.deadline_time = null;
    		this.subject = sub;
    		saveData(s);
    		if(start_date == null) {
    			Calendar start = Calendar.getInstance();
				Calendar deadline = Calendar.getInstance();
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = df.parse(semesterStartDate);
				Calendar semesterSD = Calendar.getInstance();
				semesterSD.setTime(date);
				
				if(semesterSD.get(Calendar.DAY_OF_WEEK) != 2) {
		    		semesterSD.add(Calendar.DATE, -(semesterSD.get(Calendar.DAY_OF_WEEK) - 2));
		    	}
				start.setTime(semesterSD.getTime());
				deadline.setTime(semesterSD.getTime());
				
				try {
					Integer weekNum = Integer.parseInt(wk.replaceAll("[^0-9]",""));
			    	start.add(Calendar.DATE, 7*(weekNum - 1));
			    	deadline.add(Calendar.DATE, 7*weekNum -1);
			    	if(weekNum == 1) {
			    		start.setTime(date);
					}
				} catch(Exception e) { //8주차에 중간고사인 상황에 한정
					start.add(Calendar.DATE, 7*(8-1));
					deadline.add(Calendar.DATE, 7*8);
				}
				start_date = df.format(start.getTime());
		    	start_time = "00:00";
		    	deadline_date = df.format(deadline.getTime());
		    	deadline_time = "00:00";
    		}
    	}
    	@Override
    	public void saveData(String s) {   		
			if((s.split(" "))[0].equals("XIN")) {
				this.title = (s.split("/"))[0];
				String[] strArr = (s.split("/")[1]).split("~");
				String[] strArr2 = strArr[0].split(" ");
				this.start_date = strArr2[1].replace(" ", "");
				this.start_time = strArr2[2].replace(" ", "");
				strArr2 = strArr[1].split(" ");
				this.deadline_date = strArr2[1].replace(" ", "");
				this.deadline_time = strArr2[2].replace(" ", "");
			}
			else {
				this.title = s;
			}
    	}
    	@Override
    	public void printData() {
    		System.out.println(this.subject + " | " + this.Week + " | " + this.title + " | " + this.start_date + " | " + this.start_time + " | " + this.deadline_date + " | " + this.deadline_time );
    	}
    	public String getWeek() {
    		return this.Week;
    	}
    	public String getTitle() {
    		return this.title;
    	}
    	public String getStartDate() {
    		return this.start_date;
    	}
    	public String getStartTime() {
    		return this.start_time;
    	}
    	public String getDeadlineDate() {
    		return this.deadline_date;
    	}
    	public String getDeadlineTime() {
    		return this.deadline_time;
    	}
    	public String getSubject() {
    		return this.subject;
    	}
    }
//과제 데이터
    public class AssignmentDataSet implements DataSet {
    	private String assignment_name;
    	private String deadline_date;
    	private String deadline_time;
    	private String subject;
    	private String[] strArr = null;
    	private String[] strArr2 = null;
    	
    	AssignmentDataSet(String in_assignment_name, String in_deadline_date, String in_deadline_time, String in_subject){
    		this.assignment_name = in_assignment_name;
    		this.deadline_date = in_deadline_date;
    		this.deadline_time = in_deadline_time;
    		this.subject = in_subject;
    	}
    	AssignmentDataSet(String s) {
    		strArr = s.split("\\n");
    		saveData(s);
    		for(int index = 4 ; strArr.length > index ; index+=2) {
    				saveData(s, index);
    		}
    	}
    	//무조건 호출
    	@Override
    	public void saveData(String s) {
    		this.deadline_date = strArr[1].replace(" ", "").replace("-", "");
    		this.assignment_name = strArr[2];
    		strArr2 = strArr[3].split("∙");
    		this.deadline_time = (strArr2[0].split("\\."))[3].trim();
    		this.subject = (strArr2[1].split(":"))[1].trim();
    		
    		assignmentList.add(this);
    	}
    	//해당 일자에 마감인 과제가 2개 이상일 경우 추가적인 과제 데이터를 저장하기 위해 호출
    	public void saveData(String s, int index) {
    		strArr2 = strArr[index+1].split("∙");
    		assignmentList.add(new AssignmentDataSet(strArr[index], this.deadline_date, ((strArr2[0].split("\\."))[3].trim()), ((strArr2[1].split(":"))[1].trim())));	
       	}
    	@Override
    	public void printData() {
    		System.out.print(subject + " | ");
    		System.out.print(deadline_date + " | ");
    		System.out.print(deadline_time + " | ");
    		System.out.println(assignment_name);
    		System.out.println("-------------------------------------------------");
    	}
    	public String getName() { 
    		return this.assignment_name;
    	}
    	public String getDate() {
    		return this.deadline_date;
    	}
    	public String getTime() {
    		return this.deadline_time;
    	}
    	public String getSubject() {
    		return this.subject;
    	}
    }
//학사 일정 데이터
    public class AcademicDataSet implements DataSet {
    	private String event;
    	private String start_year;
    	private String end_year;
    	private String start_month;
    	private String end_month;
    	private String start_day;
    	private String end_day;
    	
    	AcademicDataSet(String e, String s_y, String e_y, String s_m, String e_m, String s_d, String e_d) {
    		this.event = e;
    		this.start_year = s_y;
    		this.end_year = e_y;
    		this.start_month = s_m;
    		this.end_month = e_m;
    		this.start_day = s_d;
    		this.end_day = e_d;
    		
    		Calendar today = Calendar.getInstance();

    		if(today.get(Calendar.MONTH) < 6) {
    			if(this.event.contains("1학기") && this.event.contains("개강")) {    				
    				semesterStartDate = this.start_year + "-" + this.start_month + "-" + this.start_day;
    			}
    		}
    		else {
    			if(this.event.contains("2학기") && this.event.contains("개강")) {
    				semesterStartDate = this.start_year + "-" + this.start_month + "-" + this.start_day;
    			}
    		}
    	}
    	AcademicDataSet(String m, String e) {
    		String[] strArr = (m.split("\\."))[0].split(" ");
    		this.start_year = strArr[0].replace("년", "");
    		this.end_year = this.start_year;
    		this.start_month = strArr[1].replace("월", "");
    		this.end_month = this.start_month;
    		saveData(e);
    	}

    	@Override
    	public void saveData(String s) {
    		String[] strArr = s.split("\\n");
    		String[] strArr2 = null;
    		String temp = this.start_month;
    		String temp2 = this.start_year;
    		for(int i = 0; i<strArr.length;i+=2) {
    			strArr2 = strArr[i].replace(" ", "").split("-");
    			if(strArr2.length != 1) {
    				if(strArr2[0].indexOf('.') != -1) {
    					this.start_month = strArr2[0].split("\\.")[0];
    					this.start_day = strArr2[0].split("\\.")[1].split("\\(")[0];
    				}
    				else {
    					this.start_day = strArr2[0].split("\\(")[0];
    				}
    				if(strArr2[1].indexOf('.') != -1) {
    					this.end_month = strArr2[1].split("\\.")[0];
    					this.end_day = strArr2[1].split("\\.")[1].split("\\(")[0];
    				}
    				else {
    					this.end_day = strArr2[1].split("\\(")[0];
    				}
    			}
    			else {
    				this.start_day = strArr2[0].split("\\(")[0].trim();
    				this.end_day = this.start_day;
    			}
    			academicList.add(new AcademicDataSet(strArr[i+1], this.start_year, this.end_year , this.start_month, this.end_month, this.start_day, this.end_day));
    			this.start_month = temp;
    			this.end_month = temp;
    			this.start_year = temp2;
    			this.end_year = temp2;
    		}
    	}
    	@Override
    	public void printData() {
    		System.out.print(event + " | ");
    		System.out.print(start_year + "/");
    		System.out.print(start_month + "/");
    		System.out.print(start_day + " ~ ");
    		System.out.print(end_year + "/");
    		System.out.print(end_month + "/");
    		System.out.println(end_day);
    		System.out.println("---------------------------------------------------");
    	}
    	public String getEvent() {
    		return this.event;
    	}
    	public String getStartYear() { 
    		return this.start_year;
    	}
    	public String getEndYear() {
    		return this.end_year;
    	}
    	public String getStartMonth() {
    		return this.start_month;
    	}
    	public String getEndMonth() {
    		return this.end_month;
    	}
    	public String getStartDay() {
    		return this.start_day;
    	}
    	public String getEndDay() {
    		return this.end_day;
    	}
    }
}

