package yinonx.apitest.pages;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.scopes.VaadinSessionScope;
import com.vaadin.flow.theme.Theme;

import yinonx.apitest.classes.User;
import yinonx.apitest.repos.UserRepository;
import yinonx.apitest.services.UserService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//@Theme("my theme")
@Route(value = "/login")
public class loginPage extends VerticalLayout {
    
    private static final String BACKGROUND_IMAGE_URL = "https://images.fineartamerica.com/images/artworkimages/mediumlarge/3/5-video-games-pattern-gaming-console-computer-play-mister-tee.jpg";
    private LoginForm loginForm;
    private UserService userService;

    public loginPage(UserService userService) {

             
       
        this.userService = userService;
        Button registerBotton = new Button("register");
        //<theme-editor-local-classname>
        registerBotton.addClassName("login-page-button-1");
        registerBotton.getStyle().setBackgroundColor("black");
        registerBotton.getStyle().setColor("white");
        registerBotton.addClickListener(e->{
            UI.getCurrent().navigate(RegistrationPage.class);
        });
        getElement().getStyle().set("background", "url(" + BACKGROUND_IMAGE_URL + ")");
        getElement().getStyle().set("background-pedding", "0px");
        add(new H1("log in"));
        loginForm = new LoginForm();
        //<theme-editor-local-classname>
        loginForm.addClassName("login-page-login-form-1");
        // center loginForm
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        loginForm.addLoginListener(e -> checkLogin(e.getUsername(), e.getPassword()));
        add(loginForm,registerBotton);
        //<theme-editor-local-classname>
        addClassName("login-page-vertical-layout-1");

        
    }

    private void checkLogin(String username, String password){
       boolean res = userService.isUserExists(username, password);
       System.out.println(username+','+password);
      if (res){
        UI.getCurrent().getSession().setAttribute("username", username);
        VaadinSession.getCurrent().getSession().setAttribute("username", username);
       // Notification.show("loginPage the currnt logged in user in coockies is " + (String)VaadinSession.getCurrent().getAttribute("username"));  
            UI.getCurrent().navigate(gamePage.class);
    }
      else{     
           Notification.show("incorrect username or password. please try again. if you do not have an accout with us you may sign up with the register bottom", 3000, Position.TOP_CENTER);
           loginForm.setEnabled(true);
    }
}

}


