// WelcomeController.java

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
public class WelcomeController {

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/welcome.php") // PHP ফাইলের URL-টিই ব্যবহার করা হলো
    public ResponseEntity<String> handleWelcome(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message,
            @RequestParam("userfile") MultipartFile file,
            HttpServletRequest request) {

        try {
            // ১. আইপি এবং সময় সংগ্রহ করা
            String ipAddress = request.getRemoteAddr();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // ২. IP.txt ফাইলে লগ করা
            String ipLogString = "Timestamp: " + timestamp + ", IP Address: " + ipAddress + "\n";
            Files.writeString(Paths.get("IP.txt"), ipLogString, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            // ৩. ফাইল আপলোড ম্যানেজ করা
            String originalName = file.getOriginalFilename();
            String uniqueName = UUID.randomUUID().toString() + "-" + originalName;
            Path targetPath = Paths.get(UPLOAD_DIR + uniqueName);
            
            // 'uploads' ফোল্ডার না থাকলে তৈরি করা
            new File(UPLOAD_DIR).mkdirs(); 
            // ফাইলটি সেভ করা
            file.transferTo(targetPath);

            // ৪. data.txt ফাইলে সব ডেটা সেভ করা
            String dataString = "IP: " + ipAddress +
                                ", Timestamp: " + timestamp +
                                ", নাম: " + name +
                                ", ইমেল: " + email +
                                ", মেসেজ: " + message +
                                ", ফাইলের পাথ: " + targetPath.toString() + "\n";
            
            Files.writeString(Paths.get("data.txt"), dataString, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            return ResponseEntity.ok("ডেটা, ফাইল এবং আইপি সফলভাবে সেভ হয়েছে!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("ত্রুটি: সার্ভারে সমস্যা হয়েছে।");
        }
    }
}
