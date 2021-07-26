package com.example.codefellowship.web;

import com.example.codefellowship.domain.ApplicationUser;
import com.example.codefellowship.domain.Post;
import com.example.codefellowship.infrastructure.ApplicationUserRepo;
import com.example.codefellowship.infrastructure.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PostController {
    @Autowired
    ApplicationUserRepo applicationUserRepo;

    @Autowired
    PostRepo postRepo;

    @PostMapping("/posts/{id}")
    public RedirectView addPost(@RequestParam String body, @PathVariable Long id) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApplicationUser currentUser = applicationUserRepo.findApplicationUserByUsername(userDetails.getUsername());
        if (!id.equals(currentUser.getId())){
            throw new Exception("You are not allowed to add a post");
        }
        Post post = new Post(body,currentUser);
        postRepo.save(post);

        return new RedirectView("/myprofile");
    }
}
