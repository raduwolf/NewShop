package org.sci.myshop.controller;

import org.sci.myshop.dao.Role;
import org.sci.myshop.dao.User;
import org.sci.myshop.services.interfaces.RolesService;
import org.sci.myshop.services.interfaces.SecurityService;
import org.sci.myshop.services.interfaces.UserService;
import org.sci.myshop.utils.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private ServletContext servletContext;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/welcome";
    }

    @GetMapping("/login")
    public String login(Model model, String error) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

            return "login";
    }

    @GetMapping({"/", "/welcome"})
    public String welcome(Model model) {
        if (servletContext.getAttribute("init")==null) {
            initData();
            servletContext.setAttribute("init", true);
        }else{
            List<User> list = userService.findAllUsers();
            model.addAttribute("listOfUsers",list);
        }
            return "welcome";
        }

    private void initData(){
        List<Role> roles = new ArrayList<>();
        Role adminRole =new Role();
        Role userRole =new Role();
        adminRole.setName("ADMIN");
        userRole.setName("USER");
        roles.add(adminRole);
        roles.add(userRole);
        rolesService.saveRoles(roles);
        roles=rolesService.findAllRoles();
        User adminUser = new User();
        adminUser.setUsername("Wolf1800");
        adminUser.setPassword("Wolf1800");
        adminUser.setFullName("Wolf P");
        adminUser.setAddress("address");

        adminUser.setRole(roles.get(0));

        userService.save(adminUser);


        for (int i =0;i<15;i++){
            User fakeUser = new User();
            fakeUser.setUsername("User_"+i);
            fakeUser.setPassword("User_"+i);
            fakeUser.setFullName("User_"+i+" Fake");
            fakeUser.setAddress("address_"+i);
            fakeUser.setRole(roles.get(1));
            userService.save(fakeUser);
        }

    }
}
