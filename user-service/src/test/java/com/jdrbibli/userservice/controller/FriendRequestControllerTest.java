package com.jdrbibli.userservice.controller;

import com.jdrbibli.userservice.dto.FriendDTO;
import com.jdrbibli.userservice.dto.FriendRequestDTO;
import com.jdrbibli.userservice.dto.OuvrageDTO;
import com.jdrbibli.userservice.entity.FriendRequest;
import com.jdrbibli.userservice.entity.UserProfile;
import com.jdrbibli.userservice.service.FriendRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FriendRequestControllerTest {

    @Mock
    private FriendRequestService friendRequestService;

    @InjectMocks
    private FriendRequestController friendRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendRequest_shouldReturnFriendRequestDTO() {
        FriendRequest request = new FriendRequest();
        request.setId(1L);

        when(friendRequestService.sendFriendRequest(1L, 2L)).thenReturn(request);

        FriendRequestDTO dto = friendRequestController.sendRequest(1L, 2L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        verify(friendRequestService).sendFriendRequest(1L, 2L);
    }

    @Test
    void acceptRequest_shouldReturnFriendRequestDTO() {
        FriendRequest request = new FriendRequest();
        request.setId(10L);

        when(friendRequestService.acceptFriendRequest(10L)).thenReturn(request);

        FriendRequestDTO dto = friendRequestController.acceptRequest(10L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        verify(friendRequestService).acceptFriendRequest(10L);
    }

    @Test
    void rejectRequest_shouldReturnFriendRequestDTO() {
        FriendRequest request = new FriendRequest();
        request.setId(20L);

        when(friendRequestService.rejectFriendRequest(20L)).thenReturn(request);

        FriendRequestDTO dto = friendRequestController.rejectRequest(20L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(20L);
        verify(friendRequestService).rejectFriendRequest(20L);
    }

    @Test
    void removeFriend_shouldCallService() {
        friendRequestController.removeFriend(1L, 2L);
        verify(friendRequestService).removeFriend(1L, 2L);
    }

    @Test
    void listFriends_shouldReturnFriendDTOList() {
        UserProfile friend1 = new UserProfile();
        friend1.setId(1L);
        UserProfile friend2 = new UserProfile();
        friend2.setId(2L);

        when(friendRequestService.listFriends(100L)).thenReturn(Arrays.asList(friend1, friend2));

        List<FriendDTO> result = friendRequestController.listFriends(100L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(friendRequestService).listFriends(100L);
    }

    @Test
    void listReceivedRequests_shouldReturnFriendRequestDTOList() {
        FriendRequest req1 = new FriendRequest();
        req1.setId(1L);
        FriendRequest req2 = new FriendRequest();
        req2.setId(2L);

        when(friendRequestService.listReceivedRequests(100L)).thenReturn(Arrays.asList(req1, req2));

        List<FriendRequestDTO> result = friendRequestController.listReceivedRequests(100L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(friendRequestService).listReceivedRequests(100L);
    }

    @Test
    void getFriendOuvrages_shouldReturnOuvrageDTOList_whenFriends() {
        OuvrageDTO ouvrage = new OuvrageDTO();
        ouvrage.setId(5L);

        when(friendRequestService.areFriends(1L, 2L)).thenReturn(true);
        when(friendRequestService.listFriendOuvrages(2L)).thenReturn(List.of(ouvrage));

        List<OuvrageDTO> result = friendRequestController.getFriendOuvrages(2L, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(5L);
        verify(friendRequestService).areFriends(1L, 2L);
        verify(friendRequestService).listFriendOuvrages(2L);
    }

    @Test
    void getFriendOuvrages_shouldThrowException_whenNotFriends() {
        when(friendRequestService.areFriends(1L, 2L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> friendRequestController.getFriendOuvrages(2L, 1L));

        verify(friendRequestService).areFriends(1L, 2L);
        verify(friendRequestService, never()).listFriendOuvrages(anyLong());
    }
}
