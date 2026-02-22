import { Button, Intent } from '@blueprintjs/core';
import { useState } from 'react';

import { Card } from '../../../../../components/Card/Card';
import InfoForm from '../InfoForm/InfoForm';
import { InfoTable } from '../InfoTable/InfoTable';

import './InfoPanel.scss';

export function InfoPanel({ email, info, onUpdateInfo }) {
  const [isEditing, setIsEditing] = useState(false);

  const toggleEdit = () => {
    setIsEditing(prev => !prev);
  };

  const onSave = async infoData => {
    await onUpdateInfo(infoData);
    setIsEditing(false);
  };

  const action = isEditing ? undefined : (
    <Button small data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={toggleEdit} />
  );

  const renderContent = () => {
    if (isEditing) {
      return <InfoForm onSubmit={onSave} initialValues={info} onCancel={toggleEdit} />;
    }
    return <InfoTable email={email} info={info} />;
  };

  return (
    <Card title="Info" action={action} className="info-panel-card">
      {renderContent()}
    </Card>
  );
}
