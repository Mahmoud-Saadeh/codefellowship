package com.example.codefellowship.web;

import com.example.codefellowship.domain.ApplicationUser;
import com.example.codefellowship.domain.Post;
import com.example.codefellowship.infrastructure.ApplicationUserRepo;
import com.example.codefellowship.infrastructure.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    ApplicationUserRepo applicationUserRepo;
    @Autowired
    BCryptPasswordEncoder encoder;


    @GetMapping("/myprofile")
    public String getProfilePage(ModelMap model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ApplicationUser applicationUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());

        return profileData(model, applicationUser, true);
    }

    private String profileData(ModelMap model, ApplicationUser applicationUser, boolean showPostForm) {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter2.format(applicationUser.getDateOfBirth());

        List<Post> posts = applicationUser.getPosts();
//        posts.sort(Comparator.comparing(Post::getDate));
        posts.sort((o1,o2) -> o2.getDate().compareTo(o1.getDate()));

        model.addAttribute("username", applicationUser.getUsername());
        model.addAttribute("firstName", applicationUser.getFirstName());
        model.addAttribute("lastName", applicationUser.getLastName());

        model.addAttribute("date", formattedDate);
        model.addAttribute("bio", applicationUser.getBio());
        model.addAttribute("showPostForm", showPostForm);
        model.addAttribute("posts", posts);
        model.addAttribute("id", applicationUser.getId());
        return "profile";
    }

    @GetMapping("users")
    public  String getAllUsers(Model model){
        List<ApplicationUser> users = applicationUserRepo.findAll();
        model.addAttribute("users", users);

        return "users";
    }
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable Long id, ModelMap model) {
        ApplicationUser applicationUser = applicationUserRepo.findById(id).orElseThrow();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());

        boolean showPostForm;

        showPostForm = applicationUser.getId().equals(currentUser.getId());

        return profileData(model, applicationUser, showPostForm);
    }

    @PostMapping("/edituser/{id}")
    public RedirectView editProfile(@RequestParam String username,
                                    @RequestParam String firstName,
                                    @RequestParam String lastName,
                                    @RequestParam String bio,
                                    @RequestParam String dateOfBirth,
                                    @PathVariable Long id) throws ParseException {

        ApplicationUser applicationUser = applicationUserRepo.findById(id).orElseThrow();
        applicationUser.setUsername(username);
        applicationUser.setFirstName(firstName);
        applicationUser.setLastName(lastName);
        applicationUser.setBio(bio);

        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date=formatter2.parse(dateOfBirth);
        applicationUser.setDateOfBirth(date);

        Authentication authentication = new UsernamePasswordAuthenticationToken(applicationUser, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        applicationUserRepo.save(applicationUser);

        return new RedirectView("/myprofile");
    }

    @PostMapping("/editpassword/{id}")
    public RedirectView editProfile(@RequestParam String currentPassword,
                                    @RequestParam String newPassword,
                                    @PathVariable Long id,
                                    RedirectAttributes redir){

        ApplicationUser applicationUser = applicationUserRepo.findById(id).orElseThrow();
        RedirectView redirectView= new RedirectView("/myprofile",true);

        if (!encoder.matches(currentPassword,applicationUser.getPassword())){
            redir.addFlashAttribute("showError",true);
            redir.addFlashAttribute("errorPassword","Current Password is wrong");
            return redirectView;
        }else {
            redir.addFlashAttribute("showError",false);
        }

        applicationUser.setPassword(encoder.encode(newPassword));
        applicationUserRepo.save(applicationUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(applicationUser, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return redirectView;
    }

}
