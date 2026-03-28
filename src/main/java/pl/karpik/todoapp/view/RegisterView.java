package pl.karpik.todoapp.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.karpik.todoapp.service.UserService;

@Route("register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    public RegisterView(UserService userService) {
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

        H3 title = new H3("📝 Rejestracja");

        TextField username = new TextField("Login");
        username.setWidthFull();

        PasswordField password = new PasswordField("Hasło");
        password.setWidthFull();

        Button registerButton = new Button("Zarejestruj", event -> {
            boolean success = userService.register(
                    username.getValue(),
                    password.getValue()
            );
            if (success) {
                Notification.show("Konto utworzone!");
                getUI().ifPresent(ui -> ui.navigate("login"));
            } else {
                Notification.show("Użytkownik już istnieje!");
            }
        });
        registerButton.setWidthFull();

        Anchor loginLink = new Anchor("login", "Masz już konto? Zaloguj się");

        card.add(title, username, password, registerButton, loginLink);
        add(card);

        username.focus();
    }
}
