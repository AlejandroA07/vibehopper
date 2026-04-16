package com.vibehopper.infrastructure.config;

import com.vibehopper.infrastructure.persistence.event.EventDocument;
import com.vibehopper.infrastructure.persistence.user.AppUserDocument;
import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoIndexConfiguration {
    @Bean
    ApplicationRunner mongoIndexInitializer(MongoTemplate mongoTemplate, MongoIndexProperties properties) {
        return new MongoIndexInitializer(mongoTemplate, properties);
    }

    private record MongoIndexInitializer(MongoTemplate mongoTemplate, MongoIndexProperties properties)
            implements ApplicationRunner {
        @Override
        public void run(ApplicationArguments args) {
            if (!properties.ensureIndexes()) {
                return;
            }

            mongoTemplate.indexOps(EventDocument.class)
                    .ensureIndex(new GeospatialIndex("coordinates").typed(GeoSpatialIndexType.GEO_2DSPHERE));
            mongoTemplate.indexOps(EventDocument.class)
                    .ensureIndex(new CompoundIndexDefinition(new Document()
                            .append("source.sourceName", 1)
                            .append("source.externalId", 1))
                            .named("source_identity_idx"));
            mongoTemplate.indexOps(EventDocument.class)
                    .ensureIndex(new CompoundIndexDefinition(new Document()
                            .append("normalizedName", 1)
                            .append("date", 1)
                            .append("normalizedCity", 1))
                            .named("normalized_identity_idx"));
            mongoTemplate.indexOps(AppUserDocument.class)
                    .ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
        }
    }
}
