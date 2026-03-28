package pl.karpik.todoapp.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import pl.karpik.todoapp.model.Todo;
import pl.karpik.todoapp.model.User;
import pl.karpik.todoapp.service.TodoService;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.karpik.todoapp.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.combobox.ComboBox;

@PermitAll
@Route("")
public class TodoView extends VerticalLayout {

    private final TodoService todoService;
    private final UserService userService;

    private final Grid<Todo> grid = new Grid<>(Todo.class);
    private final TextField taskField = new TextField();
    private final DatePicker deadlineField = new DatePicker();
    private final Checkbox showOnlyActive = new Checkbox("Tylko aktywne");
    private final ComboBox<String> filterBox = new ComboBox<>();
    private final H3 title = new H3("📝 Todo Planner");
    private final H3 counter = new H3();

    public TodoView(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;

        setWidthFull();
        setAlignItems(Alignment.CENTER);
        getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("min-height", "100vh");

        // INPUTY
        taskField.setPlaceholder("Wpisz zadanie");
        taskField.setWidth("300px");
        taskField.setHeight("40px");

        deadlineField.setPlaceholder("Termin");
        deadlineField.setWidth("150px");
        deadlineField.setHeight("40px");

        filterBox.setHeight("40px");

        Button addButton = new Button("Dodaj", e -> addTodo());
        addButton.getStyle().set("background", "#4CAF50").set("color", "white");

        addButton.setHeight("40px");

        taskField.addKeyPressListener(Key.ENTER, e -> addTodo());

        // GRID
        grid.removeAllColumns();
        grid.setWidthFull();
        grid.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px var(--lumo-shade-10pct)");

        // ZADANIE
        grid.addComponentColumn(todo -> {
            Button btn = new Button(todo.getTitle(), e -> {
                Dialog dialog = new Dialog();
                TextField edit = new TextField();
                edit.setValue(todo.getTitle());

                Button save = new Button("Zapisz", ev -> {
                    todoService.update(todo, edit.getValue());
                    dialog.close();
                    updateList();
                });

                dialog.add(edit, save);
                dialog.open();
            });
            return btn;
        }).setHeader("Zadanie");

        // TERMIN
        grid.addComponentColumn(todo -> {
            String text = todo.getDeadline() == null ? "-" : todo.getDeadline().toString();

            Button btn = new Button(text, e -> {
                Dialog dialog = new Dialog();
                DatePicker picker = new DatePicker("Nowy termin");
                picker.setValue(todo.getDeadline());

                Button save = new Button("Zapisz", ev -> {
                    todo.setDeadline(picker.getValue());
                    todoService.save(todo);
                    dialog.close();
                    updateList();
                });

                dialog.add(picker, save);
                dialog.open();
            });

            if (todo.getDeadline() != null && todo.getDeadline().isBefore(java.time.LocalDate.now())) {
                btn.getStyle().set("color", "red");
            }

            return btn;
        }).setHeader("Termin");

        // STATUS (ładny)
        grid.addComponentColumn(todo -> {
            Button btn = new Button(
                    todo.isCompleted() ? "Zrobione" : "Do zrobienia",
                    e -> {
                        todoService.toggle(todo);
                        updateList();
                    }
            );

            btn.getStyle()
                    .set("color", "white")
                    .set("border-radius", "8px");

            if (todo.isCompleted()) {
                btn.getStyle().set("background", "#4CAF50");
            } else {
                btn.getStyle().set("background", "#f44336");
            }

            return btn;
        }).setHeader("Status");

        // USUŃ
        grid.addComponentColumn(todo -> {
            Button delete = new Button("Usuń", e -> {
                Dialog d = new Dialog();

                Button yes = new Button("Tak", ev -> {
                    todoService.delete(todo);
                    d.close();
                    updateList();
                });

                Button no = new Button("Nie", ev -> d.close());

                d.add(new H3("Na pewno usunąć?"), new HorizontalLayout(yes, no));
                d.open();
            });

            delete.getStyle()
                    .set("background", "#ff4d4f")
                    .set("color", "white");

            return delete;
        }).setHeader("Usuń");

        // THEME
        Button themeButton = new Button();
        syncThemeButtonLabel(themeButton, UI.getCurrent().getElement().getThemeList());
        themeButton.addClickListener(e -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }

            syncThemeButtonLabel(themeButton, themeList);

            themeButton.setHeight("40px");
        });

        // USER MENU
        MenuBar userMenu = new MenuBar();
        MenuItem userItem = userMenu.addItem("👤 " + getLoggedUsername());

        userItem.getSubMenu().addItem("Zmień hasło",
                e -> UI.getCurrent().navigate("change-password"));

        userItem.getSubMenu().addItem("Wyloguj", e -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().navigate("login");
        });

        // FILTER
        filterBox.setItems("Wszystkie", "Dziś", "Jutro", "Po terminie");
        filterBox.setValue("Wszystkie");
        filterBox.setWidth("150px");
        filterBox.setMinWidth("150px");
        filterBox.addValueChangeListener(e -> updateList());

        // TOOLBAR
        HorizontalLayout toolbar = new HorizontalLayout(
                taskField, deadlineField, addButton, themeButton, filterBox
        );
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.expand(taskField);
        toolbar.getStyle().set("gap","10px");

        // HEADER
        HorizontalLayout header = new HorizontalLayout(title, userMenu);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // CONTENT (środek!)
        VerticalLayout content = new VerticalLayout(
                header, toolbar, counter, showOnlyActive, grid
        );
        content.setWidth("800px");
        content.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 30px var(--lumo-shade-10pct)")
                .set("padding", "var(--lumo-space-l)");

        add(content);

        showOnlyActive.addValueChangeListener(e -> updateList());

        updateList();
    }

    private void updateList() {
        String username = getLoggedUsername();
        User user = userService.findByUsername(username);

        var todos = todoService.findByUser(user);

        if (!filterBox.getValue().equals("Wszystkie")) {
            var today = java.time.LocalDate.now();

            todos = todos.stream().filter(todo -> {
                if (todo.getDeadline() == null) return false;

                switch (filterBox.getValue()) {
                    case "Dziś":
                        return todo.getDeadline().equals(today);
                    case "Jutro":
                        return todo.getDeadline().equals(today.plusDays(1));
                    case "Po terminie":
                        return todo.getDeadline().isBefore(today);
                }
                return true;
            }).toList();
        }

        long completed = todos.stream().filter(Todo::isCompleted).count();
        counter.setText("Masz " + todos.size() + " zadań | " + completed + " ukończone");

        if (showOnlyActive.getValue()) {
            grid.setItems(todos.stream().filter(t -> !t.isCompleted()).toList());
        } else {
            grid.setItems(todos);
        }
    }

    private void addTodo() {
        if (taskField.getValue().trim().isEmpty()) {
            Notification.show("Wpisz nazwę zadania");
            return;
        }

        Todo todo = new Todo(taskField.getValue());
        todo.setDeadline(deadlineField.getValue());

        User user = userService.findByUsername(getLoggedUsername());
        todo.setUser(user);

        todoService.save(todo);

        updateList();
        taskField.clear();
        deadlineField.clear();

        taskField.focus();
    }

    private String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void syncThemeButtonLabel(Button themeButton, ThemeList themeList) {
        themeButton.setText(themeList.contains(Lumo.DARK) ? "Jasny motyw" : "Ciemny motyw");
    }
}
