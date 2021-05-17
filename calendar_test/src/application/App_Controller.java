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
    	alert.setHeaderText("학사정보 시스템 연동을 시작합니다.");
    	//alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(sw.toString())));
   
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    	    // ... user chose OK
    		
    		if( is_run == false)
        	{
        		is_run = true;
        		
        		is_run = false;		
        		db.sync_selenium();
        		
        		return;
        		
        	}
        	else
        	{
        		return;
        	}
    		
    		
    		
    	} else {
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
    
    public void test()
    {
    	System.out.println("테스트입니다 컨트롤러 접근 성공");
    }
    
    public class AlertThread extends Thread {
    	public void run() {
    		try {
    			is_run = true;
    			db.sync_selenium();
    		} catch(Exception e) {
    			
    		} finally {
    			is_run = false;
    		}
    	}
    }
}
