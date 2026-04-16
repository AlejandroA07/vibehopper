package com.vibehopper.infrastructure.config;

import com.vibehopper.infrastructure.persistence.event.EventDocument;
import com.vibehopper.infrastructure.persistence.user.AppUserDocument;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MongoIndexConfigurationTest {
    @Test
    void createsExplicitApplicationIndexesWhenEnabled() throws Exception {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        IndexOperations eventIndexes = mock(IndexOperations.class);
        IndexOperations userIndexes = mock(IndexOperations.class);
        when(mongoTemplate.indexOps(EventDocument.class)).thenReturn(eventIndexes);
        when(mongoTemplate.indexOps(AppUserDocument.class)).thenReturn(userIndexes);

        ApplicationRunner runner = new MongoIndexConfiguration()
                .mongoIndexInitializer(mongoTemplate, new MongoIndexProperties(true));

        runner.run(mock(ApplicationArguments.class));

        verify(eventIndexes, times(3)).ensureIndex(any(IndexDefinition.class));
        verify(userIndexes).ensureIndex(any(IndexDefinition.class));
    }

    @Test
    void skipsIndexCreationWhenDisabled() throws Exception {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);

        ApplicationRunner runner = new MongoIndexConfiguration()
                .mongoIndexInitializer(mongoTemplate, new MongoIndexProperties(false));

        runner.run(mock(ApplicationArguments.class));

        verify(mongoTemplate, never()).indexOps(any(Class.class));
    }
}
