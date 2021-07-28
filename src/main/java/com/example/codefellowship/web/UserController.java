package com.example.codefellowship.web;

import com.example.codefellowship.domain.ApplicationUser;
import com.example.codefellowship.domain.Post;
import com.example.codefellowship.infrastructure.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder encoder;

    @GetMapping("/myprofile")
    public String getProfilePage(ModelMap model, HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ApplicationUser applicationUser = userService.findApplicationUserByUsername(userDetails.getUsername());

        return profileData(model, applicationUser, true, applicationUser.getId(), true, applicationUser);
    }

    private String profileData(ModelMap model, ApplicationUser applicationUser, boolean isAuthorized , Long id, boolean showPostForm, ApplicationUser currentUser) {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter2.format(applicationUser.getDateOfBirth());

        List<Post> posts = applicationUser.getPosts();
        posts.sort((o1,o2) -> o2.getDate().compareTo(o1.getDate()));

        boolean isFollowing = currentUser.getFollowing().contains(userService.findById(id));

        model.addAttribute("username", applicationUser.getUsername());
        model.addAttribute("firstName", applicationUser.getFirstName());
        model.addAttribute("lastName", applicationUser.getLastName());

        model.addAttribute("date", formattedDate);
        model.addAttribute("bio", applicationUser.getBio());
        model.addAttribute("isAuthorized", isAuthorized);
        model.addAttribute("showPostForm", showPostForm);
        model.addAttribute("posts", posts);
        model.addAttribute("id", id);
        model.addAttribute("isFollowing", isFollowing);
        return "profile";
    }

    @GetMapping("users")
    public  String getAllUsers(Model model){
        List<ApplicationUser> users = userService.findAll();
        model.addAttribute("users", users);

        return "users";
    }

    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable Long id, ModelMap model) {
        ApplicationUser applicationUser = userService.findById(id);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = userService.findApplicationUserByUsername(userDetails.getUsername());

//        boolean showPostForm;
//
//        showPostForm = applicationUser.getId().equals(currentUser.getId());
        Long passedId = applicationUser.getId();
        boolean isAuthorized = applicationUser.getId().equals(currentUser.getId());
        for (GrantedAuthority role: userDetails.getAuthorities()) {
            if (role.toString().equals("ADMIN") || applicationUser.getId().equals(currentUser.getId())){
                isAuthorized = true;
                passedId = id;
            }
        }

        return profileData(model, applicationUser, isAuthorized, passedId, applicationUser.getId().equals(currentUser.getId()), currentUser);
    }

    @PostMapping("/edituser/{id}")
    public RedirectView editProfile(@RequestParam String username,
                                    @RequestParam String firstName,
                                    @RequestParam String lastName,
                                    @RequestParam String bio,
                                    @RequestParam String dateOfBirth,
                                    @PathVariable Long id) throws ParseException {

        ApplicationUser applicationUser = userService.findById(id);
        applicationUser.setUsername(username);
        applicationUser.setFirstName(firstName);
        applicationUser.setLastName(lastName);
        applicationUser.setBio(bio);

        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date=formatter2.parse(dateOfBirth);
        applicationUser.setDateOfBirth(date);

        saveContext(applicationUser);


        userService.save(applicationUser);

        return new RedirectView("/user/" + id);
    }
//    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/editpassword/{id}")
    public RedirectView editProfile(@RequestParam String currentPassword,
                                    @RequestParam String newPassword,
                                    @PathVariable Long id,
                                    RedirectAttributes redir){

        ApplicationUser applicationUser = userService.findById(id);
        RedirectView redirectView= new RedirectView("/user/" + id,true);

        if (!encoder.matches(currentPassword,applicationUser.getPassword())){
            redir.addFlashAttribute("showError",true);
            redir.addFlashAttribute("errorPassword","Current Password is wrong");
            return redirectView;
        }else {
            redir.addFlashAttribute("showError",false);
        }

        applicationUser.setPassword(encoder.encode(newPassword));
        userService.save(applicationUser);

        saveContext(applicationUser);
        return redirectView;
    }

    private void saveContext(ApplicationUser applicationUser) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = userService.findApplicationUserByUsername(userDetails.getUsername());

        if (currentUser.getId().equals(applicationUser.getId())){
            Authentication authentication = new UsernamePasswordAuthenticationToken(applicationUser, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    @PostMapping("/follow/{id}")
    public RedirectView followUser(@PathVariable Long id, RedirectAttributes redir){
        ApplicationUser applicationUser = userService.findById(id);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = userService.findApplicationUserByUsername(userDetails.getUsername());

//        applicationUser.addFollower(currentUser);

        currentUser.addFollowing(applicationUser);

        userService.save(currentUser);
//        userService.save(applicationUser);

        for (ApplicationUser us : currentUser.getFollowing()) {
            System.out.println(us.getFirstName() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }

        return new RedirectView("/user/" + id);
    }
    @PostMapping("/unfollow/{id}")
    public RedirectView unFollowUser(@PathVariable Long id, RedirectAttributes redir){
        ApplicationUser applicationUser = userService.findById(id);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = userService.findApplicationUserByUsername(userDetails.getUsername());

        currentUser.deleteFollowing(applicationUser);
        userService.save(currentUser);

        return new RedirectView("/user/" + id);
    }
    @GetMapping("/access-denied")
    public String getAccessDenied() {
        return "/403";
    }

}
