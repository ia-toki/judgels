import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit, SmallCross, SmallTick } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { FormTableCheckbox } from '../../../../components/forms/FormTableCheckbox/FormTableCheckbox';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { updateSettingsMutationOptions } from '../../../../modules/queries/setting';

import * as toastActions from '../../../../modules/toast/toastActions';

export function SessionSection({ session }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '300px' };

  const rows = [
    {
      key: 'disableLogout',
      title: 'Disable logout?',
      value: session.disableLogout ? <SmallTick /> : <SmallCross />,
    },
    {
      key: 'maxConcurrentSessionsPerUser',
      title: 'Max concurrent sessions per user',
      value: session.maxConcurrentSessionsPerUser,
    },
  ];

  const updateSettings = async values => {
    const maxConcurrentSessionsPerUser = parseInt(values.maxConcurrentSessionsPerUser, 10);
    await updateSettingsMutation.mutateAsync(
      {
        session: {
          disableLogout: !!values.disableLogout,
          maxConcurrentSessionsPerUser: Number.isNaN(maxConcurrentSessionsPerUser) ? -1 : maxConcurrentSessionsPerUser,
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
        <Button small variant="outlined" icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        disableLogout: session.disableLogout,
        maxConcurrentSessionsPerUser: session.maxConcurrentSessionsPerUser,
      };
      return (
        <Form onSubmit={updateSettings} initialValues={initialValues}>
          {({ handleSubmit, submitting }) => (
            <Flex asChild flexDirection="column" gap={2}>
              <form onSubmit={handleSubmit}>
                <HTMLTable striped>
                  <tbody>
                    <Field
                      component={FormTableCheckbox}
                      keyStyles={keyStyles}
                      name="disableLogout"
                      label="Disable logout"
                    />
                    <Field
                      component={FormTableTextInput}
                      keyStyles={keyStyles}
                      name="maxConcurrentSessionsPerUser"
                      label="Max concurrent sessions per user"
                      type="number"
                    />
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

    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild gap={2} alignItems="center">
        <h4>
          <span>Session</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
