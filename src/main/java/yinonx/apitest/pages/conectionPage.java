package yinonx.apitest.pages;

import java.util.Date;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Horizontal;

@Route(value = "/")
public class conectionPage extends VerticalLayout{
    private String sessionId, userName;
    private static final String IMAGE_LOGO_URL = "https://media.npr.org/assets/img/2021/12/28/hsieh_angela_nprpchh_videogamesa_wide-7b51b98254a8a594e0ac928517b589efb3763578-s1100-c50.jpg";

    public conectionPage() 
    {
        System.out.println("start conectionPage=====>>>>");

        add(new H2("Conection Page"));

        sessionId = VaadinSession.getCurrent().getSession().getId();
        userName = (String)VaadinSession.getCurrent().getSession().getAttribute("userName");

       
        Image imageLogo = new Image(IMAGE_LOGO_URL, "logo");
        imageLogo.setHeight("280px");

        HorizontalLayout heder = new HorizontalLayout();


        Button btnLogIn = new Button("log In", e -> logIn());
        Button btnRegister = new Button("Register", e -> register());

        heder.add(btnLogIn, btnRegister);

        add(new Text(new Date()+""));
        add(imageLogo);
        add(new H1("Welcome Gest"));
        add(new H3("( sessionId: "+sessionId+")"));
        add(heder);

        setAlignItems(Alignment.CENTER);

    }

    private void register() {
        UI.getCurrent().navigate("/register");
    }

    private void logIn() {
        UI.getCurrent().navigate("/login");
    }
}
