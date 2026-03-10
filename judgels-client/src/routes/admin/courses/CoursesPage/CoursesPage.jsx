import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingContentCard } from '../../../../components/LoadingContentCard/LoadingContentCard';
import { coursesQueryOptions } from '../../../../modules/queries/course';
import { CourseChapterEditDialog } from '../CourseChapterEditDialog/CourseChapterEditDialog';
import { CourseCreateDialog } from '../CourseCreateDialog/CourseCreateDialog';
import { CourseEditDialog } from '../CourseEditDialog/CourseEditDialog';
import { CoursesTable } from '../CoursesTable/CoursesTable';

export default function CoursesPage() {
  const [editedCourse, setEditedCourse] = useState(undefined);
  const [editDialogType, setEditDialogType] = useState(undefined);

  const { data: response } = useQuery(coursesQueryOptions());

  const editCourse = course => {
    setEditedCourse(course);
    setEditDialogType(course ? 'edit' : undefined);
  };

  const editCourseChapters = course => {
    setEditedCourse(course);
    setEditDialogType(course ? 'chapters' : undefined);
  };

  const closeDialog = () => {
    setEditedCourse(undefined);
    setEditDialogType(undefined);
  };

  const renderCourses = () => {
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

  return (
    <ContentCard>
      <h3>Courses</h3>
      <hr />
      <CourseCreateDialog />
      <CourseEditDialog isOpen={editDialogType === 'edit'} course={editedCourse} onCloseDialog={closeDialog} />
      <CourseChapterEditDialog
        isOpen={editDialogType === 'chapters'}
        course={editedCourse}
        onCloseDialog={closeDialog}
      />
      {renderCourses()}
    </ContentCard>
  );
}
