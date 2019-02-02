import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';
import { FormattedContent } from 'components/FormattedContent/FormattedContent';

import './ContestRegistrationConfirmationDialog.css';

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
            <Button text="Accept and Regsiter" intent={Intent.PRIMARY} onClick={this.onRegisterButonClick} />
          </div>
        </div>
      </Dialog>
    );
  }

  private renderTermsAndConditions = () => {
    const { contest } = this.props;
    const content = `<p>By competing in TLX contests, you agree that:</p>
    <ul>
      <li> You will not collaborate with any other contestants. </li>
      <li> You will not use fake or multiple TLX accounts, other than your own account. </li>
      <li> You will not try to hack or attack the contest system in any way. </li>
    </ul>
    <p> Failure to comply with the above rules can result to a disqualification or ban. </p>
    <p>Enjoy the contest!</p>`;

    return <FormattedContent context={{ contestJid: contest.jid }}>{content}</FormattedContent>;
  };

  private onRegisterButonClick = () => {
    this.props.onRegister();
    this.props.onClose();
  };
}
