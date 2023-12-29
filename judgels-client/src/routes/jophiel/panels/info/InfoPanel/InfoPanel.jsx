import { Button, Intent } from '@blueprintjs/core';
import { Component } from 'react';

import { Card } from '../../../../../components/Card/Card';
import InfoForm from '../InfoForm/InfoForm';
import { InfoTable } from '../InfoTable/InfoTable';

import './InfoPanel.scss';

export class InfoPanel extends Component {
  state = {
    isEditing: false,
  };

  render() {
    const action = this.state.isEditing ? undefined : (
      <Button small data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={this.toggleEdit} />
    );

    return (
      <Card title="Info" action={action} className="info-panel-card">
        {this.renderContent()}
      </Card>
    );
  }

  renderContent = () => {
    const { email, info } = this.props;
    if (this.state.isEditing) {
      const onCancel = { onCancel: this.toggleEdit };
      return <InfoForm onSubmit={this.onSave} initialValues={info} {...onCancel} />;
    }
    return <InfoTable email={email} info={info} />;
  };

  toggleEdit = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  onSave = async info => {
    await this.props.onUpdateInfo(info);
    this.setState({ isEditing: false });
  };
}
