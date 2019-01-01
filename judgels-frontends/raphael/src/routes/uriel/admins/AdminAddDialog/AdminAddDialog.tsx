import { Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { AdminsUpsertResponse } from 'modules/api/uriel/admin';

import AdminAddForm, { AdminAddFormData } from '../AdminAddForm/AdminAddForm';
import { AdminAddResultTable } from '../AdminAddResultTable/AdminAddResultTable';

export interface AdminAddDialogProps {
  onUpsertAdmins: (usernames: string[]) => Promise<AdminsUpsertResponse>;
}

interface AdminAddDialogState {
  isDialogOpen?: boolean;
  submitted?: {
    usernames: string[];
    response: AdminsUpsertResponse;
  };
}

export class AdminAddDialog extends React.Component<AdminAddDialogProps, AdminAddDialogState> {
  state: AdminAddDialogState = {};

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
        intent={Intent.PRIMARY}
        icon="plus"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Add admins
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen, submitted: undefined }));
  };

  private renderDialog = () => {
    const dialogBody =
      this.state.submitted !== undefined ? this.renderDialogAddResultTable() : this.renderDialogAddForm();
    const dialogTitle = this.state.submitted !== undefined ? 'Add admins results' : 'Add admins';

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

  private renderDialogAddForm = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.addAdmins,
    };
    return <AdminAddForm {...props} />;
  };

  private renderDialogAddResultTable = () => {
    const { usernames, response } = this.state.submitted!;
    const { insertedAdminProfilesMap, alreadyAdminProfilesMap } = response;
    return (
      <>
        <div className={classNames(Classes.DIALOG_BODY, 'uriel-admin-dialog-result-body')}>
          <AdminAddResultTable
            usernames={usernames}
            insertedAdminProfilesMap={insertedAdminProfilesMap}
            alreadyAdminProfilesMap={alreadyAdminProfilesMap}
          />
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

  private addAdmins = async (data: AdminAddFormData) => {
    const usernames = data.usernames
      .split('\n')
      .filter(s => s.length > 0)
      .map(s => s.trim());
    const response = await this.props.onUpsertAdmins(usernames);
    if (usernames.length !== Object.keys(response.insertedAdminProfilesMap).length) {
      this.setState({ submitted: { usernames, response } });
    } else {
      this.setState({ isDialogOpen: false });
    }
  };
}
