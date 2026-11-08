import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit, SmallCross, SmallTick } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { useEditableSection } from '../../../../components/EditableSections/EditableSections';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { FormTableCheckbox } from '../../../../components/forms/FormTableCheckbox/FormTableCheckbox';
import { FormTableRichTextArea } from '../../../../components/forms/FormTableRichTextArea/FormTableRichTextArea';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { FormUnsavedChangesAlert } from '../../../../components/forms/UnsavedChangesAlert/UnsavedChangesAlert';
import { updateSettingsMutationOptions } from '../../../../modules/queries/setting';

import * as toastActions from '../../../../modules/toast/toastActions';

export function UserResetPasswordSection({ userResetPassword, mailerConfigured }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const { isEditing, isEditDisabled, setIsEditing } = useEditableSection();

  const rows = [
    {
      key: 'enabled',
      title: 'Enabled?',
      value: userResetPassword.enabled ? <SmallTick /> : <SmallCross />,
    },
    {
      key: 'requestEmailSubject',
      title: 'Request email subject',
      value: userResetPassword.requestEmailTemplate.subject,
    },
    {
      key: 'requestEmailBody',
      title: 'Request email body',
      value: <HtmlText>{userResetPassword.requestEmailTemplate.body}</HtmlText>,
    },
    {
      key: 'resetEmailSubject',
      title: 'Reset email subject',
      value: userResetPassword.resetEmailTemplate.subject,
    },
    {
      key: 'resetEmailBody',
      title: 'Reset email body',
      value: <HtmlText>{userResetPassword.resetEmailTemplate.body}</HtmlText>,
    },
  ];

  const updateSettings = async values => {
    await updateSettingsMutation.mutateAsync(
      {
        userResetPassword: {
          enabled: !!values.enabled,
          requestEmailTemplate: {
            subject: values.requestEmailSubject || '',
            body: values.requestEmailBody || '',
          },
          resetEmailTemplate: {
            subject: values.resetEmailSubject || '',
            body: values.resetEmailBody || '',
          },
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
        enabled: userResetPassword.enabled,
        requestEmailSubject: userResetPassword.requestEmailTemplate.subject,
        requestEmailBody: userResetPassword.requestEmailTemplate.body,
        resetEmailSubject: userResetPassword.resetEmailTemplate.subject,
        resetEmailBody: userResetPassword.resetEmailTemplate.body,
      };
      return (
        <Form onSubmit={updateSettings} initialValues={initialValues}>
          {({ handleSubmit, submitting }) => (
            <Flex asChild flexDirection="column" gap={2}>
              <form onSubmit={handleSubmit}>
                <FormUnsavedChangesAlert />
                <HTMLTable striped>
                  <tbody>
                    <Field
                      component={FormTableCheckbox}
                      fixedKey
                      name="enabled"
                      label="Enabled"
                      disabled={!mailerConfigured && !userResetPassword.enabled}
                      inputHelper={!mailerConfigured && 'Needs the mailer config in judgels-server.yml.'}
                    />
                    <Field
                      component={FormTableTextInput}
                      fixedKey
                      name="requestEmailSubject"
                      label="Request email subject"
                    />
                    <Field
                      component={FormTableRichTextArea}
                      fixedKey
                      name="requestEmailBody"
                      label="Request email body"
                      rows={10}
                      inputHelper="Must contain {{emailCode}}; may contain {{username}}."
                    />
                    <Field
                      component={FormTableTextInput}
                      fixedKey
                      name="resetEmailSubject"
                      label="Reset email subject"
                    />
                    <Field
                      component={FormTableRichTextArea}
                      fixedKey
                      name="resetEmailBody"
                      label="Reset email body"
                      rows={10}
                      inputHelper="May contain {{username}}."
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

    return <FormTable fixedKey rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="center" gap={2}>
        <h4>
          <span>User password reset</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
