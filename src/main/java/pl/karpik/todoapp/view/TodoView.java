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
import pl.karpik.todoapp.model.Todo;
import pl.karpik.todoapp.service.TodoService;

@Route("")
public class TodoView extends VerticalLayout{

    private final TodoService todoService;
    private final Grid<Todo> grid = new Grid<>(Todo.class);
    private final TextField taskField = new TextField();
    private final DatePicker deadlineField = new DatePicker();
    private final Checkbox showOnlyActive = new Checkbox("Tylko aktywne");
    private final H3 title = new H3("Todo App");
    private final H3 counter = new H3();

    public TodoView(TodoService todoService)
    {
        this.todoService = todoService;

        taskField.setPlaceholder("Wpisz zadanie");
        taskField.setWidth("400px");

        deadlineField.setPlaceholder("Termin");



        deadlineField.setWidth("150px");

        Button addButton = new Button("Dodaj", event -> addTodo());


        taskField.addKeyPressListener(Key.ENTER, event -> addTodo());


        grid.removeAllColumns();
        grid.setWidthFull();
        grid.getStyle().set("margin-top","20px");

        grid.addComponentColumn(todo -> {
            Button editButton = new Button(todo.getTitle(), event -> {
                Dialog dialog = new Dialog();

                TextField editField = new TextField();
                editField.setValue(todo.getTitle());

                Button saveButton = new Button("Zapisz", e -> {
                    todoService.update(todo, editField.getValue());
                    Notification.show("Zmieniono nazwę");
                    dialog.close();
                    updateList();
                });
                dialog.add(editField, saveButton);
                dialog.open();
            });
            return editButton;
        }).setHeader("Zadanie");

        grid.addComponentColumn(todo -> {

            String text = "-";

            if (todo.getDeadline() != null) {
                text = todo.getDeadline().toString();

                if (todo.getDeadline().isBefore(java.time.LocalDate.now())){
                    
                }
            }
            Button deadlineButton = new Button(text);

            if (todo.getDeadline() != null && todo.getDeadline().isBefore(java.time.LocalDate.now())) {
                deadlineButton.getStyle().set("color","red");
            }
            return deadlineButton;
        }).setHeader("Termin");

        grid.addComponentColumn(todo -> {
            Button statusButton = new Button(todo.isCompleted() ? "✓" : "x", event -> {
                todoService.toggle(todo);
                Notification.show("Zmieniono status");
                updateList();
            });
            if (todo.isCompleted()) {
                statusButton.getStyle().set("color","green");
            } else {
                statusButton.getStyle().set("color","red");
            }
            statusButton.setWidth("60px");

            return statusButton;
        }).setHeader("Status");

        grid.addComponentColumn(todo -> {
            Button deleteButton = new Button("Usuń", event -> {
                Dialog confirmDialog = new Dialog();

                H3 mesage = new H3("Czy napewno chcesz usunąć zadanie?");

                Button yesButton = new Button("Tak", e-> {
                    todoService.delete(todo);
                    Notification.show("Zadanie usunięte");
                    confirmDialog.close();
                    updateList();
                });

                Button noButton = new Button("Nie", e-> confirmDialog.close());

                yesButton.getStyle().set("color","red");
                noButton.getStyle().set("color","gray");

                HorizontalLayout buttons = new HorizontalLayout(yesButton,noButton);

                confirmDialog.add(mesage, buttons);
                confirmDialog.open();
            });

            deleteButton.getStyle().set("color","red");

            return deleteButton;
        }).setHeader("Usuń");

        showOnlyActive.addValueChangeListener(event -> updateList());
        Button themeButton = new Button("Ciemny motyw");

        themeButton.addClickListener(event -> {
            ThemeList themeList = getUI().get().getElement().getThemeList();

            if(themeList.contains("dark")) {
                themeList.remove("dark");
                themeButton.setText("Ciemny motyw");
            } else {
                themeList.add("dark");
                themeButton.setText("Jasny motyw");
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(taskField,deadlineField, addButton,themeButton);

        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.BASELINE);
        toolbar.setSpacing(true);

        add(title,toolbar,counter,showOnlyActive,grid);

        setPadding(true);
        setSpacing(true);

        updateList();
    }

    private void updateList()
    {
        var todos = todoService.findAll();

        long completed = todos.stream()
                .filter(Todo:: isCompleted)
                .count();
        counter.setText("Masz " + todos.size() + " zadań | " + completed + " ukończone");

        if (showOnlyActive.getValue()) {
            grid.setItems(todoService.findActive());
        } else {
            grid.setItems(todos);
        }

    }
    private void addTodo() {
       if (taskField.getValue().trim().isEmpty()){
           Notification.show("Wpisz nazwę zadania");
           return;
       }
       Todo todo = new Todo(taskField.getValue());
       todo.setDeadline(deadlineField.getValue());

       todoService.save(todo);

       Notification.show("Zadanie dodane");

       updateList();

       taskField.clear();
       deadlineField.clear();
    }

}