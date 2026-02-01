import { Button, Intent } from '@blueprintjs/core';
import { useState } from 'react';

import { Card } from '../../../../../components/Card/Card';
import InfoForm from '../InfoForm/InfoForm';
import { InfoTable } from '../InfoTable/InfoTable';

import './InfoPanel.scss';

export function InfoPanel({ email, info, onUpdateInfo }) {
  const [state, setState] = useState({
    isEditing: false,
  });

  const render = () => {
    const action = state.isEditing ? undefined : (
      <Button small data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={toggleEdit} />
    );

    return (
      <Card title="Info" action={action} className="info-panel-card">
        {renderContent()}
      </Card>
    );
  };

  const renderContent = () => {
    if (state.isEditing) {
      const onCancel = { onCancel: toggleEdit };
      return <InfoForm onSubmit={onSave} initialValues={info} {...onCancel} />;
    }
    return <InfoTable email={email} info={info} />;
  };

  const toggleEdit = () => {
    setState(prevState => ({
      ...prevState,
      isEditing: !prevState.isEditing,
    }));
  };

  const onSave = async info => {
    await onUpdateInfo(info);
    setState(prevState => ({ ...prevState, isEditing: false }));
  };

  return render();
}
