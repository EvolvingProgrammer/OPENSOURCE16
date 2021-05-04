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

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.Button;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        CalendarView calendarView = new CalendarView();

        //Ķ���� ��� ����� 
        //ex �б�, ���, ����
        Calendar katja = new Calendar("Katja");
        Calendar dirk = new Calendar("Dirk");
        Calendar philip = new Calendar("Philip");
        Calendar jule = new Calendar("Jule");
        Calendar armin = new Calendar("Armin");
        Calendar birthdays = new Calendar("Birthdays");
        Calendar holidays = new Calendar("Holidays");

        
        //������ ��� ���, �����ͺ��̽����� ������ �������� ��� �� ��(���α׷� ���� ��)
        Entry<String> entry = new Entry<>("Hello");
        entry.setInterval(LocalDate.now());
        entry.changeStartDate(LocalDate.now());
        entry.changeEndDate(LocalDate.now());
        entry.changeStartTime(LocalTime.of(12,30));
        entry.changeEndTime(LocalTime.of(13,30));
        holidays.addEntry(entry);
        //
        
        //�÷��ʿ��� ���̴� ���� ���� �̸� ���� ���
        katja.setShortName("K");
        dirk.setShortName("D");
        philip.setShortName("P");
        jule.setShortName("J");
        armin.setShortName("A");
        birthdays.setShortName("B");
        holidays.setShortName("H");
        // Ķ������ ���̴� �� ���� ���
        katja.setStyle(Style.STYLE1);
        dirk.setStyle(Style.STYLE2);
        philip.setStyle(Style.STYLE3);
        jule.setStyle(Style.STYLE4);
        armin.setStyle(Style.STYLE5);
        birthdays.setStyle(Style.STYLE6);
        holidays.setStyle(Style.STYLE7);

        // borderpane�� �߰��ϴ� ���
        CalendarSource familyCalendarSource = new CalendarSource("Family");
        familyCalendarSource.getCalendars().addAll(birthdays, holidays, katja, dirk, philip, jule, armin);

        calendarView.getCalendarSources().setAll(familyCalendarSource);
        calendarView.setRequestedTime(LocalTime.now());

        BorderPane bp = new BorderPane();
        bp.setCenter(calendarView);
        
        HBox hbox = new HBox();
        
        // fxml�δ��� �̿��� Ķ���� �� �ٸ� ��ư�̳� ��Ÿ ��͵� �߰��� �� ���
      /*  try {
    		Parent root2 = (Parent)FXMLLoader.load(getClass().getResource("Main_ui.fxml"));
    		bp.setLeft(root2);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
        */
      
        // �޷� �ð� ������Ʈ �ϴ� ������
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
        
        
        
        
   
        
      // ȭ�� ����
        Scene scene = new Scene(bp);
        primaryStage.setTitle("Calendar");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1300);
        primaryStage.setHeight(1000);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // �̺�Ʈ �ڵ鷯�� ����ؾ� Ķ�������� �̺�Ʈ ó���� ������
        EventHandler<CalendarEvent> l = e -> handleEvent1(e);
        birthdays.addEventHandler(l);
        holidays.addEventHandler(l);
        
   
    }
    
    // �̺�Ʈ �ڵ鷯, ���⼭ �ش� ������ ������ ��Ʈ��(������)�� ������ �� ����
    private EventHandler<CalendarEvent> handleEvent1(CalendarEvent e) {
    
  
    	// ���ô� ��Ʈ���� �������ų�, ��Ʈ���� ������ ���, �� ��� ����
    	// �������� �ٸ� ī�װ��� �Ѿ���(�б� -> ���), �����ٳ����� ����� ��� 
    	// �۾� �ʿ�
    	if(e.isEntryAdded()== true || e.isEntryRemoved())
    	System.out.println(e.getEntry());
		return null;
    	
    	}

    public static void main(String[] args) {
        launch(args);
        
       
        
    }
}
