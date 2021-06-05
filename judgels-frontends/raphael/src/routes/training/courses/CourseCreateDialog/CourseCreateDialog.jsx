import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { Component } from 'react';

import CourseCreateForm from '../CourseCreateForm/CourseCreateForm';

export class CourseCreateDialog extends Component {
  state = {
    isDialogOpen: false,
  };

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New course
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createCourse,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Create new course"
        canOutsideClickClose={false}
      >
        <CourseCreateForm {...props} />
      </Dialog>
    );
  };

  renderDialogForm = (fields, submitButton) => (
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

  createCourse = async data => {
    await this.props.onCreateCourse(data);
    this.setState({ isDialogOpen: false });
  };
}
