import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../../components/ActionButtons/ActionButtons';
import { useEditableSection } from '../../../../../components/EditableSections/EditableSections';
import { FormTable } from '../../../../../components/forms/FormTable/FormTable';
import { FormTableTextInput } from '../../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { FormUnsavedChangesAlert } from '../../../../../components/forms/UnsavedChangesAlert/UnsavedChangesAlert';
import { updateSettingsMutationOptions } from '../../../../../modules/queries/setting';

import * as toastActions from '../../../../../modules/toast/toastActions';

export function GoogleAnalyticsSection({ trainingGoogleAnalytics }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const { isEditing, isEditDisabled, setIsEditing } = useEditableSection();

  const rows = [{ key: 'trackingId', title: 'Tracking ID', value: trainingGoogleAnalytics.trackingId }];

  const updateSettings = async values => {
    await updateSettingsMutation.mutateAsync(
      {
        trainingGoogleAnalytics: {
          trackingId: values.trackingId || '',
        },
      },
      {
        onSuccess: () => toastActions.showSuccessToast('Settings updated.'),
      }
    );
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small variant="outlined" icon={<Edit />} disabled={isEditDisabled} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        trackingId: trainingGoogleAnalytics.trackingId,
      };
      return (
        <Form onSubmit={updateSettings} initialValues={initialValues}>
          {({ handleSubmit, submitting }) => (
            <Flex asChild flexDirection="column" gap={2}>
              <form onSubmit={handleSubmit}>
                <FormUnsavedChangesAlert />
                <HTMLTable striped>
                  <tbody>
                    <Field component={FormTableTextInput} fixedKey name="trackingId" label="Tracking ID" />
                  </tbody>
                </HTMLTable>
                <ActionButtons justifyContent="end">
                  <Button text="Cancel" disabled={submitting} onClick={() => setIsEditing(false)} />
                  <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
                </ActionButtons>
              </form>
            </Flex>
          )}
        </Form>
      );
    }

    return <FormTable fixedKey rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="center" gap={2}>
        <h4>
          <span>Google Analytics</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
