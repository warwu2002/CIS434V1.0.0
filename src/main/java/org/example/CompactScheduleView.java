package org.example;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Compact Timetable")
@Route("compact-schedule") // The new route for the compact view
public class CompactScheduleView extends VerticalLayout {

    private final Grid<Main.TimeSlot> timetableGrid;

    public CompactScheduleView() {
        setWidthFull();
        setHeightFull();

        // Add a header
        add(new H1("Compact Timetable"));

        // Create the grid
        timetableGrid = new Grid<>(Main.TimeSlot.class, false);

        // Add columns to the grid
        timetableGrid.addColumn(Main.TimeSlot::getTimeRange).setHeader("Time").setAutoWidth(true);
        timetableGrid.addColumn(Main.TimeSlot::getMondayClasses).setHeader("Monday").setAutoWidth(true);
        timetableGrid.addColumn(Main.TimeSlot::getTuesdayClasses).setHeader("Tuesday").setAutoWidth(true);
        timetableGrid.addColumn(Main.TimeSlot::getWednesdayClasses).setHeader("Wednesday").setAutoWidth(true);
        timetableGrid.addColumn(Main.TimeSlot::getThursdayClasses).setHeader("Thursday").setAutoWidth(true);
        timetableGrid.addColumn(Main.TimeSlot::getFridayClasses).setHeader("Friday").setAutoWidth(true);

        timetableGrid.setWidthFull();

        // Remove empty time slots for the compact schedule
        List<Main.TimeSlot> filteredTimeSlots = Main.getTimeSlots().stream()
                .filter(slot -> !slot.getMondayClasses().isEmpty()
                        || !slot.getTuesdayClasses().isEmpty()
                        || !slot.getWednesdayClasses().isEmpty()
                        || !slot.getThursdayClasses().isEmpty()
                        || !slot.getFridayClasses().isEmpty())
                .toList();

        // Set the filtered time slots to the grid
        timetableGrid.setItems(filteredTimeSlots);

        // Add the grid to the layout
        add(timetableGrid);
    }
}
