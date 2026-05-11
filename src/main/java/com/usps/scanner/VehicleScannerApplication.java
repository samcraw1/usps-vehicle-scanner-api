// Says what folder/group this file belongs to. Must match the actual folder path.
package com.usps.scanner;

// Borrows two tools from Spring Boot's toolbox so we can use them below.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// "Hey Spring — THIS class is the main app. Auto-configure everything and scan
// my folders for controllers, services, etc." One sticker = a ton of magic.
@SpringBootApplication
public class VehicleScannerApplication {

    // The front door. Java always starts here.
    public static void main(String[] args) {
        // One line that does it all: starts the web server on port 8080,
        // wires up every @RestController/@Service Spring finds, and starts
        // listening for HTTP requests like POST /api/inspect.
        SpringApplication.run(VehicleScannerApplication.class, args);
    }
}
