package com.bryja.truckapp.controllers;

import com.bryja.truckapp.classes.Message;
import com.bryja.truckapp.classes.User;
import com.bryja.truckapp.repository.MessageRepository;
import com.bryja.truckapp.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
public class WebSocketController {

    private SimpMessagingTemplate messagingTemplate;

    private MessageRepository messageRepository;

    private final UserRepository userRepository;

    public WebSocketController(MessageRepository messageRepository, UserRepository repository) {
        this.messageRepository = messageRepository;
        this.userRepository = repository;
    }

    @MessageMapping({"/chat/general"})
    @SendTo("/topic/messages/general")
    public Message handleEnglishMessage(Message message, Authentication authentication) throws Exception {
        if(authentication==null){
            return null;
        }
        String username = checkmail(authentication);
        User user = userRepository.findByEmail(username);
        Message chatMessage = new Message();
        chatMessage.setContent(message.getContent());
        chatMessage.setUser(user);
        chatMessage.setChatroom("general"); // set the language of the message
        chatMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(chatMessage);;
        chatMessage.setAuthor_name(user.name);
        chatMessage.setAvatar(user.getAvatar());
        return chatMessage;
    }


    public String checkmail(Object authentication){
        if (authentication instanceof DefaultOidcUser ) {       //klasa która powstaje przy social loginie
            DefaultOidcUser oauth2User = (DefaultOidcUser) authentication;
            return oauth2User.getAttribute("email");
        } else if (authentication instanceof UserDetails) {    //zwykla klasa posiadająca dane z bazy
            UserDetails userDetails = (UserDetails) authentication;
            return userDetails.getUsername();
        }
        else if (authentication instanceof OAuth2AuthenticationToken) {    //zwykla klasa posiadająca dane z bazy
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String email = oauthToken.getPrincipal().getAttribute("email");
            return email;
        }
        else if (authentication instanceof UsernamePasswordAuthenticationToken) {    //zwykla klasa posiadająca dane z bazy
            UsernamePasswordAuthenticationToken oauthToken = (UsernamePasswordAuthenticationToken) authentication;
            String email = oauthToken.getName();
            return email;
        }
        else {
            return "notfound";
        }
    }

}
