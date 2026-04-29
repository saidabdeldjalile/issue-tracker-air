//package com.suryakn.IssueTracker;
//
//import com.suryakn.IssueTracker.entity.Comment;
//import com.suryakn.IssueTracker.entity.Ticket;
//import com.suryakn.IssueTracker.repository.CommentRepository;
//import com.suryakn.IssueTracker.repository.TicketRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Arrays;
//
//@Configuration
//public class LoadDatabase {
//    private static final Logger log = LoggerFactory.getLogger(IssueTrackerApplication.class);
//
//    @Bean
//    public CommandLineRunner demo(TicketRepository ticketRepository, CommentRepository commentRepository) {
//        return (args) -> {
//            Ticket ticket = ticketRepository.save(new Ticket("Database Connectivity", "Application not connecting to the database", "Open", "High"));
//            commentRepository.saveAll(Arrays.asList(new Comment("This is the first comment.", ticket), new Comment("This is the second comment.", ticket)));
//            Ticket ticket1 = ticketRepository.save(new Ticket("Login Issue", "Unable to login to the application", "Open", "High"));
//            commentRepository.saveAll(Arrays.asList(new Comment("Unable to authenticate.", ticket1), new Comment("Credentials not working.", ticket1)));
//
//            Ticket ticket2 = ticketRepository.save(new Ticket("UI Bug", "Buttons not aligned properly", "Open", "Medium"));
//            commentRepository.saveAll(Arrays.asList(new Comment("UI needs improvement.", ticket2), new Comment("Alignment issue found.", ticket2), new Comment("Working on the fix.", ticket2)));
//
//            Ticket ticket3 = ticketRepository.save(new Ticket("Performance Issue", "Application is slow", "Open", "High"));
//            commentRepository.saveAll(Arrays.asList(new Comment("Noticed lag in the application.", ticket3), new Comment("Performance needs to be optimized.", ticket3)));
//
//            Ticket ticket4 = ticketRepository.save(new Ticket("Server Down", "Server not responding", "Open", "Critical"));
//            commentRepository.saveAll(Arrays.asList(new Comment("Server is down.", ticket4), new Comment("Urgent attention needed.", ticket4)));
//
//            Ticket ticket5 = ticketRepository.save(new Ticket("Data Loss", "Data not found in the database", "Open", "High"));
//            commentRepository.saveAll(Arrays.asList(new Comment("Data retrieval issue.", ticket5), new Comment("Data seems to be lost.", ticket5), new Comment("Investigating the issue.", ticket5)));
//
//            Ticket ticket6 = ticketRepository.save(new Ticket("Security Breach", "Unauthorized access detected", "Open", "Critical"));
//            commentRepository.saveAll(Arrays.asList(new Comment("Potential security breach.", ticket6), new Comment("Immediate action required.", ticket6)));
//
//            Ticket ticket7 = ticketRepository.save(new Ticket("API Failure", "API not returning expected results", "Open", "High"));
//            commentRepository.saveAll(Arrays.asList(new Comment("API seems to be broken.", ticket7), new Comment("Unexpected results returned.", ticket7), new Comment("Looking into the issue.", ticket7)));
//
//            Ticket ticket8 = ticketRepository.save(new Ticket("Memory Leak", "Application consuming high memory", "Open", "Medium"));
//            commentRepository.saveAll(Arrays.asList(new Comment("High memory usage detected.", ticket8), new Comment("Possible memory leak.", ticket8)));
//
//            Ticket ticket9 = ticketRepository.save(new Ticket("Feature Request", "Request to add new feature", "Open", "Low"));
//            commentRepository.saveAll(Arrays.asList(new Comment("New feature requested.", ticket9), new Comment("Evaluating the feasibility.", ticket9)));
//
//            Ticket ticket10 = ticketRepository.save(new Ticket("Documentation Error", "Mistake found in the documentation", "Open", "Low"));
//            commentRepository.saveAll(Arrays.asList(new Comment("Error in documentation.", ticket10), new Comment("Need to update the docs.", ticket10)));
//
//            // repository.save(new Ticket("Database Connectivity", "Application not connecting to the database", "Open", "priority"));
////            repository.save(new Ticket("Page Load Error", "Home page is not loading", "Open", "priority"));
////            repository.save(new Ticket("Broken Link", "Link to the user guide is broken", "Open", "priority"));
////            repository.save(new Ticket("Performance Issue", "Application is running slow", "Open", "priority"));
////            repository.save(new Ticket("UI Misalignment", "Buttons on the form are misaligned", "Open", "priority"));
////            repository.save(new Ticket("Missing Feature", "Export to PDF feature is missing", "Open", "priority"));
////            repository.save(new Ticket("Security Bug", "Password field is not encrypted", "Open", "priority"));
////            repository.save(new Ticket("Data Loss", "Data entered in the form is not getting saved", "Open", "priority"));
////            repository.save(new Ticket("Crash", "Application crashes on clicking 'Submit'", "Open", "priority"));
//
////            log.info("Tickets found with findAll():");
////            log.info("-------------------------------");
////            repository.findAll().forEach(ticket -> log.info(ticket.toString()));
////            log.info("");
////
////            Ticket ticket = repository.findById(1L).orElseThrow();
////            log.info("Ticket found with findById(1):");
////            log.info("--------------------------------");
////            log.info(ticket.toString());
////            log.info("");
//        };
//    }
//}
