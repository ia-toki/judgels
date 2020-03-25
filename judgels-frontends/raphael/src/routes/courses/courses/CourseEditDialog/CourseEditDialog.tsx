import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { CourseUpdateData, Course } from '../../../../modules/api/jerahmeel/course';
import CourseEditForm, { CourseEditFormData } from '../CourseEditForm/CourseEditForm';

import './CourseEditDialog.css';

interface CourseEditDialogProps {
  course: Course;
  onUpdateCourse: (courseJid: string, courseSlug: string, data: CourseUpdateData) => Promise<void>;
}

interface CourseEditDialogState {
  isDialogOpen?: boolean;
}

export class CourseEditDialog extends React.Component<CourseEditDialogProps, CourseEditDialogState> {
  state: CourseEditDialogState = {};

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  private renderButton = () => {
    return (
      <Button
        className="course-edit-dialog-button"
        intent={Intent.PRIMARY}
        icon="edit"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Edit course
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const { course } = this.props;
    const initialValues: CourseEditFormData = {
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
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Edit course"
        canOutsideClickClose={false}
      >
        <CourseEditForm {...props} />
      </Dialog>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private updateCourse = async (data: CourseUpdateData) => {
    await this.props.onUpdateCourse(this.props.course.jid, this.props.course.slug, data);
    this.setState({ isDialogOpen: false });
  };
}
