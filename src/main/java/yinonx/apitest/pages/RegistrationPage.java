package yinonx.apitest.pages;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import yinonx.apitest.services.UserService;

import yinonx.apitest.classes.User;

import java.util.Random;
import java.util.stream.Stream;

@Route(value = "/register")

public class RegistrationPage extends VerticalLayout {
    private UserService userService;

    private H3 title;

    private TextField userName;

    private PasswordField password;
    private PasswordField passwordConfirm;

    private Checkbox allowMarketing;

    private Span errorMessageField;

    private Button submitButton;

    public RegistrationPage(UserService userService)

    {
        this.userService = userService;
        Div container = new Div();
        container.getStyle().set("margin", "auto");
        container.getStyle().set("margin-top", "50px"); // Adjust top margin as needed
        container.getStyle().setPadding("20px"); // Adjust top margin as needed

        title = new H3("Signup form");
        title.getStyle().setPadding("20px");
        userName = new TextField("First name");
        userName = new TextField("Last name");

        userName.getStyle().setPadding("5px");

        allowMarketing = new Checkbox("Allow Marketing Emails?");
        allowMarketing.getStyle().set("margin-top", "10px");

        password = new PasswordField("Password");
        passwordConfirm = new PasswordField("Confirm password");

        setRequiredIndicatorVisible(userName, password,
                passwordConfirm);

        errorMessageField = new Span();

        submitButton = new Button("Join the community");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        container.add(title, userName, password,
                passwordConfirm, allowMarketing, errorMessageField,
                submitButton);

        add(container);
        // Max width of the Form
        setMaxWidth("500px");

        submitButton.addClickListener(
                e -> registerInDataBase(password.getValue(), passwordConfirm.getValue(), userName.getValue()));

    }

   private void registerInDataBase(String passward, String confirmPassward, String userName) {
        if(passward!=null && confirmPassward!=null && userName!=null)
        {
            if(passward.equals(confirmPassward))
            {
            
                    User user = new User();
                    user.setUn(userName);
                    user.setPw(passward);
                    user.setId(generateRandomLong());
                    if (userService.addUser(user)){
                    UI.getCurrent().getSession().setAttribute("username", userName);
                    UI.getCurrent().navigate(gamePage.class);
                    } 
                    else{Notification.show("failed to register");}
            }
        }

        
    }
   //recursive, may not work as data gets bigger
    // public long generateRandomLong() {
    //     // Create an instance of the Random class
    //     Random random = new Random();

    //     // Generate a random long using nextLong() method
    //     long randomLong = random.nextLong();
    //    if( userService.findUserById(randomLong) == null)
    //    {
    //     return randomLong;
    //    }
    //    randomLong = generateRandomLong();
    //    return randomLong;
       
    // }
    public long generateRandomLong() {
        Random random = new Random();
        long randomLong;
    
        do {
            randomLong = random.nextLong();
        } while (userService.findUserById(randomLong) != null);
    
        return randomLong;
    }
    
    public PasswordField getPasswordField() {
        return password;
    }

    public PasswordField getPasswordConfirmField() {
        return passwordConfirm;
    }

    public Span getErrorMessageField() {
        return errorMessageField;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    private void setRequiredIndicatorVisible(HasValueAndElement<?, ?>... components) {
        Stream.of(components).forEach(comp -> comp.setRequiredIndicatorVisible(true));
    }

}