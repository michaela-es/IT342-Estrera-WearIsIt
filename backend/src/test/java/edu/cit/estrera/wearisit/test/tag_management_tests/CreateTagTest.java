package edu.cit.estrera.wearisit.test.tag_management_tests;

import edu.cit.estrera.wearisit.features.category_management.Category;
import edu.cit.estrera.wearisit.features.category_management.CategoryService;
import edu.cit.estrera.wearisit.features.tag_management.*;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TagService tagService;

    private User user;
    private User anotherUser;
    private Category category;
    private Category newCategory;
    private CreateTagRequest createTagRequest;
    private EditTagRequest editRequest;
    private Tag tag;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        category = new Category();
        category.setId(10L);
        category.setName("Test Category");

        newCategory = new Category();
        newCategory.setId(200L);
        newCategory.setName("New Category");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("Original Tag");
        tag.setCategory(category);
        tag.setUser(user);

        editRequest = new EditTagRequest();

        createTagRequest = new CreateTagRequest();
        createTagRequest.setName("Casual");
        createTagRequest.setCategoryId(10L);
    }

    @Test
    void createTag_shouldCreateTagSuccessfully() {
        when(categoryService.getCategoriesByIdsAndUser(List.of(10L), 1L))
                .thenReturn(List.of(category));

        when(tagRepository.existsByNameAndCategoryIdAndUserId(
                "Casual", 10L, 1L
        )).thenReturn(false);

        Tag savedTag = new Tag();
        savedTag.setId(100L);
        savedTag.setName("Casual");
        savedTag.setCategory(category);
        savedTag.setUser(user);

        when(tagRepository.save(any(Tag.class)))
                .thenReturn(savedTag);

        Tag result = tagService.createTag(createTagRequest, user);

        assertNotNull(result);
        assertEquals("Casual", result.getName());
        assertEquals(100L, result.getId());

        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void createTag_shouldThrowException_whenTagAlreadyExists() {
        when(categoryService.getCategoriesByIdsAndUser(List.of(10L), 1L))
                .thenReturn(List.of(category));

        when(tagRepository.existsByNameAndCategoryIdAndUserId(
                "Casual", 10L, 1L
        )).thenReturn(true);

        ApiException ex = assertThrows(
                ApiException.class,
                () -> tagService.createTag(createTagRequest, user)
        );

        assertEquals(ErrorCode.DB_002, ex.getErrorCode());

        verify(tagRepository, never()).save(any());
    }

    @Test
    void getTagsByIdsAndUser_shouldReturnTags() {
        Tag tag1 = new Tag();
        tag1.setId(1L);

        Tag tag2 = new Tag();
        tag2.setId(2L);

        List<Long> ids = List.of(1L, 2L);

        when(tagRepository.findByIdsAndUserId(ids, 1L))
                .thenReturn(List.of(tag1, tag2));

        List<Tag> result = tagService.getTagsByIdsAndUser(ids, 1L);

        assertEquals(2, result.size());
    }

    @Test
    void getTagsByIdsAndUser_shouldThrow_whenMissingTags() {
        List<Long> ids = List.of(1L, 2L);

        Tag tag1 = new Tag();
        tag1.setId(1L);

        when(tagRepository.findByIdsAndUserId(ids, 1L))
                .thenReturn(List.of(tag1));

        ApiException ex = assertThrows(
                ApiException.class,
                () -> tagService.getTagsByIdsAndUser(ids, 1L)
        );

        assertEquals(ErrorCode.ITEM_005, ex.getErrorCode());
    }

    @Test
    void editTag_Success_UpdateNameOnly() {
        editRequest.setName("Updated Tag Name");

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Updated Tag Name", 10L, 1L))
                .thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.editTag(1L, editRequest, user);

        assertThat(result.getName()).isEqualTo("Updated Tag Name");
        verify(tagRepository).save(tag);
        verify(categoryService, never()).getCategoriesByIdsAndUser(anyList(), any());
    }

    @Test
    void editTag_Success_UpdateCategoryOnly() {
        editRequest.setCategoryId(200L);

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(categoryService.getCategoriesByIdsAndUser(List.of(200L), 1L))
                .thenReturn(List.of(newCategory));
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Original Tag", 200L, 1L))
                .thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.editTag(1L, editRequest, user);

        assertThat(result.getCategory()).isEqualTo(newCategory);
        verify(tagRepository).save(tag);
    }

    @Test
    void editTag_Success_UpdateBothNameAndCategory() {
        editRequest.setName("Updated Name");
        editRequest.setCategoryId(200L);

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(categoryService.getCategoriesByIdsAndUser(List.of(200L), 1L))
                .thenReturn(List.of(newCategory));
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Updated Name", 10L, 1L))
                .thenReturn(false);
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Updated Name", 200L, 1L))
                .thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.editTag(1L, editRequest, user);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getCategory()).isEqualTo(newCategory);
        verify(tagRepository, times(2))
                .existsByNameAndCategoryIdAndUserId(anyString(), anyLong(), eq(1L));
    }

    @Test
    void editTag_ThrowsException_WhenTagNotFound() {
        editRequest.setName("New Name");

        when(tagRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.editTag(999L, editRequest, user))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Tag not found");

        verify(tagRepository, never()).save(any());
    }

    @Test
    void editTag_ThrowsException_WhenDuplicateNameInSameCategory() {
        editRequest.setName("Duplicate Name");

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Duplicate Name", 10L, 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> tagService.editTag(1L, editRequest, user))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Duplicate entry");

        verify(tagRepository, never()).save(any());
    }

    @Test
    void editTag_ThrowsException_WhenDuplicateNameInNewCategory() {
        editRequest.setName("Duplicate Name");
        editRequest.setCategoryId(200L);

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(categoryService.getCategoriesByIdsAndUser(List.of(200L), 1L))
                .thenReturn(List.of(newCategory));
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Duplicate Name", 10L, 1L))
                .thenReturn(false);
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Duplicate Name", 200L, 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> tagService.editTag(1L, editRequest, user))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Duplicate entry");

        verify(tagRepository, never()).save(any());
    }

    @Test
    void editTag_SkipsValidation_WhenNameIsNull() {
        editRequest.setCategoryId(200L);

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(categoryService.getCategoriesByIdsAndUser(List.of(200L), 1L))
                .thenReturn(List.of(newCategory));
        when(tagRepository.existsByNameAndCategoryIdAndUserId("Original Tag", 200L, 1L))
                .thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.editTag(1L, editRequest, user);

        assertThat(result.getName()).isEqualTo("Original Tag");
        verify(tagRepository, never())
                .existsByNameAndCategoryIdAndUserId(eq("Original Tag"), eq(10L), any());
    }

    @Test
    void editTag_SkipsCategoryValidation_WhenCategoryIdIsNull() {
        editRequest.setName("New Name");

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameAndCategoryIdAndUserId("New Name", 10L, 1L))
                .thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.editTag(1L, editRequest, user);

        assertThat(result.getName()).isEqualTo("New Name");
        verify(categoryService, never()).getCategoriesByIdsAndUser(anyList(), any());
    }

    @Test
    void editTag_SkipsUpdate_WhenSameNameAndCategoryProvided() {
        editRequest.setName("Original Tag");
        editRequest.setCategoryId(10L);

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.editTag(1L, editRequest, user);

        assertThat(result.getName()).isEqualTo("Original Tag");
        assertThat(result.getCategory().getId()).isEqualTo(10L);
        verify(tagRepository, never()).existsByNameAndCategoryIdAndUserId(any(), any(), any());
        verify(categoryService, never()).getCategoriesByIdsAndUser(anyList(), any());
    }

    @Test
    void deleteTag_Success() {
        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        tagService.deleteTag(1L, user);

        verify(tagRepository).delete(tag);
    }

    @Test
    void deleteTag_ThrowsException_WhenTagNotFound() {
        when(tagRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.deleteTag(999L, user))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Tag not found");

        verify(tagRepository, never()).delete(any());
    }

    @Test
    void deleteTag_ThrowsException_WhenTagBelongsToAnotherUser() {
        when(tagRepository.findByIdAndUserId(1L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.deleteTag(1L, anotherUser))
                .isInstanceOf(ApiException.class);

        verify(tagRepository, never()).delete(any());
    }

    @Test
    void editTag_ThrowsException_WhenCategoryDoesNotBelongToUser() {
        editRequest.setCategoryId(200L);

        when(tagRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(tag));
        when(categoryService.getCategoriesByIdsAndUser(List.of(200L), 1L))
                .thenThrow(new ApiException(ErrorCode.ITEM_004, "Category not found"));

        assertThatThrownBy(() -> tagService.editTag(1L, editRequest, user))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Category not found");

        verify(tagRepository, never()).save(any());
    }
}