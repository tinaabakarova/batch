package ru.org;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.org.jpa.repository.BooksDaoJpa;
import ru.org.mongo.model.Author;
import ru.org.mongo.model.Book;
import ru.org.mongo.model.Genre;
import ru.org.mongo.repository.AuthorDaoMongo;
import ru.org.mongo.repository.GenreDaoMongo;

import java.util.HashMap;
import java.util.Map;

import static ru.org.Constants.CHUNK_SIZE;
import static ru.org.Constants.PAGE_SIZE;

@Configuration
@EnableBatchProcessing
public class BatchMigrateBooksConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    private final BooksDaoJpa booksDaoJpa;

    private final AuthorDaoMongo authorDaoMongo;
    private final GenreDaoMongo genreDaoMongo;

    private final MongoTemplate mongoTemplate;
    private final Map<String, Sort.Direction> sort;

    @Autowired
    public BatchMigrateBooksConfig(JobBuilderFactory jobBuilderFactory,
                                   StepBuilderFactory stepBuilderFactory,
                                   BooksDaoJpa booksDaoJpa,
                                   AuthorDaoMongo authorDaoMongo,
                                   GenreDaoMongo genreDaoMongo,
                                   MongoTemplate mongoTemplate) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.booksDaoJpa = booksDaoJpa;
        this.authorDaoMongo = authorDaoMongo;
        this.genreDaoMongo = genreDaoMongo;
        this.mongoTemplate = mongoTemplate;
        this.sort = new HashMap<String, Sort.Direction>();
    }

    @Bean
    public RepositoryItemReader<ru.org.jpa.model.Book> repositoryBookItemReader() {
        return new RepositoryItemReaderBuilder<ru.org.jpa.model.Book>()
                .repository(booksDaoJpa)
                .methodName("findAll")
                .saveState(true)
                .pageSize(PAGE_SIZE)
                .sorts(sort)
                .name("booksReader")
                .build();
    }

    @Bean
    public ItemProcessor<ru.org.jpa.model.Book, Book> bookProcessor() {
        return book -> {
            System.out.println("processing.... " + book.getName());
            Author author = authorDaoMongo.findByName(book.getAuthor().getName());
            Genre genre = genreDaoMongo.findByName(book.getGenre().getName());
            return new Book(book.getName(), author, genre);
        };
    }

    @Bean
    public MongoItemWriter<Book> bookWriter() {
        return new MongoItemWriterBuilder<Book>().template(mongoTemplate).collection("books")
                .build();
    }

    @Bean
    public Step books() {
        return stepBuilderFactory.get("books")
                .<ru.org.jpa.model.Book, Book>chunk(CHUNK_SIZE)
                .reader(repositoryBookItemReader())
                .processor(bookProcessor())
                .writer(bookWriter())
                .build();
    }

    @Bean
    public Job migrateBooks(Step books) {
        return jobBuilderFactory.get("migrateBooks")
                .start(books)
                .build();
    }

}
