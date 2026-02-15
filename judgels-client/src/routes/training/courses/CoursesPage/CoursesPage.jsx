import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { callAction } from '../../../../modules/callAction';
import { CourseChapterEditDialog } from '../CourseChapterEditDialog/CourseChapterEditDialog';
import { CourseCreateDialog } from '../CourseCreateDialog/CourseCreateDialog';
import { CourseEditDialog } from '../CourseEditDialog/CourseEditDialog';
import { CoursesTable } from '../CoursesTable/CoursesTable';

import * as courseActions from '../modules/courseActions';

export default function CoursesPage() {
  const [state, setState] = useState({
    response: undefined,
    isEditDialogOpen: false,
    isEditChaptersDialogOpen: false,
    editedCourse: undefined,
  });

  const refreshCourses = async () => {
    const response = await callAction(courseActions.getCourses());
    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    refreshCourses();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3>Courses</h3>
        <hr />
        {renderCreateDialog()}
        {renderEditDialog()}
        {renderEditChaptersDialog()}
        {renderCourses()}
      </ContentCard>
    );
  };

  const renderCreateDialog = () => {
    return <CourseCreateDialog onCreateCourse={createCourse} />;
  };

  const renderEditDialog = () => {
    const { isEditDialogOpen, editedCourse } = state;
    return (
      <CourseEditDialog
        isOpen={isEditDialogOpen}
        course={editedCourse}
        onUpdateCourse={updateCourse}
        onCloseDialog={() => editCourse(undefined)}
      />
    );
  };

  const renderEditChaptersDialog = () => {
    const { isEditChaptersDialogOpen, editedCourse } = state;
    return (
      <CourseChapterEditDialog
        isOpen={isEditChaptersDialogOpen}
        course={editedCourse}
        onGetChapters={chapterJid => callAction(courseActions.getChapters(chapterJid))}
        onSetChapters={(courseJid, data) => callAction(courseActions.setChapters(courseJid, data))}
        onCloseDialog={() => editCourseChapters(undefined)}
      />
    );
  };

  const renderCourses = () => {
    const { response } = state;
    if (!response) {
      return <LoadingContentCard />;
    }

    const { data: courses } = response;
    if (courses.length === 0) {
      return (
        <p>
          <small>No courses.</small>
        </p>
      );
    }

    return <CoursesTable courses={courses} onEditCourse={editCourse} onEditCourseChapters={editCourseChapters} />;
  };

  const createCourse = async data => {
    await callAction(courseActions.createCourse(data));
    await refreshCourses();
  };

  const editCourse = async course => {
    setState(prevState => ({
      ...prevState,
      isEditDialogOpen: !!course,
      editedCourse: course,
    }));
  };

  const updateCourse = async (courseJid, data) => {
    await callAction(courseActions.updateCourse(courseJid, data));
    editCourse(undefined);
    await refreshCourses();
  };

  const editCourseChapters = async course => {
    setState(prevState => ({
      ...prevState,
      isEditChaptersDialogOpen: !!course,
      editedCourse: course,
    }));
  };

  return render();
}
