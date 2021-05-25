package application;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;



public class App_Controller {
	private boolean is_run;

	Main M;
	public Data_base db;
	
    @FXML
    private Button Selenium_button;

    
    public App_Controller() {
		
		is_run = false; 
		
	}

    @FXML
    void Start_Crawl(MouseEvent event) {   
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	alert.getButtonTypes().clear();
    	
    	if( is_run == true ) {
        	alert.setTitle("Already running..");
        	alert.setHeaderText("학사정보 시스템 연동 중...");
        	alert.getButtonTypes().addAll(ButtonType.YES);
    	}
    	else {
        	alert.setHeaderText("학사정보 시스템 연동을 시작합니다.");
        	alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
    	}

    	
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.isPresent() && result.get() == ButtonType.YES) {
    	    // ... user chose OK

    		if( is_run == false) {
    			crawlThread t = new crawlThread();
        		t.start();	
        		return;        		
        	}
        	else {
        		return;
        	}	
    	} 
    	else {
    	    // ... user chose CANCEL or closed the dialog
    	}
    }
    
    @FXML
    private Button Selenium_button2;
    
    @FXML
    void Start_Reset(MouseEvent event) {
    	
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	alert.setTitle("Reset Schedule..");
    	alert.setHeaderText("개인 일정을 제외한 모든 일정을 삭제하시겠습니까?");
    	
    	alert.getButtonTypes().clear();
    	alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
      	
    	final Optional<ButtonType> result = alert.showAndWait();

    	if(result.isPresent() && result.get() == ButtonType.YES) {
    		System.out.println("Yes");
    		db.reset_selenium();
    	}
    }
    
    
    //시스템 연동 스레드
    public class crawlThread extends Thread {
    	
    	@Override
    	public void run() {
    		try {
    			is_run = true;
    			db.sync_selenium();
    		} catch(Exception e) {
    			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    			alert.getButtonTypes().clear();
    	    	alert.getButtonTypes().addAll(ButtonType.OK);
    	    	alert.setHeaderText("오류가 발생했습니다");
    	    	alert.setContentText("다시 시도해 주세요");
    			return;
    		} finally {
    			is_run = false;
    		}
    	}
    }
}
