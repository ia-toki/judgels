import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { ChapterCreateDialog } from '../ChapterCreateDialog/ChapterCreateDialog';
import { ChapterEditDialog } from '../ChapterEditDialog/ChapterEditDialog';
import { ChapterLessonEditDialog } from '../ChapterLessonEditDialog/ChapterLessonEditDialog';
import { ChapterProblemEditDialog } from '../ChapterProblemEditDialog/ChapterProblemEditDialog';
import { ChaptersTable } from '../ChaptersTable/ChaptersTable';

import * as chapterActions from '../modules/chapterActions';

export default function ChaptersPage() {
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
    isEditDialogOpen: false,
    isEditLessonsDialogOpen: false,
    isEditProblemsDialogOpen: false,
    editedChapter: undefined,
  });

  const refreshChapters = async () => {
    const response = await dispatch(chapterActions.getChapters());
    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    refreshChapters();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3>Chapters</h3>
        <hr />
        {renderCreateDialog()}
        {renderEditDialog()}
        {renderEditLessonsDialog()}
        {renderEditProblemsDialog()}
        {renderChapters()}
      </ContentCard>
    );
  };

  const renderCreateDialog = () => {
    return <ChapterCreateDialog onCreateChapter={createChapter} />;
  };

  const renderEditDialog = () => {
    const { isEditDialogOpen, editedChapter } = state;
    return (
      <ChapterEditDialog
        isOpen={isEditDialogOpen}
        chapter={editedChapter}
        onUpdateChapter={updateChapter}
        onCloseDialog={() => editChapter(undefined)}
      />
    );
  };

  const renderEditLessonsDialog = () => {
    const { isEditLessonsDialogOpen, editedChapter } = state;
    return (
      <ChapterLessonEditDialog
        isOpen={isEditLessonsDialogOpen}
        chapter={editedChapter}
        onGetLessons={chapterJid => dispatch(chapterActions.getLessons(chapterJid))}
        onSetLessons={(chapterJid, data) => dispatch(chapterActions.setLessons(chapterJid, data))}
        onCloseDialog={() => editChapterLessons(undefined)}
      />
    );
  };

  const renderEditProblemsDialog = () => {
    const { isEditProblemsDialogOpen, editedChapter } = state;
    return (
      <ChapterProblemEditDialog
        isOpen={isEditProblemsDialogOpen}
        chapter={editedChapter}
        onGetProblems={chapterJid => dispatch(chapterActions.getProblems(chapterJid))}
        onSetProblems={(chapterJid, data) => dispatch(chapterActions.setProblems(chapterJid, data))}
        onCloseDialog={() => editChapterProblems(undefined)}
      />
    );
  };

  const renderChapters = () => {
    const { response } = state;
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

  const createChapter = async data => {
    await dispatch(chapterActions.createChapter(data));
    await refreshChapters();
  };

  const editChapter = async chapter => {
    setState(prevState => ({
      ...prevState,
      isEditDialogOpen: !!chapter,
      editedChapter: chapter,
    }));
  };

  const updateChapter = async (chapterJid, data) => {
    await dispatch(chapterActions.updateChapter(chapterJid, data));
    editChapter(undefined);
    await refreshChapters();
  };

  const editChapterLessons = async chapter => {
    setState(prevState => ({
      ...prevState,
      isEditLessonsDialogOpen: !!chapter,
      editedChapter: chapter,
    }));
  };

  const editChapterProblems = async chapter => {
    setState(prevState => ({
      ...prevState,
      isEditProblemsDialogOpen: !!chapter,
      editedChapter: chapter,
    }));
  };

  return render();
}
