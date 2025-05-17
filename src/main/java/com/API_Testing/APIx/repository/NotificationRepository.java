package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.websocket.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByMacAndReceiver(String mac, String receiver);
}
