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
import ru.org.jpa.repository.CommentsDaoJpa;
import ru.org.mongo.model.Book;
import ru.org.mongo.repository.BookDaoMongo;

import java.util.HashMap;
import java.util.Map;

import static ru.org.Constants.CHUNK_SIZE;
import static ru.org.Constants.PAGE_SIZE;

@Configuration
@EnableBatchProcessing
public class BatchMigrateCommentsConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    private final CommentsDaoJpa commentsDaoJpa;

    private final BookDaoMongo bookDaoMongo;

    private final MongoTemplate mongoTemplate;
    private final Map<String, Sort.Direction> sort;

    @Autowired
    public BatchMigrateCommentsConfig(JobBuilderFactory jobBuilderFactory,
                                      StepBuilderFactory stepBuilderFactory,
                                      CommentsDaoJpa commentsDaoJpa,
                                      BookDaoMongo bookDaoMongo,
                                      MongoTemplate mongoTemplate) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.commentsDaoJpa = commentsDaoJpa;
        this.bookDaoMongo = bookDaoMongo;
        this.mongoTemplate = mongoTemplate;
        this.sort = new HashMap<String, Sort.Direction>();
    }

    @Bean
    public RepositoryItemReader<ru.org.jpa.model.Comment> repositoryCommentItemReader() {
        return new RepositoryItemReaderBuilder<ru.org.jpa.model.Comment>()
                .repository(commentsDaoJpa)
                .methodName("findAll")
                .saveState(true)
                .pageSize(PAGE_SIZE)
                .sorts(sort)
                .name("commentsReader")
                .build();
    }

    @Bean
    public ItemProcessor<ru.org.jpa.model.Comment, ru.org.mongo.model.Comment> commentProcessor() {
        return comment -> {
            System.out.println("processing.... " + comment.getComment());
            Book book = bookDaoMongo.findByName(comment.getBook().getName());
            return new ru.org.mongo.model.Comment(comment.getComment(), book, comment.getUserName());
        };
    }

    @Bean
    public MongoItemWriter<ru.org.mongo.model.Comment> commentWriter() {
        return new MongoItemWriterBuilder<ru.org.mongo.model.Comment>().template(mongoTemplate).collection("comments")
                .build();
    }

    @Bean
    public Step comments() {
        return stepBuilderFactory.get("comments")
                .<ru.org.jpa.model.Comment, ru.org.mongo.model.Comment>chunk(CHUNK_SIZE)
                .reader(repositoryCommentItemReader())
                .processor(commentProcessor())
                .writer(commentWriter())
                .build();
    }

    @Bean
    public Job migrateComments(Step comments) {
        return jobBuilderFactory.get("migrateComments")
                .start(comments)
                .build();
    }

}
