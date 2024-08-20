package com.project.shelf.mapper;

public interface MyDocMapper {
    void incrementViewCount(Long id);

    void incrementVoteCount(Long id);
}
