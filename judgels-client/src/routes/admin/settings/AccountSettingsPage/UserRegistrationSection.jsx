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

export function UserRegistrationSection({ userRegistration, mailerConfigured, recaptchaConfigured }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const { isEditing, isEditDisabled, setIsEditing } = useEditableSection();

  const rows = [
    {
      key: 'enabled',
      title: 'Enabled?',
      value: userRegistration.enabled ? <SmallTick /> : <SmallCross />,
    },
    {
      key: 'recaptchaEnabled',
      title: 'reCAPTCHA enabled?',
      value: userRegistration.recaptchaEnabled ? <SmallTick /> : <SmallCross />,
    },
    {
      key: 'activationEmailSubject',
      title: 'Activation email subject',
      value: userRegistration.activationEmailTemplate.subject,
    },
    {
      key: 'activationEmailBody',
      title: 'Activation email body',
      value: <HtmlText>{userRegistration.activationEmailTemplate.body}</HtmlText>,
    },
  ];

  const updateSettings = async values => {
    await updateSettingsMutation.mutateAsync(
      {
        userRegistration: {
          enabled: !!values.enabled,
          recaptchaEnabled: !!values.recaptchaEnabled,
          activationEmailTemplate: {
            subject: values.activationEmailSubject || '',
            body: values.activationEmailBody || '',
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
        enabled: userRegistration.enabled,
        recaptchaEnabled: userRegistration.recaptchaEnabled,
        activationEmailSubject: userRegistration.activationEmailTemplate.subject,
        activationEmailBody: userRegistration.activationEmailTemplate.body,
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
                      disabled={!mailerConfigured && !userRegistration.enabled}
                      inputHelper={!mailerConfigured && 'Needs the mailer config in judgels-server.yml.'}
                    />
                    <Field
                      component={FormTableCheckbox}
                      fixedKey
                      name="recaptchaEnabled"
                      label="reCAPTCHA enabled"
                      disabled={!recaptchaConfigured && !userRegistration.recaptchaEnabled}
                      inputHelper={!recaptchaConfigured && 'Needs the recaptcha config in judgels-server.yml.'}
                    />
                    <Field
                      component={FormTableTextInput}
                      fixedKey
                      name="activationEmailSubject"
                      label="Activation email subject"
                    />
                    <Field
                      component={FormTableRichTextArea}
                      fixedKey
                      name="activationEmailBody"
                      label="Activation email body"
                      rows={10}
                      inputHelper="Must contain {{emailCode}}; may contain {{username}}."
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
          <span>User registration</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
