package edu.cit.estrera.wearisit.features.tag_management;

import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ApiResponse<TagDto>> createTag(
            @Valid @RequestBody CreateTagRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Tag tag = tagService.createTag(request, user);

        TagDto tagDto = TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .categoryId(tag.getCategory().getId())
                .categoryName(tag.getCategory().getName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tagDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TagDto>> editTag(
            @PathVariable Long id,
            @Valid @RequestBody EditTagRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Tag tag = tagService.editTag(id, request, user);

        TagDto tagDto = TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .categoryId(tag.getCategory().getId())
                .categoryName(tag.getCategory().getName())
                .build();

        return ResponseEntity.ok(ApiResponse.success(tagDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTag(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        tagService.deleteTag(id, user);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagDto>>> getUserTags(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Tag> tags = tagService.getTagsByUser(user.getUser_id());

        List<TagDto> tagDtos = tags.stream()
                .map(tag -> TagDto.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .categoryId(tag.getCategory().getId())
                        .categoryName(tag.getCategory().getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(tagDtos));
    }

    @GetMapping("/categories/{categoryId}/tags")
    public ResponseEntity<ApiResponse<List<TagDto>>> getTagsByCategory(
            @PathVariable Long categoryId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        List<Tag> tags = tagService.getTagsByCategoryAndUser(categoryId, user.getUser_id());

        List<TagDto> tagDtos = tags.stream()
                .map(tag -> TagDto.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .categoryId(categoryId)
                        .categoryName(tag.getCategory().getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(tagDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagDetailDto>> getTag(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Tag tag = tagService.getTagById(id, user.getUser_id());

        TagDetailDto tagDetail = TagDetailDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .categoryId(tag.getCategory().getId())
                .categoryName(tag.getCategory().getName())
                .itemCount(tag.getItems().size())
                .items(tag.getItems().stream()
                        .map(item -> TagItemDto.builder()
                                .id(item.getId())
                                .itemName(item.getItemName())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(ApiResponse.success(tagDetail));
    }
}