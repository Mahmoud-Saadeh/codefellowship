package com.example.codefellowship.web;

import com.example.codefellowship.domain.ApplicationUser;
import com.example.codefellowship.domain.Post;
import com.example.codefellowship.infrastructure.ApplicationUserRepo;
import com.example.codefellowship.infrastructure.PostRepo;
import com.example.codefellowship.infrastructure.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PostController {
    @Autowired
    ApplicationUserRepo applicationUserRepo;

    @Autowired
    PostRepo postRepo;

    @Autowired
    UserService userService;

    @PostMapping("/posts/{id}")
    public RedirectView addPost(@RequestParam String body, @PathVariable Long id) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());

        ApplicationUser applicationUser = userService.findById(id);
        Long passedId = applicationUser.getId();
        boolean admin = applicationUser.getId().equals(currentUser.getId());

        for (GrantedAuthority role: userDetails.getAuthorities()) {
            if (role.toString().equals("ADMIN") || applicationUser.getId().equals(currentUser.getId())){
                admin = true;
                passedId = id;
            }
        }

        if (!id.equals(currentUser.getId()) && !admin){
            throw new Exception("You are not allowed to add a post");
        }
        Post post = new Post(body,currentUser);
        postRepo.save(post);

        return new RedirectView("/myprofile");
    }

    @GetMapping("/feed")
    public String getFeed(Model model){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());

        List<Post> posts = postRepo.findAll();

        List<Post> filteredPost = new ArrayList<>();
        List<List> filteredPosts = new ArrayList<>();

        for (ApplicationUser us: currentUser.getFollowing()) {
            System.out.println(us.getId());
            filteredPost = (posts.stream().filter(post -> post.getApplicationUser().getId().equals(us.getId())).collect(Collectors.toList()));
            filteredPosts.add(filteredPost);
        }

        List<Post> sortedFilteredPosts = new ArrayList<>();
        for (List li: filteredPosts) {
            for (Object po:  li) {
                sortedFilteredPosts.add(((Post) po));
            }
        }
        sortedFilteredPosts.sort((o1,o2) -> o2.getDate().compareTo(o1.getDate()));

        model.addAttribute("posts", sortedFilteredPosts);

        return "feed";
    }
}
