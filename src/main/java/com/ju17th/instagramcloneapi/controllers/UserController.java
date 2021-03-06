package com.ju17th.instagramcloneapi.controllers;

import com.ju17th.instagramcloneapi.payload.*;
import com.ju17th.instagramcloneapi.payload.post.response.*;
import com.ju17th.instagramcloneapi.payload.post.response.notification.NotificationsPayload;
import com.ju17th.instagramcloneapi.payload.post.response.profile.EditProfileResponse;
import com.ju17th.instagramcloneapi.payload.post.response.profile.PrivateResponse;
import com.ju17th.instagramcloneapi.payload.post.response.profile.UserProfileResponse;
import com.ju17th.instagramcloneapi.payload.user.UpdateUserRequest;
import com.ju17th.instagramcloneapi.payload.user.UserIdentityAvailability;
import com.ju17th.instagramcloneapi.payload.user.UserProfile;
import com.ju17th.instagramcloneapi.payload.user.UserSummary;
import com.ju17th.instagramcloneapi.security.CurrentUser;
import com.ju17th.instagramcloneapi.security.UserPrincipal;
import com.ju17th.instagramcloneapi.service.UserService;
import com.ju17th.instagramcloneapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(), currentUser.getImagePath());
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        return userService.checkUsernameAvailability(username);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        return userService.checkEmailAvailability(email);
    }

    @GetMapping("{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
       return userService.getUserProfile(username);
    }

    @GetMapping("/search/{usernameOrName}")
    public PagedResponse<UserSummary> getUsersByUsernameOrName(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                               @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                               @PathVariable(value = "usernameOrName") String usernameOrName) {
        return userService.getUsersByUsernameOrName(page, size, usernameOrName);
    }

    @GetMapping("/profile/{username}")
    public UserProfileResponse getUserProfileByUsername(@PathVariable(value = "username") String username,
                                                        @CurrentUser UserPrincipal currentUser) {
        return userService.getUserProfileByUsername(username, currentUser);
    }

    @GetMapping("/profile/edit")
    public EditProfileResponse getProfileDetailsToEdit(@CurrentUser UserPrincipal currentUser) {
        return userService.getProfileDetailsToEdit(currentUser);
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<?> updateUser(@CurrentUser UserPrincipal currentUser,
                                        @Valid @RequestBody UpdateUserRequest updateUserRequest) {

        userService.updateUser(currentUser, updateUserRequest);

        return ResponseEntity.ok().body(new ApiResponse(true, "User updated successfully."));
    }

    @PostMapping("/profile/photo")
    public ResponseEntity<?> updateUserPhoto(@CurrentUser UserPrincipal currentUser,
                                             @RequestParam("image") MultipartFile image) {
        return userService.updateUserPicture(currentUser, image);
    }

    @DeleteMapping("/profile/photo")
    public ResponseEntity<?> deleteUserPhoto(@CurrentUser UserPrincipal currentUser) {
        userService.updateUserPicture(currentUser, null);
        return ResponseEntity.ok().body(new ApiResponse(true, "User profile picture deleted successfully."));
    }

    @PutMapping("/me/private")
    public PrivateResponse setIsPrivate(@CurrentUser UserPrincipal currentUser) {
        return userService.setIsPrivate(currentUser);
    }

    @GetMapping("/notifications")
    public NotificationsPayload getUserNotifications(@CurrentUser UserPrincipal currentUser) {
        return userService.getUserNotifications(currentUser);
    }
}
