package pl.karpik.todoapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private boolean completed;
    private LocalDate deadline;

   public Todo()
   {
   }

   public Todo (String title)
   {
       this.title = title;
       this.completed = false;
   }
        public Long getId ()
        {
            return id;
        }
        public String getTitle ()
        {
            return title;
        }
        public void setTitle(String title)
        {
            this.title = title;
        }

        public boolean isCompleted ()
        {
            return completed;
        }

        public void setCompleted ( boolean completed)
        {
            this.completed = completed;
        }
        public LocalDate getDeadline()
        {
            return deadline;
        }
        public void setDeadline(LocalDate deadline)
        {
            this.deadline = deadline;
        }


    }
