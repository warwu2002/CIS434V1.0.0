package org.example;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.notification.Notification;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

//import javax.management.Notification;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

import static javax.management.Notification.*;

@PageTitle("Class Timetable")
@Route("")
public class Main extends VerticalLayout {

    private final Grid<TimeSlot> timetableGrid;
    private Map<DayOfWeek, List<ClassSchedule>> weeklySchedule = new EnumMap<>(DayOfWeek.class);

    private final TextField classNameField = new TextField("Class Name");
    private final TextField classRoomField = new TextField("Class Room");
    private final TimePicker startTimePicker = new TimePicker("Start Time");
    private final TimePicker endTimePicker = new TimePicker("End Time");
    private final CheckboxGroup<DayOfWeek> dayPicker = new CheckboxGroup<>();

    private static List<TimeSlot> sharedTimeSlots = new ArrayList<>();

    public static List<TimeSlot> getTimeSlots() {
        return sharedTimeSlots;
    }


    private void refreshTimetable() {
        sharedTimeSlots.clear(); // Clear previous data
        List<TimeSlot> timeSlots = generateTimeSlots();

        // For each day, check all classes and add them to the corresponding time slots
        for (DayOfWeek day : DayOfWeek.values()) {
            if (weeklySchedule.containsKey(day)) {
                for (ClassSchedule classSchedule : weeklySchedule.get(day)) {
                    for (TimeSlot slot : timeSlots) {
                        // Add the class to all slots it occupies
                        if (classSchedule.startTime().isBefore(slot.getEndTime()) && classSchedule.endTime().isAfter(slot.getStartTime())) {
                            slot.setClassesForDay(day, classSchedule);
                        }
                    }
                }
            }
        }

        timetableGrid.setItems(timeSlots);
        sharedTimeSlots.addAll(timeSlots); // Share the updated time slots
    }




    public Main() {

        // Initialize the timetable grid
        // Create a grid with TimeSlot class and improve the layout
        timetableGrid = new Grid<>(TimeSlot.class, false);
        /*timetableGrid.getColumns().forEach(column -> column.setAutoWidth(true));
        timetableGrid.setWidth("auto");
        timetableGrid.getStyle().set("padding", "0");
        timetableGrid.getStyle().set("margin", "0");*/





// Add Time column (displaying time range in 24-hour format)
        timetableGrid.addColumn(TimeSlot::getTimeRange).setHeader("Time")
                .setWidth("250px")
                .setTextAlign(ColumnTextAlign.CENTER);

// Add columns for each day of the week
        timetableGrid.addColumn(new ComponentRenderer<>(slot -> {
            VerticalLayout layout = new VerticalLayout();
            if (!slot.getMondayClasses().isEmpty()) {
                TextArea mondayClasses = new TextArea();
                mondayClasses.setValue(slot.getMondayClasses());
                mondayClasses.setReadOnly(true);
                mondayClasses.setWidthFull();
                layout.add(Collections.singleton(mondayClasses));
            }
            return layout;
        })).setHeader("Monday").setWidth("150px");

// Repeat for other days
        timetableGrid.addColumn(new ComponentRenderer<>(slot -> {
            VerticalLayout layout = new VerticalLayout();
            if (!slot.getTuesdayClasses().isEmpty()) {
                TextArea tuesdayClasses = new TextArea();
                tuesdayClasses.setValue(slot.getTuesdayClasses());
                tuesdayClasses.setReadOnly(true);
                tuesdayClasses.setWidthFull();
                layout.add(Collections.singleton(tuesdayClasses));
            }
            return layout;
        })).setHeader("Tuesday").setWidth("150px");

        timetableGrid.addColumn(new ComponentRenderer<>(slot -> {
            VerticalLayout layout = new VerticalLayout();
            if (!slot.getWednesdayClasses().isEmpty()) {
                TextArea wednesdayClasses = new TextArea();
                wednesdayClasses.setValue(slot.getWednesdayClasses());
                wednesdayClasses.setReadOnly(true);
                wednesdayClasses.setWidthFull();
                layout.add(Collections.singleton(wednesdayClasses));
            }
            return layout;
        })).setHeader("Wednesday").setWidth("150px");

        timetableGrid.addColumn(new ComponentRenderer<>(slot -> {
            VerticalLayout layout = new VerticalLayout();
            if (!slot.getThursdayClasses().isEmpty()) {
                TextArea thursdayClasses = new TextArea();
                thursdayClasses.setValue(slot.getThursdayClasses());
                thursdayClasses.setReadOnly(true);
                thursdayClasses.setWidthFull();
                layout.add(Collections.singleton(thursdayClasses));
            }
            return layout;
        })).setHeader("Thursday").setWidth("150px");

        timetableGrid.addColumn(new ComponentRenderer<>(slot -> {
            VerticalLayout layout = new VerticalLayout();
            if (!slot.getFridayClasses().isEmpty()) {
                TextArea fridayClasses = new TextArea();
                fridayClasses.setValue(slot.getFridayClasses());
                fridayClasses.setReadOnly(true);
                fridayClasses.setWidthFull();
                layout.add(Collections.singleton(fridayClasses));
            }
            return layout;
        })).setHeader("Friday").setWidth("150px");

        timetableGrid.addColumn(new ComponentRenderer<>(slot -> {
            VerticalLayout layout = new VerticalLayout();
            /*if (!slot.getSaturdayClasses().isEmpty()) {
                TextArea saturdayClasses = new TextArea();
                saturdayClasses.setValue(slot.getSaturdayClasses());
                saturdayClasses.setReadOnly(true);
                saturdayClasses.setWidthFull();
                layout.add(Collections.singleton(saturdayClasses));
            }*/
            return layout;
        }));/*.setHeader("Saturday").setWidth("150px");

        timetableGrid.addColumn(new ComponentRenderer<>(slot -> {
            VerticalLayout layout = new VerticalLayout();
            if (!slot.getSundayClasses().isEmpty()) {
                TextArea sundayClasses = new TextArea();
                sundayClasses.setValue(slot.getSundayClasses());
                sundayClasses.setReadOnly(true);
                sundayClasses.setWidthFull();
                layout.add(Collections.singleton(sundayClasses));
            }
            return layout;
        })).setHeader("Sunday").setWidth("150px");*/

        timetableGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        timetableGrid.setWidthFull();
        timetableGrid.setHeight("750px");

        // Setup Day Picker
        dayPicker.setLabel("Days");
        dayPicker.setItems(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);;
        dayPicker.setHelperText("Select the days this class occurs");

        // Set 5-minute step intervals for time pickers
        startTimePicker.setStep(Duration.ofMinutes(5));
        endTimePicker.setStep(Duration.ofMinutes(5));

        // Class Form Layout
        FormLayout formLayout = new FormLayout();
        formLayout.add(classNameField, classRoomField, dayPicker, startTimePicker, endTimePicker);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        // Buttons for Form Actions
        Button addButton = new Button("Add Class", event -> addClassToSchedule());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button clearButton = new Button("Clear", event -> {
            clearForm();
            weeklySchedule.clear(); // Clear all schedules
            refreshTimetable();     // Refresh the timetable to show an empty table
            Notification.show("Timetable cleared!", 3000, Notification.Position.BOTTOM_END);
        });


        Button openCompactScheduleButton = new Button("Open Compact Schedule", event -> {
            UI.getCurrent().getPage().open("compact-schedule", "_blank");
        });
        openCompactScheduleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, clearButton, openCompactScheduleButton);
        VerticalLayout formContainer = new VerticalLayout(new H3("Add Class"), formLayout, buttonLayout);

        // Layout for Form and Timetable
        SplitLayout mainLayout = new SplitLayout();
        mainLayout.addToPrimary(formContainer);
        mainLayout.addToSecondary(timetableGrid);
        mainLayout.setSizeFull();

        add(mainLayout);

        // Initialize timetable with empty time slots
        refreshTimetable();
    }



    private void addClassToSchedule() {
        String className = classNameField.getValue();
        String classRoom = classRoomField.getValue();
        LocalTime startTime = startTimePicker.getValue();
        LocalTime endTime = endTimePicker.getValue();
        Set<DayOfWeek> selectedDays = dayPicker.getValue();

        if (className.isEmpty() || classRoom.isEmpty() || startTime == null || endTime == null || selectedDays.isEmpty()) {
            Notification.show("All fields must be filled!", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            Notification.show("End time must be after start time!", 3000, Notification.Position.MIDDLE);
            return;
        }

        for (DayOfWeek day : selectedDays) {
            for (ClassSchedule existingSchedule : weeklySchedule.getOrDefault(day, new ArrayList<>())) {
                if (startTime.isBefore(existingSchedule.endTime()) && endTime.isAfter(existingSchedule.startTime())) {
                    Notification.show("Class overlaps with an existing schedule!", 3000, Notification.Position.MIDDLE);
                    return;
                }
            }
        }

        // Loop over selected days
        for (DayOfWeek day : selectedDays) {
            ClassSchedule newClass = new ClassSchedule(className, classRoom, startTime, endTime);
            weeklySchedule.computeIfAbsent(day, k -> new ArrayList<>()).add(newClass);
        }

        // After adding the class, update the timetable
        refreshTimetable();
        clearForm();

        Notification.show("Class added successfully!", 3000, Notification.Position.BOTTOM_END);
    }

    /*private void refreshTimetable() {
        List<TimeSlot> timeSlots = generateTimeSlots();

        // For each day, check all classes and add them to the corresponding time slots
        for (DayOfWeek day : DayOfWeek.values()) {
            if (weeklySchedule.containsKey(day)) {
                for (ClassSchedule classSchedule : weeklySchedule.get(day)) {
                    // Loop over all time slots and check if the class overlaps
                    for (TimeSlot slot : timeSlots) {
                        // Get start and end time of the time slot in 24-hour format
                        LocalTime slotStartTime = slot.getStartTime();
                        LocalTime slotEndTime = slot.getEndTime();

                        // Check if the class's time range overlaps with this slot
                        if (classSchedule.startTime().isBefore(slotEndTime) && classSchedule.endTime().isAfter(slotStartTime)) {
                            slot.setClassesForDay(day, classSchedule);
                        }
                    }
                }
            }
        }

        timetableGrid.setItems(timeSlots);
    }*/



    private List<TimeSlot> generateTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        LocalTime time = LocalTime.of(8, 0);

        while (!time.isAfter(LocalTime.of(18, 0))) {
            slots.add(new TimeSlot(time, time.plusMinutes(60)));
            time = time.plusMinutes(60);
        }

        return slots;
    }

    private void clearForm() {
        classNameField.clear();
        classRoomField.clear();
        startTimePicker.clear();
        endTimePicker.clear();
        dayPicker.clear();
    }

    public record ClassSchedule(String className, String classRoom, LocalTime startTime, LocalTime endTime) {
    }

    public static class TimeSlot {
        private final String timeRange;
        private final LocalTime start;
        private final LocalTime end;
        private String mondayClasses = "";
        private String tuesdayClasses = "";
        private String wednesdayClasses = "";
        private String thursdayClasses = "";
        private String fridayClasses = "";
        //private String saturdayClasses = "";
        //private String sundayClasses = "";

        private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        public TimeSlot(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
            this.timeRange = formatTime(start) + " - " + formatTime(end);
        }

        private String formatTime(LocalTime time) {
            return time.format(timeFormatter); // Format time as 12-hour (AM/PM)
        }

        public String getTimeRange() {
            return timeRange;
        }

        // Getters for each day's classes
        public String getMondayClasses() {
            return mondayClasses;
        }

        public String getTuesdayClasses() {
            return tuesdayClasses;
        }

        public String getWednesdayClasses() {
            return wednesdayClasses;
        }

        public String getThursdayClasses() {
            return thursdayClasses;
        }

        public String getFridayClasses() {
            return fridayClasses;
        }

       /* public String getSaturdayClasses() {
            return saturdayClasses;
        }*/

        /*public String getSundayClasses() {
            return sundayClasses;
        }*/

        // Method to add classes to a specific day
        public void setClassesForDay(DayOfWeek day, ClassSchedule classSchedule) {
            String classDetails = classSchedule.className() + " (" + classSchedule.classRoom() + ")";

            switch (day) {
                case MONDAY -> mondayClasses += classDetails + "\n";
                case TUESDAY -> tuesdayClasses += classDetails + "\n";
                case WEDNESDAY -> wednesdayClasses += classDetails + "\n";
                case THURSDAY -> thursdayClasses += classDetails + "\n";
                case FRIDAY -> fridayClasses += classDetails + "\n";
            }
        }



        // Get the start and end times in 24-hour format
        public LocalTime getStartTime() {
            return start;
        }

        public LocalTime getEndTime() {
            return end;
        }
    }
}
