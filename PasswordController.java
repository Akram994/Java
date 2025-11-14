// PasswordController.java (একটি নতুন কন্ট্রোলার)

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;

@Controller
public class PasswordController {

    private final PasswordEncoder passwordEncoder;
    private final String USER_FILE = "users.json";

    public PasswordController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/update_password.php")
    public String updatePassword(
            @RequestParam("old_password") String oldPassword,
            @RequestParam("new_password") String newPassword,
            @RequestParam("confirm_password") String confirmPassword) {
        
        try {
            // users.json ফাইল পড়া
            ObjectMapper mapper = new ObjectMapper();
            File userFile = new File(USER_FILE);
            Map<String, String> userData = mapper.readValue(userFile, Map.class);
            
            // ১. পুরানো পাসওয়ার্ড চেক করা
            if (!passwordEncoder.matches(oldPassword, userData.get("password_hash"))) {
                return "redirect:/change_password.php?error=oldpass";
            }

            // ২. নতুন পাসওয়ার্ড দুটি মেলানো
            if (!newPassword.equals(confirmPassword)) {
                return "redirect:/change_password.php?error=nomatch";
            }
            
            // ৩. নতুন পাসওয়ার্ড হ্যাশ এবং সেভ করা
            userData.put("password_hash", passwordEncoder.encode(newPassword));
            mapper.writerWithDefaultPrettyPrinter().writeValue(userFile, userData);

            return "redirect:/change_password.php?success=1";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/change_password.php?error=unknown";
        }
    }
}
