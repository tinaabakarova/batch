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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.org.jpa.repository.AuthorsDaoJpa;
import ru.org.jpa.repository.GenresDaoJpa;

import java.util.HashMap;
import java.util.Map;

import static ru.org.Constants.CHUNK_SIZE;
import static ru.org.Constants.PAGE_SIZE;

@Configuration
@EnableBatchProcessing
public class BatchMigrateAuthorsAndGenresConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    private final AuthorsDaoJpa authorsDaoJpa;
    private final GenresDaoJpa genresDaoJpa;

    private final MongoTemplate mongoTemplate;
    private final Map<String, Sort.Direction> sort;

    @Autowired
    public BatchMigrateAuthorsAndGenresConfig(JobBuilderFactory jobBuilderFactory,
                                              StepBuilderFactory stepBuilderFactory,
                                              AuthorsDaoJpa authorsDaoJpa,
                                              GenresDaoJpa genresDaoJpa,
                                              MongoTemplate mongoTemplate) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.authorsDaoJpa = authorsDaoJpa;
        this.genresDaoJpa = genresDaoJpa;
        this.mongoTemplate = mongoTemplate;
        this.sort = new HashMap<String, Sort.Direction>();
    }

    @Bean
    public RepositoryItemReader<ru.org.jpa.model.Author> repositoryAuthorItemReader() {
        return new RepositoryItemReaderBuilder<ru.org.jpa.model.Author>()
                .repository(authorsDaoJpa)
                .methodName("findAll")
                .saveState(true)
                .pageSize(PAGE_SIZE)
                .sorts(sort)
                .name("authorsReader")
                .build();
    }

    @Bean
    public RepositoryItemReader<ru.org.jpa.model.Genre> repositoryGenreItemReader() {
        return new RepositoryItemReaderBuilder<ru.org.jpa.model.Genre>()
                .repository(genresDaoJpa)
                .methodName("findAll")
                .saveState(true)
                .pageSize(PAGE_SIZE)
                .sorts(sort)
                .name("genresReader")
                .build();
    }

    @Bean
    public ItemProcessor<ru.org.jpa.model.Author, ru.org.mongo.model.Author> authorProcessor() {
        return author -> {
            System.out.println("processing.... " + author.getName());
            return new ru.org.mongo.model.Author(author.getName());
        };
    }

    @Bean
    public ItemProcessor<ru.org.jpa.model.Genre, ru.org.mongo.model.Genre> genreProcessor() {
        return genre -> {
            System.out.println("processing.... " + genre.getName());
            return new ru.org.mongo.model.Genre(genre.getName());
        };
    }

    @Bean
    public MongoItemWriter<ru.org.mongo.model.Author> authorWriter() {
        return new MongoItemWriterBuilder<ru.org.mongo.model.Author>().template(mongoTemplate).collection("author")
                .build();
    }

    @Bean
    public MongoItemWriter<ru.org.mongo.model.Genre> genreWriter() {
        return new MongoItemWriterBuilder<ru.org.mongo.model.Genre>().template(mongoTemplate).collection("genre")
                .build();
    }

    @Bean
    public Step authors() {
        return stepBuilderFactory.get("authors")
                .<ru.org.jpa.model.Author, ru.org.mongo.model.Author>chunk(CHUNK_SIZE)
                .reader(repositoryAuthorItemReader())
                .processor(authorProcessor())
                .writer(authorWriter())
                .build();
    }

    @Bean
    public Step genres() {
        return stepBuilderFactory.get("genres")
                .<ru.org.jpa.model.Genre, ru.org.mongo.model.Genre>chunk(CHUNK_SIZE)
                .reader(repositoryGenreItemReader())
                .processor(genreProcessor())
                .writer(genreWriter())
                .build();
    }


    @Bean
    @Qualifier("migrateAuthorsAndGenres")
    public Job migrateAuthorsAndGenres() {
        return jobBuilderFactory.get("migrateAuthorsAndGenres")
                .start(authors())
                .next(genres())
                .build();
    }

}
