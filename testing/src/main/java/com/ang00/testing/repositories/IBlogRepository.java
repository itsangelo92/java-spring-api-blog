package com.ang00.testing.repositories;

import com.ang00.testing.models.BlogModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBlogRepository extends JpaRepository<BlogModel, Long> {
}
