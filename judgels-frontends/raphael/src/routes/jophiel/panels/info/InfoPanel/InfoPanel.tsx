import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';

import { Card } from 'components/Card/Card';
import { UserInfo } from 'modules/api/jophiel/userInfo';

import InfoForm from '../InfoForm/InfoForm';
import { InfoTable } from '../InfoTable/InfoTable';

import './InfoPanel.css';

export interface InfoPanelProps {
  info: UserInfo;
  onUpdateInfo: (info: UserInfo) => Promise<void>;
}

interface InfoPanelState {
  isEditing: boolean;
}

export class InfoPanel extends React.PureComponent<InfoPanelProps, InfoPanelState> {
  state: InfoPanelState = { isEditing: false };

  render() {
    const action = this.state.isEditing ? (
      undefined
    ) : (
      <Button data-key="edit" text="Edit" intent={Intent.PRIMARY} className="pt-small" onClick={this.toggleEdit} />
    );

    return (
      <Card title="Info" action={action} className="info-panel-card">
        {this.renderContent()}
      </Card>
    );
  }

  private renderContent = () => {
    const { info } = this.props;
    if (this.state.isEditing) {
      const onCancel = { onCancel: this.toggleEdit };
      return <InfoForm onSubmit={this.onSave} initialValues={info} {...onCancel} />;
    }
    return <InfoTable info={info} />;
  };

  private toggleEdit = () => {
    this.setState((prevState: InfoPanelState) => ({
      isEditing: !prevState.isEditing,
    }));
  };

  private onSave = async (info: UserInfo) => {
    await this.props.onUpdateInfo(info);
    this.setState({ isEditing: false });
  };
}
