package org.ubb.email_notification_service.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.ubb.email_notification_service.dto.AdoptionContent;
import org.ubb.email_notification_service.dto.AdoptionSubscriptionTaskInfo;
import org.ubb.email_notification_service.dto.ContainerResponse;
import org.ubb.email_notification_service.dto.ObjectMetadataResponse;
import org.ubb.email_notification_service.exception.DataProcessingException;
import org.ubb.email_notification_service.utils.Converter;

@Service
public class TaskProcessorService
{
    private final CloudStorageClient cloudStorageClient;
    private final EmailSenderService emailSenderService;

    public TaskProcessorService(CloudStorageClient cloudStorageClient, EmailSenderService emailSenderService)
    {
        this.cloudStorageClient = cloudStorageClient;
        this.emailSenderService = emailSenderService;
    }

    public void processTask(AdoptionSubscriptionTaskInfo taskInfo)
    {
        // Get the content for the adoption post
        ContainerResponse adoptionContainer = cloudStorageClient.getContainer(taskInfo.userName(), taskInfo.adoptionId());
        ObjectMetadataResponse adoptionInfoMetadata = adoptionContainer.content().stream()
                .filter(objectMetadata -> objectMetadata.objectName().contains("adoption_info"))
                .findFirst()
                .orElseThrow(() -> new DataProcessingException("Could not find adoption information"));
        Resource adoptionInfoJson = cloudStorageClient.getObjectContent(adoptionInfoMetadata.userName(), adoptionInfoMetadata.objectId());
        AdoptionContent adoptionContent = Converter.toAdoptionContent(adoptionInfoJson);

        // Send the emails to the sender and subscriber
        emailSenderService.sendEmail(taskInfo.posterEmail(), "New subscriber to your adoption post", createPosterEmail(adoptionContent, taskInfo));
        emailSenderService.sendEmail(taskInfo.subscriberEmail(), "New info about the adoption post", createSubscriberEmail(adoptionContent, taskInfo));
    }

    private static String createPosterEmail(AdoptionContent adoptionContent, AdoptionSubscriptionTaskInfo taskInfo)
    {
        return "Hello! The user " +
                taskInfo.subscriberEmail() +
                " has just subscribed to receive more details about your post regarding " +
                adoptionContent.petName() +
                ", the " +
                adoptionContent.petType() + ".";
    }

    private static String createSubscriberEmail(AdoptionContent adoptionContent, AdoptionSubscriptionTaskInfo adoptionSubscriptionTaskInfo)
    {
        return "Hello! We are glad that you wanted to learn more about " + adoptionContent.petName() + ", the " +
                adoptionContent.petType() + ".\nSo here are the extra details related to it:\n" +
                adoptionContent.detailedInformation() +
                "\nYou can also contact: " + adoptionSubscriptionTaskInfo.posterEmail() + " for more details.";
    }
}
