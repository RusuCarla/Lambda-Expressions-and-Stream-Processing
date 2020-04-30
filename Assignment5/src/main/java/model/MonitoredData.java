package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MonitoredData {
	
	private LocalDateTime start_time;
	private LocalDateTime end_time;
	private String activity_label;
	
	public MonitoredData(LocalDateTime start_time, LocalDateTime end_time, String activity_label) {
		this.start_time = start_time;
		this.end_time = end_time;
		this.activity_label = activity_label;
	}

	@Override
	public String toString() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return "start_time= " + start_time.format(dateTimeFormatter) + "  end_time= " + end_time.format(dateTimeFormatter) + "  activity_label= "
				+ activity_label + "\n";
	}

	public LocalDateTime getStart_time() {
		return start_time;
	}

	public void setStart_time(LocalDateTime start_time) {
		this.start_time = start_time;
	}

	public LocalDateTime getEnd_time() {
		return end_time;
	}

	public void setEnd_time(LocalDateTime end_time) {
		this.end_time = end_time;
	}

	public String getActivity_label() {
		return activity_label;
	}

	public void setActivity_label(String activity_label) {
		this.activity_label = activity_label;
	}

	public long getDuration() {
		return Duration.between(this.start_time, this.end_time).getSeconds();
	}
	
}
