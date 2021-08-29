package ru.org;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final Job migrateAuthorsAndGenres;
    private final Job migrateBooks;
    private final Job migrateComments;

    private final JobLauncher jobLauncher;


    @ShellMethod(value = "startMigrationJobWithJobLauncher", key = "start")
    public void startMigrationJobWithJobLauncher() throws Exception {
        JobExecution authorsAndGenresExecution = jobLauncher.run(migrateAuthorsAndGenres, new JobParametersBuilder()
                .toJobParameters());
        JobExecution booksExecution = jobLauncher.run(migrateBooks, new JobParametersBuilder()
                .toJobParameters());
        JobExecution commentsExecution = jobLauncher.run(migrateComments, new JobParametersBuilder()
                .toJobParameters());
        System.out.println(authorsAndGenresExecution);
        System.out.println(booksExecution);
        System.out.println(commentsExecution);
    }
}
