package org.ubb.adoption_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ubb.adoption_service.api.AdoptionInfoResponse;
import org.ubb.adoption_service.api.AdoptionPreviewInfo;
import org.ubb.adoption_service.api.AdoptionSubscriptionRequest;
import org.ubb.adoption_service.api.AdoptionSubscriptionResponse;
import org.ubb.adoption_service.exception.UnauthorizedException;
import org.ubb.adoption_service.service.AdoptionService;
import org.ubb.adoption_service.service.security.AuthenticationVerifier;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/adoptions")
public class AdoptionController
{
    private final AuthenticationVerifier authenticationVerifier;
    private final AdoptionService adoptionService;

    public AdoptionController(AuthenticationVerifier authenticationVerifier, AdoptionService adoptionService)
    {
        this.authenticationVerifier = authenticationVerifier;
        this.adoptionService = adoptionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdoptionInfoResponse> createAdoption(@RequestParam("userName") String userName,
                                                               @RequestParam("petName") String petName,
                                                               @RequestParam("petAge") String petAge,
                                                               @RequestParam("petType") String petType,
                                                               @RequestParam("detailedInformation") String detailedInformation,
                                                               @RequestParam("image") MultipartFile image)
    {
        authCheck(userName);
        var response = adoptionService.createAdoptionPost(userName, petName, petAge, petType, detailedInformation, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<AdoptionPreviewInfo>> getAdoptionsPreviews(@PageableDefault(size = 5, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable)
    {
        var response = adoptionService.getAdoptionsPreviews(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{adoptionId}/subscription")
    public ResponseEntity<AdoptionSubscriptionResponse> subscribeToAdoption(@PathVariable("adoptionId") UUID adoptionId,
                                                                            @RequestParam("posterUserName") String posterUserName,
                                                                            @RequestParam("subscriberUserName") String subscriberUserName)
    {
        authCheck(subscriberUserName);
        var requestData = new AdoptionSubscriptionRequest(adoptionId, posterUserName, subscriberUserName);
        var response = adoptionService.subscribeToAdoption(requestData);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{adoptionId}")
    public ResponseEntity<Void> deleteAdoptionPost(@PathVariable("adoptionId") UUID adoptionId, @RequestParam("userName") String userName)
    {
        authCheck(userName);
        adoptionService.deleteAdoptionPost(userName, adoptionId);
        return ResponseEntity.noContent().build();
    }

    private void authCheck(String userName)
    {
        String loggedInUser = authenticationVerifier.getAuthenticatedUser();

        if (!loggedInUser.equals(userName))
        {
            throw new UnauthorizedException("Unauthorized to perform this operation on this user");
        }
    }
}

