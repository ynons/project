package yinonx.apitest.pages;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.scopes.VaadinSessionScope;

import yinonx.apitest.classes.User;
import yinonx.apitest.repos.UserRepository;
import yinonx.apitest.services.UserService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route(value = "/login")
public class loginPage extends VerticalLayout {

    private LoginForm loginForm;
    private UserService userService;

    public loginPage(UserService userService) {

             
       
        this.userService = userService;

        add(new H1("log in"));
        loginForm = new LoginForm();
        // center loginForm
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginForm.addLoginListener(e -> checkLogin(e.getUsername(), e.getPassword()));

        add(loginForm);
      
        
    }

    private void checkLogin(String username, String password){
       boolean res = userService.isUserExists(username, password);
       System.out.println(username+','+password);
      if (res){
        UI.getCurrent().getSession().setAttribute("username", username);
        VaadinSession.getCurrent().getSession().setAttribute("username", username);
        Notification.show("loginPage the currnt logged in user in coockies is " + (String)VaadinSession.getCurrent().getAttribute("username"));  
            UI.getCurrent().navigate(gamePage.class);
    }
      else{     
           Notification.show("No user by the name "+ username + " was found", 3000, Position.TOP_CENTER);
    }
}

}


