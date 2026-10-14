
package model;

public record Service(
        int id,
        String serviceName,
        String description,
        double price,
        int durationMinutes
) {
}
