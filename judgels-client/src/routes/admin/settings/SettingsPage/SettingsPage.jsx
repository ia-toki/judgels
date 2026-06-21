import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { settingsQueryOptions, updateAppSettingsMutationOptions } from '../../../../modules/queries/setting';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function SettingsPage() {
  const { data } = useQuery(settingsQueryOptions());

  const updateAppSettingsMutation = useMutation(updateAppSettingsMutationOptions());

  const [isEditing, setIsEditing] = useState(false);

  const renderAction = () => {
    if (data === undefined || isEditing) {
      return null;
    }
    return (
      <ActionButtons>
        <Button intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit settings
        </Button>
      </ActionButtons>
    );
  };

  const renderContent = () => {
    if (data === undefined) {
      return <LoadingState />;
    }

    const app = data.app;

    if (isEditing) {
      const initialValues = {
        name: app.name,
        slogan: app.slogan,
      };
      return (
        <Form onSubmit={updateAppSettings} initialValues={initialValues}>
          {({ handleSubmit, submitting }) => (
            <form onSubmit={handleSubmit}>
              <HTMLTable striped>
                <tbody>
                  <Field component={FormTableTextInput} name="name" label="App name" />
                  <Field component={FormTableTextInput} name="slogan" label="App slogan" />
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
    return <FormTable rows={rows} />;
  };

  const updateAppSettings = values => {
    updateAppSettingsMutation.mutate(
      { name: values.name, slogan: values.slogan },
      {
        onSuccess: () => toastActions.showSuccessToast('Settings updated.'),
      }
    );
    setIsEditing(false);
  };

  return (
    <ContentCard title="Settings">
      {renderAction()}
      {renderContent()}
    </ContentCard>
  );
}
