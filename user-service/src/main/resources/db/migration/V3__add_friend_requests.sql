CREATE TABLE friend_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT,
    receiver_id BIGINT,
    status VARCHAR(20),
    FOREIGN KEY (sender_id) REFERENCES user_profiles(id),
    FOREIGN KEY (receiver_id) REFERENCES user_profiles(id)
);
