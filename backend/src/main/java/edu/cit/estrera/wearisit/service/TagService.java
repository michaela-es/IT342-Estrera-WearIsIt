package edu.cit.estrera.wearisit.service;

import edu.cit.estrera.wearisit.api.ApiException;
import edu.cit.estrera.wearisit.api.ErrorCode;
import edu.cit.estrera.wearisit.entity.Category;
import edu.cit.estrera.wearisit.entity.Tag;
import edu.cit.estrera.wearisit.entity.User;
import edu.cit.estrera.wearisit.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    @Transactional
    public Tag getOrCreateTag(String categoryName, String tagName, User user, Long userId) {
        // Get or create category first
        Category category = categoryService.getOrCreateCategory(categoryName, user, userId);

        // Get or create tag under this category
        return tagRepository.findByNameAndCategoryIdAndUserId(tagName, category.getId(), userId)
                .orElseGet(() -> createTag(tagName, category, user));
    }

    private Tag createTag(String name, Category category, User user) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCategory(category);
        tag.setUser(user);
        return tagRepository.save(tag);
    }

    public List<Long> getTagIds(List<Tag> tags) {
        return tags.stream().map(Tag::getId).collect(Collectors.toList());
    }

    public List<String> getTagNames(List<Tag> tags) {
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }

    public List<Tag> getTagsByIdsAndUser(List<Long> tagIds, Long userId) {
        List<Tag> tags = tagRepository.findByIdsAndUserId(tagIds, userId);
        if (tags.size() != tagIds.size()) {
            throw new ApiException(ErrorCode.ITEM_004, "One or more tags not found or don't belong to you");
        }
        return tags;
    }
}