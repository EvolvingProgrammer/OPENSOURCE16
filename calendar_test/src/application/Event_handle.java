package application;

import java.util.LinkedList;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.Entry;
import com.sun.javafx.scene.text.TextLine;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import javafx.event.EventHandler;
import net.bytebuddy.NamingStrategy.SuffixingRandom.BaseNameResolver.ForGivenType;



public class Event_handle {


	
	LinkedList<Entry<String>> Private_Schedule_list;
	LinkedList<Entry<String>> University_Schedule_list;
	LinkedList<Entry<String>> BlackBord_Schedule_list;
	LinkedList<Entry<String>> BlackBord_Movie_Schedule_list;
	LinkedList<Entry<String>> BlackBord_Movie_Finished_list;
	
	
	
	
	Data_base db;
	Main M;
	
	
    public EventHandler<CalendarEvent> handleEvent(CalendarEvent e) {
    
 
    	Entry<String> entry = (Entry<String>) e.getEntry();
		Entry<String> temp = entry;
    	
	if(e.isEntryAdded())
	{
    
    		 switch(e.getCalendar().getName()) {
             case "개인일정" :
            //	 M.Private_Schedule.addEntry(temp);
                 Private_Schedule_list.add(temp);
                 
                 break;
             case "학사일정" :
        //    	 M.University_Schedule.addEntry(temp);
                 University_Schedule_list.add(temp);
                 break;
             case "블랙보드 과제" :
        //    	 M.BlackBord_Schedule.addEntry(temp);
                 BlackBord_Schedule_list.add(temp);
                 break;
             case "이러닝 일정" :
            	// db.add_entry(M.BlackBord_Movie_Schedule,temp, "BlackBord_Movie_Schedule");
          //  	 M.BlackBord_Movie_Schedule.addEntry(temp);
            	 BlackBord_Movie_Schedule_list.add(temp);
                 break;
                 
             case "이러닝 완료" :
            	 BlackBord_Movie_Finished_list.add(temp);
            	 break;
             		
                 
                 
             default :
                 System.out.println("str 은 아무것도 아닙니다.");
                 break;
    		 }
    		
    		/*
    		Entry<String>my_entry= e.getEntry();
    		System.out.println(my_entry.getTitle());
    		System.out.println(my_entry.getStartDate());
    		System.out.println(my_entry.getEndDate());
    		System.out.println(my_entry.getStartTime());
    		System.out.println(my_entry.getEndTime());
    		//System.out.println(e.getCalendar().getName());
    		*/
	}
	else if( e.isEntryRemoved())
	{
    		
    		
    		if(e.getOldCalendar() != null)
    		{
    			
	    		
	    		switch(e.getOldCalendar().getName()) {
	             case "개인일정" :
	            	 db.delete_entry(Private_Schedule_list,temp.getId());
	                 break;
	             case "학사일정" :
	            	 db.delete_entry(University_Schedule_list,temp.getId());
	                 break;
	             case "블랙보드 과제" :
	            	 db.delete_entry(BlackBord_Schedule_list,temp.getId());
	                 break;
	             case "이러닝 일정" :
	            	 db.delete_entry(BlackBord_Movie_Schedule_list,temp.getId());
	                 break;
	                 
	             case "이러닝 완료" :
	            	 db.delete_entry(BlackBord_Movie_Finished_list,temp.getId());
	            	 break;
	                 
	                 
	             default :
	                 System.out.println("str 은 아무것도 아닙니다.");
	                 break;
				 }
				 
    		}
			
    	
    		
    		
    		
    	
	}
	else if (e.isDayChange())
	{
    		LinkedList<Entry<String>> t_link;
    		
    		switch(e.getCalendar().getName()) {
            case "개인일정" :
           	 t_link = Private_Schedule_list;
                break;
            case "학사일정" :
           	 t_link = University_Schedule_list;
                break;
            case "블랙보드 과제" :
           	 t_link = BlackBord_Schedule_list;
                break;
            case "이러닝 일정" :
           	 t_link = BlackBord_Movie_Schedule_list;
                break;
                
            case "이러닝 완료" :
            	t_link = BlackBord_Movie_Finished_list;
            	break;
                
                
            default :
            	t_link = Private_Schedule_list;
                System.out.println("str 은 아무것도 아닙니다.");
                break;
    		}
    		
    		
    		for(int i=0; i< t_link.size(); i++)
    		{
    			if( t_link.get(i).getId() == e.getEntry().getId())
    			{
    				t_link.get(i).changeStartDate(e.getEntry().getStartDate());
    				t_link.get(i).changeEndDate(e.getEntry().getEndDate());
    				break;
    				
    			}
    		}
	}	
	else if( e.getEventType().equals(CalendarEvent.ENTRY_TITLE_CHANGED) )
	{
    		
			LinkedList<Entry<String>> t_link;
    		
    		switch(e.getCalendar().getName()) {
            case "개인일정" :
           	 t_link = Private_Schedule_list;
                break;
            case "학사일정" :
           	 t_link = University_Schedule_list;
                break;
            case "블랙보드 과제" :
           	 t_link = BlackBord_Schedule_list;
                break;
            case "이러닝 일정" :
           	 t_link = BlackBord_Movie_Schedule_list;
                break;
                
            case "이러닝 완료" :
            	t_link = BlackBord_Movie_Finished_list;
            	break;
                
                
            default :
            	t_link = Private_Schedule_list;
                System.out.println("str 은 아무것도 아닙니다.");
                break;
    		}
    		
    		
    		
    		for(int i=0; i<t_link.size(); i++)
    		{
    			if(t_link.get(i).getId() == e.getEntry().getId())
    				t_link.get(i).setTitle(e.getEntry().getTitle());
    		}
    		
	}
	else if( e.getEventType().equals(CalendarEvent.ENTRY_CALENDAR_CHANGED)  )
	{
		
		
    		if(e.getOldCalendar() !=null)
    		{
    			
    			LinkedList<Entry<String>> before;
    			LinkedList<Entry<String>> aft;
    			Calendar after;
    			Calendar be;
        		
    			switch(e.getOldCalendar().getName()) {
                case "개인일정" :
                	before = Private_Schedule_list;
                	be  = M.Private_Schedule;
                    break;
                case "학사일정" :
                	before = University_Schedule_list;
                	be  = M.University_Schedule;
                    break;
                case "블랙보드 과제" :
                	before = BlackBord_Schedule_list;
                	be  = M.BlackBord_Schedule;
                    break;
                case "이러닝 일정" :
                	before = BlackBord_Movie_Schedule_list;
                	be  = M.BlackBord_Movie_Schedule;
                    break;
                    
                case "이러닝 완료" :
                	before = BlackBord_Movie_Finished_list;
                	be  = M.BlackBord_Movie_Finished;
                	break;
                    
                   
                    
                default :
                	before = Private_Schedule_list;
                	be = M.BlackBord_Movie_Schedule;
                    System.out.println("str 은 아무것도 아닙니다.");
                    break;
        		}
    			
    			 System.out.println(before);
    			
    			switch(e.getCalendar().getName()) {
                case "개인일정" :
                	after = M.Private_Schedule;
                	aft = Private_Schedule_list;
                    break;
                case "학사일정" :
                	after = M.University_Schedule;
                	aft = University_Schedule_list;
                    break;
                case "블랙보드 과제" :
                	after = M.BlackBord_Schedule;
                	aft = BlackBord_Schedule_list;
                    break;
                case "이러닝 일정" :
                	after = M.BlackBord_Movie_Schedule;
                	aft = BlackBord_Movie_Schedule_list;
                    break;
                case "이러닝 완료" :
                	after = M.BlackBord_Movie_Finished;
                	aft = BlackBord_Movie_Finished_list;
                    break;
                    
                    
                default :
                	after = M.Private_Schedule;
                	aft = BlackBord_Movie_Schedule_list;
                    System.out.println("str 은 아무것도 아닙니다.");
                    break;
        		}
        		
    			System.out.println(after);
        		
        		change_calendar(before, aft,be,after, temp);
        		
    		}
    		
    	
    		
    		
    		
    	
    		
    		
	}
	else if( e.getEventType().equals(CalendarEvent.ENTRY_INTERVAL_CHANGED) )
	{
    		LinkedList<Entry<String>> t_link;
    		
    		switch(e.getCalendar().getName()) {
            case "개인일정" :
           	 t_link = Private_Schedule_list;
                break;
            case "학사일정" :
           	 t_link = University_Schedule_list;
                break;
            case "블랙보드 과제" :
           	 t_link = BlackBord_Schedule_list;
                break;
            case "이러닝 일정" :
           	 t_link = BlackBord_Movie_Schedule_list;
                break;
            case "이러닝 완료" :
              	 t_link = BlackBord_Movie_Finished_list;
                   break;
                
                
            default :
            	t_link = Private_Schedule_list;
                System.out.println("str 은 아무것도 아닙니다.");
                break;
    		}
    		
    		
    		for(int i=0; i<t_link.size(); i++)
    		{
    			if(t_link.get(i).getId() == e.getEntry().getId())
    			{
    				t_link.get(i).changeStartTime(e.getEntry().getStartTime());
    				t_link.get(i).changeEndTime(e.getEntry().getEndTime());
    				break;
    			}
    				
    		}
    		
    	}
    	
    	
    	return null;
    	
    	
    	}
    
    
    public void change_calendar(LinkedList<Entry<String>> before,LinkedList<Entry<String>> aft,Calendar be, Calendar after, Entry<String> temp)
    {
    	
    	
    	
    	for(int i=0; i<before.size(); i++)
    	{
    		
    		
    		if(before.get(i).getId() == temp.getId())
    		{
    			
    			aft.add(temp);
    			before.remove(i);
    			
    			
    			
    		}
    	}
    	
    	
    	
    	
    	
    }
 

}
