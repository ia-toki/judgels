import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { chaptersQueryOptions } from '../../../../modules/queries/chapter';
import { ChapterCreateDialog } from '../ChapterCreateDialog/ChapterCreateDialog';
import { ChapterEditDialog } from '../ChapterEditDialog/ChapterEditDialog';
import { ChapterLessonEditDialog } from '../ChapterLessonEditDialog/ChapterLessonEditDialog';
import { ChapterProblemEditDialog } from '../ChapterProblemEditDialog/ChapterProblemEditDialog';
import { ChaptersTable } from '../ChaptersTable/ChaptersTable';

export default function ChaptersPage() {
  const [editedChapter, setEditedChapter] = useState(undefined);
  const [editDialogType, setEditDialogType] = useState(undefined);

  const { data: response } = useQuery(chaptersQueryOptions());

  const editChapter = chapter => {
    setEditedChapter(chapter);
    setEditDialogType(chapter ? 'edit' : undefined);
  };

  const editChapterLessons = chapter => {
    setEditedChapter(chapter);
    setEditDialogType(chapter ? 'lessons' : undefined);
  };

  const editChapterProblems = chapter => {
    setEditedChapter(chapter);
    setEditDialogType(chapter ? 'problems' : undefined);
  };

  const closeDialog = () => {
    setEditedChapter(undefined);
    setEditDialogType(undefined);
  };

  const renderChapters = () => {
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: chapters } = response;
    if (chapters.length === 0) {
      return (
        <p>
          <small>No chapters.</small>
        </p>
      );
    }

    return (
      <ChaptersTable
        chapters={chapters}
        onEditChapter={editChapter}
        onEditChapterLessons={editChapterLessons}
        onEditChapterProblems={editChapterProblems}
      />
    );
  };

  return (
    <ContentCard>
      <h3>Chapters</h3>
      <hr />
      <ChapterCreateDialog />
      <ChapterEditDialog isOpen={editDialogType === 'edit'} chapter={editedChapter} onCloseDialog={closeDialog} />
      <ChapterLessonEditDialog
        isOpen={editDialogType === 'lessons'}
        chapter={editedChapter}
        onCloseDialog={closeDialog}
      />
      <ChapterProblemEditDialog
        isOpen={editDialogType === 'problems'}
        chapter={editedChapter}
        onCloseDialog={closeDialog}
      />
      {renderChapters()}
    </ContentCard>
  );
}
