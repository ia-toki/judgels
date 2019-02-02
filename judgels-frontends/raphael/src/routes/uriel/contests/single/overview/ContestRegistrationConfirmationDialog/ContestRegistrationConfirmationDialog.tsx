import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import './ContestRegistrationConfirmationDialog.css';

export interface ContestRegistrationConfirmationDialogProps {
  onClose: () => void;
  onRegister: () => void;
}

export default class ContestRegistrationConfirmationDialog extends React.PureComponent<
  ContestRegistrationConfirmationDialogProps
> {
  render() {
    return (
      <Dialog isOpen onClose={this.props.onClose} title={`Terms & Conditions`} canOutsideClickClose={false}>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-registrants-dialog__body')}>
          {this.renderTermsAndConditions()}
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Register" intent={Intent.PRIMARY} onClick={this.onRegisterButonClick} />
          </div>
        </div>
      </Dialog>
    );
  }

  private renderTermsAndConditions = () => {
    return <div>Lorem Ipsum</div>;
  };

  private onRegisterButonClick = () => {
    this.props.onRegister();
    this.props.onClose();
  };
}
