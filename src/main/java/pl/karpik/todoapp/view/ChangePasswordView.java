package pl.karpik.todoapp.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.karpik.todoapp.service.UserService;

@PermitAll
@Route("change-password")
public class ChangePasswordView extends VerticalLayout {

    public ChangePasswordView(UserService userService) {
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

        H3 title = new H3("🔑 Zmiana hasła");

        PasswordField oldPassword = new PasswordField("Stare hasło");
        oldPassword.setWidthFull();

        PasswordField newPassword = new PasswordField("Nowe hasło");
        newPassword.setWidthFull();

        Button changeButton = new Button("Zmień hasło", event -> {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            boolean success = userService.changePassword(
                    username,
                    oldPassword.getValue(),
                    newPassword.getValue()
            );
            if (success) {
                Notification.show("Hasło zmienione");
                oldPassword.clear();
                newPassword.clear();
            } else {
                Notification.show("Błędne stare hasło");
            }
        });
        changeButton.setWidthFull();

        Anchor backLink = new Anchor("", "Wróć do listy zadań");

        card.add(title, oldPassword, newPassword, changeButton, backLink);
        add(card);

        oldPassword.focus();
    }
}
