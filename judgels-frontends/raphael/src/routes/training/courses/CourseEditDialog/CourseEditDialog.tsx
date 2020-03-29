import { Classes, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { CourseUpdateData, Course } from '../../../../modules/api/jerahmeel/course';
import CourseEditForm, { CourseEditFormData } from '../CourseEditForm/CourseEditForm';

interface CourseEditDialogProps {
  isOpen: boolean;
  course?: Course;
  onCloseDialog: () => void;
  onUpdateCourse: (courseJid: string, data: CourseUpdateData) => Promise<void>;
}

export class CourseEditDialog extends React.Component<CourseEditDialogProps> {
  render() {
    const { course, isOpen, onCloseDialog } = this.props;
    const initialValues: CourseEditFormData = course && {
      slug: course.slug,
      name: course.name,
      description: course.description,
    };
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.updateCourse,
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

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.props.onCloseDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private updateCourse = async (data: CourseUpdateData) => {
    await this.props.onUpdateCourse(this.props.course.jid, data);
  };
}
