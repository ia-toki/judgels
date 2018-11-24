import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { AdminDeleteResponse } from 'modules/api/uriel/admin';

import AdminRemoveForm, { AdminRemoveFormData } from '../AdminRemoveForm/AdminRemoveForm';
import { AdminRemoveResultTable } from '../AdminRemoveResultTable/AdminRemoveResultTable';

export interface AdminRemoveDialogProps {
  onDeleteAdmins: (usernames: string[]) => Promise<AdminDeleteResponse>;
}

interface AdminRemoveDialogState {
  isDialogOpen?: boolean;
  submitted?: {
    usernames: string[];
    response: AdminDeleteResponse;
  };
}

export class AdminRemoveDialog extends React.Component<AdminRemoveDialogProps, AdminRemoveDialogState> {
  state: AdminRemoveDialogState = {};

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
        className="uriel-admin-dialog-button"
        intent={Intent.DANGER}
        icon="trash"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Remove admins
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  private renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogRemoveResultTable() : this.renderDialogRemoveForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Remove admins results' : 'Remove admins';

    return (
      <Dialog
        className="uriel-admin-dialog"
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title={dialogTitle}
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {dialogBody}
      </Dialog>
    );
  };

  private renderDialogRemoveForm = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addAdmins,
    };
    return <AdminRemoveForm {...props} />;
  };

  private renderDialogRemoveResultTable = () => {
    const { usernames, response } = this.state.submitted!;
    const { deletedAdminProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'uriel-admin-dialog-result-body')}>
          <AdminRemoveResultTable usernames={usernames} deletedAdminProfilesMap={deletedAdminProfilesMap} />
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Done" intent={Intent.PRIMARY} onClick={this.toggleDialog} />
          </div>
        </div>
      </>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'uriel-admin-dialog-body')}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private addAdmins = async (data: AdminRemoveFormData) => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await this.props.onDeleteAdmins(usernames);
    if (usernames.length !== Object.keys(response.deletedAdminProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
