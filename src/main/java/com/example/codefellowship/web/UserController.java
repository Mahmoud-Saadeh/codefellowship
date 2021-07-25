package com.example.codefellowship.web;

import com.example.codefellowship.domain.ApplicationUser;
import com.example.codefellowship.domain.Post;
import com.example.codefellowship.infrastructure.ApplicationUserRepo;
import com.example.codefellowship.infrastructure.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    ApplicationUserRepo applicationUserRepo;

    @Autowired
    PostRepo postRepo;

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ApplicationUser applicationUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());

        return profileData(model, applicationUser, true);
    }

    private String profileData(Model model, ApplicationUser applicationUser, boolean showPostForm) {
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter2.format(applicationUser.getDateOfBirth());

        List<Post> posts = applicationUser.getPosts();

        model.addAttribute("username", applicationUser.getUsername());
        model.addAttribute("firstName", applicationUser.getFirstName());
        model.addAttribute("lastName", applicationUser.getLastName());

        model.addAttribute("date", formattedDate);
        model.addAttribute("bio", applicationUser.getBio());
        model.addAttribute("showPostForm", showPostForm);
        model.addAttribute("posts", posts);
        model.addAttribute("id", applicationUser.getId());
        System.out.println(showPostForm + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return "profile";
    }

    @GetMapping("users")
    public  String getAllUsers(Model model){
        List<ApplicationUser> users = applicationUserRepo.findAll();
        model.addAttribute("users", users);

        return "users";
    }
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        ApplicationUser applicationUser = applicationUserRepo.findById(id).orElseThrow();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());

        boolean showPostForm;

        showPostForm = applicationUser.getId().equals(currentUser.getId());

        return profileData(model, applicationUser, showPostForm);
    }

    @PostMapping("/posts/{id}")
    public RedirectView addPost(@RequestParam String body, @PathVariable Long id) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());
        if (!id.equals(currentUser.getId())){
            throw new Exception("You are not allowed to add a post");
        }
        Post post = new Post(body,currentUser);
        postRepo.save(post);

        return new RedirectView("/profile");
    }
}
