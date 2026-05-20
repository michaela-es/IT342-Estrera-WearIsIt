import React, { useState } from 'react';
import { Container, Box, Typography, Button, Grid, Paper, Divider, Snackbar, Alert } from '@mui/material';
import { useCategories } from '../hooks/useCategories';
import { useTags } from '../hooks/useTags';
import { CategoryList } from './CategoryList';
import { TagList } from './TagList';
import { AddCategoryDialog } from './Dialogs/AddCategoryDialog';
import { AddTagDialog } from './Dialogs/AddTagDialog';
import { EditCategoryDialog } from './Dialogs/EditCategoryDialog';
import { EditTagDialog } from './Dialogs/EditTagDialog';
import { DeleteConfirmDialog } from './Dialogs/DeleteConfirmDialog';

export const CategoryTagManager = () => {
  const {
    categories,
    selectedCategory,
    selectedCategoryId,
    createCategory,
    updateCategory,
    deleteCategory,
    selectCategory,
    snackbar,
    dispatch,
  } = useCategories();

  const { createTag, updateTag, deleteTag } = useTags();

  const [dialogs, setDialogs] = useState({
    addCategory: false,
    addTag: false,
    editCategory: false,
    editTag: false,
    deleteConfirm: false,
  });

  const [editingCategory, setEditingCategory] = useState(null);
  const [editingTag, setEditingTag] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const handleDeleteCategory = async () => {
    if (deleteTarget) {
      const success = await deleteCategory(deleteTarget.id);
      if (success) {
        setDialogs({ ...dialogs, deleteConfirm: false });
        setDeleteTarget(null);
      }
    }
  };

  const handleDeleteTag = async (tagId, categoryId) => {
    await deleteTag(tagId, categoryId);
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-0.02em' }}>
            🏷️ Categories & Tags
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage your clothing categories and their tags
          </Typography>
        </Box>
        <Button variant="contained" startIcon="➕" onClick={() => setDialogs({ ...dialogs, addCategory: true })}>
          New Category
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* Left Panel - Categories */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 2, borderRadius: 2 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              Categories
            </Typography>
            <CategoryList
              categories={categories}
              selectedId={selectedCategoryId}
              onSelect={selectCategory}
              onEdit={(cat) => {
                setEditingCategory(cat);
                setDialogs({ ...dialogs, editCategory: true });
              }}
              onDelete={(cat) => {
                setDeleteTarget(cat);
                setDialogs({ ...dialogs, deleteConfirm: true });
              }}
            />
          </Paper>
        </Grid>

        {/* Right Panel - Tags for Selected Category */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, borderRadius: 2, minHeight: 400 }}>
            {selectedCategory ? (
              <>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                  <Typography variant="h5" sx={{ fontWeight: 700 }}>
                    {selectedCategory.name}
                  </Typography>
                  <Button
                    variant="outlined"
                    startIcon="🏷️"
                    onClick={() => setDialogs({ ...dialogs, addTag: true })}
                  >
                    Add Tag
                  </Button>
                </Box>

                <Divider sx={{ mb: 3 }} />

                <Typography variant="subtitle2" sx={{ mb: 2, color: 'text.secondary' }}>
                  Tags ({selectedCategory.tags?.length || 0})
                </Typography>

                <TagList
                  tags={selectedCategory.tags}
                  categoryId={selectedCategory.id}
                  onEdit={(tag) => {
                    setEditingTag(tag);
                    setDialogs({ ...dialogs, editTag: true });
                  }}
                  onDelete={handleDeleteTag}
                />
              </>
            ) : (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <Typography variant="h6" color="text.secondary">
                  Select a category to view and manage its tags
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  Or create a new category to get started
                </Typography>
              </Box>
            )}
          </Paper>
        </Grid>
      </Grid>

      {/* Dialogs */}
      <AddCategoryDialog
        open={dialogs.addCategory}
        onClose={() => setDialogs({ ...dialogs, addCategory: false })}
        onSave={createCategory}
      />

      <AddTagDialog
        open={dialogs.addTag}
        onClose={() => setDialogs({ ...dialogs, addTag: false })}
        onSave={(name) => createTag(selectedCategoryId, name)}
        categoryName={selectedCategory?.name}
      />

      <EditCategoryDialog
        open={dialogs.editCategory}
        onClose={() => setDialogs({ ...dialogs, editCategory: false })}
        onSave={updateCategory}
        category={editingCategory}
      />

      <EditTagDialog
        open={dialogs.editTag}
        onClose={() => setDialogs({ ...dialogs, editTag: false })}
        onSave={(id, name) => updateTag(id, name, selectedCategoryId)}
        tag={editingTag}
        categoryName={selectedCategory?.name}
      />

      <DeleteConfirmDialog
        open={dialogs.deleteConfirm}
        onClose={() => {
          setDialogs({ ...dialogs, deleteConfirm: false });
          setDeleteTarget(null);
        }}
        onConfirm={handleDeleteCategory}
        title="Delete Category"
        message={`Are you sure you want to delete "${deleteTarget?.name}"? ${
          deleteTarget?.tags?.length > 0 ? 'This category has tags. Please delete them first.' : ''
        }`}
      />

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => dispatch({ type: 'HIDE_SNACKBAR' })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity={snackbar.severity} onClose={() => dispatch({ type: 'HIDE_SNACKBAR' })}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};