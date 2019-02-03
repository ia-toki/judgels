import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';
import { FormattedContent } from 'components/FormattedContent/FormattedContent';

import './ContestRegistrationConfirmationDialog.css';
import { APP_CONFIG } from 'conf';

export interface ContestRegistrationConfirmationDialogProps {
  contest: Contest;
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
            <Button text="Accept and Register" intent={Intent.PRIMARY} onClick={this.onRegisterButonClick} />
          </div>
        </div>
      </Dialog>
    );
  }

  private renderTermsAndConditions = () => {
    const { contest } = this.props;

    return <FormattedContent context={{ contestJid: contest.jid }}>{APP_CONFIG.termsAndConditions}</FormattedContent>;
  };

  private onRegisterButonClick = () => {
    this.props.onRegister();
    this.props.onClose();
  };
}
