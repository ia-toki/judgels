import { createContext, useContext, useId, useState } from 'react';

const EditableSectionsContext = createContext(null);

// Wraps the sections of a page, so that only one of them can be edited at a time.
export function EditableSections({ children }) {
  const [editingSectionId, setEditingSectionId] = useState(null);

  return (
    <EditableSectionsContext.Provider value={{ editingSectionId, setEditingSectionId }}>
      {children}
    </EditableSectionsContext.Provider>
  );
}

export function useEditableSection() {
  const sectionId = useId();
  const context = useContext(EditableSectionsContext);
  if (!context) {
    throw new Error('useEditableSection must be used within EditableSections');
  }
  const { editingSectionId, setEditingSectionId } = context;

  return {
    isEditing: editingSectionId === sectionId,
    isEditDisabled: editingSectionId !== null && editingSectionId !== sectionId,
    setIsEditing: isEditing => setEditingSectionId(isEditing ? sectionId : null),
  };
}
