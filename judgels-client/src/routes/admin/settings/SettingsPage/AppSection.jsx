import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { updateSettingsMutationOptions } from '../../../../modules/queries/setting';

import * as toastActions from '../../../../modules/toast/toastActions';

export function AppSection({ app }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '300px' };

  const renderEditButton = () => {
    if (isEditing) {
      return null;
    }
    return (
      <ActionButtons>
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      </ActionButtons>
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        name: app.name,
        slogan: app.slogan,
      };
      return (
        <Form onSubmit={updateSettings} initialValues={initialValues}>
          {({ handleSubmit, submitting }) => (
            <form onSubmit={handleSubmit}>
              <HTMLTable striped>
                <tbody>
                  <Field component={FormTableTextInput} keyStyles={keyStyles} name="name" label="App name" />
                  <Field component={FormTableTextInput} keyStyles={keyStyles} name="slogan" label="App slogan" />
                </tbody>
              </HTMLTable>

              <hr />
              <ActionButtons justifyContent="end">
                <Button text="Cancel" disabled={submitting} onClick={() => setIsEditing(false)} />
                <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
              </ActionButtons>
            </form>
          )}
        </Form>
      );
    }

    const rows = [
      { key: 'name', title: 'App name', value: app.name },
      { key: 'slogan', title: 'App slogan', value: app.slogan },
    ];
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  const updateSettings = values => {
    updateSettingsMutation.mutate(
      { app: { name: values.name, slogan: values.slogan } },
      {
        onSuccess: () => toastActions.showSuccessToast('Settings updated.'),
      }
    );
    setIsEditing(false);
  };

  return (
    <ContentCard title="App settings">
      {renderEditButton()}
      {renderContent()}
    </ContentCard>
  );
}
