package yinonx.apitest.pages;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Dashboard")
@Route(value = "dashboard")
@RolesAllowed("ADMIN")
public class DashboardView extends Div {

    public DashboardView() {
        addClassName("dashboard-view");

        // Create and add components here
        add(createHighlight("Current users", "745", 33.7));
        add(createHighlight("View events", "54.6k", -112.45));
        // Add other components as needed
    }

    private Div createHighlight(String title, String value, Double percentage) {
        // Create a div for each highlight
        Div highlight = new Div();
        highlight.addClassName("highlight");

        // Title
        H2 titleHeader = new H2(title);
        titleHeader.addClassName("highlight-title");
        highlight.add(titleHeader);

        // Value
        Span valueSpan = new Span(value);
        valueSpan.addClassName("highlight-value");
        highlight.add(valueSpan);

        // Percentage
        Span percentageSpan = new Span(percentage.toString());
        percentageSpan.addClassName("highlight-percentage");
        highlight.add(percentageSpan);

        // Add logic for styling based on percentage
        if (percentage > 0) {
            highlight.addClassName("success");
        } else if (percentage < 0) {
            highlight.addClassName("error");
        }

        return highlight;
    }
}
