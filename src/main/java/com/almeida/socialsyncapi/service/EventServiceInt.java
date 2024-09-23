package com.almeida.socialsyncapi.service;


import com.almeida.socialsyncapi.dto.ReadEventDTO;

import java.util.Optional;

public interface EventServiceInt {
    Optional<ReadEventDTO> getEventById(String id);
}
