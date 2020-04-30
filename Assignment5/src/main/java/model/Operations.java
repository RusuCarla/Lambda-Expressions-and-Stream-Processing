package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Operations {

	private List<MonitoredData> monitoredData;
	private String filename;
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public Operations(String filename) {
		this.filename = filename + ".txt";
	}
	
	public void read() {
		
		try {
			Stream<String> stream = Files.lines(Paths.get(filename));
			
			monitoredData = stream
					.map(tab -> tab.split("\t\t"))
					.map(tab -> new MonitoredData(LocalDateTime.parse(tab[0], dateTimeFormatter), LocalDateTime.parse(tab[1], dateTimeFormatter), tab[2]))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public long countDays() {
		
		long days = monitoredData
				.stream()
				.flatMap(t-> Stream.of(t.getStart_time(), t.getEnd_time()))
				.map(t -> t.getDayOfYear())
				.distinct()
				.count();
		System.out.println("number of monitored data days= " + days);	
		return days;
	}
	
	public Map<String, Integer> countActivities() {
		Map<String, Integer> activities = monitoredData
				.stream()
				.map(t -> t.getActivity_label())
				.collect(Collectors.toMap(t ->t, t -> 1,Integer::sum));
		
		activities.entrySet().stream().forEach(System.out::println);
		return activities;
	}
	
	public Map<String, Map<String, Long>> countActivitiesPerDay() {
		Map<String, Map<String, Long>> activitiesPerDay = monitoredData
				.stream()
				.collect(Collectors.groupingBy(t->t.getStart_time().toLocalDate().toString()+"\n", Collectors.groupingBy(t->t.getActivity_label(), Collectors.counting())));
		activitiesPerDay.entrySet().stream().forEach(System.out::println);
		return activitiesPerDay;
	}
	
	public void durationPerActivityPerDay() {
		monitoredData
				.stream()
				.forEach(t -> System.out.println(t.getActivity_label().replaceAll("\\s", "") + "\t\t" + 
						String.format("%02d:%02d:%02d", 
						        TimeUnit.MILLISECONDS.toHours(Duration.between(t.getStart_time(), t.getEnd_time()).toMillis()),
						        TimeUnit.MILLISECONDS.toMinutes(Duration.between(t.getStart_time(), t.getEnd_time()).toMillis()) - 
						        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Duration.between(t.getStart_time(), t.getEnd_time()).toMillis())),
						        TimeUnit.MILLISECONDS.toSeconds(Duration.between(t.getStart_time(), t.getEnd_time()).toMillis()) - 
						        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Duration.between(t.getStart_time(), t.getEnd_time()).toMillis())))
						));
	}
	
	public Map<String,String> durationPerActivity(){
		
		Map<String,Long> durations = monitoredData
				.stream()
				.collect(Collectors.groupingBy(t -> t.getActivity_label(),Collectors.summingLong(MonitoredData::getDuration)));
		
		
		Map<String, String> formattedDuration = durations.entrySet()
				.stream()
				.collect(Collectors.toMap(t -> t.getKey().toString().replaceAll("\\s","") +"\t", t -> 
					String.format("\t%02d:%02d:%02d", 
					        TimeUnit.MILLISECONDS.toHours(t.getValue()*1000),
					        TimeUnit.MILLISECONDS.toMinutes(t.getValue()*1000) - 
					        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(t.getValue()*1000)),
					        TimeUnit.MILLISECONDS.toSeconds(t.getValue()*1000) - 
					        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(t.getValue()*1000)))
				));
		
		formattedDuration.entrySet().stream().forEach(System.out::println);
		return formattedDuration;
	}
	
	public List<String> filter(){
		int seconds = 300;
		
		Map<String, Integer> allActivities = monitoredData
				.stream()
				.map(t -> t.getActivity_label())
				.collect(Collectors.toMap(t ->t, t -> 1,Integer::sum));
		
		Map<String, Integer> activitiesUnderFive = monitoredData
				.stream()
				.filter(t -> Duration.between(t.getStart_time(), t.getEnd_time()).getSeconds() < seconds)
				.map(t -> t.getActivity_label())
				.collect(Collectors.toMap(t ->t, t -> 1,Integer::sum));
		
		List<String> activities = monitoredData
				.stream()
				.filter(t -> activitiesUnderFive.get(t.getActivity_label()) != null)
				.filter(t -> activitiesUnderFive.get(t.getActivity_label()) >= (allActivities.get(t.getActivity_label()) * 0.9))
				.map(MonitoredData::getActivity_label)
				.distinct()
				.collect(Collectors.toList());
		activities.stream().forEach(System.out::println);
		
		return activities;
	}
	
	public void print() {
		monitoredData.stream().forEach(System.out::println);
	}
	
	public static void main (String args[]) {
		Operations operations = new Operations("Activities");
		operations.read();
		//operations.print();
		//operations.countDays();
		//operations.countActivities();
		//operations.countActivitiesPerDay();
		//operations.durationPerActivityPerDay();
		//operations.durationPerActivity();
		operations.filter();
	}
}
