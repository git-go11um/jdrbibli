package com.jdrbibli.userservice.repository;

import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.FriendRequest.Status;
import com.jdrbibli.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour gérer les opérations CRUD et les requêtes spécifiques
 * liées aux entités {@link FriendRequest}.
 */
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    /**
     * Retourne toutes les demandes d'ami envoyées par un utilisateur.
     *
     * @param sender l'utilisateur expéditeur
     * @return liste des demandes d'ami envoyées
     */
    List<FriendRequest> findBySender(UserProfile sender);

    /**
     * Retourne toutes les demandes d'ami reçues par un utilisateur.
     *
     * @param receiver l'utilisateur destinataire
     * @return liste des demandes d'ami reçues
     */
    List<FriendRequest> findByReceiver(UserProfile receiver);

    /**
     * Retourne toutes les demandes où l'utilisateur est soit expéditeur, soit destinataire.
     *
     * @param sender   l'utilisateur expéditeur
     * @param receiver l'utilisateur destinataire
     * @return liste des demandes d'ami
     */
    List<FriendRequest> findBySenderOrReceiver(UserProfile sender, UserProfile receiver);

    /**
     * Trouve une demande spécifique entre deux utilisateurs.
     *
     * @param sender   l'utilisateur expéditeur
     * @param receiver l'utilisateur destinataire
     * @return la demande si elle existe
     */
    Optional<FriendRequest> findBySenderAndReceiver(UserProfile sender, UserProfile receiver);

    /**
     * Retourne toutes les demandes reçues par un utilisateur, identifiées par ID.
     *
     * @param receiverId l'ID de l'utilisateur destinataire
     * @return liste des demandes reçues
     */
    List<FriendRequest> findByReceiverId(Long receiverId);

    /**
     * Retourne toutes les demandes reçues par un utilisateur avec un statut donné.
     *
     * @param receiverId l'ID de l'utilisateur destinataire
     * @param status     le statut de la demande
     * @return liste des demandes filtrées par statut
     */
    List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, FriendRequest.Status status);

    /**
     * Cherche une demande existante entre deux utilisateurs, quel que soit l'expéditeur.
     *
     * @param user1 le premier utilisateur
     * @param user2 le second utilisateur
     * @return la demande existante si elle existe
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
            "(fr.sender = :user1 AND fr.receiver = :user2) OR " +
            "(fr.sender = :user2 AND fr.receiver = :user1)")
    Optional<FriendRequest> findExistingRequestBetweenUsers(@Param("user1") UserProfile user1,
                                                            @Param("user2") UserProfile user2);

    /**
     * Cherche une amitié acceptée entre deux utilisateurs.
     *
     * @param user1 le premier utilisateur
     * @param user2 le second utilisateur
     * @return la relation d'amitié si elle existe et est acceptée
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
            "((fr.sender = :user1 AND fr.receiver = :user2) OR " +
            "(fr.sender = :user2 AND fr.receiver = :user1)) AND fr.status = 'ACCEPTED'")
    Optional<FriendRequest> findAcceptedFriendshipBetweenUsers(@Param("user1") UserProfile user1,
                                                               @Param("user2") UserProfile user2);

    /**
     * Retourne toutes les relations d'amitié acceptées pour un utilisateur.
     *
     * @param user l'utilisateur concerné
     * @return liste des amitiés acceptées
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
            "(fr.sender = :user OR fr.receiver = :user) AND fr.status = 'ACCEPTED'")
    List<FriendRequest> findAcceptedFriendshipsOfUser(@Param("user") UserProfile user);

    /**
     * Retourne toutes les demandes reçues par un utilisateur avec un statut donné.
     *
     * @param receiver l'utilisateur destinataire
     * @param status   le statut de la demande
     * @return liste des demandes filtrées
     */
    List<FriendRequest> findByReceiverAndStatus(UserProfile receiver, Status status);
}
