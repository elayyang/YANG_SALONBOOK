
package model;

public record Appointment(
        int id,
        int clientId,
        int serviceId,
        String appointmentDate,
        String appointmentTime,
        String status
) {
}
