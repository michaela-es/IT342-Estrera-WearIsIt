package edu.cit.estrera.wearisit.features.tag_management;

import edu.cit.estrera.wearisit.features.user_management.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
            @Valid @RequestBody CreateTagRequest request,
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        Tag tag = tagService.createTag(request, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TagResponse.from(tag));
    }
}