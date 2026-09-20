package Clinic_Management.DoctorScheduleService.entity;

import java.time.LocalTime;

public enum ShiftSession {
    MORNING(LocalTime.of(7, 30), LocalTime.of(11, 30)),
    AFTERNOON(LocalTime.of(13, 0), LocalTime.of(17, 0));

    private final LocalTime startTime;
    private final LocalTime endTime;

    ShiftSession(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}