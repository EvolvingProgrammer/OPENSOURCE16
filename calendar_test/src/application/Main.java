/*
 *  Copyright (C) 2017 Dirk Lemmermann Software & Consulting (dlsc.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package application;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalTime;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
	static MemoPaneController MemoPaneControllerHandle;
	Calendar Private_Schedule;
    Calendar University_Schedule;
    Calendar BlackBord_Schedule ;
    Calendar BlackBord_Movie_Schedule;
    Data_base db;

    @Override
    public void start(Stage primaryStage) throws Exception {
        CalendarView calendarView = new CalendarView();

     
       
      
        
       
        Private_Schedule = new Calendar("개인일정");
        University_Schedule = new Calendar("학사일정");
        BlackBord_Schedule = new Calendar("블랙보드 과제");
        BlackBord_Movie_Schedule = new Calendar("이러닝 일정");
       // Calendar armin = new Calendar("Armin");
       // Calendar birthdays = new Calendar("Birthdays");
      //  Calendar holidays = new Calendar("Holidays");
        
        Event_handle handle = new Event_handle();
        
    
        
        EventHandler<CalendarEvent> l = e -> handle.handleEvent(e);
   
        Private_Schedule.addEventHandler(l);
        University_Schedule.addEventHandler(l);
        BlackBord_Schedule.addEventHandler(l);
        BlackBord_Movie_Schedule.addEventHandler(l);
        
        
        // db와 핸들러 연결 메인 서로 연결
        db = new Data_base();
        db.M = this;
    
        handle.Private_Schedule_list = db.Private_Schedule_list;
        handle.University_Schedule_list = db.University_Schedule_list;
        handle.BlackBord_Schedule_list = db.BlackBord_Schedule_list ;
        handle.BlackBord_Movie_Schedule_list = db.BlackBord_Movie_Schedule_list;
        handle.db = db;
        handle.M = this;
        
        
      
        
        // 메인 연결후 데이터베이스 리로드 시작
        db.reload_schedule(Private_Schedule,"Private_Schedule");
        db.reload_schedule(University_Schedule,"University_Schedule");
        db.reload_schedule(BlackBord_Schedule,"BlackBord_Schedule");
        db.reload_schedule(BlackBord_Movie_Schedule,"BlackBord_Movie_Schedule");
        
        
        ///
        
       
        
     //   philip.addEventHandler(l);
     //   jule.addEventHandler(l);
      //  armin.addEventHandler(l);
        
        ////

        
      /*
        Entry<String> entry = new Entry<>("Hello");
        //entry.setInterval(LocalDate.now());
        entry.changeStartDate(LocalDate.now());
        entry.changeEndDate(LocalDate.now());
        entry.changeStartTime(LocalTime.of(12,30));
        entry.changeEndTime(LocalTime.of(13,30));
       
        University_Schedule.addEntry(entry);
        
        */
    
       
     
        
       
        Private_Schedule.setShortName("개");
        University_Schedule.setShortName("학");
        BlackBord_Schedule.setShortName("블");
        BlackBord_Movie_Schedule.setShortName("이");
      //  armin.setShortName("A");
     //   birthdays.setShortName("B");
      //  holidays.setShortName("H");
       
        Private_Schedule.setStyle(Style.STYLE1);
        University_Schedule.setStyle(Style.STYLE2);
        BlackBord_Schedule.setStyle(Style.STYLE3);
        BlackBord_Movie_Schedule.setStyle(Style.STYLE4);
     //   armin.setStyle(Style.STYLE5);
    //    birthdays.setStyle(Style.STYLE6);
    //    holidays.setStyle(Style.STYLE7);

        
        CalendarSource familyCalendarSource = new CalendarSource("Dynamic Scheduler");
        familyCalendarSource.getCalendars().addAll(Private_Schedule, University_Schedule, BlackBord_Schedule, BlackBord_Movie_Schedule);

        calendarView.getCalendarSources().setAll(familyCalendarSource);
        calendarView.setRequestedTime(LocalTime.now());

        BorderPane bp = new BorderPane();
        bp.setCenter(calendarView);
        

       
       try {
    	
    				
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("Selenium_Ui.fxml"));
    		Parent root2 = loader.load();
    		App_Controller dac = (App_Controller) loader.getController();
    		dac.test();
    		dac.db = this.db;
    		dac.M = this;
    	
    		bp.setLeft(root2);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    // 메모기능 추가 
       HBox hbox = new HBox();
       try {
   		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MemoPane.fxml"));
   		Parent root3 = fxmlLoader.load();
   		bp.setRight(root3);
   		
   		MemoPaneControllerHandle = (MemoPaneController)fxmlLoader.getController();
   		
   	} catch(Exception e) {
   		e.printStackTrace();
   	}
      
       
        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        
        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();
        
        
        primaryStage.setOnCloseRequest(e -> {
        	try{
        		String save_daily = MemoPaneControllerHandle.getDailyTextArea();
            	String save_monthly = MemoPaneControllerHandle.getMonthlyTextArea();
            	FileOutputStream output=new FileOutputStream(".\\src\\application\\DailyMemo.txt",false);
				OutputStreamWriter writer=new OutputStreamWriter(output,"UTF-8");
				FileOutputStream output2=new FileOutputStream(".\\src\\application\\MonthlyMemo.txt",false);
				OutputStreamWriter writer2=new OutputStreamWriter(output2,"UTF-8");
				BufferedWriter out=new BufferedWriter(writer);
				BufferedWriter out2=new BufferedWriter(writer2);
				out.write(save_daily);
				out.close();
				out2.write(save_monthly);
				out2.close();
        	}catch(Exception e1) {
        		e1.getStackTrace();
        	}
        	db.save_calendar();
        	Platform.exit();
        });
        
   
        
      
        Scene scene = new Scene(bp);
        primaryStage.setTitle("Calendar");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1800);
        primaryStage.setHeight(1000);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        
        
        
        
   
    }
    
  

    public static void main(String[] args) {
        launch(args);
        
       
     
        
       
        
    }
  
}
