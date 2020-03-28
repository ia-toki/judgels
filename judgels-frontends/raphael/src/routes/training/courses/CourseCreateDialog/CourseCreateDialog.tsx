import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { CourseCreateData } from '../../../../modules/api/jerahmeel/course';
import CourseCreateForm from '../CourseCreateForm/CourseCreateForm';

interface CourseCreateDialogProps {
  onCreateCourse: (data: CourseCreateData) => Promise<void>;
}

interface CourseCreateDialogState {
  isDialogOpen?: boolean;
}

export class CourseCreateDialog extends React.Component<CourseCreateDialogProps, CourseCreateDialogState> {
  state: CourseCreateDialogState = {};

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
      <Button intent={Intent.PRIMARY} icon="plus" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New course
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createCourse,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Create new course"
        canOutsideClickClose={false}
      >
        <CourseCreateForm {...props} />
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

  private createCourse = async (data: CourseCreateData) => {
    await this.props.onCreateCourse(data);
    this.setState({ isDialogOpen: false });
  };
}
