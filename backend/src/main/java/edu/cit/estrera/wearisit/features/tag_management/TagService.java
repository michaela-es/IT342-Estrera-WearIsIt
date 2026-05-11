package edu.cit.estrera.wearisit.features.tag_management;

import edu.cit.estrera.wearisit.features.category_management.Category;
import edu.cit.estrera.wearisit.features.category_management.CategoryService;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    @Transactional
    public Tag createTag(CreateTagRequest request, User user) {

        List<Category> categories = categoryService.getCategoriesByIdsAndUser(
                List.of(request.getCategoryId()),
                user.getUser_id()
        );

        Category category = categories.get(0);

        boolean exists = tagRepository.existsByNameAndCategoryIdAndUserId(
                request.getName(),
                category.getId(),
                user.getUser_id()
        );

        if (exists) {
            throw new ApiException(ErrorCode.DB_002, "Tag already exists in this category");
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setCategory(category);
        tag.setUser(user);

        return tagRepository.save(tag);
    }

    public List<Tag> getTagsByIdsAndUser(List<Long> tagIds, Long userId) {
        List<Tag> tags = tagRepository.findByIdsAndUserId(tagIds, userId);

        if (tags.size() != tagIds.size()) {
            throw new ApiException(ErrorCode.ITEM_005, "One or more tags not found or don't belong to you");
        }

        return tags;
    }

    @Transactional
    public Tag editTag(Long tagId, EditTagRequest request, User user) {
        Tag tag = tagRepository.findByIdAndUserId(tagId, user.getUser_id())
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_005, "Tag not found or doesn't belong to you"));

        if (request.getName() != null && !request.getName().equals(tag.getName())) {
            boolean exists = tagRepository.existsByNameAndCategoryIdAndUserId(
                    request.getName(),
                    tag.getCategory().getId(),
                    user.getUser_id()
            );

            if (exists) {
                throw new ApiException(ErrorCode.DB_002, "A tag with this name already exists in this category");
            }

            tag.setName(request.getName());
        }

        if (request.getCategoryId() != null && !request.getCategoryId().equals(tag.getCategory().getId())) {
            List<Category> categories = categoryService.getCategoriesByIdsAndUser(
                    List.of(request.getCategoryId()),
                    user.getUser_id()
            );

            Category newCategory = categories.get(0);

            boolean exists = tagRepository.existsByNameAndCategoryIdAndUserId(
                    tag.getName(),
                    newCategory.getId(),
                    user.getUser_id()
            );

            if (exists) {
                throw new ApiException(ErrorCode.DB_002, "A tag with this name already exists in the target category");
            }

            tag.setCategory(newCategory);
        }

        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long tagId, User user) {
        Tag tag = tagRepository.findByIdAndUserId(tagId, user.getUser_id())
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_005, "Tag not found or doesn't belong to you"));

        tagRepository.delete(tag);
    }
}