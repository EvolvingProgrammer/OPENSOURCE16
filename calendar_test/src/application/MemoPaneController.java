package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class MemoPaneController implements Initializable{

	@FXML
	private TextArea DailyTextArea;
	@FXML
	private TextArea MonthlyTextArea;
	
	public String getDailyTextArea() {
		return DailyTextArea.getText();
	}
	
	public String getMonthlyTextArea() {
		return MonthlyTextArea.getText();
	}
	
	@Override   //패널 시작할 시 txt 파일을 불러들여와 기존 메모 내용 유지
	public void initialize(URL location, ResourceBundle resources) {
		try {
			File file = new  File(".\\src\\application\\DailyMemo.txt");
			File file2 = new File(".\\src\\application\\MonthlyMemo.txt");
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			BufferedReader bufReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2),"UTF-8"));
		
			String daily_line = "";
			String monthly_line = "";
			String daily_str = "";
			String monthly_str = "";
			
			while((daily_line = bufReader.readLine()) != null) {
				daily_str = daily_str + daily_line + "\n";
			}
			while((monthly_line = bufReader2.readLine()) != null) {
				monthly_str = monthly_str + monthly_line + "\n";
			}
			
			bufReader.close();
			bufReader2.close();
			
			DailyTextArea.setText(daily_str);
			MonthlyTextArea.setText(monthly_str);	

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.getStackTrace();
		}

	}
}
