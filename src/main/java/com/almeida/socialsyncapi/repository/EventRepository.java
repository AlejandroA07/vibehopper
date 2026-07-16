package com.almeida.socialsyncapi.repository;

import com.almeida.socialsyncapi.domain.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {}
