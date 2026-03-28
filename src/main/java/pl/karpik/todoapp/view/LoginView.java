package pl.karpik.todoapp.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("350px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 30px var(--lumo-shade-20pct)")
                .set("padding", "30px");

        H3 title = new H3("🔐 Logowanie");

        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);

        Anchor registerLink = new Anchor("register", "Nie masz konta? Zarejestruj się");

        card.add(title, loginForm, registerLink);
        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean hasError = event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error");
        loginForm.setError(hasError);
    }
}
