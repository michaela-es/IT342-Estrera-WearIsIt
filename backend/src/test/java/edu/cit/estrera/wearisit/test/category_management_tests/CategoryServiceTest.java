package edu.cit.estrera.wearisit.test.category_management_tests;

import edu.cit.estrera.wearisit.features.category_management.*;
import edu.cit.estrera.wearisit.features.tag_management.Tag;
import edu.cit.estrera.wearisit.features.tag_management.TagRepository;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private User anotherUser;
    private Category category;
    private EditCategoryRequest editRequest;

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
        category.setUser(user);

        editRequest = new EditCategoryRequest();
    }

    @Test
    void getOrCreateCategory_shouldReturnExistingCategory_whenFound() {
        when(categoryRepository.findByNameAndUser_Id("Test Category", 1L))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getOrCreateCategory("Test Category", user, 1L);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getOrCreateCategory_shouldCreateNewCategory_whenNotFound() {
        when(categoryRepository.findByNameAndUser_Id("New Category", 1L))
                .thenReturn(Optional.empty());

        Category newCategory = new Category();
        newCategory.setId(20L);
        newCategory.setName("New Category");
        newCategory.setUser(user);

        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        Category result = categoryService.getOrCreateCategory("New Category", user, 1L);

        assertNotNull(result);
        assertEquals("New Category", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_shouldCreateAndReturnCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory("Test Category", user);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        assertEquals(user, result.getUser());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void getCategoryName_shouldReturnName_whenCategoryExists() {
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        String result = categoryService.getCategoryName(10L);

        assertEquals("Test Category", result);
    }

    @Test
    void getCategoryName_shouldReturnNull_whenCategoryIdIsNull() {
        String result = categoryService.getCategoryName(null);

        assertNull(result);
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void getCategoryName_shouldReturnNull_whenCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        String result = categoryService.getCategoryName(999L);

        assertNull(result);
    }

    @Test
    void getCategoriesByIdsAndUser_shouldReturnCategories_whenAllFound() {
        List<Long> ids = List.of(10L, 20L);
        Category category2 = new Category();
        category2.setId(20L);
        List<Category> categories = List.of(category, category2);

        when(categoryRepository.findByIdInAndUser_Id(ids, 1L)).thenReturn(categories);

        List<Category> result = categoryService.getCategoriesByIdsAndUser(ids, 1L);

        assertEquals(2, result.size());
    }

    @Test
    void getCategoriesByIdsAndUser_shouldReturnEmptyList_whenIdsNull() {
        List<Category> result = categoryService.getCategoriesByIdsAndUser(null, 1L);

        assertTrue(result.isEmpty());
        verify(categoryRepository, never()).findByIdInAndUser_Id(any(), any());
    }

    @Test
    void getCategoriesByIdsAndUser_shouldReturnEmptyList_whenIdsEmpty() {
        List<Category> result = categoryService.getCategoriesByIdsAndUser(new ArrayList<>(), 1L);

        assertTrue(result.isEmpty());
        verify(categoryRepository, never()).findByIdInAndUser_Id(any(), any());
    }

    @Test
    void getCategoriesByIdsAndUser_shouldThrowException_whenSomeCategoriesMissing() {
        List<Long> ids = List.of(10L, 20L);
        when(categoryRepository.findByIdInAndUser_Id(ids, 1L)).thenReturn(List.of(category));

        assertThatThrownBy(() -> categoryService.getCategoriesByIdsAndUser(ids, 1L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_001, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Category not found");
                });
    }

    @Test
    void getCategoriesWithTagsForCurrentUser_shouldReturnCategoriesWithTags() {
        when(securityUtil.getCurrentUser()).thenReturn(user);

        List<Category> categories = List.of(category);
        when(categoryRepository.findByUser_Id(1L)).thenReturn(categories);

        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("Tag1");
        tag1.setCategory(category);
        tag1.setUser(user);

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Tag2");
        tag2.setCategory(category);
        tag2.setUser(user);

        when(tagRepository.findByCategoryIdAndUser_Id(10L, 1L)).thenReturn(List.of(tag1, tag2));

        List<CategoryWithTagsDTO> result = categoryService.getCategoriesWithTagsForCurrentUser();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
        assertEquals(2, result.get(0).getTags().size());
        assertEquals("Tag1", result.get(0).getTags().get(0).getName());
        assertEquals("Tag2", result.get(0).getTags().get(1).getName());
    }

    @Test
    void getCategoriesWithTagsForCurrentUser_shouldReturnEmptyList_whenNoCategories() {
        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByUser_Id(1L)).thenReturn(new ArrayList<>());

        List<CategoryWithTagsDTO> result = categoryService.getCategoriesWithTagsForCurrentUser();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagRepository, never()).findByCategoryIdAndUser_Id(any(), any());
    }

    @Test
    void editCategory_Success_UpdateNameOnly() {
        editRequest.setName("Updated Category Name");

        when(categoryRepository.findByIdAndUser_Id(10L, 1L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndUser_IdAndIdNot("Updated Category Name", 1L, 10L))
                .thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.editCategory(10L, editRequest, user);

        assertThat(result.getName()).isEqualTo("Updated Category Name");
        verify(categoryRepository).save(category);
    }

    @Test
    void editCategory_ThrowsException_WhenCategoryNotFound() {
        editRequest.setName("New Name");

        when(categoryRepository.findByIdAndUser_Id(999L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.editCategory(999L, editRequest, user))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_001, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Category not found");
                });

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void editCategory_ThrowsException_WhenDuplicateNameExists() {
        editRequest.setName("Duplicate Name");

        when(categoryRepository.findByIdAndUser_Id(10L, 1L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndUser_IdAndIdNot("Duplicate Name", 1L, 10L))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.editCategory(10L, editRequest, user))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_002, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Category already exists");
                });

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void editCategory_SkipsUpdate_WhenNameIsNull() {
        editRequest.setName(null);

        when(categoryRepository.findByIdAndUser_Id(10L, 1L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.editCategory(10L, editRequest, user);

        assertThat(result.getName()).isEqualTo("Test Category");
        verify(categoryRepository, never()).existsByNameAndUser_IdAndIdNot(any(), any(), any());
    }

    @Test
    void editCategory_SkipsUpdate_WhenSameNameProvided() {
        editRequest.setName("Test Category");

        when(categoryRepository.findByIdAndUser_Id(10L, 1L))
                .thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.editCategory(10L, editRequest, user);

        assertThat(result.getName()).isEqualTo("Test Category");
        verify(categoryRepository, never()).existsByNameAndUser_IdAndIdNot(any(), any(), any());
    }

    @Test
    void editCategory_ThrowsException_WhenCategoryBelongsToAnotherUser() {
        editRequest.setName("New Name");

        when(categoryRepository.findByIdAndUser_Id(10L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.editCategory(10L, editRequest, anotherUser))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_001, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Category not found");
                });

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findByIdAndUser_Id(10L, 1L))
                .thenReturn(Optional.of(category));
        when(tagRepository.existsByCategoryId(10L)).thenReturn(false);
        doNothing().when(categoryRepository).delete(category);

        categoryService.deleteCategory(10L, user);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_ThrowsException_WhenCategoryNotFound() {
        when(categoryRepository.findByIdAndUser_Id(999L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(999L, user))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_001, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Category not found");
                });

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_ThrowsException_WhenCategoryHasTags() {
        when(categoryRepository.findByIdAndUser_Id(10L, 1L))
                .thenReturn(Optional.of(category));
        when(tagRepository.existsByCategoryId(10L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(10L, user))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_003, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Cannot delete category with existing tags");
                });

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_ThrowsException_WhenCategoryBelongsToAnotherUser() {
        when(categoryRepository.findByIdAndUser_Id(10L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(10L, anotherUser))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_001, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Category not found");
                });

        verify(categoryRepository, never()).delete(any());
    }
}