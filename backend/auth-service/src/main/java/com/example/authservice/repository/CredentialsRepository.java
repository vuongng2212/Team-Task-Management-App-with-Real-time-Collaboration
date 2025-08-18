package com.example.authservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.authservice.entity.Credentials;
import com.example.authservice.enums.AuthProvider;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
   Optional<Credentials> findByEmail(String email);
   Optional<Credentials> findByEmailAndProvider(String email, AuthProvider provider);
   Optional<Credentials> findByUserId(UUID userId);
   Optional<Credentials> findByExternalIdAndProvider(String externalId, AuthProvider provider);
   boolean existsByEmail(String email);
   boolean existsByEmailAndProvider(String email, AuthProvider provider);
   List<Credentials> findByUserIdAndIsActiveTrue(UUID userId);
   Optional<Credentials> findByEmailAndIsActiveTrue(String email);
   @Modifying
   @Query("UPDATE Credentials c SET c.lastLogin = CURRENT_TIMESTAMP WHERE c.id = :id")
   void updateLastLogin(@Param("id") UUID id);
   @Modifying
   @Query("UPDATE Credentials c SET c.isActive = false WHERE c.id = :id")
   void deactivateCredentials(@Param("id") UUID id);
   Optional<Credentials> findByRefreshTokenAndIsActiveTrue(String refreshToken);

}
