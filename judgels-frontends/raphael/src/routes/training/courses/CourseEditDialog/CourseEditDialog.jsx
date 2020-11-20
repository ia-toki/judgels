import { Classes, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import CourseEditForm from '../CourseEditForm/CourseEditForm';

export function CourseEditDialog({ course, isOpen, onUpdateCourse, onCloseDialog }) {
  const renderDialogForm = (fields, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={onCloseDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const updateCourse = async data => {
    await onUpdateCourse(course.jid, data);
  };

  const initialValues = course && {
    slug: course.slug,
    name: course.name,
    description: course.description,
  };
  const props = {
    renderFormComponents: renderDialogForm,
    onSubmit: updateCourse,
    initialValues,
  };

  return (
    <div className="content-card__section">
      <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit course" canOutsideClickClose={false}>
        <CourseEditForm {...props} />
      </Dialog>
    </div>
  );
}
